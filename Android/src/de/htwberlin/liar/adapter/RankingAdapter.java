package de.htwberlin.liar.adapter;

import de.htwberlin.liar.R;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class RankingAdapter extends CursorAdapter {

	public RankingAdapter(Context context, Cursor cursor) {
		super(context, cursor, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
	}
	
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		Log.i("blah", "aufgerufen");
		TextView name = (TextView) view.findViewById(R.id.ranking_listview_playername);
		TextView points = (TextView) view.findViewById(R.id.ranking_listview_playerpoints);
		name.setText(cursor.getString(cursor.getColumnIndexOrThrow("name")));
		points.setText(cursor.getString(cursor.getColumnIndexOrThrow("points")));

	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
		LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
		View view = inflater.inflate(R.layout.ranking_listview_row, viewGroup, false);
		return view;
	}

}
