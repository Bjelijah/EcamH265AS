package com.wyy.twodimcode;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.howell.ecamh265.R;

public class MainActivity extends Activity {
	TextView tv;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

//		
//		findViewById(R.id.testbtn).setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				Intent it = new Intent(MainActivity.this, CaptureActivity.class);
//				startActivityForResult(it, 1);
//			}
//		});
//		
//		tv = (TextView) findViewById(R.id.testtv);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		switch (requestCode) {
		case 1:
			if(data != null){
				String result = data.getStringExtra("result");
				if(result != null)
					tv.setText(result);
//				Uri uri = Uri.parse(result);  
//				Intent it = new Intent(Intent.ACTION_VIEW, uri);  
//				startActivity(it);
			}
			break;

		default:
			break;
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}
	

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.main, menu);
//		return true;
//	}

}
