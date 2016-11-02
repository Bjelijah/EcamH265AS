#ifndef stream_type_include_h
#define stream_type_include_h

#define STREAM_AUDIO_FRAME 2
#define STREAM_I_FRAME 1
#define STREAM_P_FRAME 0
#define STREAM_MOTION_FRAME 3
#define STREAM_B_FRAME 4
#define STREAM_FOCUS_FRAME 5
#define STREAM_MJPEG_FRAME 6
#define STREAM_ANALYZE_FRAME 7
#define STREAM_EXTRA_FRAME 8

#define IS_VIDEO_FRAME(n) ((n) == STREAM_I_FRAME  \
|| (n)== STREAM_P_FRAME  \
||(n)== STREAM_B_FRAME  \
||(n) == STREAM_MJPEG_FRAME)

#define IS_AUDIO_FRAME(n) ((n) == STREAM_AUDIO_FRAME)

#define IS_LOCATE_FRAME(n)((n) == STREAM_I_FRAME \
|| (n) == STREAM_MJPEG_FRAME)

#define IS_EXTRA_FRAME(n)((n) == STREAM_EXTRA_FRAME)

typedef struct {
	long len;
	long type; //0-bbp frame,1-i frame,2-audio
	unsigned long long time_stamp;
	long tag;
	long sys_time;
	//long reserve[1];
}stream_head;

typedef struct  
{
	int dev_type;//所属设备

	//视频属性
	int v_type;//视频类型 0:h264 1:mjpeg
	int v_w;//视频宽
	int v_h;//视频高
	int v_fr;//视频帧率
	int v_vbr;//是否动态码流
	int v_max_bps;//视频最大码流	
	int v_bright;//亮度
	int v_constrast;//对比度
	int v_saturation;//饱和度
	int v_hue;//色度
	int v_reserve[16];

	//摄像机专有属性
	int ipc_bae;//是否启动自动曝光
	int ipc_bai;//是否自动光圈
	int ipc_eshutter;//快门
	int ipc_agc;//增益
	int ipc_luma;//流明
	int ipc_noise_filter;//噪声抑制0-6,6最大
	int ipc_sharpen;//锐度0-255，6最大
	int ipc_gama;//0-9
	int ipc_reserve[16];

	//音频属性
	int a_type;//音频类型 0:g711u 1:g711a
	int a_bits;//音频位数
	int a_chn;//音频通道数
	int a_sample;//音频采样率
	int a_reserve[16];
	
	int reserve[16];
}extra_data;

// 网络杂项数据
typedef struct{
	int  pos;
	int end[2];
	int reserve[8];
}backfocal_pos;

typedef struct 
{
	int type; //0 - extra_data backfocal_pos
	int len;
}extra_data_head;


#define  HW_MEDIA_TAG 0x48574D49
#define MAX_STREAM_LEN	(1024 * 1024)

#if 1
typedef enum{
	VDEC_H264 =						0x00,	
	ADEC_G711U =					0x01,
	ADEC_HISG711A =					0x2,
	VDEC_HISH264 =					0x03,
	ADEC_HISG711U =					0x04,
	ADEC_HISADPCM =					0x05,
	VDEC_MJPEG =					0x06,
	ADEC_RAW =						0x07,
	ADEC_G711A =					0x08,
	ADEC_HISADPCM_DVI4 =			0x09,
	ADEC_AAC =						0x0a,
	ADEC_G726_32 =					0x0b,
}HW_DEC_TYPE;
#else
typedef enum{
	VDEC_H264 = ((0 + 1) << 8),	
	ADEC_G711U = ((1 + 1) << 8),
	ADEC_HISG711A = ((2 + 1) << 8),
	VDEC_HISH264 = ((3 + 1) << 8),
	ADEC_HISG711U = ((4 + 1) << 8),
	ADEC_HISADPCM = ((5 + 1) << 8),
	VDEC_MJPEG = ((6 + 1) << 8),
	ADEC_RAW = ((7 + 1) << 8),
}HW_DEC_TYPE;
#endif

typedef struct 				
{
	unsigned int    media_fourcc;			// "HKMI": 0x484B4D49 Hikvision Media Information,"HWMI":0x48574D49
	long dvr_version;
	long vdec_code;
	long adec_code; 

	unsigned char au_bits; // 8,16...
	unsigned char au_sample;//Kbps 8,16,64
	unsigned char au_channel;//1,2
	unsigned char reserve;
	unsigned int    reserved[5];            // 保留
}HW_MEDIAINFO;

#define VERSION_HIS(x) ((x) >= 40000 && (x) <= 49999)
#define VERSION_IPCAM(x) ((x) >= 60000 && (x) <= 69999)

#endif