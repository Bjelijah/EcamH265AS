package com.howell.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.SocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.howell.action.TurnProtocolMgr;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

public class SSlSocketUtil implements IConst{
	private static SSlSocketUtil mInstance = null;
	public static SSlSocketUtil getInstance(){
		if(mInstance == null){
			mInstance = new SSlSocketUtil();
		}
		return mInstance;
	}
	private SSlSocketUtil(){}
	Handler handler;
	Context mContext;
	String mIp;
	int mPort;
	

	SSLSocket mSocket;
	OutputStream mOutputStream;
	InputStream mInputStream;
	
	

	Thread mReadThread;
	boolean mbConnected;
	
	public void setHandler(Handler handler){
		this.handler = handler;
	}
	
	public void init(Context context,String ip,int port){
		this.mIp = ip;
		this.mPort = port;
		this.mContext = context;
	}
	
	public void connectSocket(){
		Log.i("123", "ssl socket connect");
		
		
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				try {
					SSLContext context; 
					KeyStore ks = KeyStore.getInstance("BKS");
					ks.load(mContext.getResources().getAssets().open("client.bks"),"123456".toCharArray());  
					KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
					kmf.init(ks,"123456".toCharArray());
//					context = SSLContext.getInstance("SSL");  
					context = SSLContext.getInstance("TLS");
					context.init(kmf.getKeyManagers(), new TrustManager[]{new MyX509TrustManager()}, null);  
					SocketFactory factory = context.getSocketFactory(); 
					Log.i("123", "ip="+mIp+" port="+mPort);
					mSocket = (SSLSocket) factory.createSocket(mIp, mPort);  
					
//					mSocket.setSoLinger(true, 30);
//					mSocket.setSendBufferSize(4096);
//					mSocket.setReceiveBufferSize(2*1024*1024);
					
					mOutputStream = mSocket.getOutputStream();
					mInputStream  = mSocket.getInputStream();

					if(mOutputStream==null){
						Log.e("123", "getout put stream = null");
					}else{
						Log.i("123", "out put stream ok");
					}
					
					
					mbConnected = true;
					
					read();
					
					
				} catch (UnrecoverableKeyException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (KeyManagementException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (KeyStoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (CertificateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}catch(Exception e){
					e.printStackTrace();
				}
			
			
				return null;
			}
			
			protected void onPostExecute(Void result) {
				handler.sendEmptyMessage(MSG_SOCK_CONNECT);
			};
			
		}.execute();
	}
	
	public void disConnectSocket(){
		Log.i("123", "disConnectSocket");
		mbConnected = false;
		
		try {
			mInputStream.close();
			mOutputStream.close();
			mSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void write(final byte [] buffer){
		if(!mbConnected){
			Log.e("123", "socket not connect");
			return;
		}

		new Thread(){
			public void run() {
				try {
					mOutputStream.write(buffer);
					mOutputStream.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			};
		}.start();
	}
	
	public void read(){
		mReadThread = new ReadThread();
		mReadThread.start();
	}
	
	class ReadThread extends Thread{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Log.i("123", "start read  mbConnected="+mbConnected);
			int ss;
			byte [] buffer = new byte[2*1024*1024];
			
			while(mbConnected){
				try {
					if(!mSocket.isClosed()){
						if(mSocket.isConnected()){
							
							ss = mInputStream.read(buffer);
							byte [] bar = new byte [ss];
							System.arraycopy(buffer, 0, bar, 0, ss);
//							ss = mIs.read(buffer);
							
							Log.i("123", "buffer="+buffer[0]+" ss="+ss);
							TurnProtocolMgr.getInstance().processMsg(bar);
							
							
						}else{
							Log.e("123", "read mSocket is not connect");
						}
						
						
						
						
					}else{
						Log.e("123", "read socket is close");
					}
					
					
				
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					break;
				}
			}
			super.run();
		}
	}
	
	
	
	
	
	
	
	
	
	static class MyX509TrustManager implements X509TrustManager{
		private static  X509Certificate[] _AcceptedIssuers = new X509Certificate[]{};
		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
			if(null != chain){
				for(int k=0; k < chain.length; k++){
					X509Certificate cer = chain[k];
					print(cer);
				}
			}
			Log.i("log123", "check client trusted. authType="+authType);

		}

		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
			if(null != chain){
				Log.i("123", "cer len="+chain.length);
				for(int k=0; k < chain.length; k++){
					X509Certificate cer = chain[k];
					print(cer);
				}
			}

			Log.i("log123", "check servlet trusted. authType="+authType);
		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {//返回受信任的证书组

			Log.i("log123", "get acceptedissuer");

			return _AcceptedIssuers;
		}


		private void print(X509Certificate cer){

			int version = cer.getVersion();
			String sinname = cer.getSigAlgName();
			String type = cer.getType();
			String algorname = cer.getPublicKey().getAlgorithm();
			BigInteger serialnum = cer.getSerialNumber();
			Principal principal = cer.getIssuerDN();
			String principalname = principal.getName();
			
			Log.i("log123", "version="+version+", sinname="+sinname+", type="+type+", algorname="+algorname+", serialnum="+serialnum+", principalname="+principalname);
		
		}

	}
	
	
	
	
	
	
}
