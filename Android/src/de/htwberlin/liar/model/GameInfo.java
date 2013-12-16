package de.htwberlin.liar.model;

import java.io.Serializable;
import java.util.ArrayList;

public class GameInfo implements Serializable{
	
	public static final String TYPE = "GameInfo";
	
	private int rounds;
	private ArrayList<Player> players;
	
	public GameInfo(int rounds, ArrayList<Player> players) {
		this.rounds = rounds;
		this.players = players;
	}

	public int getRounds() {
		return rounds;
	}

	public ArrayList<Player> getPlayers() {
		return players;
	}

}
