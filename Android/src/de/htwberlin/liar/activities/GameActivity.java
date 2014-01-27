package de.htwberlin.liar.activities;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import android.R.layout;
import android.app.Activity;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import de.htwberlin.liar.R;
import de.htwberlin.liar.database.LiarContract.Questions;
import de.htwberlin.liar.game.Game;
import de.htwberlin.liar.game.Game.Phase;
import de.htwberlin.liar.model.GameInfo;
import de.htwberlin.liar.utils.Constants;
import de.htwberlin.liar.utils.DialogUtil;

/**
 * This class handels the game screen.
 */
public class GameActivity extends LiarActivity implements Observer, LoaderCallbacks<Cursor>{

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
	
	/**The projection for the {@link LoaderManager}*/
	private String[] projection = { 
			Questions.QUESTION_ID,
			Questions.QUESTION
			};

	/**
	 * Sets the layout and calls {@link #setUp()}.
	 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game_screen_layout);
		setUp();
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
					startActivity(new Intent(GameActivity.this, ScoreActivity.class));		
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
		game = new Game(info.getPlayers(), questions, info.getRounds());
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
				answerQuestion(true);
			}
		});
		mNoButton = findViewById(R.id.game_screen_no_button);
		mNoButton.setEnabled(false);
		mNoButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				answerQuestion(false);
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
}
