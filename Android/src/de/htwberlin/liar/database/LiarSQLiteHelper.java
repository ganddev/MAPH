package de.htwberlin.liar.database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class LiarSQLiteHelper extends SQLiteOpenHelper {
	
	private static final String TAG = "DATABASE";
	private static final String DATABASE_NAME = "liar_database";
	private static final int DATABASE_VERSION = 6;
	
	private Context context;

	public LiarSQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;		
	}
	
	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(getSQL("create.sql"));
		
		ContentValues values = new ContentValues();
		values.put("name", "Sophie");
		values.put("points", "50");
		database.insert("players", null, values);
		
		ContentValues values2 = new ContentValues();
		values2.put("name", "Max");
		values2.put("points", "100");
		database.insert("players", null, values2);
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		database.execSQL(getSQL("delete.sql"));
		onCreate(database);
	}
	
	private String getSQL(String filename){
		BufferedReader reader = null;
		StringBuilder sql = new StringBuilder();
		try {
			InputStream inputStream = context.getResources().getAssets().open(filename);
			reader = new BufferedReader(new InputStreamReader(inputStream));	
			String line = reader.readLine();
			while (line != null) {
				sql.append(line).append("\n");
				line = reader.readLine();
			}
		} catch (IOException e) {
			Log.e(TAG, "Clound not read sql file.", e);
			throw new RuntimeException(e.getMessage());
		} finally {
			if(reader != null){
				try {
					reader.close();
				} catch (IOException e) {
					Log.e(TAG, "Clound not close reader.", e);
					throw new RuntimeException(e.getMessage());
				}
			}
		}
		
		return sql.toString();
	}

}
