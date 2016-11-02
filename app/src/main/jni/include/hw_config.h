#ifndef hw_config_include_h
#define hw_config_include_h

#ifndef INVALID_HANDLE
#define INVALID_HANDLE (-1)
#endif

typedef long HWND;

typedef struct
{
	short wYear;
	short wMonth;
	short wDayofWeek;
	short wDay;
	short wHour;
	short wMinute;
	short wSecond;
	short wMilliseconds;
}SYSTEMTIME;

typedef struct
{
	int left;
	int top;
	int right;
	int bottom;
}RECT;

typedef int BOOL;

#endif
