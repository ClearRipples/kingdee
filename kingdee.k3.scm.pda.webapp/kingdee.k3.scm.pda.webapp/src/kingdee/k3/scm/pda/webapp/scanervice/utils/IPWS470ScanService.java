package kingdee.k3.scm.pda.webapp.scanervice.utils;

import android.content.Context;
import android.webkit.WebView;
import android.widget.ProgressBar;

/*
 * author: hongbo_liang	
 * date: 2015-08-12
 * description: PWS470 抽象工厂接口
 */
public interface IPWS470ScanService {
	//注册广播
	void registerBroadCast(Context context, WebView webView, ProgressBar progressBar);
	//注销广播事件
	void unRegisterBroadCast();
	//销毁服务
	void destroy();
}
