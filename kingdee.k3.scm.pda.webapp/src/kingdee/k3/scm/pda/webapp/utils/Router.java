package kingdee.k3.scm.pda.webapp.utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebStorage.QuotaUpdater;
import android.widget.ProgressBar;
import kingdee.k3.scm.pda.webapp.R;
import kingdee.k3.scm.pda.webapp.base.BaseApplication;
import kingdee.k3.scm.pda.webapp.cache.ConfigCache;
import kingdee.k3.scm.pda.webapp.db.BaseSQLiteHelper;
import kingdee.k3.scm.pda.webapp.db.UrlSQLLite;
import kingdee.k3.scm.pda.webapp.sync.http.AsyncHttpClient;
import kingdee.k3.scm.pda.webapp.sync.http.AsyncHttpResponseHandler;

/*
 * author: hongbo_liang @ kingdee.com
 * date: 2015-08-13
 * description: 用于调整 web view 中页面跳转
 */
public class Router {
	
	private static WebView mWebView;
	private static ProgressBar mProgressBar;
	
	//检查数据  SQLlit 中是否存在url
	public static void settingWebView(Context context,WebView webview, ProgressBar progressBar, String url){
		mWebView = webview;	
		mProgressBar = progressBar;
		
		if(NetworkUtils.getNetworkState(context) == NetworkUtils.NETWORN_NONE){
			settingWebView(context, "file:///android_asset/NO_NETWORK.html");
		}else{		
			String currentURL = mWebView.getUrl();
			String cacheUrl = getCacheUrl(context);
			
			if((currentURL == null || currentURL.equals("")) && (url.equals("") || url == null)){				
				if(cacheUrl != null && cacheUrl != ""){
					settingWebView(context, cacheUrl);
					if(BaseApplication.mDomain == null || BaseApplication.mDomain == ""){
						BaseApplication.mDomain = getDomain(cacheUrl);	
						initEnv(context);
					}
					
					CookieHelper.synCookies(context, cacheUrl, "mi", getMachineInfo(context));
				}else{
					settingWebView(context, "file:///android_asset/init.html");
				}				
			}else{
				if(url.contains("http://") || url.contains("https://")){
					String result=getCacheUrl(context);
					
					if(result==""){
						setCacheUrl(context, url);
						settingWebView(context, url);
					}else{							
						updateCacheUrl(context, url);
						settingWebView(context, url);					
					}
					
					if(BaseApplication.mDomain == null || BaseApplication.mDomain == ""){
						BaseApplication.mDomain = getDomain(url);	
						initEnv(context);
					}
				}
				
				CookieHelper.synCookies(context, url, "mi", getMachineInfo(context));
			}			
		}
		
	}
	
	//获取缓存url数据
	public static String getCacheUrl(Context context){
		UrlSQLLite urlDB = new UrlSQLLite(context);	
		return urlDB.getCacheUrl();
	}
	
	//将 url 放到缓存中
	private static void setCacheUrl(Context context, String url){
		UrlSQLLite urlDB = new UrlSQLLite(context);			
		urlDB.setCacheUrl(url);
	}
	
	private static void updateCacheUrl(Context context, String url){
		UrlSQLLite urlDB = new UrlSQLLite(context);			
		urlDB.updateCacheUrl(url);
	}	
	
	@SuppressLint("SetJavaScriptEnabled")
	private static void settingWebView(final Context context, String url){
    	//hongbo_liang 启用客户端的Cookie
		CookieManager.getInstance().setAcceptCookie(true);		
		 
		 WebSettings webSettings = mWebView.getSettings();
		 webSettings.setJavaScriptEnabled(true);
		
		 webSettings.setDomStorageEnabled(true);
		 webSettings.setSaveFormData(true);
		 webSettings.setDatabaseEnabled(true);
		 webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
		 webSettings.setSupportZoom(false); // 不支持页面放大功能
		 webSettings.setLoadWithOverviewMode(true);
		 webSettings.setUseWideViewPort(true);		 
		 webSettings.setJavaScriptCanOpenWindowsAutomatically(true);  
         // access Assets and resources  
         webSettings.setAllowFileAccess(true);    
         //zoom page  
         webSettings.setBuiltInZoomControls(false);    
         webSettings.setPluginState(WebSettings.PluginState.ON);  
           
         //提高渲染的优先级  
         webSettings.setRenderPriority(RenderPriority.HIGH);    
         webSettings.setEnableSmoothTransition(true); 
         
         webSettings.setDatabaseEnabled(true);  
         webSettings.setDomStorageEnabled(true);  
         String databasePath = context.getDir("databases", Context.MODE_PRIVATE).getPath();  
         webSettings.setDatabasePath(databasePath);
		
		mWebView.setWebViewClient(new WebViewClient(){

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) { 
	            return true;
			}	
			
			//自定义错误页面
			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl){
				super.onReceivedError(view, errorCode, description, failingUrl);				
				view.loadUrl("file:///android_asset/404.html");
			}
			
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {								
				super.onPageStarted(view, url, favicon);
			}
			
			@Override
			public void onPageFinished(WebView view,String url)  {	
				super.onPageFinished(view, url);
			}
			
			@Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
            }
			
		});
		
		mWebView.setWebChromeClient(new WebChromeClient(){
			  @Override
	            public void onProgressChanged(WebView view, int newProgress) {
	                Log.e("newProgress", newProgress+"");
	                mProgressBar.setProgress(newProgress);
	                if(newProgress >= 100){
	                	mProgressBar.setVisibility(View.GONE);
	                }
	            }
			  
			  @Override
				public void onExceededDatabaseQuota(String url,
						String databaseIdentifier, long currentQuota,
						long estimatedSize, long totalUsedQuota,
						QuotaUpdater quotaUpdater) {
					super.onExceededDatabaseQuota(url, databaseIdentifier, currentQuota,
							estimatedSize, totalUsedQuota, quotaUpdater);
					quotaUpdater.updateQuota(5*1024*1024);
				}		
		});
		 		
		//mWebView.loadUrl("about:blank");
		mWebView.loadUrl(url);
    }
	
	//获取链接的返回状态
	private static int getRespStatus(String url) {   
        int status = -1;   
        try {   
            HttpHead head = new HttpHead(url);   
            HttpClient client = new DefaultHttpClient();   
            HttpResponse resp = client.execute(head);   
            status = resp.getStatusLine().getStatusCode();   
        } catch (IOException e) {
        	
        }   
        return status;   
	} 
	
	//获取地址的IP以及端口
	private  static String getDomain(String url){
		String domain = "";
		try{
		String host = new URL(url).getHost().toLowerCase();
		int port = new URL(url).getPort();
		domain = "http://" +host + ":" + port + "/PDA/PDAQRCode/APPDownLoad/";
		
		}catch(Exception e){
			Log.e("GETDOMAIN", e.getMessage());
		}
		
		return domain;
	}
	
	//重新设置基础属性
	private static void initEnv(Context context){
		BaseApplication.mAppId = context.getString(R.string.app_id);
		BaseApplication.mSQLiteHelper = new BaseSQLiteHelper(context.getApplicationContext(), BaseApplication.mAppId+ ".db", 1);
		BaseApplication.mDownloadPath = "/" + BaseApplication.mAppId+ "/download";

		BaseApplication.mServerLatestUrl = BaseApplication.mDomain + BaseApplication.mAppId + "/data/json/latest.json";
		BaseApplication.mServerPageUrl = BaseApplication.mDomain + BaseApplication.mAppId + "/data/json/pages/";
		BaseApplication.mServerSetUrl = BaseApplication.mDomain + BaseApplication.mAppId + "/data/json/set.json";
		BaseApplication.mServerSeasonUrl = BaseApplication.mDomain + BaseApplication.mAppId + "/data/json/seasons/";

        if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            File file = new File(Environment.getExternalStorageDirectory().getPath() +  "/" + BaseApplication.mAppId + "/config/");
            if(!file.exists()) {
                if (file.mkdirs()) {
                	BaseApplication.mSdcardDataDir = file.getAbsolutePath();
                }
            } else {
            	BaseApplication.mSdcardDataDir = file.getAbsolutePath();
            }
        }

        BaseApplication.mNetWorkState = NetworkUtils.getNetworkState(context.getApplicationContext());
        if(BaseApplication.mDomain != null && BaseApplication.mDomain != ""){
        	//checkDomain(context, BaseApplication.mDomain, false);  
        	
        	String result = "{domain: "+BaseApplication.mDomain+"}";
        	ConfigCache.setUrlCache(result, BaseApplication.mDomain + "host.json");
            updateDomain(context, result);
        }
	}
	

    public static void updateDomain(Context context,String result) {
        try {
            JSONObject appreciateConfig = new JSONObject(result);
            String domain = appreciateConfig.optString("domain");
            if (domain != null && !"".equals(domain)) {
                BaseApplication.mDomain = domain;
                PreferencesUtils.setStringPreferences(context.getApplicationContext(), BaseApplication.DOMAIN_URL, domain);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    
    
  //获取客户端的 IP 地址
  	private static String getClientIP(Context context){
  		String ip = "";
  		
  		//获取wifi服务  
          WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);  
          //判断wifi是否开启  
          if (!wifiManager.isWifiEnabled()) {  
          wifiManager.setWifiEnabled(true);    
          }  
          WifiInfo wifiInfo = wifiManager.getConnectionInfo();       
          int ipAddress = wifiInfo.getIpAddress();
          ip = intToIp(ipAddress); 
          
          //Toast.makeText(MainActivity.this, ip, Toast.LENGTH_LONG).show();
  		return ip;
  	}
  	
  	//获取设备的信息
  	private static String getMachineInfo(Context context){
  		TelephonyManager tm = (TelephonyManager)context.getSystemService( Context.TELEPHONY_SERVICE);  
          StringBuilder sb = new StringBuilder();          
          sb.append("DeviceId = " + tm.getDeviceId());  
          sb.append("&DeviceSoftwareVersion = " + tm.getDeviceSoftwareVersion());  
          sb.append("&Line1Number = " + tm.getLine1Number());  
          sb.append("&NetworkCountryIso = " + tm.getNetworkCountryIso());  
          sb.append("&NetworkOperator = " + tm.getNetworkOperator());  
          sb.append("&NetworkOperatorName = " + tm.getNetworkOperatorName());  
          sb.append("&NetworkType = " + tm.getNetworkType());  
          sb.append("&PhoneType = " + tm.getPhoneType());  
          sb.append("&SimCountryIso = " + tm.getSimCountryIso());  
          sb.append("&SimOperator = " + tm.getSimOperator());  
          sb.append("&SimOperatorName = " + tm.getSimOperatorName());  
          sb.append("&SimSerialNumber = " + tm.getSimSerialNumber());  
          sb.append("&SimState = " + tm.getSimState());  
          sb.append("&SubscriberId(IMSI) = " + tm.getSubscriberId());  
          sb.append("&VoiceMailNumber = " + tm.getVoiceMailNumber());
          sb.append("&MachineName = " + Build.MODEL);
          sb.append("&IP = " + getClientIP(context));
          Log.e("info", sb.toString());   
          
          return sb.toString();
  	}
  	
  	//将int数据转换成为 IP 地址
  	private static String intToIp(int i) {       
          return (i & 0xFF ) + "." +       
          ((i >> 8 ) & 0xFF) + "." +       
          ((i >> 16 ) & 0xFF) + "." +       
          ( i >> 24 & 0xFF) ;  
      } 
}
