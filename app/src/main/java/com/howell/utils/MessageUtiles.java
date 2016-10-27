package com.howell.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.howell.ecamh265.R;

public class MessageUtiles {
	public static void postToast(Context context,String message,int time){
		Toast.makeText(context, message, time).show();
	}
	
	public static void postAlertDialog(Context context ,String title , String message ,int iconId
			,DialogInterface.OnClickListener positiveButtonListener
			,String positiveButtonName
			,DialogInterface.OnClickListener negativeButtonListener
			,String negativeButtonName){
		Dialog alertDialog = null;
		if(positiveButtonListener != null && negativeButtonListener != null){
			alertDialog = new AlertDialog.Builder(context,R.style.myLightAlertDialog).
				setTitle(title).   
		        setMessage(message).   
		        setIcon(iconId).   
		        setPositiveButton(positiveButtonName, positiveButtonListener).   
		        setNegativeButton(negativeButtonName, negativeButtonListener).
		        create();

		}else if(negativeButtonListener == null && positiveButtonListener != null){
			alertDialog = new AlertDialog.Builder(context,R.style.myLightAlertDialog).
				setTitle(title).   
			    setMessage(message).   
			    setIcon(iconId).   
			    setPositiveButton(positiveButtonName, positiveButtonListener).
			    create();   
		}else if(positiveButtonListener == null && negativeButtonListener == null){
			if(positiveButtonName == null){
				positiveButtonName = "";
			}
			alertDialog = new AlertDialog.Builder(context,R.style.myLightAlertDialog).
				setTitle(title).   
			    setMessage(message).   
			    setIcon(iconId).   
			    setPositiveButton(positiveButtonName, new DialogInterface.OnClickListener() {
						
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
							
					}
				}).   
			    create();   
		}else{
			Log.e("postAlertDialog", "positiveButtonName can not be null !");
			return;
		}
		Window window = alertDialog.getWindow();
		window.setWindowAnimations(R.style.DialogAnimation);
		alertDialog.show();  
	}
	
	public static Dialog postWaitingDialog(Context context){
		final Dialog lDialog = new Dialog(context,android.R.style.Theme_Translucent_NoTitleBar);
		lDialog.setContentView(R.layout.wait_dialog);
		return lDialog;
	}
	
//	public static void postNewUIDialog2(Context context,String message,String buttonName,final int flag){
//		 final Dialog lDialog = new Dialog(context,android.R.style.Theme_Translucent_NoTitleBar);
////         lDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//         lDialog.setContentView(R.layout.dialog_view);
////         ((TextView) lDialog.findViewById(R.id.dialog_title)).setText(pTitle);
//         ((TextView) lDialog.findViewById(R.id.dialog_message)).setText(message);
//         ((Button) lDialog.findViewById(R.id.ok)).setText(buttonName);
//         ((Button) lDialog.findViewById(R.id.ok))
//                 .setOnClickListener(new OnClickListener() {
//                     @Override
//                     public void onClick(View v) {
//                         // write your code to do things after users clicks OK
//                    	 if(flag == 0){
//                    		 
//                    	 }
//                         lDialog.dismiss();
//                     }
//                 });
//          lDialog.show();
//	}
//	public static void postNewUIDialog(Context context,String message,String buttonName,final int flag){
//		 final Dialog lDialog = new Dialog(context,android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
////        lDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        lDialog.setContentView(R.layout.dialog_view);
////        ((TextView) lDialog.findViewById(R.id.dialog_title)).setText(pTitle);
//        ((TextView) lDialog.findViewById(R.id.dialog_message)).setText(message);
//        ((Button) lDialog.findViewById(R.id.ok)).setText(buttonName);
//        ((Button) lDialog.findViewById(R.id.ok))
//                .setOnClickListener(new OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        // write your code to do things after users clicks OK
//                   	 if(flag == 0){
//                   		 
//                   	 }
//                        lDialog.dismiss();
//                    }
//                });
//         lDialog.show();
//	}
	
	public static void postUpdateDialog(Context context,String message,String buttonName1,String buttonName2 ){
		final Dialog lDialog = new Dialog(context,android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
		lDialog.setContentView(R.layout.update_dialog);
		((TextView) lDialog.findViewById(R.id.dialog_message)).setText(message);
		((Button) lDialog.findViewById(R.id.ok)).setText(buttonName1);
		((Button) lDialog.findViewById(R.id.ok)).setOnClickListener(new OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       // write your code to do things after users clicks OK
                       lDialog.dismiss();
                   }
               });
		((Button) lDialog.findViewById(R.id.cancel)).setText(buttonName2);
		((Button) lDialog.findViewById(R.id.cancel)).setOnClickListener(new OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       // write your code to do things after users clicks OK
                       lDialog.dismiss();
                   }
               });
        lDialog.show();
	}
}
