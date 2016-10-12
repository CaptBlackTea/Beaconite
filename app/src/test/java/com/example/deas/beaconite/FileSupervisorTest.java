package com.example.deas.beaconite;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

/**
 * Created by deas on 12/10/16.
 */
public class FileSupervisorTest {

	private final ObjectMapper jsonMapper = new ObjectMapper();
	private File jsonFile = new File("/home/deas/Development/Beaconite/app/src/test/res/allCaches.json");
	private FileSupervisor fileSupervisor;

	@Before
	public void setUp() throws Exception {
		fileSupervisor = new FileSupervisor(jsonFile);
	}

	@After
	public void tearDown() throws Exception {

	}

	@Test
	public void createFileSupervisor() {

		FileSupervisor fileSup = new FileSupervisor(jsonFile);
	}

	@Test
	public void writeAllCachesInFile() throws Exception {

	}

	@Test
	public void loadCachesFromFile() throws Exception {

	}

}