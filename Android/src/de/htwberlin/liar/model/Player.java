package de.htwberlin.liar.model;

import java.io.Serializable;

public class Player implements Serializable, Comparable<Player>{

	private String name;
	private int points;
	
	public Player(String name) {
		this.name = name;
		points = 0;
	}

	public String getName() {
		return name;
	}
	
	public int getPoints() {
		return points;
	}

	public void addPoints(int points) {
		this.points = this.points + points;
	}
	
	public void subtractPoints(int points) {
		this.points = this.points - points;
	}

	@Override
	public int compareTo(Player anotherPlayer) {
		return this.getPoints() - anotherPlayer.getPoints();
	}
	
	@Override
	public boolean equals(Object object) {
		boolean result = false;
		if (object instanceof Player) {
			Player player = (Player) object;
			if (player.getName().equals(this.getName())) {
				result = true;
			}
		}
		return result;
	}	
}
