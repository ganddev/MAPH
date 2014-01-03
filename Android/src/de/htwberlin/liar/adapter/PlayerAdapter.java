package de.htwberlin.liar.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import de.htwberlin.liar.R;
import de.htwberlin.liar.model.Player;

public class PlayerAdapter extends ArrayAdapter<Player> {

	private Context context;
	private ArrayList<Player> players;
	
	public PlayerAdapter(Context context, final ArrayList<Player> playerList) {
		super(context, R.layout.player_list_row_layout, playerList);
		this.context = context;
		this.players = playerList;
		
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		PlayerViewHolder vh;
		if(convertView == null || convertView.getTag() == null)
		{
			vh = new PlayerViewHolder();
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.player_list_row_layout, parent, false);
			vh.playerNameTV = (TextView) convertView.findViewById(R.id.player_list_row_player_label);
			vh.deleteBtn = (ImageButton) convertView.findViewById(R.id.player_list_row_delete_button);
			convertView.setTag(vh); 
		}
		final Player item = getItem(position);
		vh = (PlayerViewHolder) convertView.getTag();
		vh.playerNameTV.setText(item.getName());
		
		vh.deleteBtn.setTag(position);
		vh.deleteBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				final int pos = (Integer) view.getTag();
				players.remove(pos);
				notifyDataSetChanged();
			}
		});
		
		convertView.setTag(vh);
		return convertView;
	}
	
	public void addPlayer(final Player item)
	{
		players.add(item);
		notifyDataSetChanged();
	}
	
	public List<Player> getPlayers()
	{
		return players;
	}
	
	/**
	 * Speed up a listview. Avoid reading XML files.
	 * @author bjornahlfeld
	 *
	 */
	public class PlayerViewHolder{
		ImageButton deleteBtn;
		TextView playerNameTV;
	}
}
