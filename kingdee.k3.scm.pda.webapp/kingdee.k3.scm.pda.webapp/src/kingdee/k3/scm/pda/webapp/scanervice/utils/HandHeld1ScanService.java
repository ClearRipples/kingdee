package kingdee.k3.scm.pda.webapp.scanervice.utils;

import kingdee.k3.scm.pda.webapp.R;
import kingdee.k3.scm.pda.webapp.utils.CookieHelper;
import kingdee.k3.scm.pda.webapp.utils.Router;
import cn.pda.scan.ScanThread;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.Toast;


/*
 * author: hongbo_liang @ kingdee.com
 * date: 2015-08-22
 * description: 用于处理 HandHeld1 型号的扫描服务
 */
public class HandHeld1ScanService implements IHandHeld1ScanService{
	
	private final static String TAG = "HandHeld1ScanService";
		
	private static ScanThread scanThread;

	@Override
	public void scan() {	
		scanThread.scan();
	}

	@Override
	public void startService(final Context context, final WebView webView, final ProgressBar progressBar) {		
		try {
			scanThread = new ScanThread(new Handler() {
				public void handleMessage(android.os.Message msg) {
					if (msg.what == ScanThread.SCAN) {
						String data = msg.getData().getString("data");
						//判断是链接
						if(data.contains("http")|| data.contains("https")){
							Router.settingWebView(context, webView, progressBar, data);
						}else{
							handleData(context, webView, data);
						}
						
						Log.e(TAG, data);
						//扫描声音处理
						Util.play(1, 0);
					}
				}
			});
		} catch (Exception e) {
			Toast.makeText(context.getApplicationContext(), R.string.app_scanservice_initfail, Toast.LENGTH_LONG)
					.show();
			return;
		}
		scanThread.start();
		Util.initSoundPool(context);
	}	

	@Override
	public void registerMessageHandle() {
	}

	@Override
	public void destroy() {
		if (scanThread != null) {
			scanThread.interrupt();
			scanThread.close();
		}		
	}
	
	//处理扫描到的数据
	private void handleData(final Context context, final WebView webView,String data){
		if(data != null && !data.equals("") && webView.getUrl() != null && !webView.getUrl().equals("")){
			CookieHelper.synCookies(context, webView.getUrl(), data);
		}
	}
}
