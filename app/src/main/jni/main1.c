


#include <jni.h>
#include <android/log.h>


#define LOGI(...) ((void)__android_log_print(ANDROID_LOG_INFO, "main_cc_jni", __VA_ARGS__))
#define LOGW(...) ((void)__android_log_print(ANDROID_LOG_WARN, "main_cc_jni", __VA_ARGS__))
#define LOGE(...) ((void)__android_log_print(ANDROID_LOG_ERROR, "main_cc_jni", __VA_ARGS__))



void my_test1(){
	LOGE("this is  from main1.cc");

//	uint8_t req_method = 0;
//	req_method |= 1<<kStreamReqMethodIce;
//	//req_method |= 1<<kStreamReqMethodUdp;
//	ret = demo.InviteLive(DEVID,req_method,UPNP_ADDR,UPNP_PORT);
//	//ret = demo.InviteLive(DEVID,req_method,NULL,0);
//	assert(ret);

}
