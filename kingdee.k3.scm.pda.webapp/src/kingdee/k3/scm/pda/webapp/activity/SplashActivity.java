package kingdee.k3.scm.pda.webapp.activity;

import java.io.File;

import kingdee.k3.scm.pda.webapp.R;
import kingdee.k3.scm.pda.webapp.base.BaseApplication;
import kingdee.k3.scm.pda.webapp.cache.ConfigCache;
import kingdee.k3.scm.pda.webapp.utils.DownloadUtils;
import kingdee.k3.scm.pda.webapp.utils.FileUtils;
import kingdee.k3.scm.pda.webapp.utils.NetworkUtils;
import kingdee.k3.scm.pda.webapp.utils.StringUtils;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.widget.TextView;
import android.widget.Toast;

public class SplashActivity extends BaseActivity {
	private static final int FAILURE = 0;
    private static final int SUCCESS = 1;
    private static final int OFFLINE = 2;

    private static final int SHOW_TIME_MIN = 800;

    private TextView mVersionNameText;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        mVersionNameText = (TextView) findViewById(R.id.version_name);
        mVersionNameText.setText(BaseApplication.mVersionName);

        new AsyncTask<Void, Void, Integer>() {

            @Override
            protected Integer doInBackground(Void... params) {
                int result;
                long startTime = System.currentTimeMillis();
                result = loadingCache();
                long loadingTime = System.currentTimeMillis() - startTime;
                if (loadingTime < SHOW_TIME_MIN) {
                    try {
                        Thread.sleep(SHOW_TIME_MIN - loadingTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return FAILURE;
                    }
                }
                return result;
            }

            @Override
            protected void onPostExecute(Integer result) {
                if (result == OFFLINE) {
                    Toast.makeText(SplashActivity.this, R.string.check_new_version_no_network, Toast.LENGTH_SHORT).show();
                }
                Intent intent = new Intent();
                intent.setClassName(SplashActivity.this, getString(R.string.splash_out_activity));
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            };
        }.execute(new Void[]{});
    }

    private int loadingCache() {
        if (BaseApplication.mNetWorkState == NetworkUtils.NETWORN_NONE) {
            return OFFLINE;
        }

        String result = ConfigCache.getUrlCache(BaseApplication.mServerLatestUrl);
        if (result != null) {
            return SUCCESS;
        }

        if (BaseApplication.mSdcardDataDir == null) {
            BaseApplication.mSdcardDataDir = Environment.getExternalStorageDirectory().getPath()
                    +  "/" + BaseApplication.mAppId + "/config/";
        }
        File file = new File(BaseApplication.mSdcardDataDir + "/" + StringUtils.replaceUrlWithPlus(BaseApplication.mServerLatestUrl));
        if (file.exists() && file.isFile()) {
            file.delete();
        }
        try {
            DownloadUtils.download(BaseApplication.mServerLatestUrl, file, false, null);
            result = FileUtils.readTextFile(file);
            ConfigCache.setUrlCache(result, BaseApplication.mServerLatestUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return SUCCESS;
    }
}
