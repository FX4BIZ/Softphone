package com.fxforbiz.softphone.ui;

import java.awt.Dimension;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
import com.fxforbiz.softphone.core.Application;

/**
 * This class represents the main window of the application.
 */
public class MainWindow extends JFrame{
	/**
	 * Either the window is collapsed or not.
	 */
	private boolean collapsed;
	/**
	 * Either the window is collapsed on width or not.
	 */
	private boolean collapsedWidth;
	
	/**
	 * Constructor.
	 */
    public MainWindow() {
    	super(
    			Application.getInstance().getPropertyManger().getProperty("APP_NAME")+
    			" ("+
    			Application.getInstance().getPropertyManger().getProperty("APP_VERSION")+
    			")"+
    			(Application.getInstance().getPropertyManger().getProperty("DEBUG").equals("true") ? " - DEBUG MODE - " : "")
    	);
    	
    	this.collapsed = false;
    	this.collapsedWidth = false;
    	
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setLocation(50, 50);
        this.setResizable(false);
    }
    
    /**
     * This method show up the main application window.
     */
    public void enable() {
    	this.pack();
    	this.setVisible(true);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override public void toFront() {
        int sta = super.getExtendedState() & ~JFrame.ICONIFIED & JFrame.NORMAL;

        super.setExtendedState(sta);
        super.setAlwaysOnTop(true);
        super.toFront();
        super.requestFocus();
        super.setAlwaysOnTop(false);
    }

    /**
     * Getter for the collapsed toggle.
     * @return the collapsed toggle.
     */
	public boolean isCollapsed() {
		return collapsed;
	}

	/**
	 * Setter for the collapsed toggle.
	 * @param collapsed The new value for the collapsed toggle.
	 */
	public void setCollapsed(boolean collapsed) {
		this.collapsed = collapsed;
	}

	/**
	 * This method toggle the window collapsing.
	 */
	public void toggleWindowCollapse() {
		if(this.collapsed) {
			this.setSize(new Dimension((int)this.getSize().getWidth(),400));
		} else {
			this.setSize(new Dimension((int)this.getSize().getWidth(),100));
		}
		this.collapsed = !this.collapsed;
	}

	/**
	 * This method toggle the window width collapsing.
	 */
	public void toggleWindowCollapseWidth() {
		if(this.collapsedWidth) {
			this.setSize(new Dimension(400,(int)this.getSize().getHeight()));
		} else {
			this.setSize(new Dimension(100,(int)this.getSize().getHeight()));
		}
		this.collapsedWidth = !this.collapsedWidth;
	}
}
