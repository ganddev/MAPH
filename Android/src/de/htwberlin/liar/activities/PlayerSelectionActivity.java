package de.htwberlin.liar.activities;

import java.util.ArrayList;
import java.util.UUID;

import android.app.ActionBar;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import de.htwberlin.liar.R;
import de.htwberlin.liar.adapter.PlayerAdapter;
import de.htwberlin.liar.database.LiarContract.Players;
import de.htwberlin.liar.model.GameInfo;
import de.htwberlin.liar.model.Player;
import de.htwberlin.liar.utils.Constants;
import de.htwberlin.liar.utils.DialogUtil;

/**
 * Handels the screen to select the players for an game.
 */
public class PlayerSelectionActivity extends LiarActivity {
	
	/**An Adapter for the list of players*/
	private PlayerAdapter playerAdapter;
	/**Input field to add a player*/
	private EditText playerNameInput;
	/**The Picker for the number of questions*/
	private NumberPicker picker;
	

	private static final UUID GAME_ID = UUID.randomUUID();
	
	/** Called when the activity is first created. */

	/** 
	 * Sets the {@link View} elements including the Action bar.
	 */

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_selection_layout);
        final ActionBar ab = getActionBar();
        ab.setTitle(getString(R.string.add_players));
        setUp();
        
    }
    
    /**
     * Sets up the {@link ListView}, the add player views, the {@link NumberPicker} and the 
     * button to start the game.
     */
    private void setUp(){
    	setUpList();
    	setUpAddingPlayers();
    	setUpNumberPicker();
    	setUpStartGameButton();
    }
    
    /**
     * Adds the adapter to the {@link ListView}.
     */
    private void setUpList(){
    	playerAdapter = new PlayerAdapter(this, new ArrayList<Player>());
    	ListView playerListView = (ListView) findViewById(R.id.player_selection_player_list);
    	playerListView.setAdapter(playerAdapter);
    	
    }
    
    /**
     * Adds button an input field to add a player.
     */
    private void setUpAddingPlayers(){
    	playerNameInput = (EditText) findViewById(R.id.player_selection_player_name_field);
    	View addButton = findViewById(R.id.player_selection_add_player_button);
    	addButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				addPlayerAction(playerNameInput.getText().toString(), GAME_ID);
				playerNameInput.setText("");
			}
		});
    }
    
    /**
     * Sets up teh {@link NumberPicker}.
     */
    private void setUpNumberPicker(){
    	picker = (NumberPicker) findViewById(R.id.player_selection_rounds_picker);
        picker.setMinValue(getInteger(R.integer.min_rounds));
        picker.setMaxValue(getInteger(R.integer.max_rounds));
        picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        picker.setWrapSelectorWheel(false);
    }
    
    /**
     * Sets up the Button to start the game. An error dialog is shown,
     * if not enough player were created.
     */
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
					intent.putExtra(Constants.GAME_ID, GAME_ID.toString());
					intent.putExtra(GameInfo.TYPE, new GameInfo(picker.getValue(), playerAdapter.getPlayers()));
					startActivity(intent);
				}
			}
		});
    }
    
    
    /**
     * Adds the player if not already exits. If exits shows an error dialog.
     * 
     * @param name
     * @param gameId
     */
    private void addPlayerAction(String name, UUID gameId){
    	if(!name.equals(getString(R.string.empty))){
    		Player player = new Player(name, gameId);
        	if (!playerAdapter.getPlayers().contains(player)) {
        		playerAdapter.add(player);
        		ContentResolver cr = getContentResolver();
        		cr.insert(Players.CONTENT_URI, player.toContentValues());
    		} else {
    			String message = String.format(getString(R.string.player_already_exists), player.getName());
    			DialogUtil.showMessageDialog(this, message);
    		}
    	} else {
    		DialogUtil.showMessageDialog(this, R.string.no_player_input);
    	}
    	
    	
    }

}
