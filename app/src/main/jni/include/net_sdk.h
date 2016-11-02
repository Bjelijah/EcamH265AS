#ifndef net_sdk_include_h
#define net_sdk_include_h

#include "net_err.h"
#include "protocol_type.h"

#ifdef __cplusplus
#if	__cplusplus
extern "C"{
#endif
#endif

typedef  long USER_HANDLE;
typedef  long LIVE_STREAM_HANDLE;
typedef  long FILE_STREAM_HANDLE;
typedef  long FILE_LIST_HANDLE;
typedef  long ALARM_STREAM_HANDLE;
typedef  long VOICE_STREAM_HANDLE;

enum HW_STREAM_TYPE
{
	HW_STREAM_P_FRAME = 0,
	HW_STREAM_I_FRAME = 1,
	HW_STREAM_AUDIO_FRAME = 2,
	HW_STREAM_MOTION_FRAME = 3, 
	HW_STREAM_B_FRAME = 4,
	HW_STREAM_FOCUS_FRAME = 5,
	HW_MJPEG_FRAME = 6,
}; 
enum HW_ALARM_TYPE
{
	HW_ALARM_HARD = 1,
	HW_ALARM_MOTION = 2,
	HW_ALARM_VIDEO_LOST = 3,
	HW_ALARM_START_REC = 4,
	HW_ALARM_STOP_REC = 5,
	HW_ALARM_MASK = 6,
	HW_ALARM_IN = 7,
	HW_ALARM_HEARTBEAT = 8,
	HW_ALARM_MOTIONEX = 10,
	HW_ALARM_SLOT_NODEFINE = 11,
	HW_ALARM_SLOT_LOST = 12,
};

/*预览数据回调函数
 * handle:					预览句柄
 * stream_type:				参见HW_STREAM_TYPE
 * buf:						预览数据buf
 * len:						预览数据长度
 * userdata:				hwnet_get_live_stream传进来的userdata	
 * return:					无
 * */
typedef void live_stream_fun(LIVE_STREAM_HANDLE handle,int stream_type,const char* buf,int len,long userdata);


/*录像数据回调函数
 * handle:					录像句柄
 * buf:						录像数据buf
 * len:						录像数据长度
 * userdata:				hwnet_get_file_stream传进来的userdata
 * return:					无
 * */
typedef void file_stream_fun(FILE_STREAM_HANDLE handle,const char* buf,int len,long userdata);


/*
 * 报警数据回调函数
 * handle:					报警句柄
 * alarm_type:				报警类型,请参见HW_ALARM_TYPE
 * buf:						报警数据
 * 	--如果alarm_type != HW_ALARM_MOTIONEX,则buf的数据结构为
 * 		struct
 * 		{
 * 			int value;//一般为通道值
 * 			int status;//一般不用
 * 			int reserve[32];
 * 		};
 * 	--如果alarm_type == HW_ALARM_MOTIONEX,则buf的数据结构为
 * 		struct
 * 		{
 * 			int slot;
 * 			int sec;//1970到现在的sec
 * 			unsigned msec;//毫秒
 * 			char data[128 * 128 / 8]//报警区域
 * 			int reserve[32];
 * 		};
 * len:						报警数据长度
 * userdata:				用户数据
 */
typedef void alarm_stream_fun(ALARM_STREAM_HANDLE handle,int alarm_type,const char* buf,int len,long userdata);


/*初始化(一开始调用)
 * local_port:				本地侦听端口(保留)	
 * return:					1:成功 0:失败
 * */
BOOL hwnet_init(int local_port);


/*卸载(最后调用)
 * return:					1:成功 0:失败
 * */
BOOL hwnet_release();


/*登陆服务器
 * ip:						ip地址
 * port:					端口
 * log_name:				用户名	
 * log_psw:					密码
 * return:					-1:失败 >=0:成功
 * */
USER_HANDLE hwnet_login(const char* ip,int port,const char* log_name,const char* log_psw);


/*登出服务器
 * handle:					hwnet_login()返回的句柄
 * return:					1:成功  0:失败
 * */
BOOL hwnet_logout(USER_HANDLE handle);


/*获取服务器基本信息
 *	handle:					hwnet_login()返回的句柄
 *	dev_config:				保存信息,不允许为NULL
 *	return:					1:成功 0:失败
 * */
BOOL hwnet_get_device_config(USER_HANDLE handle,device_info_t* dev_cfg);


/*获取服务器报警信息
 * handle:					hwnet_login()返回的句柄
 * alarm_state:				报警信息缓存,不允许为NULL
 * return:					1:成功 0:失败
 * */
BOOL hwnet_get_alarm_state(USER_HANDLE handle,alarm_state_t* alarm_state);


/*强制I frame
 * handle:					hwnet_login()返回的句柄
 * slot:					服务器通道(从0开始)
 * return:					1:成功 0:失败
 * */
BOOL hwnet_force_i_frame(USER_HANDLE handle,int slot);


/*获取预览数据
 * handle:					hwnet_login返回的句柄
 * slot:					服务端通道(从0开始)
 * is_sub:					1:子码流 0:主码流
 * connect_mode:			0:TCP 1:UDP
 * fun:						数据回调,不允许为NULL
 * userdata:				用户数据，传递到回调函数中
 * return:					-1:失败 >=0:成功
 * */
LIVE_STREAM_HANDLE hwnet_get_live_stream(USER_HANDLE handle,int slot,int is_sub,int connect_mode,live_stream_fun* fun,long userdata);


/*关闭预览数据
 * handle:					hwnet_get_live_stream()返回的句柄
 * return:					1:成功 0:失败
 * */
BOOL hwnet_close_live_stream(LIVE_STREAM_HANDLE handle);


/*获取预览数据头
 * handle:					hwnet_get_live_stream()返回的句柄
 * buf:						保存数据头的缓存
 * len:						数据缓存长度
 * head_len:				数据头的长度
 * return:					1:成功 0:失败
 * */
BOOL hwnet_get_live_stream_head(LIVE_STREAM_HANDLE handle,char* buf,int len,int* head_len);


/*获取录像文件数据
 * handle:					hwnet_login()返回的句柄
 * slot:					服务器通道号(从0开始)
 * beg:						文件开始时间
 * end:						文件结束时间
 * fun:						回调函数
 * userdata:				回调函数用户数据
 * file_info:				返回实际文件信息
 * return:					-1:失败 >=0:成功
 * */
typedef struct
{
	unsigned long long len;/*文件的长度*/
	int reserved[32];/*保留,必须为0*/
}file_stream_t;
FILE_STREAM_HANDLE hwnet_get_file_stream(USER_HANDLE handle,int slot,SYSTEMTIME beg,SYSTEMTIME end,file_stream_fun* fun,long userdata,file_stream_t* file_info);

FILE_STREAM_HANDLE hwnet_get_file_stream_ex(USER_HANDLE handle,int slot,SYSTEMTIME beg,SYSTEMTIME end,int type,file_stream_fun* fun,long userdata,file_stream_t* file_info);

FILE_STREAM_HANDLE hwnet_get_file_stream_ex2(USER_HANDLE handle,int slot,int stream,SYSTEMTIME beg,SYSTEMTIME end,int file_type,int time_type,file_stream_fun* fun,long userdata,file_stream_t* file_info);


/*关闭录像文件数据
 * handle:					hwnet_get_file_stream()返回的句柄
 * return:					1:成功 0:失败
 * */
BOOL hwnet_close_file_stream(FILE_STREAM_HANDLE handle);


/*获取录像数据头
 * handle:					hwnet_get_file_stream()返回的句柄
 * buf:						保存数据头的缓存
 * len:						数据缓存长度
 * head_len:				数据头的长度
 * return:					1:成功 0:失败
 * */
BOOL hwnet_get_file_stream_head(FILE_STREAM_HANDLE handle,char* buf,int len,int* head_len);


/*获取文件列表
 * handle:					hwnet_login()返回的句柄
 * slot:					服务器通道(从0开始)
 * beg:						搜索开始时间
 * end:						搜索结束时间
 * type:					文件类型 0-所有文件 1-普通录像文件 2-运动录像文件
 * return:					-1:失败 >=0:成功
 * */
FILE_LIST_HANDLE hwnet_get_file_list(USER_HANDLE handle,int slot,SYSTEMTIME beg,SYSTEMTIME end,int type);


/*按页获取文件列表
 * handle:					hwnet_login()返回的句柄
 * slot:					服务器通道(从0开始)
 * beg:						搜索开始时间
 * end:						搜索结束时间
 * type:					文件类型 0-所有文件 1-普通录像文件 2-运动录像文件
 * order_by_time:           0:升序  1:降序
 * time_type:               时间类型 0:北京时间 1:UTC时间
 * page_info:               请参照Pagination结构 
 */
FILE_LIST_HANDLE hwnet_get_file_list_by_page(USER_HANDLE handle,int slot,int stream,SYSTEMTIME beg,SYSTEMTIME end,int type,int order_by_time,int time_type,Pagination* page_info);


/*获取文件列表个数
 * handle:					hwnet_get_file_list()返回的句柄
 * count:					保存文件个数,不允许为NULL
 * return:					1:成功 0:失败
 * */
BOOL hwnet_get_file_count(FILE_LIST_HANDLE handle,int* count);


/*获取文件详细信息
 * handle:					hwnet_get_file_list()返回的句柄
 * index:					文件序号，有效值在0 - hwnet_get_file_count()
 * beg:						返回文件开始时间
 * end:						返回文件结束时间
 * type:					返回文件类型 1-普通录像文件 2-运动录像文件
 * return:					1:成功 0:失败
 * */
BOOL hwnet_get_file_detail(FILE_LIST_HANDLE handle,int index,SYSTEMTIME* beg,SYSTEMTIME* end,int* type);


/*关闭文件列表
 * handle:					hwnet_get_file_list()返回的句柄
 * return:					1:成功 0:失败
 * */
BOOL hwnet_close_file_list(FILE_LIST_HANDLE handle);


/*获取智能搜索文件列表
 * handle:					hwnet_login()返回的句柄
 * slot:					服务器通道(从0开始)
 * beg:						搜索开始时间
 * end:						搜索结束时间
 * search_rt:				搜索区域，范围为0-100
 * return:					-1:失败 >=0:成功 
 * */
FILE_LIST_HANDLE hwnet_get_smart_file_list(USER_HANDLE handle,int slot,SYSTEMTIME beg,SYSTEMTIME end,RECT search_rt);


/*获取服务器类型
 * handle:					hwnet_login()返回的句柄
 * type_int:				如果!=NULL,返回int的版本,可以为NULL,但是当为NULL时，type_str不允许为NULL
 * type_str:				如果!=NULL,返回字符串版本,可以为NULL,但是当为NULL时,type_int不允许为NULL
 * 							当前支持以下几种类型:"ipc","nvr","dvs","unknown"
 * return:					1:成功 0:失败
 * */
BOOL hwnet_get_type(USER_HANDLE handle,int* type_int,char* type_str);


/*获取色彩
 * handle:					hwnet_login()返回的句柄
 * slot:					服务器通道(从0开始)
 * bright:					返回亮度，不允许为NULL(0-255)
 * contrast:				返回对比度，不允许为NULL(0-255)
 * saturation:				返回饱和度，不允许为NULL(0-255)
 * hue:						返回色度,不允许为NULL(0-255)
 * return:					1:成功 0:失败
 * */
BOOL hwnet_get_video_color(USER_HANDLE handle,int slot,unsigned int* bright,unsigned int* contrast,unsigned int* saturation,unsigned int* hue);


/*设置色彩
 * handle:					hwnet_login()返回的句柄
 * slot:					服务器通道(从0开始)
 * bright:					亮度(0 - 255)
 * contrast:				对比度(0 - 255)
 * saturation:				饱和度(0 - 255)
 * hue:						色度(0 - 255)
 * return:					1:成功 0:失败
 * */
BOOL hwnet_set_video_color(USER_HANDLE handle,int slot,unsigned int bright,unsigned int contrast,unsigned int saturation,unsigned int hue);


/*获取网络设置
 * handle:					hwnet_login()返回的句柄
 * net_cfg:	
 * 	--port:					网络端口
 * 	--ip:					ip地址
 * 	--net_mask:				子网掩码
 * 	--gate_way:				网关
 * 	--mac_addr:				mac地址
 * 	--dns:					dns地址，当前无效
 * 	--multi_ip:				多播地址，当前无效
 * 	--ppoe_enable:			是否启用ppoe,当前无效
 * 	--ppoe_user:			如果ppoe_enable为1,表示ppoe用户名。当前无效。
 * 	--ppoe_password:		如果ppoe_enable为1,表示ppoe密码。当前无效。
 * 	--ppoe_ip:				如果ppoe_enable为1,表示ppoe ip地址。当前无效。
 * return:					1:成功 0：失败
 * */
BOOL hwnet_get_net_cfg(USER_HANDLE handle,net_cfg_t* net_cfg);


/*保存网络设置
 * handle:					hwnet_login()返回的句柄
 * net_cfg:	
 * 	--port:					网络端口
 * 	--ip:					ip地址
 * 	--net_mask:				子网掩码
 * 	--gate_way:				网关
 * 	--mac_addr:				mac地址
 * 	--dns:					dns地址，当前无效
 * 	--multi_ip:				多播地址，当前无效
 * 	--ppoe_enable:			是否启用ppoe,当前无效
 * 	--ppoe_user:			如果ppoe_enable为1,表示ppoe用户名。当前无效。
 * 	--ppoe_password:		如果ppoe_enable为1,表示ppoe密码。当前无效。
 * 	--ppoe_ip:				如果ppoe_enable为1,表示ppoe ip地址。当前无效。
 * return:					1:成功  0:失败
 * */
BOOL hwnet_set_net_cfg(USER_HANDLE handle,net_cfg_t* net_cfg);


/*获取通道配置,当前只适用于NVR
 * handle:					hwnet_login()返回的句柄
 * slot_cfg:				
 * 	--slot:					由用户填写，服务器通道号(从0开始)
 * 	--log_name:				接口返回，远程设备用户名,如果ip为"",无效
 * 	--log_psw:				接口返回，远程设备密码,如果ip为"",无效
 * 	--ip:					接口返回，远程设备ip地址,如果ip为"",表明该通道不连接任何设备。
 * 	--mac:					接口返回，远程设备mac地址,无效。
 * 	--port:					接口返回，远程设备端口号,通常为5198。如果ip为"",无效。
 * 	--remote_slot:			接口返回，远程设备通道号(从0开始)。如果ip为"",无效。
 * return:					1:成功  0:失败
 * */
BOOL hwnet_get_slot_cfg(USER_HANDLE handle,slot_cfg_t* slot_cfg);


/*保存通道配置,当前只适用于NVR
 * handle:					hwnet_login()返回的句柄
 * slot_cfg:					
 * 	--slot:					服务器通道号(从0开始)
 * 	--log_name:				远程设备用户名,如果ip为"\0",无效
 * 	--log_psw:				远程设备密码,如果ip为"",无效
 * 	--ip:					远程设备ip地址,如果ip为"",表明该通道不连接任何设备。
 * 	--mac:					远程设备mac地址,无效。
 * 	--port:					远程设备端口号,通常为5198。如果ip为"",无效。
 * 	--remote_slot:			远程设备通道号(从0开始)。如果ip为"",无效。
 * return:					1:成功  0:失败
 * */
BOOL hwnet_set_slot_cfg(USER_HANDLE handle,slot_cfg_t* slot_cfg);


/*获取图像质量
 * handle:					hwnet_login()返回的句柄
 * quality:				
 * 	--slot:					由用户填写，服务器通道号(从0开始)
 * 	--encode_type:			接口返回，0-cif,1-d1,2-720p 3-1080P
 * 	--quality_level:		接口返回，0-最好，1-次好，2-较好，3-一般
 * 	--max_bps:				接口返回，当最高位为0时，0-不限，1-较小，2-大，3-较大，4-最大,CIF(2000,400,600,800,1000),D1(4000,800,1000,1400,1600),720p(8000,2000,2500,3000,4000),1080p(14000,6000,8000,10000,12000)当最高位为1时，其他位为自定义码流大小。
 * 	--vbr:					接口返回，0-cbr(定码率) 1-vbr(编码率)
 * 	--framerate:			接口返回，0-全帧率,1~24-实际帧率
 * 	--reserve:				保留，必须为0
 * return:					1:成功  0:失败
 * */
BOOL hwnet_get_video_quality(USER_HANDLE handle,video_quality_t* quality);


/*保存图像质量
 * handle:					hwnet_login()返回的句柄
 * quality:				
 * 	--slot:					服务器通道号(从0开始)
 * 	--encode_type:			0-cif,1-d1,2-720p 3-1080P
 * 	--quality_level:		0-最好，1-次好，2-较好，3-一般
 * 	--max_bps:				当最高位为0时，0-不限，1-较小，2-大，3-较大，4-最大,CIF(2000,400,600,800,1000),D1(4000,800,1000,1400,1600),720p(8000,2000,2500,3000,4000),当最高位为1时，其他位为自定义码流大小。
 * 	--vbr:					0-cbr(定码率) 1-vbr(编码率)
 * 	--framerate:			0-全帧率,1~24-实际帧率
 * 	--reserve:				保留，必须为0
 * return:					1:成功  0:失败
 * */
BOOL hwnet_set_video_quality(USER_HANDLE handle,video_quality_t* quality);

/*获取子码流图像质量
 * handle:					hwnet_login()返回的句柄
 * quality:				
 * 	--slot:					由用户填写，服务器通道号(从0开始)
 * 	--used:					是否启用子码流，1-启用  0-不启用,当为0时，下面参数无效。 * 	--encode_type:			接口返回，0-cif,1-d1,2-720p 3-1080P 0xff-qcif
 * 	--quality_level:		接口返回，0-最好，1-次好，2-较好，3-一般
 * 	--max_bps:				接口返回，0-不限，1-较小，2-大，3-较大，4-最大,CIF(2000,400,600,800,1000),D1(4000,800,1000,1400,1600),720p(8000,2000,2500,3000,4000)。
 * 	--vbr:					接口返回，0-cbr(定码率) 1-vbr(编码率)
 * 	--framerate:			接口返回，0-全帧率,1~24-实际帧率
 * 	--reserve:				保留，必须为0
 * return:					1:成功  0:失败
 * */
BOOL hwnet_get_sub_video_quality(USER_HANDLE handle,sub_video_quality_t* quality);


/*获取子码流图像质量
 * handle:					hwnet_login()返回的句柄
 * quality:				
 * 	--slot:					服务器通道号(从0开始)
 * 	--used:					是否启用子码流，1-启用  0-不启用,当为0时，下面参数无效。
 * 	--encode_type:			0-cif,1-d1,2-720p 3-1080P 0xff-qcif
 * 	--quality_lev:			0-最好，1-次好，2-较好，3-一般
 * 	--max_bps:				0-不限，1-较小，2-大，3-较大，4-最大,CIF(2000,400,600,800,1000),D1(4000,800,1000,1400,1600),720p(8000,2000,2500,3000,4000)。
 * 	--vbr:					0-cbr(定码率) 1-vbr(编码率)
 * 	--framerate:			0-全帧率,1~24-实际帧率
 * 	--reserve:				保留，必须为0
 * return:					1:成功  0:失败
 * */
BOOL hwnet_set_sub_video_quality(USER_HANDLE handle,sub_video_quality_t* quality);


/*远程重启
 * handle:					hwnet_login()返回的句柄
 * return:					1:成功  0:失败
 * */
BOOL hwnet_reboot(USER_HANDLE handle);


/*远程关机
 * handle:					hwnet_login()返回的句柄
 * return:					1:成功  0:失败
 * */
BOOL hwnet_shutdown(USER_HANDLE handle);


/*获取远程通道数，当前只适用于NVR
 * handle:					hwnet_login()返回的句柄
 * count:					返回通道数目
 * return:					1:成功  0:返回
 * */
BOOL hwnet_get_channel_count(USER_HANDLE handle,unsigned int* count);

/*设置远程通道数，当前只适用于NVR
 * handle:					hwnet_login()返回的句柄
 * count:					设置通道数目
 * return:					1:成功  0:返回
 * */
BOOL hwnet_set_channel_count(USER_HANDLE handle,unsigned int count);


/*获取OSD日期
 * handle:					hwnet_login()返回的句柄
 * osd_date:
 * 	--slot:					由用户填写，服务器通道号(从0开始)
 * 	--show:					接口返回，1:显示  0:不显示
 * 	--left:					接口返回，左坐标，有效范围为0-703
 * 	--top:					接口返回，上坐标，有效范围为0-575
 * 	--type:					接口返回，日期类型，当前该参数保留
 * 	--show_week:			接口返回，是否显示日期，1-显示 0-不显示
 * return:					1:成功  0:返回
 * */
BOOL hwnet_get_osd_date(USER_HANDLE handle,osd_date_t* osd_date);


/*设置OSD日期
 * handle:					hwnet_login()返回的句柄
 * osd_date:
 * 	--slot:					服务器通道号(从0开始)
 * 	--show:					1:显示  0:不显示,当为0时，下面参数无效。
 * 	--left:					左坐标，有效范围为0-703
 * 	--top:					上坐标，有效范围为0-575
 * 	--type:					日期类型，当前该参数保留
 * 	--show_week:			是否显示日期，1-显示 0-不显示
 * return:					1:成功  0:返回
 * */
BOOL hwnet_set_osd_date(USER_HANDLE handle,osd_date_t* osd_date);


/*获取osd通道名称
 * handle:					hwnet_login()返回的句柄
 * osd_name:
 * 	--slot:					由用户填写，服务器通道号(从0开始)
 * 	--show:					接口返回，1:显示  0:不显示
 * 	--left:					接口返回，左坐标，有效范围为0-703
 * 	--top:					接口返回，上坐标，有效范围为0-575
 * 	--name:					接口返回，通道名称
 * return:					1:成功  0:返回
 * */
BOOL hwnet_get_osd_name(USER_HANDLE handle,osd_name_t* osd_name);


/*设置osd通道名称
 * handle:					hwnet_login()返回的句柄
 * osd_name:
 * 	--slot:					服务器通道号(从0开始)
 * 	--show:					1:显示  0:不显示,当为0时，下面参数无效
 * 	--left:					左坐标，有效范围为0-703
 * 	--top:					上坐标，有效范围为0-575
 * 	--name:					通道名称
 * return:					1:成功  0:返回
 * */
BOOL hwnet_set_osd_name(USER_HANDLE handle,osd_name_t* osd_name);


/*获取ipc参数
 * handle:					hwnet_login()返回的句柄
 * ipc_feature:
 * 	--slot:					由用户填写，服务器通道号(从0开始)
 * 	--bae:					接口返回，1-自动曝光， 0-非自动曝光
 * 	--eshutter:				接口返回，快门速度,参见HW_IPCAM_ESHUTTER_E
 *  --agcgain:				接口返回，增益,参见HW_IPCAM_AGC_E
 *  --black_white_mode:		接口返回，保留
 *  --badj:					接口返回，保留
 *  --black_level:			接口返回，保留
 *  --batuo_wb:				接口返回，保留
 *  --wb_mode:				接口返回，保留
 *	--rgb_gain:				接口返回，保留
 *	--noise_filter:			接口返回，保留
 *	--sharp:				接口返回，保留
 *	--luma:					接口返回，保留
 * return:					1:成功  0:返回
 * */
BOOL hwnet_ipc_get_feature(USER_HANDLE handle,ipc_feature_t* ipc_feature);


/*设置ipc参数
 * handle:					hwnet_login()返回的句柄
 * ipc_feature:
 * 	--slot:					服务器通道号(从0开始)
 * 	--bae:					1-自动曝光， 0-非自动曝光
 * 	--eshutter:				快门速度,参见HW_IPCAM_ESHUTTER_E
 *  --agcgain:				增益,参见HW_IPCAM_AGC_E
 *  --black_white_mode:		保留
 *  --badj:					保留
 *  --black_level:			保留
 *  --batuo_wb:				保留
 *  --wb_mode:				保留
 *	--rgb_gain:				保留
 *	--noise_filter:			保留
 *	--sharp:				保留
 *	--luma:					保留
 * return:					1:成功  0:返回
 * */
BOOL hwnet_ipc_set_feature(USER_HANDLE handle,ipc_feature_t* ipc_feature);


/*获取编码流类型
 * handle:					hwnet_login()返回的句柄
 * slot:					服务器通道号(从0开始)
 * type:					1-视频流，2-音频流，3-复合流
 * return:					1:成功  0:返回
 */
BOOL hwnet_get_stream_type(USER_HANDLE handle,int slot,int* stream_type);


/*设置编码流类型
 * handle:					hwnet_login()返回的句柄
 * slot:					服务器通道号(从0开始)
 * type:					1-视频流，2-音频流，3-复合流
 * return:					1:成功  0:返回
 */
BOOL hwnet_set_stream_type(USER_HANDLE handle,int slot,int stream_type);


/*获取移动侦测参数
 * handle:					hwnet_login()返回的句柄
 * row:						返回行数,可以为NULL
 * col:						返回列数,可以为NULL
 * motion_cfg:				
 * 	--slot:					服务器通道号(从0开始)
 * 	--lev:					灵敏度0-6,0-最高,5最低,6关闭
 * 	--rec_delay;			如果触发录像，需要录像多少时间。0-6,分别对应 10秒,20秒，30秒，1分，2分，6分，10分
 * 	--data;					是否设置了移动侦测
 * return:					1:成功	0:返回
 */
/*
 * example:
 * boo is_motion_set(motion_cfg_t* cfg,int x,int y,int cols)
 * {
 * 		int *data = (int*)cfg->data;
 * 		int int_num_in_one_row = 1 + (cols - 1) / 32;
 * 		return data[y * int_num_in_one_row + x / 32] & (1 << (x % 32));
 * }
 *
 * void motion_set(motion_cfg_t* cfg,int x,int y,int cols)
 * {
 * 		int * data = (int*)cfg->data;
 * 		int int_num_in_one_row = 1 + (cols - 1) / 32;
 * 		data[y * int_num_in_one_row + x / 32] |= (1 << (x % 32));
 * }
 *
 *	int row,col;
 *	motion_cfg_t motion_cfg;
 *	if(hwnet_get_motion_cfg(handle,&row,&col,&motion_cfg))
 *	{
 *		for(int = 0; i < row; i++)
 *		{
 *			for(int j = 0; j < col; j++)
 *			{
 *				if(is_motion_set(&cfg,i,j,col))
 *				{
 *					//该区域设置了移动侦测
 *				}
 *				else
 *				{	
 *					//该区域未设置移动侦测
 *				}
 *			}
 *		}
 *	}
 */
BOOL hwnet_get_motion_cfg(USER_HANDLE handle,int* row,int* col,motion_cfg_t* motion_cfg);


/*设置移动侦测参数
 * handle:					hwnet_login()返回的句柄
 * motion_cfg:				
 * 	--slot:					服务器通道号(从0开始)
 * 	--lev:					灵敏度0-6,0-最高,5最低,6关闭
 * 	--rec_delay;			如果触发录像，需要录像多少时间。0-6,分别对应 10秒,20秒，30秒，1分，2分，6分，10分
 * 	--data;					由18个long组成，根据返回的row,col来判断
 * return:					1:成功	0:返回

 */
BOOL hwnet_set_motion_cfg(USER_HANDLE handle,motion_cfg_t* motion_cfg);


/*获取报警信息
 * handle:					hwnet_login()返回的句柄
 * fun:						报警回调函数
 * userdata:				用户数据
 * return:					1:成功   0:返回
 */
ALARM_STREAM_HANDLE hwnet_get_alarm_stream(USER_HANDLE handle,alarm_stream_fun* fun,long userdata);


/*关闭报警信息
 * handle:					hwnet_login()返回的句柄
 * return:					1:成功	0:返回
 */
BOOL hwnet_close_alarm_stream(ALARM_STREAM_HANDLE handle);


/*获取系统时间
 * handle:					hwnet_login()返回的句柄
 * systm:					返回系统时间 
 * return:					1:成功  0:返回
 */
BOOL hwnet_get_systime(USER_HANDLE handle,SYSTEMTIME* systm);


/*设置系统时间
 * handle:					hwnet_login()返回的句柄
 * systm:					系统时间 
 * return:					1:成功  0:返回
 */
BOOL hwnet_set_systime(USER_HANDLE handle,SYSTEMTIME* systm);

/*恢复出厂值
 * handle:					hwnet_login()返回的句柄
 * return:					1:成功  0:返回
 */
BOOL hwnet_factory_default(USER_HANDLE handle);

/*
 * 获取串口设置
 * handle:					hwnet_login()返回的句柄
 * cfg
 *	--rs232_no				串口号(0开始),由应用层设置,下面的由接口返回
 * 	--rate				 	0-50, 1-75, 2-110, 3-150, 4-300, 5-600, 6-1200, 7-2400, 8-4800, 9-9600, 10-19200
							11-38400, 12-57600, 13-76800, 14-115200 
 *	--date_bit				0-5bit 1-6bit 2-7bit 3-8bit
 *	--stop_bit				0-1bit 1-2bit
 *	--parity				0-无校验 1-奇校验  2-偶校验
 *	--flow_control			0-无 1-软流控 2-硬流控
 *	--work_mode				0-云台 1-报警接收 2-透明通道
 *	--annunciator_type  	保留
 *	--reserved[32]      	保留
 * return:					1:成功  0:返回
 */
BOOL hwnet_get_rs232(USER_HANDLE handle, rs232_cfg_t *cfg);

/*
 * 串口设置
 * handle:					hwnet_login()返回的句柄
 * cfg
 *	--rs232_no				串口号(0开始)
 *	--rate				 	0-50, 1-75, 2-110, 3-150, 4-300, 5-600, 6-1200, 7-2400, 8-4800, 9-9600, 10-19200
							11-38400, 12-57600, 13-76800, 14-115200 
 *	--date_bit				0-5bit 1-6bit 2-7bit 3-8bit
 *	--stop_bit				0-1bit 1-2bit
 *  --parity				0-无校验 1-奇校验  2-偶校验
 *	--flow_control			0-无 1-软流控 2-硬流控
 *	--work_mode				0-云台 1-报警接收 2-透明通道
 *	--annunciator_type  	保留
 *	--reserved[32]      	保留
 * return:					1:成功  0:返回
 */
BOOL hwnet_set_rs232(USER_HANDLE handle,rs232_cfg_t *cfg);

/*
 * 获取ptz
 * handle:					hwnet_login()返回的句柄
 * cfg
 *	--slot					通道号(从0开始，由应用层设置,其他接口返回)
 *	--rs232_no				串口号(从0开始)
 *	--rate					0-50, 1-75, 2-110, 3-150, 4-300, 5-600, 6-1200, 7-2400, 8-4800, 9-9600, 10-19200
							11-38400, 12-57600, 13-76800, 14-115200 
 *	--date_bit				0-5bit 1-6bit 2-7bit 3-8bit
 *	--stop_bit				0-1bit 1-2bit
 *	--parity				0-无校验 1-奇校验  2-偶校验
 *	--flow_control			0-无 1-软流控 2-硬流控
 *	--protocol				0-PELCO_D 1-PELCO_P 2-ALEC 3-YAAN
 *	--address				地址
 *	--reserve[32]			保留
 * return:					1:成功  0:返回
 */
BOOL hwnet_get_ptz(USER_HANDLE handle, ptz_cfg_t *cfg);

/*
 * 设置ptz
 * handle:					hwnet_login()返回的句柄
 * cfg
 *	--slot					通道号(从0开始)
 *	--rs232_no				串口号(从0开始)
 *	--rate					0-50, 1-75, 2-110, 3-150, 4-300, 5-600, 6-1200, 7-2400, 8-4800, 9-9600, 10-19200
							11-38400, 12-57600, 13-76800, 14-115200 
 *	--date_bit				0-5bit 1-6bit 2-7bit 3-8bit
 *	--stop_bit				0-1bit 1-2bit
 *	--parity				0-无校验 1-奇校验  2-偶校验
 *	--flow_control			0-无 1-软流控 2-硬流控
 *	--protocol				0-PELCO_D 1-PELCO_P 2-ALEC 3-YAAN
 *	--address				地址
 *	--reserve[32]			保留

 * return:					1:成功  0:返回
 */
BOOL hwnet_set_ptz(USER_HANDLE handle, ptz_cfg_t *cfg);

/*
 * 控制ptz
 * handle:					hwnet_login()返回的句柄
 * ctrl
 * 	--slot					通道号(从0开始)
 * 	--control				0-方向	1-镜头  2-点击放大	3-预置点
 * 	--cmd					当control==0
 * 								7-左上
 * 								8-上
 * 								9-右上
 * 								4-左
 * 								5-停止
 * 								6-右
 * 								1-坐下
 * 								2-下
 * 								3-右下
 * 							当control==1
 * 								1-iris open
 * 								2-iris close
 * 								3-len tele
 * 								4-len widt
 * 								5-focus far
 * 								6-focus near
 * 								7-stop
 * 							当control== 2
 * 								1-点击放大
 * 							当control== 3
 * 								1-预置点设置
 * 								2-预置点删除
 * 								3-执行预置点
 *	--value					当control==1表示速度(0-64),当control==3表示预置点号
 *	--rect					只在control==2时有效，表明点击放大的坐标，范围相对于(704 * 576)
 * return:					1:成功  0:返回
 */
BOOL hwnet_ptz_ctrl(USER_HANDLE handle, ptz_ctrl_t *ctrl);

/*
 * 获取编码参数
 * handle:					hwnet_login()返回的句柄
 * slot:					通道号(从0开始)
 * stream:					流序号(从0开始)
 * encode_video:			返回视频编码参数，如果为NULL,不返回
 * video_arg:				返回视频编码特有信息,由用户层保证数据合法(用户一般传4096的数据即可),如果encode_video不为NULL,该值不允许为NULL
 * encode_audio:			返回音频编码参数，如果为NULL,不返回
 * audio_arg:				当前该值无效
 * return:					1:成功 0:失败
 * example:
 * 	//只获取视频编码参数
 * 	encode_video_t video_info;
 * 	char video_arg[4096];
 * 	if(hwnet_encode_get(user_handle,slot,stream,&video_info,video_arg,NULL,NULL))
 * 	{
 * 		if(video_info.type == 0)
 * 		{
 * 			//h264
 * 			h264_t* h264_info = (h264_t*)video_arg;
 * 		}
 * 		else if(video_info.type == 1)
 * 		{
 * 			//mjpeg
 * 			mjpeg_t* mjpeg_info = (mjpeg_t*)video_arg;
 * 		}
 * 	}
 *
 *	//只获取音频编码参数
 *	encode_audio_t audio_info;
 *	if(hwnet_encode_get(user_handle,slot,stream,NULL,NULL,&audio_info,NULL))
 *	{
 *		//process audio
 *	}
 *
 *	//获取视频和音频编码参数
 *	if(hwnet_encode_get(user_handle,slot,stream,&video_info,video_arg,&audio_info,NULL))
 *	{
 *		//process video && audio
 *	}
 */
BOOL hwnet_encode_get(USER_HANDLE handle,
					int slot,
					int stream,
					encode_video_t* encode_vide,
					void* video_arg,
					encode_audio_t* encode_audio,
					void* audio_arg);

/*
 * 设置编码参数
 * handle:					hwnet_login()返回的句柄
 * slot:					通道号(从0开始)
 * stream:					流序号(从0开始)
 * encode_video:			设置视频编码参数，如果为NULL,不设置 
 * video_arg:				设置视频编码特有信息,由用户层保证数据合法(用户一般传4096的数据即可),如果encode_video不为NULL,该值不允许为NULL
 * encode_audio:			设置音频编码参数，如果为NULL,不返回
 * audio_arg:				当前该值无效
 * return:					1:成功 0:失败
 * example:					参照hwnet_encode_get()函数
 */
BOOL hwnet_encode_set(USER_HANDLE handle,
				int slot,
				int stream,
				encode_video_t* encode_video,
				void* video_arg,
				encode_audio_t* encode_audio,
				void * audio_arg);


/*
 * 手动启动录像
 * handle:					hwnet_login()返回的句柄
 * slot:					通道号(从0开始)
 * return:					1:成功 0:失败
 */
BOOL hwnet_start_record(USER_HANDLE handle,int slot);


/*
 * 手动停止录像
 * handle:					hwnet_login()返回的句柄
 * slot:					通道号(从0开始)
 * return:					1:成功 0:失败
 */
BOOL hwnet_stop_record(USER_HANDLE handle,int slot);


typedef void voice_stream_fun(VOICE_STREAM_HANDLE handle,const char* buf,int len,long userdata);
/*
 * 启动语言对讲
 * handle:					hwnet_login()返回的句柄
 * slot:                    通道号
 * voice_type:              0:客户端到设备 1:双向
 * fun:                     音频回调
 * userdata:                回调用户数据
 * return:                  有效句柄
 */
VOICE_STREAM_HANDLE hwnet_voice_start(USER_HANDLE handle,int slot,int voice_type,voice_stream_fun* fun,long userdata);


/*
 * 发送语言包给客户端
 * vh:                      hwnet_start_voice()返回的句柄
 * buf:                     原始pcm数据，必须为8K,16bit,1chn
 * len:                     数据长度
 * return:                  1:成功 0:失败
 */
BOOL hwnet_voice_input(VOICE_STREAM_HANDLE handle,const char* buf,int len);


/*
 * 停止语言对讲
 * vh:                      hwnet_start_voice()返回的句柄
 * return:                  1:成功 0:失败
 */
BOOL hwnet_voice_stop(VOICE_STREAM_HANDLE handle);

/**
 * 远程抓拍图片
 * handle:					hwnet_login()返回的句柄
 * slot:                    通道号
 * stream:                  0:主码流 1:子码流 (当前只支持子码流) 
 * quality:                 图像质量,0-100
 * path:                    保存路径
 * return:                  1:成功 0:失败
 */
BOOL hwnet_save_to_jpg(USER_HANDLE handle,int slot,int stream,int quality,const char* path);


/**
 * 启动录像(扩展)
 * handle:					hwnet_login()返回的句柄
 * slot:                    通道号
 * stream:                  0:主码流 1:子码流  
 * return:                  1:成功 0:失败
 */
BOOL hwnet_start_record_ex(USER_HANDLE handle,int slot,int stream);


/**
 * 停止录像(扩展)
 * handle:					hwnet_login()返回的句柄
 * slot:                    通道号
 * stream:                  0:主码流 1:子码流  
 * return:                  1:成功 0:失败
 */
BOOL hwnet_stop_record_ex(USER_HANDLE handle,int slot,int stream);


/**
 * 设置录像覆盖模式
 * handle:					hwnet_login()返回的句柄
 * type:                    0:不覆盖,1:循环覆盖
 * return:                  1:成功 0:失败
 */
BOOL hwnet_set_record_type(USER_HANDLE handle,int slot,int stream,int type);


/**
 * 设置翻转模式
 * handle:					hwnet_login()返回的句柄
 * enable:                  0:不翻转,1:翻转
 * return:                  1:成功 0:失败
 */
BOOL hwnet_enable_flip(USER_HANDLE handle,int slot,int enable);


/**
 * 获取翻转模式
 * handle:					hwnet_login()返回的句柄
 * is_flip:                 0:不翻转,1:翻转
 * return:                  1:成功 0:失败
 */
BOOL hwnet_get_flip(USER_HANDLE handle,int slot,int* is_flip);


/**
 * 获取黑白模式设置
 * handle:					hwnet_login()返回的句柄
 * bw:                      黑白模式设置
 *  ----control_mode        0-手动  1-自动
 *  ----sense               1-3 在自动模式下起作用
 *  ----blackwhite          0-彩色 1-黑白 在手动模式下起作用
 *  ----slot                通道号
 *  ----reserve             保留

 * return:                  1:成功 0:失败
 */
BOOL hwnet_get_blackwhite(USER_HANDLE handle,net_blackwhite_t* bw);

/**
 * 设置黑白模式设置
 * handle:					hwnet_login()返回的句柄
 * bw:                      黑白模式设置
 *  ----control_mode        0-手动  1-自动
 *  ----sense               1-3 在自动模式下起作用
 *  ----blackwhite          0-彩色 1-黑白 在手动模式下起作用
 *  ----slot                通道号
 *  ----reserve             保留
 * return:                  1:成功 0:失败
 */
BOOL hwnet_set_blackwhite(USER_HANDLE handle,net_blackwhite_t* bw);


/**
 * 获取gpio状态
 * handle:					hwnet_login()返回的句柄
 * gpio:                    gpio号
 * value:                   返回gpio状态 1:高电位 0:低电位
 * return:                  1:成功 0:失败
 */
BOOL hwnet_get_gpio(USER_HANDLE handle,int gpio,int* value);


/*
 * 设置gpio
 * gpio:                    gpio号
 * value:                   gpio状态 1:高电位 0:低电位
 * return:                  1:成功 0:失败
 */
BOOL hwnet_set_gpio(USER_HANDLE handle,int gpio,int value);


/*
 * 获取报警状态
 * alarmin:                 alarm 号
 * value:                   0:定时 1:手动布防 2:手动撤防
 * return:                  1:成功 0:失败
 */
BOOL hwnet_get_alarm_guard(USER_HANDLE handle,int alarmin,int* value);


/*
 * 设置报警状态
 * alarmin:                 alarm 号
 * value:                   0:定时 1:手动布防 2:手动撤防
 * return:                  1:成功 0:失败
 */
BOOL hwnet_set_alarm_guard(USER_HANDLE handle,int alarmin,int value);


/*
 * 获取自定义osd
 * return:                  1:成功 0:失败
 */
BOOL hwnet_get_custom_osd(USER_HANDLE handle,net_custom_osd_name_t* custom_osd);

/*
 * 设置自定义osd
 * return:                  1:成功 0:失败
 */
BOOL hwnet_set_custom_osd(USER_HANDLE handle,net_custom_osd_name_t* custom_osd);


/**
 * dvo pos机接口
 */
BOOL hwnet_dvo_get_osd_set(USER_HANDLE handle,net_dvo_custom_osd_set_t* dvo_osd_set);
BOOL hwnet_dvo_set_osd_set(USER_HANDLE handle,net_dvo_custom_osd_set_t* dvo_osd_set);
BOOL hwnet_dvo_get_osd_row(USER_HANDLE handle,net_dvo_custom_osd_row_t* dvo_osd_row);
BOOL hwnet_dvo_set_osd_row(USER_HANDLE handle,net_dvo_custom_osd_row_t* dvo_osd_row);


/**
 *获取nvr通道(扩展)
 */
BOOL hwnet_nvr_get_channel_set(USER_HANDLE handle,net_nvr_channel_set_t* channel_set);

/**
 * 获取/设置ipc slow shutter等参数
 */
BOOL hwnet_ipc_get_misc(USER_HANDLE handle,net_ipcam_misc_t* ipcam_misc);
BOOL hwnet_ipc_set_misc(USER_HANDLE handle,net_ipcam_misc_t* ipcam_misc);

/**
 * 获取ipc 实时参数
 */
BOOL hwnet_ipc_get_live_ae_info(USER_HANDLE handle,net_live_ipc_ae_info_t* ae_info);
BOOL hwnet_ipc_set_live_ae_info(USER_HANDLE handle,net_live_ipc_ae_info_t* ae_info);


/**
 * 获取实时黑白状态
 */
BOOL hwnet_ipc_get_black_white_status(USER_HANDLE handle,int slot,net_black_white_status_t* bw_status);
BOOL hwnet_ipc_manual_black_white_status(USER_HANDLE handle,int slot,net_black_white_status_t* bw_status);


/**
 * 获取RFID
 */
BOOL hwnet_ipc_get_rfid_info(USER_HANDLE handle,net_rfid_info_t* rfid);
BOOL hwnet_ipc_set_rfid_info(USER_HANDLE handle,net_rfid_info_t* rfid);


/**
 * 获取YUV
 */
BOOL hwnet_get_remote_yuv(USER_HANDLE handle,net_capture_yuv_req_t* req,char* buf,int buf_len,net_capture_yuv_response_t* yuv_info);


/**
 * 获取JPEG
 */
BOOL hwnet_get_jpg_buf(USER_HANDLE handle,net_capture_jpg_t* req,char* jpg_buf,int buf_len,int* jpg_len);

/*
 * 获取错误值
 */
int hwnet_get_last_err();

#ifdef __cplusplus
#if	__cplusplus
}
#endif
#endif

#endif
