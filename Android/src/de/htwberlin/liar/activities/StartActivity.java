package de.htwberlin.liar.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import de.htwberlin.liar.R;

public class StartActivity extends LiarActivity
{
	
	private Button bEEG, bLiarTest;//bGalvanic;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_layout);
        
        findViewById(R.id.start_layout_start_button).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(StartActivity.this, PlayerSelectionActivity.class);
				startActivity(intent);
			}
		});
        
        /**
         * Initialiserung des Buttons zur EEG-Eigenanalyse
         */
        bEEG = (Button) findViewById(R.id.start_layout_eeg_button);
        bEEG.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final Intent eegIntent = new Intent(StartActivity.this, EegActivity.class);
				startActivity(eegIntent);
			}
		});
        
        /**
         * Initialiserung des Buttons zur LiarTest-Eigenanalyse
         */
        bLiarTest = (Button) findViewById(R.id.start_layout_galvanic_button);
        bLiarTest.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final Intent LiarTestIntent  = new Intent(StartActivity.this, LiarTestActivity.class);
				startActivity(LiarTestIntent);
			}
		});
        
        findViewById(R.id.start_layout_rankingc_button).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(StartActivity.this, RankingActivity.class);
				startActivity(intent);
			}
		});
        
    }

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.eeg, menu);
		return true;
	}
    
    @Override
    public boolean onMenuItemSelected (int featureId, MenuItem item){
    	switch (item.getItemId()) {
		case R.id.action_settings:
			
			return true;

		case R.id.action_help:
			final Intent helpIntent = new Intent(this, GalvanicHelpActivity.class);
			startActivity(helpIntent);
			return true;
		default:
			return super.onMenuItemSelected(featureId, item);
		}
    	
    }
}
