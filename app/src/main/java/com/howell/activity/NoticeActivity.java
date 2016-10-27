package com.howell.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.howell.ecamh265.R;
import com.howell.ehlib.MyListViewWithFoot;
import com.howell.ehlib.MyListViewWithFoot.OnRefreshListener;
import com.howell.protocol.GetPictureReq;
import com.howell.protocol.GetPictureRes;
import com.howell.protocol.NoticeList;
import com.howell.protocol.QueryNoticesRes;
import com.howell.protocol.SoapManager;
import com.howell.utils.MessageUtiles;
import com.howell.utils.NoticePagingUtils;
import com.howell.utils.PhoneConfig;
import com.howell.utils.SDCardUtils;
import com.howell.utils.ScaleImageUtils;

import org.kobjects.base64.Base64;

import java.util.ArrayList;
/**
 * @author 霍之昊 
 *
 * 类说明
 */
public class NoticeActivity extends Activity implements OnRefreshListener,OnClickListener{ 
	private MyListViewWithFoot listview;
//	private ImageButton mBack;
	private ArrayList<NoticeList> list;
	private SoapManager mSoapManager;
	private NoticeAdapter adapter;
	int phoneWidth,requiredWidthSize; 
	private NoticePagingUtils pageUtils;
	
	private String HD = "HD";
	
	private static final int ONFIRSTREFRESHDOWN = 1;
	private static final int NO_MORE_NOTICE = 2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notice_activity);

		init();
		Log.e("", SDCardUtils.freeSpaceOnSd()+"");
	}
	
	private void init(){
		SDCardUtils.createBitmapDir();
		//获取手机宽度
		phoneWidth = PhoneConfig.getPhoneWidth(NoticeActivity.this); 
		//设置通知图片宽度
		requiredWidthSize = ( phoneWidth - 10 ) / 3;
		
		mSoapManager = SoapManager.getInstance();
		SoapManager.context = this;
//		mBack = (ImageButton)findViewById(R.id.ib_notice_account_back);
//		mBack.setOnClickListener(this);
		
		pageUtils = new NoticePagingUtils();
		
		listview = (MyListViewWithFoot)findViewById(R.id.notice_listview);
		listview.setonRefreshListener(this);
		adapter = new NoticeAdapter(this);
		listview.setAdapter(adapter);
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		pageUtils.clearResource();
		QueryNoticesRes res = pageUtils.getQueryNotices();
		if(res == null){
			list = new ArrayList<NoticeList>();
//			handler.sendEmptyMessage(ONFIRSTREFRESHDOWN);
			handler.sendEmptyMessage(NO_MORE_NOTICE);
		}else if(res.getResult().equals("OK")){
			list = res.getNodeList();
			System.out.println("大小："+list.size());
//			handler.sendEmptyMessage(ONFIRSTREFRESHDOWN);
		}
		handler.sendEmptyMessage(ONFIRSTREFRESHDOWN);
	}

	@Override
	public void onFirstRefresh() {
		// TODO Auto-generated method stub
		QueryNoticesRes res = pageUtils.getQueryNotices();
		list = new ArrayList<NoticeList>();
		if(res == null){
//			handler.sendEmptyMessage(ONFIRSTREFRESHDOWN);
			handler.sendEmptyMessage(NO_MORE_NOTICE);
		}else if(res.getResult().equals("OK")){
			list = res.getNodeList();
			//System.out.println("大小："+list.size());
//			handler.sendEmptyMessage(ONFIRSTREFRESHDOWN);
		}
		handler.sendEmptyMessage(ONFIRSTREFRESHDOWN);
	}
	
	@Override
	public void onFootRefresh() {
		// TODO Auto-generated method stub
		new AsyncTask<Void, Void, Void>() {
			int position = 0;
			QueryNoticesRes res = null;
			protected Void doInBackground(Void... params) {
				position = list.size();
				res = pageUtils.getQueryNotices();
				if(res != null){
					list.addAll(res.getNodeList());
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				if(res == null){
					handler.sendEmptyMessage(NO_MORE_NOTICE);
				}
				adapter.notifyDataSetChanged();
				listview.onFootRefreshComplete();
				listview.setSelection(position);
			}

		}.execute();
	}
	
	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch(msg.what){
			case ONFIRSTREFRESHDOWN:
				listview.onRefreshComplete();
				adapter.notifyDataSetChanged();
				break;
			case NO_MORE_NOTICE:
				MessageUtiles.postToast(NoticeActivity.this, "没有更多消息", 1000);
				break;
			}
		}
	};
	
	class ShowImagesTask extends AsyncTask<Void, Integer, Void> {
		private ImageView iv1,iv2,iv3,iv4;
		private GetPictureRes res = null,res2 = null,res3 = null, res4 = null;
		NoticeList notice;
		public ShowImagesTask(ImageView iv1, ImageView iv2, ImageView iv3,
				ImageView iv4, int position) {
			super();
			this.iv1 = iv1;
			this.iv2 = iv2;
			this.iv3 = iv3;
			this.iv4 = iv4;
			notice = list.get(position);
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			GetPictureReq req ,req2 ,req3,req4;
			Log.e("123","size:"+notice.getPictureID().size());
			
			switch(notice.getPictureID().size()){
			case 0:	
				break;
			case 1:
				if(!SDCardUtils.isBitmapExist(notice.getPictureID().get(0))){
					req = new GetPictureReq(mSoapManager.getLoginResponse().getAccount(),mSoapManager.getLoginResponse().getLoginSession(),notice.getPictureID().get(0));
					res = mSoapManager.getGetPictureRes(req);
				}else{
					Log.i("123", ""+notice.getPictureID().get(0));
				}
				break;
			case 2:
				if(!SDCardUtils.isBitmapExist(notice.getPictureID().get(0))){
					req = new GetPictureReq(mSoapManager.getLoginResponse().getAccount(),mSoapManager.getLoginResponse().getLoginSession(),notice.getPictureID().get(0));
					res = mSoapManager.getGetPictureRes(req);
				}
				if(!SDCardUtils.isBitmapExist(notice.getPictureID().get(1))){
					req2 = new GetPictureReq(mSoapManager.getLoginResponse().getAccount(),mSoapManager.getLoginResponse().getLoginSession(),notice.getPictureID().get(1));
					res2 = mSoapManager.getGetPictureRes(req2);
				}
				break;
			
			case 3:
				if(!SDCardUtils.isBitmapExist(notice.getPictureID().get(0))){
					req = new GetPictureReq(mSoapManager.getLoginResponse().getAccount(),mSoapManager.getLoginResponse().getLoginSession(),notice.getPictureID().get(0));
					res = mSoapManager.getGetPictureRes(req);
				}
				if(!SDCardUtils.isBitmapExist(notice.getPictureID().get(1))){
					req2 = new GetPictureReq(mSoapManager.getLoginResponse().getAccount(),mSoapManager.getLoginResponse().getLoginSession(),notice.getPictureID().get(1));
					res2 = mSoapManager.getGetPictureRes(req2);
				}
				if(!SDCardUtils.isBitmapExist(notice.getPictureID().get(2))){
					req3 = new GetPictureReq(mSoapManager.getLoginResponse().getAccount(),mSoapManager.getLoginResponse().getLoginSession(),notice.getPictureID().get(2));
					res3 = mSoapManager.getGetPictureRes(req3);
				}
				break;
			case 4:
				if(!SDCardUtils.isBitmapExist(notice.getPictureID().get(0))){
					req = new GetPictureReq(mSoapManager.getLoginResponse().getAccount(),mSoapManager.getLoginResponse().getLoginSession(),notice.getPictureID().get(0));
					res = mSoapManager.getGetPictureRes(req);
				}
				if(!SDCardUtils.isBitmapExist(notice.getPictureID().get(1))){
					req2 = new GetPictureReq(mSoapManager.getLoginResponse().getAccount(),mSoapManager.getLoginResponse().getLoginSession(),notice.getPictureID().get(1));
					res2 = mSoapManager.getGetPictureRes(req2);
				}
				if(!SDCardUtils.isBitmapExist(notice.getPictureID().get(2))){
					req3 = new GetPictureReq(mSoapManager.getLoginResponse().getAccount(),mSoapManager.getLoginResponse().getLoginSession(),notice.getPictureID().get(2));
					res3 = mSoapManager.getGetPictureRes(req3);
				}
				if(!SDCardUtils.isBitmapExist(notice.getPictureID().get(3))){
					req4 = new GetPictureReq(mSoapManager.getLoginResponse().getAccount(),mSoapManager.getLoginResponse().getLoginSession(),notice.getPictureID().get(3));
					res4 = mSoapManager.getGetPictureRes(req4);
				}
				
				break;
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			Bitmap bm = null;
			switch(notice.getPictureID().size()){
			case 0:	
				break;
			case 1:
				if(res != null && res.getResult() != null && res.getResult().equals("OK")){
					bm = ScaleImageUtils.decodeByteArray(requiredWidthSize, requiredWidthSize, Base64.decode(res.getPicture()));
					iv1.setImageBitmap(bm);
					SDCardUtils.saveBmpToSd(bm,notice.getPictureID().get(0));
					
					SDCardUtils.saveBmpToSd(BitmapFactory.decodeByteArray(Base64.decode(res.getPicture()), 0, Base64.decode(res.getPicture()).length),notice.getPictureID().get(0)+HD);
				}else if(res == null && SDCardUtils.isBitmapExist(notice.getPictureID().get(0))){
					iv1.setImageBitmap(BitmapFactory.decodeFile(SDCardUtils.getBitmapCachePath()+notice.getPictureID().get(0)));
				}
				break;
			case 2:
				if(res != null && res.getResult() != null && res.getResult().equals("OK")){
					bm = ScaleImageUtils.decodeByteArray(requiredWidthSize, requiredWidthSize, Base64.decode(res.getPicture()));
					iv1.setImageBitmap(bm);
					SDCardUtils.saveBmpToSd(bm,notice.getPictureID().get(0));
					SDCardUtils.saveBmpToSd(BitmapFactory.decodeByteArray(Base64.decode(res.getPicture()), 0, Base64.decode(res.getPicture()).length),notice.getPictureID().get(0)+HD);
				}else if(res == null && SDCardUtils.isBitmapExist(notice.getPictureID().get(0))){
					iv1.setImageBitmap(BitmapFactory.decodeFile(SDCardUtils.getBitmapCachePath()+notice.getPictureID().get(0)));
				}
				if(res2 != null && res2.getResult() != null && res2.getResult().equals("OK")){
					bm = ScaleImageUtils.decodeByteArray(requiredWidthSize, requiredWidthSize, Base64.decode(res2.getPicture()));
					iv2.setImageBitmap(bm);
					SDCardUtils.saveBmpToSd(bm,notice.getPictureID().get(1));
					SDCardUtils.saveBmpToSd(BitmapFactory.decodeByteArray(Base64.decode(res2.getPicture()), 0, Base64.decode(res2.getPicture()).length),notice.getPictureID().get(1)+HD);
					
				}else if(res2 == null && SDCardUtils.isBitmapExist(notice.getPictureID().get(1))){
					iv2.setImageBitmap(BitmapFactory.decodeFile(SDCardUtils.getBitmapCachePath()+notice.getPictureID().get(1)));
				}
				Log.i("123", "1  "+notice.getPictureID().get(0));
				Log.i("123", "2  "+notice.getPictureID().get(1));
				
				break;
			case 3:
				if(res != null && res.getResult() != null && res.getResult().equals("OK")){
					bm = ScaleImageUtils.decodeByteArray(requiredWidthSize, requiredWidthSize, Base64.decode(res.getPicture()));
					iv1.setImageBitmap(bm);
					SDCardUtils.saveBmpToSd(bm,notice.getPictureID().get(0));
					
					SDCardUtils.saveBmpToSd(BitmapFactory.decodeByteArray(Base64.decode(res.getPicture()), 0, Base64.decode(res.getPicture()).length),notice.getPictureID().get(0)+HD);
				}else if(res == null && SDCardUtils.isBitmapExist(notice.getPictureID().get(0))){
					iv1.setImageBitmap(BitmapFactory.decodeFile(SDCardUtils.getBitmapCachePath()+notice.getPictureID().get(0)));
				}
				if(res2 != null && res2.getResult() != null && res2.getResult().equals("OK")){
					bm = ScaleImageUtils.decodeByteArray(requiredWidthSize, requiredWidthSize, Base64.decode(res2.getPicture()));
					iv2.setImageBitmap(bm);
					SDCardUtils.saveBmpToSd(bm,notice.getPictureID().get(1));
					
					SDCardUtils.saveBmpToSd(BitmapFactory.decodeByteArray(Base64.decode(res2.getPicture()), 0, Base64.decode(res2.getPicture()).length),notice.getPictureID().get(1)+HD);
				}else if(res2 == null && SDCardUtils.isBitmapExist(notice.getPictureID().get(1))){
					iv2.setImageBitmap(BitmapFactory.decodeFile(SDCardUtils.getBitmapCachePath()+notice.getPictureID().get(1)));
				}
				if(res3 != null && res3.getResult() != null && res3.getResult().equals("OK")){
					bm = ScaleImageUtils.decodeByteArray(requiredWidthSize, requiredWidthSize, Base64.decode(res3.getPicture()));
					iv3.setImageBitmap(bm);
					SDCardUtils.saveBmpToSd(bm,notice.getPictureID().get(2));
					
					SDCardUtils.saveBmpToSd(BitmapFactory.decodeByteArray(Base64.decode(res3.getPicture()), 0, Base64.decode(res3.getPicture()).length),notice.getPictureID().get(2)+HD);
				}else if(res3 == null && SDCardUtils.isBitmapExist(notice.getPictureID().get(2))){
					iv3.setImageBitmap(BitmapFactory.decodeFile(SDCardUtils.getBitmapCachePath()+notice.getPictureID().get(2)));
				}
			case 4:
				if(res != null && res.getResult() != null && res.getResult().equals("OK")){
					bm = ScaleImageUtils.decodeByteArray(requiredWidthSize, requiredWidthSize, Base64.decode(res.getPicture()));
					iv1.setImageBitmap(bm);
					//存缩略图
					SDCardUtils.saveBmpToSd(bm,notice.getPictureID().get(0));
					//存原图
					SDCardUtils.saveBmpToSd(BitmapFactory.decodeByteArray(Base64.decode(res.getPicture()), 0, Base64.decode(res.getPicture()).length),notice.getPictureID().get(0)+HD);
				}else if(res == null && SDCardUtils.isBitmapExist(notice.getPictureID().get(0))){
					iv1.setImageBitmap(BitmapFactory.decodeFile(SDCardUtils.getBitmapCachePath()+notice.getPictureID().get(0)));
				}
				if(res2 != null && res2.getResult() != null && res2.getResult().equals("OK")){
					bm = ScaleImageUtils.decodeByteArray(requiredWidthSize, requiredWidthSize, Base64.decode(res2.getPicture()));
					iv2.setImageBitmap(bm);
					SDCardUtils.saveBmpToSd(bm,notice.getPictureID().get(1));
					
					SDCardUtils.saveBmpToSd(BitmapFactory.decodeByteArray(Base64.decode(res2.getPicture()), 0, Base64.decode(res2.getPicture()).length),notice.getPictureID().get(1)+HD);
				}else if(res2 == null && SDCardUtils.isBitmapExist(notice.getPictureID().get(1))){
					iv2.setImageBitmap(BitmapFactory.decodeFile(SDCardUtils.getBitmapCachePath()+notice.getPictureID().get(1)));
				}
				if(res3 != null && res3.getResult() != null && res3.getResult().equals("OK")){
					bm = ScaleImageUtils.decodeByteArray(requiredWidthSize, requiredWidthSize, Base64.decode(res3.getPicture()));
					iv3.setImageBitmap(bm);
					SDCardUtils.saveBmpToSd(bm,notice.getPictureID().get(2));
					
					SDCardUtils.saveBmpToSd(BitmapFactory.decodeByteArray(Base64.decode(res3.getPicture()), 0, Base64.decode(res3.getPicture()).length),notice.getPictureID().get(2)+HD);
				}else if(res3 == null && SDCardUtils.isBitmapExist(notice.getPictureID().get(2))){
					iv3.setImageBitmap(BitmapFactory.decodeFile(SDCardUtils.getBitmapCachePath()+notice.getPictureID().get(2)));
				}
				if(res4 != null && res4.getResult() != null && res4.getResult().equals("OK")){
					bm = ScaleImageUtils.decodeByteArray(requiredWidthSize, requiredWidthSize, Base64.decode(res4.getPicture()));
					iv4.setImageBitmap(bm);
					SDCardUtils.saveBmpToSd(bm,notice.getPictureID().get(3));
					
					SDCardUtils.saveBmpToSd(BitmapFactory.decodeByteArray(Base64.decode(res4.getPicture()), 0, Base64.decode(res4.getPicture()).length),notice.getPictureID().get(3)+HD);
				}else if(res4 == null && SDCardUtils.isBitmapExist(notice.getPictureID().get(3))){
					iv4.setImageBitmap(BitmapFactory.decodeFile(SDCardUtils.getBitmapCachePath()+notice.getPictureID().get(3)));
				}
				break;
			}
			
		}
	}
	
    public class NoticeAdapter extends BaseAdapter {
    	private LinearLayout.LayoutParams lp;
        private Context mContext;

        public NoticeAdapter(Context context) {
            mContext = context;
    		lp = new LinearLayout.LayoutParams(requiredWidthSize, requiredWidthSize);
    		lp.setMargins(5, 5, 0, 0);
    		
        }
        
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return list == null ? 0 : list.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return list == null ? null : list.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
        	System.out.println("getView:"+position);
        	ViewHolder holder = null;
            if (convertView == null) {
            	LayoutInflater layoutInflater = LayoutInflater.from(mContext);
				convertView = layoutInflater.inflate(R.layout.notice_item, null);
				holder = new ViewHolder();
				holder.title = (TextView)convertView.findViewById(R.id.notice_item_title);
				holder.message = (TextView)convertView.findViewById(R.id.notice_item_message);
				holder.time = (TextView)convertView.findViewById(R.id.notice_item_time);
				
				holder.iv1 = (ImageView)convertView.findViewById(R.id.notice_item_imageView1);
				holder.iv2 = (ImageView)convertView.findViewById(R.id.notice_item_imageView2);
				holder.iv3 = (ImageView)convertView.findViewById(R.id.notice_item_imageView3);
				holder.iv4 = (ImageView)convertView.findViewById(R.id.notice_item_imageView4);
				
				holder.iv1.setLayoutParams(lp);
				holder.iv2.setLayoutParams(lp);
				holder.iv3.setLayoutParams(lp);
				holder.iv4.setLayoutParams(lp);
				
				holder.iv1.setOnClickListener(NoticeActivity.this);
				holder.iv2.setOnClickListener(NoticeActivity.this);
				holder.iv3.setOnClickListener(NoticeActivity.this);
				holder.iv4.setOnClickListener(NoticeActivity.this);
				
                convertView.setTag(holder);
                
            }else{
            	holder = (ViewHolder)convertView.getTag();
            }
            NoticeList notice = list.get(position);
            holder.title.setText(notice.getName());
            holder.message.setText(notice.getMessage());
            holder.time.setText(notice.getTime().substring(0, 10)+" "+notice.getTime().substring(11,19));
            
            holder.iv1.setTag(position);
            holder.iv2.setTag(position);
            holder.iv3.setTag(position);
            holder.iv4.setTag(position);
            
            switch(notice.getPictureID().size()){
			case 0:	
				holder.iv1.setVisibility(View.GONE);
				holder.iv2.setVisibility(View.GONE);
				holder.iv3.setVisibility(View.GONE);
				holder.iv4.setVisibility(View.GONE);
				break;
			case 1:
				holder.iv1.setVisibility(View.VISIBLE);
				holder.iv1.setImageDrawable(getResources().getDrawable(R.mipmap.empty_photo));
				holder.iv2.setVisibility(View.GONE);
				holder.iv3.setVisibility(View.GONE);
				holder.iv4.setVisibility(View.GONE);
				break;
			case 2:
				holder.iv1.setVisibility(View.VISIBLE);
				holder.iv1.setImageDrawable(getResources().getDrawable(R.mipmap.empty_photo));
				holder.iv2.setVisibility(View.VISIBLE);
				holder.iv2.setImageDrawable(getResources().getDrawable(R.mipmap.empty_photo));
				holder.iv3.setVisibility(View.GONE);
				holder.iv4.setVisibility(View.GONE);
				break;
			
			case 3:
				holder.iv1.setVisibility(View.VISIBLE);
				holder.iv1.setImageDrawable(getResources().getDrawable(R.mipmap.empty_photo));
				holder.iv2.setVisibility(View.VISIBLE);
				holder.iv2.setImageDrawable(getResources().getDrawable(R.mipmap.empty_photo));
				holder.iv3.setVisibility(View.VISIBLE);
				holder.iv3.setImageDrawable(getResources().getDrawable(R.mipmap.empty_photo));
				holder.iv4.setVisibility(View.GONE);
				break;
			case 4:
				holder.iv1.setVisibility(View.VISIBLE);
				holder.iv1.setImageDrawable(getResources().getDrawable(R.mipmap.empty_photo));
				holder.iv2.setVisibility(View.VISIBLE);
				holder.iv2.setImageDrawable(getResources().getDrawable(R.mipmap.empty_photo));
				holder.iv3.setVisibility(View.VISIBLE);
				holder.iv3.setImageDrawable(getResources().getDrawable(R.mipmap.empty_photo));
				holder.iv4.setVisibility(View.VISIBLE);
				holder.iv4.setImageDrawable(getResources().getDrawable(R.mipmap.empty_photo));
				break;
			}
            //if(notice.getPictureID().size() > 0){
            ShowImagesTask task = new ShowImagesTask(holder.iv1, holder.iv2, holder.iv3, holder.iv4, position);
            task.execute();
            //}
			return convertView;
        }
    }
    
	public class ViewHolder {
		public ImageView iv1,iv2,iv3,iv4;
		public TextView title,message,time;
	}
	
	private ArrayList<String> getPictures(int position){
		ArrayList<String> pathList = new ArrayList<String>();
		for(int i = 0 ; i < list.get(position).getPictureID().size() ; i++){
			if(SDCardUtils.isBitmapExist(list.get(position).getPictureID().get(i)+HD))
				pathList.add(SDCardUtils.getBitmapCachePath()+list.get(position).getPictureID().get(i)+HD);
		}
		
	
		
		
		return pathList;
	}
	
	private void clickPiture(View v,int which){
		
		
		
		
		Intent intent = new Intent(this, BigImages.class);
		intent.putExtra("position", which);
		System.out.println("arrayList size:"+getPictures(Integer.valueOf(v.getTag().toString())).size());
		intent.putStringArrayListExtra("arrayList", getPictures(Integer.valueOf(v.getTag().toString())));
        startActivity(intent);  
        overridePendingTransition(R.anim.zoomin, R.anim.zoomout);  
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
//		case R.id.ib_notice_account_back:
//			finish();
//			break;
		case R.id.notice_item_imageView1:
			clickPiture(v,0); 
			break;
		case R.id.notice_item_imageView2:
			clickPiture(v,1); 
			break;
		case R.id.notice_item_imageView3:
			clickPiture(v,2); 
			break;
		case R.id.notice_item_imageView4:
			clickPiture(v,3); 
			break;
		default:
			break;
		}
	}
}
