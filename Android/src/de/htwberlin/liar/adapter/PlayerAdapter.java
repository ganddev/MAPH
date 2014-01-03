package de.htwberlin.liar.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import de.htwberlin.liar.R;
import de.htwberlin.liar.model.Player;

public class PlayerAdapter extends ArrayAdapter<Player> {

	private Context context;
	private ArrayList<Player> playerList; 
	private List<OnDeleteButtonClickListener> listeners;
	
	public interface OnDeleteButtonClickListener {
		
		public void onClick(int position);
		
	}
	
	public PlayerAdapter(Context context, final ArrayList<Player> playerList) {
		super(context, R.layout.player_list_row_layout, playerList);
		this.context = context;
		this.playerList = playerList;
		this.listeners = new ArrayList<OnDeleteButtonClickListener>();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rootView = inflater.inflate(R.layout.player_list_row_layout, parent, false);
		TextView nameLabel = (TextView) rootView.findViewById(R.id.player_list_row_player_label);
		nameLabel.setText(playerList.get(position).getName());
		View deleteButton = rootView.findViewById(R.id.player_list_row_delete_button);
		deleteButton.setTag(position);
		deleteButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				for (OnDeleteButtonClickListener listern : listeners) {
					listern.onClick((Integer) view.getTag());
				}
			}
		});
		
		
		return rootView;
	}
	
	public void setOnDeleteButtonClickListener(OnDeleteButtonClickListener listener) {
		listeners.add(listener);
	}
	
}
