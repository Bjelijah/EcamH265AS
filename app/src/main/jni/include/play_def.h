#ifndef play_include_h
#define play_include_h
#include "hw_config.h"

#ifdef __cplusplus
#if __cplusplus
extern "C" {
#endif
#endif

typedef unsigned long PLAY_HANDLE;
typedef unsigned long MOTION_HANDLE;

typedef void (draw_callback)(PLAY_HANDLE handle,
									  unsigned int hDc,
									  long nUser);

typedef void (yuv_callback)(PLAY_HANDLE handle, 
									 const unsigned char* yuv,
									 int len,
									 int width,
									 int height,
									 unsigned long long time,
									 long user);

typedef void (yuv_callback_ex)(PLAY_HANDLE handle,
									 const unsigned char* y,
									 const unsigned char* u,
									 const unsigned char* v,
									 int y_stride,
									 int uv_stride,
									 int width,
									 int height,
									 unsigned long long time,
									 long user);

enum{
	PLAY_LIVE = 0,
	PLAY_FILE = 1,
};

enum{
	FILE_BMP         = 0,
	FILE_JPG         = 1,
};

enum{
	MOTION_RESULT_SUCCESS = 0,
	MOTION_RESULT_NO_MORE_DATA = 1,
	MOTION_RESULT_INVALID_HANDLE = 2,
	MOTION_RESULT_INVALID_PARAM = 3,
};

typedef struct{
	int row;
	int col;
	int time;
	int frame_idx;
	char data[72];
}motion_node;

BOOL		 hwplay_init(HWND window_id,int window_w,int window_h);
BOOL		 hwplay_release();
PLAY_HANDLE  hwplay_open_stream(const char* head,int head_len,int buf_len,int open_mode,RECT area);
PLAY_HANDLE  hwplay_open_local_file(const char* file_name);
BOOL 		 hwplay_play(PLAY_HANDLE handle);
BOOL 		 hwplay_stop(PLAY_HANDLE handle);
BOOL 		 hwplay_input_data(PLAY_HANDLE handle, const char* buf,int len);
BOOL 		 hwplay_open_sound(PLAY_HANDLE handle);
BOOL 		 hwplay_close_sound(PLAY_HANDLE handle);
BOOL 		 hwplay_is_sound_open(PLAY_HANDLE handle);
BOOL 		 hwplay_pause(PLAY_HANDLE handle,BOOL bpause);
BOOL 		 hwplay_is_pause(PLAY_HANDLE handle);
BOOL 		 hwplay_get_speed(PLAY_HANDLE handle,float* speed);
BOOL 		 hwplay_set_speed(PLAY_HANDLE handle,float speed);
BOOL 		 hwplay_refresh(PLAY_HANDLE handle);
BOOL 		 hwplay_save_to_bmp(PLAY_HANDLE handle,const char* bmp_path);
BOOL 		 hwplay_save_to_jpg(PLAY_HANDLE handle,const char* jpg_path,int quality);
BOOL 		 hwplay_get_yuv_source_data(PLAY_HANDLE handle,int left,int top,int w,int h,char* dst_buf,int buf_len);
BOOL 		 hwplay_get_video_size(PLAY_HANDLE handle,int* w, int* h);
BOOL 		 hwplay_step_froward(PLAY_HANDLE handle);
BOOL 		 hwplay_step_back(PLAY_HANDLE handle);
BOOL 		 hwplay_register_draw_fun(PLAY_HANDLE handle,draw_callback* fun,long user_data);
BOOL 		 hwplay_register_yuv_callback_ex(PLAY_HANDLE handle,yuv_callback_ex* fun,long user_data);
BOOL 		 hwplay_get_pos(PLAY_HANDLE handle,int* pos);
BOOL 		 hwplay_set_pos(PLAY_HANDLE handle,int pos);
BOOL 		 hwplay_get_played_msec(PLAY_HANDLE handle,int* msec);
BOOL 		 hwplay_get_total_msec(PLAY_HANDLE handle,int* totalmsec);
BOOL 		 hwplay_get_current_frame(PLAY_HANDLE handle,int* frame_num);
BOOL 		 hwplay_get_total_frame(PLAY_HANDLE handle,int* frame_num);
BOOL 		 hwplay_get_stream_buf_len(PLAY_HANDLE handle, int* buf_len);
BOOL 		 hwplay_get_stream_buf_remain(PLAY_HANDLE handle,int* buf_remain);
BOOL		 hwplay_realloc_stream_buf_len(PLAY_HANDLE handle, int new_buf_len);
BOOL 		 hwplay_clear_stream_buf(PLAY_HANDLE handle);
BOOL 		 hwplay_throw_b(PLAY_HANDLE handle,int throw_num);
BOOL 		 hwplay_throw_p(PLAY_HANDLE handle,BOOL bthrow);
BOOL 		 hwplay_wait_sync(PLAY_HANDLE handle,BOOL bwait);
BOOL 		 hwplay_auto_adjust_size(PLAY_HANDLE handle,BOOL bauto);
BOOL 		 hwplay_zoom_rect(PLAY_HANDLE handle,BOOL benable,RECT* dst,RECT* src);
BOOL 		 hwplay_set_frame(PLAY_HANDLE handle,int frame_num);
BOOL 		 hwplay_start_cut_file(const char* src_file,int beg_frame,int end_frame,const char* dst_file);
BOOL 		 hwplay_start_cut_file_by_time(const char* src_file,int beg_sec,int end_sec,const char* dst_file);
BOOL 		 hwplay_get_cut_pos(int* pos);
BOOL 		 hwplay_stop_cut_file();
BOOL 		 hwplay_get_framenum_in_buf(PLAY_HANDLE handle,int* frame_num);
BOOL		 hwplay_clear_framenum_in_buf(PLAY_HANDLE handle);
BOOL 		 hwplay_get_max_framenum_in_buf(PLAY_HANDLE handle,int* max_frame_num);
BOOL 		 hwplay_set_max_framenum_in_buf(PLAY_HANDLE handle,int max_frame_num);
BOOL 		 hwplay_start_join_file(const char* src_file,const char* dst_file);
BOOL 		 hwplay_get_join_pos(int* pos);
BOOL 		 hwplay_stop_join_file();

//智能搜索支持
MOTION_HANDLE 		 hwplay_start_motion_search(PLAY_HANDLE handle,RECT search_rt);
BOOL 			  	 hwplay_get_next_motion(MOTION_HANDLE handle,motion_node* mt_node,int* err_code);
BOOL				 hwplay_stop_motion_search(MOTION_HANDLE handle);

//osd
BOOL 				 hwplay_get_osd(PLAY_HANDLE handle, SYSTEMTIME* sys);
BOOL 				 hwplay_set_osd(PLAY_HANDLE handle,SYSTEMTIME sys);
BOOL 				 hwplay_get_frame_osd(PLAY_HANDLE handle,int frame,SYSTEMTIME* sys);

//播放模式
BOOL  				 hwplay_init_draw(BOOL used3d);

//图像处理
//色彩调节,参数范围(0-100)
BOOL  				 hwplay_start_color_adjust(PLAY_HANDLE handle);
BOOL  				 hwplay_set_color_value(PLAY_HANDLE handle,int bright,int contrast,int saturation,int hue);
BOOL  				 hwplay_get_color_value(PLAY_HANDLE handle,int* bright,int* contrast,int* saturation,int* hue);
BOOL  				 hwplay_get_color_default_value(PLAY_HANDLE handle,int* bright,int* contrast,int* saturation,int* hue);
BOOL  				 hwplay_stop_color_adjust(PLAY_HANDLE handle);
//黑白模式
BOOL  			     hwplay_start_balck_mode(PLAY_HANDLE handle);
BOOL  				 hwplay_stop_black_mode(PLAY_HANDLE handle);
//锐度调节,参数范围(0-100)
BOOL  				 hwplay_start_sharpen_adjust(PLAY_HANDLE handle);
BOOL  				 hwplay_set_sharpen_value(PLAY_HANDLE handle,int sharpen);
BOOL  				 hwplay_get_sharpen_value(PLAY_HANDLE handle,int* sharpen);
BOOL  				 hwplay_get_sharpen_default_value(PLAY_HANDLE handle,int* sharpen);
BOOL  				 hwplay_stop_sharpen_adjust(PLAY_HANDLE handle);
//梯度调节
BOOL  				 hwplay_start_gradient_adjust(PLAY_HANDLE handle);
BOOL  				 hwplay_set_gradient_mode(PLAY_HANDLE handle,BOOL bcolor,BOOL bcartoon);
BOOL 				 hwplay_stop_gradient_adjust(PLAY_HANDLE handle);

//显示比例 (因为视频编码尺寸不一定与实际显示的匹配，所以让用户来自己选择
//比如ipcam 子码流虽然是704 * 576的，但是实际却需要以16:9来显示。
//scale = "16:9" or scale = "4:3" or scale = "unknown"(显示整个窗口)
BOOL  				 hwplay_set_scale(PLAY_HANDLE handle,const char* scale);

BOOL 			     hwplay_reattch_rect(PLAY_HANDLE handle,RECT rt);

//原始数据回调
//可以替代hwplay_register_yuv_callback_ex
typedef void (source_callback)(PLAY_HANDLE handle,
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
										long user);
BOOL  hwplay_register_source_data_callback(PLAY_HANDLE handle,source_callback* fun,long user);

#ifdef __cplusplus
#if __cplusplus
}
#endif
#endif

#endif
