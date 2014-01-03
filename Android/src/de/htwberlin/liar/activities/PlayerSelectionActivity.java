package de.htwberlin.liar.activities;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Toast;
import de.htwberlin.liar.R;
import de.htwberlin.liar.adapter.PlayerAdapter;
import de.htwberlin.liar.model.GameInfo;
import de.htwberlin.liar.model.Player;
import de.htwberlin.liar.utils.DialogUtil;

public class PlayerSelectionActivity extends LiarActivity {
	
	private PlayerAdapter playerAdapter;
	private EditText playerNameInput;
	private NumberPicker picker;
	
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
    	setUpStartGameButton();
    }
    
    private void setUpList(){
    	playerAdapter = new PlayerAdapter(this, new ArrayList<Player>());
    	ListView playerListView = (ListView) findViewById(R.id.player_selection_player_list);
    	playerListView.setAdapter(playerAdapter);
    	
    }
    
    private void setUpAddingPlayers(){
    	playerNameInput = (EditText) findViewById(R.id.player_selection_player_name_field);
    	View addButton = findViewById(R.id.player_selection_add_player_button);
    	addButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				addPlayerAction(playerNameInput.getText().toString());
			}
		});
    }
    
    private void setUpNumberPicker(){
    	picker = (NumberPicker) findViewById(R.id.player_selection_rounds_picker);
        picker.setMinValue(getInteger(R.integer.min_rounds));
        picker.setMaxValue(getInteger(R.integer.max_rounds));
        picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        picker.setWrapSelectorWheel(false);
    }
    
    private void setUpStartGameButton(){
    	View startGameButton = findViewById(R.id.player_selection_start_game_button);
    	startGameButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (playerAdapter.getPlayers().size() < getInteger(R.integer.min_palyers)) {
					String message = String.format(getString(R.string.not_enough_player_message), getInteger(R.integer.min_palyers));
					DialogUtil.showMessageDialog(PlayerSelectionActivity.this, message);					
				} else {
					final Intent intent = new Intent(PlayerSelectionActivity.this, GameActivity.class);
					intent.putExtra(GameInfo.TYPE, new GameInfo(picker.getValue(), playerAdapter.getPlayers()));
					startActivity(intent);
				}
			}
		});
    }
    
    private void addPlayerAction(String name){
    	if(!name.equals(getString(R.string.empty))){
    		Player player = new Player(name);
        	if (!playerAdapter.getPlayers().contains(player)) {
        		playerAdapter.add(player);
    		} else {
    			String message = String.format(getString(R.string.player_already_exists), player.getName());
    			DialogUtil.showMessageDialog(this, message);
    		}
    	} else {
    		DialogUtil.showMessageDialog(this, R.string.no_player_input);
    	}
    	
    	
    }

}
