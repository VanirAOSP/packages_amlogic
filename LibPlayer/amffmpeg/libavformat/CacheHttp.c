/*
 * CacheHttp for threading download
 *
 * This file is part of FFmpeg.
 *
 * FFmpeg is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * FFmpeg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with FFmpeg; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */

#include "libavutil/avstring.h"
#include "avformat.h"
#include <unistd.h>
#include <time.h>
#include <strings.h>
#include "internal.h"
#include "network.h"
#include "os_support.h"
#include "libavutil/opt.h"
#include <pthread.h>
#include "libavutil/fifo.h"
#include "CacheHttp.h"
#include "hls_livesession.h"

#include "bandwidth_measure.h" 

#define BUFFER_SIZE (1024*4)
#define CIRCULAR_BUFFER_SIZE (20*188*4096)
#define WAIT_TIME (100*1000)
#define TMP_BUFFER_SIZE (188*100)   
#define LIVE_HTTP_RETRY_TIMES 20

typedef struct {
    URLContext * hd;
    unsigned char headers[BUFFER_SIZE];    
    int EXIT;
    int EXITED;
    int RESET;
    int reset_flag;
    int finish_flag;
    int seek_flag;
    int have_list_end;
    int circular_buffer_error; 
    double item_duration;
    double item_starttime;
    int64_t item_pos;
    int64_t item_size;
    int64_t seek_pos;
    enum KeyType ktype;
    char key[33];
    char iv[33];
    AVFifoBuffer * fifo;    
    void * bandwidth_measure;
    pthread_t circular_buffer_thread;
    pthread_mutex_t  read_mutex;    
    int is_first_read;

} CacheHttpContext;

static int CacheHttp_ffurl_open_h(URLContext ** h, const char * filename, int flags, const char * headers, int * http)
{
    return ffurl_open_h(h, filename, flags,headers, http);
}

static int CacheHttp_advanced_ffurl_open_h(URLContext ** h,const char * filename, int flags, const char * headers, int * http,CacheHttpContext* ctx){
    if(ctx->ktype == KEY_NONE){
        return CacheHttp_ffurl_open_h(h, filename, flags,headers, http);
    }else{//crypto streaming
        int ret = -1;
        URLContext* input = NULL;
        if ((ret = ffurl_alloc(&input, filename, AVIO_FLAG_READ|AVIO_FLAG_NONBLOCK)) < 0){
            return ret;    
        }
        
        av_set_string3(input->priv_data, "key", ctx->key, 0, NULL);
        av_set_string3(input->priv_data, "iv", ctx->iv, 0, NULL);
        if ((ret = ffurl_connect(input)) < 0) {
            ffurl_close(input);
            input = NULL;
            *h = NULL;
            return ret;
        }
        *h = input;
    }
    return 0;
}
static int CacheHttp_ffurl_read(URLContext * h, unsigned char * buf, int size)
{
    return ffurl_read(h, buf, size);
}

static int CacheHttp_ffurl_close(URLContext * h)
{
    return ffurl_close(h);
}

static int64_t CacheHttp_ffurl_seek(URLContext *h, int64_t pos, int whence)
{
    return ffurl_seek(h, pos, whence);
}

static int CacheHttp_dump_open(void ** handle, const char * url, int flag)
{
    if(!url)
        return -1;
    
    av_log(NULL,AV_LOG_INFO,"---------------> cachehttp_dump url=%s", url);
    FILE * fp = NULL;
    if(flag == 1 && !(*handle)) {
        fp = fopen("/temp/cachehttp_dump.dat", "ab+");
        *handle = (void*)fp;
    } else if(flag == 2) {
        char *ptr = strrchr(url, '/');
        char * name = av_malloc(strlen(ptr) + 6);
        strcpy(name, "/temp");
        strcpy(name+5, ptr);
        *(name+strlen(ptr) + 5) = '\0';
        fp = fopen(name, "w+");
        *handle = (void *)fp;
        av_free(name);
    }
    return 0;
}

static int CacheHttp_dump_write(void * handle, const char *buf, int size)
{   
    if(!handle || !buf)
        return -1;

    int ret = fwrite(buf, 1, size, (FILE*)handle);
    fflush((FILE*)handle);
    return ret;
}

static int CacheHttp_dump_close(void * handle)
{
    if(!handle)
        return -1;

    int ret = fclose((FILE*)handle);
    handle = NULL;
    return ret;
}

static void *circular_buffer_task( void *_handle);

int CacheHttp_Open(void ** handle,const char* headers)
{
    CacheHttpContext * s = (CacheHttpContext *)av_malloc(sizeof(CacheHttpContext));
    if(!s) {
        *handle = NULL;
        return AVERROR(EIO);
    }

    *handle = (void *)s;
    s->hd  = NULL;
    s->item_duration = 0;
    s->item_pos = 0;
    s->item_starttime = 0;
    s->finish_flag = 0;
    s->seek_flag = 0;
    s->reset_flag = -1;
    s->have_list_end = -1;
    s->is_first_read = 1;
    memset(s->headers, 0x00, sizeof(s->headers));
    s->fifo = NULL;
    float value=0.0;
    int config_ret;
    config_ret=am_getconfig_float("libplayer.ffmpeg.hlshttpbufmax",&value);
    if(config_ret<0 || value < 1024*32)
    	s->fifo = av_fifo_alloc(CIRCULAR_BUFFER_SIZE);
    else{
    	s->fifo = av_fifo_alloc(value);
    }
    pthread_mutex_init(&s->read_mutex,NULL);
    
    s->EXIT = 0;
    s->EXITED = 0;
    s->RESET = 0;
    if(headers){
        av_strlcpy(s->headers,headers,BUFFER_SIZE);
    }
    int ret = am_getconfig_float("libplayer.hls.measured_buffer",&value);
    int  meause_unit_max = 1024*1024*10;//10M
    if(ret>=0){
        meause_unit_max = (int)value*1024;
    }
    
    if(meause_unit_max/TMP_BUFFER_SIZE<100){
        s->bandwidth_measure=bandwidth_measure_alloc(100,0);        

    }else{
        s->bandwidth_measure=bandwidth_measure_alloc(meause_unit_max/TMP_BUFFER_SIZE,0); 
    }
    ret = ffmpeg_pthread_create(&s->circular_buffer_thread, NULL, circular_buffer_task, s);
    av_log(NULL, AV_LOG_INFO, "----------- pthread_create ret=%d\n",ret);

    return ret;
}

int CacheHttp_Read(void * handle, uint8_t * cache, int size)
{
    if(!handle)
        return AVERROR(EIO);
    
    CacheHttpContext * s = (CacheHttpContext *)handle;
    pthread_mutex_lock(&s->read_mutex);
    
    if (s->fifo) {
    	int avail;
       avail = av_fifo_size(s->fifo);
    	//av_log(NULL, AV_LOG_INFO, "----------- http_read   avail=%d, size=%d ",avail,size);
        if(s->is_first_read>0){
            float value = 0.0;
            int ret = -1;        
            ret = am_getconfig_float("libplayer.hls.initial_buffered", &value);
            if(ret>=0){
                if(avail/1024<value){
                    //av_log(NULL, AV_LOG_INFO, "buffer data avail=%d, initial buffer buffered data size=%f  ",avail,value*1024);                   
                    pthread_mutex_unlock(&s->read_mutex);
                    return AVERROR(EAGAIN);
                }
            }
            s->is_first_read = 0;
        }
	if(url_interrupt_cb()) {
	    pthread_mutex_unlock(&s->read_mutex);
	    return 0;
	} else if(avail) {
           // Maximum amount available
           size = FFMIN( avail, size);
           av_fifo_generic_read(s->fifo, cache, size, NULL);
    	    pthread_mutex_unlock(&s->read_mutex);
           return size;        
       } else if(s->EXITED) {
           pthread_mutex_unlock(&s->read_mutex); 
           return 0;
       } else if(!s->finish_flag) {
           pthread_mutex_unlock(&s->read_mutex);          
           //read just need retry
           return AVERROR(EAGAIN);
       }
    }
    pthread_mutex_unlock(&s->read_mutex);
    
    return 0;
}

int CacheHttp_Reset(void * handle)
{
    av_log(NULL, AV_LOG_INFO, "--------------- CacheHttp_Reset begin");
    if(!handle)
        return AVERROR(EIO);

    CacheHttpContext * s = (CacheHttpContext *)handle;
    s->RESET = 1;
    while(!s->EXITED && 0 == s->reset_flag) {
        usleep(1000);
    }
    av_log(NULL, AV_LOG_INFO, "--------------- CacheHttp_Reset suc !");
    pthread_mutex_lock(&s->read_mutex);
    if(s->fifo)
        av_fifo_reset(s->fifo);
    pthread_mutex_unlock(&s->read_mutex);
    s->RESET = 0;
    s->finish_flag = 0;
    return 0;
}

int CacheHttp_Seek(void * handle, int64_t pos)
{
    av_log(NULL, AV_LOG_INFO, "--------------- CacheHttp_Seek begin");
    if(!handle)
        return AVERROR(EIO);

    CacheHttpContext * s = (CacheHttpContext *)handle;
    s->RESET = 1;
    while(!s->EXITED && 0 == s->reset_flag) {
        usleep(1000);
    }
    pthread_mutex_lock(&s->read_mutex);
    if(s->fifo)
        av_fifo_reset(s->fifo);
    pthread_mutex_unlock(&s->read_mutex);
    s->RESET = 0;
    s->finish_flag = 0;
    s->seek_flag = 1;
    s->seek_pos = pos;
    av_log(NULL, AV_LOG_INFO, "--------------- CacheHttp_Seek suc !");
    return 0;
}

int CacheHttp_Close(void * handle)
{
    if(!handle)
        return AVERROR(EIO);
    
    CacheHttpContext * s = (CacheHttpContext *)handle;
    s->EXIT = 1;
   
    ffmpeg_pthread_join(s->circular_buffer_thread, NULL);
   
    av_log(NULL,AV_LOG_DEBUG,"-----------%s:%d\n",__FUNCTION__,__LINE__);
    if(s->fifo) {
    	av_fifo_free(s->fifo);
    }
    pthread_mutex_destroy(&s->read_mutex);    
    bandwidth_measure_free(s->bandwidth_measure);
    return 0;
}

int CacheHttp_GetSpeed(void * _handle, int * arg1, int * arg2, int * arg3)
{ 
    if(!_handle)
        return AVERROR(EIO);
  
    CacheHttpContext * s = (CacheHttpContext *)_handle; 
    int ret = bandwidth_measure_get_bandwidth(s->bandwidth_measure,arg1,arg2,arg3);	
    //av_log(NULL, AV_LOG_ERROR, "download bandwidth latest=%d.%d kbps,latest avg=%d.%d k bps,avg=%d.%d kbps\n",*arg1/1000,*arg1%1000,*arg2/1000,*arg2%1000,*arg3/1000,*arg3%1000);
    return ret;
}

int CacheHttp_GetBuffedTime(void * handle)
{
    if(!handle)
        return AVERROR(EIO);

    CacheHttpContext * s = (CacheHttpContext *)handle; 
    int64_t buffed_time=0;  

    //av_log(NULL, AV_LOG_ERROR, "---------- 000 CacheHttp_GetBufferedTime  s->item_size=%lld", s->item_size);
    if(s->item_duration>= 0 && s->item_size > 0) {
        buffed_time = s->item_starttime+s->item_pos*s->item_duration/s->item_size;
      // av_log(NULL, AV_LOG_ERROR, "----------CacheHttp_GetBufferedTime  buffed_time=%lld", buffed_time);
    } else {
        buffed_time = s->item_starttime;
        if(s->finish_flag>0) {
            int64_t full_time = getTotalDuration(NULL);
            buffed_time = full_time;
        }
    }   

    return buffed_time;
}

static void *circular_buffer_task( void *_handle)
{
    CacheHttpContext * s = (CacheHttpContext *)_handle; 
    URLContext *h = NULL;
    float config_value = 0.0;
    void * fp = NULL;
    int config_ret = 0;
    
    while(!s->EXIT) {

       av_log(h, AV_LOG_ERROR, "----------circular_buffer_task  item ");
       s->reset_flag = 1;
	if (url_interrupt_cb()) {
		 s->circular_buffer_error = EINTR;
               goto FAIL;
	}

        if(h) {
            CacheHttp_ffurl_close(h);
            config_ret = am_getconfig_float("libplayer.hls.dump",&config_value);
            if(config_ret >= 0 && (int)config_value == 2)
                CacheHttp_dump_close(fp);
            h = NULL;
        }        
       
        list_item_t * item = getCurrentSegment(NULL);
        if(!item||(!item->file&&!item->flags&ENDLIST_FLAG)) {
            usleep(WAIT_TIME);
            continue;
        }
        
        s->reset_flag = 0;
        s->item_starttime = item->start_time;
        s->item_duration = item->duration;
        s->have_list_end = item->have_list_end;
        s->ktype = item->ktype;
        if(item->key_ctx!=NULL&& s->ktype==KEY_AES_128){
            ff_data_to_hex(s->iv, item->key_ctx->iv, sizeof(item->key_ctx->iv), 0);
            ff_data_to_hex(s->key, item->key_ctx->key, sizeof(item->key_ctx->key), 0);
            s->iv[32] = s->key[32] = '\0';
        }
        if(item&&item->flags&ENDLIST_FLAG){
            s->finish_flag =1;
        }else{
            s->finish_flag =0;
        }        
       
        if(s->finish_flag){      
            av_log(NULL, AV_LOG_INFO, "ENDLIST_FLAG, return 0\n");
            //break;
            usleep(500*1000);
            continue;
        }
        
        int err, http_code;
        char* filename = NULL;
        if(s->ktype == KEY_NONE){
            filename = av_strdup(item->file);

        }else{
            char url[MAX_URL_SIZE];
            if (strstr(item->file, "://"))
                snprintf(url, sizeof(url), "crypto+%s", item->file);
            else
                snprintf(url, sizeof(url), "crypto:%s", item->file);

            filename = av_strdup(url);
            
        }
        int retry_num = 0;
OPEN_RETRY:
        if(s->RESET)
            goto SKIP;
        err = CacheHttp_advanced_ffurl_open_h(&h, filename,AVIO_FLAG_READ|AVIO_FLAG_NONBLOCK, s->headers, &http_code,s);
        if (err) {
            if(url_interrupt_cb()) {
                if(filename) {
                    av_free(filename);
                    filename = NULL;
                }
                break;
             }
             if(1 == http_code && !s->have_list_end) {
                av_log(h, AV_LOG_ERROR, "----------CacheHttpContext : ffurl_open_h 404\n");
                if(retry_num++ < LIVE_HTTP_RETRY_TIMES) {
                    usleep(WAIT_TIME);
                    goto OPEN_RETRY;
                } else {
                    goto SKIP;
                }
             } else if(s->have_list_end || ((2 == http_code || 3 == http_code)&& !s->have_list_end)) {
                usleep(1000*20);
                goto OPEN_RETRY;
             } else if(!s->have_list_end&&err ==AVERROR(EIO)){
                if(retry_num++ < LIVE_HTTP_RETRY_TIMES) {//if live streaming,just keep on 2s.
                    usleep(WAIT_TIME);
                    goto OPEN_RETRY;
                } else {
                	  av_log(h, AV_LOG_ERROR, "----------CacheHttpContext : ffurl_open_h failed ,%d\n",err);
	                if(filename) {
	                    av_free(filename);
	                    filename = NULL;
	                }
	                break;
                }             
             	   	
             }else{
                av_log(h, AV_LOG_ERROR, "----------CacheHttpContext : ffurl_open_h failed ,%d\n",err);
                if(filename) {
                    av_free(filename);
                    filename = NULL;
                }
                 break;
             }          
        }

        if(h && s->seek_flag) {
            int64_t cur_pos = CacheHttp_ffurl_seek(h, 0, SEEK_CUR);
            int64_t pos_ret = CacheHttp_ffurl_seek(h, s->seek_pos-cur_pos, SEEK_CUR);
            av_log(NULL,AV_LOG_INFO,"--------------> cachehttp_seek   seek_pos=%lld, pos_ret=%lld", s->seek_pos, pos_ret);
            s->seek_flag = 0;
        }
        
        s->hd = h;
        s->item_pos = 0;
        s->item_size = CacheHttp_ffurl_seek(s->hd, 0, AVSEEK_SIZE);
        item->item_size = s->item_size;
        char tmpbuf[TMP_BUFFER_SIZE];
        int left = 0;
        int tmpdatasize = 0;
        config_ret = am_getconfig_float("libplayer.hls.dump",&config_value);
        if(config_ret >= 0 && config_value > 0)
                CacheHttp_dump_open(&fp, filename, (int)config_value);
        
        while(!s->EXIT) {

           if(s->RESET)
                break;
            
	    if (url_interrupt_cb()) {
		 s->circular_buffer_error = EINTR;
               break;
	    }

           if(!s->hd)
                break;

           if(s->hd && tmpdatasize <= 0) {
                bandwidth_measure_start_read(s->bandwidth_measure);                 
                tmpdatasize = CacheHttp_ffurl_read(s->hd, tmpbuf, TMP_BUFFER_SIZE);
                bandwidth_measure_finish_read(s->bandwidth_measure,tmpdatasize);
           }

            //if(tmpdatasize > 0) {
        	    pthread_mutex_lock(&s->read_mutex);
        	    left = av_fifo_space(s->fifo);
                  left = FFMIN(left, s->fifo->end - s->fifo->wptr);
                  

                   if( !left) {
        		pthread_mutex_unlock(&s->read_mutex);
        		usleep(WAIT_TIME);
                    	continue;
                   }
                     left = FFMIN(left, tmpdatasize);
                   if(left >0) {
                        memcpy(s->fifo->wptr, tmpbuf , left);
                        tmpdatasize-=left;
                   }
                   if(tmpdatasize>0){
                      memmove(tmpbuf, tmpbuf+left , tmpdatasize);
                    }
                   
                   if (left > 0) {
                       config_ret = am_getconfig_float("libplayer.hls.dump",&config_value);
                       if(config_ret >= 0 && config_value > 0)
                            CacheHttp_dump_write(fp, s->fifo->wptr, left);
                	  s->fifo->wptr += left;
                        if (s->fifo->wptr >= s->fifo->end)
                            s->fifo->wptr = s->fifo->buffer;
                        s->fifo->wndx += left;
                        s->item_pos += left;
                   } else if(left == AVERROR(EAGAIN) || (left < 0 && s->have_list_end&& left != AVERROR_EOF)) {
                        pthread_mutex_unlock(&s->read_mutex);
                        continue;
                   } else {
                        pthread_mutex_unlock(&s->read_mutex);
                        av_log(h, AV_LOG_ERROR, "---------- circular_buffer_task read left = %d\n", left);
                        break;
                   }
                   pthread_mutex_unlock(&s->read_mutex);
             //}

	    //usleep(WAIT_TIME);

        } 

SKIP:
        if(filename){
            av_free(filename);
            filename = NULL;
        }
	 if(!s->RESET)
        	switchNextSegment(NULL);
    }
    
FAIL:

    if(h)
        CacheHttp_ffurl_close(h);
    s->hd = NULL;
    s->EXITED = 1;
    config_ret = am_getconfig_float("libplayer.hls.dump",&config_value);
    if(config_ret >= 0 && config_value > 0)
        CacheHttp_dump_close(fp);
    av_log(NULL, AV_LOG_ERROR, "---------> CacheHttp thread quit !");
    return NULL;
}


int CacheHttp_GetBufferPercentage(void *_handle,int* per){
    int ret = 0;
    int total = 0;
    if(!_handle){
        return AVERROR(EIO);
    }
    CacheHttpContext * s = (CacheHttpContext *)_handle; 
    pthread_mutex_lock(&s->read_mutex);
   
    total = av_fifo_size(s->fifo);
    *per  = (int)total*100/CIRCULAR_BUFFER_SIZE;
    *per = ret;
    pthread_mutex_unlock(&s->read_mutex);
    return 0;
}

