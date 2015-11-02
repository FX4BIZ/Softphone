package com.fxforbiz.softphone.core.managers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.fxforbiz.softphone.core.Application;
import com.fxforbiz.softphone.core.beans.LoginRequestResult;
import com.fxforbiz.softphone.utils.Logger;
import com.google.gson.Gson;

/**
 * This class is a singleton manager for the login process via the application.
 */
public class LoginManager {
	/**
	 * The instance of LoginManager
	 */
	private static LoginManager instance = null;
	/**
	 * Path to the storage file for the login record.
	 */
	private static String SAVE_FILE = ".save";
	
	/**
	 * Constructor.
	 */
	private LoginManager() {
		LoginManager.instance = this;
	}
	
	/**
	 * Getter of the instance of the login manager. If the login manager is not initialized, it initialize it.
	 * @return An instance of the login manager.
	 */
	public static LoginManager getInstance() {
		LoginManager ret = LoginManager.instance;
		if(LoginManager.instance == null) {
			ret = new LoginManager();
		}
		return ret;
	}
	
	/**
	 * This method retrieves the login informations stored near the softphone, if the file does not exists, this method will return an empty String array.
	 * @return An array containing the login informations.
	 */
	public String [] getStoredLogin() {
		String [] r = new String[0];
		
		File f = new File(LoginManager.SAVE_FILE);
		if(f.exists()) {
			
			String [] infos = new String[2];
			infos[0] = "";
			infos[1] = "";

			try {
				FileInputStream fis = new FileInputStream(f);
				BufferedReader br = new BufferedReader(new InputStreamReader(fis));
				
				String line;
				int it = 0;
	            while((line = br.readLine()) != null) {
	            	infos[it] = line;
	            	it++;
	            }
	            
	            br.close();
			} catch(FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if(!infos[0].equals("") && !infos[1].equals("")) {
				r = new String[2];
				r[0] = new String(Base64.getDecoder().decode(infos[0].getBytes()));
				r[1] = new String(Base64.getDecoder().decode(infos[1].getBytes()));
			}
		}
		
		return r;
	}
	
	/**
	 * This method start the login process, create the authentication token, then do a login request against server and checks the result.
	 * @param login The login typed by the user.
	 * @param password The password typed by the user.
	 * @param save Either the process have to save the login informations into a save file.
	 * @return Either the login is failed or successful.
	 */
	public boolean startLoginProcess(String login, String password, boolean save) {
		boolean success = true;
		
		Application.getInstance().getLoginPane().getStatusLabel().setText("Connecting, please wait...");
		Application.getInstance().getLoginPane().getLoginField().setEnabled(false);
		Application.getInstance().getLoginPane().getPasswordField().setEnabled(false);
		Application.getInstance().getLoginPane().getAutoLoginCheckBox().setEnabled(false);
		Application.getInstance().getLoginPane().getLoginButton().setEnabled(false);
			
		boolean loginSuccess = false;

        long startTime = System.currentTimeMillis();
        loginSuccess = tryLogin(login, password);
        long endTime = System.currentTimeMillis();
        long duration = (endTime-startTime);
		Application.getInstance().getLogger().log("Request Time = "+duration+" ms...", Logger.INFO);
				
		if(loginSuccess) {
			Application.getInstance().getLogger().log("tokenKey = "+Application.getInstance().getKeyManager().getKey(), Logger.INFO);
			Application.getInstance().setAppToken(Application.getInstance().getKeyManager().getKey());
			Application.getInstance().getLoginPane().getStatusLabel().setText("Loading FXSoftPhone...");

			if(save) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						saveLoginInformations(login, password);
					}
				}).start();
			} else {
				new Thread(new Runnable() {
					@Override
					public void run() {
						removeLoginInformationSaved();
					}
				}).start();
			}
			Application.getInstance().launchSoftPhone();
		} else {
			Application.getInstance().getLogger().log("login failed !", Logger.WARNING);
			success = false;
		}
		
		return success;
	}
	
	/**
	 * This method execute the request against server to authenticate the user.
	 * @param login The login to use for authentication.
	 * @param password The password to use for authentication.
	 * @return Either the request is failed or successful.
	 */
	private boolean tryLogin(String login, String password) {
		boolean success = false;
		
		Application.getInstance().getKeyManager().generateKey(login,password);
		
		String encodedLogin = Base64.getEncoder().encodeToString(login.getBytes());
		String token = Base64.getEncoder().encodeToString(Application.getInstance().getKeyManager().getKey().getBytes());

		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(Application.getInstance().getPropertyManger().getProperty("URL_LOGIN"));
		List<NameValuePair> params = new ArrayList<NameValuePair>(2);
		params.add(new BasicNameValuePair("username", encodedLogin));
		params.add(new BasicNameValuePair("token", token));
		try {
			httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();

			if (entity != null) {
				String result = EntityUtils.toString(entity);
				
				Application.getInstance().getLogger().log("Login request response : "+result, Logger.INFO);
				
				Gson gson = new Gson();
			    LoginRequestResult lrr = gson.fromJson(result, LoginRequestResult.class);  
			    
				success = lrr.success;
			}
			
			httpclient.getConnectionManager().shutdown();;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return success;
	}

	/**
	 * This method save login informations into a save file.
	 * @param login The login to be recorded.
	 * @param password The password to be recorded.
	 * @return Either the login informations are successfully recorded or not.
	 */
	private boolean saveLoginInformations(String login, String password) {
		boolean success = true;
		
		String encodedLogin = Base64.getEncoder().encodeToString(login.getBytes());
		String encodedPassword = Base64.getEncoder().encodeToString(password.getBytes());
		
		File fout = new File(LoginManager.SAVE_FILE);
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(fout);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
			 
			bw.write(encodedLogin);
			bw.newLine();
			bw.write(encodedPassword);
		 
			bw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			success = false;
		} catch (IOException e) {
			e.printStackTrace();
			success = false;
		}
		
		Application.getInstance().getLogger().log("Save file created...", Logger.INFO);
		Application.getInstance().getLogger().log("---- Login saved : "+login+" - "+password+" as "+encodedLogin+" - "+encodedPassword, Logger.INFO);

		return success;
	}
	
	/**
	 * This method remove the save file if it exists.
	 * @return Either the save file is deleted successfully or not.
	 */
	private boolean removeLoginInformationSaved() {
		boolean success = true;
		
		File f = new File(LoginManager.SAVE_FILE);
		if(f.exists()) {
			success = f.delete();
		}
		
		Application.getInstance().getLogger().log("Save file deleted...", Logger.INFO);
		
		return success;
	}
}
