/**
 * 
 */
package DES;

import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

import org.apache.commons.codec.binary.Base64;



//https://blog.csdn.net/liupeifeng3514/article/details/89713430
public class DES {
//	String key = "12345678";
	
	public static final String KEY_ALGORITHM = "DES";

	public static final String CIPHER_ALGORITHM = "DES/CBC/PKCS5Padding";
	
	public static final String ENCODE = "UTF-8";
	
	private final static String DEFAULT_KEY = "A1B2C3D4E5F60708";
	
	private static String DES_IV = "JM23456*";
	
	
	
	//获取秘钥对象
	private static SecretKey keyGenerator(String keyStr) throws Exception{
		
		byte input[] = HexStringToBytes(keyStr);
		DESKeySpec desKey = new DESKeySpec(input);
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(KEY_ALGORITHM);
		SecretKey secretKey = keyFactory.generateSecret(desKey);	
		
		return secretKey;	
	}

	private static int parse(char c) {
		
		if(c >= 'a') {
			return (c - 'a' + 10)& 0x0f;
		}
		if(c >= 'A') {
			return (c - 'A' + 10)& 0x0f;
		}
		return (c - '0' + 10)& 0x0f;		
	}
	
	private static byte[] HexStringToBytes(String hexstr) {

		byte[] results = new byte[hexstr.length()/2];
		
		for(int i = 0; i < results.length; i++) {
			char c0 = hexstr.charAt(i*2 + 0);
			char c1 = hexstr.charAt(i*2 + 1);
			results[i] = (byte)((parse(c0)<<4)|parse(c1));
		}
		return results;
	}
	
	public String encrypt(String data) throws Exception {
		
		Key deskey = keyGenerator(DEFAULT_KEY);
		
		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
		
		IvParameterSpec iv = new IvParameterSpec(DES_IV.getBytes("UTF-8"));
		
		cipher.init(Cipher.ENCRYPT_MODE,deskey,iv);
		
		byte[] results = cipher.doFinal(data.getBytes(ENCODE));
				
		return Base64.encodeBase64String(results);
		
	}
	
	
	public String decrypt(String data) throws Exception{
		
		Key deskey = keyGenerator(DEFAULT_KEY);
		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
		IvParameterSpec iv = new IvParameterSpec(DES_IV.getBytes("UTF-8"));	
		cipher.init(Cipher.DECRYPT_MODE,deskey,iv);
		
		byte[] results = Base64.decodeBase64(data);
		results = cipher.doFinal(results);	
		return new String(results, ENCODE);	
	
	}

	
}
