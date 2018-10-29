/*
 * Copyright (C) 2010 The MobileSecurePay Project
 * All right reserved.
 * author: shiqun.shi@alipay.com
 *
 *  提示：如何获取安全校验码和合作身份者id
 *  1.用您的签约支付宝账号登录支付宝网站(www.alipay.com)
 *  2.点击“商家服务”(https://b.alipay.com/order/myorder.htm)
 *  3.点击“查询合作者身份(pid)”、“查询安全校验码(key)”
 */

package com.dt5000.ischool.utils.alipay;

// 请参考 Android平台安全支付服务(msp)应用开发接口(4.2 RSA算法签名)部分，并使用压缩包中的openssl RSA密钥生成工具，生成一套RSA公私钥。
// 这里签名时，只需要使用生成的RSA私钥。
// Note: 为安全起见，使用RSA私钥进行签名的操作过程，应该尽量放到商家服务器端去进行。
public final class Keys {

	// 合作商户ID，用签约支付宝账号登录www.alipay.com后，在商家服务页面中获取，以2088开头的16位纯数字
	public static final String DEFAULT_PARTNER = "2088701422943482";

	// 商户收款的支付宝账号
	public static final String DEFAULT_SELLER = "2088701422943482";

	// 商户（RSA）私钥，自助生成
	public static final String PRIVATE = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAMTlJDyhMDYrEeJ+BgoNMu0R8ZSW9bcizYCzi5UiTyOuA/CG3jKsGM/0GG0+g+x4KKFltF/ikaGjTymSFhAykDA919Hc7BYioy8tNtncQVRC5vLanC8iHK5QtRZunavGZrtS8RVp62sKl/5jmnNvpeDqH38O7Ro6Gac7wv5cTOvVAgMBAAECgYEAh4Q7C6vZlf7Q4XXhzDtHaNonzyCT4EeI7+Mj2DJ0C9eyiHMbLf87WK3lvhMAE9qkBGoO2swDdiAD1VXh76YNjKBae39LwHHmmACL/i9uV7jZao+rhGrSXuE8c6ZDOQ/RbR9JUPps4ot/FCt0RRJf3OarbpOB1gYBmhPRu6u31SECQQD08VcBKjIv5HrNREzRd84Uy4g54jQmv50agVBf671ttnQhmCS8L8lECEYiLp9IABdphiL+iGCKrSWTkZyzi9CtAkEAzciLBfDw34FQfLAMVrzt+HrBUDWPxZLH5jMzewn+icFZmvmxtixLWq+GzBGPAJE04imx4cvPZ+eYDLpXzdzkyQJBAON2a1GNmeWXy+JrFts+4oW1LOB0C/If5wkCJV2uUc9crCO2YgbQaZtgA6Eioo/+Zb987Wppwx2FBWwwwNCLelECQAu76LmBAKst+4Hwo/N6OHOXxFgnhbg1Y3Kr7r6QVlRSkyJ0JH8HzZAxPBV81tOodASFbiA69+ur2A65vXH+q4kCQFRN8B37aOIazk7/0KbX6hMpMmOjbIXjeh4akX3Y38c82PW450LjtrJPaIKEWTQ1/qIO8INKtzO1rk2gU1oz5yM=";

	// 支付宝（RSA）公钥，无需修改
	public static final String PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";

}
