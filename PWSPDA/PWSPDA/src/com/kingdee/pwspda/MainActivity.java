package com.kingdee.pwspda;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebStorage.QuotaUpdater;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kingdee.database.LoginInDAO;
import com.kingdee.utils.DecodeMsg;
import com.qq.weixin.mp.aes.AesException;


public class MainActivity extends Activity implements View.OnClickListener {

	private Button bt_scan;
	private EditText et_result;
	IntentFilter intentfilter;
	private WebView webView;
	private LoginInDAO loginDao;
	private String decryString;
	private Context context;
	private String result;
	private String encodingAesKey="jWmYm7qr5nMoAUwZRjGtBxmz3KA1tkAj3ykkR6q2B2C";
	private BroadcastReceiver myReceiver=new BroadcastReceiver(){

		@Override
		public void onReceive(Context arg0, Intent intent) {
			// TODO Auto-generated method stub
			if(intent.getAction().equalsIgnoreCase("df.scanservice.result")){
				 
				 result=intent.getStringExtra("result");
				 Toast.makeText(MainActivity.this, result, Toast.LENGTH_LONG).show();
				 String currentURL=webView.getUrl();
				  if(currentURL==null||currentURL.equals("")||currentURL.contains("type=0")||currentURL.contains("type=7")){
					try {
						Log.e("onReceive","hei");
						decryString=DecodeMsg.decode(result, encodingAesKey);
						String url=ParseJsonResult(decryString);
						
						if(url.contains("http://")){
							String result=loginDao.GetWebUrl();
							
							if(result==""){
								if(loginDao.WriteLoginInfo(url)){
									settingWebView(url);
								}
							}else{
								
								if(loginDao.UpdateWeburl(url)){
								    settingWebView(url);
								}
							}
						}else{
							Toast.makeText(MainActivity.this, url, Toast.LENGTH_LONG).show();
						}
					} catch (AesException e) {
						//TODO： 是否需要进行捕获
					}
				  }else{
					  //TODO: 将扫描得到的数据存放到 cookie 中，并加上status： 0： 为空，1： 正在写，3：已经写完，在前端通过JS来获取扫描的数据
					  syncCookies(webView.getUrl(), result);
				  }
				
			}
		}

	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        initView();
        loginDao=new LoginInDAO(this);
        String resultString=loginDao.GetWebUrl();
        if(resultString.equals("")){
        	Log.e("MainActivity","数据库里没有数据地址");
        }else{
        	String resultUrlString=resultString.substring(0, resultString.indexOf("&"));
        	Log.e("MainActivity",resultUrlString);
        	settingWebView(resultUrlString);
        }
        
        
	}
	
	int count=0;
	Date startDate;
	long firstTime=0;
	Toast mToast=null;
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		//Toast.makeText(MainActivity.this, String.valueOf(keyCode), Toast.LENGTH_SHORT).show();
		if(KeyEvent.KEYCODE_BACK==keyCode){
			 count++;
//			 startDate=new Date();
//			 
//			 if(firstTime==0){
//				 firstTime=startDate.getTime();
//				 if(count==1){
//					 Toast.makeText(MainActivity.this, "再按一次退出l！", Toast.LENGTH_LONG).show();
//				 }
//				 
//			 }else{
//				 //两次点击返回键时间间隔超过一分钟，重置计数器
//				 if(startDate.getTime()-firstTime>1000){
//					 count=1;
//					 firstTime=startDate.getTime();
//					 Toast.makeText(MainActivity.this, "再按一次退出！", Toast.LENGTH_LONG).show();
//				 }else{
//					 if(count==2){
//						 finish();
//					 }
//				 }
//			 }
			
			 
			 if(count<2){
				 if(mToast==null){
					 mToast= Toast.makeText(MainActivity.this, "再按一次退出！", Toast.LENGTH_SHORT);
				     mToast.show();
				 }
				
			 }else if(count==2){
				 finish();
			 }
			 
		}
		if(KeyEvent.KEYCODE_MENU==keyCode){
			Toast.makeText(MainActivity.this,"home", Toast.LENGTH_SHORT).show();
		}
		
			
		
		return false;
	}


	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Intent intent=new Intent("df.scanservice.toapp");
		this.sendBroadcast(intent);
		registerService();
	}
	
	@Override
	protected void onPause(){
		super.onPause();
		this.unregisterReceiver(myReceiver);
	}
    @Override
    protected void onStop(){
    	super.onStop();
    	if(mToast!=null){
			Log.e("onStop","Toast is not null");
			mToast.cancel();
			mToast=null;
		}
    }
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(mToast!=null){
			Log.e("onDestory","Toast is not null");
			mToast.cancel();
			mToast=null;
		}
	}
    //把扫描的数据存在cookie中
    private void syncCookies(String url,String value){
    	CookieSyncManager.createInstance(MainActivity.this);
    	CookieManager cookieManager=CookieManager.getInstance();
    	cookieManager.setCookie(url,"scan_msg="+value);
    	CookieSyncManager.getInstance().sync();
    }
	private void registerService() {
		// TODO Auto-generated method stub
		intentfilter=new IntentFilter();
		intentfilter.addAction("df.scanservice.result");
		this.registerReceiver(myReceiver, intentfilter);
		
	}
	private void initView() {
		// TODO Auto-generated method stub
//		bt_scan=(Button) this.findViewById(R.id.bt_scan);
//		bt_scan.setOnClickListener(this);
		webView=(WebView) this.findViewById(R.id.webView);
	}
	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
//		switch(view.getId()){
//		case R.id.bt_scan:
//			
//			Intent intent=new Intent("df.scanservice.start");
//			this.sendBroadcast(intent);
//			break;
//		}
	}
	private void settingWebView(String url){
		//webView=(WebView) findViewById(R.id.webview);
		webView.loadUrl(url);
		webView.reload();
		//hongbo_liang 启用客户端的Cookie
		CookieManager.getInstance().setAcceptCookie(true);		
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebViewClient(new WebViewClient(){

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// TODO Auto-generated method stub
			
				webView.loadUrl(url);
				return true;
			}
			
			
		});
		webView.setWebChromeClient(new WebChromeClient(){
            
			@Override
			public void onExceededDatabaseQuota(String url,
					String databaseIdentifier, long quota,
					long estimatedDatabaseSize, long totalQuota,
					QuotaUpdater quotaUpdater) {
				// TODO Auto-generated method stub
				super.onExceededDatabaseQuota(url, databaseIdentifier, quota,
						estimatedDatabaseSize, totalQuota, quotaUpdater);
				quotaUpdater.updateQuota(5*1024*1024);
			}	
		});
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setSaveFormData(true);
        webView.getSettings().setDatabaseEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.getSettings().setSupportZoom(false); // 不支持页面放大功能
        String databasePath=getApplicationContext().getDir("database",Context.MODE_PRIVATE).getAbsolutePath();
        webView.getSettings().setDatabasePath(databasePath);
        Log.e("MainActivity the path of database is ",databasePath);
	}

	/*
	 * 
	 * 
	 */
	private String ParseJsonResult(String msg){
		String urlString="";
		String AccID="";
		String dbConnString="";
		String userNameString="";
		String userPsw="";
		try {
			JSONObject jsonObject=new JSONObject(msg);
			urlString=jsonObject.getString("WebURL");
			  AccID=jsonObject.getString("AccID");
			  urlString+="&AccID="+AccID+"&";
			  dbConnString=jsonObject.getString("DBConnStr");
			  String[] listStrings=dbConnString.split(";");
              for(int i=0;i<listStrings.length;i++){
            	if(i==0){
            		urlString+=listStrings[i].substring(1)+"&";
            	}else if(i==listStrings.length-1){
            		String data=listStrings[i];
            		urlString+=data.substring(0,data.length()-1);
            	}else{
            		urlString+=listStrings[i]+"&";
            	}
               }  
			userNameString=jsonObject.getString("UserName");
			userPsw=jsonObject.getString("UserPsw");
		    urlString+="&UserName="+userNameString+"&UserPsw="+userPsw;
		    urlString+="&timestamp="+new Date().toGMTString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return urlString;
	}
	
}
