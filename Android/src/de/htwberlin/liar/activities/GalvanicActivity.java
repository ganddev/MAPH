package de.htwberlin.liar.activities;

import de.htwberlin.liar.R;
import android.os.Bundle;
import android.os.Build;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

public class GalvanicActivity extends LiarActivity {

	private TextView gs_werte;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.galvanic_layout);
        
        gs_werte = (TextView) findViewById(R.id.galvanic_skin_werte);
        gs_werte.setMovementMethod(ScrollingMovementMethod.getInstance());
        
        for(int i = 0; i <= 100; i++){
        	gs_werte.setText(i + "\n" + gs_werte.getText());
        	i++;
        	try {
				wait(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        
    }

    
}
