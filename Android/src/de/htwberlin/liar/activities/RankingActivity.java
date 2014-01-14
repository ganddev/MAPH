package de.htwberlin.liar.activities;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import de.htwberlin.liar.R;
import de.htwberlin.liar.adapter.RankingAdapter;
import de.htwberlin.liar.database.LiarContract.Players;
import de.htwberlin.liar.utils.Constants;

public class RankingActivity extends ListActivity implements LoaderCallbacks<Cursor>{

	private RankingAdapter mAdapter;
	
	private String[] projection = { Players.PLAYER_ID,
			Players.PLAYER_NAME, Players.PLAYER_POINTS};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.default_list);

		String from[] = {Players.PLAYER_NAME,Players.PLAYER_POINTS};
		int[] to = {R.id.ranking_listview_playername,R.id.ranking_listview_playerpoints};
		mAdapter = new RankingAdapter(this, R.layout.ranking_listview_row, null, from, to, 0 );
		setListAdapter(mAdapter);
		LoaderManager lm = getLoaderManager();
		lm.initLoader(Constants.PLAYER_LOADER_ID, null, this);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		
		super.onPause();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		String selection = "";
		return new CursorLoader(getApplicationContext(), Players.CONTENT_URI, projection, selection, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		mAdapter.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		mAdapter.swapCursor(null);
	}

}
