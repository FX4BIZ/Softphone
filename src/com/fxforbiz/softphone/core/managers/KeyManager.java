package com.fxforbiz.softphone.core.managers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Base64;

/**
 * This class is a singleton manager handling the key for encryption purposes.
 */
public class KeyManager {
	/**
	 * The instance of KeyManager
	 */
	private static KeyManager instance = null;
	/**
	 * Path to the key file.
	 */
	private static String KEY_FILE = ".key";
	/**
	 * The key to encrypt authentication token.
	 */
	private String key;
	
	/**
	 * Constructor.
	 */
	private KeyManager() {
		KeyManager.instance = this;
	}
	
	/**
	 * Getter of the instance of the key manager. If the key manager is not initialized, it initialize it.
	 * @return An instance of the key manager.
	 */
	public static KeyManager getInstance() {
		KeyManager ret = KeyManager.instance;
		if(KeyManager.instance == null) {
			ret = new KeyManager();
		}
		return ret;
	}
	
	/**
	 * This method checks if the key file is present.
	 * @return Either the key file is present.
	 */
	public boolean checkKeyPresence() {
		boolean success = false;
		File f = new File(KeyManager.KEY_FILE);
		if(f.exists()) {
			success = true;
		}
		return success;
	}
	
	/**
	 * This function generate a token for authenticate requests against server.
	 * @param login The login to use for authentication.
	 * @param password The password to use for authentication.
	 * @return Either the token creation is successful or not.
	 */
	public boolean generateKey(String login,String password) {
		boolean success = false;
		
		String salt = "";
		
		File f = new File(KeyManager.KEY_FILE);
		try {
			FileInputStream fis = new FileInputStream(f);
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			
			String line;
            while((line = br.readLine()) != null) {
            	salt = line;
            }
            
            br.close();
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(!salt.equals("")) {
			try {
				String digest = generateHash(login,password,salt);
				
				this.key = digest;
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if(!this.key.equals("")) {
				success=true;
			}
		}
		
		return success;
	}
	
	/**
	 * This method construct the token string for authentication
	 * @param login The login to encrypt.
	 * @param password The password to encrypt.
	 * @param salt The salt to use to encrypt password.
	 * @return The string corresponding to the authentication token.
	 */
	private String generateHash(String login, String password, String salt) {
		String salted = login+password+"{"+salt+"}";
		return Base64.getEncoder().encodeToString(salted.getBytes());
	}

	/**
	 * Getter for the authentication key.
	 * @return the authentication key.
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Setter for the authentication key.
	 * @param key The new key for authentication.
	 */
	public void setKey(String key) {
		this.key = key;
	}
	
	
}
