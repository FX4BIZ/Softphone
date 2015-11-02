package com.fxforbiz.softphone.core;

import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import com.fxforbiz.softphone.core.httprequests.LogoutRequest;
import com.fxforbiz.softphone.core.managers.KeyManager;
import com.fxforbiz.softphone.core.managers.PropertyManager;
import com.fxforbiz.softphone.core.managers.UpdateManager;
import com.fxforbiz.softphone.ui.BrowserPane;
import com.fxforbiz.softphone.ui.LoginPane;
import com.fxforbiz.softphone.ui.MainWindow;
import com.fxforbiz.softphone.utils.Logger;

/**
 * The main application class. <BR>
 * This class is built as a singleton to provide access of part of the application everywhere in the code.
 * It also handle the starting of the application.
 */
public class Application {
	/**
	 * The current instance of the application.
	 */
	private static Application instance;
	/**
	 * The instance of the property manager instance of the application.
	 */
	private PropertyManager propertyManger;
	/**
	 * The update manager instance of the application.
	 */
	private UpdateManager updateManager;
	/**
	 * The logger instance for the application.
	 */
	private Logger logger;
	/**
	 * The key manager instance of the application.
	 */
	private KeyManager keyManager;
	/**
	 * The main JFrame of the application.
	 */
	private MainWindow mainWindow;
	/**
	 * A login pane to be put in the mainWindow.
	 */
	private LoginPane loginPane;
	/**
	 * A browser pane to be put in the mainWindow.
	 */
	private BrowserPane browserPane;
	/**
	 * The string to auth someone on requests.
	 */
	private String AppToken;
	
	/**
	 * The application name
	 */
	private String AppName;
	/**
	 * A file used as a mutex to disable launching of multiple softphone.
	 */
    private File lockerFile;
    /**
     * A channel attached to the lockerFile.
     */
    private FileChannel channel;
    /**
     * A locker attached to the channel and the lockerFile.
     */
    private FileLock lock;
	
    /**
     * Constructor.<BR>
     * This function handle the starting of the application. It will first handle the LookAndFeel options,
     * then loads properties for applications, initialize the Logger, and checks for multilaunching of the application.
     * After that, the function will check for update, and if it's needed, will update the application and restart it.
     * Finaly it will initialize GUI parts and finalize the application starting.
     */
	private Application() {
		Application.instance = this;
		
    	try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
    	
		this.propertyManger = PropertyManager.getInstance();
    	
		this.logger = Logger.getInstance();
		if(this.propertyManger.getProperty("DEBUG").equals("true")) {
			Logger.setEnabled(true);
		}
		
		this.AppName = this.propertyManger.getProperty("APP_NAME")+"Locker";
		if(this.alreadyStarted()) {
			JOptionPane.showMessageDialog(null, "Softphone already started. You cannot start multiple softphones.", "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
		
		this.updateManager = UpdateManager.getInstance();
		if(this.updateManager.checkForUpdate()) {
			this.getLogger().log("An update is needed", Logger.WARNING);
						
			boolean updateResult = this.updateManager.startUpdateProcess();
			if(!updateResult) {
				JOptionPane.showMessageDialog(null, "An error occured while updating softphone, exiting.", "Error", JOptionPane.ERROR_MESSAGE);
				System.exit(0);
			}
			this.getLogger().log("Restarting...", Logger.WARNING);
			this.restartApplication();
		}
		
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
            	new LogoutRequest().execute();
                closeLock();
                deleteFile();
            }
        });
		
		this.keyManager = KeyManager.getInstance();
		if(!this.keyManager.checkKeyPresence()) {
			this.logger.log("No key detected, exiting !", Logger.INFO);
			JOptionPane.showMessageDialog(null, "You have no key installed for this softphone, please contact the IT Team to have a key for this softphone", "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
		
		this.mainWindow = new MainWindow();
		this.loginPane = new LoginPane();
		
		this.mainWindow.add(this.loginPane);
		this.mainWindow.enable();
		
		this.AppToken = "";
		
		this.logger.log("Application "+this.propertyManger.getProperty("APP_NAME")+" (version "+this.propertyManger.getProperty("APP_VERSION")+") initialized !", Logger.INFO);
	}
	
	/**
	 * This function will restart the application, starting a new instance of a java command.
	 */
	public void restartApplication()
	{
		final String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
		File currentJar = null;
		try {
			currentJar = new File(Application.class.getProtectionDomain().getCodeSource().getLocation().toURI());
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}

		if(!currentJar.getName().endsWith(".jar")) {
			return; 
		}
	
		final ArrayList<String> command = new ArrayList<String>();
		command.add(javaBin);
		command.add("-jar");
		command.add(currentJar.getPath());

		final ProcessBuilder builder = new ProcessBuilder(command);
		try {
			builder.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.exit(0);
	}
	
	/**
	 * This static function returns the application instance, if it not exists, it will create it.
	 * @return the instance of the application
	 */
	public static Application getInstance() {
		Application ret = Application.instance;
		if(Application.instance == null) {
			ret = new Application();
		}
		return ret;
	}
	
	/**
	 * This function will enable the launching of the application, reseting the mainWindow and put the softphone in it.
	 */
	public void launchSoftPhone() {
		this.browserPane = new BrowserPane();
		this.mainWindow.remove(this.loginPane);
		this.mainWindow.add(this.browserPane.getBrowserView());
		this.mainWindow.setSize(new Dimension(290, 485));
		
		this.mainWindow.addWindowListener(new WindowListener() {
			public void windowOpened(WindowEvent e) {
				browserPane.getBrowser().executeJavaScript("$('#numberInput').focus();");
			}
			public void windowIconified(WindowEvent e) {}
			public void windowDeiconified(WindowEvent e) {
				browserPane.getBrowser().executeJavaScript("$('#numberInput').focus();");
			}
			public void windowDeactivated(WindowEvent e) {}
			public void windowClosing(WindowEvent e) {}
			public void windowClosed(WindowEvent e) {}
			public void windowActivated(WindowEvent e) {
				browserPane.getBrowser().executeJavaScript("$('#numberInput').focus();");
			}
		});
		
		this.mainWindow.revalidate();
		this.mainWindow.toFront();
		this.mainWindow.repaint();
	}
	
	/**
	 * This function will restart the softphone.
	 */
	public void reloadSoftPhone() {
		Application.getInstance().getLogger().log("Reloading browser pane...", Logger.WARNING);

		this.mainWindow.remove(this.browserPane.getBrowserView());
		this.mainWindow.setCollapsed(false);
		
		this.browserPane = new BrowserPane();
		
		this.mainWindow.add(this.browserPane.getBrowserView());
		this.mainWindow.setSize(new Dimension(290, 485));
		this.mainWindow.revalidate();
		this.mainWindow.toFront();
		this.mainWindow.repaint();
		
		this.mainWindow.addWindowListener(new WindowListener() {
			public void windowOpened(WindowEvent e) {}
			public void windowIconified(WindowEvent e) {}
			public void windowDeiconified(WindowEvent e) {}
			public void windowDeactivated(WindowEvent e) {}
			public void windowClosing(WindowEvent e) {}
			public void windowClosed(WindowEvent e) {}
			public void windowActivated(WindowEvent e) {
				browserPane.getBrowser().executeJavaScript("$('#numberInput').focus();");
			}
		});
	}
	
	/**
	 * This function checks if the application is already started. If not, it will put a file as a mutex to disable another starting of the application.
	 * @return either the application is started or not.
	 */
	public boolean alreadyStarted() {
        try {
        	this.lockerFile = new File("."+this.AppName);
        	this.channel = new RandomAccessFile(this.lockerFile, "rw").getChannel();

			try {
                this.lock = channel.tryLock();
            }
            catch (OverlappingFileLockException e) {
                closeLock();
                return true;
            }

            if (this.lock == null) {
                closeLock();
                return true;
            }
            return false;
        }
        catch (Exception e) {
            closeLock();
            return true;
        }
	}
	
	/**
	 * This function will release the lock on the mutex file.
	 */
    private void closeLock() {
        try { lock.release();  }
        catch (Exception e) {  }
        try { channel.close(); }
        catch (Exception e) {  }
    }

    /**
     * this function handle the deleting of the mutex file.
     */
    private void deleteFile() {
        try { this.lockerFile.delete(); }
        catch (Exception e) { }
    }
	
	/**
	 * Getter for the main window of the application.
	 * @return the main window of the application
	 */
	public MainWindow getMainWindow() {
		return mainWindow;
	}

	/**
	 * Setter for the main window of the application.
	 * @param mainWindow A mainWindow to take place of the main window of the application.
	 */
	public void setMainWindow(MainWindow mainWindow) {
		this.mainWindow = mainWindow;
	}

	/**
	 * Getter for the browser pane of the application.
	 * @return The browser pane of the application.
	 */
	public BrowserPane getBrowserPane() {
		return browserPane;
	}

	/**
	 * Setter for the browser pane of the application.
	 * @param browserPane A new browser pane to replace the old one.
	 */
	public void setBrowserPane(BrowserPane browserPane) {
		this.browserPane = browserPane;
	}

	/**
	 * Getter for the login pane of the application.
	 * @return the login pane of the application.
	 */
	public LoginPane getLoginPane() {
		return loginPane;
	}

	/**
	 * Setter for the login pane of the application.
	 * @param loginPane A new login pane for replacing the old one.
	 */
	public void setLoginPane(LoginPane loginPane) {
		this.loginPane = loginPane;
	}

	/**
	 * Getter for the application token of the application.
	 * @return the applicaton token of the application.
	 */
	public String getAppToken() {
		return AppToken;
	}

	/**
	 * Setter for the application token of the application.
	 * @param appToken A new application token for the application.
	 */
	public void setAppToken(String appToken) {
		AppToken = appToken;
	}

	/**
	 * Getter of the logger for the application
	 * @return The logger for the application.
	 */
	public Logger getLogger() {
		return logger;
	}

	/**
	 * Setter for the logger for the application.
	 * @param logger A new logger for the application.
	 */
	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	/**
	 * Getter for the key manager instance of the application.
	 * @return the key manager instance of the application.
	 */
	public KeyManager getKeyManager() {
		return keyManager;
	}

	/**
	 * Setter for the key manager of the application.
	 * @param keyManager A new key manager for the application.
	 */
	public void setKeyManager(KeyManager keyManager) {
		this.keyManager = keyManager;
	}

	/**
	 * Getter for the properties manager of the application.
	 * @return the properties manager of the application.
	 */
	public PropertyManager getPropertyManger() {
		return propertyManger;
	}

	/**
	 * Setter for the properties manager of the application.
	 * @param propertyManger A new properties manager for the application.
	 */
	public void setPropertyManger(PropertyManager propertyManger) {
		this.propertyManger = propertyManger;
	}

	/**
	 * Getter of the update manager of the application.
	 * @return the update manager of the application.
	 */
	public UpdateManager getUpdateManager() {
		return updateManager;
	}

	/**
	 * Setter of the update manager of the application.
	 * @param updateManager A new update manager for the application.
	 */
	public void setUpdateManager(UpdateManager updateManager) {
		this.updateManager = updateManager;
	}
}
