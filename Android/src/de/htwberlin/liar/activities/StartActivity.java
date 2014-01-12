package de.htwberlin.liar.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import de.htwberlin.liar.R;

public class StartActivity extends LiarActivity
{
	
	private Button bEEG, bGalvanic;
	
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
         * Initialiserung des Buttons zur Galvanic-Skin-Eigenanalyse
         */
        bGalvanic = (Button) findViewById(R.id.start_layout_galvanic_button);
        bGalvanic.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final Intent galvanicIntent  = new Intent(StartActivity.this, GalvanicActivity.class);
				startActivity(galvanicIntent);
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
}
