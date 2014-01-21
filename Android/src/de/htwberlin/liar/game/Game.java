package de.htwberlin.liar.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Random;

import de.htwberlin.liar.model.Player;

public class Game extends Observable{
	
	private static final int ADD_POINTS = 10;
	private static final int SUBTRACT_POINTS = 5;
	
	private int currentPlayer;
	private int maxQuestions;
	private String currentQuestion;
	private Phase phase;
	private List<Player> players;
	private List<String> questions;
	private List<String> currentQuestionSet;
	
	public enum Phase {
		NEXT_QUESTION,
		ANSWER,
		GAME_END
	}

	public Game(final List<Player> players, final List<String> questions, final int maxQuestions) {
		if (players.isEmpty()) {
			throw new IllegalArgumentException("Player list is not allowed to be empty.");
		} else if (maxQuestions <= 0){
			throw new IllegalStateException("The max questtions can not be zero.");
		}
		this.currentPlayer = 0;
		this.maxQuestions = maxQuestions;
		this.players = players;
		this.questions = questions;
		this.currentQuestionSet = new ArrayList<String>();
		createQuestionSet();
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
		final int round = maxQuestions - currentQuestionSet.size();
		return  (round > maxQuestions) ? maxQuestions : round;
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
			notifyChanges();
		default:
			throw new IllegalStateException("No matching Phase found.");
		}
	}
	
	private void nextQuestion(){
		if (getRound() + 1 <= maxQuestions) {
			currentQuestion = findQuestion();
			phase = Phase.ANSWER;
		} else {
			currentPlayer++;
			if (currentPlayer < players.size()) {
				createQuestionSet();
				currentQuestion = findQuestion();
				phase = Phase.ANSWER;
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
	
	private String findQuestion(){
		String nextQuestion = null;
		nextQuestion = currentQuestionSet.get(0);
		currentQuestionSet.remove(nextQuestion);
		return nextQuestion;
	}
	
	private void createQuestionSet(){
		Random random = new Random();
		currentQuestionSet.clear();
		int i = 0;
		while(i < maxQuestions){
			final int index = random.nextInt(questions.size());
			String question = questions.get(index); 
			if (!currentQuestionSet.contains(question)) {
				currentQuestionSet.add(question);
				i++;
			}
		}
	}	
	
}
