package com.fxforbiz.softphone.core.beans;

/**
 * This class is a bean containing the result of a login request.
 */
public class LoginRequestResult {
	/**
	 * Either the login request is successful or note.
	 */
	public boolean success;
	/**
	 * The application key returned by the login request if the request was successful.
	 */
	public String appKey;
}
