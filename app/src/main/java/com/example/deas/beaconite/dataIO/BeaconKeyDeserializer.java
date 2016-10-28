package com.example.deas.beaconite.dataIO;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;

import org.altbeacon.beacon.Beacon;

import java.io.IOException;
import java.io.Serializable;

/**
 * Created by deas on 28/10/16.
 */

public class BeaconKeyDeserializer extends KeyDeserializer implements Serializable {
	@Override
	public Object deserializeKey(String key, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		return constructBeacon(key);
	}

	private Beacon constructBeacon(String key) {
		//Beacon for toString looks like this:
		// "id1: df7e1c79-43e9-44ff-886f-100000000003 id2: 3 id3: 1"

		String[] ids = key.split(" ");

		// HINT: constructs a Beacon without txPower -> if needed do not forget to store it in
		// the JSON, because currently JSON only stores the ids!
		return new Beacon.Builder().setId1(ids[1])
				.setId2(ids[3]).setId3(ids[5]).build();
	}
}
