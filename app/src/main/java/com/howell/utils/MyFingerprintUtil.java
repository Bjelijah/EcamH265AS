package com.howell.utils;

import android.hardware.fingerprint.FingerprintManager;
import android.hardware.fingerprint.FingerprintManager.AuthenticationResult;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import bean.MyFingerprintBeans;

public class MyFingerprintUtil {
	
	@Deprecated
	public static List<MyFingerprintBeans> getAllFingerPrint(FingerprintManager fm) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		List<MyFingerprintBeans> list = new ArrayList<MyFingerprintBeans>();
		Class<FingerprintManager> fpmClass = FingerprintManager.class;
		List l = null;
		Method fpmMethod = null,idMethod = null,nameMethod=null;

		fpmMethod = fpmClass.getMethod("getEnrolledFingerprints");
		fpmMethod.setAccessible(true);
		Object o1 = fpmMethod.invoke(fm);
		l = (List) o1;

		Class fingerprint = null; 

		for(int i=0;i<l.size();i++){
			fingerprint = Class.forName("android.hardware.fingerprint.Fingerprint");
			idMethod = fingerprint.getMethod("getFingerId");
			nameMethod = fingerprint.getMethod("getName");
			Object fpIdObj = idMethod.invoke(l.get(i));
			Object nameObj = nameMethod.invoke(l.get(i));
			int fpId = Integer.valueOf(fpIdObj.toString());
			String name = nameObj.toString();
			MyFingerprintBeans bean = new MyFingerprintBeans(fpId, name);
			list.add(bean);
		}
		return list;
	}


	public static MyFingerprintBeans getFingerprint(AuthenticationResult result) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException{
		MyFingerprintBeans bean = new MyFingerprintBeans();
		Class<AuthenticationResult> c = AuthenticationResult.class;
		Method method1 = c.getMethod("getFingerprint");
		method1.setAccessible(true);
		Object o = method1.invoke(result);
		String className = o.getClass().getName();
		Class fingerprint = Class.forName(className);
		Method method2 = fingerprint.getMethod("getFingerId");
		Object idObj = method2.invoke(o);
		int fingerID = Integer.valueOf(idObj.toString());
		Method method3 = fingerprint.getMethod("getName");
		Object nameObj = method3.invoke(o);
		String name = nameObj.toString();
		bean.setFpID(fingerID);
		bean.setName(name);
		Log.e("123", "  +++++  "+bean.toString());
		return bean;
	}
}
