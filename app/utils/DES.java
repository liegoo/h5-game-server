package utils;


import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
 
/**
 * des加密类
 * @author cai9911
 *
 */
public class DES {
    private String Algorithm = "DES";
    private KeyGenerator keygen;
    private SecretKey deskey;
    private Cipher c;
    private byte[] cipherByte;


    /**
     * 初始化 DES 实例
     */
    public DES() {
        init();
    }
    
    /**
     * 初始化
     */
    public void init() {
        Security.addProvider(new com.sun.crypto.provider.SunJCE());
        try {
            keygen = KeyGenerator.getInstance(Algorithm);
            deskey = keygen.generateKey();
            c = Cipher.getInstance(Algorithm);
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        } catch (NoSuchPaddingException ex) {
            ex.printStackTrace();
        }
    }


    /**
     * 进行加密
     *
     * @param str 要加密的数据
     * @return 返回加密后的 byte 数组
     */
    public byte[] createEncryptor(String str) {
        try {
            c.init(Cipher.ENCRYPT_MODE, deskey);
            cipherByte = c.doFinal(str.getBytes());
        } catch (java.security.InvalidKeyException ex) {
            ex.printStackTrace();
        } catch (javax.crypto.BadPaddingException ex) {
            ex.printStackTrace();
        } catch (javax.crypto.IllegalBlockSizeException ex) {
            ex.printStackTrace();
        }
        return cipherByte;
    }


    /**
     * Byte 数组进行解密
     *
     * @param buff 要解密的数据
     * @return 返回加密后的 String
     */
    public String createDecryptor(byte[] buff) {
        try {
            c.init(Cipher.DECRYPT_MODE, deskey);
            cipherByte = c.doFinal(buff);
        } catch (java.security.InvalidKeyException ex) {
            ex.printStackTrace();
        } catch (javax.crypto.BadPaddingException ex) {
            ex.printStackTrace();
        } catch (javax.crypto.IllegalBlockSizeException ex) {
            ex.printStackTrace();
        }
        return (new String(cipherByte));
    }


    /**
     * 已知密钥的情况下加密
     */
    public static String encode(String str, String key) throws Exception {
        SecureRandom sr = new SecureRandom();
        byte[] rawKey = Base64.decode(key);
        
        DESKeySpec dks = new DESKeySpec(rawKey);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = keyFactory.generateSecret(dks);

        javax.crypto.Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, sr);

        byte[] data = str.getBytes("UTF8");
        byte[] encryptedData = cipher.doFinal(data);
        return new String(Base64.encode(encryptedData));
    }


    /**
     * 已知密钥的情况下解密
     *
     * @param str 加密字符串
     * @param key key
     * @return decode
     * @throws Exception Exception
     */
    public static String decode(String str, String key) throws Exception {
        SecureRandom sr = new SecureRandom();
        byte[] rawKey = Base64.decode(key);
        DESKeySpec dks = new DESKeySpec(rawKey);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = keyFactory.generateSecret(dks);
        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, sr);
        byte[] encryptedData = Base64.decode(str);
        byte[] decryptedData = cipher.doFinal(encryptedData);
        return new String(decryptedData, "UTF8");
    }

    /**
     * 生成 DESKey
     *
     * @return DESKey �?��符串形式保存
     * @throws java.security.NoSuchAlgorithmException
     *          该算法不存在
     */

    public static String generatorDESKey() throws NoSuchAlgorithmException {
        KeyGenerator keygen = KeyGenerator.getInstance("DES");
        SecretKey desKey = keygen.generateKey();
        return new String(desKey.getEncoded());

    }

    /**
     * test
     * @param args
     */
    public static void main(String[] args){

        //String result = null;
        try {
            //result = generatorDESKey();
            String str = "{\"gameid\":3,\"uid\":\"257776605011868283\",\"sessionid\":\"1234567890s\"}";
            String ss = DES.encode(str,"2222222222222222");
//            System.out.println("密文：" + ss);
//            System.out.println("解密：" + DES.decode(ss, "2222222222222222"));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
       // System.out.println("result = " + result);
    }
}
