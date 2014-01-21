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
import android.widget.Button;
import android.widget.TextView;
import de.htwberlin.liar.R;
import de.htwberlin.liar.database.LiarContract.Questions;
import de.htwberlin.liar.game.Game;
import de.htwberlin.liar.model.GameInfo;
import de.htwberlin.liar.utils.Constants;
import de.htwberlin.liar.utils.DialogUtil;

public class GameActivity extends LiarActivity implements Observer, LoaderCallbacks<Cursor>{

	private Game game;
	private View mAnswerButtonContainer;
	private Button mNextButton;
	private View mYesButton;
	private View mNoButton;
	private TextView mRroundsTextView;
	private TextView mCurrentPlayerTextView;
	private TextView mQuestionTextView;
	
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
		mYesButton.setEnabled(true);
		mNoButton.setEnabled(true);
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
		mRroundsTextView = (TextView) findViewById(R.id.game_screen_rounds_label);
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
	
	private void showQuestion() {
		mAnswerButtonContainer.setVisibility(View.VISIBLE);
		mNextButton.setVisibility(View.GONE);
		mRroundsTextView.setText(getString(R.string.round)+ " " + game.getRound() + "/" + game.getMaxQuestions());
		mCurrentPlayerTextView.setText(game.getPlayer().getName());
		mQuestionTextView.setText(game.getQuestion());
	};
	
	private void answerQuestion(boolean answer){
		mAnswerButtonContainer.setVisibility(View.GONE);
		mNextButton.setVisibility(View.VISIBLE);
		boolean result = game.answerQuestion(answer);
		String resultString = (result) ? getString(R.string.truth) : getString(R.string.lie);
		resultString = game.getPlayer().getName() + ", " + String.format(getString(R.string.result_text_for_question), resultString);
		mCurrentPlayerTextView.setText(resultString);	
	}
}
