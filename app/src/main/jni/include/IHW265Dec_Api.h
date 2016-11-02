/******************************************************************************
* Copyright (C), 2010-2020, Huawei Tech. Co., Ltd.
* File name      : IHW265Dec_Api.h
* Author & ID    : 
* Version        : 1.00
* Date           : 
* Description    : 
* Others         : 
* Function List  : 
* History        : 
* 1. Date        : 
*    Author & ID : 
*    Modification:      
*******************************************************************************/
#ifndef __IHW265DEC_API_H__  /* Macro sentry to avoid redundant including */
#define __IHW265DEC_API_H__

#ifdef __cplusplus
extern "C"{
#endif 

#include <include/IHWVideo_Typedef.h>

typedef void* IH265DEC_HANDLE;   // Decoder handle

// Decoder status info
typedef enum
{
	DECODER_INVALID = 0x00224466,   // Decoder invalid status
	DECODER_VALID   = 0x11335577,   // Decoder created status

}DECODER_STATUS;

/// Supported slice type
typedef enum
{
    IH265D_B_SLICE = 0,
    IH265D_P_SLICE = 1,
    IH265D_I_SLICE = 2
}SLICE_TYPE;

typedef enum tagHW265D_FRAMETYPE
{
    IH265D_FRAME_I = 0,
    IH265D_FRAME_P,
    IH265D_FRAME_B,
    IH265D_FRAME_UNKNOWN
} HW265D_FRAMETYPE;

typedef enum tagHW265D_DECODEMODE
{
    IH265D_DECODE     = 0,       // Safe decode
    IH265D_DECODE_END            // Decode over and decoder output the residual picture
} HW265D_DECODEMODE;

typedef enum tagHW265D_DECODESTATUS
{
    IH265D_GETDISPLAY     = 0,
    IH265D_NEED_MORE_BITS,
    IH265D_NO_PICTURE,
    IH265D_ERR_HANDLE
} HW265D_DECODESTATUS;

// Thread type
typedef enum tagHW265D_THREADTYPE
{
    IH265D_SINGLE_THREAD     = 0,    
    IH265D_MULTI_THREAD              
} HW265D_THREADTYPE;

// Output order
typedef enum tagHW265D_OUTPUTORDER
{
    IH265D_DECODE_ORDER     = 0,       // Output by decoder order
    IH265D_DISPLAY_ORDER               // Output by display order
} HW265D_OUTPUTORDER;

typedef struct tagHW265D_USERDATA 
{ 
    UINT32  uiUserDataType;              // User data type
    UINT32  uiUserDataSize;              // User data size 
    UINT8   *pucData;                    // User data buffer

    struct tagHW265D_USERDATA  *pNext;      // Direct next user data
}HW265D_USERDATA;

// The inside picture buffer provided by outside
typedef struct tagHW265D_OUTPUTBUFFER
{ 
    BOOL32 bRefresh;
    INT32  iPicWidth;
    INT32  iPicHeight;
    INT32  iBufferNum;

    UINT8 *pucData[100];
}HW265D_OUTBUFFER;

typedef struct tagIHW265D_INIT_PARAM
{
    UINT32  uiChannelID;                 // [in] channel ID, used for channel info
    INT32   iMaxWidth;                   // [in] maximum width
    INT32   iMaxHeight;                  // [in] maximum height
    INT32   iMaxRefNum;                  // [in] maximum reference num

    HW265D_THREADTYPE  eThreadType;  // thread type
    HW265D_OUTPUTORDER eOutputOrder; // output type only used for DecodeFrame mode

    HW265D_USERDATA    *pstUserData;
            
    IHWVIDEO_ALG_MALLOC_FXN  MallocFxn;
    IHWVIDEO_ALG_FREE_FXN    FreeFxn;
    IHWVIDEO_ALG_LOG_FXN     LogFxn;     // log output callback function
}IHW265D_INIT_PARAM;


typedef struct tagIH265DEC_INARGS
{
    UINT8  *pStream;
    UINT32 uiStreamLen;
    UINT64 uiTimeStamp;
    HW265D_DECODEMODE eDecodeMode;

#if OUTPUT_TEST_ARM
    HW265D_OUTBUFFER stOutBuffer;
#endif
}IH265DEC_INARGS;

typedef struct tagCU_OUTPUT_INFO
{
	UINT32 uiCuNumIntra4;
	UINT32 uiCuNumIntra8;
	UINT32 uiCuNumIntra16;
	UINT32 uiCuNumIntra32;
    UINT32 uiCuNumIntra64;
	UINT32 uiCuNumPcm4;
	UINT32 uiCuNumPcm8;
	UINT32 uiCuNumPcm16;
	UINT32 uiCuNumPcm32;
	UINT32 uiCuNumPcm64;
	UINT32 uiCuNumInter8;
	UINT32 uiCuNumInter16;
	UINT32 uiCuNumInter32;
	UINT32 uiCuNumInter64;
	UINT32 uiCuNumSkip8;
	UINT32 uiCuNumSkip16;
	UINT32 uiCuNumSkip32;
	UINT32 uiCuNumSkip64;
}CU_OUTPUT_INFO;


typedef struct tagIH265DEC_OUTARGS
{
    UINT32              uiChannelID;// [out] channel ID, used for identify channel info
    UINT32              uiBytsConsumed;

    UINT64              uiTimeStamp; 
    HW265D_FRAMETYPE    eFrameType;   
    HW265D_DECODESTATUS eDecodeStatus;

    UINT32              uiDecWidth;
    UINT32              uiDecHeight;
	UINT32              uiYStride;
    UINT32              uiUVStride;
#if OUTPUT_TEST_ARM
    UINT32              uiCropLeft;
    UINT32              uiCropTop;
    UINT32              uiCropWidth;
    UINT32              uiCropHeight;
#endif

	UINT8               *pucOutYUV[3];// YUV address, store YUV in order
    
	// bitlen
	UINT32              uiCodingBytesOfCurFrm;    
	// sei
	HW265D_USERDATA     stUserData;
	// vui
	UINT32	            uiAspectRatioIdc;
    UINT32              uiSarWidth;
	UINT32	            uiSarHeight;

	// vps
	UINT32              uiVpsNumUnitsInTick;
	UINT32              uiVpsTimeScale;
	// cuinfo
	CU_OUTPUT_INFO      stCuOutInfo;
    // errorinfo
    BOOL32              bIsError;
}IH265DEC_OUTARGS;
/*@}*/

/*******************************************************************************
* All error code are negative value
* bit[31:24]: show if it has error, 0xF0 for fatal error, need to exit; warning error such as 0xE0, decoder can not ensure validity
* bit[23:16]: show error algorithm, 40 for H.265
* bit[15:0] : show inside error, 1xxxx for general error, 2xxxx for init error, 3xxxx for control error, 4xxxx for process error, 5xxxx for delete error..
*******************************************************************************/
typedef enum tagIHW265D_RETURN
{
    // Suceess return
    IHW265D_OK                        = 0,              // Call ok
    IHW265D_NEED_MORE_BITS            = 1,              // Call ok, but need more bit for a frame
    IHW265D_FIND_NEW_PIC              = 2,
#if OUTPUT_TEST_ARM
    IHW265D_NEED_CHANGE_SIZE          = 3,
#endif

	// Warning return code
	IHW265D_NAL_HEADER_WARNING		= 0x00000004,
	IHW265D_VPS_WARNING				= 0x00000008,
	IHW265D_SPS_WARNING				= 0x00000010,
	IHW265D_PPS_WARNING				= 0x00000020,
	IHW265D_SLICEHEADER_WARNING		= 0x00000040,
	IHW265D_SLICEDATA_WARNING		= 0x00000080,

    // General error
    IHW265D_INVALID_ARGUMENT          = 0xF0401000,     // Input parameter is wrong
    IHW265D_DECODER_NOT_CREATE,                         // Decoder not creted

    // Init error
    IHW265D_MALLOC_FAIL               = 0xF0402000,     // memory malloc failed
    IHW265D_INVALID_MAX_WIDTH,                          // maximum width exceed limit
    IHW265D_INVALID_MAX_HEIGHT,                         // maximum height exceed limit
    IHW265D_INVALID_MAX_REF_PIC,                        // maximum reference num exceed limit
	IHW265D_INVALID_THREAD_CONTROL,                     // error thread control
    IHW265D_INVALID_MALLOC_FXN,                         // malloc callback function pointer invalid
    IHW265D_INVALID_FREE_FXN,                           // free callback function pointer invalid
    IHW265D_INVALID_LOG_FXN,                            // log callback function pointer invalid
    IHW265D_STREAMBUF_NULL,                             // decoder input stram buf is empty
    IHW265D_INVALID_STREAMBUF_LENGTH,                   // decoder input stream lenth error
    IHW265D_YUVBUF_NULL,                                // decoder output yuv buffer pointer is NULL
    IHW265D_YUVBUF_ADDR_NOT_ALIGN_16,                   // decoder output yuv buffer address not alignment by 16 byte
    IHW265D_POSTPROCESS_ERR,                            // postprocess select error
    IHW265D_ERRCONCEAL_ERR,                             // error canceal parameter config error

    // Decode error
    IHW265D_NAL_HEADER_ERR				= 0xF0404001,   // NAL decode error
	IHW265D_VPS_ERR						= 0xF0404002,   // vps decode error
	IHW265D_SPS_ERR						= 0xF0404003,   // sps decode error
	IHW265D_PPS_ERR						= 0xF0404004,   // pps decode error
	IHW265D_SLICEHEADER_ERR				= 0xF0404005,   // sliceheader decode error
	IHW265D_SLICEDATA_ERR				= 0xF0404006,   // slicedata decode error
    // Decode warning
    IHW265D_FRAME_DECODE_WARN			= 0xE0404007,    // frame data warning, the stream may has error code, output yuv picture quality can not be provided
	IHW265D_THREAD_ERROR    			= 0xE0404008     // multi thread error

}IHW265D_RETURN;

/******************************************************************************
* Instruction : create decoder handle
*
* Param : phDecoder   - [out] decoder handle pointer
*         pstInitParam- [in]  decoder init config parameter set address
*
* Return Value :  Success return IHW265D_OK
*           Failed return other return code
*******************************************************************************/
HW_VIDEO_ALG_EXPORT_API INT32 IHW265D_Create(IH265DEC_HANDLE *phDecoder, IHW265D_INIT_PARAM *pstInitParam);

/******************************************************************************
* Instruction : decode a frame data
*
* Param   :  hDecoder    - [in]  decoder handle
*           pstInArgs   - [in]  input parameter struct pointer
*           pstOutArgs  - [out] output parameter struct pointer
*
* Return Value :  Success return IHW265D_OK
*           Failed return other return code
*******************************************************************************/
HW_VIDEO_ALG_EXPORT_API INT32 IHW265D_DecodeAU(IH265DEC_HANDLE hDecoder, IH265DEC_INARGS *pstInArgs, IH265DEC_OUTARGS *pstOutArgs);

HW_VIDEO_ALG_EXPORT_API INT32 IHW265D_DecodeFrame( IH265DEC_HANDLE hDecoder, IH265DEC_INARGS *pstInArgs, IH265DEC_OUTARGS *pstOutArgs);

/******************************************************************************
* Instruction : delete decoder
*
* Param   :  hDecoder - [in] decoder handle
*
* Return Value :  Success return IHW265D_OK
*           Failed return other return code
*******************************************************************************/
HW_VIDEO_ALG_EXPORT_API INT32 IHW265D_Delete(IH265DEC_HANDLE hDecoder);

/******************************************************************************
* Instruction : get decoder version
*
* Param   :  pstVersion - [out] version number struct pointer
*
* Return Value :  Success return IHW265D_OK
*           Failed return other return code
*******************************************************************************/
HW_VIDEO_ALG_EXPORT_API INT32 IHW265D_GetVersion(IHWVIDEO_ALG_VERSION_STRU *pstVersion); 

#ifdef __cplusplus
}
#endif
#endif /* __IHW265D_API_H__ */

