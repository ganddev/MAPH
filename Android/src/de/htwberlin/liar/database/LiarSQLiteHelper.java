package de.htwberlin.liar.database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import de.htwberlin.liar.database.LiarContract.Players;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class LiarSQLiteHelper extends SQLiteOpenHelper {
	
	private static final String TAG = "DATABASE";
	private static final String DATABASE_NAME = "liar_database.db";
	private static final int DATABASE_VERSION = 1;
	
	private Context context;

	
	public interface Tables {
		public final String PLAYERS = "players";
	}
	
	public LiarSQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;		
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.i(TAG, "Create database for liar app");
		db.execSQL("CREATE TABLE " + Tables.PLAYERS + " ("
				+ Players.PLAYER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ Players.PLAYER_NAME + " TEXT,"
				+ Players.PLAYER_POINTS  + " REAL,"
				+ Players.PLAYER_GAME_ID + " TEXT, "
				+ "UNIQUE (" + Players.PLAYER_ID + ") ON CONFLICT REPLACE);");
		
		final ContentValues values = new ContentValues();
        values.put("name", "Sophie");
        values.put("points", "50");
        db.insert(Tables.PLAYERS, null, values);
        
        final ContentValues values2 = new ContentValues();
        values2.put("name", "Max");
        values2.put("points", "100");
        db.insert(Tables.PLAYERS, null, values2);
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		database.execSQL("DROP TABLE IF EXISTS " +Tables.PLAYERS);
		onCreate(database);
	}

}
