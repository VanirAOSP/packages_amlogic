LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)
#LOCAL_MODULE_TAGS := optional
LOCAL_ARM_MODE := arm

LOCAL_SRC_FILES := \
tif_aux.c        tif_flush.c     tif_print.c \
tif_close.c      tif_getimage.c  tif_read.c \
tif_codec.c      tif_jbig.c      tif_strip.c \
tif_color.c      tif_jpeg_12.c   tif_swab.c \
tif_compress.c   tif_jpeg.c      tif_thunder.c \
tif_dir.c        tif_luv.c       tif_tile.c \
tif_dirinfo.c    tif_lzma.c      tif_unix.c \
tif_dirread.c    tif_lzw.c       tif_version.c \
tif_dirwrite.c   tif_next.c      tif_warning.c \
tif_dumpmode.c   tif_ojpeg.c     \
tif_error.c      tif_open.c      tif_write.c \
tif_extension.c  tif_packbits.c  tif_zip.c \
tif_fax3.c       tif_pixarlog.c \
tif_fax3sm.c     tif_predict.c \

LOCAL_C_INCLUDES += \
	$(LOCAL_PATH)/include \

LOCAL_SHARED_LIBRARIES :=libjpeg libz
LOCAL_STATIC_LIBRARIES :=libcutils libpng  libgif

LOCAL_MODULE:= libtiff

include $(BUILD_STATIC_LIBRARY)
