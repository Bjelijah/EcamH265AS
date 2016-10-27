package com.howell.db;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import bean.UserLoginDBBean;

public class UserLoginDao {
	DBHelper dbHelper;
	SQLiteDatabase db;
	
	
	//创建数据库
	public UserLoginDao(Context context,String name,int version){
		dbHelper = new DBHelper(context, name, null, version);
	}
	
	public void insert(UserLoginDBBean info){
		db = dbHelper.getWritableDatabase();
		String sql = "insert into userinfo (num,username,userpassword)values(?,?,?);";
		db.execSQL(sql,new Object[]{info.getUserNum(),info.getUserName(),info.getUserPassword()});
	}
	
	public void updataById(int id,UserLoginDBBean info){
		db = dbHelper.getWritableDatabase();
		String sql = "update userinfo set num=?,username=?,userpassword= ? where id=?;";
		db.execSQL(sql, new Object[]{info.getUserNum(),info.getUserName(),info.getUserPassword(),id});
	}
	
	public void updataByNum(UserLoginDBBean info){
		db = dbHelper.getWritableDatabase();
		String sql = "update userinfo set num=?,username=?,userpassword= ? where num=?;";
		db.execSQL(sql, new Object[]{info.getUserNum(),info.getUserName(),info.getUserPassword(),info.getUserNum()});
	}
	
	public void deleteById(int id){
		db = dbHelper.getWritableDatabase();
		String sql = "delete from userinfo where id=?;";
		db.execSQL(sql, new Object[]{id});
	}
	
	public void deleteByNum(int num){
		db = dbHelper.getWritableDatabase();
		String sql = "delete from userinfo where num=?;";
		db.execSQL(sql, new Object[]{num});
	}
	
	public void deleteAll(){
		db = dbHelper.getWritableDatabase();
		String sql = "delete  from userinfo;";
		db.execSQL(sql, new Object[]{});
	}
	
	
	public List<UserLoginDBBean> queryAll(){
		List<UserLoginDBBean> data = new ArrayList<UserLoginDBBean>();
		db = dbHelper.getWritableDatabase();
		String sql = "select * from userinfo order by id asc";
		Cursor cursor = db.rawQuery(sql, null);
		while(cursor.moveToNext()){
			int id = cursor.getInt(cursor.getColumnIndex("id"));
			int num = cursor.getInt(cursor.getColumnIndex("num"));
			String userName = cursor.getString(cursor.getColumnIndex("username"));
			String userPassword = cursor.getString(cursor.getColumnIndex("userpassword"));
			UserLoginDBBean info = new UserLoginDBBean(num, userName, userPassword);
			data.add(info);
		}
		return data;
	}
	
	public boolean findByNum(int userNum){
		boolean result = false;
		db = dbHelper.getWritableDatabase();
		String sql = "select * from userinfo where num=?;";
		Cursor cursor = db.rawQuery(sql, new String[]{userNum+""});
		if (cursor.moveToNext()) {
			result = true;
		}
	
		return result;
	}
	
	
	public List<UserLoginDBBean> queryByNum(int userNum){
		db = dbHelper.getWritableDatabase();
		List<UserLoginDBBean> data = new ArrayList<UserLoginDBBean>();
		String sql = "select * from userinfo where num=?;";
		Cursor cursor = db.rawQuery(sql, new String[]{userNum+""});
		while(cursor.moveToNext()){
			int id = cursor.getInt(cursor.getColumnIndex("id"));
			int num = cursor.getInt(cursor.getColumnIndex("num"));
			String userName = cursor.getString(cursor.getColumnIndex("username"));
			String userPassword = cursor.getString(cursor.getColumnIndex("userpassword"));
			UserLoginDBBean info = new UserLoginDBBean(num, userName, userPassword);
			data.add(info);
		}
		return data;
	}
	
	
	public void close(){
		if(null!=db){
			db.close();
			db = null;
		}
		if (dbHelper!=null) {
			dbHelper.close();
			dbHelper = null;
		}
	}
	
}
