package de.htwberlin.liar.activities;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import de.htwberlin.liar.R;
import de.htwberlin.liar.fragments.HelpDialog;

/**
 * Handles the start Screen.
 */
public class StartActivity extends LiarActivity
{
	/**Button to start a game*/
	private Button bStartNewGame;
	/**Button to start a analyzes*/
	private Button bAnalysis;
	/**Button to show teh ranking*/
	private Button bRanking;
	/**Button to show instructions*/
	private Button bInstructions;
//	private Button bGalvanic;
	
    /** 
     * Sets up the Buttons action.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_layout);
        
        bStartNewGame = (Button)findViewById(R.id.start_layout_start_button);
        bStartNewGame.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(StartActivity.this, PlayerSelectionActivity.class);
				startActivity(intent);
			}
		});
        
        /**
         * Analysis button
         */
        bAnalysis = (Button) findViewById(R.id.start_layout_eeg_button);
        bAnalysis.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final Intent eegIntent = new Intent(StartActivity.this, LiarTestActivity.class);
				startActivity(eegIntent);
			}
		});
        
        /**
         * Ranking button
         */
        bRanking = (Button) findViewById(R.id.start_layout_ranking_button);
        bRanking.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final Intent LiarTestIntent  = new Intent(StartActivity.this, RankingActivity.class);
				startActivity(LiarTestIntent);
			}
		});
        
        /**
         * Instructions button
         */
        bInstructions = (Button) findViewById(R.id.start_layout_instructions_button);
        bInstructions.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//TODO Add a Dialog fragment heer
			}
		});
        
        
//        bGalvanic = (Button)findViewById(R.id.button_galvanic_activity);
//        bGalvanic.setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				Intent galvanicIntent = new Intent(StartActivity.this, GalvanicActivity.class);
//				startActivity(galvanicIntent);
//			}
//		});
        
    }

    /**
     * Creates the option menu.
     */
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.eeg, menu);
		return true;
	}
    
    /**
     * Called if an item of the menu was clicked.
     */
    @Override
    public boolean onMenuItemSelected (int featureId, MenuItem item){
    	switch (item.getItemId()) {
		case R.id.action_settings:
			
			return true;

		case R.id.action_help:
			FragmentManager fm = getFragmentManager();
			HelpDialog hFM = new HelpDialog();
			hFM.show(fm, "helpDialog");
			return true;
		default:
			return super.onMenuItemSelected(featureId, item);
		}
    	
    }
}
