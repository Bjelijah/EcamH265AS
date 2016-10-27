package com.howell.utils;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;



import android.content.Context;
import android.util.Log;

public class SSLConection {
	private static TrustManager [] trustManagers;
	
	public static class _FakeX509TrustManager implements X509TrustManager{
		private static final X509Certificate[] _AcceptedIssuers = new X509Certificate[]{};
		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			
			if(null != chain){
				for(int k=0; k < chain.length; k++){
					X509Certificate cer = chain[k];
					print(cer);
				}
			}
			Log.i("log123", "check client trusted. authType="+authType);
		}

		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			// TODO Auto-generated method stub
			if(null != chain){
				for(int k=0; k < chain.length; k++){
					X509Certificate cer = chain[k];
					print(cer);
				}
			}
			Log.i("log123", "check servlet trusted. authType="+authType);	
		}

		 public boolean isClientTrusted(X509Certificate[] chain) { 
	            return true; 
	    } 

	    public boolean isServerTrusted(X509Certificate[] chain) { 
	            return true; 
	    } 
		
		@Override
		public X509Certificate[] getAcceptedIssuers() {
			// TODO Auto-generated method stub
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
	public static void allowAllSSL(Context mContext){
		if(mContext==null){
			Log.e("123", "allow all ssl context = null");
		}
		
		
		HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
			
			@Override
			public boolean verify(String hostname, SSLSession session) {
				Log.i("log123", "verify hostname="+hostname+",PeerHost= "+session.getPeerHost());
				return true;
			}
		});
		
		SSLContext context;
		if(trustManagers == null){
			trustManagers = new TrustManager[]{new _FakeX509TrustManager()};
		}
		
		
		InputStream ksIn = null;
		
		try {
//			KeyStore keyStore = KeyStore.getInstance("PKCS12");
			KeyStore keyStore = KeyStore.getInstance("BKS");
//			 ksIn = mContext.getResources().getAssets().open("client.p12");
			ksIn = mContext.getResources().getAssets().open("client.bks");
			Log.i("123", "get ksin");
			
		
			keyStore.load(ksIn, "123456".toCharArray());
			
			
			String kmfAlgorithm = KeyManagerFactory.getDefaultAlgorithm();
			KeyManagerFactory kmf = KeyManagerFactory.getInstance(kmfAlgorithm);
			kmf.init(keyStore, "123456".toCharArray());
			
			context = SSLContext.getInstance("TLS");
//			context = SSLContext.getInstance("SSL");
//			Log.i("123", "using ssl");
			context.init(kmf.getKeyManagers(), trustManagers, new SecureRandom());
//			context.init(null, trustManagers, new SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}
		//Log.i("123", "allow all ssl");	
		catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				ksIn.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}	
}
