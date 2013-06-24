LOCAL_PATH := $(call my-dir)

common_flags += -Wno-attributes -fno-strict-aliasing 

include $(CLEAR_VARS)
LOCAL_MODULE    := libfaad
LOCAL_SRC_FILES := $(notdir $(wildcard $(LOCAL_PATH)/*.c))
LOCAL_ARM_MODE := arm
LOCAL_C_INCLUDES := $(LOCAL_PATH) \
	  $(LOCAL_PATH)/codebook    
LOCAL_CFLAGs += -Wno-attributes -fno-strict-aliasing

include $(BUILD_STATIC_LIBRARY)

include $(CLEAR_VARS)

LOCAL_SHARED_LIBRARIES += libutils libmedia libz libbinder libdl libcutils libc 

LOCAL_MODULE    := libfaad
LOCAL_SRC_FILES := $(notdir $(wildcard $(LOCAL_PATH)/*.c))
LOCAL_ARM_MODE := arm
LOCAL_MODULE_TAGS := optional
LOCAL_C_INCLUDES := $(LOCAL_PATH) \
	 $(LOCAL_PATH)/codebook   
LOCAL_PRELINK_MODULE := false 
LOCAL_CFLAGs += -Wno-attributes -fno-strict-aliasing

include $(BUILD_SHARED_LIBRARY)
