package de.htwberlin.liar.activities;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.UUID;

import com.neurosky.thinkgear.TGDevice;

import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import de.htwberlin.liar.R;
import de.htwberlin.liar.database.LiarContract.Questions;
import de.htwberlin.liar.game.Game;
import de.htwberlin.liar.game.Game.Phase;
import de.htwberlin.liar.model.GameInfo;
import de.htwberlin.liar.utils.ConnectedThread;
import de.htwberlin.liar.utils.Constants;
import de.htwberlin.liar.utils.DialogUtil;
import de.htwberlin.liar.utils.MatheBerechnungen;

/**
 * This class handels the game screen.
 */
public class GameActivity extends LiarActivity implements Observer, LoaderCallbacks<Cursor>{

	private static final double LIE_FACTOR_0_1 = 0.1;
	/**The class containing the business logic of teh game*/
	private Game game;
	/**Container with the yes/no buttons*/
	private View mAnswerButtonContainer;
	/**The Button for the next question or to switch to the {@link ScoreActivity}*/
	private Button mNextButton;
	/**The yes button for an answer*/
	private View mYesButton;
	/**The yes no for an answer*/
	private View mNoButton;
	/**The view showing the current and max rounds*/
	private TextView mRoundsTextView;
	/**The view with the current player*/
	private TextView mCurrentPlayerTextView;
	/**The view with the question or other informations*/
	private TextView mQuestionTextView;
	
	
	private UUID gameId;
	/**The projection for the {@link LoaderManager}*/
	private String[] projection = { 
			Questions.QUESTION_ID,
			Questions.QUESTION
			};


	/**
	 * Sets the layout and calls {@link #setUp()}.
	 */

	// --- ab hier kommt jetzt alles fuer die Bluetooth-Konnektivitaet --- //
	
	private static final String TAG = "bluetooth2";
	private static final int BLUETOOTH_INTENT_CODE = 2;
	
	private boolean galvanicFinishedFlag = false;
	private boolean eegAttentionFinishedFlag = false;
	private boolean eegMeditationFinishedFlag = false;
	
	private boolean calibrationGalvanicFlag = true;
	private boolean calibrationAttentionFlag = true;
	private boolean calibrationMedtationFlag = true;
	private boolean calibrationBlinkFlag = true;
	
	//the blink counter from eeg
	int blinkCounter;
	
	//A string to output the galvanic value
	String sbprint = "";
	
	//array for compute the standard derivation
	private static final int ARRAYLENGTH = 10;
	int[] std_att, std_med, std_resis;
	
	//Status zum Datensameln
	private boolean enabled_attention, enabled_meditation, enabled_blinks, enabled_galvanic;
	
	private int attentionArrayCounter, meditationArrayCounter, galvanicArrayCounter;
	
	//result for specified standard derivation
	double std_res_att, std_res_med, std_res_resis;
	double after_calib_att, after_calib_med, after_calib_resis, after_calib_blinks;
	
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

	//the well known UUID for Bluetooth Connections
	private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

	// Change this adress to your device mac - the mac from your arduino bt shield
	// linvor address 00:12:07:17:18:24
	private static String ADDRESS = "00:12:07:17:18:24";
	
	//BluetoothDevice galvanic skin
	BluetoothDevice galvanicBtDevice;
		
	//TGDevice is used for pairing to myndplay eeg
	TGDevice tgDevice;
	
	// --- Ende der Bluetooth Sachen --- //
	

	private ProgressDialog pDlg;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game_screen_layout);
		setUp();
		
		//setUpcalibrationDialog();
		
		Bundle extras = getIntent().getExtras();
		if(extras != null && extras.containsKey(Constants.GAME_ID)){
			gameId = UUID.fromString(extras.getString(Constants.GAME_ID));
		}
	
	
		// --- jetzt kommt hier alles fuer Bluetooth
		
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
		
		//Zaehler f�r die Durchlaeufe durch die Wertearray
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
		
		/*
		 * jetzt kommen ein paar Speicherobjekte fuer nach der Kalibrierung
		 */
		after_calib_att = 0.0;
		after_calib_med =0.0; 
		after_calib_resis = 0.0; 
		after_calib_blinks = 0;
		
		
		galvanicAdapter = BluetoothAdapter.getDefaultAdapter();
		eegAdapter = BluetoothAdapter.getDefaultAdapter();
	
		if(eegAdapter != null){		
			Log.d(TAG, "... the EEG Address is correct...");
			//Check the bluetooth state.
			checkBTState();
		}	
		
		// --- Ende: Bluetooth Connection, Zaehler, Arrays.... Luegendetektor-Stuff --- //
	}

	
	
	private void setUpcalibrationDialog() {
		pDlg = new ProgressDialog(this);
		pDlg.setMessage(getString(R.string.do_calibrate));
		pDlg.setCancelable(false);
		pDlg.show();	
	}


	/**
	 * The {@link Observer} update method. Updates depend on the phase {@link Phase}
	 * provided by the {@link Observable}.
	 * 
	 * <table>
	 * 		<tr>
	 * 			<td>ANSWER</td>
	 * 			<td>GAME_END</td>
	 * 		</tr>
	 * 		<tr>
	 * 			<td>Shows the Question and waits for an Answer.</td>
	 * 			<td>Switch to the {@link ScoreActivity} when {@link #mNextButton} is pressed.</td>
	 * 		</tr>
	 * </table>
	 * 
	 * @throws IllegalArgumentException if data is not of Type {@link Phase}.
	 * @throws IllegalStateException if {@link Phase} is neither {@link Phase#ANSWER} nor {@link Phase#GAME_END}
	 */
	@Override
	public void update(Observable observable, Object data) {
		if(!(data instanceof Game.Phase)){
			throw new IllegalArgumentException("Observer received unknown data. Data is not of type Game.Phase.");
		}
		Game.Phase phase = (Game.Phase) data;
		switch (phase) {
		case ANSWER:
			showQuestion();
			break;
		case GAME_END:
			mNextButton.setText(getString(R.string.go_to_score));
			mNextButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent nextActivity = new Intent(GameActivity.this, ScoreActivity.class);
					nextActivity.putExtra(Constants.GAME_ID, gameId.toString());
					startActivity(nextActivity);		
				}
			});
			
			break;
		default:
			throw new IllegalStateException("No matching Phase found.");
		}
		
	}
	
	/**
	 * Creates a new {@link CursorLoader}, when the loader is created.
	 */
	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		String selection = "";
		return new CursorLoader(getApplicationContext(), Questions.CONTENT_URI, projection, selection, null, null);
	}

	/**
	 * Gets all Questions to Set up the game. Enables the yes and no button and starts the game.
	 */
	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		List<String> questions = new ArrayList<String>();
		while (cursor.moveToNext()) {
			final int index = cursor.getColumnIndexOrThrow(Questions.QUESTION);
			String question = cursor.getString(index);
			questions.add(question);
		}
		setUpGame(questions);
		mYesButton.setEnabled(true);
		mNoButton.setEnabled(true);
		game.next();
		
	}

	/**
	 * Does nothing.
	 */
	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		//Noting to do here
	}
	
	/**
	 * When back button is pressed asked if the game should be stopped. If so,
	 * go back to the {@link StartActivity} an remove other Acivities from the stack.
	 * If not, does nothing.
	 */
	@Override
	public void onBackPressed() {
		DialogUtil.showConfirmDialog(this, R.string.back_to_tile, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            	Intent intent = new Intent(GameActivity.this, StartActivity.class);
            	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            	startActivity(intent);
                dialog.dismiss();
            }
        });
		
	}

	/**
	 * Calls {@link #setUpDisplays()} and initializes the {@link LoaderManager}.
	 */
	private void setUp() {
		setUpDisplays();
		LoaderManager lm = getLoaderManager();
		lm.initLoader(Constants.QUESTION_LOADER_ID, null, this);
	}

	/**
	 * Creates the {@link Game} objects for this class. Also adds this class as
	 * {@link Observer} for the game.
	 * 
	 * @param questions the list of questions to choose from in this game
	 */
	private void setUpGame(List<String> questions) {
		GameInfo info = (GameInfo) getIntent().getParcelableExtra(GameInfo.TYPE);
		game = new Game(info.getPlayers(), questions, info.getRounds(), getApplicationContext());
		game.addObserver(this);
	}

	/**
	 * Sets up all the {@link View}s and adds listeners to the button to processed.
	 */
	private void setUpDisplays() {
		mRoundsTextView = (TextView) findViewById(R.id.game_screen_rounds_label);
		mCurrentPlayerTextView = (TextView) findViewById(R.id.game_screen_current_player_display);
		mQuestionTextView = (TextView) findViewById(R.id.game_screen_question_text);
		mAnswerButtonContainer = findViewById(R.id.game_screen_answer_button_container);
		mNextButton = (Button) findViewById(R.id.game_screen_next_question_button);
		mNextButton.setVisibility(View.GONE);
		mNextButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				game.next();
			}
		});
		mYesButton = findViewById(R.id.game_screen_yes_button);
		mYesButton.setEnabled(false);
		mYesButton.setOnClickListener(new View.OnClickListener() {
					
			@Override
			public void onClick(View v) {
				enabled_attention = true;
				enabled_galvanic = true;
				enabled_meditation = true;
				enabled_blinks = true;
			}
		});
		mNoButton = findViewById(R.id.game_screen_no_button);
		mNoButton.setEnabled(false);
		mNoButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				enabled_attention = true;
				enabled_galvanic = true;
				enabled_meditation = true;
				enabled_blinks = true;
			}
		});
	}
	
	/**
	 * Adds the question to the question view and also updates the other views, which
	 * show the games state.
	 */
	private void showQuestion() {
		mAnswerButtonContainer.setVisibility(View.VISIBLE);
		mNextButton.setVisibility(View.GONE);
		mRoundsTextView.setText(getString(R.string.round)+ " " + game.getRound() + "/" + game.getMaxQuestions());
		mCurrentPlayerTextView.setText(game.getPlayer().getName());
		mQuestionTextView.setText(game.getQuestion());
	};
	
	/**
	 * Called when answered a question with the yes or no button.
	 * Forwards the answer to the game, gets the result and updates the views as informations for teh user.
	 * The answer button container will be hidden and the next button will appear.
	 * 
	 * @param answer
	 */
	private void answerQuestion(boolean answer){
		mAnswerButtonContainer.setVisibility(View.GONE);
		mNextButton.setVisibility(View.VISIBLE);
		boolean result = game.answerQuestion(answer);
		String resultString = (result) ? getString(R.string.truth) : getString(R.string.lie);
		resultString = game.getPlayer().getName() + ", " + String.format(getString(R.string.result_text_for_question), resultString);
		mCurrentPlayerTextView.setText(resultString);	
	}
	
	// --- TAKE CARE: alle for Bluetooth comes up here --- //
	
	/**
	 * simpliest way to empty an array
	 * @param array the array 
	 * @param length the length ot the array
	 */
	private void arrayLeeren(int array[], int length){
		
		for(int i = 0; i < length; i++){
			array[i] = 0;
		}
	}
	
	/**
	 * Methode baut eine Bluetooth-Verbindung zwischen dem EEG und dem Smartphone auf. Dazu wird auf
	 * den globalen Bluetooth-Adapter zugegriffen und deren Inhalt ueberprueft. Zur Connection wird 
	 * ausserdem das TGdevice aus lib/ThinkGear.jar verwendet. Die Bibliothek ist ein Blackbox - daher 
	 * keine Ahnung was dadrin passiert! Good Luck ^^  
	 */
	private void setupEegBluetooth(){
		
		eegAdapter.startDiscovery();

		Toast.makeText(this, "Create new TGDevice...", Toast.LENGTH_LONG)
				.show();
		tgDevice = new TGDevice(eegAdapter, eegHandler);
		Log.d(TAG, "...TGDevice initialized:...");// +tgDevice.getConnectedDevice().toString());
		tgDevice.connect(true);
		tgDevice.start();

		eegAdapter.cancelDiscovery();
	
	}
	
	/**
	 * try to build a galvanic connection
	 */
	private void setupGalvanicBluetooth(){
		
		galvanicBtDevice = galvanicAdapter.getRemoteDevice(ADDRESS);
		
		try {
			galvanicBtSocket = createBluetoothSocket(galvanicBtDevice);
		} catch (final IOException e) {
			Log.e(TAG, e.getMessage());
			exitWithErrorMessage("Fatal Error", "In onResume() and socket create failed: "
					+ e.getMessage() + ".");
		}

		//Cancel discovery because it need to much ressources
		galvanicAdapter.cancelDiscovery();

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

		// Create a data stream so we can talk to server.
		Log.d(TAG, "...Create Socket...");
		if (galvanicBtSocket != null) {
			mConnectedThread =  new ConnectedThread(galvanicBtSocket, gsHandler, RECIEVE_MESSAGE);
			mConnectedThread.start();
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
    	if(pDlg != null){
    		pDlg.dismiss();
    		pDlg = null;
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
				//setUpcalibrationDialog();
				//pDlg.setMessage(getString(R.string.setup_bluetooth));
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
				//Log.d(TAG, "the incoming String: "+strIncom);
				sb.append(strIncom); // append string to stringbuilder sb
				
				int startOfValueIndex = sb.indexOf(";");
				//Log.d(TAG, "my start of Value index: "+startOfValueIndex);
				int endOfValueIndex_1 = sb.indexOf("\r\n"); // determine the end-of-line
				//Log.d(TAG, "my end of Value index 1: "+endOfValueIndex_1);
				int endOfValueIndex_2 = sb.indexOf(" ");				
				//Log.d(TAG, "my end of Value index 2: "+endOfValueIndex_2);
				if (endOfValueIndex_1 > startOfValueIndex) { // if end-of-line,
					sbprint = sb.substring(startOfValueIndex+1, endOfValueIndex_1);// extract string
					sb.delete(0, sb.length()); // and clear
				} else if(endOfValueIndex_2 > startOfValueIndex) {
						sbprint = sb.substring(startOfValueIndex+1, endOfValueIndex_2);
						sb.delete(0, sb.length()); // and clear
					} else {
						break;
					}	
				/*
				 * hier war eine Ueberpruefung durch die Standardabweichung erwuenscht 
				 * 
				 * nur solange wie der Counter < 10 (ARRAYLENGTH) ist, wird das Array mit den aktuellen Daten 
				 * versorgt, sonst wird die Standardabweichung fuer genau dieses Array berechnet und in der *_res_* 
				 * Variable gespeichert
				 */
					 
				if(enabled_galvanic){
					if (calibrationGalvanicFlag) {
						if (galvanicArrayCounter >= 0
								&& galvanicArrayCounter < ARRAYLENGTH) {
							std_resis = MatheBerechnungen.werteSichern(
									galvanicArrayCounter, std_resis,
									Integer.valueOf(sbprint), TAG);
							galvanicArrayCounter += 1;
						} else {
							galvanicArrayCounter = 0;
							after_calib_resis = MatheBerechnungen
									.standardAbweichung(std_resis);
							Log.d(TAG, "End of enabled_galvanic");
							calibrationGalvanicFlag = false;
						}
					} else {
						if (galvanicArrayCounter >= 0
								&& galvanicArrayCounter < ARRAYLENGTH) {
							std_resis = MatheBerechnungen.werteSichern(
									galvanicArrayCounter, std_resis,
									Integer.valueOf(sbprint), TAG);
							galvanicArrayCounter += 1;
						} else {
							galvanicArrayCounter = 0;
							std_res_resis = MatheBerechnungen
									.standardAbweichung(std_resis);
							enabled_galvanic = false;
							Log.d(TAG, "End of enabled_galvanic");
							galvanicFinishedFlag = true;
						}
					}
				}	  
//            	    
//					gs_std_resis.setText("");
//					gs_std_resis.setText("Data from Arduino: " + sbprint); // update TextView
					sbprint = "";
//				}
				Log.d(TAG, "...String:" + sb.toString() + "Byte:"
						+ msg.arg1 + "...");
				break;
			}
			Log.d(TAG, "Values: " +eegMeditationFinishedFlag +" ," + galvanicFinishedFlag + " ," +eegAttentionFinishedFlag);
			if(eegMeditationFinishedFlag && galvanicFinishedFlag && eegAttentionFinishedFlag){
				theHonestSkin();
			}
		};
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
//	                	eeg_std_att.setText("Connecting...\n"+eeg_std_att.getText());
	                	Log.d(TAG, "TGDevice State: connecting");
	                	break;		                    
	                case TGDevice.STATE_CONNECTED:
//	                	eeg_std_att.setText("Connected.\n" + eeg_std_att.getText()); 
	                	Log.d(TAG, "TGDevice State: connected");
	                	tgDevice.start();
	                	//pDlg.setMessage(getString(R.string.do_calibrate));
	                    break;
	                case TGDevice.STATE_NOT_FOUND:
	                	Log.d(TAG, "TGDevice State: not found");
//	                	eeg_std_att.setText("Can't find\n" + eeg_std_att.getText());
	                	break;
	                case TGDevice.STATE_NOT_PAIRED:
	                	Log.d(TAG, "TGDevice State: not paired");
//	                	eeg_std_att.setText("not paired\n" + eeg_std_att.getText());
	                	break;
	                case TGDevice.STATE_DISCONNECTED:
	                	Log.d(TAG, "TGDevice State: disconnected");
//	                	eeg_std_att.setText("Disconnected ...\n" + eeg_std_att.getText());
                }
                break;

        	case TGDevice.MSG_ATTENTION:
        		
        		//Ausfuehrung der Sicherung der Attentionwerte im std_att-Array und ggf. STD-Berechnung
        		//gleiches vorgehen wie bei den Galvanic Skin Werten
            	
        		if(enabled_attention){
					if(calibrationAttentionFlag) {
						if (attentionArrayCounter >= 0
								&& attentionArrayCounter < ARRAYLENGTH) {
							std_att = MatheBerechnungen.werteSichern(
									attentionArrayCounter, std_att,
									Integer.valueOf(msg.arg1), TAG); // is
																		// msg.arg1
																		// still
																		// an
																		// integer?
							attentionArrayCounter += 1;
						} else {
							attentionArrayCounter = 0;
							after_calib_att = MatheBerechnungen
									.standardAbweichung(std_att);
							Log.d("STD Attention", "Der Wert: " + std_res_att);
							calibrationAttentionFlag = false;
							Log.d(TAG, "End of enabled_attention");
						}
					} else {
						if(attentionArrayCounter >=0 && attentionArrayCounter < ARRAYLENGTH){
							std_att = MatheBerechnungen.werteSichern(attentionArrayCounter, std_att,  Integer.valueOf(msg.arg1), TAG); //is msg.arg1 still an integer?
							attentionArrayCounter += 1;
						} else {
							attentionArrayCounter = 0;
	    		    		std_res_att = MatheBerechnungen.standardAbweichung(std_att);
	    		    		Log.d("STD Attention", "Der Wert: "+std_res_att);
	    		    		enabled_attention = false;
	    		    		eegAttentionFinishedFlag = true;
	    		    		Log.d(TAG, "End of enabled_attention");
						}
					}
				}	 
        		
//        		eeg_std_att.setText("Attention: " + msg.arg1 + "\n" + eeg_std_att.getText());
        		break;
            case TGDevice.MSG_MEDITATION:
            	
            	//Ausfuehrung der Sicherung der Attentionwerte im std_med-Array und ggf. STD-Berechnung
            	//gleiches vorgehen wie bei den Galvanic Skin Werten
            	
            	if(enabled_meditation){
					if(calibrationMedtationFlag){
						if (meditationArrayCounter >= 0
								&& meditationArrayCounter < ARRAYLENGTH) {
							std_med = MatheBerechnungen.werteSichern(
									meditationArrayCounter, std_med,
									Integer.valueOf(msg.arg1), TAG); // is
																		// msg.arg1
																		// still
																		// an
																		// integer?
							meditationArrayCounter += 1;
						} else {
							meditationArrayCounter = 0;
							after_calib_med = MatheBerechnungen
									.standardAbweichung(std_med);
							Log.d("STD Meditation", "Der Wert: " + std_res_med);
							calibrationMedtationFlag = false;
						}
					} else {
						if(meditationArrayCounter >=0 && meditationArrayCounter < ARRAYLENGTH){
							std_med = MatheBerechnungen.werteSichern(meditationArrayCounter, std_med,  Integer.valueOf(msg.arg1),TAG); //is msg.arg1 still an integer?
							meditationArrayCounter += 1;
						} else {
							meditationArrayCounter = 0;
	    		    		std_res_med = MatheBerechnungen.standardAbweichung(std_med);
	    		    		Log.d("STD Meditation", "Der Wert: "+std_res_med);
	    		    		enabled_meditation = false;
	    		    		Log.d(TAG, "End of enabled_meditation");
	    		    		eegMeditationFinishedFlag = true;
						}
					}
				}
            	
//            	eeg_std_medit.setText("Meditation: " + msg.arg1 + "\n" + eeg_std_medit.getText());
            	break;
            case TGDevice.MSG_BLINK:
            		
            		// hier wird der Blinzel-Counter erhoeht, toll, was !? ^^
            		
            	if(enabled_blinks){
					if (calibrationBlinkFlag) {
						blinkCounter += 1;
						if (!enabled_attention && !enabled_meditation) {
							enabled_blinks = false;
						}
						after_calib_blinks = blinkCounter;
						blinkCounter = 0;
						calibrationBlinkFlag = false;
						Log.d("Blinks", "Der Wert: " + blinkCounter);
					} else {
						blinkCounter += 1;
						if (!enabled_attention && !enabled_meditation) {
							enabled_blinks = false;
						}
						Log.d("Blinks", "Der Wert: " + blinkCounter);
					}
            	}            		
//            		eeg_blink_counts.setText("Anzahl: " + blinkCounter);
            	break;
            case TGDevice.MSG_LOW_BATTERY:
            	Toast.makeText(getApplicationContext(), "Low battery!", Toast.LENGTH_SHORT).show();
            	break;
            default:
            	break;
        	}
        	if(eegMeditationFinishedFlag && galvanicFinishedFlag && eegAttentionFinishedFlag){
    			theHonestSkin();
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
		return device.createRfcommSocketToServiceRecord(MY_UUID);
	}
	
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
	
	
	/**
	 * the method theHonestSkin checks all calculated standard derivation to the calibrated values and computes a result (truth or lie)
	 * and checks whether three of the lies are true to give out a true (it's a liar) or false (no it's an hones player)
	 * @return true (liar) or false (honest skin)
	 */
	private boolean theHonestSkin(){
		Log.d(TAG, "values: " + eegAttentionFinishedFlag + " " +eegMeditationFinishedFlag + " " + galvanicFinishedFlag);
		Log.d(TAG, "Calculate lie...");
		boolean liar = false;
		
		boolean att_lie = false;
		if(std_res_att <= (after_calib_att += after_calib_att*LIE_FACTOR_0_1)){
			att_lie = false;
		}else{
			att_lie = true;
		}
		
		boolean med_lie = false;
		if(std_res_med >= (after_calib_med -= after_calib_med*LIE_FACTOR_0_1)){
			med_lie = false;
		}else{
			med_lie = true;
		}
		
		boolean res_lie = false;
		if(std_res_resis <= (after_calib_resis += after_calib_resis*LIE_FACTOR_0_1)){
			res_lie = false;
		}else{
			res_lie = true;
		}
		
		boolean blinks_lie = false;
		if(blinkCounter > after_calib_blinks){
			blinks_lie = false;
		}else{
			blinks_lie = true;
		}
		
		if((att_lie && med_lie && res_lie) || (att_lie && med_lie && blinks_lie) || 
				(att_lie && res_lie && blinks_lie) || (med_lie && res_lie && blinks_lie)){
			liar = true;
		}
		eegAttentionFinishedFlag = false;
		eegMeditationFinishedFlag = false;
		galvanicFinishedFlag = false;
		
		answerQuestion(liar);
		return liar;
	}	
}

