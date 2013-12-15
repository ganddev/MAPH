package de.htwberlin.liar.activities;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import de.htwberlin.liar.R;
import de.htwberlin.liar.adapeter.PlayerAdapter;
import de.htwberlin.liar.model.Player;

public class PlayerSelectionActivity extends LiarActivity {
	
	private PlayerAdapter playerAdapter;
	private ArrayList<Player> playerList;
	private EditText playerNameInput;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_selection_layout);
        setUp();
        
    }
    
    private void setUp(){
    	setUpList();
    	setUpAddingPlayers();
    	setUpNumberPicker();
    }
    
    private void setUpList(){
    	playerList = new ArrayList<Player>();
    	playerAdapter = new PlayerAdapter(this, playerList);
    	playerAdapter.setOnDeleteButtonClickListener(new PlayerAdapter.OnDeleteButtonClickListener() {
			
			@Override
			public void onClick(int position) {
				playerList.remove(position);
				playerAdapter.notifyDataSetChanged();
			}
		});
    	ListView playerListView = (ListView) findViewById(R.id.player_selection_player_list);
    	ViewGroup header = (ViewGroup) getLayoutInflater().inflate(R.layout.list_text_header, playerListView, false);
    	TextView text = (TextView) header.findViewById(R.id.list_text_header_text);
    	text.setText(getString(R.string.player));
    	playerListView.addHeaderView(header);
    	playerListView.setAdapter(playerAdapter);
    }
    
    private void setUpAddingPlayers(){
    	playerNameInput = (EditText) findViewById(R.id.player_selection_player_name_field);
    	View addButton = findViewById(R.id.player_selection_add_player_button);
    	addButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				playerList.add(new Player(playerNameInput.getText().toString()));
				playerAdapter.notifyDataSetChanged();
			}
		});
    }
    
    private void setUpNumberPicker(){
    	NumberPicker picker = (NumberPicker) findViewById(R.id.player_selection_rounds_picker);
        picker.setMinValue(getInteger(R.integer.min_rounds));
        picker.setMaxValue(getInteger(R.integer.max_rounds));
        picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        picker.setWrapSelectorWheel(false);
    }

}
