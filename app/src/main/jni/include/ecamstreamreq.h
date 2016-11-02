#ifndef _ECAMERA_STREAM_REQUEST_C_H
#define _ECAMERA_STREAM_REQUEST_C_H

#include <stdint.h>
#include <time.h>

#include "ice.h"

#ifdef __cplusplus
extern "C"
{
#endif

/**
 * @file ecamstreamreq.h
 * @desc 创建ecam_stream_req对象，负责向请求设备媒体数据
 * 	选择各种方法(STREAM_REQ_METHOD)获取媒体数据，通过StreamArrive回调得到数据
 *
 * 	调用方法:
 * 	新建invite
 * 		1. stream_req_new()
 * 		2. fill the stream_req_context data structure
 * 		3. use stream_req_prepare_sdp() to make a local sdp
 * 		4. send "invite" soap command to the Server with local sdp
 * 		5. invoke stream_req_handle_remote_sdp() to handle the remote sdp if camera gives the right response
 * 		6. after invite, invoke stream_req_start() to request the stream via the UDP or ICE method
 * 		7. send "bye" soap command to server for leaving the stream
 * 		8. invoke stream_req_free() to free the resource
 *
 * 	如果是playback的拖动进度条,不需要重新建立udp或者ice通信，沿用之前的通信方式
 * 		1. invoke stream_req_stop() to stop the stream (directly to the camera), don't send "bye" to server
 * 		2. set stream_req_context.re_invite = 1
 * 		3. send "invite" soap command to server with last dialog_id, which MUST be "playback" session
 * 		4. 接下去跟上述5一致
 *
 */

struct ecam_stream_req;
typedef struct ecam_stream_req ecam_stream_req_t;

typedef enum {
	kFrameTypeP=0,
	kFrameTypeI,
	kFrameTypeAudio,
} ECAM_STREAM_REQ_FRAME_TYPE;

typedef enum {
	kStreamReqMethodUnknown=0,
	kStreamReqMethodUdp,
	kStreamReqMethodIce,
} ECAM_STREAM_REQ_METHOD;


typedef void (* PacketArrive)(ecam_stream_req_t * req, const char * data, size_t len);
typedef void (* StreamArrive)(ecam_stream_req_t * req, ECAM_STREAM_REQ_FRAME_TYPE media_type, const char * data, size_t len, uint32_t timestamp);
typedef void (* MethodChange)(ecam_stream_req_t * req, ECAM_STREAM_REQ_METHOD method);

/**
 * @desc 创建一个stream_request对象
 * @param use_inner_timer: 使用内置定时器消费数据，即StreamArrive回调得到的数据按照数据的timestamp做好定时和音画同步，
 * 	若为0，则一有数据上报回调，由用户自行负责数据处理
 * @param cb: 回调处理列表
 * @return 成功则返回创建对象，失败返回NULL
 */
ecam_stream_req_t * ecam_stream_req_new(const char * account);


/**
 * 注册回调
 */
void ecam_stream_req_regist_packet_cb(ecam_stream_req_t * req,PacketArrive cb);
void ecam_stream_req_regist_stream_cb(ecam_stream_req_t * req,StreamArrive cb);
void ecam_stream_req_regist_method_cb(ecam_stream_req_t * req,MethodChange cb);

/**
 * @desc 销毁对象
 */
void ecam_stream_req_free(ecam_stream_req_t * req);

/**
 * @desc 设置/获取关联对象的用户数据
 */
void ecam_stream_req_set_usr_data(ecam_stream_req_t * req, void * usr_data);
void * ecam_stream_req_get_usr_data(ecam_stream_req_t * req);

/* 请求上下文 */
struct ecam_stream_req_context
{
    int playback;
    time_t beg,end;

    /* 是否是重新invite，一般用于在playback时拖拉进度条，
     * 此时保持之前的连接方式,不需要重新udp认证或者ICE */
    int re_invite;

    uint8_t method_map;	/* 获取视频的方法 */

    char udp_addr[64];
    uint16_t udp_port;
    struct ICEOption ice_opt;

    // crypto
    struct {
        int enable;// 是否启用加密，0:disable 1:enable
    } crypto;

    // channel stream, 支持在线切换视频
    int channel;
    int stream;
};

/**
 * @desc 启动媒体数据请求
 * @param req: 对象
 * @param context: 请求上下文，见struct stream_req_context
 * @param timeout_ms: 超时时间，若timeout_ms时间内未收到数据，则返回失败,0表示不超时
 * @return 0:ok -1:错误 -2:超时错误
 */
int ecam_stream_req_start(ecam_stream_req_t * req, struct ecam_stream_req_context * context, int timeout_ms);

/**
 * @desc 停止接收数据
 * @param req: 对象
 * @param timeout_ms: 超时时间，若timeout_ms时间内未收到数据，则返回失败,0表示不超时
 * @return 0:ok -1:错误 -2:超时错误
 */
int ecam_stream_req_stop(ecam_stream_req_t * req, int timeout_ms);

/**
 * @desc 生成sdp
 * @param req: 对象
 * @param context: 请求上下文，见struct stream_req_context
 * @return 需要free
 */
char * ecam_stream_req_prepare_sdp(ecam_stream_req_t * req, struct ecam_stream_req_context * context);

/**
 * @desc 处理远程sdp
 * @param req: 对象
 * @param context: 请求上下文，见struct stream_req_context
 * @param dialog_id: dialog_id
 * @param sdpstr: 远程sdp字符串
 */
int ecam_stream_req_handle_remote_sdp(ecam_stream_req_t * req, struct ecam_stream_req_context * context, const char * dialog_id, const char * sdpstr);

/**
 * @desc 获取method
 */
ECAM_STREAM_REQ_METHOD ecam_stream_req_get_transfer_method(ecam_stream_req_t *req);

/**
 * @desc 获取ice对象
 */
ICE_t * ecam_stream_req_get_ice(ecam_stream_req_t * req);


/**
 * @desc 在handle_remote_sdp()之后，可以通过该接口获取录像列表修正过的时间,即将预置流时间也计算在内
 */
void ecam_stream_req_get_sdp_time(ecam_stream_req_t * req,time_t *beg, time_t *end);

/**
 * @desc 发送心跳信息,保持媒体数据链路
 */
//int stream_req_keeplive(stream_req_t * req);

/**
* @desc 发送音频数据
* @param rtp_payload: g.711为0
*/
int ecam_stream_send_audio(ecam_stream_req_t * req, int rtp_payload, const char * data, size_t len, uint32_t timestamp);

/**
 * @desc 获取远程的rtp数据，可用于设置本地的解码器
 * @param req: 请求对象
 * @param desc: 媒体描述，实际是rtpmap的信息
 * @param payload: rtp payload

 * @return -1: failed 0:sdp中不存在该媒体信息 1：存在该媒体信息
 */
int ecam_stream_req_get_video(ecam_stream_req_t *req, char *desc, int *payload);
int ecam_stream_req_get_audio(ecam_stream_req_t *req, char *desc, int *payload);

#ifdef __cplusplus
}
#endif

#endif
