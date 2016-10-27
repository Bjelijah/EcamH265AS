package com.howell.ehlib;

import android.content.Context;
import android.graphics.drawable.Drawable;

/**
 * @author yangyu
 *	���������������ڲ���������Ʊ����ͼ�꣩
 */
public class ActionItem {
	//����ͼƬ����
	public Drawable mDrawable;
	//�����ı�����
	public CharSequence mTitle;
	
	public ActionItem(Drawable drawable, CharSequence title){
		this.mDrawable = drawable;
		this.mTitle = title;
	}
	
	public ActionItem(Context context, CharSequence title){
		this.mTitle = title;
		this.mDrawable = null;
	}
	
	public ActionItem(Context context, int titleId, int drawableId){
		this.mTitle = context.getResources().getText(titleId);
		this.mDrawable = context.getResources().getDrawable(drawableId);
	}
	
	public ActionItem(Context context, CharSequence title, int drawableId) {
		this.mTitle = title;
		this.mDrawable = context.getResources().getDrawable(drawableId);
	}
}
