package de.htwberlin.liar.activities;


import android.app.ListActivity;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import de.htwberlin.liar.R;
import de.htwberlin.liar.adapter.RankingAdapter;
import de.htwberlin.liar.database.LiarContract.Players;
import de.htwberlin.liar.utils.Constants;

public class ScoreActivity extends ListActivity implements LoaderCallbacks<Cursor> {

	
	private RankingAdapter mAdapter;
	
	private String[] projection = { 
			Players.PLAYER_ID,
			Players.PLAYER_NAME, Players.PLAYER_POINTS
			};
	
	private static final String from[] = {Players.PLAYER_NAME,Players.PLAYER_POINTS};
	private static final int[] to = {R.id.ranking_listview_playername,R.id.ranking_listview_playerpoints};
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_score);
		
		mAdapter = new RankingAdapter(this,  R.layout.ranking_listview_row, null, from, to, 0 );
		setListAdapter(mAdapter);
		final Bundle extras = getIntent().getExtras();
		
		
		final LoaderManager lm = getLoaderManager();
		lm.initLoader(Constants.SCORE_LOADER, extras, this);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		final String selection = Players.PLAYER_GAME_ID +"=?";
		final String sortOrder = null;
		final String[] selectionArgs = {args.getString(Constants.GAME_ID)};
		return new CursorLoader(this, Players.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
		mAdapter.swapCursor(c);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		
	}
}
