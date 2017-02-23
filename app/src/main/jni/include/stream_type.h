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
	int dev_type;//�����豸

	//��Ƶ����
	int v_type;//��Ƶ���� 0:h264 1:mjpeg
	int v_w;//��Ƶ��
	int v_h;//��Ƶ��
	int v_fr;//��Ƶ֡��
	int v_vbr;//�Ƿ�̬����
	int v_max_bps;//��Ƶ�������	
	int v_bright;//����
	int v_constrast;//�Աȶ�
	int v_saturation;//���Ͷ�
	int v_hue;//ɫ��
	int v_reserve[16];

	//�����ר������
	int ipc_bae;//�Ƿ������Զ��ع�
	int ipc_bai;//�Ƿ��Զ���Ȧ
	int ipc_eshutter;//����
	int ipc_agc;//����
	int ipc_luma;//����
	int ipc_noise_filter;//��������0-6,6���
	int ipc_sharpen;//���0-255��6���
	int ipc_gama;//0-9
	int ipc_reserve[16];

	//��Ƶ����
	int a_type;//��Ƶ���� 0:g711u 1:g711a
	int a_bits;//��Ƶλ��
	int a_chn;//��Ƶͨ����
	int a_sample;//��Ƶ������
	int a_reserve[16];
	
	int reserve[16];
}extra_data;

// ������������
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
	VDEC_HIS_H265  =                0x0f,
	VDEC_HISH265_ENCRYPT = 0x10,
	VDEC_H264_ENCRYPT = 0x11,
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
	unsigned int    reserved[5];            // ����
}HW_MEDIAINFO;

#define VERSION_HIS(x) ((x) >= 40000 && (x) <= 49999)
#define VERSION_IPCAM(x) ((x) >= 60000 && (x) <= 69999)

#endif