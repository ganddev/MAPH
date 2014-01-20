package de.htwberlin.liar.activities;

import java.util.Observable;
import java.util.Observer;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import de.htwberlin.liar.R;
import de.htwberlin.liar.game.Game;
import de.htwberlin.liar.model.GameInfo;
import de.htwberlin.liar.utils.DialogUtil;

public class GameActivity extends LiarActivity implements Observer{

	private Game game;
	private View answerButtonContainer;
	private View nextButton;
	private TextView roundsDisplay;
	private TextView currentPlayerDisplay;
	private TextView questionDisplay;

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

	private void setUp() {
		setUpGame();
		setUpDisplays();
		game.next();
	}

	private void setUpGame() {
		GameInfo info = (GameInfo) getIntent().getParcelableExtra(GameInfo.TYPE);
		game = new Game(info.getPlayers(), info.getRounds());
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
		findViewById(R.id.game_screen_yes_button).setOnClickListener(new View.OnClickListener() {
					
			@Override
			public void onClick(View v) {
				answerQuestion(true);
			}
		});
		findViewById(R.id.game_screen_no_button).setOnClickListener(new View.OnClickListener() {
			
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
		currentPlayerDisplay.setText(game.getPlayer().getName() + ", ihre Antwort war " + ((result) ? "wahr." : "gelogen." ));
		
		
	}
}
