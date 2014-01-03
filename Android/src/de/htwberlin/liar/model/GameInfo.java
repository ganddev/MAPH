package de.htwberlin.liar.model;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class GameInfo implements Parcelable{
	
	public static final String TYPE = "GameInfo";
	
	private int rounds;
	private List<Player> players;
	
	public GameInfo(int rounds, List<Player> players) {
		this.rounds = rounds;
		this.players = players;
	}
	
	private GameInfo(Parcel in) {
		this.rounds = in.readInt();
		this.players = new ArrayList<Player>();
		in.readList(this.players, Player.class.getClassLoader());
	}

	public int getRounds() {
		return rounds;
	}

	public List<Player> getPlayers() {
		return players;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.rounds);
		dest.writeList(players);
	}

	public static final Parcelable.Creator<GameInfo> CREATOR = new Parcelable.Creator<GameInfo>() {
        public GameInfo createFromParcel(Parcel in) {
            return new GameInfo(in); 
        }

        public GameInfo[] newArray(int size) {
            return new GameInfo[size];
        }
    };
}
