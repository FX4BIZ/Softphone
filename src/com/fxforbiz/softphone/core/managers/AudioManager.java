package com.fxforbiz.softphone.core.managers;

import java.io.IOException;
import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

/**
 * This class is a singleton manager for playing audio via the application. It uses JavaFX Audio Player to provide sound.
 */
public class AudioManager {
	/**
	 * The instance of AudioManager
	 */
	private static AudioManager instance = null;
	/**
	 * The media player that plays sounds.
	 */
	private MediaPlayer mediaPlayer;
	/**
	 * Path to the "ring" sound.
	 */
	public static String RING = "/com/fxforbiz/softphone/assets/sounds/ringOnIncoming.mp3" ;
	/**
	 * Path to the "connect" sound.
	 */
	public static String CONNECT = "/com/fxforbiz/softphone/assets/sounds/connect.mp3" ;
	/**
	 * Path to the "disconnect" sound.
	 */
	public static String DISCONNECT = "/com/fxforbiz/softphone/assets/sounds/disconnect.mp3" ;

	/**
	 * Constructor.<BR>
	 * This constructor initialize the JFX environment for the audio player.
	 */
	private AudioManager() {
        new JFXPanel();
		this.mediaPlayer = null;
		AudioManager.instance = this;
	}
	
	/**
	 * Getter of the instance of the audio manager. If the audio manager is not initialized, it initialize it.
	 * @return An instance of the audio manager.
	 */
	public static AudioManager getInstance() {
		AudioManager ret = AudioManager.instance;
		if(AudioManager.instance == null) {
			ret = new AudioManager();
		}
		return ret;
	}
	
	/**
	 * This method allow the application to play a sound given in parameters.
	 * @param toPlay The path of the sound to play.
	 * @param repeat Either the audio manager have to repeat the sound.
	 * @return Either the player started play successfully.
	 * @throws IOException if the audio file is not found.
	 */
	public boolean play(String toPlay, boolean repeat) throws IOException {
        if(this.mediaPlayer != null) {
        	this.stop();
        }
        
        String ressource = null;
        try {
            ressource = this.getClass().getResource(toPlay).toURI().toString();
        } catch (Exception e) {
        	e.printStackTrace();
        }
        
        if(ressource == null) {
        	throw new IOException("The file you're trying to load is unavailable : "+toPlay);
        }
        
        this.mediaPlayer = new MediaPlayer(new Media(ressource));
        
        if(repeat) {
            this.mediaPlayer.setOnEndOfMedia(new Runnable() {
                public void run() {
                	if(mediaPlayer != null) {
                		mediaPlayer.seek(Duration.ZERO);
                	}
                }
            });
        }
        
        this.mediaPlayer.play();

		return true;
	}
	
	/**
	 * this method stops the sound if a sound is played, and destroy the actual player to avoid sound overriding.
	 * @return Either player was stopped successfully.
	 */
	public boolean stop() {
        if(this.mediaPlayer != null) {
        	this.mediaPlayer.stop();
        	this.mediaPlayer.dispose();
        	this.mediaPlayer = null;
        }
        return true;
	}
}
