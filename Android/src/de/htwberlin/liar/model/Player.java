package de.htwberlin.liar.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Player implements Parcelable, Comparable<Player>{

	private String name;
	private int points;
	
	public Player(String name) {
		this.name = name;
		points = 0;
	}
	
	private Player(Parcel in)
	{
		this.name = in.readString();
		this.points = in.readInt();
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

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeInt(points);
	}
	
	public static final Parcelable.Creator<Player> CREATOR = new Parcelable.Creator<Player>() {
        public Player createFromParcel(Parcel in) {
            return new Player(in); 
        }

        public Player[] newArray(int size) {
            return new Player[size];
        }
    };
}
