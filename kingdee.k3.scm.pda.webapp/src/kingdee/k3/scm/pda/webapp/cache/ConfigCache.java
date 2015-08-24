package kingdee.k3.scm.pda.webapp.cache;

import java.io.File;
import java.io.IOException;

import android.os.Environment;
import android.util.Log;
import kingdee.k3.scm.pda.webapp.base.BaseApplication;
import kingdee.k3.scm.pda.webapp.utils.FileUtils;
import kingdee.k3.scm.pda.webapp.utils.NetworkUtils;
import kingdee.k3.scm.pda.webapp.utils.StringUtils;

public class ConfigCache {
    private static final String TAG = ConfigCache.class.getName();

    public static final int CONFIG_CACHE_MOBILE_TIMEOUT  = 7200000;  //2 hour
    public static final int CONFIG_CACHE_WIFI_TIMEOUT    = 1800000;   //30 minute

    public static String getUrlCache(String url) {
        if (url == null) {
            return null;
        }

        String result = null;
        File file = new File(BaseApplication.mSdcardDataDir + "/" + StringUtils.replaceUrlWithPlus(url));
        if (file.exists() && file.isFile()) {
            long expiredTime = System.currentTimeMillis() - file.lastModified();
            Log.d(TAG, file.getAbsolutePath() + " expiredTime:" + expiredTime/60000 + "min");
            //1. in case the system time is incorrect (the time is turn back long ago)
            //2. when the network is invalid, you can only read the cache
            if (BaseApplication.mNetWorkState != NetworkUtils.NETWORN_NONE && expiredTime < 0) {
                return null;
            }
            if(BaseApplication.mNetWorkState == NetworkUtils.NETWORN_WIFI && expiredTime > CONFIG_CACHE_WIFI_TIMEOUT) {
                return null;
            } else if (BaseApplication.mNetWorkState == NetworkUtils.NETWORN_MOBILE && expiredTime > CONFIG_CACHE_MOBILE_TIMEOUT) {
                return null;
            }
            try {
                result = FileUtils.readTextFile(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static void setUrlCache(String data, String url) {
        if (BaseApplication.mSdcardDataDir == null) {
            return;
        }
        File dir = new File(BaseApplication.mSdcardDataDir);
        if (!dir.exists() && Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            dir.mkdirs();
        }
        File file = new File(BaseApplication.mSdcardDataDir + "/" + StringUtils.replaceUrlWithPlus(url));
        try {
            //创建缓存数据到磁盘，就是创建文件
            FileUtils.writeTextFile(file, data);
        } catch (IOException e) {
            Log.d(TAG, "write " + file.getAbsolutePath() + " data failed!");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * delete cahce file recursively
     * @param cacheFile if null means clear cache function, or clear cache file
     */
    public static void clearCache(File cacheFile) {
        if (cacheFile == null) {
            if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
                try {
                    File cacheDir = new File(Environment.getExternalStorageDirectory().getPath() +  "/" + BaseApplication.mAppId + "/");
                    if (cacheDir.exists()) {
                        clearCache(cacheDir);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (cacheFile.isFile()){
            cacheFile.delete();
        } else if (cacheFile.isDirectory()) {
            File[] childFiles = cacheFile.listFiles();
            for (int i = 0; i < childFiles.length; i++) {
                clearCache(childFiles[i]);
            }
        }
    }
}
