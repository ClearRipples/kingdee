package kingdee.k3.scm.pda.webapp.utils;

import android.content.Context;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

public class CookieHelper {
	
	private static String TAG = "COOKIE-HELPER";
	
	//用于将扫描得到的数据存放到Cookie中
	public static void synCookies(Context context, String url, String msg) {  
        CookieSyncManager.createInstance(context);  
        CookieManager cookieManager = CookieManager.getInstance();  
        cookieManager.setCookie(url, "scan_msg=" + msg);              
        CookieSyncManager.getInstance().sync(); 
        Log.i(TAG, "数据存入到 COOKIE");
    }
	
	public static void synCookies(Context context, String url, String key, String msg){
		CookieSyncManager.createInstance(context);  
        CookieManager cookieManager = CookieManager.getInstance();  
        cookieManager.setCookie(url, key +"=" + msg); 
        CookieSyncManager.getInstance().sync();
        
        Log.i(TAG, "设备数据存入到 COOKIE");
	}
}
