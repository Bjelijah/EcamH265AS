#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
//#include <openssl/des.h>//FIXME
//#include <openssl/md5.h>//FIXME
#include <assert.h>
#include <getopt.h>
#include <sys/time.h>

#include <netinet/in.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <arpa/inet.h>

#include <jni.h>
#include <android/log.h>
//#include "howell/net/udpsocket.h"
#include "ecamstreamreq.h"
#include<string>
//#include "demo/webservice/HomeMCUServiceBinding_USCOREIHomeMCUService.nsmap"//FIXME
#include "demo/webservice/soapH.h"//FIXME
#include "demo/webservice/soapStub.h"//FIXME
#include "demo/base64.h"

const char * ACCOUNT ="10086012";
const char * PASSWORD="10086012";
//const char * DEVID="12345678901234567890";
//const char * DEVID="36e1e07416bc4b05a12b";
// const char * DEVID="20140430000000000129";
const char * DEVID="P0511HQ5V00100000000";
// const char * DEVID="a7b56fafeb7a4e36b80e";
//const char * DEVID="N1202BY0030000000000";
const char * SERVER="http://www.haoweis.com:8800/HomeService/HomeMCUService.svc";
//const char * SERVER="http://5.196.6.27:8800/HomeService/HomeMCUService.svc";

#define LOGI(...) ((void)__android_log_print(ANDROID_LOG_INFO, "main_cc_jni", __VA_ARGS__))
#define LOGW(...) ((void)__android_log_print(ANDROID_LOG_WARN, "main_cc_jni", __VA_ARGS__))
#define LOGE(...) ((void)__android_log_print(ANDROID_LOG_ERROR, "main_cc_jni", __VA_ARGS__))



#define USE_TURN 1
const char * TURN_SERVER="42.96.151.28";
//const char * TURN_SERVER="180.166.7.214";

const char * UPNP_ADDR="192.168.128.37";
int UPNP_PORT=6002;

static void to_hex_format_string(char * str, unsigned char * hex, int cnt)
{
	char tmp[3];
	int i;
	for (i=0; i<cnt; ++i) {
		sprintf(tmp,"%02x",hex[i]);
		strcat(str,tmp);
	}
}

class FrameStatistics
{
public:
	FrameStatistics(std::string name, int timeout=5*1000) {
		timeout_ = timeout;
		total_cnt_ = 0;
		start_ts_ = 0;
		name_ = name;
	}
	~FrameStatistics() {

	}

	void inputFrame() {
		total_cnt_ += 1;

		if (start_ts_ == 0) {
			start_ts_ = clock();
		}
		else {
			uint32_t diff = clock() - start_ts_;
			// printf("%u clock:%u\n", diff, clock());

			if (diff > (uint32_t)timeout_) {
				if (total_cnt_ == 0) {
					// printf("avg:0 kbps\n");
				}
				else {
					printf("[%s %ld] timediff:%d frame_cnt:%d, avg:%.2f fps\n", name_.c_str(), time(NULL), diff, total_cnt_, total_cnt_/(diff/1000.0));
				}

				total_cnt_ = 0;
				start_ts_ = 0;
			}
		}
	}

	static uint32_t clock() {
		struct timeval time;
		gettimeofday(&time, NULL);
		uint64_t value = ((uint64_t)time.tv_sec) * 1000 + (time.tv_usec / 1000);
		return (uint32_t)(value & 0xfffffffful);
	}

private:
	int timeout_;
	int total_cnt_;
	uint32_t start_ts_;
	std::string name_;
};

class StreamStatistics
{
public:
	StreamStatistics(std::string name, int timeout=10*1000) {
		timeout_ = timeout;
		total_len_ = 0;
		start_ts_ = 0;
		name_ = name;
	}

	~StreamStatistics() {
	}

	void inputData(int len) {
		total_len_ += len;

		if (start_ts_ == 0) {
			start_ts_ = clock();
		}
		else {
			uint32_t diff = clock() - start_ts_;
			if (diff > (uint32_t)timeout_) {
				if (total_len_ == 0) {
					// printf("avg:0 kbps\n");
				}
				else {
					printf("[%s %ld] timediff:%d data_len:%d, avg:%.2f kbps\n", name_.c_str(), time(NULL), diff, total_len_, total_len_*8/1024.0/(diff/1000.0));
				}

				total_len_ = 0;
				start_ts_ = 0;
			}
		}
	}

	static uint32_t clock() {
		struct timeval time;
		gettimeofday(&time, NULL);
		uint64_t value = ((uint64_t)time.tv_sec) * 1000 + (time.tv_usec / 1000);
		return (uint32_t)(value & 0xfffffffful);
	}

private:
	int timeout_;
	int total_len_;
	uint32_t start_ts_;
	std::string name_;
};


class ClientDemo
{
public:
	const static int REQ_TIMEOUT=10000;

	ClientDemo(const char * account, const char * passwd) {
		if (account!=NULL) account_ = std::string(account);
		if (passwd!=NULL) passwd_ = std::string(passwd);
		//udp_sender_ = new howell_net::UdpSocket(0,"127.0.0.1",17000);
		package_s_ = new StreamStatistics("packet");
		stream_s_ = new StreamStatistics("stream");
		frame_s_ = new FrameStatistics("frame");
	}

	~ClientDemo() {
		delete package_s_;
		delete stream_s_;
		delete frame_s_;
	}

	bool Login() {

		int result = 0;

		struct soap * soap = soap_new();
		struct _ns1__userLoginReq req;
		struct _ns1__userLoginRes res;
		memset(&req,0,sizeof(req));

		char password[256];
		memset(password,0,sizeof(password));
		req.Account = (char *)account_.c_str();
		req.PwdType = ns1__PwdType__Common;
		passwordEncrypt((const unsigned char *)passwd_.c_str(),(unsigned char *)password,256);
		req.Password = password;

		fprintf(stderr, "crypto password:%s\n",password);
		soap_call___ns1__userLogin(soap,SERVER,NULL,&req,&res);
		if (soap->error) {
			printf("soap error:%d,%s,%s\n", soap->error, *soap_faultcode(soap), *soap_faultstring(soap) );
			result = soap->error;
			return false;
		}

		fprintf(stderr,"login session:%s\n",res.LoginSession);
		login_session_ = std::string(res.LoginSession);

		soap_end(soap);
		soap_delete(soap,NULL);
		return true;
	}

	bool InviteLive(const char * devid,uint8_t method_bitmap, const char * udp_addr, int udp_port) {
		stream_req_  =  ecam_stream_req_new(account_.c_str());
		ecam_stream_req_set_usr_data(stream_req_,(void *)this);
		ecam_stream_req_regist_packet_cb(stream_req_,OnPacketArrive);
		ecam_stream_req_regist_stream_cb(stream_req_,OnStreamArrive);

		struct ecam_stream_req_context c;
		memset(&c,0,sizeof(c));

		c.playback = 0;
		c.beg = c.end = 0;
		c.re_invite = false;
		c.method_map = method_bitmap;
		c.crypto.enable = 0;
		c.channel = 0;
		c.stream = 1;
		if (udp_addr != NULL) {
			strncpy(c.udp_addr,udp_addr,63);
		}
		c.udp_port = udp_port;
		buildIceOpt(c.ice_opt);

		if (invite(devid,&c)!=0) {
			fprintf(stderr,"invite error\n");
			exit(-1);
		}

		int ret = ecam_stream_req_start(stream_req_,&c,REQ_TIMEOUT);
		fprintf(stderr,"stream req start ret:%d\n",ret);
		return ret==0?true:false;
	}

	bool InvitePlayback(const char * devid,uint8_t method_bitmap, const char * udp_addr, int udp_port) {
		stream_req_  =  ecam_stream_req_new(account_.c_str());
		ecam_stream_req_set_usr_data(stream_req_,(void *)this);
		//ecam_stream_req_regist_packet_cb(stream_req_,OnPacketArrive);
		ecam_stream_req_regist_stream_cb(stream_req_,OnStreamArrive);

		struct ecam_stream_req_context c;
		memset(&c,0,sizeof(c));

		c.playback = 1;
		c.beg = /*1372050203; */ 1453252583;
		c.end = /*1372050335; */ 1453252616;
		c.re_invite = false;
		c.channel = 0;
		c.stream = 1;
		c.crypto.enable = 1;
		c.method_map = method_bitmap;
		if (udp_addr != NULL) {
			strncpy(c.udp_addr,udp_addr,63);
		}
		c.udp_port = udp_port;
		buildIceOpt(c.ice_opt);

		if (invite(devid,&c)!=0) {
			fprintf(stderr,"invite error\n");
			exit(-1);
		}

		printf("stream request start %ld\n",time(NULL));
		int ret = ecam_stream_req_start(stream_req_,&c,REQ_TIMEOUT);
		printf("stream request end %ld\n",time(NULL));
		fprintf(stderr,"stream req start ret:%d\n",ret);
		return ret==0?true:false;
	}

	// 重新请求，用于回放拖拉进度条
	bool Reinvite() {
		if (stream_req_ == NULL) {
			return false;
		}

		ecam_stream_req_stop(stream_req_, 0);

		struct ecam_stream_req_context c;
		memset(&c,0,sizeof(c));

		c.playback = 1;
		c.beg = /*1372050203; */ 1453252583+5;
		c.end = /*1372050335; */ 1453252616;
		c.re_invite = true;
		c.channel = 0;
		c.stream = 1;
		c.crypto.enable = 1;

		int ret = ecam_stream_req_start(stream_req_,&c,REQ_TIMEOUT);
		fprintf(stderr,"stream req start [reinvite] ret:%d\n",ret);
		return ret==0?true:false;
	}

	bool Bye(const char * devid) {

		if (stream_req_!=NULL) {
			ecam_stream_req_free(stream_req_);
			stream_req_ = NULL;
		}

		// //return true;
		// fprintf(stderr,"before bye\n");
		// ecam_stream_req_stop(stream_req_,5000);
		// fprintf(stderr,"after \n");
		//return true;

		struct soap * soap;

		soap = soap_new();

		struct _ns1__byeReq req;
		struct _ns1__byeRes res;
		memset(&req,0,sizeof(req));
		memset(&res,0,sizeof(res));

		req.Account = (char *)account_.c_str();
		req.LoginSession = (char *)login_session_.c_str();
		req.DevID = (char *)devid;
		req.ChannelNo = 0;
		req.StreamType = ns1__StreamType__Sub;
		req.DialogID = (char *)dialog_id_.c_str();

		soap_call___ns1__bye(soap,SERVER,NULL,&req,&res);
		if (soap->error) {
			fprintf(stderr,"soap error:%d,%s,%s\n", soap->error, *soap_faultcode(soap), *soap_faultstring(soap) );
		}
		else
			fprintf(stderr,"bye ret:%d\n",res.result);
		soap_end(soap);
		soap_delete(soap,NULL);



		return true;
	}


	void VodList(const char * devid) {
		struct soap * soap = soap_new();

		struct _ns1__vodSearchReq req;
		struct _ns1__vodSearchRes res;
		memset(&req,0,sizeof(req));
		memset(&res,0,sizeof(res));

		req.Account = (char *)account_.c_str();
		req.LoginSession = (char *)login_session_.c_str();
		req.DevID = (char *)devid;
		req.ChannelNo = 0;
		req.StreamType = ns1__StreamType__Sub;
		req.StartTime = 0;
		req.EndTime = time(NULL);
		int pageno=1;
		req.PageNo = &pageno;

		soap_call___ns1__vodSearch(soap,SERVER,NULL,&req,&res);
		if (soap->error) {
			fprintf(stderr,"soap error:%d,%s,%s\n", soap->error, *soap_faultcode(soap), *soap_faultstring(soap) );
		}
		else
			fprintf(stderr,"vodlist ret:%d\n",res.result);
		soap_end(soap);
		soap_delete(soap,NULL);
	}

	void Reboot(const char *devid) {
		struct soap *soap = soap_new();
		struct _ns1__rebootReq req;
		struct _ns1__rebootRes res;
		memset(&req,0,sizeof(req));
		memset(&res,0,sizeof(res));

		req.Account = (char *)account_.c_str();
		req.LoginSession = (char *)login_session_.c_str();
		req.DevID = (char *)devid;

		soap_call___ns1__reboot(soap,SERVER,NULL,&req,&res);
		if (soap->error) {
			fprintf(stderr,"soap error:%d,%s,%s\n", soap->error, *soap_faultcode(soap), *soap_faultstring(soap) );
		}
		else {
			fprintf(stderr,"reboot ret:%d\n",res.result);
		}

		soap_end(soap);
		soap_delete(soap,NULL);
	}

private:
	static void OnPacketArrive(ecam_stream_req_t * req, const char * data, size_t len) {
		// fprintf(stderr,">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> %s len:%d\n",__func__,len);
		ClientDemo * self=(ClientDemo *)ecam_stream_req_get_usr_data(req);
		// self->package_s_->inputData(len);
		//if (self->udp_sender_!=NULL) self->udp_sender_->send(data,len);
	}

	static void OnStreamArrive(ecam_stream_req_t * req, ECAM_STREAM_REQ_FRAME_TYPE media_type, const char * data, size_t len, uint32_t timestamp) {

		ClientDemo * self=(ClientDemo *)ecam_stream_req_get_usr_data(req);
		// if (media_type == kFrameTypeAudio) return;

		//static uint32_t last_ts=0;
		//int diff = timestamp-last_ts;
		//if (abs(diff)>4000 || abs(diff)<3000) {
		//printf("type:%d len:%d diff:%d\n",media_type,len,diff);
		//}
		//exit(0);
		// printf(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> %ld type:%d len:%d ts:%d\n",time(NULL),media_type,len, timestamp);
		// self->stream_s_->inputData(len);
		if (media_type != kFrameTypeAudio) {
			self->frame_s_->inputFrame();
		}
		// if (media_type == kFrameTypeI) {
		//   printf("%ld type:%d len:%d ts:%u\n",time(NULL),media_type,len,timestamp);
		//   // exit(0);
		// }

#if 0
		// send audio test
		for (int i=0; i<10; i++) {
			char buf[64];
			sprintf(buf,"%d",i);
			ecam_stream_send_audio(req, 0, buf, strlen(buf), 0);
		}
#endif

		//sleep(1);
		//last_ts = timestamp;
		// return;

		// static int cnt=0;
		// if (media_type == kFrameTypeI || media_type == kFrameTypeP) {
		//     cnt++;
		//     if (cnt==25) {
		//         sleep(1);
		//         cnt=0;
		//     }
		// }
	}

	void buildIceOpt(struct ICEOption &opt) {
		memset(&opt,0,sizeof(opt));
		opt.comp_cnt = 1;
		//stun server
		strcpy(opt.stun_addr,TURN_SERVER);
		opt.stun_port = 34780;

#if USE_TURN>0
		//turn server
		strcpy(opt.turn_addr,TURN_SERVER);
		opt.turn_port = 34780;
		opt.turn_tcp = 0;
		strcpy(opt.turn_username,"100");
		strcpy(opt.turn_password,"100");
#endif
	}

	//jzh: from ShenLingfei
	int passwordEncrypt(const unsigned char *plaitext, unsigned char *ciphertext, int len) {
		if (!plaitext || !ciphertext || len <= 192)
		{
			return -1;
		}

		printf("plain pasword:   %s\n",plaitext);

		// 随机产生key
		// DES_cblock my_key;
		// DES_cblock my_iv;
		// DES_random_key(&my_key);
		// DES_random_key(&my_iv);
		DES_cblock my_key = {0x05, 0xf7, 0xdd, 0x03, 0xa7, 0xac, 0xe4, 0x42};
		DES_cblock my_iv = {0xbc, 0x30, 0x3b, 0xa4, 0xab, 0x99, 0x42, 0xd2};
		// 设置key
		DES_key_schedule my_schedule;
		int ret = DES_set_key(&my_key, &my_schedule);
		if (ret) {
			return -1;
		}

		// 明文
		const unsigned char *my_input = (const unsigned char*)plaitext;
		// md5
		MD5_CTX md5;
		ret = MD5_Init(&md5);
		int ret1 = MD5_Update(&md5, (const void *)my_input, strlen((const char *)my_input));
		unsigned char md5_out[17] = {0};
		int ret2 = MD5_Final(md5_out, &md5);
		unsigned char md_out[33] = {0};
		to_hex_format_string((char *)md_out, md5_out, 16);

		printf("first md5: %s\n",md_out);

		//把随机产生的key和iv转换为16进制字符串
		unsigned char key_string[17] = {0};
		to_hex_format_string((char *)key_string, my_key, 8);
		unsigned char iv_string[17] = {0};
		to_hex_format_string((char *)iv_string, my_iv, 8);

		// 第一次加密
		unsigned char output_tmp[128] = {0};
		DES_ncbc_encrypt(md_out, output_tmp, 128, &my_schedule, &my_iv, DES_ENCRYPT);
		unsigned char my_first_cipher[128*2+1] = {0};
		to_hex_format_string((char *)my_first_cipher, output_tmp, 128);
		//printf("enc1: %s\n",my_first_cipher);

		printf("first cipher: %s\n",my_first_cipher);

		//第二次加密, 数据合成
		unsigned char com[128] = {0};
		memcpy(com, key_string, 16);    // memcpy源字符数组需要多一个字节，避免运行时栈崩溃
		memcpy(&com[16], iv_string, 16);
		memcpy(&com[32], my_first_cipher, 64);
		unsigned char sec_key[8] = {0x48, 0x4F, 0x57, 0x45, 0x4C, 0x4C, 0x4B, 0x45};
		unsigned char sec_iv[8] = {0x48, 0x4F,0x57, 0x45, 0x4C, 0x4C, 0x56, 0x49};

		// 设置key
		DES_key_schedule sec_schedule;
		ret = DES_set_key(&sec_key, &sec_schedule);
		if (ret)
		{
			return -1;
		}

		// 加密
		unsigned char sec_out[97] = {0};
		DES_ncbc_encrypt(com, sec_out, 96, &sec_schedule, &sec_iv, DES_ENCRYPT);
		unsigned char final_out[193] = {0};
		to_hex_format_string((char *)final_out, sec_out, 96);
		memcpy(ciphertext, final_out, 192);

		printf("final cipher: %s\n",final_out);
		return 0;
	}

	int passwordEncrypt_old(const unsigned char *plaitext, unsigned char *ciphertext, int len) {
		if (!plaitext || !ciphertext || len <= 192)
		{
			return -1;
		}

		printf("plain pasword:   %s\n",plaitext);

		// 随机产生key
		// DES_cblock my_key;
		// DES_cblock my_iv;
		// DES_random_key(&my_key);
		// DES_random_key(&my_iv);
		DES_cblock my_key = {0x05, 0xf7, 0xdd, 0x03, 0xa7, 0xac, 0xe4, 0x42};
		DES_cblock my_iv = {0xbc, 0x30, 0x3b, 0xa4, 0xab, 0x99, 0x42, 0xd2};
		// 设置key
		DES_key_schedule my_schedule;
		int ret = DES_set_key(&my_key, &my_schedule);
		if (ret) {
			return -1;
		}

		// 明文
		const unsigned char *my_input = (const unsigned char*)plaitext;
		// md5
		MD5_CTX md5;
		ret = MD5_Init(&md5);
		int ret1 = MD5_Update(&md5, (const void *)my_input, strlen((const char *)my_input));
		unsigned char md5_out[17] = {0};
		int ret2 = MD5_Final(md5_out, &md5);
		unsigned char md_out[33] = {0};
		to_hex_format_string((char *)md_out, md5_out, 16);

		printf("first md5: %s\n",md_out);

		//把随机产生的key和iv转换为16进制字符串
		unsigned char key_string[17] = {0};
		to_hex_format_string((char *)key_string, my_key, 8);
		unsigned char iv_string[17] = {0};
		to_hex_format_string((char *)iv_string, my_iv, 8);

		// 第一次加密
		unsigned char output_tmp[33] = {0};
		DES_ncbc_encrypt(md_out, output_tmp, 32, &my_schedule, &my_iv, DES_ENCRYPT);
		unsigned char my_first_cipher[65] = {0};
		to_hex_format_string((char *)my_first_cipher, output_tmp, 32);

		printf("first cipher: %s\n",my_first_cipher);

		//第二次加密, 数据合成
		unsigned char com[128] = {0};
		memcpy(com, key_string, 16);    // memcpy源字符数组需要多一个字节，避免运行时栈崩溃
		memcpy(&com[16], iv_string, 16);
		memcpy(&com[32], my_first_cipher, 64);
		unsigned char sec_key[8] = {0x48, 0x4F, 0x57, 0x45, 0x4C, 0x4C, 0x4B, 0x45};
		unsigned char sec_iv[8] = {0x48, 0x4F,0x57, 0x45, 0x4C, 0x4C, 0x56, 0x49};

		// 设置key
		DES_key_schedule sec_schedule;
		ret = DES_set_key(&sec_key, &sec_schedule);
		if (ret)
		{
			return -1;
		}

		// 加密
		unsigned char sec_out[97] = {0};
		DES_ncbc_encrypt(com, sec_out, 96, &sec_schedule, &sec_iv, DES_ENCRYPT);
		unsigned char final_out[193] = {0};
		to_hex_format_string((char *)final_out, sec_out, 96);
		memcpy(ciphertext, final_out, 192);

		printf("final cipher: %s\n",final_out);
		return 0;
	}

	int invite(const char * devid, struct ecam_stream_req_context *c)
	{
		printf("start to invite %ld\n",time(NULL));
		struct soap * soap;
		int result = 0;

		char * local_sdp;
		size_t sdp_len;
		char * remote_sdp;

		printf("start to prepare sdp %ld\n",time(NULL));
		local_sdp = ecam_stream_req_prepare_sdp(stream_req_,c);
		if (local_sdp==NULL) return -1;
		printf("after to prepare sdp %ld\n",time(NULL));

		printf("start to send invite to server %ld\n",time(NULL));
		soap = soap_new();

		struct _ns1__inviteReq req;
		struct _ns1__inviteRes res;
		memset(&req,0,sizeof(req));
		memset(&res,0,sizeof(res));

		req.Account = (char *)account_.c_str();
		req.LoginSession = (char *)login_session_.c_str();
		req.DevID = (char *)devid;
		req.ChannelNo = 0;
		switch (1) {
		case 0: req.StreamType = ns1__StreamType__Main; break;
		case 1: req.StreamType = ns1__StreamType__Sub; break;
		default:req.StreamType = ns1__StreamType__Sub; break;
		}
		if (!c->re_invite)
			createDialogId();
		req.DialogID = (char *)dialog_id_.c_str();
		req.SDPMessage = base64_encode((const unsigned char *)local_sdp,strlen(local_sdp));

		fprintf(stderr,"ready to invite:%s\n",req.SDPMessage);
		soap_call___ns1__invite(soap,SERVER,NULL,&req,&res);
		if (soap->error) {
			printf("soap error:%d,%s,%s\n", soap->error, *soap_faultcode(soap), *soap_faultstring(soap) );
			result = soap->error;
			goto quit;
		}
		fprintf(stderr,"after invite result:%d\n",res.result);

		if (res.result!=0) {
			result=-1;
			goto quit;
		}

		printf("end to send invite to server %ld\n",time(NULL));

		remote_sdp = (char *)base64_decode(res.SDPMessage,&sdp_len);
		fprintf(stderr,"remote sdp:%s\n",remote_sdp);

		printf("start to handle sdp %ld\n",time(NULL));
		ecam_stream_req_handle_remote_sdp(stream_req_,c,dialog_id_.c_str(),remote_sdp);
		printf("end to handle sdp %ld\n",time(NULL));
		//stream_req_->ProcessRemoteSdp(c,dialog_id_.c_str(),remote_sdp);

		//media info
#if 1
		printf("\n\n----------------------- remote media info ---------------------------\n");
		{
			int has;
			char desc[32];
			int payload;
			has = ecam_stream_req_get_video(stream_req_, desc, &payload);
			if (has) {
				printf("\tvideo: desc:%s payload:%d\n",desc,payload);
			}
			has = ecam_stream_req_get_audio(stream_req_, desc, &payload);
			if (has) {
				printf("\taudio: desc:%s payload:%d\n",desc,payload);
			}
		}
		printf("---------------------------------------------------------------------\n\n");
#endif
		free(remote_sdp);

		quit:
		free(req.SDPMessage);
		soap_end(soap);
		soap_delete(soap,NULL);
		free(local_sdp);
		return result;
	}

	void createDialogId() {
		char d[32];
		snprintf(d,31,"%ld",random());
		dialog_id_ = std::string(d);
	}

	std::string account_;
	std::string passwd_;
	std::string login_session_;
	std::string dialog_id_;
	//ecamera::StreamRequest * stream_req_;
	ecam_stream_req_t * stream_req_;
	StreamStatistics * package_s_, *stream_s_;
	FrameStatistics * frame_s_;
	//howell_net::UdpSocket * udp_sender_; //jzh: 用于将数据发给VLC做测试
};

static void menu()
{
	printf("\n\t-------------MENU-------------------\n");
	printf("\t|i\tinvite the device\n");
	printf("\t|p\tinvite the device's Playback\n");
	printf("\t|b\tbye to the device\n");
	printf("\t|l\tvod list\n");
	printf("\t|r\treboot the device\n");
	printf("\t|q\tquit\n");
	printf("\t-----------------------------------\n\n");
}

int main(int argc, char * argv[])
{
	ice_global_init();
	srand(time(NULL));

	ClientDemo demo(ACCOUNT,PASSWORD);
	bool ret = demo.Login();
	assert(ret);

	while (1) {
		menu();
		char c;
		if (scanf("%c",&c) != 1) {
			continue;
		}

		switch (c) {
		case 'i':
		{
			uint8_t req_method = 0;
			req_method |= 1<<kStreamReqMethodIce;
			//req_method |= 1<<kStreamReqMethodUdp;
			ret = demo.InviteLive(DEVID,req_method,UPNP_ADDR,UPNP_PORT);
			//ret = demo.InviteLive(DEVID,req_method,NULL,0);
			assert(ret);
		}
		break;

		case 'p':
		{
			uint8_t req_method;
			//req_method = 1<<ecamera::StreamRequest::kMethodICE | 1<<ecamera::StreamRequest::kMethodUdp;
			//req_method = 1<<kStreamReqMethodUdp;
			req_method = 1<<kStreamReqMethodIce;
			ret = demo.InvitePlayback(DEVID,req_method,"192.168.128.95",6002);
			//ret = demo.InviteLive(DEVID,req_method,NULL,0);
			assert(ret);
		}
		break;

		case 'b':
			demo.Bye(DEVID);
			break;

		case 'l':
			demo.VodList(DEVID);
			break;

		case 'r':
			demo.Reboot(DEVID);
			break;

		case 'q':
			exit(0);

		case 'R':
			printf("Reinvite\n");
			demo.Reinvite();

		default:
			break;
		}
	}

	return 0;
}

void my_test(){
	LOGE("this is  from main.cc");

	uint8_t req_method = 0;
	req_method |= 1<<kStreamReqMethodIce;
	//req_method |= 1<<kStreamReqMethodUdp;
	ret = demo.InviteLive(DEVID,req_method,UPNP_ADDR,UPNP_PORT);
	//ret = demo.InviteLive(DEVID,req_method,NULL,0);
	assert(ret);

}
