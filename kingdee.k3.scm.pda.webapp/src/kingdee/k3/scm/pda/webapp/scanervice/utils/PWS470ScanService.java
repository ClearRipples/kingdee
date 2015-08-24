package kingdee.k3.scm.pda.webapp.scanervice.utils;

import kingdee.k3.scm.pda.webapp.utils.CookieHelper;
import kingdee.k3.scm.pda.webapp.utils.Router;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.webkit.WebView;
import android.widget.ProgressBar;

/*
 * author: hongbo_liang @ kingdee.com
 * date: 2015-08-22
 * description: 用于处理 PWS470 型号的扫描服务
 */
public class PWS470ScanService implements IPWS470ScanService {
	
	public final static String TAG = "PWS470ScanService";
	private static IntentFilter intentfilter;
	private static Context mContext;	
	private static WebView mWebView;
	private static ProgressBar mWebViewProgressBar;
	
	//接收广播
	private static BroadcastReceiver PWS470Receiver=new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equalsIgnoreCase("df.scanservice.result")){
				Log.i(TAG, intent.getStringExtra("result"));
				 String data = intent.getStringExtra("result");	
				 if(data.contains("http")|| data.contains("https")){
						Router.settingWebView(mContext.getApplicationContext(), mWebView, mWebViewProgressBar, data);
					}else{
						handleData(data);
					}
			}
		}
	};

	@Override
	public void destroy() {		
	}

	@Override
	public void registerBroadCast(Context context, WebView webView, ProgressBar progressBar) {
		mContext = context;
		mWebView = webView;
		mWebViewProgressBar = progressBar;
		intentfilter=new IntentFilter();
		intentfilter.addAction("df.scanservice.result");
		context.registerReceiver(PWS470Receiver, intentfilter);
	}

	@Override
	public void unRegisterBroadCast() {	
		mContext.unregisterReceiver(PWS470Receiver);
	}
	
	private static void handleData(String data){
		if(data != null && !data.equals("") && mWebView.getUrl() != null && !mWebView.getUrl().equals("")){
			CookieHelper.synCookies(mContext, mWebView.getUrl(), data);
		}
	}
}
