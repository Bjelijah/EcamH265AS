#ifndef _ICE_H
#define _ICE_H

/**
 * 使用pjnath实现ICE功能
 */

#include <stdio.h>
#include <stdlib.h>
#include <osipparser2/sdp_message.h>

#ifdef __cplusplus
extern "C"
{
#endif

struct ICE;
typedef struct ICE ICE_t;

struct ICEOption
{
	int 	comp_cnt;
	char 	stun_addr[32];
	int 	stun_port;
	char	turn_addr[32];
	int 	turn_port;
	int 	turn_tcp;
	char 	turn_username[64];
	char    turn_password[64];
};

typedef enum {
	kICERoleOffer,
	kICERoleAnswer,
} ICE_PLAY_ROLE;

typedef enum {
	kICETypeUnknown,
	kICETypeP2P,
	kICETypeTurn,
} ICE_TYPE;

#if 0
struct IceSessCand
{
	char foundation[32];
	unsigned comp_id;
	char transport[12];
	int prio;
	char ipaddr[80];
	int port;
	char type[32];		//host; srflx; relay
};

struct IceRemoteInfo
{
	char		ufrag[80];
	char		pwd[80];
	unsigned	comp_cnt;
	char		def_addr[PJ_ICE_MAX_COMP];
	int		def_port[PJ_ICE_MAX_COMP];
	unsigned	cand_cnt;
	struct IceSessCand cand[PJ_ICE_ST_MAX_CAND];
};
#endif

/**
 * ice环境初始化，在创建任何ice对象之前调用，只需调用一次
 * ugly的多线程使用:
 * 	如果在由非pjlib库接口创建的线程，如native api或其他库创建的线程内
 * 	调用 ice_start_nego(),需要在该线程内先执行pj_thread_register(),
 * 	将自建的线程加入到pjlib，否则pjlib有断言错误。
 * 	另外线程释放后不要忘记释放register后得到的资源.
 */
int ice_global_init(void);

typedef void (* ICE_ON_READ)(ICE_t * ice, int comp_id, const char * src_addr, int port, void * buf, size_t len);

int ice_create(struct ICEOption * option, 
		ICE_ON_READ ice_on_read,
		void * user_data,
		ICE_t **ice);

void ice_destroy(ICE_t *ice);

void * ice_get_user_data(ICE_t *ice);

int ice_init_session(ICE_t *ice, ICE_PLAY_ROLE role);

/**
 * 在原有的sdp上添加ice的信息
 */
int ice_add_sdp_info(ICE_t *ice, sdp_message_t * sdp);

/**
 * 解析并接收远程发过来的sdp,该函数只处理和ICE有关的数据，不关心具体媒体类型等
 */
int ice_apply_remote_sdp(ICE_t *ice, sdp_message_t * sdp);

/**
 * 启动ICE协商
 * @param ms: 超时时间，in ms, 0 表示非超时模式
 * @return 1:ok  0:fail  -1:error -2:timeout
 */
int ice_start_nego(ICE_t * ice, int ms);

int ice_send(ICE_t * ice, unsigned comp_id, void * buf, size_t len);

int ice_nego_result(ICE_t * ice);

//invoke after ice_start_nego()
ICE_TYPE ice_get_type(ICE_t *ice);

#if 0
/**
 * 收集本地的信息
 */
int ice_gather_info(ICE_t *ice, struct IceRemoteInfo * info);

/**
 * 接收远程信息
 */
int ice_apply_remote_info(ICE_t * ice, struct IceRemoteInfo * info);
#endif

#ifdef __cplusplus
}
#endif
#endif
