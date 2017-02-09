package com.example.deas.beaconite.GameStuff;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the player of the Beaconite game.
 * <p>
 * Created by dea on 09.02.2017.
 */
public class Player {
	// tokens that are currently owned by the player
	private final List<String> tokens;

	public Player() {
		tokens = new ArrayList<>();
	}

	public void fillTokenlist(List<String> tokens) {
		this.tokens.addAll(tokens);
	}

	public void removeToken(String token) {
		this.tokens.remove(token);
	}

	public boolean hasToken(String token) {
		return tokens.contains(token);
	}
}
