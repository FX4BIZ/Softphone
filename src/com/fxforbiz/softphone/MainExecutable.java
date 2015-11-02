package com.fxforbiz.softphone;

import javax.swing.SwingUtilities;

import com.fxforbiz.softphone.core.Application;

/**
 * This class is the main endpoint of the softphone application.
 */
public class MainExecutable {
	/**
	 * The main application entry point.
	 * @param args command line arguments for running the app. 
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override public void run() {
				Application.getInstance();
			}
		});
	}
}
