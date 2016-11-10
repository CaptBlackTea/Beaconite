package com.example.deas.beaconite;

import android.util.Log;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by deas on 10/11/16.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({Log.class, FingerprintMedian.BeaconFingerPrint.class})
public class BeaconFingerPrintTest {

	FingerprintMedian.BeaconFingerPrint beaconFP;
	private List<Integer> rssiList = fillRssiList();

	@Before
	public void setUp() throws Exception {
		PowerMockito.mockStatic(Log.class);

		beaconFP = new FingerprintMedian.BeaconFingerPrint(rssiList);
	}

	// Rssi values should be between -25 and -100;
	// 0 means it is invisible
	private List<Integer> fillRssiList() {
		List<Integer> tmpRssiList = new ArrayList<>();

		tmpRssiList.add(0);

		for (int i = -100; i <= -25; i++) {
			tmpRssiList.add(i);
		}

		System.out.println(tmpRssiList);

		return tmpRssiList;
	}

	@After
	public void tearDown() throws Exception {

	}

	@Test
	public void calcCorridor() {

	}

	@Test
	public void calcMedian() {

	}

	@Test
	public void isCovered() throws Exception {

	}

}