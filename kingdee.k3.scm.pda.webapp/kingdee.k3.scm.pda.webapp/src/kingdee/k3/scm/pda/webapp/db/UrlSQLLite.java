package kingdee.k3.scm.pda.webapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/*
 * author: hongbo_liang @ kingdee.com
 * date: 2015-08-14
 * description: 用于处理 webview 链接缓存 
 */
public class UrlSQLLite extends BaseSQLiteHelper{
	
	private static final String TAG= "URLSQLLITE";
	
	private static final String DATABASENAME_STRING="k3WisePDA";
	private static final int VERSION=1;
	private static final String TABLENAME_STRING="host_url";
	
	private SQLiteDatabase database;
	
	public UrlSQLLite(Context context) {
		super(context, DATABASENAME_STRING, null, VERSION);
		
		this.mCreateSql = "create table if not exists "+ TABLENAME_STRING + " (_id INTEGER,appurl TEXT not null);";
		database = this.getWritableDatabase();
	}
	
	//缓存地址
	public void setCacheUrl(String url){
		ContentValues values=new ContentValues();
		values.put("_id", 1);
		values.put("appurl", url);
		
		long result=database.insert(TABLENAME_STRING,null , values);
		if(result>=0){
			Log.e(TAG, "插入数据成功");
		}else{
			Log.e(TAG, "插入数据失败");
		}
	}
	
	//获取缓存的信息
	public String getCacheUrl(){
		String urlString="";
		database=this.getWritableDatabase();
		Cursor cursor=database.query(TABLENAME_STRING, new String[]{"appurl"}, null, null, null, null, null);
		while(cursor.moveToNext()){
		    urlString=cursor.getString(cursor.getColumnIndex("appurl"));
		}
		return urlString;
	}
	
	//更新缓存数据
	public boolean updateCacheUrl(String webUrl){
		boolean flag=false;
		database=this.getWritableDatabase();
		ContentValues values=new ContentValues();
		values.put("appurl", webUrl);
		int result1=database.update(TABLENAME_STRING, values, "_id=?", new String[]{"1"});
		if(result1>=0){			
			Log.e(TAG, "更新数据成功");		
		}
		return flag;
	}
}
