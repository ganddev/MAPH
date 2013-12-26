package de.htwberlin.liar.activities;

import de.htwberlin.liar.R;
import android.os.Bundle;
import android.bluetooth.BluetoothAdapter;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.neurosky.thinkgear.*;
//import from neurosky needs the following packages: TGData, TGDevice

public class EegActivity extends LiarActivity {
	
	BluetoothAdapter bluetoothAdapter;

	TextView eeg_att, eeg_blink, eeg_medit;
	Button b;
	
	TGDevice tgDevice;
	final boolean rawEnabled = false;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eeg_layout);
        eeg_att = (TextView) findViewById(R.id.eeg_sensor_text_view);
        eeg_att.setMovementMethod(ScrollingMovementMethod.getInstance());
        eeg_att.setText("");
        eeg_blink = (TextView) findViewById(R.id.eeg_blink);
        eeg_blink.setText("");
        eeg_medit = (TextView) findViewById(R.id.eeg_meditation);
        eeg_medit.setText("");
        
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        
        if(bluetoothAdapter == null) {
        	// Alert user that Bluetooth is not available
        	Toast.makeText(this, "Bluetooth not available", Toast.LENGTH_LONG).show();
        	finish();
        	return;
        }else {
        	/* create the TGDevice */
        	Toast.makeText(this, "Create new TGDevice...", Toast.LENGTH_LONG).show();
        	tgDevice = new TGDevice(bluetoothAdapter, handler);
        }  
        
        eeg_att.setText("");
        
        if(tgDevice != null){
        	tgDevice.connect(true);
        	tgDevice.start();
        	Toast.makeText(this, "Connected...", Toast.LENGTH_LONG).show();
        	//eeg_att.append("Connected: "+tgDevice.getState()+"\n");
            //eeg_att.append("Not Paired: "+tgDevice.STATE_NOT_PAIRED+"\n");
        }
        else{
        	Toast.makeText(this, "Not Connected - no device found", Toast.LENGTH_LONG).show();
        	eeg_att.append("No TGDevice found...");
        	//eeg_att.append("Connected: "+tgDevice.getState());
            //eeg_att.append("Not Paired: "+tgDevice.STATE_NOT_PAIRED);
        }
        
        
    }
    
    @Override
    public void onDestroy() {
    	tgDevice.close();
        super.onDestroy();
    }
    
    /**
     * Handles messages from TGDevice
     */
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	switch (msg.what) {
            
        	case TGDevice.MSG_STATE_CHANGE:
                switch (msg.arg1) {
	                case TGDevice.STATE_IDLE:
	                    break;
	                case TGDevice.STATE_CONNECTING:		                	
	                	eeg_att.append("Connecting...\n");
	                	break;		                    
	                case TGDevice.STATE_CONNECTED:
	                	eeg_att.setText("Connected.\n" + eeg_att.getText()); 
	                	tgDevice.start();
	                    break;
	                case TGDevice.STATE_NOT_FOUND:
	                	eeg_att.setText("Can't find\n" + eeg_att.getText());
	                	break;
	                case TGDevice.STATE_NOT_PAIRED:
	                	eeg_att.setText("not paired\n" + eeg_att.getText());
	                	break;
	                case TGDevice.STATE_DISCONNECTED:
	                	eeg_att.setText("Disconnected mang\n" + eeg_att.getText());
                }
                break;

        	/*case TGDevice.MSG_HEART_RATE:
        		eeg_att.append("Heart rate: " + msg.arg1 + "\n");
                break;*/
            case TGDevice.MSG_ATTENTION:
            	//att = msg.arg1;
            	eeg_att.setText("Attention: " + msg.arg1 + "\n" + eeg_att.getText());
            	break;
            case TGDevice.MSG_MEDITATION:
            	eeg_medit.setText("Meditation: " + msg.arg1 + "\n" + eeg_medit.getText());
            	break;
            case TGDevice.MSG_BLINK:
            		eeg_blink.setText("Blink: " + msg.arg1 + "\n" + eeg_blink.getText());
            	break;
            case TGDevice.MSG_LOW_BATTERY:
            	Toast.makeText(getApplicationContext(), "Low battery!", Toast.LENGTH_SHORT).show();
            	break;
            default:
            	break;
        }
        }
    };
    
    public void doStuff(View view) {
    	if(tgDevice.getState() != TGDevice.STATE_CONNECTING && tgDevice.getState() != TGDevice.STATE_CONNECTED)
    		tgDevice.connect(rawEnabled);   
    	//tgDevice.ena
    }
}