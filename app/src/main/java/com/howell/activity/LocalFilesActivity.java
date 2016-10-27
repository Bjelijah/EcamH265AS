package com.howell.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.howell.ecamh265.R;
import com.howell.utils.PhoneConfig;
import com.howell.utils.ScaleImageUtils;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class LocalFilesActivity extends Activity {
	private ListView listview;
	private LinearLayout noImageBg;
	private ArrayList<String> mList ;
	private int imageWidth;
	private int imageHeight;
	private LinearLayout.LayoutParams lp;
	private File f;
	private MyAdapter adapter;
	private Bitmap bm;
	private Bitmap bitmapReference;
	private static final int SHOWPICTURE = 1;
	private ShowPictureHandler handler/*,handler2,handler3*/;
    private Activities mActivities;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.local_files);
    	mActivities = Activities.getInstance();
    	mActivities.addActivity("LocalFilesActivity",LocalFilesActivity.this);
    	noImageBg = (LinearLayout)findViewById(R.id.ll_loacl_file_no_image);
		imageWidth = PhoneConfig.getPhoneWidth(getApplicationContext())/3;
		imageHeight = imageWidth * 3 / 4;
		f = new File("/sdcard/eCamera");
		lp = new LinearLayout.LayoutParams(imageWidth, imageHeight);
		lp.setMargins(0, 0, 0, 10);
		listview = (ListView)findViewById(R.id.lv_localfiles);
		//mList = new ArrayList<String>();
		getFileName(f);
		if(mList.size() != 0){
			noImageBg.setVisibility(View.GONE);
		}else{
			noImageBg.setVisibility(View.VISIBLE);
		}
		handler = new ShowPictureHandler();
		adapter = new MyAdapter(this);
		listview.setAdapter(adapter);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if((bm!=null)&&(!bm.isRecycled())){
	    	bm.recycle();
	    	bm = null;
    	}
		if((bitmapReference!=null)&&(!bitmapReference.isRecycled())){
			bitmapReference.recycle();
			bitmapReference = null;
    	}
		handler = null;
		mList = null;
		if(adapter.maps != null){
			adapter.maps.clear();
			adapter.maps = null;
		}
		mActivities.removeActivity("LocalFilesActivity");
	}
	
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		System.out.println("Local Files onRestart");
		if(adapter.maps != null)
			adapter.maps.clear();
		getFileName(f);
		if(mList.size() != 0){
			noImageBg.setVisibility(View.GONE);
		}else{
			noImageBg.setVisibility(View.VISIBLE);
		}
		adapter.notifyDataSetChanged();
	}
	
	public ArrayList<String> getFileName(File file){
		File[] fileArray = file.listFiles();
		mList = new ArrayList<String>();
		for (File f : fileArray) {
			System.out.println(f.getPath());
			if(f.isFile() && !mList.contains(f.getPath())){
				mList.add(f.getPath());
			}
		}
		Collections.sort(mList, new SortByDate());
		return mList;
	}
	
	class SortByDate implements Comparator {
		public int compare(Object o1, Object o2) {
			String s1 = (String) o1;
			String s2 = (String) o2;
			return s2.compareTo(s1);
		}
	}

	class ShowPictureHandler extends Handler{
		private int position;
		private ImageView iv;
		
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case SHOWPICTURE:
				 synchronized(this){
					int requiredWidthSize = PhoneConfig.getPhoneWidth(LocalFilesActivity.this) / 3; 
					position = msg.arg1;
					iv = (ImageView)msg.obj;
					bm = ScaleImageUtils.decodeFile(requiredWidthSize,
							requiredWidthSize * 3 / 4,new File(mList.get(position)));
		            iv.setImageBitmap(bm);
		            adapter.maps.put(position, new SoftReference<Bitmap>(bm));
		            System.out.println("position:"+position);
				 }
				break;

			default:
				break;
			}
		}
	}
	
	public class MyAdapter extends BaseAdapter {
		private Context mContext;
		private Map<Integer,SoftReference<Bitmap>> maps;
		
		public MyAdapter(Context mContext) {
			super();
			this.mContext = mContext;
			this.maps = new HashMap<Integer,SoftReference<Bitmap>>();
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if(mList == null){
				return 0;
			}
			if(mList.size()%3 == 0){
				return mList.size() / 3;
			}else{
				return mList.size() / 3 + 1;
			}
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			 return mList == null ? null : mList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup arg2) {
			// TODO Auto-generated method stub
			System.out.println("getView");
			int firstImagePostion = position * 2 + position ;
			int secondImagePositon = position * 2 + position + 1;
			int thirdImagePositon = position * 2 + position + 2;
			ViewHolder holder = null;
            if (convertView == null) {
            	LayoutInflater layoutInflater = LayoutInflater.from(mContext);
				convertView = layoutInflater.inflate(R.layout.localfile_item, null);
				holder = new ViewHolder();
				
				holder.iv1 = (ImageView)convertView.findViewById(R.id.imageView1);
				holder.iv2 = (ImageView)convertView.findViewById(R.id.imageView2);
				holder.iv3 = (ImageView)convertView.findViewById(R.id.imageView3);
//				holder.imageTtile = (TextView)convertView.findViewById(R.id.local_image_title);
				
				holder.iv1.setLayoutParams(lp);
				holder.iv2.setLayoutParams(lp);
				holder.iv3.setLayoutParams(lp);
				
				holder.iv1.setOnClickListener(listener);
				holder.iv2.setOnClickListener(listener);
				holder.iv3.setOnClickListener(listener);
				
//				holder.iv1.setOnLongClickListener(longClickListener);
//				holder.iv2.setOnLongClickListener(longClickListener);
//				holder.iv3.setOnLongClickListener(longClickListener);
				
				convertView.setTag(holder);
            }else{
            	holder = (ViewHolder)convertView.getTag();
            	
            }
            
//            String date = mList.get(firstImagePostion);
//            System.out.println("date:"+date);
//            
//            holder.imageTtile.setText(date.subSequence(16, 20)+"-"+date.substring(20, 22)+"-"+
//            date.substring(22, 24));
            
            if(firstImagePostion == mList.size()){
            	holder.iv1.setVisibility(View.GONE);
            	holder.iv2.setVisibility(View.GONE);
	        	holder.iv3.setVisibility(View.GONE);
            	return convertView;
            }else{
            	holder.iv1.setVisibility(View.VISIBLE);
            	holder.iv2.setVisibility(View.VISIBLE);
            	holder.iv3.setVisibility(View.VISIBLE);
            }
            
            holder.iv1.setTag(firstImagePostion);
            if(!maps.containsKey(firstImagePostion)){
            	Message msg = new Message();
        		msg.what = SHOWPICTURE;
        		msg.obj = holder.iv1;
        		msg.arg1 = firstImagePostion;
        		handler.sendMessage(msg);
            }else{
            	SoftReference<Bitmap> reference = maps.get(firstImagePostion);  
                bitmapReference = reference.get();  
                if(bitmapReference != null)
                	holder.iv1.setImageBitmap(bitmapReference);
                else{
                	Message msg = new Message();
            		msg.what = SHOWPICTURE;
            		msg.obj = holder.iv1;
            		msg.arg1 = firstImagePostion;
                	handler.sendMessage(msg);
                }
            }
            System.out.println(mList.get(firstImagePostion)+","+firstImagePostion);
            
	        if(secondImagePositon == mList.size()){
	        	holder.iv2.setVisibility(View.GONE);
	        	holder.iv3.setVisibility(View.GONE);
            	return convertView;
            }else{
            	holder.iv2.setVisibility(View.VISIBLE);
            	holder.iv3.setVisibility(View.VISIBLE);
            }
	        
	        holder.iv2.setTag(secondImagePositon);
	        if(!maps.containsKey(secondImagePositon)){
	        	Message msg = new Message();
        		msg.what = SHOWPICTURE;
        		msg.obj = holder.iv2;
        		msg.arg1 = secondImagePositon;
            	handler.sendMessage(msg);
            }else{
            	SoftReference<Bitmap> reference = maps.get(secondImagePositon);  
                bitmapReference = reference.get();  
                if(bitmapReference != null)
                	holder.iv2.setImageBitmap(bitmapReference);
                else{
                	Message msg = new Message();
            		msg.what = SHOWPICTURE;
            		msg.obj = holder.iv2;
            		msg.arg1 = secondImagePositon;
                	handler.sendMessage(msg);
                }
            }
	        
	        System.out.println(mList.get(secondImagePositon)+","+secondImagePositon);
            if(thirdImagePositon == mList.size()){
            	holder.iv3.setVisibility(View.GONE);
            	return convertView;
            }else{
            	holder.iv3.setVisibility(View.VISIBLE);
            }
            holder.iv3.setTag(thirdImagePositon);
            if(!maps.containsKey(thirdImagePositon)){
            	Message msg = new Message();
        		msg.what = SHOWPICTURE;
        		msg.obj = holder.iv3;
        		msg.arg1 = thirdImagePositon;
            	handler.sendMessage(msg);
            }else{
            	SoftReference<Bitmap> reference = maps.get(thirdImagePositon);  
                bitmapReference = reference.get();  
                if(bitmapReference != null)
                	holder.iv3.setImageBitmap(bitmapReference);
                else{
                	Message msg = new Message();
            		msg.what = SHOWPICTURE;
            		msg.obj = holder.iv3;
            		msg.arg1 = thirdImagePositon;
                	handler.sendMessage(msg);
                }
            }
            System.out.println(mList.get(thirdImagePositon)+","+thirdImagePositon);
			return convertView;
		}

	}
	
	public static class ViewHolder {
		public ImageView iv1,iv2,iv3;
		public TextView imageTtile;
	}
	
	private OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View view) {
			// TODO Auto-generated method stub
			System.out.println(view.getTag());
			Intent intent = new Intent(LocalFilesActivity.this, BigImages.class);
			intent.putExtra("position", Integer.valueOf(view.getTag().toString()));
			intent.putStringArrayListExtra("arrayList", mList);
	        startActivity(intent);  
	        overridePendingTransition(R.anim.zoomin, R.anim.zoomout);  
		}
		
	};
	
//	private OnLongClickListener longClickListener = new OnLongClickListener() {
//
//		@Override
//		public boolean onLongClick(View view) {
//			// TODO Auto-generated method stub
//			System.out.println("picture:"+mList.get(Integer.valueOf(view.getTag().toString())));
//			deleteImage(new File(mList.get(Integer.valueOf(view.getTag().toString()))));
//			mList.remove(mList.get(Integer.valueOf(view.getTag().toString())));
//			adapter.notifyDataSetChanged();
//			return false;
//		}
//	};
	
}
