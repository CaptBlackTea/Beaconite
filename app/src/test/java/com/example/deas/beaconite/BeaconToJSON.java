package com.example.deas.beaconite;

import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.altbeacon.beacon.Beacon;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({Log.class})
public class BeaconToJSON {

	Beacon beacon = new Beacon.Builder().setId1("DF7E1C79-43E9-44FF-886F-" + Long.toHexString
			(1 + 0x100000000000L))
			.setId2("" + 1).setId3("1").setTxPower(-55).build();

	@Before
	public void setUp() throws Exception {
		PowerMockito.mockStatic(Log.class);

	}

	@Test
	public void beaconToJson() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
		System.out.println(mapper.writeValueAsString(beacon));
	}
}