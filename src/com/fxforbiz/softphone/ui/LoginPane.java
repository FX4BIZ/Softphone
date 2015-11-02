package com.fxforbiz.softphone.ui;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import com.fxforbiz.softphone.core.managers.LoginManager;
import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JCheckBox;
import javax.swing.JButton;

public class LoginPane extends JPanel {
	/**
	 * The login test field of the panel.
	 */
	private JTextField loginField;
	/**
	 * The password text field of the panel.
	 */
	private JPasswordField passwordField;
	/**
	 * The checkbox handling the recording of the login informations for next logins.
	 */
	private JCheckBox autoLoginCheckBox;
	/**
	 * The button that handles the submition of the login form.
	 */
	private JButton loginButton;
	/**
	 * The label describing the status of the login process.
	 */
	private JLabel statusLabel;

	/** 
	 * Constructor.<BR>
	 * This constructor call the parent constructor, then initialize graphical components of the panel
	 */
	public LoginPane() {
		super();
		this.initDisplay();
	}
	
	/**
	 * This method initialize graphical components of the panel.
	 */
	public void initDisplay() {
		String [] loginInfos = LoginManager.getInstance().getStoredLogin();
		
		this.setFocusable(true);
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{10, 112, 266, 10, 0};
		gridBagLayout.rowHeights = new int[]{10, 0, 0, 0, 0, 0, 0, 0, 10, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		JLabel lblLogin = new JLabel("Login : ");
		GridBagConstraints gbc_lblLogin = new GridBagConstraints();
		gbc_lblLogin.anchor = GridBagConstraints.WEST;
		gbc_lblLogin.insets = new Insets(0, 0, 5, 5);
		gbc_lblLogin.gridx = 1;
		gbc_lblLogin.gridy = 1;
		add(lblLogin, gbc_lblLogin);
		
		this.loginField = new JTextField();
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.insets = new Insets(0, 0, 5, 5);
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.gridx = 2;
		gbc_textField.gridy = 1;
		
		if(loginInfos.length != 0) {
			this.loginField.setText(loginInfos[0]);
		}
		
		add(loginField, gbc_textField);
		loginField.setColumns(10);
		
		JLabel lblPassword = new JLabel("Password :");
		GridBagConstraints gbc_lblPassword = new GridBagConstraints();
		gbc_lblPassword.anchor = GridBagConstraints.WEST;
		gbc_lblPassword.insets = new Insets(0, 0, 5, 5);
		gbc_lblPassword.gridx = 1;
		gbc_lblPassword.gridy = 2;
		add(lblPassword, gbc_lblPassword);
		
		this.passwordField = new JPasswordField();
		GridBagConstraints gbc_passwordField = new GridBagConstraints();
		gbc_passwordField.insets = new Insets(0, 0, 5, 5);
		gbc_passwordField.fill = GridBagConstraints.HORIZONTAL;
		gbc_passwordField.gridx = 2;
		gbc_passwordField.gridy = 2;
		
		if(loginInfos.length != 0) {
			this.passwordField.setText(loginInfos[1]);
		}
		
		add(passwordField, gbc_passwordField);
		
		this.autoLoginCheckBox = new JCheckBox("Auto-login on launch\r\n");
		GridBagConstraints gbc_chckbxNewCheckBox = new GridBagConstraints();
		gbc_chckbxNewCheckBox.anchor = GridBagConstraints.WEST;
		gbc_chckbxNewCheckBox.gridwidth = 2;
		gbc_chckbxNewCheckBox.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxNewCheckBox.gridx = 1;
		gbc_chckbxNewCheckBox.gridy = 4;
		
		if(loginInfos.length != 0) {
			this.autoLoginCheckBox.setSelected(true);
		}
		add(autoLoginCheckBox, gbc_chckbxNewCheckBox);
		
		this.loginButton = new JButton("Connect");
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.gridwidth = 2;
		gbc_btnNewButton.insets = new Insets(0, 0, 5, 5);
		gbc_btnNewButton.gridx = 1;
		gbc_btnNewButton.gridy = 6;
		add(loginButton, gbc_btnNewButton);
		
		this.statusLabel = new JLabel("");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 0);
		gbc_lblNewLabel.gridwidth = 4;
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 7;
		add(statusLabel, gbc_lblNewLabel);
		
		this.loginButton.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				String login = loginField.getText();
				char[] passwordArr = passwordField.getPassword();
				String passwordL = "";
				for(char c : passwordArr) {
					passwordL+=c;
				}
				final String password = passwordL;
				boolean save = autoLoginCheckBox.isSelected();
				
				getStatusLabel().setForeground(Color.BLACK);
				getStatusLabel().setText("Logging in...");
				getLoginField().setEnabled(false);
				getPasswordField().setEnabled(false);
				getAutoLoginCheckBox().setEnabled(false);
				getLoginButton().setEnabled(false);
				
				Runnable r = new Runnable() {
					@Override
					public void run() {
						boolean res = LoginManager.getInstance().startLoginProcess(login, password, save);
						if(!res) {
							getStatusLabel().setText("Login failed : invalid login or password");
							getStatusLabel().setForeground(Color.RED);
							getLoginField().setEnabled(true);
							getPasswordField().setEnabled(true);
							getAutoLoginCheckBox().setEnabled(true);
							getLoginButton().setEnabled(true);
						}
					}
				};
				new Thread(r).start();			
			}
		});
		
		this.addKeyListener(new KeyListener() {
			@Override public void keyTyped(KeyEvent e) {}
			@Override public void keyReleased(KeyEvent e) {}
			@Override public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					loginButton.doClick();
				}
			}
		});
		
		this.loginField.addKeyListener(new KeyListener() {
			@Override public void keyTyped(KeyEvent e) {}
			@Override public void keyReleased(KeyEvent e) {}
			@Override public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					loginButton.doClick();
				}
			}
		});
		
		this.passwordField.addKeyListener(new KeyListener() {
			@Override public void keyTyped(KeyEvent e) {}
			@Override public void keyReleased(KeyEvent e) {}
			@Override public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					loginButton.doClick();
				}
			}
		});
	}

	/**
	 * Getter for the login text field of the panel.
	 * @return the login text field of the panel.
	 */
	public JTextField getLoginField() {
		return loginField;
	}

	/**
	 * Setter for the login text field of the panel.
	 * @param loginField A new JTextField to replace the old one.
	 */
	public void setLoginField(JTextField loginField) {
		this.loginField = loginField;
	}

	/**
	 * Getter for the password text field of the panel.
	 * @return the password text field of the panel.
	 */
	public JPasswordField getPasswordField() {
		return passwordField;
	}

	/**
	 * Setter for the password text field of the panel.
	 * @param passwordField A new JPasswordField to replace the old one.
	 */
	public void setPasswordField(JPasswordField passwordField) {
		this.passwordField = passwordField;
	}

	/**
	 * Getter for the auto-login checkbox of the panel.
	 * @return the auto-login checkbox of the panel.
	 */
	public JCheckBox getAutoLoginCheckBox() {
		return autoLoginCheckBox;
	}

	/**
	 * Setter for the auto-login checkbox of the panel.
	 * @param autoLoginCheckBox A new JCheckBox to replace the old one.
	 */
	public void setAutoLoginCheckBox(JCheckBox autoLoginCheckBox) {
		this.autoLoginCheckBox = autoLoginCheckBox;
	}

	/**
	 * Getter for the submit button of the panel.
	 * @return the submit button of the panel.
	 */
	public JButton getLoginButton() {
		return loginButton;
	}

	/**
	 * Setter for the submit button of the panel.
	 * @param loginButton A new JButton to replace the old one.
	 */
	public void setLoginButton(JButton loginButton) {
		this.loginButton = loginButton;
	}

	/**
	 * Getter for the status label of the panel.
	 * @return The status label of the panel.
	 */
	public JLabel getStatusLabel() {
		return statusLabel;
	}

	/**
	 * Setter for the status label of the panel.
	 * @param statusLabel A new JLabel to replace the old one.
	 */
	public void setStatusLabel(JLabel statusLabel) {
		this.statusLabel = statusLabel;
	}
}
