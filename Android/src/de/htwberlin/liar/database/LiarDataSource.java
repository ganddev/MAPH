package de.htwberlin.liar.database;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class LiarDataSource {
	
	private SQLiteDatabase database;
	private LiarSQLiteHelper sqlHelper;
	
	public LiarDataSource(Context context) {
		this.sqlHelper = new LiarSQLiteHelper(context);
	}
	
	public void open() throws SQLException {
		this.database = sqlHelper.getWritableDatabase();
	}
	
	public void close() {
		sqlHelper.close();
	}
	
	public Cursor getPlayersCursor(){
		return database.query(
				"players", 
				new String[] {"player_id", "name", "points"},
				null,
				null,
				null,
				null,
				"points DESC"
				);
	}
}
