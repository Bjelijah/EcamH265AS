/*******************************************************************************
Copyright (C), 2010-2020, Huawei Tech. Co., Ltd.
File name: IHWVideo_Typedef.h
Author & ID: 
Version: 1.00
Date:
Description: This file include usually data type redefine macro��windows��linux��
Function List:
History:

*******************************************************************************/
#ifndef __IHWVIDEO_TYPEDEF_H__
#define __IHWVIDEO_TYPEDEF_H__

#ifdef __cplusplus
#if __cplusplus
extern "C" {
#endif    /* __cpluscplus*/
#endif    /* __cpluscplus*/

/******************************************************************************
*                             Redefine Data Type
*******************************************************************************/
// Redefine integer data type
typedef signed char        INT8;
typedef signed short       INT16;
typedef signed int         INT32;
typedef unsigned char      UINT8;
typedef unsigned short     UINT16;
typedef unsigned int       UINT32;
#define __GNUC__ 1//FIXME add by cbj
#if defined(__GNUC__) || defined(__TMS320C6X_PLATFORM__)
typedef          long long INT64;
typedef unsigned long long UINT64;
//typedef         signed int intptr_t;
#else
typedef          __int64   INT64;
typedef unsigned __int64   UINT64;
#endif

#ifdef __TMS320C6X_PLATFORM__  //__TMS320C6X__
// redefine 40bit data type, only for TI DSP platform
typedef unsigned long       UINT40;
typedef long                INT40;
#else
// redefine 40bit data type, only for VC platform
typedef INT64               UINT40;
typedef INT64               INT40;
#endif

// Redefine float data type
typedef float              FLOAT32;
typedef double             FLOAT64;

// Redefine bool data type
typedef char               BOOL8;
typedef short              BOOL16;
typedef int                BOOL32;

#ifndef TRUE
#define TRUE               1
#endif

#ifndef FALSE
#define FALSE              0
#endif

#define OUTPUT_TEST_ARM              0

/******************************************************************************
*                             Dynamic and static lib config
*******************************************************************************/
#if defined(_MSC_VER)

#if defined(HW_VIDEO_ALG_EXPORTS_DLL)
#define HW_VIDEO_ALG_EXPORT_API extern __declspec(dllexport)
#elif defined(HW_VIDEO_ALG_EXPORTS_LIB)
#define HW_VIDEO_ALG_EXPORT_API extern __declspec(dllimport)
#else 
#define HW_VIDEO_ALG_EXPORT_API 
#endif

#define inline __inline

#elif defined(__GNUC__)
#define HW_VIDEO_ALG_EXPORT_API 
#else
#define HW_VIDEO_ALG_EXPORT_API 
#endif

/******************************************************************************
*                      version info/ log level/ callback func
*******************************************************************************/
// Version
#define IHWVIDEO_ALG_VERSION_LENGTH   48      // version array lenth
#define IHWVIDEO_ALG_TIME_LENGTH      28      // module compile time array lenth
typedef struct tagIHWVIDEO_ALG_VERSION
{
    INT8    cVersionChar[IHWVIDEO_ALG_VERSION_LENGTH];   // library version
    INT8    cReleaseTime[IHWVIDEO_ALG_TIME_LENGTH];      // compiled time   
    UINT32  uiCompileVersion;                            // compiler version
}IHWVIDEO_ALG_VERSION_STRU;


// Log level
typedef enum tagIHWVIDEO_ALG_LOG_LEVEL
{
    IHWVIDEO_ALG_LOG_ERROR = 0,   // log for error
    IHWVIDEO_ALG_LOG_WARNING,     // log for waring
    IHWVIDEO_ALG_LOG_INFO,        // log for help
    IHWVIDEO_ALG_LOG_DEBUG        // print debug info, used for developer debug

}IHWVIDEO_ALG_LOG_LEVEL;

typedef enum tagIHWVIDEO_ALG_CMD
{
    IHWVIDEO_ALG_CMD_SETPARAM = 0,   // set dynamic param
    IHWVIDEO_ALG_CMD_GETPARAM        // get dynamic param
    
}IHWVIDEO_ALG_CMD;

/******************************************************************************
* Instruction : memory create callback func type
*
* Param : uiChannelID - [in] channel ID
*         uiSize      - [in] memory size
*
* Return Value :  Success return memory address
*           Failed return NULL
*******************************************************************************/
typedef void *(* IHWVIDEO_ALG_MALLOC_FXN)( UINT32 uiChannelID, UINT32 uiSize);

/******************************************************************************
* Instruction   :  memory free callback func type
*
* Param   :  uiChannelID - [in] channel ID
*            pMem        - [in] memory address
*
* Return Value :  Null
*******************************************************************************/
typedef void  (* IHWVIDEO_ALG_FREE_FXN)( UINT32 uiChannelID, void *pMem);

/******************************************************************************
* Instruction   :  log callback func type
*
* Param  :  uiChannelID - [in] channel ID
*           eLevel      - [in] set log level
*           pszMsg      - [in] log info(string)
*           ...         - [in] changeable param
*
* Return Value :  Null
*******************************************************************************/
typedef void  (* IHWVIDEO_ALG_LOG_FXN)( UINT32 uiChannelID, IHWVIDEO_ALG_LOG_LEVEL eLevel, INT8 *pszMsg, ...);


#ifdef __cplusplus
#if __cplusplus
}
#endif    /* __cpluscplus*/
#endif    /* __cpluscplus*/

#endif /**< __IHWVIDEO_TYPEDEF_H__ */

