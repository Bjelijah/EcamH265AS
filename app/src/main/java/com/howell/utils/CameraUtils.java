package com.howell.utils;

import java.util.List;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.AsyncTask;
import android.util.Log;

public class CameraUtils {
	private Camera camera;
	//private Timer timer;
	private TimerTask task;
	
	public Camera getCamera() {
		return camera;
	}

	public void setCamera(Camera camera) {
		this.camera = camera;
	}

	private void openCamera(){
		try{
			System.out.println("start open camera 1");	
			camera = Camera.open();
			System.out.println("start open camera 2");	
			camera.startPreview();
			System.out.println("start open camera 3");	
		}
		catch(RuntimeException e){
			camera = Camera.open(Camera.getNumberOfCameras()-1);
			System.out.println("open()方法有问题");
		}
	}
	
	public void stopTwinkle(){
		new Thread(){
			public void run() {
				System.out.println("start stop 1");
				if(task != null){
					System.out.println("start stop 2");
					task.cancel(true);
				}
				System.out.println("start stop 3");
				closeCamera();
			};
		}.start();
		
//		if(timer != null){
//			timer.cancel();
//			closeCamera();
//		}
	}
	
	private void closeCamera(){
		System.out.println("start close camera 1");
		if(camera != null){
			System.out.println("start close camera 2");
			camera.stopPreview();
			System.out.println("start close camera 3");
			camera.release();
			System.out.println("start close camera 4");
			camera = null;
			System.out.println("start close camera 5");
		}
	}
	
	public void twinkle(){
		
		task = new TimerTask();
		task.execute();
		//myThread.start();	
//		int num;
//		new Thread(){
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				super.run();
				//打开摄像机
//				System.out.println("start open camera");
//				openCamera();
//				System.out.println("finish open camera");
//				//闪烁 500ms亮 500ms灭
//				System.out.println("start init timer");
//				
//				while(!isThreadCancelled){
//					try {
//						turnLightOn(camera);
//						Thread.sleep(500);
//						turnLightOff(camera);
//						Thread.sleep(500);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//				timer = new Timer();//实例化Timer类
//				timer.schedule(new TimerTask(){
//					int num = 0;
//					public void run(){
//						System.out.println(num);
//						if(num % 2 == 0){
//							turnLightOn(camera);
//							System.out.println("亮");
//						}else{
//							turnLightOff(camera);
//							System.out.println("灭");
//						}
//						num++;
//					}
//				},0,500);
//			}
//		}.start();
		
	}
	
	class TimerTask extends AsyncTask<Void, Integer, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub
        	System.out.println("start open camera");
			openCamera();
			System.out.println("finish open camera");
			//闪烁 500ms亮 500ms灭
			System.out.println("start init timer");
			
			while(true){
				System.out.println("1111111");
				if(isCancelled()){
					return null;
				}
				try {
					turnLightOn(camera);
					Thread.sleep(500);
					turnLightOff(camera);
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
        }
        
        @Override
        protected void onPostExecute(Void result) {
        	// TODO Auto-generated method stub
        	super.onPostExecute(result);
        }
    }
	
//	Handler handler = new Handler(){
//		@Override
//		public void handleMessage(Message msg) {
//			// TODO Auto-generated method stub
//			super.handleMessage(msg);
//			switch (msg.what) {
//			case 1:
//				TextView tv = (TextView) msg.obj;
//				tv.setVisibility(View.VISIBLE);
//				break;
//
//			default:
//				break;
//			}
//		}
//	};
	
	/**
	 * 通过设置Camera打开闪光灯
	 * @param mCamera
	 */
	private void turnLightOn(Camera mCamera) {
		if (mCamera == null) {
			System.out.println("camera == null");
			return;
		}
		Parameters parameters = mCamera.getParameters();
		if (parameters == null) {
			System.out.println("parameters == null");
			return;
		}
		List<String> flashModes = parameters.getSupportedFlashModes();
		// Check if camera flash exists
		if (flashModes == null) {
			// Use the screen as a flashlight (next best thing)
			System.out.println("flashModes == null");
			return;
		}
		String flashMode = parameters.getFlashMode();
		Log.i("", "Flash mode: " + flashMode);
		Log.i("", "Flash modes: " + flashModes);
		if (!Parameters.FLASH_MODE_TORCH.equals(flashMode)) {
			// Turn on the flash
			System.out.println("turn on");
			if (flashModes.contains(Parameters.FLASH_MODE_TORCH)) {
				System.out.println("turn on 2");
				parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
				mCamera.setParameters(parameters);
			} else {
			}
		}
	}
	/**
	 * 通过设置Camera关闭闪光灯
	 * @param mCamera
	 */
	private void turnLightOff(Camera mCamera) {
		if (mCamera == null) {
			return;
		}
		Parameters parameters = mCamera.getParameters();
		if (parameters == null) {
			return;
		}
		List<String> flashModes = parameters.getSupportedFlashModes();
		String flashMode = parameters.getFlashMode();
		// Check if camera flash exists
		if (flashModes == null) {
			return;
		}
		Log.i("", "Flash mode: " + flashMode);
		Log.i("", "Flash modes: " + flashModes);
		if (!Parameters.FLASH_MODE_OFF.equals(flashMode)) {
			// Turn off the flash
			if (flashModes.contains(Parameters.FLASH_MODE_OFF)) {
				parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
				mCamera.setParameters(parameters);
			} else {
				Log.e("", "FLASH_MODE_OFF not supported");
			}
		}
	}
}
