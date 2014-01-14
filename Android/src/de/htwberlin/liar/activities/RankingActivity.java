package de.htwberlin.liar.activities;

import android.app.ListActivity;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import de.htwberlin.liar.R;
import de.htwberlin.liar.adapter.RankingAdapter;
import de.htwberlin.liar.database.LiarDataSource;
import de.htwberlin.liar.utils.Constants;

public class RankingActivity extends ListActivity {

	private LiarDataSource datasource;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.default_list);
		datasource = new LiarDataSource(this);
		datasource.open();
		Cursor playsers = datasource.getPlayersCursor();
		
		String from[] = {Constants.COLUMN_NAME,Constants.COLUMN__POINTS};
		int[] to = {R.id.ranking_listview_playername,R.id.ranking_listview_playerpoints};
		RankingAdapter adapter = new RankingAdapter(this, R.layout.ranking_listview_row, playsers, from, to, 0 );
		setListAdapter(adapter);
	}

	@Override
	protected void onResume() {
		datasource.open();
		super.onResume();
	}

	@Override
	protected void onPause() {
		datasource.close();
		super.onPause();
	}

}
