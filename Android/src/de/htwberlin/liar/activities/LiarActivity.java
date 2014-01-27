package de.htwberlin.liar.activities;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

/**
 *	An abstract class with utility methods and a default screen orientation.
 */
public abstract class LiarActivity extends Activity{
	
	/**
	 * Calls {@link #configure()}
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		configure();
	}
	
	/**
	 * Sets the screen orientation to portrait mode. Override if necessary.
	 */
	protected void configure(){
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}
	
	/**
	 * Gets an integer value.
	 * 
	 * @param resid the id of the value
	 * @return the integer for the id
	 */
	protected final int getInteger(int resid){
		return getResources().getInteger(resid);
	}
	
	/**
	 * Gets a {@link Drawable}.
	 * 
	 * @param resid the id of teh {@link Drawable}
	 * @return the {@link Drawable} fpr teh given id
	 */
	protected final Drawable getDrawable(int resid) {
		return getResources().getDrawable(resid);
	}
}
