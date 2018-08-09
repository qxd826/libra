package com.qxd.libra;


import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.Security;
import java.security.spec.AlgorithmParameterSpec;

/**
 * Created by qxd on 2017/7/21.
 */
public class PasswordEncodeUtil {
    static final String KEY_ALGORITHM = "AES";
    static final String CIPHER_ALGORITHM_CBC = "AES/CBC/NoPadding";
    static final String SECRET_IV = "12_anemz_ANECdke";
    static final String SECRET_KEY = "1234abcd_xyz789t1234abcd_xyz789t";

    /**
     * MD5加密
     * @param str
     * @return String
     * @throws ParseException
     */
/*    public static String encodeMD5(String str){
        return Hashing.md5().newHasher().putString(str, Charsets.UTF_8).hash().toString();
    }*/

    /**
     * 用户密码加密
     *
     * @param password
     *
     * @return String
     *
     * @throws Exception
     */
    public static String encodePassword(String password) throws Exception {
       /* Security.addProvider(new BouncyCastleProvider());
        SecretKeySpec skc = new SecretKeySpec(SECRET_KEY.getBytes(), KEY_ALGORITHM);
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM_CBC);
        //算法参数
        AlgorithmParameterSpec paramSpec = new IvParameterSpec(SECRET_IV.getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, skc, paramSpec);
        byte[] input = password.getBytes("UTF-8");
        int len = input.length;
        byte[] cipherText = new byte[cipher.getOutputSize(len)];
        cipher.update(input, 0, len, cipherText, 0);
        return Base64.encodeBase64String(cipherText);*/
        return "";
    }
}
