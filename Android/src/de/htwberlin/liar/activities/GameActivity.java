package de.htwberlin.liar.activities;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import de.htwberlin.liar.R;
import de.htwberlin.liar.model.GameInfo;
import de.htwberlin.liar.model.Player;

public class GameActivity extends LiarActivity implements OnClickListener {

	private int currentRound;
	private int currentPlayer;
	private int maxRounds;
	private List<Player> players;
	private TextView roundsDisplay;
	private TextView currentPlayerDisplay;
	private TextView mQuestionTextView;
	private LinearLayout mButtonLayout;
	
	private Button mYesButton, mNoButton, mNextQuestionBtn;
	
	private int mPoints = 0;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game_screen_layout);
		setUp();
	}

	private void setUp() {
		setUpGame();
		setUpDisplays();
	}

	private void setUpGame() {
		GameInfo info = (GameInfo) getIntent().getParcelableExtra(GameInfo.TYPE);
		currentRound = 1;
		currentPlayer = 0;
		maxRounds = info.getRounds();
		players = info.getPlayers();
	}

	private void setUpDisplays() {
		roundsDisplay = (TextView) findViewById(R.id.game_screen_rounds_label);
		roundsDisplay.setText(getString(R.string.round)+ " " + currentRound + "/" + maxRounds);
		mQuestionTextView = (TextView) findViewById(R.id.game_screen_question_text);
		currentPlayerDisplay = (TextView) findViewById(R.id.game_screen_current_player_display);
		//TODO: Only for testing
		currentPlayerDisplay.setText(players.get(currentPlayer).getName() + " ist an der Reihe!");
		
		mYesButton = (Button) findViewById(R.id.game_screen_yes_button);
		mYesButton.setVisibility(View.VISIBLE);
		mYesButton.setOnClickListener(this);
		mNoButton = (Button) findViewById(R.id.game_screen_no_button);
		mNoButton.setVisibility(View.VISIBLE);
		mNoButton.setOnClickListener(this);
		mButtonLayout = (LinearLayout) findViewById(R.id.game_screen_answer_button_container);
		mButtonLayout.setVisibility(View.VISIBLE);
		mNextQuestionBtn = (Button) findViewById(R.id.game_screen_next_question_button);
		mNextQuestionBtn.setVisibility(View.GONE);
		mNextQuestionBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {		
		if(view.getId() == R.id.game_screen_next_question_button && currentRound != (maxRounds+1)){
			setUpDisplays();
		} else if( view.getId() == R.id.game_screen_next_question_button && currentRound >= maxRounds){
			Intent intent = new Intent(this, ScoreActivity.class);
			startActivity(intent);
		} else {
			// TODO Start measurement here!!!
			setUpViewsForEvaluation(false);
		}
	}
	
	private void setUpViewsForEvaluation(final boolean lie){
		//First set the views to gone.
		mButtonLayout.setVisibility(View.GONE);
		currentPlayerDisplay.setVisibility(View.GONE);
		if(currentRound == maxRounds){
			mNextQuestionBtn.setText(getString(R.string.go_to_score));
		}
		mNextQuestionBtn.setVisibility(View.VISIBLE);
		if(lie){
			mQuestionTextView.setText(getString(R.string.lie));
			mPoints += 3;
		} else {
			mQuestionTextView.setText(getString(R.string.truth));
			mPoints += 10;
		}
		currentRound += 1;
	}
}
