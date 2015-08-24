package kingdee.k3.scm.pda.webapp.license;

import java.io.File;
import java.io.IOException;

import kingdee.k3.scm.pda.webapp.base.BaseApplication;
import kingdee.k3.scm.pda.webapp.utils.FileUtils;
import kingdee.k3.scm.pda.webapp.utils.StringUtils;
import android.util.Log;

/*
 * author: hongbo_liang 
 * date： 2015-08-12
 * description: 用于处理软件和硬件的绑定验证
 */
public class ConfigLicense {
    private static final String TAG = ConfigLicense.class.getName();
   
    //获取 license 中的文件的路径
    public static String getLicense(String url) {
		if(url == null){
			return null;
		}

		String result = null;
		File file = new File(BaseApplication.mSdcardLicenseDir + "/" + StringUtils.replaceUrlWithPlus(url));
		if (file.exists() && file.isFile()) {
		//获取文件的修改时间
		long expiredTime = System.currentTimeMillis() - file.lastModified();
		Log.d(TAG, file.getAbsolutePath() + " expiredTime:" + expiredTime/60000 + "min");
		    try {
		        result = FileUtils.readTextFile(file);
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
		}
		return result;
    }
    
    public static String getGenerateLicense(){
    	RegSoft regSoft = new RegSoft();
    	String licence = regSoft.getLicense();
    	
    	return licence;
    }
}
