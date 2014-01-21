package de.htwberlin.liar.activities;


import android.app.ListActivity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import de.htwberlin.liar.R;

public class ScoreActivity extends ListActivity implements LoaderCallbacks<Cursor> {

	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_score);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		// TODO return a new cursorloader...
		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
		// swap cursor for adapter...
		
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// swap cursor in adapter to null...
		
	}
}
