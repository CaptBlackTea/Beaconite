package com.example.deas.beaconite;

import android.util.Log;

import com.example.deas.beaconite.dataIO.BeaconMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import junit.framework.Assert;

import org.altbeacon.beacon.AltBeacon;
import org.altbeacon.beacon.Beacon;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by deas on 12/10/16.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({Log.class})
public class FileSupervisorTest {

	private final ObjectMapper jsonMapper = new BeaconMapper();
	private FileSupervisor fileSupervisor;
	private File jsonFile = new File("/home/deas/Development/Beaconite/app/src/test/res/allCaches.json");
	private File jsonFileExtendedInfo = new File
			("/home/deas/Development/Beaconite/app/src/test/res" +
					"/allCaches_extendedInformation.json");
	private File emptyFile;

	// TODO: better mocking or is real Data better
	private List<Cache> createListWithCaches() {
		List<Cache> cacheList = new ArrayList<>();

		// generate 4 Caches
		for (int cacheNumber = 0; cacheNumber < 4; cacheNumber++) {
			Cache cache = new Cache("Cache" + cacheNumber);


			// each cache gets 2 time intervals
			for (int timeIntervalNumber = 0; timeIntervalNumber < 2; timeIntervalNumber++) {
				cache.addNewTimeInterval(new TimeInterval(1475583764019L + cacheNumber, 1475583766547L + cacheNumber));
			}

			List<Beacon> beaconList = beaconBuilder(4);
			BeaconMap beaconMap = new BeaconMap();

			for (Beacon b : beaconList) {
				SortedMap timestampRssiMap = new TreeMap<Long, Integer>();
				timestampRssiMap.put(1L, 1);
				beaconMap.put(b, timestampRssiMap);
			}

			cache.calculateFingerprint(beaconMap);

			cacheList.add(cache);

		}


		return cacheList;
	}

	private List<Beacon> beaconBuilder(int number) {
		List<Beacon> beaconsList = new ArrayList<Beacon>();

		for (int i = 1; i <= number; i++) {
			beaconsList.add(new AltBeacon.Builder().setId1("DF7E1C79-43E9-44FF-886F-" + Long.toHexString(i + 0x100000000000L))
					.setId2("" + i).setId3("1").setTxPower(-55).build());
		}

		return beaconsList;
	}


	@Before
	public void setUp() throws Exception {
		PowerMockito.mockStatic(Log.class);
		jsonMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
//		jsonMapper.enableDefaultTyping();

		emptyFile = new File("/home/deas/Development/Beaconite/app/src/test/res" +
				"/emptyFile.json");
	}

	@After
	public void tearDown() throws Exception {

		//TODO: use again! but now the output is needed.... m)
//		emptyFile.delete();
	}

	@Test
	public void createFileSupervisor() {
		FileSupervisor fileSup = new FileSupervisor(emptyFile);
		Assert.assertNotNull(fileSup.getFile());
		Assert.assertNotNull(fileSup.getJsonMapper());
	}

	@Test
	public void writeAllCachesInFile() throws Exception {

		List<Cache> cacheList = createListWithCaches();
		FileSupervisor fileSupEmptyFile = new FileSupervisor(emptyFile);
		fileSupEmptyFile.writeAllCachesInFile(cacheList);

		String expectedJson = jsonMapper.writerFor(new TypeReference<List<Cache>>() {
		}).writeValueAsString(cacheList);
		String actualJson = fileSupEmptyFile.getJsonString();

		Assert.assertEquals("File contains not the expected data.", expectedJson, actualJson);
	}

	@Test
	public void loadCachesFromFile() throws Exception {
//		fileSupervisor = new FileSupervisor(jsonFile);
		fileSupervisor = new FileSupervisor(emptyFile);
		List<Cache> expectedCacheList = createListWithCaches();

		List<Cache> actualCacheList = fileSupervisor.loadCachesFromFile();

		Assert.assertEquals("List contains not the expected Cache data.", expectedCacheList, actualCacheList);
	}

}