package com.fxforbiz.softphone.core.httprequests;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
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
import com.fxforbiz.softphone.core.beans.LogoutRequestResult;
import com.fxforbiz.softphone.utils.Logger;
import com.google.gson.Gson;

/**
 * This class handles the logout request when the application closes.
 */
public class LogoutRequest {
	/**
	 * A bean containing the result of the request.
	 */
	private LogoutRequestResult logoutRequestResult;
	
	/**
	 * This function launches the logout request against the server.
	 * @return the Logout request object.
	 */
	public LogoutRequest execute() {
						
		String status = "0";
		String token = Application.getInstance().getKeyManager().getKey();

		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(Application.getInstance().getPropertyManger().getProperty("URL_LOGOUT"));
		List<NameValuePair> params = new ArrayList<NameValuePair>(2);
		params.add(new BasicNameValuePair("status", status));
		params.add(new BasicNameValuePair("token", token));
		
		try {
			httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();

			if (entity != null) {
				String result = EntityUtils.toString(entity);
				
				Application.getInstance().getLogger().log("Logout request response : "+result, Logger.INFO);
				
				Gson gson = new Gson();
			    LogoutRequestResult lrr = gson.fromJson(result, LogoutRequestResult.class);  
			    
			    this.logoutRequestResult = lrr;
			}
						
			httpclient.getConnectionManager().shutdown();;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return this;
	}
	
	/**
	 * Getter for the result of the request. This could be directly chained with the execute function.
	 * @return the result of the request
	 */
	public boolean getResult() {
		return this.logoutRequestResult.success;
	}
}
