package de.htwberlin.liar.activities;

import de.htwberlin.liar.R;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;

import java.io.IOException;
import java.io.InputStream;
//import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.UUID;

import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.neurosky.thinkgear.*;
//import from neurosky needs the following packages: TGData, TGDevice


public class LiarTestActivity extends LiarActivity {
	
	private static final String TAG = "bluetooth2";
	
	private static final int BLUETOOTH_INTENT_CODE = 2;
	
	// Text view to display value
	private TextView gs_std_resis, eeg_std_att, eeg_std_medit, eeg_blink_counts;
	
	//the blink counter from eeg
	int blinkCounter;
	
	//A string to output the galvanic value
	String sbprint = "";
	
	//array for compute the standard derivation
	private static int ARRAYLENGTH = 10;
	int[] std_att, std_med, std_resis;
	
	//result for specified standard derivation
	double std_res_att, std_res_med, std_res_resis;
	
	//Status for handler
	final int RECIEVE_MESSAGE = 1;
	
	//Bluetooth adapter
	private BluetoothAdapter btAdapter = null;
	
	//Bluetooth socket
	private BluetoothSocket btSocket = null;
	
	//Use a stringbuilder for performance.
	private StringBuilder sb = new StringBuilder();

	//Thread which handles the connections.
	private ConnectedThread mConnectedThread;

	/**
	 *  SPP UUID servic. Hint: If you are connecting to a Bluetooth serial board 
	 *  then try using the well-known SPP UUID 00001101-0000-1000-8000-00805F9B34FB. 
	 *  However if you are connecting to an Android peer then please generate your own unique UUID.
	 */
	private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

	// Change this adress to your device mac - the mac from your arduino bt shield
	// linvor address 00:12:07:17:18:24
	private static String ADDRESS = "00:12:07:17:18:24";
	
	//TGDevice is used for pairing to myndplay eeg
	TGDevice tgDevice;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.liar_test_layout);
		
		//setup the UI with all TextViews
		setupUI();
		
		/* 
		 * neue Variablen fuer die Activity:
		 * hierbei handelt es sich um die Arrays zur Berechnung der Standardabweichungen fuer 
		 * Attention und Meditation (beide EEG) und des Widerstands vom Galvanic
		 * 
		 * ACHTUNG: wird bis dato noch nicht verwendet, da die Standardabweichung (Mittelwert, Varianz, STD) 
		 * in eine eigene Klasse umziehen soll - dann muss das Konstrukt auf Herz und Nieren getestet werden
		 */
		std_att = new int[ARRAYLENGTH];
		std_med = new int[ARRAYLENGTH];
		std_resis = new int[ARRAYLENGTH];
		
		//ein Zaehler fuer die Augenblinzler - wird auch verwendet
		blinkCounter = 0;
		
		/*
		 * Die Resultate aus der Berechnung der Standardabweichung fuer:
		 * std_res_att = Attention (EEG)
		 * std_res_med = Meditation (EEG)
		 * std_res_resis = Widerstand (Galvanic)
		 */
		std_res_att = 0.0;
		std_res_med = 0.0;
		std_res_resis = 0.0;
		
		//get the bluetooth adapter from the host device.
		btAdapter = BluetoothAdapter.getDefaultAdapter();
		
		//setup bluetooth connection
		setupEegBluetooth();
		
		//Check the bluetooth state.
		checkBTState();
		
	}
	
	/**
	 * Aufbau der UI der Activity! Kann man sicherlich eleganter l�sen, fuer meine Belange hats gereicht!
	 * Die TextViews im Einzelnen: 
	 * gs_std_resis = Ausgabe der Standardabweichung aller Widerstaende vom Galvanic
	 * eeg_std_att = Ausgabe der Standardabweichung aller Attentionwerte des EEG
	 * eeg_std_medit = Ausgabe der Standardabweichung aller Meditationwerte des EEG
	 * eeg_blink_counts = Ausgabe aller Blinzler (von Augenblizeln ^^ ) 
	 */
	private void setupUI(){
		gs_std_resis = (TextView) findViewById(R.id.galvanic_std_resistance);
		gs_std_resis.setText("Hallo Galvanic Auswertung");
		gs_std_resis.setMovementMethod(ScrollingMovementMethod.getInstance());
		
		eeg_std_att = (TextView) findViewById(R.id.eeg_std_attention);
		eeg_std_att.setText("Hallo EEG Std-Attention");
		eeg_std_att.setMovementMethod(ScrollingMovementMethod.getInstance());
		
		eeg_std_medit = (TextView) findViewById(R.id.eeg_std_meditation);
		eeg_std_medit.setText("Hallo EEG Meditation");
		eeg_std_medit.setMovementMethod(ScrollingMovementMethod.getInstance());
		
		eeg_blink_counts = (TextView) findViewById(R.id.blink_counts);
		eeg_blink_counts.setText("Ihre BLinks");
		eeg_blink_counts.setMovementMethod(ScrollingMovementMethod.getInstance());
	}
	
	/**
	 * Methode baut eine Bluetooth-Verbindung zwischen dem EEG und dem Smartphone auf. Dazu wird auf
	 * den globalen Bluetooth-Adapter zugegriffen und deren Inahlt ueberprueft. Zur Connection wird 
	 * ausserdem das TGdevice aus lib/ThinkGear.jar verwendet. Die Bibliothek ist ein Blackbox - daher 
	 * keine Ahnung was dadrin passiert! Good Luck ^^  
	 */
	private void setupEegBluetooth(){
		
		if(btAdapter == null) {
        	// Alert user that Bluetooth is not available
        	Toast.makeText(this, "Bluetooth not available", Toast.LENGTH_LONG).show();
        	finish();
        	return;
        }else {
        	/* create the TGDevice */
        	Toast.makeText(this, "Create new TGDevice...", Toast.LENGTH_LONG).show();
        	tgDevice = new TGDevice(btAdapter, eegHandler);
        }  
        
        eeg_std_att.setText("");
        
        if(tgDevice != null){
        	tgDevice.connect(true);
        	tgDevice.start();
        	//Toast.makeText(this, "Connected...", Toast.LENGTH_LONG).show();
        }
        else{
        	//Toast.makeText(this, "Not Connected - no device found", Toast.LENGTH_LONG).show();
        	eeg_std_att.append("No TGDevice found...");
        }
	}
	
	@Override
    public void onDestroy() {
    	tgDevice.close();
        super.onDestroy();
    }
	
	/**
     * Handles messages from arduino and for updating the ui by incomming bytes and computing 
     * if the testing person is a liar or not!
     */
    private final Handler gsHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			//int counter = 0;
			switch (msg.what) {
			case RECIEVE_MESSAGE: // if receive massage
				byte[] readBuf = (byte[]) msg.obj;
				String strIncom = new String(readBuf, 0, msg.arg1); //create string from bytes
				sb.append(strIncom); // append string
				int startOfValueIndex = sb.indexOf(";");
				int endOfLineIndex = sb.indexOf("\r\n"); // determine the end-of-line
				if (endOfLineIndex > 0) { // if end-of-line,
					sbprint = sb.substring(startOfValueIndex+1, endOfLineIndex); // extract string
					sb.delete(0, sb.length()); // and clear
					/*
					 * hier war eine Ueberpruefung durch die Standardabweichung erwuenscht 
					 * 
					 * nur solange wie der Counter != 10 ist, wird das Array mit den aktuellen Daten 
					 * versorgt, sonst 
					 */
					 /*if(counter >=0 && counter <= 9){
    					std_resis[counter] = Integer.valueOf(sbprint);
    					counter += 1;
    		  	  	  } else{
    		    		//wird der Counter wieder auf 0 gesetzt und das Array zur STD geschickt 
    		    		counter = 0;
    		    		std_res_resis = standardDeviation(std_resis);
    		  		  }
            	    */
					//hatte hier auch versucht, die Abhandlung der Array-Werte-Zuweisung auszulagern
					//std_resis = werteSichern(counter, std_resis, Integer.valueOf(sbprint));
					//counter += 1;
					gs_std_resis.setText("");
					gs_std_resis.setText("Data from Arduino: " + sbprint); // update TextView
					sbprint = "";
				}
				Log.d(TAG, "...String:" + sb.toString() + "Byte:"
						+ msg.arg1 + "...");
				break;
			}
		};
	};
	
	/**
	 * a simple way to store value of type integer in an array - maybe never used!!! :(
	 * @author Patte
	 * @param zaehler the counter of the given array
	 * @param array the array to store the sepcified value
	 * @param wert the specified integer value 
	 * @return the array with the new stored value
	 */
	/*
	private int[] werteSichern(int zaehler, int[] array, int wert){
		
		if(zaehler >=0 && zaehler <= 9){
			array[zaehler] = wert;
  	  	  } else{
    		zaehler = 0;
    		standardAbweichung(std_resis);
  		  }
		
		return array;
	}*/
	
	/**
     * Handles messages from TGDevice
     * Es wird ein neuer Handler "eegHandler" erzeugt, der die reinkommenden Daten interpretiert und 
     * entsprechend weitere Befehle ausfuehrt oder (zu fast 100% in unserem Fall) die Daten ausgibt bzw. 
     * in ein Array schreibt 
     */
    private final Handler eegHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	
        	//lokale Zaehler fuer die Arrayindizes von Attention (a) und Meditation (m)
        	int a = 0, m=0;
        	
        	switch (msg.what) {
            
        	case TGDevice.MSG_STATE_CHANGE:
                switch (msg.arg1) {
	                case TGDevice.STATE_IDLE:
	                    break;
	                case TGDevice.STATE_CONNECTING:		                	
	                	eeg_std_att.append("Connecting...\n");
	                	break;		                    
	                case TGDevice.STATE_CONNECTED:
	                	eeg_std_att.setText("Connected.\n" + eeg_std_att.getText()); 
	                	tgDevice.start();
	                    break;
	                case TGDevice.STATE_NOT_FOUND:
	                	eeg_std_att.setText("Can't find\n" + eeg_std_att.getText());
	                	break;
	                case TGDevice.STATE_NOT_PAIRED:
	                	eeg_std_att.setText("not paired\n" + eeg_std_att.getText());
	                	break;
	                case TGDevice.STATE_DISCONNECTED:
	                	eeg_std_att.setText("Disconnected mang\n" + eeg_std_att.getText());
                }
                break;

        	case TGDevice.MSG_ATTENTION:
        		
        		//Ausfuehrung der Sicherung der Attentionwerte im std_att-Array und ggf. STD-Berechnung
            	
        		/*if(a >=0 && a <= 9){
        			std_att[a] = msg.arg1;
        			a += 1;
        		  } else{
        		    a = 0;
        		    std_res_att = standardDeviation(std_att);
        		  }
        		*/
        		eeg_std_att.setText("Attention: " + msg.arg1 + "\n" + eeg_std_att.getText());
        		break;
            case TGDevice.MSG_MEDITATION:
            	
            	//Ausfuehrung der Sicherung der Attentionwerte im std_med-Array und ggf. STD-Berechnung
            	
            	/*if(m >=0 && m <= 9){
    				std_med[m] = msg.arg1;
    				a += 1;
    		  	  } else{
    		    	m = 0;
    		    	std_res_med = standardDeviation(std_med);
    		  		}
            	 */
            	eeg_std_medit.setText("Meditation: " + msg.arg1 + "\n" + eeg_std_medit.getText());
            	break;
            case TGDevice.MSG_BLINK:
            		
            		// hier wird der Blinzel-Counter erhoeht, toll, was !? ^^
            		
            		blinkCounter += 1;
            		eeg_blink_counts.setText("Anzahl: " + blinkCounter);
            	break;
            case TGDevice.MSG_LOW_BATTERY:
            	Toast.makeText(getApplicationContext(), "Low battery!", Toast.LENGTH_SHORT).show();
            	break;
            default:
            	break;
        }
        }
    };
	
    /**
	 * Create a bluetoothsocket from a given device
	 * 
	 * @param device
	 * @return Returns a bluetooth socket..
	 * @throws IOException
	 */
	private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
		/**
		 * Devices is running on honeycomb (API level 10) or higher?
		 * We can use createInsecureRfcommSocketToServiceRecord
		 */
		if (Build.VERSION.SDK_INT >= 10) {
			try {
				final Method m = device.getClass().getMethod(
						"createInsecureRfcommSocketToServiceRecord",
						new Class[] { UUID.class });
				return (BluetoothSocket) m.invoke(device, MY_UUID);
			} catch (Exception e) {
				Log.e(TAG, "Could not create Insecure RFComm Connection", e);
			}
		}
		/**
		 * Device is running with an android lower honeycomb
		 */
		return device.createRfcommSocketToServiceRecord(MY_UUID);
	}
	
	@Override
	public void onResume() {
		super.onResume();

		Log.d(TAG, "...onResume - try connect...");

		//connect to the linvor address
		BluetoothDevice device = btAdapter.getRemoteDevice(ADDRESS);

		/**
		 * We need to things. the MACADRESS of the device. And the UUID for our service.
		 */

		try {
			btSocket = createBluetoothSocket(device);
		} catch (final IOException e) {
			Log.e(TAG, e.getMessage());
			exitWithErrorMessage("Fatal Error", "In onResume() and socket create failed: "
					+ e.getMessage() + ".");
		}

		//Cancel discovery because it need to much ressources
		btAdapter.cancelDiscovery();

		// Establish the connection. This will block until it connects.
		Log.d(TAG, "...Connecting...");
		try {
			btSocket.connect();
			Log.d(TAG, "....Connection ok...");
		} catch (final IOException e) {
			Log.e(TAG, e.getLocalizedMessage());
			try {
				btSocket.close();
			} catch (final IOException e2) {
				Log.e(TAG, e2.getMessage());
				exitWithErrorMessage("Fatal Error",
						"In onResume() and unable to close socket during connection failure"
								+ e2.getMessage() + ".");
			}
		}

		// Create a data stream so we can talk to server.
		Log.d(TAG, "...Create Socket...");
		if (btSocket != null) {
			mConnectedThread = new ConnectedThread(btSocket);
			mConnectedThread.start();
		}
	}
	
	/**
	 * Try to close the btSocket. We don't need it anymore
	 */
	@Override
	public void onPause() {
		super.onPause();

		Log.d(TAG, "...In onPause()...");

		try {
			btSocket.close();
		} catch (final IOException e2) {
			Log.e(TAG, e2.getMessage());
			exitWithErrorMessage("Fatal Error", "In onPause() and failed to close socket."
					+ e2.getMessage() + ".");
		}
	}
	
	/**
	 * Check for Bluetooth support and then check to make sure it is turned on
	 */
	private void checkBTState() {
		if (btAdapter == null) {
			Log.e(TAG, "Bluetooth adapter is null");
			exitWithErrorMessage("Fatal Error", "Bluetooth not support");
		} else {
			if (btAdapter.isEnabled()) {
				Log.d(TAG, "...Bluetooth ON...");
			} else {
				// If bluetooth is not enabled start the activity
				final Intent enableBtIntent = new Intent(
						BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent, BLUETOOTH_INTENT_CODE);
			}
		}
	}
	
	/**
	 * Make a toast with a title and a message, e.g. for ERROR-Messages
	 * @param title The title for the toast.
	 * @param message The message for the toast.
	 */
	private void exitWithErrorMessage(String title, String message) {
		Toast.makeText(getBaseContext(), title + " - " + message,
				Toast.LENGTH_LONG).show();
		finish();
	}

	/**
	 * thread handling
	 * 
	 */
	private class ConnectedThread extends Thread {
		private final InputStream mmInStream;
		//private final OutputStream mmOutStream;

		public ConnectedThread(BluetoothSocket socket) {
			InputStream tmpIn = null;
			//OutputStream tmpOut = null;

			// Get the input and output streams, using temp objects because
			// member streams are final
			try {
				if (socket != null) {
					
					tmpIn = socket.getInputStream();
			//		tmpOut = socket.getOutputStream();
				}
			} catch (IOException e) {
				Log.e(TAG, e.getMessage());
			}

			mmInStream = tmpIn;
			//mmOutStream = tmpOut;
			
		}

		public void run() {
			byte[] buffer = new byte[256]; // buffer store for the stream
			int bytes; // bytes returned from read()

			//While true, for listening on incomming bytes.
			while (true) {
				try {
					// Read from the InputStream
					if (mmInStream != null) {
						bytes = mmInStream.read(buffer); // Get number of bytes and message in "buffer"
						Log.d(TAG, "Received " + bytes + " bytes");
						//Send message to handler, which will handle the ui update.
						gsHandler.obtainMessage(RECIEVE_MESSAGE, bytes, -1, buffer)
								.sendToTarget(); 
					} else {
						Log.w(TAG, "Stream is null");
					}
				} catch (final IOException e) {
					break;
				}
			}
		}
		
	}	
	
	/**
	 * Berechnet den Mittelwert eines Int-Arrays und gibt diesen wieder zurueck
	 * @param input Das Eingabearray mit Integer-Werten
	 * @return der Mittelwert vom Typ Double aus allen Felder des Eingabearrays
	 */
	private double mittelwertBerechnen(int[] input){
		int sum = 0;
        for(int i=0; i<input.length; i++){
            sum += input[i];
        }
        return (sum / input.length);
	}
	
	/**
	 * Berechnet die Varianz eines Integerarrays und gibt das Ergebnis als double Wert zurueck
	 * @param input Ein Integer-Array
	 * @return die Varianz vom Typ Double des Eingabe-Int-Array
	 */
	private double varianzBerechnen(int[] input, double average){
		
		double varianz = 0.0;
        
        for (int i=0; i<input.length;i++){
           varianz += Math.pow((input[i] - average), 2) / (input.length - 1);
        }
        
        return varianz;
        
	}
	
	/**
	 * Standardabweichung aus Mittelwert und Varianz berechnen
	 * 
	 * @author Phill und Patte 
	 * @param input Ein Integerarray
	 * @return die Standardabweichung vom Typ Double des Eingabe-Int-Array
	 */
	public double standardAbweichung(int[] input){
        
		// Mittelwert --------------------------------------------------------------
		
        double mittelwert = mittelwertBerechnen(input);
        
        // Varianz -----------------------------------------------------------------
        double varianz = varianzBerechnen(input, mittelwert);
        
        // STD DEV -----------------------------------------------------------------
        double std = Math.sqrt(varianz/(input.length-1)); 
        
        return std;
    }
	
	// unused
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.liar_test, menu);
		return true;
	}

}
