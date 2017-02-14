package com.example.deas.beaconite.GameStuff;

import com.example.deas.beaconite.graphStuff.BeaconiteVertex;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the player of the Beaconite game.
 * <p>
 * Created by dea on 09.02.2017.
 */
public class Player {
	// tokens that are currently owned by the player
	private final List<BeaconiteVertex> tokens;

	public Player() {
		tokens = new ArrayList<>();
	}

	public List<BeaconiteVertex> getTokens() {
		return tokens;
	}

	public void fillTokenlist(List<BeaconiteVertex> tokens) {
		this.tokens.clear();
		this.tokens.addAll(tokens);
	}

	public void removeToken(BeaconiteVertex token) {
		this.tokens.remove(token);
	}

	public boolean hasToken(BeaconiteVertex token) {
		return tokens.contains(token);
	}
}
