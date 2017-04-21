LOCAL_PATH := $(call my-dir)




include $(CLEAR_VARS)
LOCAL_MODULE := osipparser2
LOCAL_SRC_FILES := libosipparser2.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := pj
LOCAL_SRC_FILES := libpj-arm-unknown-linux-androideabi.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := pj-util
LOCAL_SRC_FILES := libpjlib-util-arm-unknown-linux-androideabi.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := pjnath
LOCAL_SRC_FILES := libpjnath-arm-unknown-linux-androideabi.a
LOCAL_STATIC_LIBRARIES:=pj pj-util
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := crypto
LOCAL_SRC_FILES := libcrypto.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := ssl
LOCAL_SRC_FILES := libssl.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := ecamstream
LOCAL_SRC_FILES := libecamstream.a
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/include
LOCAL_STATIC_LIBRARIES:=osipparser2 pj pj-util pjnath gnustl_static ssl crypto
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := hwplay
LOCAL_SRC_FILES := libhwplay.so
LOCAL_CFLAGS := -fPIC
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/include
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := jpush
LOCAL_SRC_FILES := libjpush.so
LOCAL_CFLAGS := -fPIC
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/include
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := avdevice
LOCAL_SRC_FILES := libavdevice.a
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/include
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := avfilter
LOCAL_SRC_FILES := libavfilter.a
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/include
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := avutil
LOCAL_SRC_FILES := libavutil.a
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/include
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := postproc
LOCAL_SRC_FILES := libpostproc.a
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/include
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := swresample
LOCAL_SRC_FILES := libswresample.a
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/include
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := swscale
LOCAL_SRC_FILES := libswscale.a
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/include
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := avformat
LOCAL_SRC_FILES := libavformat.a
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/include
LOCAL_STATIC_LIBRARIES:=swscale swresample postproc avutil avfilter avdevice 
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := avcodec
LOCAL_SRC_FILES := libavcodec.a
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/include
LOCAL_STATIC_LIBRARIES:=swscale swresample postproc avutil avfilter avdevice
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := XQuquerOfflineSDK
LOCAL_SRC_FILES := libXQuquerOfflineSDK.so
LOCAL_CFLAGS := -fPIC
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/include
include $(PREBUILT_SHARED_LIBRARY)

#include $(CLEAR_VARS)
#LOCAL_MODULE := ffmpeg
# Add your application source files here...
#LOCAL_SRC_FILES := ffmpeg.c
#LOCAL_SHARED_LIBRARIES := libhwplay
#LOCAL_STATIC_LIBRARIES := swscale swresample postproc avutil avformat avfilter avdevice avcodec
#LOCAL_LDFLAGS := -LF:/Android/android-ndk-r8e/sources/cxx-stl/gnu-libstdc++/4.6/libs/armeabi-v7a
#LOCAL_LDLIBS := -llog -lgnustl_static -lGLESv2 -lz -ldl -lgcc
#	-L$(NDK_PLATFORMS_ROOT)/$(TARGET_PLATFORM)/arch-arm/usr/lib -L$(LOCAL_PATH) -lz -ldl -lgcc 
#include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := hiPlay
LOCAL_SRC_FILES := libHW_H265dec_Andr.a
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/include
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := hwtrans
LOCAL_SRC_FILES := libhwtrans.so
LOCAL_CFLAGS := -fPIC
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/include
include $(PREBUILT_SHARED_LIBRARY)




include $(CLEAR_VARS)
LOCAL_MODULE := player_jni
# Add your application source files here...
LOCAL_SRC_FILES := yv12gl_jni.c streamreq_jni.c audio_jni.c g711.cpp g7.cpp main1.c HiPlayDemo.cpp
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/include
LOCAL_SHARED_LIBRARIES := hwplay hiPlay hwtrans ecamstream jpush
#LOCAL_STATIC_LIBRARIES := ecamstream
LOCAL_LDFLAGS := -LE:/Android/android-ndk-r10e/sources/cxx-stl/gnu-libstdc++/4.8/libs/armeabi-v7a
LOCAL_LDLIBS := -llog  -lGLESv2 -lz -ldl -lgcc #-lgnustl_static
LOCAL_CFLAGS := -fPIC
LOCAL_DISABLE_FATAL_LINKER_WARNINGS := true
#	-L$(NDK_PLATFORMS_ROOT)/$(TARGET_PLATFORM)/arch-arm/usr/lib -L$(LOCAL_PATH) -lz -ldl -lgcc 
include $(BUILD_SHARED_LIBRARY)




