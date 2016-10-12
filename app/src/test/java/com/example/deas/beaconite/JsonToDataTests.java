package com.example.deas.beaconite;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * Tutorial: http://www.makeinjava.com/convert-list-objects-to-from-json-using-jackson-objectmapper/
 * Created by deas on 05/10/16.
 */
public class JsonToDataTests {


	private ObjectMapper mapper;
	private List<Cache> cacheList;


	//###########################################

	@Before
	public void setUp() throws Exception {
		mapper = new ObjectMapper();
		//Set pretty printing of json
		mapper.enable(SerializationFeature.INDENT_OUTPUT);

	}

	@Test
	public void listCreationTest() throws Exception {

	}


	@Test
	public void serializeToJSONTest() throws Exception {

		//1. Convert List of Cache objects to JSON
		String arrayToJson = mapper.writeValueAsString(cacheList);
		System.out.println("1. Convert List of cache objects to JSON :");
		System.out.println(arrayToJson);

	}

	@Test
	public void deserializeFromJSONTest() throws Exception {

		//2. Convert JSON to List of Cache objects
		//Define Custom Type reference for List<Cache> type
		TypeReference<List<Cache>> mapType = new TypeReference<List<Cache>>() {
		};

		String arrayToJson = mapper.writeValueAsString(cacheList);

		List<Cache> jsonToPersonList = mapper.readValue(arrayToJson, mapType);
		System.out.println("\n2. Convert JSON to List of person objects :");

		//Print list of person objects output using Java 8
		System.out.println(jsonToPersonList);
	}


}