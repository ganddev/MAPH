package de.htwberlin.liar.activities;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import de.htwberlin.liar.R;
import de.htwberlin.liar.database.LiarContract.Questions;
import de.htwberlin.liar.game.Game;
import de.htwberlin.liar.model.GameInfo;
import de.htwberlin.liar.utils.Constants;
import de.htwberlin.liar.utils.DialogUtil;

public class GameActivity extends LiarActivity implements Observer, LoaderCallbacks<Cursor>{

	private Game game;
	private View answerButtonContainer;
	private View nextButton;
	private View yesButton;
	private View noButton;
	private TextView roundsDisplay;
	private TextView currentPlayerDisplay;
	private TextView questionDisplay;
	
	private String[] projection = { 
			Questions.QUESTION_ID,
			Questions.QUESTION
			};

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game_screen_layout);
		setUp();
	}
	
	@Override
	public void update(Observable observable, Object data) {
		if(!(data instanceof Game.Phase)){
			throw new IllegalStateException("Observer received unknown data. Data is not of type Game.Phase.");
		}
		Game.Phase phase = (Game.Phase) data;
		switch (phase) {
		case ANSWER:
			showQuestion();
			break;
		case GAME_END:
			DialogUtil.showMessageDialog(this, "Spiel beendet!");
			break;
		default:
			throw new IllegalStateException("No matching Phase found.");
		}
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		String selection = "";
		return new CursorLoader(getApplicationContext(), Questions.CONTENT_URI, projection, selection, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		List<String> questions = new ArrayList<String>();
		while (cursor.moveToNext()) {
			final int index = cursor.getColumnIndexOrThrow(Questions.QUESTION);
			String question = cursor.getString(index);
			questions.add(question);
		}
		setUpGame(questions);
		yesButton.setEnabled(true);
		noButton.setEnabled(true);
		game.next();
		
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		//Noting to do here
	}
	
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

	private void setUp() {
		setUpDisplays();
		LoaderManager lm = getLoaderManager();
		lm.initLoader(Constants.QUESTION_LOADER_ID, null, this);
	}

	private void setUpGame(List<String> questions) {
		GameInfo info = (GameInfo) getIntent().getParcelableExtra(GameInfo.TYPE);
		game = new Game(info.getPlayers(), questions, info.getRounds());
		game.addObserver(this);
	}

	private void setUpDisplays() {
		roundsDisplay = (TextView) findViewById(R.id.game_screen_rounds_label);
		currentPlayerDisplay = (TextView) findViewById(R.id.game_screen_current_player_display);
		questionDisplay = (TextView) findViewById(R.id.game_screen_question_text);
		answerButtonContainer = findViewById(R.id.game_screen_answer_button_container);
		nextButton = findViewById(R.id.game_screen_next_button);
		nextButton.setVisibility(View.GONE);
		nextButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				game.next();
			}
		});
		yesButton = findViewById(R.id.game_screen_yes_button);
		yesButton.setEnabled(false);
		yesButton.setOnClickListener(new View.OnClickListener() {
					
			@Override
			public void onClick(View v) {
				answerQuestion(true);
			}
		});
		noButton = findViewById(R.id.game_screen_no_button);
		noButton.setEnabled(false);
		noButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				answerQuestion(false);
			}
		});
	}
	
	private void showQuestion() {
		answerButtonContainer.setVisibility(View.VISIBLE);
		nextButton.setVisibility(View.GONE);
		roundsDisplay.setText(getString(R.string.round)+ " " + game.getRound() + "/" + game.getMaxQuestions());
		currentPlayerDisplay.setText(game.getPlayer().getName());
		questionDisplay.setText(game.getQuestion());
	};
	
	private void answerQuestion(boolean answer){
		answerButtonContainer.setVisibility(View.GONE);
		nextButton.setVisibility(View.VISIBLE);
		boolean result = game.answerQuestion(answer);
		String resultString = (result) ? getString(R.string.truth) : getString(R.string.lie);
		resultString = game.getPlayer().getName() + ", " + String.format(getString(R.string.result_text_for_question), resultString);
		currentPlayerDisplay.setText(resultString);	
	}
}
