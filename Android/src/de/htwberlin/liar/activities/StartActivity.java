package de.htwberlin.liar.activities;

import de.htwberlin.liar.R;
import de.htwberlin.liar.R.layout;
import android.app.Activity;
import android.os.Bundle;

public class StartActivity extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_layout);
    }
}
