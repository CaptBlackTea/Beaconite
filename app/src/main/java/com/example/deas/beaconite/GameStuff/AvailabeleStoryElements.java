package com.example.deas.beaconite.GameStuff;

import com.example.deas.beaconite.activities.StoryElements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by deas on 17/02/17.
 */

public class AvailabeleStoryElements implements StoryElements {
	private Map<String, String> dangerProtectionPairs;
	private List<String> treasures;

	public AvailabeleStoryElements() {
		dangerProtectionPairs = new HashMap<>();
		treasures = new ArrayList<>();
	}

	public AvailabeleStoryElements(Map<String, String> dangerProtectionPairs, List<String>
			treasures) {
		this.dangerProtectionPairs = dangerProtectionPairs;
		this.treasures = treasures;
	}

	public boolean addDangerProtectionPairs(String dangerName, String protectionName) throws IllegalArgumentException {

		if (dangerProtectionPairs.containsKey(dangerName)) {
			throw new IllegalArgumentException(String.format("The Danger - %s - already exists!",
					dangerName));
		} else if (dangerProtectionPairs.containsValue(protectionName)) {
			throw new IllegalArgumentException(String.format("The Protection - %s - already " +
					"exists!", protectionName));
		}

		this.dangerProtectionPairs.put(dangerName, protectionName);

		return true;


	}

	public Map<String, String> getDangerProtectionPairs() {
		return dangerProtectionPairs;
	}

	public void setDangerProtectionPairs(Map<String, String> dangerProtectionPairs) {
		this.dangerProtectionPairs = dangerProtectionPairs;
	}

	@Override
	public List<String> getDangerElements() {
		List<String> dangers = new ArrayList<>();
		dangers.addAll(dangerProtectionPairs.keySet());
		return dangers;
	}

	@Override
	public List<String> getProtectionElements() {
		ArrayList<String> protections = new ArrayList<>();
		protections.addAll(dangerProtectionPairs.values());
		return protections;
	}

	@Override
	public List<String> getTreasures() {
		return treasures;
	}

	public void setTreasures(List<String> treasures) {
		if (treasures != null) {
			this.treasures = treasures;
		}
	}

	@Override
	public List<String> getStoryCardsElements() {
		return null;
	}

	public String makeTreasuresPrettyList() {
		if (treasures == null) {
			return "no treasures";
		}

		return treasures.toString().substring(1, treasures.toString().length() - 1);
	}

	public void addTreasure(String treasure) throws IllegalArgumentException {
		if (!treasures.contains(treasure)) {
			treasures.add(treasure);
		} else {
			throw new IllegalArgumentException(String.format("The Treasure - %s - already " +
					"exists!", treasure));
		}
	}

	@Override
	public String toString() {
		return "AvailabeleStoryElements{" +
				"dangerProtectionPairs=" + dangerProtectionPairs +
				", treasures=" + treasures +
				'}';
	}
}
