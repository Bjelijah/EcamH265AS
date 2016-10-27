package com.howell.ehlib;



import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.ActionProvider;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.SubMenu;
import android.view.View;
@SuppressLint("NewApi")
public class PlusActionProvider extends ActionProvider {

	private Context context;
	
	public PlusActionProvider(Context context) {
		super(context);
		this.context = context;
	}

	@Override
	public View onCreateActionView() {
		return null;
	}

	@Override
	public void onPrepareSubMenu(SubMenu subMenu) {
		subMenu.clear();
		subMenu.add("test11")
				.setIcon(null)
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						Log.e("", "plus_group_chat");
						return true;
					}
				});
		subMenu.add("test22")
				.setIcon(null)
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						return false;
					}
				});
//		subMenu.add(context.getString(R.string.plus_video_chat))
//				.setIcon(R.drawable.ofm_video_icon)
//				.setOnMenuItemClickListener(new OnMenuItemClickListener() {
//					@Override
//					public boolean onMenuItemClick(MenuItem item) {
//						return false;
//					}
//				});
//		subMenu.add(context.getString(R.string.plus_scan))
//				.setIcon(R.drawable.ofm_qrcode_icon)
//				.setOnMenuItemClickListener(new OnMenuItemClickListener() {
//					@Override
//					public boolean onMenuItemClick(MenuItem item) {
//						return false;
//					}
//				});
//		subMenu.add(context.getString(R.string.plus_take_photo))
//				.setIcon(R.drawable.ofm_camera_icon)
//				.setOnMenuItemClickListener(new OnMenuItemClickListener() {
//					@Override
//					public boolean onMenuItemClick(MenuItem item) {
//						return false;
//					}
//				});
	}

	@Override
	public boolean hasSubMenu() {
		return true;
	}

}