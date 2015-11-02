package com.fxforbiz.softphone.core.externalfunctions;

import java.io.IOException;

import com.fxforbiz.softphone.core.Application;
import com.fxforbiz.softphone.core.managers.AudioManager;
import com.fxforbiz.softphone.utils.Logger;
import com.teamdev.jxbrowser.chromium.BrowserFunction;
import com.teamdev.jxbrowser.chromium.JSValue;

/**
 * This class is a handler for the "beginRing" javascript function, executing java code for each time the javascript function is called.
 * @see com.teamdev.jxbrowser.chromium.BrowserFunction
 */
public class BeginRingBrowserFunction implements BrowserFunction {

	/**
	 * Constructor. <BR>
	 * This constructor only log the loading of the function into the browser pane of the application.
	 */
	public BeginRingBrowserFunction() {
		Application.getInstance().getLogger().log("BeginRingBrowserFunction loaded !", Logger.INFO);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public JSValue invoke(JSValue... args) {
    	boolean result = false;

    	try {
			result = AudioManager.getInstance().play(AudioManager.RING, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
		Application.getInstance().getMainWindow().toFront();
    	
    	return JSValue.create(result);
	}

}
