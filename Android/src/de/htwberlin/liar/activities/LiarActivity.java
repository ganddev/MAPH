package de.htwberlin.liar.activities;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

public abstract class LiarActivity extends Activity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		configure();
	}
	
	protected void configure(){
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}
	
	protected final int getInteger(int resid){
		return getResources().getInteger(resid);
	}
	
	protected final Drawable getDrawable(int resid) {
		return getResources().getDrawable(resid);
	}
}
