package de.htwberlin.liar.activities;

import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;
import de.htwberlin.liar.R;

public class GalvanicActivity extends LiarActivity {

	private TextView gs_werte;
	/**Handels Runnable Threads*/
	private Handler handler;
	/**last value of the galvanic skin sensor*/
	private int lastUpdate;
	
	//Thread to update view
	private Runnable runnable = new Runnable() {
		
		@Override
		public void run() {
			gs_werte.setText(lastUpdate + "\n" + gs_werte.getText());
			if (lastUpdate < 30) {
				lastUpdate++;
				//call Runnable again after 1000 ms
				handler.postDelayed(runnable, 1000); 
			}	
		}
	};
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.galvanic_layout);
        
        //initialize values 
        handler = new Handler();
        lastUpdate = 0;
        //initialize views
        gs_werte = (TextView) findViewById(R.id.galvanic_skin_werte);
        gs_werte.setMovementMethod(ScrollingMovementMethod.getInstance());
             
        //start thread
        handler.postDelayed(runnable, 0);      
    }

    
}
