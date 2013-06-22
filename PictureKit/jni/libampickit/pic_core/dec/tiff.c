#include <stdio.h>
#include <tiffio.h>
#include <cutils/log.h>
#include "pic_app.h"
#include "tiffio.h"
#include <android/log.h>
#define  LOGI(...) __android_log_print(ANDROID_LOG_INFO, "MCA", __VA_ARGS__)


int fh_tiff_id(aml_dec_para_t* para)
{
	FILE* fd;
	char id[2];
	fd = fopen(para->fn, "r");
	if (fd == -1) {
		return(0);
	}
	fread(id, 1, 2, fd);
	fclose(fd);
	if ((id[0]=='I' && id[1]=='I') || (id[0]=='M' && id[1]=='M')) {
		return(1);
	}
	return(0);
}

int fh_tiff_getsize(char *name,int *x,int *y)
{
	TIFF* tiff = TIFFOpen(name, "r");
	if (tiff == NULL) {
		return(0);
	}
	TIFFSetDirectory(tiff,0);
	TIFFGetField(tiff, TIFFTAG_IMAGEWIDTH, x);
	TIFFGetField(tiff, TIFFTAG_IMAGELENGTH, y);
	TIFFClose(tiff);
	return(FH_ERROR_OK);
}

int fh_tiff_load(aml_dec_para_t* para , aml_image_info_t* image)
{
	uint16 bitspersample = 1;
	uint16 samplesperpixel = 1;
	uint16 bitsperpixel = 0;
	int dwBytePerLine = 0;
	int dwLeng = 0;
	char *pData;
	uint32 *raster;        
	uint32 row;
	char *bits2;
	char *name;
	int x=0, y=0;
	int width = para->iwidth;
   	int height = para->iheight;
	name = para->fn;
        TIFF* tiff = TIFFOpen(name, "r");
        if (tiff == NULL) {
                return(0);
        }
	TIFFSetDirectory(tiff,0);
//	TIFFGetField(tiff, TIFFTAG_SAMPLESPERPIXEL, &samplesperpixel);
//	TIFFGetField(tiff, TIFFTAG_BITSPERSAMPLE, &bitspersample);
//	LOGI("samplesperpixel= %d, bitspersample= %d\n", samplesperpixel, bitspersample);
//	bitsperpixel = bitspersample * samplesperpixel;
	dwBytePerLine = (width*bitsperpixel+31)/32 *4;
//	dwLeng = height*dwBytePerLine;
//	pData = (char *)malloc(sizeof(char)*dwLeng);
	raster = (uint32*)_TIFFmalloc(width * height * sizeof (uint32));
	TIFFReadRGBAImage(tiff, width, height, raster, 1); 
	LOGI("w:%dh:%d", width, height);
	for (y = 0; y < (height / 2); y++)
	{
		for (x = 0; x < width; x++)
		{
			row = raster[x + width * y];
			raster[x + width * y] = raster[width * (height - y - 1) + x];
			raster[width * (height - y - 1) + x] = row;
		}
	}
//	_TIFFfree(raster);
	image->data = (char *)raster;
	image->width = width;
	image->height = height;
	image->depth = 32;
	image->bytes_per_line = dwBytePerLine;
	image->dest_x = 0;
	image->dest_y = 0;
	image->dest_w = width;
	image->dest_h = height;
	return(FH_ERROR_OK);
}

