package com.fxforbiz.softphone.core.managers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import javax.swing.JOptionPane;

import com.fxforbiz.softphone.core.Application;
import com.fxforbiz.softphone.utils.Logger;

/**
 * This class is a singleton manager for properties of the application.
 */
public class PropertyManager {
	/**
	 * The instance of PropertyManager.
	 */
	private static PropertyManager instance = null;
	/**
	 * Path to the configuration files that handles properties.
	 */
	private static String CONFIG_FILE_PATH = "config.properties";
	/**
	 * The object containing the properties.
	 */
	private Properties properties;
	
	/**
	 * Constructor.<BR>
	 * This constructor loads the properties contained in the configuration file.
	 */
	private PropertyManager() {
		this.loadProperties();
		PropertyManager.instance = this;
	}

	/**
	 * Getter of the instance of the properties manager. If the properties manager is not initialized, it initialize it.
	 * @return An instance of the properties manager.
	 */
	public static PropertyManager getInstance() {
		PropertyManager ret = PropertyManager.instance;
		if(PropertyManager.instance == null) {
			ret = new PropertyManager();
		}
		return ret;
	}
	
	/**
	 * This method reload properties into the property object. This should be used if the configuration file had changes.
	 */
	public void reloadProperties() {
		Application.getInstance().getLogger().log("Reloading configuration...", Logger.INFO);
		this.properties = null;
		this.loadProperties();
	}
	
	/**
	 * This method loads the properties contained into the configuration file into the property object.
	 */
	private void loadProperties() {
		File configFile = null;
		
		try{
			configFile = new File(PropertyManager.CONFIG_FILE_PATH);
		} catch(NullPointerException e) {}
		
		if(!configFile.exists()) {
			JOptionPane.showMessageDialog(null, "A config file is missing for this softphone.", "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
		
		try {
		    FileReader reader = new FileReader(configFile);
		    this.properties = new Properties();
		    this.properties.load(reader);
		    reader.close();
		} catch (FileNotFoundException ex) {
		    ex.printStackTrace();
		} catch (IOException ex) {
		    ex.printStackTrace();
		}
	}
	
	/**
	 * Getter for a property in the property object.
	 * @param property The property needed.
	 * @return A String containing the property needed, or null if the property given does not exists.
	 */
	public String getProperty(String property) {
		return this.properties.getProperty(property);
	}
}
