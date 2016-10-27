package com.xququ.OfflineDemo;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.howell.ecamh265.R;
import com.xququ.OfflineSDK.XQuquerService;
import com.xququ.OfflineSDK.XQuquerService.XQuquerListener;

public class QuquerActivity extends Activity implements OnClickListener, XQuquerListener
{
	final String TAG = "XQuquerDemo";
	
	private XQuquerService xququerService;
	
	private EditText txtMessage;
	private Button butSend;
	private Button butStop;
	public  AudioManager audiomanage;  
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ququer);
		audiomanage = (AudioManager)getSystemService(Context.AUDIO_SERVICE); 
	    int maxVolume = audiomanage.getStreamMaxVolume(AudioManager.STREAM_MUSIC);  
	    audiomanage.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume - 1 , 0);
		txtMessage = (EditText)this.findViewById(R.id.Message);
		butSend = (Button)this.findViewById(R.id.Send);
		butStop = (Button)this.findViewById(R.id.Stop);

		
		butSend.setOnClickListener(this);
		butStop.setOnClickListener(this);
		
		xququerService = XQuquerService.getInstance();
		
		txtMessage.setText("Hello World !");
	}
	
	@Override
	protected void onStart()
	{
		super.onStart();
		xququerService.start(this);		
		Log.i(TAG, "onStart");
	}
	
	@Override
	protected void onStop()
	{
		super.onStop();
		xququerService.stop();	

		Log.i(TAG, "onStop");
	}

	@Override
	public void onClick(View sender)
	{
		int id = sender.getId();
		if(id==R.id.Send)
		{
			send();
		}
		else if(id==R.id.Stop)
		{
			stop();
		}
	}
	
	private void send()
	{
		byte[] data = txtMessage.getText().toString().getBytes();
		if(data.length>0) xququerService.sendData(data, 0.5f);  //0.0 ~ 1.0
	}
	
	private void stop()
	{
		xququerService.stopSend();
	}

	@Override
	public void onSend()
	{
		Log.i(TAG, "onSend");
		Toast.makeText(this, "���ͳɹ�", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onRecv(byte[] data)
	{
		String message = new String(data);
		Log.i(TAG, "onRecv:"+message);
		Toast.makeText(this, "���ճɹ�", Toast.LENGTH_LONG).show();
		txtMessage.setText(message);
	}
}
