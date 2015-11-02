package com.fxforbiz.softphone.core.externalfunctions;

import com.fxforbiz.softphone.core.Application;
import com.fxforbiz.softphone.core.managers.AudioManager;
import com.fxforbiz.softphone.utils.Logger;
import com.teamdev.jxbrowser.chromium.BrowserFunction;
import com.teamdev.jxbrowser.chromium.JSValue;

/**
 * This class is a handler for the "collapseWidth" javascript function, executing java code for each time the javascript function is called.
 * @see com.teamdev.jxbrowser.chromium.BrowserFunction
 */
public class CollapseWidthBrowserFunction implements BrowserFunction {

	/**
	 * Constructor. <BR>
	 * This constructor only log the loading of the function into the browser pane of the application.
	 */
	public CollapseWidthBrowserFunction() {
		Application.getInstance().getLogger().log("CollapseWidthBrowserFunction loaded !", Logger.INFO);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public JSValue invoke(JSValue... args) {
    	boolean result = false;
    	Application.getInstance().getMainWindow().toggleWindowCollapseWidth();
    	
    	return JSValue.create(result);
	}

}
