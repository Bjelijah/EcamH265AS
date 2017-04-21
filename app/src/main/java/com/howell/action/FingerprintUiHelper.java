package com.howell.action;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.hardware.fingerprint.FingerprintManager.AuthenticationResult;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.howell.utils.MyFingerprintUtil;

import java.lang.reflect.InvocationTargetException;

import bean.MyFingerprintBeans;


@SuppressLint({"NewApi", "Override"})
public class FingerprintUiHelper extends FingerprintManager.AuthenticationCallback {

	private FingerprintManager mFingerprintManager;
	private CancellationSignal mCancellationSignal;
	private Callback mCallback;
	private boolean mSelfCancelled;
	private Context mContext;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Log.i("123", "get msg msg.what=" + msg.what);
			super.handleMessage(msg);
		}
	};

	public FingerprintUiHelper(Context context, FingerprintManager fingerprintManager, Callback callback) {
		this.mFingerprintManager = fingerprintManager;
		this.mCallback = callback;
		this.mContext = context;
	}

	public static boolean isFingerAvailable(Context context) {

		FingerprintManager fm = context.getSystemService(FingerprintManager.class);
		if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
			return false;
		}
		if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
			return false;
		}
		return fm.isHardwareDetected() && fm.hasEnrolledFingerprints();
	}

	public boolean isFingerprintAuthAvailable() {
		if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
			return false;
		}
		if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
			return false;
		}
		return mFingerprintManager.isHardwareDetected() && mFingerprintManager.hasEnrolledFingerprints();
	}

	public void startListening(FingerprintManager.CryptoObject cryptoObject) {
		if (!isFingerprintAuthAvailable()) {
			Log.e("123", "finger print not available");
			return;
		}
		mCancellationSignal = new CancellationSignal();
		mSelfCancelled = false;
		if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
			return;
		}
		//Log.i("123","finger start listen");
		mFingerprintManager.authenticate(cryptoObject, mCancellationSignal, 0, this, mHandler);
	}

	public void stopListening(){
		if (mCancellationSignal!=null ) {
			mSelfCancelled = true;
			try{
				mCancellationSignal.cancel();
			}catch(Exception e){
				e.printStackTrace();
			}
			mCancellationSignal = null;
		}
	}



	@Override
	public void onAuthenticationError(int errorCode, CharSequence errString) {
		Log.i("123", "onAuthenticationError:"+"errorCode="+errorCode+"   errString="+errString);

		switch (errorCode) {
		case FingerprintManager.FINGERPRINT_ERROR_CANCELED:
			mSelfCancelled = true;
			try {
				mCallback.onFingerCancel();
			} catch (Exception e) {
				e.printStackTrace();
			}

			break;
		case FingerprintManager.FINGERPRINT_ERROR_LOCKOUT:
			try{
				mCallback.onError(errorCode,errString);
			}catch(Exception e){
				e.printStackTrace();
			}
			break;

		default:
			break;
		}
	}

	@Override
	public void onAuthenticationFailed() {
		Log.e("123", "onAuthenticationFailed");
		mCallback.onFailed();
	}

	@Override
	public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
		Log.e("123", "onAuthenticationError:"+"helpcode="+helpCode+"   helpString="+helpString);
		mCallback.onHelp(helpCode, helpString);
	}

	@Override
	public void onAuthenticationSucceeded(AuthenticationResult result) {
		Log.i("123","onAuthenticationSucceeded");
		MyFingerprintBeans bean = new MyFingerprintBeans();
		Log.e("123","bean start fingerprint");
		try {
			bean = MyFingerprintUtil.getFingerprint(result);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NullPointerException e){
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
		Log.e("123","bean="+bean);
		Log.e("123", "onAuthenticationSucceeded   fingerID="+bean.getFpID());

		mCallback.onAuthenticated(bean.getFpID());
	}


	public interface Callback{
		void onAuthenticated(int id);
		void onFailed();
		void onHelp(int helpCode, CharSequence str);
		void onError(int code, CharSequence s);
		void onFingerCancel();
	}

}
