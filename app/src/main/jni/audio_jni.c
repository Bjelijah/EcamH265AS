#include <jni.h>
#include <stdio.h>
#include <string.h>
#include <android/log.h>
#include <semaphore.h>

#define LOGI(...) ((void)__android_log_print(ANDROID_LOG_INFO, "audio_jni", __VA_ARGS__))   
#define LOGW(...) ((void)__android_log_print(ANDROID_LOG_WARN, "audio_jni", __VA_ARGS__))   
#define LOGE(...) ((void)__android_log_print(ANDROID_LOG_ERROR, "audio_jni", __VA_ARGS__))  


struct AudioPlay
{
  /* multi thread */
  int method_ready;
  JavaVM * jvm;
  JNIEnv * env;
  jmethodID mid;
  jobject obj;
  jfieldID data_length_id;
  jbyteArray data_array;
  int data_array_len;

	int stop;
  sem_t over_audio_sem;
  sem_t over_audio_ret_sem;
};
static struct AudioPlay self;

void audio_stop()
{
	self.stop=1;
	//self.over = 1;   
	//sem_post(&self.over_audio_sem);  
	//sem_wait(&self.over_audio_ret_sem); 
}


void audio_play(const char* buf,int len,int au_sample,int au_channel,int au_bits)
{
	
	if (self.stop) return;

/*
  if (sem_trywait(&self.over_audio_sem)==0) {  
	  if (self.method_ready)
	  {  
		  
	  }
	  sem_post(&self.over_audio_ret_sem);  
	  self.stop=1;
	  return;
  }
  */

  if ((*self.jvm)->AttachCurrentThread(self.jvm, &self.env, NULL) != JNI_OK) {   
      LOGE("%s: AttachCurrentThread() failed", __FUNCTION__);   
      return;
    }
  /* get JAVA method first */
  if (!self.method_ready) {
    

    jclass cls;
    cls = (*self.env)->GetObjectClass(self.env,self.obj);
    if (cls == NULL) {   
      LOGE("FindClass() Error.....");   
      goto error;   
    }
    //�ٻ�����еķ���   
    self.mid = (*self.env)->GetMethodID(self.env, cls, "audioWrite", "()V");
    if (self.mid == NULL) {   
      LOGE("GetMethodID() Error.....");   
      goto error;
    }

    self.method_ready=1;
  }
   
  /* update length */
  (*self.env)->SetIntField(self.env,self.obj,self.data_length_id,len);
  /* update data */

  if (len<=self.data_array_len) {
	  //LOGI("audio_play");

    (*self.env)->SetByteArrayRegion(self.env,self.data_array,0,len,buf);

    /* notify the JAVA */
    (*self.env)->CallVoidMethod(self.env, self.obj, self.mid, NULL);


  }

   if ((*self.jvm)->DetachCurrentThread(self.jvm) != JNI_OK) {   
				LOGE("%s: DetachCurrentThread() failed", __FUNCTION__);   
	}   
  /* char* data = (*self.env)->GetByteArrayElements(self.env,self.data_array,0); */
  /* memcpy(data,buf,len); */
 
  return;

 error:
  if ((*self.jvm)->DetachCurrentThread(self.jvm) != JNI_OK) {   
    LOGE("%s: DetachCurrentThread() failed", __FUNCTION__);   
  }   
}

JNIEXPORT void JNICALL Java_com_howell_activity_PlayerActivity_nativeAudioInit
(JNIEnv *env, jobject obj)
{
  (*env)->GetJavaVM(env,&self.jvm);   

  //����ֱ�Ӹ�ֵ(g_obj = obj)   
  self.obj = (*env)->NewGlobalRef(env,obj);
  jclass clz = (*env)->GetObjectClass(env, obj);
  self.data_length_id = (*env)->GetFieldID(env,clz, "mAudioDataLength", "I");

  jfieldID id = (*env)->GetFieldID(env,clz,"mAudioData","[B");

  jbyteArray data = (*env)->GetObjectField(env,obj,id);
  self.data_array = (*env)->NewGlobalRef(env,data);
  (*env)->DeleteLocalRef(env, data);
  self.data_array_len =(*env)->GetArrayLength(env,self.data_array);

  sem_init(&self.over_audio_sem,0,0);
  sem_init(&self.over_audio_ret_sem,0,0);

  self.method_ready = 0;
  self.stop = 0;
}

JNIEXPORT void JNICALL Java_com_howell_activity_PlayerActivity_nativeAudioStop
(JNIEnv *env, jclass cls)
{
  audio_stop();
}

JNIEXPORT void JNICALL Java_com_howell_activity_PlayerActivity_nativeAudioDeinit
(JNIEnv *env, jobject obj)
{
  /* TODO */
  
}
