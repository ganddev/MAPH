package de.htwberlin.liar.database;

import android.net.Uri;

public final class LiarContract {

	public static final String CONTENT_AUTHORITY = "de.htwberlin.liar";

	public static final Uri BASE_CONTENT_URI = Uri.parse("content://"
			+ CONTENT_AUTHORITY);
	
	interface PlayerColumns {
		String PLAYER_ID = "_id";
		public static final String PLAYER_NAME = "name";
		public static final String PLAYER_POINTS = "points";
		public static final String PLAYER_GAME_ID = "gameId";
	}
	
	interface QuestionsColumns {
		String QUESTION_ID = "_id";
		public static final String QUESTION = "question";
	}
	
	private static final String PATH_PLAYERS = "players";
	private static final String PATH_QUESTIONS = "questions";
	
	public static class Players implements PlayerColumns {
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_PLAYERS).build();
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.liar.players";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.liar.players";
		
		public static Uri buildPlayerUri(String playerId) {
			return CONTENT_URI.buildUpon().appendPath(playerId).build();
		}
	}
	
	public static class Questions implements QuestionsColumns{
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_QUESTIONS).build();
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.liar.questions";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.liar.questions";
		
		public static Uri buildPlayerUri(String questionId) {
			return CONTENT_URI.buildUpon().appendPath(questionId).build();
		}
	}
	
	private LiarContract(){}
}
