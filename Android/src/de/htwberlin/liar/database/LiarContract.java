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
	}
	
	private static final String PATH_PLAYERS = "players";
	
	public static class Players implements PlayerColumns {
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_PLAYERS).build();
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.liar.players";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.liar.players";
		
		public static Uri buildPlayerUri(String playerId) {
			return CONTENT_URI.buildUpon().appendPath(playerId).build();
		}
	}
	
	private LiarContract(){}
}
