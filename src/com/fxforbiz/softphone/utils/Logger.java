package com.fxforbiz.softphone.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This singleton class handles the logging for the application.
 */
public class Logger {
	/**
	 * The information level of logging.
	 */
	public static String INFO = "INFO";
	/**
	 * The warning level of logging.
	 */
	public static String WARNING = "WARNING";
	/**
	 * The critical level of logging.
	 */
	public static String CRITICAL = "CRITICAL";
	/**
	 * Either the logging is enabled or not for the application.
	 */
	public static boolean enabled = false;
	/**
	 * The current instance of the Logger.
	 */
	private static Logger instance;
	
	/**
	 * Constructor.<BR>
	 */
	private Logger() {
		Logger.instance = this;
	}
	
	/**
	 * Getter for the Logger instance, if it's not initialized, it will initialize it.
	 * @return the current Logger instance.
	 */
	public static Logger getInstance() {
		Logger ret = Logger.instance;
		if(Logger.instance == null) {
			ret = new Logger();
		}
		return ret;
	}
	
	/**
	 * This method handles the logging for the application in the standard output.
	 * @param toLog The string to print on the standard output.
	 * @param level The level of importance for the logging.
	 */
	public void log(String toLog, String level) {
		if(Logger.isEnabled()) {
			String toPrint = "";
			
			SimpleDateFormat dateF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			toPrint+= "["+dateF.format(new Date())+"]";
			if(level != null) {
				toPrint += "["+level+"]"+" ";
			}
			toPrint += toLog;
			System.out.println(toPrint);
		}
	}

	/**
	 * Getter for the enabling toggle.
	 * @return the enabling toggle.
	 */
	public static boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Setter for the enabling toggle.
	 * @param enabled Either the logger is enabled or not.
	 */
	public static void setEnabled(boolean enabled) {
		Logger.enabled = enabled;
	}
	
}
