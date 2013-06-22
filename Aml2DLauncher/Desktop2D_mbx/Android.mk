#ifeq ($(BOARD_USE_DEFAULT_AML2DLAUNCHER_MBX),true)
LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-subdir-java-files) 
LOCAL_PACKAGE_NAME := Aml2DLauncher_mbx
LOCAL_CERTIFICATE := platform

include $(BUILD_PACKAGE)
#endif

