package kingdee.k3.scm.pda.webapp.base;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

import kingdee.k3.scm.pda.webapp.R;
import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import kingdee.k3.scm.pda.webapp.cache.ConfigCache;
import kingdee.k3.scm.pda.webapp.db.BaseSQLiteHelper;
import kingdee.k3.scm.pda.webapp.license.ConfigLicense;
import kingdee.k3.scm.pda.webapp.sync.http.AsyncHttpClient;
import kingdee.k3.scm.pda.webapp.sync.http.AsyncHttpResponseHandler;
import kingdee.k3.scm.pda.webapp.utils.NetworkUtils;
import kingdee.k3.scm.pda.webapp.utils.PreferencesUtils;

/*
 * author: hongbo_liang	
 * date: 2015-08-12
 * description:  application 抽象类，使用时需要对其进行继承，并在 mainfest 中修改 application 节点
 */
public abstract class BaseApplication extends Application {

	   public static BaseSQLiteHelper mSQLiteHelper;
	    public static final String DOMAIN_URL = "url";
	    public static String mDomain = "";
	    public static String mBakeDomain = "";

	    public static String mAppId;

	    public static int mNetWorkState = NetworkUtils.NETWORN_NONE;

	    public static String mDownloadPath;
	    public static int mVersionCode;
	    public static String mVersionName;
	    public static boolean mShowUpdate = true;
	    
	    //可应用于安卓市场，或者其他的应用市场
	    private static String mMarketName; // goapk etc.

	    public static String mSdcardDataDir;
	    public static String mApkDownloadUrl = null;

	    public static String mServerLatestUrl;
	    public static String mServerPageUrl;
	    public static String mServerSetUrl;
	    public static String mServerSeasonUrl;
	    public static String mMachineModel = Build.MODEL; //产品的型号
	    
	    //license 标识
	    public static boolean hasLicense = false;
	    public static String mSdcardLicenseDir;
	    public static String mSN = "";
	    public static String mMIEI = "";

	    public static int mMaxPage;
	    
	    @Override
	    public void onCreate() {
	        initEnv();
	        initLocalVersion();
	    }	    
	    
	    public void initLocalVersion(){
	        PackageInfo pinfo;
	        //ApplicationInfo ainfo;
	        try {
	            pinfo = this.getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_CONFIGURATIONS);
	            mVersionCode = pinfo.versionCode;
	            mVersionName = pinfo.versionName;

	            //ainfo = this.getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
	            //mMarketName = ainfo.metaData.getString("UMENG_CHANNEL");
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }

	    public void initEnv() {
	        mAppId = getString(R.string.app_id);
	        mSQLiteHelper = new BaseSQLiteHelper(getApplicationContext(), mAppId+ ".db", 1);
	        mDownloadPath = "/" + mAppId+ "/download";

	        mServerLatestUrl = mDomain + mAppId + "/data/json/latest.json";
	        mServerPageUrl = mDomain + mAppId + "/data/json/pages/";
	        mServerSetUrl = mDomain + mAppId + "/data/json/set.json";
	        mServerSeasonUrl = mDomain + mAppId + "/data/json/seasons/";

	        if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
	            File file = new File(Environment.getExternalStorageDirectory().getPath() +  "/" + mAppId + "/config/");
	            if(!file.exists()) {
	                if (file.mkdirs()) {
	                    mSdcardDataDir = file.getAbsolutePath();
	                }
	            } else {
	                mSdcardDataDir = file.getAbsolutePath();
	            }
	        }

	        mNetWorkState = NetworkUtils.getNetworkState(this);
	        if(mDomain != null && mDomain != ""){
	        	//checkDomain(mDomain, false);
	        }
	    }

	    public void checkDomain(final String domain, final boolean stop){
	        mDomain = PreferencesUtils.getStringPreference(getApplicationContext(), DOMAIN_URL, mDomain);
	        String cacheConfigString = ConfigCache.getUrlCache(domain + "host.json");
	        if (cacheConfigString != null) {
	            updateDomain(cacheConfigString);
	        } else {
	            AsyncHttpClient client = new AsyncHttpClient();
	            client.get(domain + "host.json", new AsyncHttpResponseHandler(){

	                @Override
	                public void onStart() {
	                }

	                @Override
	                public void onSuccess(String result) {
	                    ConfigCache.setUrlCache(result, domain + "host.json");
	                    updateDomain(result);
	                }

	                @Override
	                public void onFailure(Throwable arg0) {
	                    if (!stop) {
	                        checkDomain(mBakeDomain, true);
	                    }
	                }

	                @Override
	                public void onFinish() {
	                }
	            });
	        }
	    }

	    public void updateDomain(String result) {
	        try {
	            JSONObject appreciateConfig = new JSONObject(result);
	            String domain = appreciateConfig.optString("domain");
	            if (domain != null && !"".equals(domain)) {
	                BaseApplication.mDomain = domain;
	                PreferencesUtils.setStringPreferences(getApplicationContext(), DOMAIN_URL, domain);
	            }
	        } catch (JSONException e) {
	            e.printStackTrace();
	        }
	    }

	    public static boolean isForbiddenAdWall() {
	        if ("goapk".equals(mMarketName)) {
	            return true;
	        }
	        return false;
	    }
	    
	    
	    //用于检测是否存在合法的license信息
	    public static void checkLicense(String mMIEI){
	    	if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
	    		File file = new File(Environment.getExternalStorageDirectory().getPath() +  "/" + mAppId + "/license/");
	    		if(!file.exists()) {
	                if (file.mkdirs()) {
	                	mSdcardLicenseDir = file.getAbsolutePath();
	                }
	            } else {
	            	mSdcardLicenseDir = file.getAbsolutePath();
	            }
	    	}
	    	
	    	BaseApplication.mSN = Build.SERIAL.toUpperCase();
	    	BaseApplication.mMIEI = mMIEI.replace(" ", "").toUpperCase();
	    	
	    	String generateLicense = ConfigLicense.getGenerateLicense();
	    	String licenceLocal = ConfigLicense.getLicense(BaseApplication.mSN + ".license");
	    	if(generateLicense != null && licenceLocal != null){	 
	    		licenceLocal = licenceLocal.toLowerCase();
	    		generateLicense = generateLicense.toLowerCase();
		    	if(generateLicense.equals(licenceLocal.replaceAll("\r\n", ""))){
		    		hasLicense = true;
		    	}else{
		    		hasLicense = false;
		    	}
	    	}else{
	    		hasLicense = false;
	    	}
	    }
}
