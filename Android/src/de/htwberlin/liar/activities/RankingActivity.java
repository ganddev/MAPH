package de.htwberlin.liar.activities;

import de.htwberlin.liar.R;
import de.htwberlin.liar.adapter.RankingAdapter;
import de.htwberlin.liar.database.LiarDataSource;
import android.app.ListActivity;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;

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
		RankingAdapter adapter = new RankingAdapter(this, playsers);
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
