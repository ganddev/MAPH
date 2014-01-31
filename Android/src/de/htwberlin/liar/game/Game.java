package de.htwberlin.liar.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

import android.content.ContentResolver;
import android.content.Context;
import android.util.Log;
import de.htwberlin.liar.database.LiarContract.Players;
import de.htwberlin.liar.model.Player;

/**
 * This class contains the Game logic. It most important method is the {@link #next()} method,
 * which let the game proceed. Not that you have to register yourself as {@link Observer}
 * of this class to get notified of a change after calling {@link #next()}.
 */
public class Game extends Observable{
	
	
	private Context mContext;
	
	/**Points to add per correct answered question*/
	private static final int ADD_POINTS = 10;
	/**Points to subtract per correct answered question*/
	private static final int SUBTRACT_POINTS = 5;
	
	/**Index of the current player*/
	private int currentPlayer;
	/**The current round*/
	private int currentRound;
	/**The current question to answer*/
	private String currentQuestion;
	/**The current {@link Phase}*/
	private Phase phase;
	/**List of players for this game*/
	private List<Player> players;
	/**List of questions for current player*/
	private List<String> questionSet;
	
	/**
	 * Enum for the Game Phases.
	 * 
	 * <table>
	 * 		<tr>
	 * 			<td>NEXT_QUESTION</td>
	 * 			<td>ANSWER</td>
	 * 			<td>GAME_END</td>
	 * 		</tr>
	 * 		<tr>
	 * 			<td>The Game is currently searching for the next question.</td>
	 * 			<td>The game waits for an answer.</td>
	 * 			<td>The game ended.</td>
	 * 		</tr>
	 * </table>
	 */
	public enum Phase {
		NEXT_QUESTION,
		ANSWER,
		GAME_END
	}

	/**
	 * The constructor sets up the game. This includes all class fields.
	 * 
	 * @param players the players of this game
	 * @param questions the questions of this game
	 * @param maxQuestions the max questions per player
	 */
	public Game(final List<Player> players, final List<String> questions, final int maxQuestions, final Context context) {
		if (players.isEmpty()) {
			throw new IllegalArgumentException("Player list is not allowed to be empty.");
		} else if (questions.size() < maxQuestions){
			throw new IllegalArgumentException("The max questions must be equal or less than number of given questions.");
		}
		this.currentPlayer = 0;
		this.players = players;
		this.currentRound = 0;
		createQuestionSet(questions, maxQuestions);
		this.phase = Phase.NEXT_QUESTION;
		this.currentQuestion = null;
		mContext = context;
	}
	
	/**
	 * Gets the current question
	 * 
	 * @return the current question
	 */
	public String getQuestion(){
		return currentQuestion;
	}
	
	/**
	 * Gets the current player
	 * 
	 * @return the current player
	 */
	public Player getPlayer(){
		return players.get(currentPlayer);
	}
	
	/**
	 * Gets the current round. The current round is <code>maxQuestions - currentQuestionSet.size()</code>
	 * and can not be higher than the max number of questions.
	 * 
	 * @return the current round.
	 */
	public int getRound(){
		return  currentRound;
	}
	
	/**
	 * Gets the max number of questions.
	 * 
	 * @return max number of questions
	 */
	public int getMaxQuestions(){
		return questionSet.size();
	}
	
	/**
	 * Answers a question and returns if the answer was a lie or the truth.
	 * 
	 * @param answer
	 * @return
	 */
	public boolean answerQuestion(boolean answer){	
		boolean isTrue = answer;
		final Player currentPlayerObj = players.get(currentPlayer);
		if(isTrue){
			currentPlayerObj.addPoints(ADD_POINTS);
		}
		else {
			currentPlayerObj.subtractPoints(SUBTRACT_POINTS);
		}
		updatePlayerInDB(currentPlayerObj);
		phase = Phase.NEXT_QUESTION;
		return isTrue;
	}
	
	
	private void updatePlayerInDB(final Player player) {
		final ContentResolver cr = mContext.getContentResolver();
		String where = Players.PLAYER_NAME +" =?  AND " + Players.PLAYER_GAME_ID + "=?";
		String[] selectionArgs = {players.get(currentPlayer).getName(), players.get(currentPlayer).getGameId().toString()};
		int rows = cr.update(Players.CONTENT_URI, players.get(currentPlayer).toContentValues(), where, selectionArgs);
		Log.d("Updated rows ",":" +rows);
	}
	/**
	 * 
	 */
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
			break;
		default:
			throw new IllegalStateException("No matching Phase found.");
		}
	}
	
	/**
	 * Calculates the next question. If <code>getRound() + 1 > questionSet.size()</code>
	 * the next player will be selected. If there is no more next player, 
	 * <code>currentPlayer + 1 >= players.size()</code>, the game ends.<br>
	 * This method will always notify the {@link Observer}.
	 */
	private void nextQuestion(){
		if (getRound() + 1 <= questionSet.size()) {
			currentQuestion = findQuestion();
			phase = Phase.ANSWER;
		} else if (currentPlayer + 1 < players.size()) {
			currentRound = 0;
			currentPlayer++;
			currentQuestion = findQuestion();
			phase = Phase.ANSWER;
		}
		else {
			phase = Phase.GAME_END;
		}
		notifyChanges();
	}
	
	/**
	 * Notify the {@link Observer} with the current {@link Phase}.
	 */
	private void notifyChanges(){
		setChanged();
		notifyObservers(phase);
	}
	
	/**
	 * First gets the next question form the question set, 
	 * <code>questionSet.get(currentRound)</code>, and than
	 * increases the Round by one. 
	 * 
	 * @return the next question
	 */
	private String findQuestion(){
		final String question = questionSet.get(currentRound);
		currentRound++;
		return question;
	}
	
	/**
	 * Creates a random question set form the List of available questions.
	 * 
	 * @param questions the questions to chose from
	 * @param size the size of the set to create
	 */
	private void createQuestionSet(List<String> questions, int size){
		this.questionSet = new ArrayList<String>(size);
		Random random = new Random();
		questionSet.clear();
		for(int i = 0; i < size; i++){
			final int index = random.nextInt(questions.size());
			String question = questions.get(index); 
			questionSet.add(question);
			questions.remove(question);
		}
	}
	
}
