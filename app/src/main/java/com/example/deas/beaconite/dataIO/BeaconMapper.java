package com.example.deas.beaconite.dataIO;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Created by deas on 28/10/16.
 */

public class BeaconMapper extends ObjectMapper {

	public BeaconMapper() {
		registerModule(new BeaconModule());
		configure(SerializationFeature.INDENT_OUTPUT, true);
	}
}
