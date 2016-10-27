package com.howell.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.howell.broadcastreceiver.HomeKeyEventBroadCastReceiver;
import com.howell.ecamh265.R;
import com.howell.ehlib.HackyViewPager;
import com.howell.utils.FileUtils;
import com.howell.utils.PhoneConfig;
import com.howell.utils.ScaleImageUtils;

import java.io.File;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher.OnViewTapListener;


public class BigImages extends Activity implements OnClickListener,OnPageChangeListener,OnViewTapListener{
	//LocalFilesActivity传过来的照片集合下标，用于查找当前用户点击查看的是具体那张照片
	//position根据onPageSelected方法监听用户滑动照片而改变
	private int position;
	//LocalFilesActivity传过来的照片地址集合
	private ArrayList<String> mList;

	//isShown用于判断按钮是否显示的标志位
	//isScale用于判断图片是否改变分辨率的标志位
	private boolean isShown,isScale;

	private ImageButton mShare,mBack,mDelete,mScale;
	private FrameLayout title,bottom;
	private TextView mImagePosition;
	private HackyViewPager viewPager;
	private SamplePagerAdapter adapter;

	//单例，用于存放所有已打开的activity，便于按下home键后finish所有栈内的activity
	private Activities mActivities;
	//用于监听home键
	private HomeKeyEventBroadCastReceiver receiver;


	private	ImageView im;//fixme

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.big_images);

		//添加BigImages Activity到Activities单例中
		mActivities = Activities.getInstance();
		mActivities.addActivity("BigImages",BigImages.this);
		//注册广播
		receiver = new HomeKeyEventBroadCastReceiver();
		registerReceiver(receiver, new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));

		Intent intent = getIntent();
		position = intent.getIntExtra("position", 0);
		System.out.println("position:"+position);
		mList = intent.getStringArrayListExtra("arrayList");

		isShown = true;
		isScale = true;

		mShare = (ImageButton)findViewById(R.id.ib_share);
		mBack = (ImageButton)findViewById(R.id.ib_bigimage_back);
		title = (FrameLayout)findViewById(R.id.fl_title);
		bottom = (FrameLayout)findViewById(R.id.fl_bottom);
		mDelete = (ImageButton)findViewById(R.id.ib_delete);
		mScale = (ImageButton)findViewById(R.id.ib_scale);
		mShare.setOnClickListener(this);
		mBack.setOnClickListener(this);
		title.setOnClickListener(this);
		mDelete.setOnClickListener(this);
		mScale.setOnClickListener(this);

		mImagePosition = (TextView)findViewById(R.id.tv_bigimage_position);
		mImagePosition.setText((position+1) + "/" + mList.size());

		viewPager = (HackyViewPager) findViewById(R.id.viewPager);
		try{
			adapter = new SamplePagerAdapter();
		}catch(OutOfMemoryError e){
			System.out.println("OutOfMemory");
		}

		viewPager.setAdapter(adapter);

		viewPager.setCurrentItem(position);

		viewPager.setOnPageChangeListener(this);


//        im = (ImageView)findViewById(R.id.imageView1_iv);

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		//把Activity从单例中移除
		mActivities.removeActivity("BigImages");
		//注销广播
		unregisterReceiver(receiver);
		mList = null;
	}

	class SamplePagerAdapter extends PagerAdapter {
		private boolean scale;

		public SamplePagerAdapter() {
			super();
			scale = true;
		}

		public boolean isScale() {
			return scale;
		}

		public void setScale(boolean scale) {
			this.scale = scale;
		}

		@Override
		public int getItemPosition(Object object) {
			// TODO Auto-generated method stub
			return POSITION_NONE;
		}

		@Override
		public int getCount() {
			return /*sDrawables.length*/mList.size();
		}

		@Override
		public View instantiateItem(ViewGroup container, int position) {

			System.out.println("instatiateItem position:"+position);
			//获取手机屏幕宽度
			int requiredWidthSize = PhoneConfig.getPhoneWidth(BigImages.this);
			PhotoView photoView = new PhotoView(container.getContext());
			Date d = new Date(new File(mList.get(position)).lastModified());
			SimpleDateFormat foo = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			System.out.println("最后修改时间："+foo.format(d));
			System.out.println("最后修改时间："+new File(mList.get(position)).lastModified());
			Log.i("123", "view:"+position);
			if(scale){
				Log.e("123", "scale  view:"+position+"file name:"+mList.get(position));
//				Bitmap bm = ScaleImageUtils.resizeImage(ScaleImageUtils.decodeFile(requiredWidthSize,requiredWidthSize * 9 / 16
//						,new File(mList.get(1))),requiredWidthSize , requiredWidthSize * 9 / 16);
				photoView.setImageBitmap(/*sDrawables[position]*/ScaleImageUtils.resizeImage(ScaleImageUtils.decodeFile(requiredWidthSize,requiredWidthSize * 9 / 16
						,new File(mList.get(position))),requiredWidthSize , requiredWidthSize * 9 / 16));


//				im.setImageBitmap(bm);




			}else{
				Log.e("123", "no scale  view:"+position);
				photoView.setImageBitmap(ScaleImageUtils.decodeFile(requiredWidthSize,requiredWidthSize * 3 / 4
						,new File(mList.get(position))));
			}
			//注册点击事件
			photoView.setOnViewTapListener(BigImages.this);
			// Now just add PhotoView to ViewPager and return it
			container.addView(photoView, LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT);
			photoView.setTag(position);
			return photoView;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.ib_scale:
			{
				if(!isScale){
					isScale = true;
					mScale.setImageResource(R.mipmap.icon_scale_16_9);
				}else{
					isScale = false;
					mScale.setImageResource(R.mipmap.icon_scale_4_3);
				}
				adapter.setScale(isScale);
				adapter.notifyDataSetChanged();
				break;
			}
			case R.id.ib_share:
			{
				Intent sharingIntent = new Intent(Intent.ACTION_SEND);
				Uri screenshotUri = Uri.parse("file://"+mList.get(position));
				sharingIntent.setType("image/jpeg");
				sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
				startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_pic)));
				break;
			}
			case R.id.ib_bigimage_back:
				BigImages.this.finish();
				break;
			case R.id.ib_delete:
			{
				Dialog alertDialog = new AlertDialog.Builder(BigImages.this).
						setTitle(getResources().getString(R.string.big_image_activity_dialog_title_remove)).
						setMessage(getResources().getString(R.string.big_image_activity_dialog_message)).
						setIcon(R.drawable.expander_ic_minimized).
						setPositiveButton(getResources().getString(R.string.big_image_activity_dialog_yes_button_name), new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								FileUtils.deleteImage(new File(mList.get(position)));
								mList.remove(position);
								mImagePosition.setText((position+1) + "/" + mList.size());
								adapter.notifyDataSetChanged();
							}
						}).
						setNegativeButton(getResources().getString(R.string.big_image_activity_dialog_no_button_name), new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								// TODO Auto-generated method stub

							}
						}).
						create();
				alertDialog.show();
				break;
			}
			default:
				break;
		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onPageSelected(int position) {
		// TODO Auto-generated method stub
		Log.i("123", "onPageSelected  :"+ position);
		this.position = position;
		mImagePosition.setText((position+1) + "/" + mList.size());

//		for(int i =0;i<mList.size();i++){
//			Log.i("123", "mlist "+i+ mList.get(i).toString());
//		}

	}

	@Override
	public void onViewTap(View view, float x, float y) {
		// TODO Auto-generated method stub
		if(isShown){
			title.setVisibility(View.INVISIBLE);
			bottom.setVisibility(View.INVISIBLE);
			isShown = false;
		}else{
			title.setVisibility(View.VISIBLE);
			bottom.setVisibility(View.VISIBLE);
			isShown = true;
		}
	}
}
