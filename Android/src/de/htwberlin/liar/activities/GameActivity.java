package de.htwberlin.liar.activities;

import java.util.ArrayList;

import android.os.Bundle;
import android.widget.TextView;
import de.htwberlin.liar.R;
import de.htwberlin.liar.model.GameInfo;
import de.htwberlin.liar.model.Player;

public class GameActivity extends LiarActivity {

	private int currentRound;
	private int currentPlayer;
	private int maxRounds;
	private ArrayList<Player> players;
	private TextView roundsDisplay;
	private TextView currentPlayerDisplay;

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
		GameInfo info = (GameInfo) getIntent().getSerializableExtra(GameInfo.TYPE);
		currentRound = 1;
		currentPlayer = 0;
		maxRounds = info.getRounds();
		players = info.getPlayers();
	}

	private void setUpDisplays() {
		roundsDisplay = (TextView) findViewById(R.id.game_screen_rounds_display);
		roundsDisplay.setText(currentRound + "/" + maxRounds);

		currentPlayerDisplay = (TextView) findViewById(R.id.game_screen_current_player_display);
		//TODO: Only for testing
		currentPlayerDisplay.setText(players.get(currentPlayer).getName() + " ist an der Reihe!");
	}
}
