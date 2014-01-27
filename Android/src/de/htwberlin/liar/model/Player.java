package de.htwberlin.liar.model;

import java.util.UUID;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;
import de.htwberlin.liar.database.LiarContract.Players;

public class Player implements Parcelable, Comparable<Player>{

	private String name;
	private int points;
	
	private UUID gameId;
	
	public Player(final String name, final UUID gameId) {
		this.name = name;
		this.points = 0;
		this.gameId = gameId;
	}
	
	private Player(Parcel in)
	{
		this.name = in.readString();
		this.points = in.readInt();
		this.gameId = UUID.fromString(in.readString());
	}

	public String getName() {
		return name;
	}
	
	public int getPoints() {
		return points;
	}
	
	public UUID getGameId(){
		return this.gameId;
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

	public ContentValues toContentValues() {
		final ContentValues item = new ContentValues();
		item.put(Players.PLAYER_NAME, this.name);
		item.put(Players.PLAYER_POINTS, this.points);
		item.put(Players.PLAYER_GAME_ID, this.gameId.toString());
		return item;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeInt(points);
		dest.writeString(this.gameId.toString());
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
