package kingdee.k3.scm.pda.webapp.activity;

import java.util.Timer;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.Toast;
import kingdee.k3.scm.pda.webapp.R;
import kingdee.k3.scm.pda.webapp.base.BaseApplication;
import kingdee.k3.scm.pda.webapp.cache.ConfigCache;
import kingdee.k3.scm.pda.webapp.scanervice.utils.ModelConst;
import kingdee.k3.scm.pda.webapp.scanervice.utils.ScanFactory;
import kingdee.k3.scm.pda.webapp.sync.http.AsyncHttpClient;
import kingdee.k3.scm.pda.webapp.sync.http.AsyncHttpResponseHandler;
import kingdee.k3.scm.pda.webapp.upgrade.AppUpgradeService;
import kingdee.k3.scm.pda.webapp.utils.NetworkUtils;
import kingdee.k3.scm.pda.webapp.utils.Router;

public class MainActivity extends Activity {
	private Timer scanTimer = null;
	private ProgressBar mWebViewProgressBar;
	private WebView mWebView;

	private int mLatestVersionCode = 0;
	private String mLatestVersionUpdate = null;
	private String mLatestVersionDownload = null;
	
    private static final String TAG = "Exit Application"; 
    private static final String LICENSE_TAG = "LICENSE";
    
    private ScanFactory scanFactory = new ScanFactory();
	   
    // 定义一个变量，来标识是否退出 
    private static boolean isExit = false; 
   
    private static Handler mHandler = new Handler() { 
   
        @Override 
        public void handleMessage(Message msg) { 
            super.handleMessage(msg); 
            isExit = false; 
        } 
    };
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getWindow().addFlags(0x80000000);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
	 	this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
	 	    	WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);	
		//检测license
		TelephonyManager telephonyManager = (TelephonyManager)MainActivity.this.getSystemService( Context.TELEPHONY_SERVICE);		
		BaseApplication.checkLicense(telephonyManager.getDeviceId());
		
		setContentView(R.layout.activity_main);		
		
		if(BaseApplication.hasLicense){
			Log.i(LICENSE_TAG, "has license");
			mWebView=(WebView)findViewById(R.id.webview);
			mWebViewProgressBar = (ProgressBar)findViewById(R.id.index_progressBar);
			
			if(ModelConst.HANDHELD1.equals(BaseApplication.mMachineModel.toUpperCase())){
				scanFactory.createHandHeld1ScanService().startService(getApplicationContext(), mWebView, mWebViewProgressBar);
			}
			
			Router.settingWebView(MainActivity.this, mWebView, mWebViewProgressBar, "");
			
			setInfomationList();
		}else{
			Log.i(LICENSE_TAG, "no license");
			new AlertDialog.Builder(this)
			.setTitle(R.string.check_license_title)
			.setMessage(R.string.check_license_exception)
			.setPositiveButton(R.string.check_license_confirm,
					new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							MainActivity.this.finish();
						}
					}).create().show();
		}			
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(ModelConst.PWS470.equals(BaseApplication.mMachineModel.toUpperCase())){
			Intent intent=new Intent("df.scanservice.toapp");
			this.sendBroadcast(intent);
			scanFactory.createPWS470ScanService().registerBroadCast(getApplicationContext(), mWebView, mWebViewProgressBar);
		}
	}
	
	@Override
	protected void onPause(){
		super.onPause();
		if(ModelConst.PWS470.equals(BaseApplication.mMachineModel.toUpperCase())){
			scanFactory.createPWS470ScanService().unRegisterBroadCast();
		}
	}
	
	@Override 
    public boolean onKeyDown(int keyCode, KeyEvent event) { 
		if (keyCode == KeyEvent.KEYCODE_HOME) {
			
			if(BaseApplication.hasLicense){
				if(ModelConst.HANDHELD1.equals(BaseApplication.mMachineModel.toUpperCase())){
					scanFactory.createHandHeld1ScanService().scan();
				}
			}else{
				new AlertDialog.Builder(this)
				.setTitle(R.string.check_license_title)
				.setMessage(R.string.check_license_exception)
				.setPositiveButton(R.string.check_license_confirm,
						new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								MainActivity.this.finish();
							}
						}).create().show();
			}
		}
		
		if (keyCode == KeyEvent.KEYCODE_BACK) { 
            exit(); 
            return true; 
        } 
        return super.onKeyDown(keyCode, event); 
    }
	
	@Override
	protected void onDestroy() {
		if(scanTimer!= null){
			scanTimer.cancel();
		}
		if(ModelConst.PWS470.equals(BaseApplication.mMachineModel.toUpperCase())){
			scanFactory.createHandHeld1ScanService().destroy();
		}
		super.onDestroy();
	}
	
	
	//退出应用，目前是没有完全进行退出
    private void exit() { 
        if (!isExit) { 
            isExit = true; 
            Toast.makeText(getApplicationContext(), R.string.app_exit_message, 
                    Toast.LENGTH_SHORT).show(); 
            // 利用handler延迟发送更改状态信息 
            mHandler.sendEmptyMessageDelayed(0, 2000); 
        } else { 
        	scanFactory.createHandHeld1ScanService().destroy();
            Log.e(TAG, "exit application"); 
            this.finish();
        } 
    }    

	//设置应用信息列表
	private void setInfomationList() {
		String cacheConfigString = ConfigCache
				.getUrlCache(BaseApplication.mServerLatestUrl);
		if (cacheConfigString != null) {
			try {
				showInfomationList(cacheConfigString);
				checkNewVersion(false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			//若网络不可用，则显示网络连接失败
			if (BaseApplication.mNetWorkState == NetworkUtils.NETWORN_NONE) {
				showFailEmptyView();
				return;
			}

			AsyncHttpClient client = new AsyncHttpClient();
			client.get(BaseApplication.mServerLatestUrl,
					new AsyncHttpResponseHandler() {

						@Override
						public void onStart() {
						}

						@Override
						public void onSuccess(String result) {
							try {
								showInfomationList(result);
								ConfigCache.setUrlCache(result,
										BaseApplication.mServerLatestUrl);
								checkNewVersion(false);
							} catch (Exception e) {
								// 更新出现异常的界面
								showFailEmptyView();
								e.printStackTrace();
							}
						}

						@Override
						public void onFailure(Throwable arg0) {
							// 更新失败的时候的页面
							showFailEmptyView();
						}

					});
		}
	}

	/**
	 * 检查应用的更新
	 * 
	 * @param isManual
	 * : app auto-detect upgrade or user hand click to download in
	 *  list
	 */
	public void checkNewVersion(boolean isManual) {
		if (BaseApplication.mVersionCode < mLatestVersionCode
				&& (BaseApplication.mShowUpdate || isManual)) {
			new AlertDialog.Builder(this)
					.setTitle(R.string.check_new_version)
					.setMessage(mLatestVersionUpdate)
					.setPositiveButton(R.string.app_upgrade_confirm,
							new OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									Intent intent = new Intent(
											MainActivity.this,
											AppUpgradeService.class);
									intent.putExtra("downloadUrl",
											mLatestVersionDownload);
									startService(intent);
								}
							})
					.setNegativeButton(R.string.app_upgrade_cancel,
							new OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									BaseApplication.mShowUpdate = false;
								}
							}).create().show();
		}

		if (BaseApplication.mVersionCode >= mLatestVersionCode && isManual) {
			Toast.makeText(this, R.string.check_new_version_latest,
					Toast.LENGTH_SHORT).show();
		}
	}	

	// 跳转到更新错误页面
	private void showFailEmptyView() {
		
	}

	// 获取显示设备信息列表
	private void showInfomationList(String result) throws JSONException {
		JSONObject statusConfig = new JSONObject(result);

		mLatestVersionCode = statusConfig.optInt("version-code");
		mLatestVersionUpdate = statusConfig.optString("version-update");
		mLatestVersionDownload = BaseApplication.mDomain
				+ statusConfig.optString("version-download");
		if (mLatestVersionDownload != null) {
			BaseApplication.mApkDownloadUrl = mLatestVersionDownload;
		}
	}
}
