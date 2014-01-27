package de.htwberlin.liar.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import de.htwberlin.liar.R;
import de.htwberlin.liar.database.LiarContract.Players;
import de.htwberlin.liar.database.LiarContract.Questions;

public class LiarSQLiteHelper extends SQLiteOpenHelper {
	
	private static final String TAG = "DATABASE";
	private static final String DATABASE_NAME = "liar_database.db";
	private static final int DATABASE_VERSION = 1;
	
	private Context context;

	
	public interface Tables {
		public final String PLAYERS = "players";
		public final String QUESTIONS = "questions";
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
		
		db.execSQL("CREATE TABLE " + Tables.QUESTIONS + " ("
			+ Questions.QUESTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ Questions.QUESTION + " TEXT,"
			+ "UNIQUE (" + Questions.QUESTION_ID + ") ON CONFLICT REPLACE);");
		
		initQuestions(db);
		
		//Testvalues		
		final ContentValues values = new ContentValues();
        values.put(Players.PLAYER_NAME, "Sophie");
        values.put(Players.PLAYER_POINTS, "50");
        db.insert(Tables.PLAYERS, null, values);
        
        final ContentValues values2 = new ContentValues();
        values2.put(Players.PLAYER_NAME, "Max");
        values2.put(Players.PLAYER_NAME, "100");
        db.insert(Tables.PLAYERS, null, values2);
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		database.execSQL("DROP TABLE IF EXISTS " +Tables.PLAYERS);
		database.execSQL("DROP TABLE IF EXISTS " +Tables.QUESTIONS);
		onCreate(database);
	}
	
	private void initQuestions(SQLiteDatabase db){
		String[] questions = context.getResources().getStringArray(R.array.default_questions);
		for (String question : questions) {
			final ContentValues value = new ContentValues();
			value.put(Questions.QUESTION, question);
			db.insert(Tables.QUESTIONS, null, value);
		}
	}

}
