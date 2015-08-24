package com.kingdee.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class LoginInDAO {
private LoginInfo loginInfo;
private SQLiteDatabase database;
private String WEBURL="webUrl";
public LoginInDAO(Context context){
	Log.e("LoginDAO","LoginDAo");
	loginInfo=new LoginInfo(context);
	database=loginInfo.getWritableDatabase();
}
public boolean WriteLoginInfo(String url){
	boolean flag=false;
	database=loginInfo.getWritableDatabase();
	ContentValues values=new ContentValues();
	values.put("_id", 1);
	values.put("webUrl", url);
	long result=database.insert(LoginInfo.TABLENAME_STRING,null , values);
	if(result>=0){
		flag=true;
	}
	return flag;
}
public String GetWebUrl(){
	String urlString="";
	database=loginInfo.getWritableDatabase();
	Cursor cursor=database.query(LoginInfo.TABLENAME_STRING, new String[]{"webUrl"}, null, null, null, null, null);
	while(cursor.moveToNext()){
	    urlString=cursor.getString(cursor.getColumnIndex("webUrl"));
	}
	return urlString;
}
public boolean UpdateWeburl(String webUrl){
	boolean flag=false;
	database=loginInfo.getWritableDatabase();
	ContentValues values=new ContentValues();
	values.put("webUrl", webUrl);
	int result1=database.update(LoginInfo.TABLENAME_STRING, values, "_id=?", new String[]{"1"});
	if(result1>=0){
		
		flag=true;
		
	}
	return flag;
}
}
