#include <stdlib.h>
#include <stdio.h>
//#include "Stream2Mp4.h"

#include <libavutil/opt.h>
#include <libavutil/mathematics.h>
#include <libavutil/timestamp.h>
#include <libavformat/avformat.h>
#include <libswresample/swresample.h>
#include <libswresample/swresample.h>

#define STREAM_FRAME_RATE 25
#define STREAM_PIX_FMT    AV_PIX_FMT_YUV420P /* default pix_fmt */

static int ptsInc = 0;
static int vi = -1;
static int waitkey = 1;

// < 0 = error
// 0 = I-Frame
// 1 = P-Frame
// 2 = B-Frame
// 3 = S-Frame
int getVopType( const void *p, int len )
{
	if ( !p || 6 >= len )
		return -1;

	unsigned char *b = (unsigned char*)p;

	// Verify NAL marker
	if ( b[ 0 ] || b[ 1 ] || 0x01 != b[ 2 ] )
	{   b++;
	if ( b[ 0 ] || b[ 1 ] || 0x01 != b[ 2 ] )
		return -1;
	} // end if

	b += 3;

	// Verify VOP id
	if ( 0xb6 == *b )
	{   b++;
	return ( *b & 0xc0 ) >> 6;
	} // end if

	switch( *b )
	{   case 0x65 : return 0;
	case 0x61 : return 1;
	case 0x01 : return 2;
	} // end switch

	return -1;
}

int get_nal_type( void *p, int len )
{
	if ( !p || 5 >= len )
		return -1;

	unsigned char *b = (unsigned char*)p;

	// Verify NAL marker
	if ( b[ 0 ] || b[ 1 ] || 0x01 != b[ 2 ] )
	{   b++;
	if ( b[ 0 ] || b[ 1 ] || 0x01 != b[ 2 ] )
		return -1;
	} // end if

	b += 3;

	return *b;
}


/* Add an output stream */
AVStream *add_stream(AVFormatContext *oc, AVCodec **codec, enum AVCodecID codec_id)
{
	AVCodecContext *c;
	AVStream *st;

	/* find the encoder */
	*codec = avcodec_find_encoder(codec_id);
	if (!*codec)
	{
		printf("could not find encoder for '%s' \n", avcodec_get_name(codec_id));
		exit(1);
	}

	st = avformat_new_stream(oc, *codec);
	if (!st)
	{
		printf("could not allocate stream \n");
		exit(1);
	}
	st->id = oc->nb_streams-1;
	c = st->codec;
	vi = st->index;

	switch ((*codec)->type)
	{
	case AVMEDIA_TYPE_AUDIO:
		c->sample_fmt = (*codec)->sample_fmts ? (*codec)->sample_fmts[0] : AV_SAMPLE_FMT_FLTP;
		c->bit_rate = 64000;
		c->sample_rate = 44100;
		c->channels = 2;
		break;

	case AVMEDIA_TYPE_VIDEO:
		c->codec_id = codec_id;
		c->bit_rate = 90000;
		c->width = 480;
		c->height = 354;
		c->time_base.den = 15;
		c->time_base.num = 1;
		c->gop_size = 12;
		c->pix_fmt = STREAM_PIX_FMT;
		if (c->codec_id == AV_CODEC_ID_MPEG2VIDEO)
		{
			c->max_b_frames = 2;
		}
		if (c->codec_id == AV_CODEC_ID_MPEG1VIDEO)
		{
			c->mb_decision = 2;
		}
		break;

	default:
		break;
	}

	if (oc->oformat->flags & AVFMT_GLOBALHEADER)
	{
		c->flags |= CODEC_FLAG_GLOBAL_HEADER;
	}

	return st;
}



void open_video(AVFormatContext *oc, AVCodec *codec, AVStream *st)
{
	int ret;
	AVCodecContext *c = st->codec;

	/* open the codec */
	ret = avcodec_open2(c, codec, NULL);
	if (ret < 0)
	{
		printf("could not open video codec");
		//exit(1);
	}

}

int CreateMp4(AVFormatContext *m_pOc, void *p, int len)
{
	int ret; // 成功返回0，失败返回1
	const char* pszFileName = "output002.mp4";
	AVOutputFormat *fmt;
	AVCodec *video_codec;
	AVStream *m_pVideoSt;

	if (0x67 != get_nal_type(p, len))
	{
		printf("can not detect nal type");
		return -1;
	}
	av_register_all();

	avformat_alloc_output_context2(m_pOc, NULL, NULL, pszFileName);
	if (!m_pOc)
	{
		printf("Could not deduce output format from file extension: using MPEG. \n");
		avformat_alloc_output_context2(&m_pOc, NULL, "mpeg", pszFileName);
	}
	if (!m_pOc)
	{
		return 1;
	}

	fmt = m_pOc->oformat;

	if (fmt->video_codec != AV_CODEC_ID_NONE)
	{
		m_pVideoSt = add_stream(m_pOc, &video_codec, fmt->video_codec);
	}

	if (m_pVideoSt)
	{
		open_video(m_pOc, video_codec, m_pVideoSt);
	}

	av_dump_format(m_pOc, 0, pszFileName, 1);

	/* open the output file, if needed */
	if (!(fmt->flags & AVFMT_NOFILE))
	{
		ret = avio_open(&m_pOc->pb, pszFileName, AVIO_FLAG_WRITE);
		if (ret < 0)
		{
			printf("could not open '%s': %s\n", pszFileName);
			return 1;
		}
	}

	/* Write the stream header, if any */
	ret = avformat_write_header(m_pOc, NULL);
	if (ret < 0)
	{
		printf("Error occurred when opening output file");
		return 1;
	}
}


/* write h264 data to mp4 file
 * 创建mp4文件返回2；写入数据帧返回0 */

void WriteVideo(AVFormatContext *m_pOc,void* data, int nLen)
{
	int ret;

	if ( 0 > vi )
	{
		printf("vi less than 0");
		//return -1;
	}
	AVStream *pst = m_pOc->streams[ vi ];

	// Init packet
	AVPacket pkt;

	// 我的添加，为了计算pts
	AVCodecContext *c = pst->codec;

	av_init_packet( &pkt );
	pkt.flags |= ( 0 >= getVopType( data, nLen ) ) ? AV_PKT_FLAG_KEY : 0;

	pkt.stream_index = pst->index;
	pkt.data = (uint8_t*)data;
	pkt.size = nLen;

	// Wait for key frame
	if ( waitkey )
		if ( 0 == ( pkt.flags & AV_PKT_FLAG_KEY ) )
			return ;
		else
			waitkey = 0;


	pkt.pts = (ptsInc++) * (90000/STREAM_FRAME_RATE);
	//pkt.dts = (ptsInc++) * (90000/STREAM_FRAME_RATE);

	ret = av_interleaved_write_frame( m_pOc, &pkt );
	if (ret < 0)
	{
		printf("cannot write frame");
	}


}

void CloseMp4(AVFormatContext *m_pOc)
{
	waitkey = -1;
	vi = -1;

	if (m_pOc)
		av_write_trailer(m_pOc);

	if (m_pOc && !(m_pOc->oformat->flags & AVFMT_NOFILE))
		avio_close(m_pOc->pb);

	if (m_pOc)
	{
		avformat_free_context(m_pOc);
		m_pOc = NULL;
	}

}

