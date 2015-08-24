package com.kingdee.utils;

import java.nio.charset.Charset;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;
import java.nio.charset.Charset;
 


import android.annotation.SuppressLint;
import com.qq.weixin.mp.aes.AesException;
import com.qq.weixin.mp.aes.PKCS7Encoder;

@SuppressLint("NewApi")
public class DecodeMsg {
	static Charset CHARSET = Charset.forName("utf-8");
	@SuppressLint("NewApi")
	public static String decode(String encrptMsg,String encodingAesKey) throws AesException{
		byte[] aesKey;
		byte[] original;
		
		//aesKey = Base64.decodeBase64(encodingAesKey + "=");
		aesKey=Base64.decodeBase64((encodingAesKey+"=").getBytes());
		try {
			// 设置解密模式为AES的CBC模式
			Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
			SecretKeySpec key_spec = new SecretKeySpec(aesKey, "AES");

            IvParameterSpec iv=new IvParameterSpec(Arrays.copyOfRange(aesKey,0,16));
            cipher.init(Cipher.DECRYPT_MODE,key_spec,iv);
			// 使用BASE64对密文进行解码
			//byte[] encrypted = Base64.decodeBase64(encrptMsg);
			byte[] encrypted = Base64.decodeBase64(encrptMsg.getBytes());
			// 解密
			original = cipher.doFinal(encrypted);
		} catch (Exception e) {
			e.printStackTrace();
			throw new AesException(AesException.DecryptAESError);
		}

		String xmlContent, from_appid;
		try {
			// 去除补位字符
			byte[] bytes = PKCS7Encoder.decode(original);

			// 分离16位随机字符串,网络字节序和AppId
			byte[] networkOrder = Arrays.copyOfRange(bytes, 16, 20);

			int xmlLength = recoverNetworkBytesOrder(networkOrder);

			xmlContent = new String(Arrays.copyOfRange(bytes, 20, 20 + xmlLength), CHARSET);
			from_appid = new String(Arrays.copyOfRange(bytes, 20 + xmlLength, bytes.length),
					CHARSET);
		} catch (Exception e) {
			e.printStackTrace();
			throw new AesException(AesException.IllegalBuffer);
		}

		/*// appid不相同的情况
		if (!from_appid.equals(appId)) {
			throw new AesException(AesException.ValidateAppidError);
		}*/
		return xmlContent;
		
		
	}
	// 还原4个字节的网络字节序
		public static int recoverNetworkBytesOrder(byte[] orderBytes) {
			int sourceNumber = 0;
			for (int i = 0; i < 4; i++) {
				sourceNumber <<= 8;
				sourceNumber |= orderBytes[i] & 0xff;
			}
			return sourceNumber;
		}
}
