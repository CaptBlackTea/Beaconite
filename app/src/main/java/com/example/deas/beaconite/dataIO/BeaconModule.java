package com.example.deas.beaconite.dataIO;

import com.fasterxml.jackson.databind.module.SimpleModule;

import org.altbeacon.beacon.Beacon;

/**
 * Created by deas on 28/10/16.
 */

public class BeaconModule extends SimpleModule {

	public BeaconModule() {
		super();
		addKeyDeserializer(Beacon.class, new BeaconKeyDeserializer());
	}
}
