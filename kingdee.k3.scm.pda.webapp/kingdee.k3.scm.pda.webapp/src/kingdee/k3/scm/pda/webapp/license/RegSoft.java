package kingdee.k3.scm.pda.webapp.license;

import java.security.MessageDigest;

import kingdee.k3.scm.pda.webapp.base.BaseApplication;
import android.util.Log;

/*
 * @author: hongbo_liang
 * @date: 2015-08-01
 * @description: 用于读取license文件，判断是否有 K3 许可文件
 */
 class RegSoft {
	private int len = 20;
	private int[] intCode = new int[127];    //存储密钥	
	
	//默认构造函数
	public RegSoft(){
		len = BaseApplication.mSN.trim().length() + BaseApplication.mMIEI.trim().length();
	}
	
	public RegSoft(String sn, String miei){
		len = sn.length() + miei.length();
	}
	
   //初始化密钥
    private void setIntCode()
    {
        for (int i = 1; i < intCode.length; i++)
        {
            intCode[i] = i % 9;
        }
    }
    
    public String getLicense(){    		
		String pdaLic = string2MD5(getRNum(BaseApplication.mSN,BaseApplication.mMIEI));
		
		return pdaLic;
    }
    
	//生成序列号
	private String getRNum(String sn,String miei){
		char[] charCode = new char[len];  //存储ASCII码
		int[] intNumber = new int[len];   //存储ASCII码值
		setIntCode();
		String strMNum = sn.toUpperCase() + miei.toUpperCase();
        strMNum = strMNum.substring(0, len - 1);

        for (int i = 1; i < charCode.length; i++)   //存储机器码
        {
        	char [] str = strMNum.toCharArray();
            charCode[i] = str[i-1];
        }
        for (int j = 1; j < intNumber.length; j++)  //改变ASCII码值
        {
            intNumber[j] = (int)(charCode[j]) + intCode[(int)(charCode[j])];
        }
        String strAsciiName = "";   //注册码
        for (int k = 1; k < intNumber.length; k++)  //生成注册码
        {

            if ((intNumber[k] >= 48 && intNumber[k] <= 57) || (intNumber[k] >= 65 && intNumber[k]
                <= 90) || (intNumber[k] >= 97 && intNumber[k] <= 122))  //判断如果在0-9、A-Z、a-z之间
            {
                strAsciiName +=  (char)(intNumber[k]);
            }
            else if (intNumber[k] > 122)  //判断如果大于z
            {
                strAsciiName +=  (char)(intNumber[k] - 10);
            }
            else
            {
                strAsciiName +=  (char)(intNumber[k] - 9);
            }
        }
        return strAsciiName;
	}
	
	//将字符串进行加密处理 md5 32
	private String string2MD5(String inStr){  
        MessageDigest md5 = null;  
        try{  
            md5 = MessageDigest.getInstance("MD5");  
        }catch (Exception e){ 
        	Log.d("license",e.getMessage()); 
        }  
        
        char[] charArray = inStr.toCharArray();  
        byte[] byteArray = new byte[charArray.length];  
  
        for (int i = 0; i < charArray.length; i++)  
            byteArray[i] = (byte) charArray[i];  
        byte[] md5Bytes = md5.digest(byteArray);  
        StringBuffer hexValue = new StringBuffer();  
        for (int i = 0; i < md5Bytes.length; i++){  
            int val = ((int) md5Bytes[i]) & 0xff;  
//            if (val < 16)  
//                hexValue.append("0");  
            hexValue.append(Integer.toHexString(val));  
        }  
        return hexValue.toString();    
    }	
}