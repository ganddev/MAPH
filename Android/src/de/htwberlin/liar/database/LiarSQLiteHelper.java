package de.htwberlin.liar.database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class LiarSQLiteHelper extends SQLiteOpenHelper {
	
	private static final String TAG = "DATABASE";
	private static final String DATABASE_NAME = "liar_database";
	private static final int DATABASE_VERSION= 1;
	
	private Context context;

	public LiarSQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;		
	}
	
	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(getSQL("create.sql"));
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		database.execSQL(getSQL("delete.sql"));
		onCreate(database);
	}
	
	private String getSQL(String filename){
		InputStream inputStream = null;
		StringBuilder sql = new StringBuilder();
		try {
			inputStream = context.getResources().getAssets().open("delete.sql");
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));	
			String line = reader.readLine();
			while (line != null) {
				sql.append(line).append("\n");
				line = reader.readLine();
			}
		} catch (IOException e) {
			Log.e(TAG, "Clound not read sql file.", e);
			throw new RuntimeException(e.getMessage());
		}
		
		return sql.toString();
	}

}
