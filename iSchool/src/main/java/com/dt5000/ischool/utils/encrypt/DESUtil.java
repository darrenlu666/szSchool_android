package com.dt5000.ischool.utils.encrypt;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

import android.annotation.SuppressLint;

/**
 * DES加密工具类
 * 
 * @author 周锋
 * @date 2016年1月18日 下午3:02:26
 * @ClassInfo com.dt5000.ischool.utils.encrypt.DESUtil
 * @Description
 */
public class DESUtil {

	private static String KEY = "diantong";

	private static byte[] IV = new byte[] { 1, 2, 3, 4, 5, 6, 7, 8 };

	/**
	 * DES加密
	 * 
	 * @param encryptStr
	 *            待加密字符串
	 * @return
	 */
	@SuppressLint("TrulyRandom")
	public static String encrypt(String encryptStr) {
		String result = "";
		try {
			DESKeySpec desKeySpec = new DESKeySpec(KEY.getBytes("UTF-8"));
			SecretKeyFactory secretKeyFactory = SecretKeyFactory
					.getInstance("DES");
			SecretKey secretKey = secretKeyFactory.generateSecret(desKeySpec);

			IvParameterSpec ivParameterSpec = new IvParameterSpec(IV);
			Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
			byte[] encryptBytes = cipher.doFinal(encryptStr.getBytes("UTF-8"));

			result = Base64.encode(encryptBytes);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * DES解密
	 * 
	 * @param decryptStr
	 *            待解密字符串
	 * @return
	 */
	public static String decrypt(String decryptStr) {
		String result = "";
		try {
			DESKeySpec desKeySpec = new DESKeySpec(KEY.getBytes("UTF-8"));
			SecretKeyFactory secretKeyFactory = SecretKeyFactory
					.getInstance("DES");
			SecretKey secretKey = secretKeyFactory.generateSecret(desKeySpec);

			IvParameterSpec ivParameterSpec = new IvParameterSpec(IV);
			Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
			byte[] decryptBytes = cipher.doFinal(Base64.decode(decryptStr));

			result = new String(decryptBytes, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

}
