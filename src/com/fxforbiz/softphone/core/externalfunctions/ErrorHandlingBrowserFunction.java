package com.fxforbiz.softphone.core.externalfunctions;

import com.fxforbiz.softphone.core.Application;
import com.fxforbiz.softphone.utils.Logger;
import com.teamdev.jxbrowser.chromium.BrowserFunction;
import com.teamdev.jxbrowser.chromium.JSValue;

/**
 * This class is a handler for the "errorHandling" javascript function, executing java code for each time the javascript function is called.
 * @see com.teamdev.jxbrowser.chromium.BrowserFunction
 */
public class ErrorHandlingBrowserFunction implements BrowserFunction {

	/**
	 * Constructor. <BR>
	 * This constructor only log the loading of the function into the browser pane of the application.
	 */
	public ErrorHandlingBrowserFunction() {
		Application.getInstance().getLogger().log("ErrorHandlingBrowserFunction loaded !", Logger.INFO);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public JSValue invoke(JSValue... args) {
    	Application.getInstance().reloadSoftPhone();
		Application.getInstance().getLogger().log("A bug occured, reloading...", Logger.WARNING);
    	return JSValue.create(true);
	}

}
