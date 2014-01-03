package de.htwberlin.liar.model;

import java.io.Serializable;
import java.util.List;

public class GameInfo implements Serializable{
	
	public static final String TYPE = "GameInfo";
	
	private int rounds;
	private List<Player> players;
	
	public GameInfo(int rounds, List<Player> players) {
		this.rounds = rounds;
		this.players = players;
	}

	public int getRounds() {
		return rounds;
	}

	public List<Player> getPlayers() {
		return players;
	}

}
