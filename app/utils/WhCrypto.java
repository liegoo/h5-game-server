package utils;

import jws.Jws;

public class WhCrypto
{
    static {
        System.load(Jws.configuration.getProperty("whcrypto.lib"));
    }

    /**
     * returns bytes of the encrypted data
     */
    public static native byte[] encrypt(byte[] data);

    /**
     * data - encrypted data, data will also be used to store decrypted data
     *
     * returns length of the original data
     */
    public static native int decrypt(byte[] data);

    /**
     * Note: data_len must be multiple of 8
     * data - the data to be encrypted, and will also be used
     *        to store the encrypted data.
     * return 0 if encryption succeeded.
     */ 
    public static native int encryptBlock(byte[] data);

    /**
     * Note: data_len must be multiple of 8
     * data - the data to be decrypted, and will also be used
     *        to store the decrypted data.
     * return 0 if decryption succeeded.
     */ 
    public static native int decryptBlock(byte[] data);
}
