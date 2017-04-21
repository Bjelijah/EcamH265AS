/*
 * transmgr.h
 *
 *  Created on: 2016年7月8日
 *      Author: cbj
 */

#ifndef TRANSMGR_H_
#define TRANSMGR_H_

#ifdef __cplusplus
#if	__cplusplus
extern "C"{
#endif
#endif


typedef int on_connect(const char* session_id);
typedef int on_ack_res(int msgCommand,void * res,int len);
typedef int on_data_fun(int type,const char *data,int len);
typedef int on_socket_error();
/**
 * 初始化
 */
int trans_init(on_connect *c_fun,on_ack_res* a_fun,on_data_fun* d_fun,on_socket_error* e_fun);

int trans_set_no_use_ssl(bool is_no_use);


int trans_set_crt(const char *ca_crt,const char *client_crt,const char * client_key);

int trans_set_crt_path(const char* ca_path,const char* client_path,const char *key_path);

int trans_deInit();

int trans_connect(int type,const char *deviceId,const char *username,const char *password,const char *ip,int port);

int trans_disconnect();

int trans_subscribe(const char *jsonStr,int jsonLen);

int trans_unsubscribe();

int trans_getCamrea(const char *jsonStr,int jsonLen);

int trans_getRecordFiles(const char *jsonStr,int jsonLen);

int trans_Ptz(const char *jsonStr,int jsonLen);

int trans_log_test();

#ifdef __cplusplus
#if	__cplusplus
}
#endif
#endif



#endif /* TRANSMGR_H_ */
