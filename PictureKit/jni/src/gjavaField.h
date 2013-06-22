#ifndef GLOBAL_JAVA_FIELD_INCLUDES_
#define GLOBAL_JAVA_FIELD_INCLUDES_
static jclass   gBitmapConfig_class;
static jfieldID gBitmapConfig_nativeInstanceID;

static jclass   gdecoderInfo_class;
static jfieldID gdecoderInfo_nativeInstanceID;
static jfieldID gdecoderInfo_imageWidthID;
static jfieldID gdecoderInfo_imageHeightID;
static jfieldID gdecoderInfo_widthToDecoderID;
static jfieldID gdecoderInfo_HeightToDecoderID;
static jfieldID gdecoderInfo_colorModeID;
static jfieldID gdecoderInfo_decodeModeID;
static jfieldID gdecoderInfo_thumbPreferID;
static jfieldID gdecoderInfo_pictureTypeID;
static jfieldID gdecoderInfo_3DPrefID;
static jfieldID gdecoderInfo_3DParam1ID;
static jfieldID gdecoderInfo_3DParam2ID;

static jclass   gImageInfo_class;
static jfieldID gImageInfo_nativeInstanceID;
static jfieldID gImageInfo_originImageWidthID;
static jfieldID gImageInfo_originImageHeightID;
static jfieldID gImageInfo_outImageWidthID;
static jfieldID gImageInfo_outImageHeightID;
static jfieldID gImageInfo_depthID;
static jfieldID gImageInfo_dataBufferID;

static jobject   gVMRuntime_singleton;
static jmethodID gVMRuntime_trackExternalAllocationMethodID;
static jmethodID gVMRuntime_trackExternalFreeMethodID;

static const char* this_class_name="com/amlogic/graphics/PictureKit";
#endif /* GLOBAL_JAVA_FIELD_INCLUDES_ */
