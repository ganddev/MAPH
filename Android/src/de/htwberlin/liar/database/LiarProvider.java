package de.htwberlin.liar.database;

import de.htwberlin.liar.database.LiarContract.Players;
import de.htwberlin.liar.database.LiarContract.Questions;
import de.htwberlin.liar.database.LiarSQLiteHelper.Tables;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.widget.SlidingDrawer;

public class LiarProvider extends ContentProvider {

	private static final int PLAYERS = 100;
	private static final int PLAYERS_ID = 101;
	private static final int QUESTIONS = 200;
	private static final int QUESTIONS_ID = 201;
	private LiarSQLiteHelper mOpenHelpener;
	
	private static final UriMatcher sUriMatcher = buildUriMatcher();
	
	private static UriMatcher buildUriMatcher() {
		final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
		final String authority = LiarContract.CONTENT_AUTHORITY;
		matcher.addURI(authority, "players", PLAYERS);
		matcher.addURI(authority, "locations/*", PLAYERS_ID);
		matcher.addURI(authority, "questions", QUESTIONS);
		matcher.addURI(authority, "locations/*", QUESTIONS_ID);
		
		return matcher;
	}
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		final SQLiteDatabase database = mOpenHelpener.getWritableDatabase();
		int rows = -1;
		switch (sUriMatcher.match(uri)) {
		case PLAYERS:
			rows = database.delete(Tables.PLAYERS, selection, selectionArgs);
			break;
		case QUESTIONS:
			rows = database.delete(Tables.QUESTIONS, selection, selectionArgs);
			break;
		default:
			throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		final int match = sUriMatcher.match(uri);
		switch (match) {
		case PLAYERS:
			return Players.CONTENT_TYPE;
		case PLAYERS_ID:
			return Players.CONTENT_ITEM_TYPE;
		case QUESTIONS:
			return Questions.CONTENT_TYPE;
		case QUESTIONS_ID:
			return Questions.CONTENT_ITEM_TYPE;
		default:
			throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		final SQLiteDatabase database = mOpenHelpener.getWritableDatabase();
		long id = -1;
		final String nullColumnHack = null;
		Uri contentUri = null;
		switch (sUriMatcher.match(uri)) {
		case PLAYERS:
			id = database.insert(Tables.PLAYERS, nullColumnHack, values);
			contentUri = Players.CONTENT_URI;
			break;
		case QUESTIONS:
			id = database.insert(Tables.QUESTIONS, nullColumnHack, values);
			contentUri = Questions.CONTENT_URI;
			break;
		default:
			throw new UnsupportedOperationException("Unknown uri: " +uri);
		}
		if (id > -1) {
			Uri insertedId = ContentUris.withAppendedId(contentUri, id);
			getContext().getContentResolver().notifyChange(insertedId, null);
			return insertedId;
		} else {
			return null;
		}
	}

	@Override
	public boolean onCreate() {
		mOpenHelpener = new LiarSQLiteHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteDatabase database = mOpenHelpener.getReadableDatabase();
		final SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		String having = null;
		String groupBy = null;
		switch (sUriMatcher.match(uri)) {
		case PLAYERS:
			builder.setTables(Tables.PLAYERS);
			break;
		case QUESTIONS:
			builder.setTables(Tables.QUESTIONS);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		Cursor c = builder.query(database, projection, selection,
				selectionArgs, groupBy, having, sortOrder);
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public int update(Uri arg0, ContentValues arg1, String arg2, String[] arg3) {
		// TODO Auto-generated method stub
		return 0;
	}

}
