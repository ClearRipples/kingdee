package kingdee.k3.scm.pda.webapp.scanervice.utils;

import android.content.Context;
import android.webkit.WebView;
import android.widget.ProgressBar;

/*
 * author: hongbo_liang	
 * date: 2015-08-22
 * description: 扫描工厂
 */
public class ScanFactory implements IScanFactory{
	
	@Override
	public IHandHeld1ScanService createHandHeld1ScanService() {
		return new HandHeld1ScanService();
	}

	@Override
	public IPWS470ScanService createPWS470ScanService() {
		return new PWS470ScanService();
	}	
}
