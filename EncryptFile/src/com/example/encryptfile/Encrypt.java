package com.example.encryptfile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Encrypt {
	//Generate key from password
	public static byte[] generateKey(String password) throws Exception {
		byte[] keyStart = password.getBytes("UTF-8");

		KeyGenerator kgen = KeyGenerator.getInstance("AES");
		SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "Crypto");
		sr.setSeed(keyStart);
		
		//Init key with length 128 bit
		kgen.init(128, sr);
		SecretKey skey = kgen.generateKey();
		return skey.getEncoded();
	}
public static byte[] generateFileKey() throws Exception {

		KeyGenerator kgen = KeyGenerator.getInstance("AES");
		SecureRandom sr = new SecureRandom();
		
		//Init key with length 128 bit
		kgen.init(128, sr);
		SecretKey skey = kgen.generateKey();
		return skey.getEncoded();
	}
	
	//Encrypt file by key byte[]
public static byte[] encodeFile(byte[] key, byte[] fileData)
			throws Exception {

		//Init secret key
		SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec);

		//Encrypt file byte[]
		byte[] encrypted = cipher.doFinal(fileData);

		return encrypted;
	}

	//Decrypt file byte[]
	public static byte[] decodeFile(byte[] key, byte[] fileData)
			throws Exception {
		SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.DECRYPT_MODE, skeySpec);

		byte[] decrypted = cipher.doFinal(fileData);

		return decrypted;
	}
	
	//Read file to byte[]
	public static byte[] read(File file) throws IOException {
	    byte[] buffer = new byte[(int) file.length()];
	    InputStream ios = null;
	    try {
	        ios = new FileInputStream(file);
	        if (ios.read(buffer) == -1) {
	            throw new IOException(
	                    "EOF reached while trying to read the whole file");
	        }
	    } finally {
	        try {
	            if (ios != null)
	                ios.close();
	        } catch (IOException e) {
	        }
	    }
	    return buffer;
	}
}
