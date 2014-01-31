package de.htwberlin.liar.activities;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.androidplot.Plot;
import com.androidplot.util.Redrawer;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
//import from neurosky needs the following packages: TGData, TGDevice
import com.neurosky.thinkgear.TGDevice;

import de.htwberlin.liar.R;

import de.htwberlin.liar.utils.ConnectedThread;
import de.htwberlin.liar.utils.MatheBerechnungen;


public class LiarTestActivity extends LiarActivity  {
	
	// used for androidplot 
	private XYPlot eegAttentionPlot;
	private SimpleXYSeries eegAttentionSeries;
	private List<Integer> eegAttentionList;
	private XYPlot eegMeditationPlot;
	private SimpleXYSeries eegMeditationSeries;
	private List<Integer> eegMeditationList;
	private XYPlot galvanicPlot;
	private SimpleXYSeries galvanicSeries;
	private List<Integer> galvanicList;
	private int displayedPoints;
	private Redrawer redrawer;

	private static final String TAG = "bluetooth2";
	
	private static final int BLUETOOTH_INTENT_CODE = 2;
	
	// Text view to display value
//	private TextView gs_std_resis, eeg_std_att, eeg_std_medit, eeg_blink_counts, calibrate_result;
//	
//	private Button button_calibrate;
	
//	private static final String YOUR_ATTENTION = "Ihre Aufmerksamkeit";
//	private static final String YOUR_MEDITATION = "Ihre Meditation";
//	private static final String YOUR_BLINKS = "Ihre Blinzler";
//	private static final String YOUR_GALVANIC = "Ihre Hautleitfähigkeit";
	
	//the blink counter from eeg
	int blinkCounter;
	
	//A string to output the galvanic value
	private String sbprint = "";
	
	//array for compute the standard derivation
	private static final int ARRAYLENGTH = 10;
	int[] std_att, std_med, std_resis;
	
	//Status zum Datensameln
	private boolean enabled_attention, enabled_meditation, enabled_blinks, enabled_galvanic;
	
	private int attentionArrayCounter, meditationArrayCounter, galvanicArrayCounter;
	
	//result for specified standard derivation
	double std_res_att, std_res_med, std_res_resis;
	
	//Status for handler
	public final int RECIEVE_MESSAGE = 1;
	
	//Bluetooth adapter
	private BluetoothAdapter galvanicAdapter;
	private BluetoothAdapter eegAdapter;
	
	//Bluetooth socket
	private BluetoothSocket galvanicBtSocket = null;
	
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
	
	//BluetoothDevice galvanic skin
	BluetoothDevice galvanicBtDevice;
		
	//TGDevice is used for pairing to myndplay eeg
	TGDevice tgDevice;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.liar_test_layout);
		
		//setup the UI with all TextViews
		//setupUI();
		
		//------------------------------------------------
		// used for androidplot
		displayedPoints = 5;
		LineAndPointFormatter lineformatter1 = new LineAndPointFormatter(Color.BLUE, Color.YELLOW, Color.CYAN, new PointLabelFormatter());// evtl. muss der PointLabelFormatter noch mit was anderem initialisiert werden
		LineAndPointFormatter lineformatter2 = new LineAndPointFormatter(Color.RED, Color.BLACK, Color.YELLOW, new PointLabelFormatter());// evtl. muss der PointLabelFormatter noch mit was anderem initialisiert werden
		eegAttentionPlot = (XYPlot) findViewById(R.id.eegAttentionPlot);
		eegAttentionPlot.getGraphWidget().getDomainLabelPaint().setColor(Color.TRANSPARENT);
		eegAttentionPlot.setDomainStepValue(1);
		eegAttentionPlot.setTicksPerRangeLabel(1);
		eegAttentionPlot.setDomainBoundaries(0, displayedPoints, BoundaryMode.FIXED);	//x-Achse
		eegAttentionPlot.setRangeBoundaries(0, 100, BoundaryMode.FIXED);//y-Achse	
		eegAttentionSeries = new SimpleXYSeries("Attention");
		eegAttentionPlot.addSeries(eegAttentionSeries, lineformatter1);
		eegAttentionList = new ArrayList<Integer>();
						
		eegMeditationPlot = (XYPlot) findViewById(R.id.eegMeditationPlot);
		eegMeditationPlot.getGraphWidget().getDomainLabelPaint().setColor(Color.TRANSPARENT);
		eegMeditationPlot.setDomainStepValue(1);
		eegMeditationPlot.setTicksPerRangeLabel(1);
		eegMeditationPlot.setDomainBoundaries(0, displayedPoints, BoundaryMode.FIXED);
		eegMeditationPlot.setRangeBoundaries(0, 100, BoundaryMode.FIXED);
		eegMeditationSeries = new SimpleXYSeries("Meditation");
		eegMeditationPlot.addSeries(eegMeditationSeries, lineformatter1);
		eegMeditationList = new ArrayList<Integer>();
		
		galvanicPlot = (XYPlot) findViewById(R.id.galvanicPlot);
		galvanicPlot.getGraphWidget().getDomainLabelPaint().setColor(Color.TRANSPARENT);
		galvanicPlot.setDomainStepValue(1);
		galvanicPlot.setTicksPerRangeLabel(1);
		galvanicPlot.setDomainBoundaries(0, displayedPoints, BoundaryMode.FIXED);
		galvanicPlot.setRangeBoundaries(10000, 30000, BoundaryMode.FIXED);
		galvanicSeries = new SimpleXYSeries("Galvanic Skin");
		galvanicPlot.addSeries(galvanicSeries, lineformatter2); 
		galvanicList = new ArrayList<Integer>();
		
		redrawer = new Redrawer(Arrays.asList(new Plot[]{eegAttentionPlot, eegMeditationPlot, galvanicPlot}),  100,  false);
		
		//------------------------------------------------
		
		/* 
		 * neue Variablen fuer die Activity:
		 * hierbei handelt es sich um die Arrays zur Berechnung der Standardabweichungen fuer 
		 * Attention, Meditation und Blinzler (alle EEG) und des Widerstands vom Galvanic
		 * 
		 */
		std_att = new int[ARRAYLENGTH];
		arrayLeeren(std_att, ARRAYLENGTH);
		std_med = new int[ARRAYLENGTH];
		arrayLeeren(std_med, ARRAYLENGTH);
		std_resis = new int[ARRAYLENGTH];
		arrayLeeren(std_resis, ARRAYLENGTH);
		
		enabled_attention = false;
		enabled_meditation = false;
		enabled_blinks = false;
		enabled_galvanic = false;
		
		//Zaehler für die Durchlaeufe durch die Wertearray
		attentionArrayCounter = 0; 
		meditationArrayCounter = 0;
		galvanicArrayCounter = 0;
		//ein Zaehler fuer die Augenblinzler - wird auch verwendet
		blinkCounter = 0;
		
		/*
		 * Die Resultate aus der Berechnung der Standardabweichung fuer:
		 * std_res_att = Attention (EEG)
		 * std_res_med = Meditation (EEG)
		 * std_res_blinks = Blinks (EEG)
		 * std_res_resis = Widerstand (Galvanic)
		 */
		std_res_att = 0.0;
		std_res_med = 0.0;
		std_res_resis = 0.0;
		
		galvanicAdapter = BluetoothAdapter.getDefaultAdapter();
		eegAdapter = BluetoothAdapter.getDefaultAdapter();
	
		if(eegAdapter != null){		
			Log.d(TAG, "... the EEG Address is correct...");
			//Check the bluetooth state.
			checkBTState();
		}
		
		
//		button_calibrate.setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				
//				//zuerst darf der Button nicht nochmal geclickt werden
//				button_calibrate.setEnabled(false);
//				
////				setCalibratingTextView(String.valueOf(std_res_att),String.valueOf(std_res_med), 
////						String.valueOf(blinkCounter), String.valueOf(std_res_resis));
//				
//				//Daten fuer Attention sammeln
//				enabled_attention = true;
//								
//				//Daten fuer Meditation sammeln
//				enabled_meditation = true;
//				
//				//addieren aller Blinks
//				enabled_blinks = true;
//				
//				//lokales speichern der Blinkanzahl
//				
//				//Daten des Galvanic sammeln
//				enabled_galvanic = true;
//				
//				//Attentionarray auswerten
//				
//				//Meditationarrays auswerten
//				
//				//Galvanicarray auswerten
//				
//				while(!enabled_attention && enabled_meditation && enabled_blinks ){//&& !enabled_galvanic){
//					setCalibratingTextView(String.valueOf(std_res_att),String.valueOf(std_res_med), 
//							String.valueOf(blinkCounter), String.valueOf(std_res_resis));
//				}
//								
//				button_calibrate.setEnabled(true);
//				
//			}
//		});
					
	}
	
	/**
	 * simpliest way to empty an array
	 * @param array the array 
	 * @param length the length ot the array
	 */
	public void arrayLeeren(int array[], int length){
		
		for(int i = 0; i < length; i++){
			array[i] = 0;
		}
	}
	
	/**
	 * Aufbau der UI der Activity! Kann man sicherlich eleganter l�sen, fuer meine Belange hats gereicht!
	 * Die TextViews im Einzelnen: 
	 * gs_std_resis = Ausgabe der Standardabweichung aller Widerstaende vom Galvanic
	 * eeg_std_att = Ausgabe der Standardabweichung aller Attentionwerte des EEG
	 * eeg_std_medit = Ausgabe der Standardabweichung aller Meditationwerte des EEG
	 * eeg_blink_counts = Ausgabe aller Blinzler (von Augenblizeln ^^ ) 
	 */
//	private void setupUI(){
//		gs_std_resis = (TextView) findViewById(R.id.galvanic_std_resistance);
//		gs_std_resis.setText("Hallo Galvanic Auswertung");
//		gs_std_resis.setMovementMethod(ScrollingMovementMethod.getInstance());
//		
//		eeg_std_att = (TextView) findViewById(R.id.eeg_std_attention);
//		eeg_std_att.setText("Hallo EEG Std-Attention");
//		eeg_std_att.setMovementMethod(ScrollingMovementMethod.getInstance());
//		
//		eeg_std_medit = (TextView) findViewById(R.id.eeg_std_meditation);
//		eeg_std_medit.setText("Hallo EEG Meditation");
//		eeg_std_medit.setMovementMethod(ScrollingMovementMethod.getInstance());
//		
//		eeg_blink_counts = (TextView) findViewById(R.id.blink_counts);
//		eeg_blink_counts.setText(YOUR_BLINKS);
//		eeg_blink_counts.setMovementMethod(ScrollingMovementMethod.getInstance());
//		
//		button_calibrate = (Button)findViewById(R.id.b_calibrate);
//		button_calibrate.setText(R.string.do_calibrate);
		
//		calibrate_result = (TextView) findViewById(R.id.tv_calibrated_result);
//		calibrate_result.setMovementMethod(ScrollingMovementMethod.getInstance());
//		setCalibratingTextView("---","---","---","---");
//	}
	
	
//	private void setCalibratingTextView(String att, String med, String blinks, String galv){
//		calibrate_result.setText(YOUR_ATTENTION+": "+att+"\n");
//		calibrate_result.append(YOUR_MEDITATION+": "+med+"\n");
//		calibrate_result.append(YOUR_BLINKS+": "+blinks+"\n");
//		calibrate_result.append(YOUR_GALVANIC+": "+galv);
//	}
	
	/**
	 * Methode baut eine Bluetooth-Verbindung zwischen dem EEG und dem Smartphone auf. Dazu wird auf
	 * den globalen Bluetooth-Adapter zugegriffen und deren Inahlt ueberprueft. Zur Connection wird 
	 * ausserdem das TGdevice aus lib/ThinkGear.jar verwendet. Die Bibliothek ist ein Blackbox - daher 
	 * keine Ahnung was dadrin passiert! Good Luck ^^  
	 */
	private void setupEegBluetooth(){
		
		eegAdapter.startDiscovery();
//			
			Toast.makeText(this, "Create new TGDevice...", Toast.LENGTH_LONG).show();
			tgDevice = new TGDevice(eegAdapter, eegHandler);
			Log.d(TAG, "...TGDevice initialized:...");//+tgDevice.getConnectedDevice().toString());
			tgDevice.connect(true);
			tgDevice.start();
	     	
		eegAdapter.cancelDiscovery();
	
	}
	
	/**
	 * try to build a galvanic connection
	 */
	private void setupGalvanicBluetooth(){
		
		galvanicBtDevice = galvanicAdapter.getRemoteDevice(ADDRESS);
		
		/**
		 * We need two things. the MACADRESS of the device. And the UUID for our service.
		 */

		try {
			galvanicBtSocket = createBluetoothSocket(galvanicBtDevice);
		} catch (final IOException e) {
			Log.e(TAG, e.getMessage());
			exitWithErrorMessage("Fatal Error", "In onResume() and socket create failed: "
					+ e.getMessage() + ".");
		}

		//Cancel discovery because it need to much ressources
		galvanicAdapter.cancelDiscovery();

		connectSocketToBluetoothDevice();

		// Create a data stream so we can talk to server.
		Log.d(TAG, "...Create Socket...");
		if (galvanicBtSocket != null) {
			mConnectedThread =  new ConnectedThread(galvanicBtSocket, gsHandler, RECIEVE_MESSAGE);
			mConnectedThread.start();
		}
	}

	/**
	 * 
	 */
	private void connectSocketToBluetoothDevice() {
		// Establish the connection. This will block until it connects.
		Log.d(TAG, "...Connecting...");
		try {
			galvanicBtSocket.connect();
//			debuggingText.setText("Galvanic Connected\n"+debuggingText.getText());
			Log.d(TAG, "....Connection ok...");
		} catch (final IOException e) {
			Log.e(TAG, e.getLocalizedMessage());
			try {
				galvanicBtSocket.close();
			} catch (final IOException e2) {
				Log.e(TAG, e2.getMessage());
				exitWithErrorMessage("Fatal Error",
						"In onResume() and unable to close socket during connection failure"
								+ e2.getMessage() + ".");
			}
		}
	}
	
	/**
	 * on destroy disconnect all connection to any devices, e.g. bluetooth devices
	 */
	@Override
    public void onDestroy() {
    	try {
    		tgDevice.close();
        	galvanicBtSocket.close();        	
		} catch (IOException e) {
			
			e.printStackTrace();
		}
        super.onDestroy();
    }
	
	/**
	 * Check for Bluetooth support and then check to make sure it is turned on
	 */
	private void checkBTState() {
		if (eegAdapter == null) {
			Log.e(TAG, "Bluetooth adapter is null");
			exitWithErrorMessage("Fatal Error", "Bluetooth not support");
		} else {
			if (eegAdapter.isEnabled()) {
				Log.d(TAG, "...Bluetooth ON...");
				//start the device connections
				Log.d(TAG, "...Start Galvanic Bluetooth...");
				//setup galvanic connection
				setupGalvanicBluetooth();
				Log.d(TAG, "...Start EEG Bluetooth...");
				//setup bluetooth connection
				setupEegBluetooth();
			} else {
				Log.d(TAG, "... Start Bluetooth ...");
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
				sb.append(strIncom); // append string to stringbuilder sb
				int startOfValueIndex = sb.indexOf(";");
				int endOfValueIndex_1 = sb.indexOf("\r\n"); // determine the end-of-line
				int endOfValueIndex_2 = sb.indexOf(" ");				
				if (endOfValueIndex_1 > startOfValueIndex) { // if end-of-line,
					sbprint = sb.substring(startOfValueIndex+1, endOfValueIndex_1);// extract string
					sb.delete(0, sb.length()); // and clear
				} else if(endOfValueIndex_2 > startOfValueIndex) {
						sbprint = sb.substring(startOfValueIndex+1, endOfValueIndex_2);
						sb.delete(0, sb.length()); // and clear
				} else {
					break;
				}	
								
				try{
					int galvValue = Integer.parseInt(sbprint)/10;
					Log.d("Galvanic", "int: "+galvValue);
				
					if(galvValue > 0){
						galvanicList.add(galvValue);
						if (galvanicList.size() > displayedPoints) {
							galvanicList.remove(0);
						}
						galvanicSeries.setModel(galvanicList, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
						redrawer.start();
					}
				} catch (NumberFormatException nfe){
					nfe.printStackTrace();
				}
				
				
					
				sbprint = ""; // don't use commentary characters for this line --- I'll kill u!
					
//				//}
				Log.d(TAG, "...String:" + sb.toString() + "Byte:"+ msg.arg1 + "...");
				break;
			}	
		}
	};
	
	
	/**
     * Handles messages from TGDevice
     * Es wird ein neuer Handler "eegHandler" erzeugt, der die reinkommenden Daten interpretiert und 
     * entsprechend weitere Befehle ausfuehrt oder (zu fast 100% in unserem Fall) die Daten ausgibt bzw. 
     * in ein Array schreibt 
     */
    private final Handler eegHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	
        	switch (msg.what) {
            
        	case TGDevice.MSG_STATE_CHANGE:
                switch (msg.arg1) {
	                case TGDevice.STATE_IDLE:
	                    break;
	                case TGDevice.STATE_CONNECTING:		                	
	                	//eeg_std_att.setText("Connecting...\n"+eeg_std_att.getText());
	                	break;		                    
	                case TGDevice.STATE_CONNECTED:
	                	//eeg_std_att.setText("Connected.\n" + eeg_std_att.getText()); 
	                	Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_SHORT).show();
	                	tgDevice.start();
	                    break;
	                case TGDevice.STATE_NOT_FOUND:
	                	//eeg_std_att.setText("Can't find\n" + eeg_std_att.getText());
	                	Toast.makeText(getApplicationContext(), "Not found - restart this window", Toast.LENGTH_SHORT).show();
	                	break;
	                case TGDevice.STATE_NOT_PAIRED:
	                	//eeg_std_att.setText("not paired\n" + eeg_std_att.getText());
	                	break;
	                case TGDevice.STATE_DISCONNECTED:
	                	//eeg_std_att.setText("Disconnected ...\n" + eeg_std_att.getText());
                }
                break;

        	case TGDevice.MSG_ATTENTION:
        		
        		eegAttentionList.add(msg.arg1);
				if (eegAttentionList.size() > displayedPoints) {
					eegAttentionList.remove(0);
				}
				eegAttentionSeries.setModel(eegAttentionList, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
				redrawer.start();
        		break;
        		
            case TGDevice.MSG_MEDITATION:
            	
            	eegMeditationList.add(msg.arg1);
				if (eegMeditationList.size() > displayedPoints) {
					eegMeditationList.remove(0);
				}
				eegMeditationSeries.setModel(eegMeditationList, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
				redrawer.start();
            	
            	break;
            case TGDevice.MSG_BLINK:
            		
            	
//            	if(enabled_blinks){
//            		blinkCounter += 1;
//            		if(!enabled_attention && !enabled_meditation){
//            			enabled_blinks = false;
//            		}
//            		Log.d("Blinks", "Der Wert: "+blinkCounter);
//            	}
//            		
//            		eeg_blink_counts.setText("Anzahl: " + blinkCounter);
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
	
//	
	
//	// unused
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.liar_test, menu);
//		return true;
//	}
	
	@Override
	protected void onActivityResult (int requestCode, int resultCode, Intent data){
		super.onActivityResult(requestCode, resultCode, data);
		//setup bluetooth connection
		switch (requestCode) {
		case BLUETOOTH_INTENT_CODE:
			setupGalvanicBluetooth();
			setupEegBluetooth();
			break;

		default:
			break;
		}
	}
}
