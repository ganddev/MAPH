package de.htwberlin.liar.adapter;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import de.htwberlin.liar.R;
import de.htwberlin.liar.database.LiarContract.Players;
import de.htwberlin.liar.utils.Constants;

public class RankingAdapter extends SimpleCursorAdapter {

	Context mContext;
	
	public RankingAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to, int flags) {
		super(context, layout, c, from, to, flags);
		mContext = context;
	}
	
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {	
		if (convertView == null || convertView.getTag() == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.ranking_listview_row, null);
			RankingViewHolder holder = new RankingViewHolder();
			holder.name = (TextView) convertView
					.findViewById(R.id.ranking_listview_playername);
			holder.points = (TextView) convertView.findViewById(R.id.ranking_listview_playerpoints);
			convertView.setTag(holder);
		}
		RankingViewHolder holder = (RankingViewHolder) convertView.getTag();
		final Cursor c = getCursor();
		c.moveToPosition(position);
		final int columnIndexForName = c.getColumnIndexOrThrow(Players.PLAYER_NAME);
		final int columnIndexForPoints = c.getColumnIndexOrThrow(Players.PLAYER_POINTS);
		holder.name.setText(c.getString(columnIndexForName));
		holder.points.setText(c.getString(columnIndexForPoints));
		convertView.setTag(holder);
		
		return convertView;

	}
	

	
	public class RankingViewHolder{
		public TextView name, points;
	}
}
