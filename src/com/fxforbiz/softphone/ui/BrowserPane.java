package com.fxforbiz.softphone.ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import com.fxforbiz.softphone.core.Application;
import com.fxforbiz.softphone.core.externalfunctions.BeginRingBrowserFunction;
import com.fxforbiz.softphone.core.externalfunctions.CollapseBrowserFunction;
import com.fxforbiz.softphone.core.externalfunctions.CollapseWidthBrowserFunction;
import com.fxforbiz.softphone.core.externalfunctions.ConnectBrowserFunction;
import com.fxforbiz.softphone.core.externalfunctions.DisconnectBrowserFunction;
import com.fxforbiz.softphone.core.externalfunctions.EndRingBrowserFunction;
import com.fxforbiz.softphone.core.externalfunctions.ErrorHandlingBrowserFunction;
import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.BrowserPreferences;
import com.teamdev.jxbrowser.chromium.LoadURLParams;
import com.teamdev.jxbrowser.chromium.LoggerProvider;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;

/**
 * This class represents the browser panel shown in the application.
 */
public class BrowserPane extends JPanel {
	/**
	 * An instance of the browser, used to process the code of the web page shown.
	 */
	private Browser browser;
	/**
	 * An instance of a browser view, handling the graphical part of the panel, and rendering web pages.
	 */
	private BrowserView browserView;
	
	/**
	 * A JFrame containing the remote debugging tool.
	 */
	private static JFrame debuggingFrame;
	/**
	 * A JFrame containing buttons that handles useful actions.
	 */
	private static JFrame toolboxFrame;
	
	/**
	 * Constructor.<BR>
	 * The constructor of the browser panel handles the creation of all objects needed to render a web page in the application.
	 * It will also add a debugging frame and a tool box frame if the debug mode is enabled.
	 */
	public BrowserPane() {
        LoggerProvider.setLevel(Level.OFF);
		
        boolean debugging = Application.getInstance().getPropertyManger().getProperty("DEBUG").equals("true");
        
        if(debugging) {
        	BrowserPreferences.setChromiumSwitches("--remote-debugging-port=9222");
        }
        
        this.browser = new Browser();
        this.browserView = new BrowserView(browser);
        
        this.browser.getCacheStorage().clearCache();
        this.browser.getCookieStorage().deleteAll();
        
        if(debugging) {
           this.launchDebuggingTools();
        }
        
        String token = Application.getInstance().getAppToken();
        
        this.browser.registerFunction("javacallbeginring", new BeginRingBrowserFunction());	
        this.browser.registerFunction("javacallendring", new EndRingBrowserFunction());	
        this.browser.registerFunction("javacallcollapse", new CollapseBrowserFunction());	
        this.browser.registerFunction("javacallconnect", new ConnectBrowserFunction());	
        this.browser.registerFunction("javacalldisconnect", new DisconnectBrowserFunction());	
        this.browser.registerFunction("javacallerrorhandling", new ErrorHandlingBrowserFunction());
        this.browser.registerFunction("javacallcollapsewidth", new CollapseWidthBrowserFunction());	

        this.browser.loadURL(       
        		new LoadURLParams(Application.getInstance().getPropertyManger().getProperty("URL_LOAD_SOFTPHONE"), "token="+token)
        );
        
	}
	
	/**
	 * This method build and displays the debugging frame and the tool box frame.
	 */
	public void launchDebuggingTools() {
    	String remoteDebuggingURL = this.browser.getRemoteDebuggingURL();
    	
    	try {    		
    		debuggingFrame.setVisible(false);
	    	debuggingFrame.dispose();
    		toolboxFrame.setVisible(false);
	    	toolboxFrame.dispose();
    	} catch (NullPointerException e) { }
    	
        Browser browser2 = new Browser();
        BrowserView browserView2 = new BrowserView(browser2);

        debuggingFrame = new JFrame("Chrome developper tool (embed)");
        debuggingFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        debuggingFrame.add(browserView2, BorderLayout.CENTER);
        debuggingFrame.setSize(1200, 500);
        debuggingFrame.setLocationRelativeTo(null);
        debuggingFrame.setVisible(true);

        browser2.loadURL(remoteDebuggingURL);
        
        toolboxFrame = new JFrame("Debugging toolbox (embed)");
        toolboxFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        toolboxFrame.setLayout(new GridLayout(4,1));
        JButton btn = new JButton("Refresh all");
        btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Application.getInstance().reloadSoftPhone();
			}
		});
        
        toolboxFrame.add(btn);
        
        JButton btn2 = new JButton("Get flash value");
        btn2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				browser.executeJavaScript("if(NoFlash == false) { console.log('this softphone uses flash'); } else { console.log('this softphone uses rtc'); }");
			}
		});
        
        toolboxFrame.add(btn2);
        
        toolboxFrame.setSize(300, 200);
        toolboxFrame.setLocationRelativeTo(null);
        toolboxFrame.setVisible(true); 
	}

	/**
	 * Getter for the browser of the panel.
	 * @return The browser of the panel.
	 */
	public Browser getBrowser() {
		return browser;
	}

	/**
	 * Setter for the browser of the panel.
	 * @param browser The new browser instance.
	 */
	public void setBrowser(Browser browser) {
		this.browser = browser;
	}

	/**
	 * Getter for the browser view of the panel.
	 * @return The browser view of the panel.
	 */
	public BrowserView getBrowserView() {
		return browserView;
	}

	/**
	 * Setter for the browser view of the panel.
	 * @param browserView The new browser view.
	 */
	public void setBrowserView(BrowserView browserView) {
		this.browserView = browserView;
	}
	
	
}
