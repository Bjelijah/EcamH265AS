package com.wyy.twodimcode;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.howell.activity.ChangeDeviceName;
import com.howell.ecamh265.R;
import com.howell.protocol.AddDeviceJsonString;
import com.howell.utils.MessageUtiles;
import com.wyy.twodimcode.camera.CameraManager;
import com.wyy.twodimcode.decoding.CaptureActivityHandler;
import com.wyy.twodimcode.decoding.InactivityTimer;
import com.wyy.twodimcode.view.ViewfinderView;

import org.json.JSONException;
import org.json.JSONObject;
import org.kobjects.base64.Base64;

import java.io.IOException;
import java.util.Vector;

public class CaptureActivity extends Activity implements Callback,OnClickListener {

	private CaptureActivityHandler handler;
	private ViewfinderView viewfinderView;
	private boolean hasSurface;//surface��û�б�����
	private Vector<BarcodeFormat> decodeFormats;
	private String characterSet;
	private InactivityTimer inactivityTimer;
	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	private static final float BEEP_VOLUME = 0.10f;
	private boolean vibrate;//���ɨ��ʱ�Ƿ�����ʾ
	
	private ImageView mBack;

	private static final int TEST = 1;
	
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.twodimcode);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		mBack = (ImageView)findViewById(R.id.iv_left);
		mBack.setOnClickListener(this);
		
		CameraManager.init(getApplication());
		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);//activity��ֹһ��ʱ����Զ��ر�

		
	}

	
	@Override
	protected void onResume() {
		super.onResume();
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		final SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			new Thread(){
				public void run() {
					Message msg = new Message();
					msg.what = TEST;
					msg.obj = surfaceHolder;
					myHandler.sendMessage(msg);
				};
			}.start();
			//initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		decodeFormats = null;
		characterSet = null;

		playBeep = false;
		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			playBeep = false;
		}
		initBeepSound();
		vibrate = true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}
//��ʼ�������
	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			CameraManager.get().openDriver(surfaceHolder);
		} catch (IOException ioe) {
			return;
		} catch (RuntimeException e) {
			return;
		}
		if (handler == null) {
			handler = new CaptureActivityHandler(this, decodeFormats,
					characterSet);
		}
	}
	
	Handler myHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch(msg.what){
			case TEST:
				SurfaceHolder holder = (SurfaceHolder)msg.obj;
				initCamera(holder);
				break;
			}
		}
	};

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceCreated(final SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			new Thread(){
				public void run() {
					Message msg = new Message();
					msg.what = TEST;
					msg.obj = holder;
					myHandler.sendMessage(msg);
				};
			}.start();
//			initCamera(holder);
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;

	}

	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();

	}

	//ɨ�������
	public void handleDecode(Result obj, Bitmap barcode) {
		AddDeviceJsonString add = null;
		inactivityTimer.onActivity();
		viewfinderView.drawResultBitmap(barcode);//�����ͼƬ
		 playBeepSoundAndVibrate();//��������Ч��
		
		String str = obj.getText();
		System.out.println("res:"+str);
		String decodeStr = "";
		try{
			decodeStr = new String(Base64.decode(str));
			System.out.println("decodeStr:"+decodeStr);
		}catch(Exception e){
			MessageUtiles.postToast(CaptureActivity.this, getResources().getString(R.string.invalid_code), 1000);
			finish();
			return;
		}
		try {
			add = parseJsonString(decodeStr);
			System.out.println("add:"+add.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			MessageUtiles.postToast(CaptureActivity.this, getResources().getString(R.string.invalid_code), 1000);
			finish();
			return;
		}
		Intent intent = new Intent(CaptureActivity.this,ChangeDeviceName.class);
		intent.putExtra("addDevice", add);
		startActivity(intent);
//		setResult(1, it);
		finish();
		
	}
	
	private AddDeviceJsonString parseJsonString(String str) throws JSONException{
		// {"id":"N0108AX3590000000000","key":"5a89207ffd46","serial_no":"N0108AX359","company":"howell"}
		JSONObject param = new JSONObject(str);
		String id = param.getString("id");
		String key = param.getString("key");
		String serial_no = param.getString("serial_no");
		String company = param.getString("company");
		
		return new AddDeviceJsonString(id, key, serial_no, company);
	}
	
	
	
//��������
	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(
					R.raw.beep);

			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(),
						file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}

	private static final long VIBRATE_DURATION = 200L;
//������������
	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	/**
	 * When the beep has finished playing, rewind to queue up another one.
	 */
	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.iv_left:
			finish();
			break;

		default:
			break;
		}
	}


}