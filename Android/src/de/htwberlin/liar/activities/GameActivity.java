package de.htwberlin.liar.activities;

import java.util.List;

import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import de.htwberlin.liar.R;
import de.htwberlin.liar.database.LiarContract.Players;
import de.htwberlin.liar.model.GameInfo;
import de.htwberlin.liar.model.Player;

public class GameActivity extends LiarActivity implements OnClickListener {

	private int currentRound;
	private Player currentPlayer;
	private int maxRounds;
	private List<Player> players;
	private TextView roundsDisplay;
	private TextView currentPlayerDisplay;
	private TextView mQuestionTextView;
	private LinearLayout mButtonLayout;
	
	private Button mYesButton, mNoButton, mNextQuestionBtn;
	
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
		maxRounds = info.getRounds();
		players = info.getPlayers();
		if(players != null){
			currentPlayer = players.get(0);
		}
	}

	private void setUpDisplays() {
		roundsDisplay = (TextView) findViewById(R.id.game_screen_rounds_label);
		roundsDisplay.setText(getString(R.string.round)+ " " + currentRound + "/" + maxRounds);
		mQuestionTextView = (TextView) findViewById(R.id.game_screen_question_text);
		currentPlayerDisplay = (TextView) findViewById(R.id.game_screen_current_player_display);
		if(currentPlayer != null){
			currentPlayerDisplay.setText(currentPlayer.getName() + " ist an der Reihe!");
		}
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
		} else if( view.getId() == R.id.game_screen_next_question_button && currentRound >= maxRounds && players.isEmpty()){
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
		if(currentRound == maxRounds  && players.isEmpty()){
			updatePlayerScore();
			mNextQuestionBtn.setText(getString(R.string.go_to_score));
		} else {
			updatePlayerScore();
			mNextQuestionBtn.setText(getString(R.string.next_player));
		}
		mNextQuestionBtn.setVisibility(View.VISIBLE);
		if(lie){
			mQuestionTextView.setText(getString(R.string.lie));
			currentPlayer.addPoints(3);
		} else {
			mQuestionTextView.setText(getString(R.string.truth));
			currentPlayer.addPoints(10);
		}
		currentRound += 1;
	}
	
	private void updatePlayerScore() {
		ContentResolver cr = getContentResolver();

		String where = Players.PLAYER_NAME + "=? AND " + Players.PLAYER_GAME_ID
				+ "=?";
		String[] selectionArgs = { currentPlayer.getName(),
				currentPlayer.getGameId().toString() };
		cr.update(Players.CONTENT_URI, currentPlayer.toContentValues(), where,
				selectionArgs);
		players.remove(currentPlayer);
		if (!players.isEmpty()) {
			currentPlayer = players.get(0);

		}
	}
}
