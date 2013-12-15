package de.htwberlin.liar.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import de.htwberlin.liar.R;

public class StartActivity extends LiarActivity
{
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
    }
}
