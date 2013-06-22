/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_amlogic_graphics_PictureKit */

#ifndef _Included_com_amlogic_graphics_PictureKit
#define _Included_com_amlogic_graphics_PictureKit
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_amlogic_graphics_PictureKit
 * Method:    PictureKitVersion
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_amlogic_graphics_PictureKit_PictureKitVersion
  (JNIEnv *, jclass);

/*
 * Class:     com_amlogic_graphics_PictureKit
 * Method:    getPictureInfoNative
 * Signature: (Ljava/lang/String;)Lcom/amlogic/graphics/DecoderInfo;
 */
JNIEXPORT jobject JNICALL Java_com_amlogic_graphics_PictureKit_getPictureInfoNative
  (JNIEnv *, jclass, jstring);

/*
 * Class:     com_amlogic_graphics_PictureKit
 * Method:    loadPictureNative
 * Signature: (Ljava/lang/String;Lcom/amlogic/graphics/DecoderInfo;)Lcom/amlogic/graphics/ImageInfo;
 */
JNIEXPORT jobject JNICALL Java_com_amlogic_graphics_PictureKit_loadPictureNative
  (JNIEnv *, jclass, jstring, jobject);

/*
 * Class:     com_amlogic_graphics_PictureKit
 * Method:    loadPicture2BmNative
 * Signature: (Ljava/lang/String;Lcom/amlogic/graphics/DecoderInfo;)Landroid/graphics/Bitmap;
 */
JNIEXPORT jobject JNICALL Java_com_amlogic_graphics_PictureKit_loadPicture2BmNative
  (JNIEnv *, jclass, jstring, jobject);

    JNIEXPORT void JNICALL Java_com_amlogic_graphics_PictureKit_freeBuffer
  (JNIEnv *, jclass, jobject);
    
#ifdef __cplusplus
}
#endif
#endif