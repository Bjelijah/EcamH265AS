#include <jni.h>
#include<stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <android/log.h>
#include <EGL/egl.h>
#include <EGL/eglext.h>
#include <GLES2/gl2.h>
#include <GLES2/gl2ext.h>

#include "include/stream_type.h"
#include "include/net_sdk.h"
#include "include/play_def.h"
#include "include/IHW265Dec_Api.h"
#include "transmgr.h"
#include"com_howell_jni_JniUtil.h"




#define LOGI(...) ((void)__android_log_print(ANDROID_LOG_INFO, "JNI", __VA_ARGS__))
#define LOGW(...) ((void)__android_log_print(ANDROID_LOG_WARN, "JNI", __VA_ARGS__))
#define LOGE(...) ((void)__android_log_print(ANDROID_LOG_ERROR, "JNI", __VA_ARGS__))



typedef struct{
	char set_time_method_name[32];
	char request_method_name[32];
}yv12_info_t;

typedef struct{
	char * y;
	char * u;
	char * v;
	unsigned long long time;
	int width;
	int height;
	int enable;

	/* multi thread */
	int method_ready;
	JavaVM * jvm;
	JNIEnv * env;
	jmethodID request_render_method,set_time_method;
	jobject callback_obj;
	pthread_mutex_t lock;
	unsigned long long first_time;
}YV12_display_t;

typedef struct AudioPlay
{
	/* multi thread */
	int method_ready,jsonMethod_ready;
	JavaVM * jvm;
	JNIEnv * env;
	jmethodID mid,jsonMid;
	jobject obj;
	jfieldID data_length_id;
	jbyteArray data_array;
	int data_array_len;
	int stop;
}TAudioPlay;
static TAudioPlay* self = NULL;


static yv12_info_t    * g_yuv_info     = NULL;
static YV12_display_t * g_yuv_display  = NULL;


/**
 *  Local Fun
 */
void yv12gl_display(const unsigned char * y, const unsigned char *u,const unsigned char *v, int width, int height, unsigned long long time)
{
	if(g_yuv_display == NULL) return;
	if (!g_yuv_display->enable) return;
	g_yuv_display->time = time/1000;

	if( g_yuv_display->jvm->AttachCurrentThread(&g_yuv_display->env,NULL)!= JNI_OK) {
		LOGE("%s: AttachCurrentThread() failed", __FUNCTION__);
		return;
	}
	/* get JAVA method first */
	if (!g_yuv_display->method_ready) {
		jclass cls =  g_yuv_display->env->GetObjectClass(g_yuv_display->callback_obj);
		if (cls == NULL) {
			LOGE("FindClass() Error.....");
			goto error;
		}
		//�ٻ�����еķ���

		g_yuv_display->request_render_method = g_yuv_display->env->GetMethodID(cls,g_yuv_info->request_method_name,"()V");
		g_yuv_display->set_time_method = g_yuv_display->env->GetMethodID(cls,g_yuv_info->set_time_method_name,"(J)V");
		if (g_yuv_display->request_render_method == NULL || g_yuv_display->set_time_method == NULL) {
			LOGE("%d  GetMethodID() Error.....",__LINE__);
			goto error;
		}
		g_yuv_display->method_ready=1;
	}
	g_yuv_display->env->CallVoidMethod(g_yuv_display->callback_obj,g_yuv_display->set_time_method,g_yuv_display->time);
	pthread_mutex_lock(&g_yuv_display->lock);
	if (width!=g_yuv_display->width || height!=g_yuv_display->height) {
		LOGI("g_display->width = %d  width=%d",g_yuv_display->width,width);
		if(g_yuv_display->y!=NULL){
			free(g_yuv_display->y);
			g_yuv_display->y = NULL;
		}
		if(g_yuv_display->u!=NULL){
			free(g_yuv_display->u);
			g_yuv_display->u = NULL;
		}
		if(g_yuv_display->v!=NULL){
			free(g_yuv_display->v);
			g_yuv_display->v = NULL;
		}
		g_yuv_display->y = (char *)realloc(g_yuv_display->y,width*height);
		g_yuv_display->u = (char *)realloc(g_yuv_display->u,width*height/4);
		g_yuv_display->v = (char *)realloc(g_yuv_display->v,width*height/4);
		g_yuv_display->width = width;
		g_yuv_display->height = height;
	}
	memcpy(g_yuv_display->y,y,width*height);
	memcpy(g_yuv_display->u,u,width*height/4);
	memcpy(g_yuv_display->v,v,width*height/4);
	pthread_mutex_unlock(&g_yuv_display->lock);

	g_yuv_display->env->CallVoidMethod(g_yuv_display->callback_obj,g_yuv_display->request_render_method,NULL);

	if (g_yuv_display->jvm->DetachCurrentThread() != JNI_OK) {
		LOGE("%s: DetachCurrentThread() failed", __FUNCTION__);
	}
	return;

	error:
	if (g_yuv_display->jvm->DetachCurrentThread() != JNI_OK) {
		LOGE("%s: DetachCurrentThread() failed", __FUNCTION__);
	}
	return;
}

void audio_play(const char* buf,int len)
{
	if(self==NULL){
		LOGE("self==NULL");
		return;
	}
	if (self->stop) {
		LOGE("self.stop =1");
		return;
	}
	if(self->obj == NULL){

		return;
	}
	if (self->jvm->AttachCurrentThread( &self->env, NULL) != JNI_OK) {
		LOGE("%s: AttachCurrentThread() failed", __FUNCTION__);
		return;
	}
	/* get JAVA method first */
	if (!self->method_ready) {


		jclass cls;
		cls = self->env->GetObjectClass(self->obj);
		if (cls == NULL) {
			LOGE("FindClass() Error.....");
			goto error;
		}
		//�ٻ�����еķ���
		self->mid = self->env->GetMethodID(cls, "audioWrite", "()V");
		if (self->mid == NULL) {
			LOGE("GetMethodID() Error.....");
			goto error;
		}

		self->method_ready=1;
	}
	/* update length */
	self->env->SetIntField(self->obj,self->data_length_id,len);
	/* update data */
	if (len<=self->data_array_len) {
		self->env->SetByteArrayRegion(self->data_array,0,len,(const signed char *)buf);
		/* notify the JAVA */
		self->env->CallVoidMethod( self->obj, self->mid, NULL);
	}

	LOGE("start to detach audio play thread");

	if (self->jvm->DetachCurrentThread() != JNI_OK) {
		LOGE("%s: DetachCurrentThread() failed", __FUNCTION__);
	}else{
		LOGE("audio_play detach ok");
	}
	/* char* data = (*self.env)->GetByteArrayElements(self.env,self.data_array,0); */
	/* memcpy(data,buf,len); */
	return;

	error:
	if (self->jvm->DetachCurrentThread() != JNI_OK) {
		LOGE("%s: DetachCurrentThread() failed", __FUNCTION__);
	}
}





////////////////////////////////////////////////////////
////////////////////////////////////////////////////////
/**
 *  JNI interface
 */
JNIEXPORT void JNICALL Java_com_howell_jni_JniUtil_YUVInit
(JNIEnv *env, jclass){
	if(g_yuv_info == NULL){
		g_yuv_info = (yv12_info_t*)malloc(sizeof(yv12_info_t));
		memset(g_yuv_info,0,sizeof(yv12_info_t));
	}
	if(g_yuv_display == NULL){
		g_yuv_display = (YV12_display_t*)malloc(sizeof(YV12_display_t));
		memset(g_yuv_display,0,sizeof(YV12_display_t));
		env->GetJavaVM(&g_yuv_display->jvm);
		//FIXME  now obj=JniYV12Util should be YV12Renderer
		pthread_mutex_init(&g_yuv_display->lock,NULL);
		g_yuv_display->width  = 352;
		g_yuv_display->height = 288;
		g_yuv_display->y = (char*)malloc(g_yuv_display->width*g_yuv_display->height);
		g_yuv_display->u = (char*)malloc(g_yuv_display->width*g_yuv_display->height/4);
		g_yuv_display->v = (char*)malloc(g_yuv_display->width*g_yuv_display->height/4);
		memset(g_yuv_display->y,0,g_yuv_display->width*g_yuv_display->height);//4:2:2 black
		memset(g_yuv_display->u,128,g_yuv_display->width*g_yuv_display->height/4);
		memset(g_yuv_display->v,128,g_yuv_display->width*g_yuv_display->height/4);
	}
}

JNIEXPORT void JNICALL Java_com_howell_jni_JniUtil_YUVDeinit
(JNIEnv *env, jclass){
	return;//we never release
	if(g_yuv_info!=NULL){
		free(g_yuv_info);
		g_yuv_info = NULL;
	}
	if(g_yuv_display!=NULL){
		if(g_yuv_display->callback_obj!=NULL){
			env->DeleteGlobalRef(g_yuv_display->callback_obj);
		}
		if(g_yuv_display->y!=NULL){
			free(g_yuv_display->y);
			g_yuv_display->y = NULL;
		}
		if(g_yuv_display->u!=NULL){
			free(g_yuv_display->u);
			g_yuv_display->u = NULL;
		}
		if(g_yuv_display->v!=NULL){
			free(g_yuv_display->v);
			g_yuv_display->v = NULL;
		}
		pthread_mutex_destroy(&g_yuv_display->lock);
		free(g_yuv_display);
		g_yuv_display = NULL;
	}
}

JNIEXPORT void JNICALL Java_com_howell_jni_JniUtil_YUVSetCallbackObject
(JNIEnv *env, jclass, jobject callbackObject, jint flag){
	if(g_yuv_info==NULL)return;
	switch (flag) {
	case 0:
		g_yuv_display->callback_obj = env->NewGlobalRef(callbackObject);
		break;
	default:
		break;
	}
}

JNIEXPORT void JNICALL Java_com_howell_jni_JniUtil_YUVSetCallbackMethodName
(JNIEnv *env, jclass, jstring method_name, jint flag){
	if(g_yuv_info==NULL)return;
	const char * _method_name= env->GetStringUTFChars(method_name,NULL);
	switch (flag) {
	case 0:
		strcpy(g_yuv_info->set_time_method_name,_method_name);
		break;
	case 1:
		strcpy(g_yuv_info->request_method_name,_method_name);
		break;
	default:
		break;
	}
	env->ReleaseStringUTFChars(method_name,_method_name);
}

JNIEXPORT void JNICALL Java_com_howell_jni_JniUtil_YUVLock
(JNIEnv *, jclass){
	if(g_yuv_display == NULL){
		return;
	}
	pthread_mutex_lock(&g_yuv_display->lock);
}

JNIEXPORT void JNICALL Java_com_howell_jni_JniUtil_YUVUnlock
(JNIEnv *, jclass){
	if(g_yuv_display == NULL){
		return;
	}
	pthread_mutex_unlock(&g_yuv_display->lock);
}

JNIEXPORT void JNICALL Java_com_howell_jni_JniUtil_YUVSetEnable
(JNIEnv *, jclass){
	g_yuv_display->enable = 1;
}

JNIEXPORT void JNICALL Java_com_howell_jni_JniUtil_YUVRenderY
(JNIEnv *, jclass){
	if (g_yuv_display->y == NULL) {
		char value[4] = {0,0,0,0};
		glTexImage2D(GL_TEXTURE_2D,0,GL_LUMINANCE,2,2,0,GL_LUMINANCE,GL_UNSIGNED_BYTE,value);
	}
	else {
		//LOGI("render y");
		glTexImage2D(GL_TEXTURE_2D,0,GL_LUMINANCE,g_yuv_display->width,g_yuv_display->height,0,GL_LUMINANCE,GL_UNSIGNED_BYTE,g_yuv_display->y);
	}
}

JNIEXPORT void JNICALL Java_com_howell_jni_JniUtil_YUVRenderU
(JNIEnv *, jclass){
	if (g_yuv_display->u == NULL) {
		char value[] = {128};
		glTexImage2D(GL_TEXTURE_2D,0,GL_LUMINANCE,1,1,0,GL_LUMINANCE,GL_UNSIGNED_BYTE,value);
	}
	else {
		glTexImage2D(GL_TEXTURE_2D,0,GL_LUMINANCE,g_yuv_display->width/2,g_yuv_display->height/2,0,GL_LUMINANCE,GL_UNSIGNED_BYTE,g_yuv_display->u);
	}
}

JNIEXPORT void JNICALL Java_com_howell_jni_JniUtil_YUVRenderV
(JNIEnv *, jclass){
	if (g_yuv_display->v == NULL) {
		char value[] = {128};
		glTexImage2D(GL_TEXTURE_2D,0,GL_LUMINANCE,1,1,0,GL_LUMINANCE,GL_UNSIGNED_BYTE,value);
	}
	else {
		glTexImage2D(GL_TEXTURE_2D,0,GL_LUMINANCE,g_yuv_display->width/2,g_yuv_display->height/2,0,GL_LUMINANCE,GL_UNSIGNED_BYTE,g_yuv_display->v);
	}
}

///////////////////////////////////////////
//////////////////////////////////////////
/////////////////////////////////////////////

/**
 *  net
 */
//192.168.18.23

struct StreamResource
{
	JavaVM * jvm;
	JNIEnv * env;
	jobject obj;
	jmethodID mid;
	USER_HANDLE user_handle;
	ALARM_STREAM_HANDLE alarm_stream_handle;
	PLAY_HANDLE play_handle;
	IH265DEC_HANDLE play_265_handle;
	LIVE_STREAM_HANDLE live_stream_handle;
	FILE_STREAM_HANDLE file_stream_handle;
	int media_head_len;
	int is_exit;	//退出标记位
};
static struct StreamResource * res = NULL;





static int register_nvr(const char* ip){
	//	int ret = hwnet_init(5888);
	//	/* 192.168.18.23 */
	//	LOGI("ret=%d    ip=%s   ",ret,ip);
	//	res->user_handle = hwnet_login(ip,5198,"admin","12345");
	//	if(res->user_handle == -1){
	//		LOGE("hwnet_login fail");
	//		return 0;
	//	}
	return 0;
}

void *HW265D_Malloc(UINT32 channel_id, UINT32 size)
{
	return (void *)malloc(size);
}

void HW265D_Free(UINT32 channel_id, void * ptr)
{
	free(ptr);
}

void HW265D_Log( UINT32 channel_id, IHWVIDEO_ALG_LOG_LEVEL eLevel, INT8 *p_msg, ...)
{
}

void initHi265Decode(){
	if(res == NULL)return;
	IHW265D_INIT_PARAM stInitParam = {0};
	IH265DEC_INARGS stInArgs;
	IH265DEC_OUTARGS stOutArgs = {0};
	IHWVIDEO_ALG_VERSION_STRU stVersion;

	INT32 MultiThreadEnable = 0;	// default is single thread mode
	INT32 DispOutput = 0;

	stInitParam.uiChannelID = 0;
	stInitParam.iMaxWidth   = 1920;
	stInitParam.iMaxHeight  = 1088;
	stInitParam.iMaxRefNum  = 4;

	stInitParam.eThreadType = MultiThreadEnable? IH265D_MULTI_THREAD: IH265D_SINGLE_THREAD;
	stInitParam.eOutputOrder= DispOutput? IH265D_DISPLAY_ORDER:IH265D_DECODE_ORDER;

	stInitParam.MallocFxn  = HW265D_Malloc;
	stInitParam.FreeFxn    = HW265D_Free;
	stInitParam.LogFxn     = HW265D_Log;

	INT32 iRet = 0;
	iRet = IHW265D_Create(&res->play_265_handle, &stInitParam);
	if (IHW265D_OK != iRet)
	{
		LOGE("265 create error");
	}

}

void deInit265Decode(){
	if(res==NULL)return;
	IHW265D_Delete(res->play_265_handle);
}



INT32 H265DecLoadAU(UINT8* pStream, UINT32 iStreamLen, UINT32* pFrameLen)
{
	UINT32 i;
	UINT32 state = 0xffffffff;
	BOOL32 bFrameStartFound=0;
	BOOL32 bSliceStartFound = 0;

	*pFrameLen = 0;
	if( NULL == pStream || iStreamLen <= 4)
	{
		return -1;
	}

	for( i = 0; i < iStreamLen; i++)
	{
		if( (state & 0xFFFFFF7E) >= 0x100 &&
				(state & 0xFFFFFF7E) <= 0x13E )
		{
			if( 1 == bFrameStartFound || bSliceStartFound == 1 )
			{
				if( (pStream[i+1]>>7) == 1)
				{
					*pFrameLen = i - 4;
					return 0;
				}
			}
			else
			{
				bSliceStartFound = 1;
				//bFrameStartFound = 1;
			}
		}

		/*find a vps, sps, pps*/
		if( (state&0xFFFFFF7E) == 0x140 ||
				(state&0xFFFFFF7E) == 0x142 ||
				(state&0xFFFFFF7E) == 0x144)
		{
			if (1 == bSliceStartFound)
			{
				bSliceStartFound = 1;
			}
			else if(1 == bFrameStartFound)
			{
				*pFrameLen = i - 4;
				return 0;
			}
			else
			{
				bFrameStartFound = 1;
			}
		}

		state = (state << 8) | pStream[i];
	}

	*pFrameLen = i;
	return -1;
}





void hi265InputData(const char *buf,int len){

	INT32 iNaluLen;

	H265DecLoadAU((UINT8 *)buf, (UINT32)len, (UINT32*)&iNaluLen);




}

















void on_live_stream_fun(LIVE_STREAM_HANDLE handle,int stream_type,const char* buf,int len,long userdata){
	//__android_log_print(ANDROID_LOG_INFO, "jni", "-------------stream_type %d-len %d",stream_type,len);
	//res->stream_len += len;
	if(res == NULL){
		return;
	}
	if(res->is_exit == 1){
		return;
	}



	int ret = hwplay_input_data(res->play_handle, buf ,len);

	//	LOGI("on live stream fun len=%d",len);

	//hi265InputData(buf,len);



}

static void on_source_callback(PLAY_HANDLE handle, int type, const char* buf, int len, unsigned long timestamp, long sys_tm, int w, int h, int framerate, int au_sample, int au_channel, int au_bits, long user){

	LOGE("type=%d  len=%d  w=%d  h=%d  timestamp=%ld sys_tm=%ld  framerate=%d  au_sample=%d  au_channel=%d au_bits=%d",type,len,w,h,timestamp,sys_tm,framerate,au_sample,au_channel,au_bits);

	if(type == 0){//音频
		//		audio_play(buf,len,au_sample,au_channel,au_bits);
		audio_play(buf, len);//add cbj
	}else if(type == 1){//视频
		unsigned char* y = (unsigned char *)buf;
		unsigned char* u = y+w*h;
		unsigned char* v = u+w*h/4;
		yv12gl_display(y,u,v,w,h,timestamp);
	}
}


JNIEXPORT void JNICALL Java_com_howell_jni_JniUtil_netInit
(JNIEnv *env, jclass cls){
	if(res == NULL){
		res = (StreamResource *)malloc(sizeof(StreamResource));
		memset(res,0,sizeof(StreamResource));
		env->GetJavaVM(&res->jvm);
		res->obj = NULL;
		res->is_exit = 0;
	}
}

JNIEXPORT void JNICALL Java_com_howell_jni_JniUtil_netDeinit
(JNIEnv *env, jclass){
	return ;//we never release
	if(res != NULL){
		res->is_exit = 1 ;
		if(res->obj!=NULL){
			env->DeleteGlobalRef(res->obj);
			res->obj = NULL;
		}
		free(res);
		res = NULL;
	}
}

JNIEXPORT jboolean JNICALL Java_com_howell_jni_JniUtil_login
(JNIEnv *env, jclass, jstring ip){
	const char* _ip = env-> GetStringUTFChars(ip,NULL);
	int ret = register_nvr(_ip);
	env->ReleaseStringUTFChars(ip,_ip);
	return ret==1?true:false;
}

JNIEXPORT jboolean JNICALL Java_com_howell_jni_JniUtil_loginOut
(JNIEnv *, jclass){
	//	if(res==NULL)return false;
	//	if(res->user_handle<0)return false;
	//	int ret = hwnet_logout(res->user_handle);
	//	return ret==1?true:false;
	return false;
}

JNIEXPORT void JNICALL Java_com_howell_jni_JniUtil_setCallBackObj
(JNIEnv *env, jclass, jobject obj){
	if(res == NULL) return;
	res->obj = env->NewGlobalRef(obj);
}

JNIEXPORT jboolean JNICALL Java_com_howell_jni_JniUtil_readyPlayLive
(JNIEnv *, jclass){
	if(res == NULL) return false;

	hwplay_init(1,0,0);
	RECT area;
	HW_MEDIAINFO media_head;
	memset(&media_head,0,sizeof(media_head));
	int slot = 0;
	int is_sub = 1;
	int connect_mode = 0;


	media_head.media_fourcc = HW_MEDIA_TAG;
	media_head.au_channel = 1;
	media_head.au_sample = 8;
	media_head.au_bits = 16;
	media_head.adec_code = ADEC_AAC;
	//	media_head.vdec_code = 0x0f;
	media_head.vdec_code = 0x10;

	/*
	res->live_stream_handle = hwnet_get_live_stream(res->user_handle,slot,is_sub,connect_mode,on_live_stream_fun,0);
	//initHi265Decode();
	int ret2 = hwnet_get_live_stream_head(res->live_stream_handle,(char*)&media_head,1024,&res->media_head_len);
	 */


	LOGE(" code= 0x%x",media_head.vdec_code);


	PLAY_HANDLE  ph = hwplay_open_stream((const char*)&media_head,sizeof(media_head),1024*1024,0,area);
	res->play_handle = ph;
	hwplay_open_sound(ph);
	//hwplay_set_max_framenum_in_buf(ph,is_playback?25:5);
	LOGI("ph=%d",ph);
	int b = hwplay_register_source_data_callback(ph,on_source_callback,0);
	LOGI("callback bool = %d   play_handle=%d ",b,res->play_handle);
	return res->play_handle>=0?true:false;
}

JNIEXPORT jboolean JNICALL Java_com_howell_jni_JniUtil_readyPlayPlayback
(JNIEnv *env, jclass cls){
	return Java_com_howell_jni_JniUtil_readyPlayLive(env,cls);
}

JNIEXPORT void JNICALL Java_com_howell_jni_JniUtil_playView
(JNIEnv *, jclass){
	if(res == NULL) return;
	if(res->play_handle<0)return;
	res->is_exit = 0;
	hwplay_play(res->play_handle);
}

JNIEXPORT void JNICALL Java_com_howell_jni_JniUtil_stopView
(JNIEnv *, jclass){
	if(res == NULL)return;

	hwplay_stop(res->play_handle);
	//hwnet_close_live_stream(res->live_stream_handle);

	res->is_exit = 1;
	//deInit265Decode();
}




///////////////////////////////
/////////////////////////////////////
///////////////////////////////////////


/**
 * audio
 */




JNIEXPORT void JNICALL Java_com_howell_jni_JniUtil_nativeAudioInit
(JNIEnv *env, jclass){
	if(self==NULL){
		self = (TAudioPlay*)malloc(sizeof(TAudioPlay));
		memset(self,0,sizeof(TAudioPlay));
		env->GetJavaVM(&self->jvm);
		self->jsonMethod_ready=0;
		self->method_ready = 0;
		self->stop = 0;
		LOGI("native audio init ok");
	}
}

JNIEXPORT void JNICALL Java_com_howell_jni_JniUtil_nativeAudioDeinit
(JNIEnv *env, jclass){
	return;//we never release
	if(self!=NULL){
		if(self->obj!=NULL){
			env->DeleteGlobalRef(self->obj);
			self->obj = NULL;
		}
		env->DeleteGlobalRef( self->data_array);
		free(self);
		self = NULL;
	}
}

JNIEXPORT void JNICALL Java_com_howell_jni_JniUtil_nativeAudioSetCallbackObject
(JNIEnv *env, jclass, jobject obj, jint flag){
	if(self==NULL)return;
	switch(flag){
	case 0:
		self->obj = env->NewGlobalRef(obj);
		break;
	default:
		break;
	}
}


JNIEXPORT void JNICALL Java_com_howell_jni_JniUtil_nativeAudioSetCallbackMethodName
(JNIEnv *env, jclass, jstring str, jint flag){
	if(self==NULL)return;
	if(self->obj==NULL)return;
	jclass clz = env->GetObjectClass(self->obj);
	char *_str = (char *)env->GetStringUTFChars(str,NULL);
	switch(flag){
	case 0:{
		self->data_length_id = env->GetFieldID(clz, _str, "I");
	}
	break;
	case 1:{
		jfieldID id = env->GetFieldID(clz,_str,"[B");
		jbyteArray data = (jbyteArray)env->GetObjectField(self->obj,id);
		self->data_array = (jbyteArray)env->NewGlobalRef(data);
		env->DeleteLocalRef(data);
		self->data_array_len =env->GetArrayLength(self->data_array);
	}
	break;
	default:
		break;
	}
	env->ReleaseStringUTFChars(str,_str);
}

JNIEXPORT void JNICALL Java_com_howell_jni_JniUtil_nativeAudioStop
(JNIEnv *, jclass){
	if(self==NULL)return;
	self->stop=1;
}

JNIEXPORT void JNICALL Java_com_howell_jni_JniUtil_nativeAudioBPlayable
(JNIEnv *, jclass){
	if(self==NULL)return;
	self->stop=0;
}



////////////////////////////////////////////
////////////////////////////////////////////
///////////////////////////////////////////

/**************************
 *  HISI encode
 **************************/

JNIEXPORT void JNICALL Java_com_howell_jni_JniUtil_getHI265Version
(JNIEnv *env, jclass){

	IHWVIDEO_ALG_VERSION_STRU stVersion;
	IHW265D_GetVersion(&stVersion);
	LOGI("version = %d",stVersion.uiCompileVersion);
}


/////////////////////////////////
//////////////////////////////////////////////
//////////////////////////////////////////////

/**
 * transmission
 */


typedef struct {
	char ip[32];
	int port;

	JavaVM* jvm;
	JNIEnv * env;
	jobject callback_obj;
	jmethodID on_connect_method,on_disconnect_method,on_recordFile_method,on_socket_error_method;
	int transDataLen;
}TRANS_T;

static TRANS_T* g_transMgr = NULL;

int on_my_connect(const char* session_id){

	if(g_transMgr==NULL)return -1;
	LOGI("session_id=%s",session_id);

	//		if(g_transMgr->jvm->AttachCurrentThread( &g_transMgr->env, NULL) != JNI_OK) {
	//				LOGE("%s: AttachCurrentThread() failed", __FUNCTION__);
	//				return -1;
	//		}

	JNIEnv *env = NULL;
	//		if(g_transMgr->jvm->GetEnv((void **)&env,JNI_VERSION_1_4)!=JNI_OK){
	//			LOGE("get env error");
	//			return -1;
	//		}

	JavaVM * _jvm = g_transMgr->jvm;


	if(_jvm->AttachCurrentThread( &env, NULL) != JNI_OK) {
		LOGE("%s: AttachCurrentThread() failed", __FUNCTION__);
		return 0;
	}



	jstring str = env->NewStringUTF(session_id);
	env->CallVoidMethod(g_transMgr->callback_obj,g_transMgr->on_connect_method,str);

	env->DeleteLocalRef(str);
	if (_jvm->DetachCurrentThread() != JNI_OK) {
		LOGE("%s: DetachCurrentThread() failed", __FUNCTION__);
	}


	return 0;
}
int on_record_list_res(const char* jsonStr,int len){
	if(jsonStr==NULL)return -1;
	if(g_transMgr==NULL)return -1;
	if(g_transMgr->callback_obj==NULL)return -1;
	if(g_transMgr->on_recordFile_method==NULL)return -1;
	JNIEnv *env = NULL;
	JavaVM * _jvm = g_transMgr->jvm;
	if(_jvm->AttachCurrentThread( &env, NULL) != JNI_OK) {
		LOGE("%s: AttachCurrentThread() failed", __FUNCTION__);
		return 0;
	}
	jstring str = env->NewStringUTF(jsonStr);
	env->CallVoidMethod(g_transMgr->callback_obj,g_transMgr->on_recordFile_method,str);
	env->DeleteLocalRef(str);
	if (_jvm->DetachCurrentThread() != JNI_OK) {
		LOGE("%s: DetachCurrentThread() failed", __FUNCTION__);
	}

	return 0;
}

int on_my_ack_res(int msgCommand,void * res,int len){

	LOGI("msgCommand = 0x%x",msgCommand);
	switch (msgCommand) {
	case 0x13:
	{
		JNIEnv *env = NULL;
		JavaVM * _jvm = g_transMgr->jvm;
		if(_jvm->AttachCurrentThread( &env, NULL) != JNI_OK) {
			LOGE("%s: AttachCurrentThread() failed", __FUNCTION__);
			return 0;
		}
		env->CallVoidMethod(g_transMgr->callback_obj,g_transMgr->on_disconnect_method);
		if (_jvm->DetachCurrentThread() != JNI_OK) {
			LOGE("%s: DetachCurrentThread() failed", __FUNCTION__);
		}
	}
	break;
	case 0x104:
	{
		on_record_list_res((const char*)res,len);
	}
	break;
	default:
		break;
	}





	return 0;
}

long long g_timeStamp = 0;

long getTimeStamp(){

	g_timeStamp += 40;
	return g_timeStamp;
}

int on_my_socket_error_fun(){
	LOGE("on my socket_error fun");
	if(res == NULL)return -1;
	JNIEnv *env = NULL;
	JavaVM * _jvm = g_transMgr->jvm;
	if(_jvm->AttachCurrentThread( &env, NULL) != JNI_OK) {
		LOGE("%s: AttachCurrentThread() failed", __FUNCTION__);
		return 0;
	}
	env->CallVoidMethod(g_transMgr->callback_obj,g_transMgr->on_socket_error_method);
	if (_jvm->DetachCurrentThread() != JNI_OK) {
		LOGE("%s: DetachCurrentThread() failed", __FUNCTION__);
	}

	return 0;
}

int on_my_data_fun(int type,const char *data,int len){
	//LOGI("on data fun  len=%d",len);
	if(res == NULL){
		LOGE("res == null");
		return -1;
	}
	if(res->is_exit == 1){
		LOGE("res is exit");
		return -1;
	}
	if(res->play_handle==-1){
		LOGE("res play heandle = -1");
		return -1;
	}

#if 0
	stream_head head ;
	memset(&head,0,sizeof(head));
	head.len = len + sizeof(stream_head);
	head.sys_time = time(NULL);
	head.tag = 0x48574D49;
	//	head.time_stamp =  (unsigned long long)0 / 90 * 1000;//FIXME
	//	if(media_type == kFrameTypeAudio){
	//		head.time_stamp =  (unsigned long long)timestamp / 8 * 1000;
	//	}

	if(type==1){
		head.type =1;
		head.time_stamp = getTimeStamp();
	}else if(type == 4){
		head.type = 2;
	}else{
		head.type = 0;
	}
#endif



	//	LOGI("play handle=%d",res->play_handle);

	//int ret = hwplay_input_data(res->play_handle,(char*)&head ,sizeof(head));
	//LOGI("input data head ret=%d",ret);
	//__android_log_print(ANDROID_LOG_INFO, "jni", "-------------media_type %d- timestamp: %llu",media_type,head.time_stamp);
	//getNowTime();
	if(g_transMgr!=NULL){
		g_transMgr->transDataLen += len;
	}

	int ret = hwplay_input_data(res->play_handle, data ,len);
	LOGI("input data data ret=%d",ret);
	return 0;

	//Fixme
}







JNIEXPORT void JNICALL Java_com_howell_jni_JniUtil_transInit
(JNIEnv *env, jclass, jstring ip, jint port){
	if(g_transMgr==NULL){
		g_transMgr = (TRANS_T*)malloc(sizeof(TRANS_T));
		memset(g_transMgr,0,sizeof(TRANS_T));


		env->GetJavaVM(&g_transMgr->jvm);
	}
	trans_init(on_my_connect,on_my_ack_res,on_my_data_fun,on_my_socket_error_fun);
	const char * _ip = env->GetStringUTFChars(ip,0);
	strcpy(g_transMgr->ip,_ip);
	g_transMgr->port = port;

	env->ReleaseStringUTFChars(ip,_ip);
	LOGI("trans init ok");
}




JNIEXPORT void JNICALL Java_com_howell_jni_JniUtil_transDeinit
(JNIEnv *env, jclass){
	trans_deInit();
	return;//we never release
	if(g_transMgr!=NULL){
		if(g_transMgr->callback_obj!=NULL){
			env->DeleteGlobalRef(g_transMgr->callback_obj);
			g_transMgr->callback_obj = NULL;
		}
		free(g_transMgr);
		g_transMgr = NULL;
	}
}

JNIEXPORT void JNICALL Java_com_howell_jni_JniUtil_transConnect
(JNIEnv *env, jclass, jint type, jstring id, jstring name, jstring pwd){
	if(g_transMgr==NULL)return;
	const char * _id = env->GetStringUTFChars(id,0);
	const char * _name = env->GetStringUTFChars(name,0);
	const char * _pwd = env->GetStringUTFChars(pwd,0);

	int ret = trans_connect(type,_id,_name,_pwd,g_transMgr->ip,g_transMgr->port);
	LOGI("trans connet ret=%d",ret);
	env->ReleaseStringUTFChars(id,_id);
	env->ReleaseStringUTFChars(name,_name);
	env->ReleaseStringUTFChars(pwd,_pwd);
	LOGI("trans connect ok");
}

JNIEXPORT void JNICALL Java_com_howell_jni_JniUtil_transDisconnect
(JNIEnv *, jclass){
	trans_disconnect();
}

JNIEXPORT void JNICALL Java_com_howell_jni_JniUtil_transSetCallBackObj
(JNIEnv *env, jclass, jobject obj, jint flag){
	if(g_transMgr==NULL)return;
	switch(flag){
	case 0:
		g_transMgr->callback_obj = env->NewGlobalRef(obj);
		break;
	default:
		break;
	}

}

JNIEXPORT void JNICALL Java_com_howell_jni_JniUtil_transSetCallbackMethodName
(JNIEnv *env, jclass, jstring method, jint flag){
	if(g_transMgr==NULL) return;
	const char * _mehtod = env->GetStringUTFChars(method,0);
	switch(flag){
	case 0:{
		jclass clz = env->GetObjectClass(g_transMgr->callback_obj);
		g_transMgr->on_connect_method = env->GetMethodID(clz,_mehtod,"(Ljava/lang/String;)V");
		break;
	}

	case 1:{
		jclass clz = env->GetObjectClass(g_transMgr->callback_obj);
		g_transMgr->on_disconnect_method = env->GetMethodID(clz,_mehtod,"()V");
		break;
	}

	case 2:{
		jclass clz = env->GetObjectClass(g_transMgr->callback_obj);
		g_transMgr->on_recordFile_method = env->GetMethodID(clz,_mehtod,"(Ljava/lang/String;)V");
		break;
	}

	case 3:{
		jclass clz = env->GetObjectClass(g_transMgr->callback_obj);
		g_transMgr->on_socket_error_method = env->GetMethodID(clz,_mehtod,"()V");
		break;
	}

	default:
		break;
	}
	env->ReleaseStringUTFChars(method,_mehtod);
}

JNIEXPORT void JNICALL Java_com_howell_jni_JniUtil_transSubscribe
(JNIEnv *env, jclass, jstring jsonStr, jint jsonLen){
	const char *_jsonStr = env->GetStringUTFChars(jsonStr,0);
	trans_subscribe(_jsonStr,jsonLen);
	env->ReleaseStringUTFChars(jsonStr,_jsonStr);
}

JNIEXPORT void JNICALL Java_com_howell_jni_JniUtil_transUnsubscribe
(JNIEnv *env, jclass){
	trans_unsubscribe();
}


JNIEXPORT void JNICALL Java_com_howell_jni_JniUtil_catchPic
(JNIEnv *env, jclass, jstring path){
	if(res==NULL)return;
	if(res->play_handle==-1)return;
	const char *_path = env->GetStringUTFChars(path,0);
	//int ret = hwplay_save_to_jpg(res[index]->play_handle,temp,70);

	hwplay_save_to_jpg(res->play_handle,_path,70);

	env->ReleaseStringUTFChars(path,_path);

}



JNIEXPORT jint JNICALL Java_com_howell_jni_JniUtil_transGetStreamLenSomeTime
(JNIEnv *, jclass){
	if(g_transMgr==NULL)return 0;
	int streamLen = g_transMgr->transDataLen;
	g_transMgr->transDataLen = 0;
	return streamLen;
}

JNIEXPORT void JNICALL Java_com_howell_jni_JniUtil_transGetCam
(JNIEnv *env, jclass, jstring jsonStr, jint len){
	const char *_jsonStr = env->GetStringUTFChars(jsonStr,0);
	trans_getCamrea(_jsonStr,len);
	env->ReleaseStringUTFChars(jsonStr,_jsonStr);

}

JNIEXPORT void JNICALL Java_com_howell_jni_JniUtil_transGetRecordFiles
(JNIEnv *env, jclass, jstring jsonStr, jint len){
	const char *_jsonStr = env->GetStringUTFChars(jsonStr,0);
	trans_getRecordFiles(_jsonStr,len);
	env->ReleaseStringUTFChars(jsonStr,_jsonStr);
}

JNIEXPORT void JNICALL Java_com_howell_jni_JniUtil_transSetCrt
(JNIEnv *env, jclass, jstring ca, jstring client, jstring key){
	const char * _ca = env->GetStringUTFChars(ca,0);
	const char * _client = env->GetStringUTFChars(client,0);
	const char * _key  = env->GetStringUTFChars(key,0);

	LOGI("ca:%s",_ca);
	LOGI("client:%s",_client);
	LOGI("key:%s",_key);
	trans_set_crt(_ca,_client,_key);

	env->ReleaseStringUTFChars(ca,_ca);
	env->ReleaseStringUTFChars(client,_client);
	env->ReleaseStringUTFChars(key,_key);


}

JNIEXPORT void JNICALL Java_com_howell_jni_JniUtil_transSetCrtPaht
(JNIEnv *env, jclass, jstring caPath, jstring clientPath, jstring keyPath){
	const char *_caPath = env->GetStringUTFChars(caPath,0);
	const char * _clientPath = env->GetStringUTFChars(clientPath,0);
	const char * _keyPath = env->GetStringUTFChars(keyPath,0);

	LOGI("ca path:%s",_caPath);
	LOGI("client path:%s",_clientPath);
	LOGI("key path:%s",_keyPath);

	trans_set_crt_path(_caPath,_clientPath,_keyPath);

	env->ReleaseStringUTFChars(caPath,_caPath);
	env->ReleaseStringUTFChars(clientPath,_clientPath);
	env->ReleaseStringUTFChars(keyPath,_keyPath);
}

JNIEXPORT void JNICALL Java_com_howell_jni_JniUtil_turnInputViewData
(JNIEnv *env, jclass, jbyteArray byteArray, jint len){
	if(res == NULL){
		LOGE("res == null");
		return ;
	}
	if(res->is_exit == 1){
		LOGE("res is exit");
		return ;
	}
	if(res->play_handle==-1){
		LOGE("res play heandle = -1");
		return ;
	}

	jbyte * data ;
	data = env->GetByteArrayElements(byteArray,0);

	if(g_transMgr!=NULL){
		g_transMgr->transDataLen += len;
	}
	int ret = hwplay_input_data(res->play_handle,(const char*) data ,len);
	LOGI("input data data ret=%d",ret);
	env->ReleaseByteArrayElements(byteArray,data,0);
}




