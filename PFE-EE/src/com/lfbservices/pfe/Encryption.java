package com.lfbservices.pfe;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * This class handles the encryption of datas.
 */ 
public class Encryption {
	
	/**
	 * This method encrypts the given parameter using a sha1 Algorithm and a salt.
	 * @param input The {@link String} to encrypt.
	 * @return A {@link String} representing the given input encrypted using an encryption Algorithm.
	 * @throws NoSuchAlgorithmException If an error occurred when sending the JSON request.
	 */ 
	public static String sha1(String input) throws NoSuchAlgorithmException {
		if (input == null) return null;
		// The salt string.
		input.concat("r?!*$*/|`*/@|h");
        MessageDigest mDigest = MessageDigest.getInstance("SHA1");
        byte[] result = mDigest.digest(input.getBytes());
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < result.length; i++) {
            sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }
}
