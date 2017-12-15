LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

#opencv library
OPENCVROOT:= C:\OpenCV-android-sdk
OPENCV_CAMERA_MODULES:=on
OPENCV_INSTALL_MODULES:=on
OPENCV_LIB_TYPE:=SHARED
include ${OPENCVROOT}\sdk\native\jni\OpenCV.mk

LOCAL_MODULE    := MyLib
LOCAL_SRC_FILES := com_example_pdg_opencvexam_NativeClass.cpp
LOCAL_LDLIBS := -llog
LOCAL_DEFAULT_CPP_EXTENSION := cpp

include $(BUILD_SHARED_LIBRARY)