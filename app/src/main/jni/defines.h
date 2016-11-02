#ifndef _HW_TRANSMISSION_DEFINES_H
#define _HW_TRANSMISSION_DEFINES_H

#include <stdint.h>

namespace hwtrans {

const uint8_t SYNC_BYTE = 0xa5;

enum {
    kHeadFlagNoAck = 1<<6,
    kHeadFlagContentType = 1<<7,
};

#define head_flag_set_no_ack(f) do { (f)|=kHeadFlagNoAck; } while(0)
#define head_flag_set_content_type_binary(f) do { (f)|=kHeadFlagContentType; } while(0)
#define head_flag_is_no_ack(f) (f)&(kHeadFlagNoAck)
#define head_flag_is_binary(f) (f)&(kHeadFlagContentType)

typedef struct {
    uint8_t sync;
    uint8_t version;
    uint8_t flag;
    uint8_t reserved;
    uint16_t command;
    uint16_t seq;
    uint32_t payload_len;
    uint32_t reserved2;
} HWTransmissionHead;

enum {
    kCmdConnect = 0x10,
    kCmdConnectAck,
    kCmdDisconnect,
    kCmdDisconnectAck,
    kCmdSubscribe,
    kCmdSubscribeAck,
    kCmdUnsubscribe,
    kCmdUnsubscribeAck,
    kCmdPushReq,
    kCmdPushReqAck,
    kCmdPush = 0x20,
    kCmdGetCamrea 					= 0x101,
    kCmdGetCamreaAck			= 0x102,
    kCmdGetRecordedFiles			= 0x103,
    kCmdGetRecordedFilesAck 	= 0x104,
    kCmdPtzCtrl 						= 0x105,
    kCmdPtzCtrlAck 					= 0x106,
};




typedef enum {
    kTypeCamera = 1,
    kTypePC = 100,
    kTypeAndroid = 101,
    kTypeIOS = 102
} ClientType;

typedef struct {
    uint8_t flag;
    uint32_t dialog_id;
    uint8_t frame_type;
    uint8_t reserved[2];
}__attribute__((packed)) PushHeader;

}

#endif
