package com.example.yhj.mobilesafe.utils;


import android.util.Base64;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Crypto {
	/**
	 * Encrypt plain string and encode to Base64
	 * 
	 * @param seed
	 * @param plain
	 * @return
	 * @throws Exception
	 */
	public static String encrypt(String seed, String plain) throws Exception {
		byte[] rawKey = getRawKey(seed.getBytes());
		byte[] encrypted = encrypt(rawKey, plain.getBytes());
		return Base64.encodeToString(encrypted, Base64.DEFAULT);
	}

	/**
	 * Decrypt Base64 encoded encrypted string
	 * 
	 * @param seed
	 * @param encrypted
	 * @return
	 * @throws Exception
	 */
	static String decrypt(String seed, String encrypted)
			throws Exception {
		byte[] rawKey = getRawKey(seed.getBytes());
		byte[] enc = Base64.decode(encrypted.getBytes(), Base64.DEFAULT);
		byte[] result = decrypt(rawKey, enc);
		return new String(result);
	}

	private static byte[] getRawKey(byte[] seed) throws Exception {
		KeyGenerator keygen = KeyGenerator.getInstance("AES");
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
		random.setSeed(seed);
		keygen.init(128, random); // 192 and 256 bits may not be available
		SecretKey key = keygen.generateKey();
		byte[] raw = key.getEncoded();
		return raw;
	}

	private static byte[] encrypt(byte[] raw, byte[] plain) throws Exception {
		SecretKeySpec keySpec = new SecretKeySpec(raw, "AES");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, keySpec);
		byte[] encrypted = cipher.doFinal(plain);
		return encrypted;
	}

	private static byte[] decrypt(byte[] raw, byte[] encrypted)
			throws Exception {
		SecretKeySpec keySpec = new SecretKeySpec(raw, "AES");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.DECRYPT_MODE, keySpec);
		byte[] decrypted = cipher.doFinal(encrypted);
		return decrypted;
	}
}