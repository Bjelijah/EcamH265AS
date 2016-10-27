package com.howell.utils;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.howell.ecamh265.R;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ClientUpdateUtils {
	private static final int DOWN_ERROR = 1;
    /*  
     *   
     * �����Ի���֪ͨ�û����³���   
     *   
     * �����Ի���Ĳ��裺  
     *  1.����alertDialog��builder.    
     *  2.Ҫ��builder��������, �Ի��������,��ʽ,��ť  
     *  3.ͨ��builder ����һ���Ի���  
     *  4.�Ի���show()����    
     */    
	static boolean  h265NoNeedUpdata = true;//add by cbj for H265 20160721
    public static void showUpdataDialog(final Context context,final String httpUrl) {  
    	if(h265NoNeedUpdata){
    		return;
    	}
        Builder builer = new Builder(context) ;
        builer.setIcon(R.drawable.expander_ic_minimized);
        builer.setTitle(context.getResources().getString(R.string.update_dialog_title));

        builer.setMessage(context.getResources().getString(R.string.update_dialog_message));    
        //����ȷ����ťʱ�ӷ����������� �µ�apk Ȼ��װ      
        builer.setPositiveButton(context.getResources().getString(R.string.ok), new OnClickListener() {    
        public void onClick(DialogInterface dialog, int which) {    
                Log.i("","����apk,����");    
                downLoadApk(context,httpUrl);    
            }       
        });    
        //����ȡ��ťʱ���е�¼     
        builer.setNegativeButton(context.getResources().getString(R.string.cancel), new OnClickListener() {    
            public void onClick(DialogInterface dialog, int which) {    
                // TODO Auto-generated method stub     
                //LoginMain();    
            }    
        });    
        AlertDialog dialog = builer.create();    
        dialog.show();    
    }   
    
    /*  
     * �ӷ�����������APK  
     */    
    protected static void downLoadApk(final Context context,final String httpUrl) {    
        final ProgressDialog pd;    //������Ի���     
        pd = new  ProgressDialog(context);    
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);    
        pd.setMessage(context.getResources().getString(R.string.download_dialog_message));    
        pd.show();    
        new Thread(){    
            @Override    
            public void run() {    
                try {    
                    File file = getFileFromServer(httpUrl, pd);    
                    //sleep(3000);    
                    installApk(file,context);    
                    pd.dismiss(); //�����������Ի���     
                } catch (Exception e) {    
                    Message msg = new Message();    
                    msg.what = DOWN_ERROR;
                    msg.obj = context;
                    handler.sendMessage(msg);    
                    e.printStackTrace();    
                }    
            }}.start();    
    }  
    
    public static Handler handler = new Handler(){        
        @Override    
        public void handleMessage(Message msg) {    
            // TODO Auto-generated method stub     
            super.handleMessage(msg);    
            switch (msg.what) {    
//            case UPDATA_CLIENT:    
//                 //�Ի���֪ͨ�û������      
//                 showUpdataDialog();    
//                 break;    
//            case GET_UNDATAINFO_ERROR:    
//                    //��������ʱ      
//                    Toast.makeText(getApplicationContext(), "��ȡ������������Ϣʧ��", 1).show();    
//                    LoginMain();    
//                    break;      
            case DOWN_ERROR:    
                    //����apkʧ��     
            		Context context = (Context)msg.obj;
                    Toast.makeText((Context)msg.obj, context.getResources().getString(R.string.download_dialog_fail), Toast.LENGTH_SHORT).show();
                    break;      
            }    
        }    
    };   
    
    //��װapk      
    protected static void installApk(File file,Context context) {    
        Intent intent = new Intent();    
        //ִ�ж���     
        intent.setAction(Intent.ACTION_VIEW);    
        //ִ�е��������     
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");//���߰����˴�AndroidӦΪandroid��������ɰ�װ����      
        context.startActivity(intent);    
    }    
    
	public static File getFileFromServer(String httpUrl, ProgressDialog pd) throws Exception{     
		//�����ȵĻ���ʾ��ǰ��sdcard�������ֻ��ϲ����ǿ��õ�      
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){     
			URL url = new URL(httpUrl);     
			HttpURLConnection conn =  (HttpURLConnection) url.openConnection();     
			conn.setConnectTimeout(5000);     
			//��ȡ���ļ��Ĵ�С       
			pd.setMax(conn.getContentLength());     
			InputStream is = conn.getInputStream();     
			File file = new File(Environment.getExternalStorageDirectory(), "ecamera.apk");     
			FileOutputStream fos = new FileOutputStream(file);     
			BufferedInputStream bis = new BufferedInputStream(is);     
			byte[] buffer = new byte[1024];     
			int len ;     
			int total=0;     
			while((len =bis.read(buffer))!=-1){     
				fos.write(buffer, 0, len);     
				total+= len;     
				//��ȡ��ǰ������      
				pd.setProgress(total);     
			}     
			fos.close();     
			bis.close();     
			is.close();     
			return file;     
		}     
		else{     
			return null;     
		}     
	}    
	
}
