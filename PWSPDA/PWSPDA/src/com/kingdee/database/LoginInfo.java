package com.kingdee.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class LoginInfo extends SQLiteOpenHelper {

	public LoginInfo(Context context) {
		super(context, DATABASENAME_STRING, null, VERSION);
		// TODO Auto-generated constructor stub
	}

	private static final String DATABASENAME_STRING="k3WisePDA";
	private static final int VERSION=1;
	public static final String TABLENAME_STRING="LoginIn1";
	private String sqlString="create table if not exists LoginIn1"+
	"(_id INTEGER,webUrl TEXT not null);";
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		Log.e("LoginInfo","onCreate!");
        CreatTable(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}
	private void CreatTable(SQLiteDatabase db){
		Log.e("LoginInfo","create database!");
		db.execSQL(sqlString);
	}

}
