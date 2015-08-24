package kingdee.k3.scm.pda.webapp.scanervice.utils;

import android.content.Context;
import android.webkit.WebView;
import android.widget.ProgressBar;

/*
 * author: hongbo_liang	
 * date: 2015-08-12
 * description: HandHeld1 抽象工厂接口
 */
public interface IHandHeld1ScanService {
	//调用扫描
	void scan();
	//注册消息处理事件
	void registerMessageHandle();
	//启动扫描服务
	void startService(Context context, WebView webView, ProgressBar progressBar);
	//销毁服务
	void destroy();
}


