package kingdee.k3.scm.pda.webapp.scanervice.utils;

import android.content.Context;
import android.webkit.WebView;
import android.widget.ProgressBar;

/*
 * author: hongbo_liang	@ kingdee.com
 * date: 2015-08-22
 * description: 抽象工厂接口
 */
public interface IScanFactory {
	public IHandHeld1ScanService createHandHeld1ScanService();
	public IPWS470ScanService createPWS470ScanService(); 
}
