package com.fxforbiz.softphone.core.managers;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.io.FileUtils;
import com.fxforbiz.softphone.core.Application;
import com.fxforbiz.softphone.utils.Logger;

/**
 * This class is a singleton manager that handles updates for the application.
 */
public class UpdateManager {
	/**
	 * The instance of UpdateManager.
	 */
	private static UpdateManager instance = null;
	
	/**
	 * Constructor.
	 */
	private UpdateManager() {
		UpdateManager.instance = this;
	}
	
	/**
	 * Getter of the instance of the update manager. If the update manager is not initialized, it initialize it.
	 * @return An instance of the update manager.
	 */
	public static UpdateManager getInstance() {
		UpdateManager ret = UpdateManager.instance;
		if(UpdateManager.instance == null) {
			ret = new UpdateManager();
		}
		return ret;
	}

	/**
	 * This methods checks in the properties of the application if an update is needed.
	 * @return Either an update is needed or not.
	 */
	public boolean checkForUpdate() {
		String dateStr = Application.getInstance().getPropertyManger().getProperty("END_DATE_LICENCE");		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date end = null;
		try {
			end = sdf.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Date now = new Date(); 
				
		return !now.before(end);
	}

	/**
	 * This method handles the update process for the application.
	 * @return Either the update is successful or not.
	 */
	public boolean startUpdateProcess() {
		Application.getInstance().getLogger().log("Begining update...", Logger.INFO);
				
		String urlLicenceFile = Application.getInstance().getPropertyManger().getProperty("URL_UPDATE_LICENCE_FILE");		
		String urlPropertiesFile = Application.getInstance().getPropertyManger().getProperty("URL_UPDATE_PROPERTIES_FILE");
		String appVersion = Application.getInstance().getPropertyManger().getProperty("APP_VERSION");

		String pathLicenceFile = "Softphone_"+appVersion+"_lib/license.jar";
		String pathPropertiesFile = "config.properties";

		File licenceFileToRemove = new File(pathLicenceFile);
		File propertiesFileToRemove = new File(pathPropertiesFile);
		
		if(licenceFileToRemove.exists() == false) {
			Application.getInstance().getLogger().log("licence file missing, aborting... ("+licenceFileToRemove.getAbsolutePath()+")", Logger.CRITICAL);
			return false;
		}
			
		if(propertiesFileToRemove.exists() == false) {
			Application.getInstance().getLogger().log("properties file missing, aborting...", Logger.CRITICAL);
			return false;
		}
				
		try{
			licenceFileToRemove.delete();
			propertiesFileToRemove.delete();
		} catch(SecurityException se){ se.printStackTrace(); }
		
		Application.getInstance().getLogger().log("Updating licence...", Logger.INFO);
		this.downloadFile(urlLicenceFile, pathLicenceFile);
		Application.getInstance().getLogger().log("Updating config...", Logger.INFO);
		this.downloadFile(urlPropertiesFile, pathPropertiesFile);
		Application.getInstance().getLogger().log("Update successfull !", Logger.INFO);
		return true;
	}
	
	/**
	 * This method allow to download a file identified by the URL given in parameters, and record it in a file in the file path given in parameters.
	 * @param URL The URL of the file to download.
	 * @param pathOfFile The path to record the file downloaded.
	 * @return Either the file is downloaded successfully or not.
	 */
	private boolean downloadFile(String URL, String pathOfFile) {
			
		try {
			URL url = new URL(URL);
			File destination = new File(pathOfFile);
			FileUtils.copyURLToFile(url, destination);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		File verifFile = new File(pathOfFile);
		return verifFile.exists();
	}
}
