package de.htwberlin.liar.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import de.htwberlin.liar.R;

public class StartActivity extends LiarActivity
{
	
	private Button bEEG, bGalvanic;
	Intent eeg_intent, galvanic_intent;
	
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
        //bEEG.setText(R.string.start_eeg_button);
        //eeg_intent = new Intent(this, EegActivity.class);
        eeg_intent = new Intent();
        eeg_intent.setClassName(this, "EegActivity");
        bEEG.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(eeg_intent);
			}
		});
        
        /**
         * Initialiserung des Buttons zur Galvanic-Skin-Eigenanalyse
         */
        //galvanic_intent = new Intent(this, GalvanicActivity.class);
        galvanic_intent = new Intent();
        galvanic_intent.setClassName(this, "GalvanicActivity.class");
        bGalvanic = (Button) findViewById(R.id.start_layout_galvanic_button);
        bGalvanic.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(galvanic_intent);
			}
		});
        
    }
}
