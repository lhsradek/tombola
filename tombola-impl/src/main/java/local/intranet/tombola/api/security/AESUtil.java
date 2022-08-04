package local.intranet.tombola.api.security;

import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.security.crypto.codec.Hex;

/**
 * 
 * {@link AESUtil} for {@link local.intranet.tombola.api.controller.IndexController}
 * <p>
 * https://www.baeldung.com/java-aes-encryption-decryption
 * <br>
 * https://github.com/eugenp/tutorials/blob/master/core-java-modules/core-java-security-algorithms/src/main/java/com/baeldung/aes/AESUtil.java
 * 
 */
public class AESUtil {
	
	private static final String AESUTIL_AES = "AES";
	private static final String AESUTIL_AES_PADDING = "AES/CBC/PKCS5Padding";
	private static final String AESUTIL_PBKDF2_WITH_HMAC_SHA256 = "PBKDF2WithHmacSHA256";

    /**
     * 
     * Encrypt
     * 
     * @param input      {@link String}
     * @param key        {@link SecretKey}
     * @param iv         {@link IvParameterSpec}
     * @return {@link String}
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
	public static String encrypt(String input, SecretKey key, IvParameterSpec iv) 
    		throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException,
    		InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(AESUTIL_AES_PADDING);
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        byte[] cipherText = cipher.doFinal(input.getBytes());
        String ret = Base64.getEncoder().encodeToString(cipherText); 
        return ret;
    }

	/**
	 * 
	 * Decrypt
	 * 
	 * @param cipherText {@link String}
     * @param key        {@link SecretKey}
     * @param iv         {@link IvParameterSpec}
     * @return {@link String}
	 * @throws NoSuchPaddingException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidAlgorithmParameterException
	 * @throws InvalidKeyException
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 */
    public static String decrypt(String cipherText, SecretKey key, IvParameterSpec iv)
    		throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException,
    		InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
    	Cipher cipher = Cipher.getInstance(AESUTIL_AES_PADDING.toUpperCase());
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        byte[] ret = cipher.doFinal(Base64.getDecoder().decode(cipherText));
        return new String(ret);
    }

    /**
     * 
     * Generate key
     * 
     * @param n (128, 192 a 256) 
     * @return {@link SecretKey}
     * @throws NoSuchAlgorithmException
     */
    public static SecretKey generateKey(int n) {
		try {
			KeyGenerator keyGenerator = KeyGenerator.getInstance(AESUTIL_AES);
	        keyGenerator.init(n);
	        SecretKey ret = keyGenerator.generateKey();
	        return ret;
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
    }
    
    /**
     * 
     * Generate salt
     * 
     * @return {@link String}
     */
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte ret[] = new byte[20];
        random.nextBytes(ret);
        return new String(ret);
    }

    /**
     * 
     * Get key from password
     * 
     * @param password {@link String}
     * @param salt     {@link String}
     * @return {@link SecretKey}
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static SecretKey getKeyFromPassword(String password, String salt)
    		throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance(AESUTIL_PBKDF2_WITH_HMAC_SHA256);
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, 256);
        SecretKey ret = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), AESUTIL_AES);
        return ret;
    }

    /**
     * 
     * Generate Iv
     * 
     * @return {@link IvParameterSpec}
     */
    public static IvParameterSpec generateIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        IvParameterSpec ret = new IvParameterSpec(iv); 
        return ret;
    }

	/**
	 * 
	 * Get plain text from Base64
	 * 
	 * @param data base64 
	 * @return plain text
	 */
	public static String getBase64(String data) {
		String ret = "";
        if (data != null) {
        	ret = new String(Base64.getDecoder().decode(data));
        }
        return ret;
	}

	/**
	 * 
	 * Set to Base64
	 * 
	 * @param data plain text
	 * @return base64
	 */
	public static String setBase64(String data) {
        String ret = new String(Base64.getEncoder()
        		.encode(data.getBytes(Charset.forName("UTF-8"))));
        return ret;
	}

	/**
	 * 
	 * Get plain text from hex
	 * 
	 * @param data as hex 
	 * @return plain text
	 */
	public static String getHex(String data) {
		String ret = "";
		if (data != null) {
			ret = new String(Hex.decode(data));
		}
        return ret;
	}

	/**
	 * 
	 * Set to Hex
	 * 
	 * @param data plain text
	 * @return hex
	 */
	public static String setHex(String data) {
        String ret = new String(Hex.encode(data.getBytes(Charset.forName("UTF-8"))));
		return ret;
	}
	
}
