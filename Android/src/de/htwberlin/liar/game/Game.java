package de.htwberlin.liar.game;

import java.util.List;
import java.util.Observable;

import android.util.Xml.Encoding;
import de.htwberlin.liar.model.Player;

public class Game extends Observable{
	
	private static final int ADD_POINTS = 10;
	private static final int SUBTRACT_POINTS = 5;
	private static final int CURRENT_PLAYER_INIT = 0;
	
	private int currentRound;
	private int currentPlayer;
	private int maxQuestions;
	private String currentQuestion;
	private Phase phase;
	private List<Player> players;
	
	public enum Phase {
		NEXT_QUESTION,
		ANSWER,
		GAME_END
	}

	public Game(final List<Player> players, final int maxQuestions) {
		if (players.isEmpty()) {
			throw new IllegalArgumentException("Player list is not allowed to be empty.");
		} else if (maxQuestions < CURRENT_PLAYER_INIT){
			throw new IllegalStateException("The max questtions can not be same or lower than " + CURRENT_PLAYER_INIT + ".");
		}
		this.currentRound = CURRENT_PLAYER_INIT;
		this.currentPlayer = 0;
		this.maxQuestions = maxQuestions;
		this.players = players;
		this.phase = Phase.NEXT_QUESTION;
		this.currentQuestion = null;
	}
	
	public String getQuestion(){
		return currentQuestion;
	}
	
	public Player getPlayer(){
		return players.get(currentPlayer);
	}
	
	public int getRound(){
		return currentRound;
	}
	
	public int getMaxQuestions(){
		return maxQuestions;
	}
	
	public boolean answerQuestion(boolean answer){	
		boolean isTrue = answer;
		if(isTrue){
			players.get(currentPlayer).addPoints(ADD_POINTS);
		}
		else {
			players.get(currentPlayer).subtractPoints(SUBTRACT_POINTS);
		}
		phase = Phase.NEXT_QUESTION;
		return isTrue;
	}
	
	public void next(){
		switch (phase) {
		case NEXT_QUESTION:
			nextQuestion();
			break;
		case GAME_END:
			notifyChanges();
			break;
		case ANSWER:
			throw new IllegalStateException("Please answer question first.");
		default:
			throw new IllegalStateException("No matching Phase found.");
		}
	}
	
	private void nextQuestion(){
		if(phase != Phase.GAME_END) currentRound++;
		if (currentRound <= maxQuestions) {
			currentQuestion = currentRound + " Sind sie ein L\u00fcgner?";
			phase = Phase.ANSWER;
		} else {
			currentPlayer++;
			if (currentPlayer < players.size()) {
				currentRound = CURRENT_PLAYER_INIT;
				nextQuestion(); //Recursion
			} else {
				phase = Phase.GAME_END;
			}
		}
		notifyChanges();
	}
	
	private void notifyChanges(){
		setChanged();
		notifyObservers(phase);
	}
	
	
}
