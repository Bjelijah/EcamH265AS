#include <jni.h>
#include <pthread.h>
#include <android/log.h>
#include "ecamstreamreq.h"
#include "ice.h"
#include "hwplay/stream_type.h"
#include "hwplay/play_def.h"
#include <time.h>
#include "g711/g711.h"

//#include <sys/timeb.h>


#define LOGI(...) ((void)__android_log_print(ANDROID_LOG_INFO, "streamreq_jni", __VA_ARGS__))
#define LOGW(...) ((void)__android_log_print(ANDROID_LOG_WARN, "streamreq_jni", __VA_ARGS__))
#define LOGE(...) ((void)__android_log_print(ANDROID_LOG_ERROR, "streamreq_jni", __VA_ARGS__))

#define RESOURCE_ARRAY_LENGHT 20
struct StreamResource
{
  ecam_stream_req_t * req;
  //struct ecam_stream_req_context * context;
  PLAY_HANDLE play_handle;
  int is_playback;
  int has_create_thread;
  int is_exit;
  int stream_count;
  pthread_t id;
  pthread_t audio_thread_id;

  JavaVM * jvm;
  JNIEnv * env;
  jmethodID mid;
  jobject obj;

  size_t stream_len;

  time_t beg_time,end_time;
  //unsigned long yuv_timestamp;
};
static pthread_once_t once_ctrl = PTHREAD_ONCE_INIT;
//struct StreamResource * res = NULL;
//static int quick_quit;
static struct StreamResource *res[RESOURCE_ARRAY_LENGHT] ;

/*struct audio_data datas[1024];

struct audio_data{
	unsigned long timestamp;//时标,单位为毫秒
	const char* buf;//数据缓存,pcm数据
	int len;//数据长度,如果为视频则应该等于w * h * 3 / 2
	int au_sample;//音频采样率,视频数据无效
	int au_channel;//音频通道数,视频数据无效
	int au_bits;//音频位宽,视频数据无效
};*/

/* 鍏ㄥ眬鍒濆鍖�*/



static void global_init(void)
{
  ice_global_init();
  hwplay_init(1,0,0);
}
//static long timer = 0,lastTimer = 0;
struct timeval last_tv;

static int num = 0;
static uint32_t last_time = 0;

unsigned long long temp = 0;
static void on_yuv_callback_ex(PLAY_HANDLE handle,
									 const unsigned char* y,
									 const unsigned char* u,
									 const unsigned char* v,
									 int y_stride,
									 int uv_stride,
									 int width,
									 int height,
									 unsigned long long time,
									 long user)
{	
	//__android_log_print(ANDROID_LOG_INFO, "jni", "on_yuv_callback_ex :%llu, %llu",time,(time - temp));
	temp = time;
	//getNowTime();
	//sdl_display_input_data(y,u,v,width,height,time);

	//fixme
	/*
	struct timeval now;
	long diff;
	gettimeofday(&now,NULL);
	if (last_tv.tv_sec>0) {
		diff=now.tv_sec/1000+now.tv_usec*1000-(last_tv.tv_sec/1000+last_tv.tv_usec*1000);
	}
	__android_log_print(ANDROID_LOG_INFO, "yuv", "on_yuv_callback_ex:%ld ",diff);
	last_tv=now;
*/
	//fixme
//	LOGI("is exit=%d",res[user]->is_exit);
	if(res[user]->is_exit == 1) return;

	//LOGI("time=%d",time);
//	if(last_time == 0){
//		last_time = time;
//	}



	//LOGI("w*h = %d  *   %d",width,height);

	yv12gl_display(y,u,v,width,height,time);
}

/*void getNowTime(){
	 struct  timeval tv;
	 gettimeofday(&tv,NULL);
	 printf("tv_sec:%d.tv_usec:%d\n",tv.tv_sec,tv.tv_usec);
	 __android_log_print(ANDROID_LOG_INFO, "getNowTime", "tv_sec.tv_usec: %d.%d\n",tv.tv_sec,tv.tv_usec);
	//return lpsystime;
}*/

void on_source_callback(PLAY_HANDLE handle,
			int type,//3-音频,1-视频
			const char* buf,//数据缓存,如果是视频，则为YV12数据，如果是音频则为pcm数据
			int len,//数据长度,如果为视频则应该等于w * h * 3 / 2
			unsigned long timestamp,//时标,单位为毫秒
			long sys_tm,//osd 时间(1970到现在的UTC时间)
			int w,//视频宽,音频数据无效
			int h,//视频高,音频数据无效
			int framerate,//视频帧率,音频数据无效
			int au_sample,//音频采样率,视频数据无效
			int au_channel,//音频通道数,视频数据无效
			int au_bits,//音频位宽,视频数据无效
			long user)
{
//  __android_log_print(ANDROID_LOG_INFO, "123", "on_source_callback timestamp: %d type:%d",timestamp,type);
  if(res[user]->is_exit == 1) return;
  if (type == 0) {
	  //struct audio_data data;
	  //data.timestamp = timestamp;
	  //data.buf = buf;
    //audio_play(buf,len,au_sample,au_channel,au_bits);
  }/*else if(){
	  res->yuv_timestamp
  }*/
  //else if (type == 1) {
    //native_catch_picture(res[user]->play_handle);
  //}
  //__android_log_print(ANDROID_LOG_INFO, "JNI", "type0 over");

}

void on_audio_callback(PLAY_HANDLE handle,
		const char* buf,//数据缓存,如果是视频，则为YV12数据，如果是音频则为pcm数据
		int len,//数据长度,如果为视频则应该等于w * h * 3 / 2
		unsigned long timestamp,//时标,单位为毫秒
		long user){
	//__android_log_print(ANDROID_LOG_INFO, "audio", "on_audio_callback timestamp: %lu ",timestamp);

	if(res[user]->is_exit == 1) return;
	audio_play(buf,len,0,0,0);

}

/*void audio_thread(void *arg){
	__android_log_print(ANDROID_LOG_INFO, "audio", "pthread audio create success");


}*/

void timer_thread(void *arg){
//__android_log_print(ANDROID_LOG_INFO, "timer_thread", "1");
	int arr_index = (int)arg;
	if((*res[arr_index]->jvm)->AttachCurrentThread(res[arr_index]->jvm, &res[arr_index]->env, NULL) != JNI_OK) {
      //LOGE("%s: AttachCurrentThread() failed", __FUNCTION__);   
      return;
	}
	 /* get JAVA method first */
	jclass cls = (*res[arr_index]->env)->GetObjectClass(res[arr_index]->env,res[arr_index]->obj);
	//self.clz = (*self.env)->FindClass(self.env, "com/howell/webcam/player/YV12Renderer");
	if (cls == NULL) {   
		//LOGE("FindClass() Error.....");   
		goto error;   
	}
		//鍐嶈幏寰楃被涓殑鏂规硶
	res[arr_index]->mid = (*res[arr_index]->env)->GetMethodID(res[arr_index]->env, cls, "getStreamLen", "(I)V");

	if (res[arr_index]->mid == NULL) {
		//LOGE("GetMethodID() Error.....");   
		goto error;   
	}
	while(!res[arr_index]->is_exit){
//		__android_log_print(ANDROID_LOG_INFO, "timer_thread", "2");
		usleep(200*1000);
	 
		/* notify the JAVA */
		(*res[arr_index]->env)->CallVoidMethod(res[arr_index]->env,res[arr_index]->obj,res[arr_index]->mid,res[arr_index]->stream_len);
		res[arr_index]->stream_len = 0;
		//__android_log_print(ANDROID_LOG_INFO, "timer_thread", "3");
	}

	//__android_log_print(ANDROID_LOG_INFO, "timer_thread", "4");
	if ((*res[arr_index]->jvm)->DetachCurrentThread(res[arr_index]->jvm) != JNI_OK) {
				//LOGE("%s: DetachCurrentThread() failed", __FUNCTION__);   
	}   
	//__android_log_print(ANDROID_LOG_INFO, "timer_thread", "5");

	return;

error:
	if ((*res[arr_index]->jvm)->DetachCurrentThread(res[arr_index]->jvm) != JNI_OK) {
		//LOGE("%s: DetachCurrentThread() failed", __FUNCTION__);   
	}   
	return;
	
}


static uint32_t get_my_clock() {
          struct timeval time;
          gettimeofday(&time, NULL);
          uint64_t value = ((uint64_t)time.tv_sec) * 1000 + (time.tv_usec / 1000);
          return (uint32_t)(value & 0xfffffffful);
      }


static uint32_t my_last_time = 0;
static int my_frame_num = 0;

static void OnStreamArrive(ecam_stream_req_t * req, ECAM_STREAM_REQ_FRAME_TYPE media_type, const char * data, size_t len, uint32_t timestamp) {
	LOGE("on stream arrive");
/**
 *帧数统计
	my_frame_num++;
	if(my_last_time==0){
		my_last_time = get_my_clock();
		LOGI("last_time=%d",my_last_time);
	}



	if((get_my_clock()-my_last_time)>5000){
		LOGI("num=%d",my_frame_num);
		my_frame_num=0;
		my_last_time=0;
	}

	return;
*/
	//__android_log_print(ANDROID_LOG_INFO, "thread", "aaaaa");
	//return;
	//PLAY_HANDLE ph = ecam_stream_req_get_usr_data(req);
	//__android_log_print(ANDROID_LOG_INFO, "OnStreamArrive", "timestamp: %d",timestamp);
	//return;
	//if(media_type == 2){
		//return;
	//}
	int arr_index = ecam_stream_req_get_usr_data(req);
	//__android_log_print(ANDROID_LOG_INFO, "OnStreamArrive", "len: %d, arr_idx: %d",len,arr_index);

	res[arr_index]->stream_len += len;
	if(media_type != 2){
		res[arr_index]->stream_count++;
	}
	if(res[arr_index]->has_create_thread == 0){
		int ret;
		ret = pthread_create(&res[arr_index]->id,NULL,(void *)timer_thread,(void *)arr_index);
		if(ret != 0){
			__android_log_print(ANDROID_LOG_INFO, "thread", "create thread fail");
			//return;
		}
		res[arr_index]->has_create_thread = 1;
		//pthread_join(res->id,NULL);
	}
	
	stream_head head ;
	head.len = len + sizeof(stream_head);
	head.sys_time = time(NULL);
	head.tag = 0x48574D49;
	head.time_stamp =  (unsigned long long)timestamp / 90 * 1000;
	if(media_type == kFrameTypeAudio){
		head.time_stamp =  (unsigned long long)timestamp / 8 * 1000;
	}
	head.type = media_type;
	//__android_log_print(ANDROID_LOG_INFO, "jni", "-------------media_type %d- timestamp: %llu",media_type,head.time_stamp);
		//getNowTime();
	if(res[arr_index]->is_playback == 0){
		hwplay_input_data(res[arr_index]->play_handle, (char*)&head ,sizeof(head));
		hwplay_input_data(res[arr_index]->play_handle, data ,len);
	}else if (res[arr_index]->is_playback == 1)
	{
		#if 1
		while(res[arr_index]->play_handle != -1 && !res[arr_index]->is_exit)
		{
			if(!hwplay_input_data(res[arr_index]->play_handle, (char*)&head ,sizeof(head)))
			{
				usleep(10000);
				continue;
			}
			if(!hwplay_input_data(res[arr_index]->play_handle, data ,len))
			{
				usleep(10000);
				continue;
			}
			break;
		}
		#endif
	}

/*	int buf_len;
	int ret = hwplay_get_stream_buf_remain(res[arr_index]->play_handle,&buf_len);
	if(ret == 1)
	{
		__android_log_print(ANDROID_LOG_INFO, "jni", "buf_len %d",buf_len);
	}*/
	//__android_log_print(ANDROID_LOG_INFO, "OnStreamArrive", "OnStreamArrive exit arr_index:%d",arr_index);
}

static PLAY_HANDLE init_play_handle(int is_playback,int arr_index){
	__android_log_print(ANDROID_LOG_INFO, "jni", "start init ph palyback: %d",is_playback);
	//hwplay_init(1,352,288);
	char *desc = malloc(100);
	memset(desc,0,100);
	int payload;
	int ret = -1;
	ret = ecam_stream_req_get_audio(res[arr_index]->req, desc, &payload);
	__android_log_print(ANDROID_LOG_INFO, "init_play_handle", "ecam_stream_req_get_audio ret:%d,desc:%s,payload:%d",ret,desc,payload);
	//ret = ecam_stream_req_get_video(res[arr_index]->req, desc, &payload);
	//__android_log_print(ANDROID_LOG_INFO, "init_play_handle", "ecam_stream_req_get_video desc:%s,payload:%d",desc,payload);

	RECT area ;
	//memset(&area,0,sizeof(area));
	//area.right = 177;
	//area.bottom = 144;
	HW_MEDIAINFO media_head;
	memset(&media_head,0,sizeof(media_head));
	media_head.media_fourcc = HW_MEDIA_TAG;
	media_head.au_channel = 1;
	media_head.au_sample = 8;
	media_head.au_bits = 16;
	media_head.adec_code = ADEC_AAC;
	media_head.vdec_code = 0x0f;
	if(ret == 1){
		if(strstr(desc,"pcmu") != NULL || strstr(desc,"PCMU") != NULL){
			__android_log_print(ANDROID_LOG_INFO, "init_play_handle", "ecam_stream_req_get_audio g711");
			media_head.adec_code = ADEC_G711U;
		}
	}
	free(desc);
	__android_log_print(ANDROID_LOG_INFO, "init_play_handle", "ecam_stream_req_get_audio aac");
	PLAY_HANDLE  ph = hwplay_open_stream((char*)&media_head,sizeof(media_head),1024*1024,is_playback,area);
	ret = hwplay_open_sound(ph);
	__android_log_print(ANDROID_LOG_INFO, "JNI", "hwplay_open_sound ret:%d",ret);
	__android_log_print(ANDROID_LOG_INFO, "JNI", "is_playback is:%d",is_playback);
//	hwplay_set_max_framenum_in_buf(ph,25);
	//__android_log_print(ANDROID_LOG_INFO, "JNI", "media_head.media_fourcc is:%d",media_head.media_fourcc);
	__android_log_print(ANDROID_LOG_INFO, "JNI", "ph is:%d",ph);
	//resource->play_handle = ph;
	hwplay_register_yuv_callback_ex(ph,on_yuv_callback_ex,arr_index);
	//hwplay_register_source_data_callback(ph,on_source_callback,arr_index);
	hwplay_register_audio_callback(ph,on_audio_callback,arr_index);

	/*int ret = pthread_create(&res[arr_index]->audio_thread_id,NULL,(void *)audio_thread,NULL);
	if(ret != 0){
		__android_log_print(ANDROID_LOG_INFO, "thread", "create audio thread fail");
		//return;
	}*/
	//	__android_log_print(ANDROID_LOG_INFO, "JNI", "true");
	//else
	//	__android_log_print(ANDROID_LOG_INFO, "JNI", "false");
	hwplay_play(ph);
	return ph;
}

static struct ecam_stream_req_context * fill_context(JNIEnv *env,jobject obj){
	__android_log_print(ANDROID_LOG_INFO, "jni", "start init context");
	__android_log_print(ANDROID_LOG_INFO, "fill_context", "1");
	if(env == NULL){
		__android_log_print(ANDROID_LOG_INFO, "fill_context", "env == null");
	}
	if(obj == NULL){
		__android_log_print(ANDROID_LOG_INFO, "fill_context", "obj == null");
	}
	jclass clazz = (*env)->GetObjectClass(env, obj);	
	__android_log_print(ANDROID_LOG_INFO, "fill_context", "0.1");
	jfieldID playbackID = (*env)->GetFieldID(env,clazz, "playback", "I");
	__android_log_print(ANDROID_LOG_INFO, "fill_context", "0.2");
	jfieldID begID = (*env)->GetFieldID(env,clazz,"beg", "J");
	__android_log_print(ANDROID_LOG_INFO, "fill_context", "0.3");
	jfieldID endID = (*env)->GetFieldID(env,clazz,"end", "J");
	__android_log_print(ANDROID_LOG_INFO, "fill_context", "0.4");
	jfieldID re_inviteID = (*env)->GetFieldID(env,clazz, "re_invite", "I");
	__android_log_print(ANDROID_LOG_INFO, "fill_context", "0.5");
	jfieldID method_bitmapID = (*env)->GetFieldID(env,clazz, "method_bitmap", "I");
	__android_log_print(ANDROID_LOG_INFO, "fill_context", "0.6");
	jfieldID udp_addrID = (*env)->GetFieldID(env,clazz, "udp_addr", "Ljava/lang/String;");
	__android_log_print(ANDROID_LOG_INFO, "fill_context", "0.7");
	jfieldID udp_portID = (*env)->GetFieldID(env,clazz, "udp_port", "I");
	__android_log_print(ANDROID_LOG_INFO, "fill_context", "0.8");
	jfieldID ice_optID = (*env)->GetFieldID(env,clazz, "ice_opt", "Lcom/howell/entityclass/StreamReqIceOpt;");
	__android_log_print(ANDROID_LOG_INFO, "fill_context", "0.9");
	jfieldID cryptoID = (*env)->GetFieldID(env,clazz, "crypto", "Lcom/howell/entityclass/Crypto;");
	__android_log_print(ANDROID_LOG_INFO, "fill_context", "1.0");
	jfieldID channelID = (*env)->GetFieldID(env,clazz, "channel", "I");
	__android_log_print(ANDROID_LOG_INFO, "fill_context", "1.1");
	jfieldID streamID = (*env)->GetFieldID(env,clazz, "stream", "I");
	__android_log_print(ANDROID_LOG_INFO, "fill_context", "1.2");
	jint jplayback = (jint)(*env)->GetIntField(env,obj, playbackID);
	jlong jbeg = (jlong)(*env)->GetLongField(env,obj, begID);
	jlong jend = (jlong)(*env)->GetLongField(env,obj, endID);
	jint jre_invite = (jint)(*env)->GetIntField(env,obj, re_inviteID);
	jint jmethod_bitmap = (jint)(*env)->GetIntField(env,obj, method_bitmapID);
	jstring judp_addr = (jstring)(*env)->GetObjectField(env,obj, udp_addrID);	
	jint judp_port = (jint)(*env)->GetIntField(env,obj, udp_portID);
	//jobject jice_opt = (jobject)(*env)->GetObjectField(env,obj, ice_optID);

	jint jchannel = (jint)(*env)->GetIntField(env,obj, channelID);
	__android_log_print(ANDROID_LOG_INFO, "fill_context", "channel:%d",jchannel);
	jint jstream = (jint)(*env)->GetIntField(env,obj, streamID);
	__android_log_print(ANDROID_LOG_INFO, "fill_context", "stream:%d",jstream);
	
	const char* cudp_addr = (*env)-> GetStringUTFChars(env,judp_addr,NULL);
	__android_log_print(ANDROID_LOG_INFO, "jni", "jplayback %d,jre_invite %d,jmethod_bitmap %d,judp_addr %s,judp_port %d",jplayback,jre_invite,jmethod_bitmap
		,cudp_addr,judp_port);

	jobject streamReqIceOpt = (jobject)(*env)->GetObjectField(env,obj, ice_optID);
	
	__android_log_print(ANDROID_LOG_INFO, "jni", ">>>>>>>>>>> here 0");

	jclass clazz2 = (*env)->GetObjectClass(env, streamReqIceOpt);
	
	jfieldID comp_cntID = (*env)->GetFieldID(env,clazz2, "comp_cnt", "I");
	jfieldID stun_addrID = (*env)->GetFieldID(env,clazz2, "stun_addr", "Ljava/lang/String;");
	jfieldID stun_portID = (*env)->GetFieldID(env,clazz2, "stun_port", "I");
	jfieldID turn_addrID = (*env)->GetFieldID(env,clazz2, "turn_addr", "Ljava/lang/String;");
	jfieldID turn_portID = (*env)->GetFieldID(env,clazz2, "turn_port", "I");
	jfieldID turn_tcpID = (*env)->GetFieldID(env,clazz2, "turn_tcp", "I");
	jfieldID turn_usernameID = (*env)->GetFieldID(env,clazz2, "turn_username", "Ljava/lang/String;");
	jfieldID turn_passwordID = (*env)->GetFieldID(env,clazz2, "turn_password", "Ljava/lang/String;");

	__android_log_print(ANDROID_LOG_INFO, "jni", ">>>>>>>>>>> here 10");

	jint jcomp_cnt = (jint)(*env)->GetIntField(env,streamReqIceOpt, comp_cntID);
	__android_log_print(ANDROID_LOG_INFO, "jni", ">>>>>>>>>>> here 11");
	jstring jstun_addr = (jstring)(*env)->GetObjectField(env,streamReqIceOpt, stun_addrID);
	__android_log_print(ANDROID_LOG_INFO, "jni", ">>>>>>>>>>> here 12");
	jint jstun_port = (jint)(*env)->GetIntField(env,streamReqIceOpt, stun_portID);
	__android_log_print(ANDROID_LOG_INFO, "jni", ">>>>>>>>>>> here 13");
	jstring jturn_addr = (jstring)(*env)->GetObjectField(env,streamReqIceOpt, turn_addrID);
	__android_log_print(ANDROID_LOG_INFO, "jni", ">>>>>>>>>>> here 14");
	jint jturn_port = (jint)(*env)->GetIntField(env,streamReqIceOpt, turn_portID);
	__android_log_print(ANDROID_LOG_INFO, "jni", ">>>>>>>>>>> here 15");
	jint jturn_tcp = (jint)(*env)->GetIntField(env,streamReqIceOpt, turn_tcpID);
	__android_log_print(ANDROID_LOG_INFO, "jni", ">>>>>>>>>>> here 16");
	jstring jturn_username = (jstring)(*env)->GetObjectField(env,streamReqIceOpt, turn_usernameID);
	__android_log_print(ANDROID_LOG_INFO, "jni", ">>>>>>>>>>> here 17");
	jstring jturn_password = (jstring)(*env)->GetObjectField(env,streamReqIceOpt, turn_passwordID);
	__android_log_print(ANDROID_LOG_INFO, "jni", "!!!!!----------turn_addr %p----------!!!!!!",jturn_addr);


	const char* cstun_addr = (*env)-> GetStringUTFChars(env,jstun_addr,NULL);
	
	jobject crypto = (jobject)(*env)->GetObjectField(env,obj, cryptoID);
	jclass clazz3 = (*env)->GetObjectClass(env, crypto);

	jfieldID enableID = (*env)->GetFieldID(env,clazz3, "enable", "I");
	jint jenable = (jint)(*env)->GetIntField(env,crypto, enableID);
	__android_log_print(ANDROID_LOG_INFO, "jni", "jenable:%d",jenable);
	__android_log_print(ANDROID_LOG_INFO, "jni", "finish receive data");

	struct ecam_stream_req_context *c = malloc(sizeof(*c));
	memset(c,0,sizeof(struct ecam_stream_req_context));
	__android_log_print(ANDROID_LOG_INFO, "jni", "start fill context");
	__android_log_print(ANDROID_LOG_INFO, "jni", "1");
	c->playback = jplayback;
	__android_log_print(ANDROID_LOG_INFO, "jni", "2");
	c->beg = jbeg;
	__android_log_print(ANDROID_LOG_INFO, "jni", "3");
	c->end = jend;
	__android_log_print(ANDROID_LOG_INFO, "jni", "4");
	c->re_invite = jre_invite;//false
	__android_log_print(ANDROID_LOG_INFO, "jni", "5");
	c->method_map = jmethod_bitmap;
	__android_log_print(ANDROID_LOG_INFO, "jni", "6");
	//if (udp_addr != NULL) {
	strncpy(c->udp_addr,cudp_addr,63);
	__android_log_print(ANDROID_LOG_INFO, "jni", "7");
	//}
	c->udp_port = judp_port;
__android_log_print(ANDROID_LOG_INFO, "jni", "8");
	struct ICEOption *opt = malloc(sizeof(*opt));
	memset(opt,0,sizeof(struct ICEOption));
	__android_log_print(ANDROID_LOG_INFO, "jni", "9");
	opt->comp_cnt = jcomp_cnt;
	__android_log_print(ANDROID_LOG_INFO, "jni", "10");
	//stun server
	strcpy(opt->stun_addr,cstun_addr);
	opt->stun_port = jstun_port;
	__android_log_print(ANDROID_LOG_INFO, "jni", "11");
	//turn server
	if(jturn_addr != NULL){
		const char* cturn_addr = (*env)-> GetStringUTFChars(env,jturn_addr,NULL);
		strcpy(opt->turn_addr,cturn_addr);
		(*env)->ReleaseStringUTFChars(env,jturn_addr,cturn_addr);
	}
		__android_log_print(ANDROID_LOG_INFO, "jni", "12");
	if(jturn_port != -1){
		opt->turn_port = jturn_port;
	}
		__android_log_print(ANDROID_LOG_INFO, "jni", "13");
	if (jturn_tcp != -1)
	{
		opt->turn_tcp = jturn_tcp;
	}
		__android_log_print(ANDROID_LOG_INFO, "jni", "14");
	if(jturn_username != NULL){
		const char* cturn_username = (*env)-> GetStringUTFChars(env,jturn_username,NULL);
		strcpy(opt->turn_username,cturn_username);
		(*env)->ReleaseStringUTFChars(env,jturn_username,cturn_username);
	}
		__android_log_print(ANDROID_LOG_INFO, "jni", "15");
	if (jturn_password != NULL)
	{
		const char* cturn_password = (*env)-> GetStringUTFChars(env,jturn_password,NULL);
		strcpy(opt->turn_password,cturn_password);
		(*env)->ReleaseStringUTFChars(env,jturn_password,cturn_password);
	}
	__android_log_print(ANDROID_LOG_INFO, "jni", "16");
	//__android_log_print(ANDROID_LOG_INFO, "jni", "jturn_addr %s,jturn_port %d,jturn_tcp %d,jturn_username %s,jturn_password %s",jturn_addr,jturn_port,jturn_tcp,jturn_username,jturn_password);

	c->ice_opt = *opt;
		__android_log_print(ANDROID_LOG_INFO, "jni", "17");
	free(opt);
    //__android_log_print(ANDROID_LOG_INFO, "jni", "stun_server:<%s>",c->ice_opt.stun_addr);
	//__android_log_print(ANDROID_LOG_INFO, "jni", "createStreamReqContext success");
	(*env)->ReleaseStringUTFChars(env,judp_addr,cudp_addr);
	(*env)->ReleaseStringUTFChars(env,jstun_addr,cstun_addr);
		__android_log_print(ANDROID_LOG_INFO, "jni", "18");
	c -> crypto.enable = jenable;
	LOGI("crypto  enable = %d",c->crypto.enable);
	c->channel = jchannel;
	__android_log_print(ANDROID_LOG_INFO, "jni", "jni channel:%d",c->channel);
	c->stream = jstream;
	__android_log_print(ANDROID_LOG_INFO, "jni", "jni stream:%d",c->stream);
	__android_log_print(ANDROID_LOG_INFO, "jni", "19");
	return c;
}

static jlong new_resource(JNIEnv *env,jobject obj,const char * account,int is_playback)
{
	/* make sure init once */
	int arr_index = -1;
	pthread_once(&once_ctrl,global_init);
	//memset(&datas,0,1024*sizeof(struct audio_data));
	__android_log_print(ANDROID_LOG_INFO, "res[handle_flag]", "11111111");
	int i;
	for(i = 0 ; i < RESOURCE_ARRAY_LENGHT ; i++){
		if(res[i] == NULL){
			arr_index = i;
			break;
		}
	}
/*	if (res[arr_index] == NULL){
		res[arr_index] = (struct StreamResource *)calloc(1,sizeof(struct StreamResource));
		__android_log_print(ANDROID_LOG_INFO, "res[handle_flag]->id", "res[handle_flag]->id:%d",res[arr_index]->id);
	}*/
	if (arr_index == -1) {
		__android_log_print(ANDROID_LOG_INFO, "new resource", "index out of bound");
		return -1;
	}
	res[arr_index] = (struct StreamResource *)calloc(1,sizeof(struct StreamResource));
	res[arr_index]->req = ecam_stream_req_new(account);
	res[arr_index]->is_playback = is_playback;
	res[arr_index]->is_exit = 0;
	res[arr_index]->stream_len = 0;
	res[arr_index]->has_create_thread = 0;
	res[arr_index]->stream_count = 0;
	res[arr_index]->beg_time = 0;
	res[arr_index]->end_time = 0;
	ecam_stream_req_set_usr_data(res[arr_index]->req,(void *)arr_index);
	ecam_stream_req_regist_stream_cb(res[arr_index]->req,OnStreamArrive);
		  //res->context = init_context_handle(env,obj);
	//res[arr_index]->play_handle = init_play_handle(is_playback,arr_index);
	(*env)->GetJavaVM(env,&res[arr_index]->jvm);
	res[arr_index]->obj = (*env)->NewGlobalRef(env,obj);
	return arr_index;
}

static void free_resource(void* handle)
{
	/*__android_log_print(ANDROID_LOG_INFO, "jni", "stop0000000");
	res_index++;
	__android_log_print(ANDROID_LOG_INFO, "jni", "stop01111");
	if(res_index == 19){
		res_index = 0;
	}
	__android_log_print(ANDROID_LOG_INFO, "jni", "stop022222");
	//struct StreamResource *res = (struct StreamResource *)handle;

	__android_log_print(ANDROID_LOG_INFO, "jni", "index :%d",res_index);*/
	int arr_index = handle;
	if (res[arr_index] != NULL) {
		__android_log_print(ANDROID_LOG_INFO, "jni", "stop1111");

		__android_log_print(ANDROID_LOG_INFO, "jni", "stop22222");
		//pthread_join(res[flag]->id,NULL);
		__android_log_print(ANDROID_LOG_INFO, "jni", "start free ecam_stream_req stop");
		ecam_stream_req_free(res[arr_index]->req);
		__android_log_print(ANDROID_LOG_INFO, "jni", "finish free ecam_stream_req stop");
		__android_log_print(ANDROID_LOG_INFO, "jni", "start free hwplay stop");
		__android_log_print(ANDROID_LOG_INFO, "jni", "res[arr_index]->play_handle %d",res[arr_index]->play_handle);
		hwplay_stop(res[arr_index]->play_handle);
		__android_log_print(ANDROID_LOG_INFO, "jni", "stop333333333333");
		__android_log_print(ANDROID_LOG_INFO, "jni", "finish free hwplay stop");
		free(res[arr_index]);
        res[arr_index]=NULL;
	}else{
		__android_log_print(ANDROID_LOG_INFO, "jni", "stop handle is NULL!! ");
	}
}

int Java_com_howell_utils_InviteUtils_setCatchPictureFlag(JNIEnv *env, jclass cls,jlong index,jstring jpath,jint jlength)
{
	__android_log_print(ANDROID_LOG_INFO, "--->", "setflag");
	//self.is_catch_picture = 1;
	char* temp = (*env)-> GetStringUTFChars(env,jpath,NULL);
	__android_log_print(ANDROID_LOG_INFO, ">>>", "index :%d",index);
	__android_log_print(ANDROID_LOG_INFO, ">>>", "temp :%s",temp);
	int ret = hwplay_save_to_jpg(res[index]->play_handle,temp,70);
	__android_log_print(ANDROID_LOG_INFO, ">>>", "ret :%d",ret);
	(*env)->ReleaseStringUTFChars(env,jpath,temp);
	__android_log_print(ANDROID_LOG_INFO, ">>>", "finish fill buf");
	__android_log_print(ANDROID_LOG_INFO, "--->", "setflag over");
	return ret;
}

void Java_com_howell_utils_InviteUtils_joinThread
(JNIEnv *env, jobject obj,jlong handle){
	__android_log_print(ANDROID_LOG_INFO, "jni", "handle:%d",handle);
	__android_log_print(ANDROID_LOG_INFO, "jni", "start join thread ");
	int arr_index = handle;
	res[arr_index]->is_exit = 1;
	if(res[arr_index]->id != 0)
		pthread_join(res[arr_index]->id,NULL);
	__android_log_print(ANDROID_LOG_INFO, "jni", "finish join thread ");
}

long Java_com_howell_utils_InviteUtils_createHandle
(JNIEnv *env, jobject obj, jstring str,jint is_palyback){
	const char * account = (*env)->GetStringUTFChars(env,str,NULL);
	return new_resource(env,obj,account,is_palyback);
}

jstring Java_com_howell_utils_InviteUtils_prepareSDP
(JNIEnv *env, jclass cls, jlong handle,jobject obj){
	__android_log_print(ANDROID_LOG_INFO, "jni", "start prepareSDP ");
	if(obj == NULL){
		__android_log_print(ANDROID_LOG_INFO, "jni", "obj is null ");
	}
	char * local_sdp;
	//ecam_stream_req_t * stream_req_ = (ecam_stream_req_t *)handle;
	//struct ecam_stream_req_context context;
	//struct ecam_stream_req_context *c = fill_context(env,obj,&context);
	struct ecam_stream_req_context *c = fill_context(env,obj);
	//struct StreamResource * res = handle;
	int arr_index = handle;
	__android_log_print(ANDROID_LOG_INFO, "jni", "arr_index:%d handle:%ld",arr_index,handle);
	__android_log_print(ANDROID_LOG_INFO, "jni", "res:%p",res[arr_index]);
	__android_log_print(ANDROID_LOG_INFO, "jni", "req:%p",res[arr_index]->req);
	local_sdp = (char *)ecam_stream_req_prepare_sdp(res[arr_index]->req,c);
    __android_log_print(ANDROID_LOG_INFO, "jni", "sdp:<%s>",local_sdp);
	free(c);
	__android_log_print(ANDROID_LOG_INFO, "jni", "prepareSDP success");
	return (*env)->NewStringUTF(env,local_sdp);
}

int Java_com_howell_utils_InviteUtils_handleRemoteSDP
(JNIEnv *env, jclass cls, jlong handle,jobject obj, jstring dialog_id, jstring remote_sdp){
	//ecam_stream_req_t * stream_req_ = (ecam_stream_req_t *)handle;
	struct ecam_stream_req_context *c = fill_context(env,obj);
	//struct StreamResource * res = handle;
	int arr_index = handle;
	if(res[arr_index] == NULL) return -1;//澶辫触
	char *dialog_id_jni = (*env)-> GetStringUTFChars(env,dialog_id,NULL);
	char *remote_sdp_jni = (*env)-> GetStringUTFChars(env,remote_sdp,NULL);
	ecam_stream_req_handle_remote_sdp(res[arr_index]->req,c,dialog_id_jni,remote_sdp_jni);
	//初始化解码器
	res[arr_index]->play_handle = init_play_handle(res[arr_index]->is_playback,arr_index);

	free(c);	
	(*env)->ReleaseStringUTFChars(env,remote_sdp,remote_sdp_jni);
	(*env)->ReleaseStringUTFChars(env,remote_sdp,dialog_id_jni);
	__android_log_print(ANDROID_LOG_INFO, "jni", "handleRemoteSDP success");
	return 0;//鎴愬姛
}

int Java_com_howell_utils_InviteUtils_start
(JNIEnv *env, jclass cls, jlong handle,jobject obj, jint timeout_ms){
	//ecam_stream_req_t * stream_req_ = (ecam_stream_req_t *)handle;
	__android_log_print(ANDROID_LOG_INFO, "jni", "!!!!!!-----start start----------!!!!");
	struct ecam_stream_req_context *c = fill_context(env,obj);
	__android_log_print(ANDROID_LOG_INFO, "jni", "stream:%d,channel:%d",c->stream,c->channel);
	//PLAY_HANDLE ph = initDecoder();
	//resource->play_handle = ph;
	//ecam_stream_req_set_usr_data(stream_req_,ph);
	//struct StreamResource * res = handle;
	int arr_index = handle;
	if(res[arr_index] == NULL){
		__android_log_print(ANDROID_LOG_INFO, "start func", "res[arr_index] == NULL");
		return -1;//澶辫触
	}
	//pauseAudio();
	__android_log_print(ANDROID_LOG_INFO, "jni", "ecam_stream_req_start");
	int ret = ecam_stream_req_start(res[arr_index]->req,c,timeout_ms);
	__android_log_print(ANDROID_LOG_INFO, "ret------------>", "ret %d",ret);
	free(c);
	//pauseAudio();
	__android_log_print(ANDROID_LOG_INFO, "jni", "!!!!!!-----finish start----------!!!!");
	return ret;
}

void Java_com_howell_utils_InviteUtils_freeHandle
(JNIEnv *env, jclass cls, jlong handle){
	__android_log_print(ANDROID_LOG_INFO, "JNI", "Start stop");
	free_resource(handle);
	__android_log_print(ANDROID_LOG_INFO, "JNI", "finish stop");
}

void Java_com_howell_utils_InviteUtils_prepareReplay
(JNIEnv *env, jclass cls,jint isPlayBack, jlong handle){
	//struct StreamResource * res = handle;
	int arr_index = handle;
	ecam_stream_req_stop(res[arr_index]->req,3000);
	__android_log_print(ANDROID_LOG_INFO, ">>>>>>>>>", "ecam_stream_req_stop");
	hwplay_stop(res[arr_index]->play_handle);
	__android_log_print(ANDROID_LOG_INFO, ">>>>>>>>>", "hwplay_stop");
	yuv12gl_set_enable(1);
	res[arr_index]->play_handle = init_play_handle(isPlayBack,arr_index);
	__android_log_print(ANDROID_LOG_INFO, ">>>>>>>>>", "init_play_handle");
}

jint Java_com_howell_utils_InviteUtils_getMethod
(JNIEnv *env, jclass cls,jlong handle){
	int arr_index = handle;
	int req_flag = ecam_stream_req_get_transfer_method(res[arr_index]->req);
	if(req_flag == 0){
		return 0;//OTHER
	}else if(req_flag == 1){
		return 3;//UPNP	
	}else if(req_flag == 2){
		ICE_t *ice = ecam_stream_req_get_ice(res[arr_index]->req);
		int ice_flag = ice_get_type(ice);
		if(ice_flag == 0){
			return 0;//OTHER
		}else if(ice_flag == 1){
			return 2;//STUN
		}else if(ice_flag == 2){
			return 1;//TURN
		}else{
			return -1;//error
		}
	}else{
		return -1;//error
	}
}

int Java_com_howell_utils_InviteUtils_getStreamCount
(JNIEnv *env, jclass cls,jlong handle){
	__android_log_print(ANDROID_LOG_INFO, "getStreamCount", "000000000 ,handle:%d",handle);
	int arr_index = handle;
	__android_log_print(ANDROID_LOG_INFO, "getStreamCount", "1111111111");
	__android_log_print(ANDROID_LOG_INFO, "getStreamCount", "2222222222,res[arr_index]->stream_count: %d",res[arr_index]->stream_count);
	return res[arr_index]->stream_count;
}

void Java_com_howell_utils_InviteUtils_playbackPause
(JNIEnv *env, jclass cls,jlong handle,jboolean bPause){
	int arr_index = handle;
	hwplay_pause(res[arr_index]->play_handle,bPause);
}

void Java_com_howell_utils_InviteUtils_getSdpTime
(JNIEnv *env, jclass cls,jlong handle){
	int arr_index = handle;
	ecam_stream_req_get_sdp_time(res[arr_index]->req,&res[arr_index]->beg_time, &res[arr_index]->end_time);
}

long Java_com_howell_utils_InviteUtils_getBegSdpTime
(JNIEnv *env, jclass cls,jlong handle){
	int arr_index = handle;
	return res[arr_index]->beg_time;
}

long Java_com_howell_utils_InviteUtils_getEndSdpTime
(JNIEnv *env, jclass cls,jlong handle){
	int arr_index = handle;
	return res[arr_index]->end_time;
}

int Java_com_howell_utils_TalkManager_setAudioData(JNIEnv *env, jclass cls,jlong handle,jbyteArray bytes ,int len){
	int arr_index = handle;
	char *data = (*env)->GetByteArrayElements(env,bytes,NULL);
	if(data == NULL){
		__android_log_print(ANDROID_LOG_INFO, "setAudioData", "data == NULL");
		return -1;
	}
	int dstlen = 0;
	char *encodeData = malloc(1024);
	memset(encodeData,0,1024);
	g711u_Encode(data, encodeData, len, &dstlen);
	int ret = ecam_stream_send_audio(res[arr_index]->req,0, encodeData, dstlen, 0);
	(*env)->ReleaseByteArrayElements(env,bytes, data, 0);
	free(encodeData);
	return ret;
}

//extern void my_test();



void Java_com_howell_utils_InviteUtils_testMainJni
(JNIEnv *env, jclass cls){
	my_test1();
//	LOGI("test main jni  ");
}







