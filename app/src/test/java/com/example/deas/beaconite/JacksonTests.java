package com.example.deas.beaconite;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Tutorial: http://www.makeinjava.com/convert-list-objects-to-from-json-using-jackson-objectmapper/
 * Created by deas on 05/10/16.
 */
public class JacksonTests {


	private ObjectMapper mapper;
	private List<Person> personList;


	//###########################################

	@Before
	public void setUp() throws Exception {
		mapper = new ObjectMapper();
		//Set pretty printing of json
		mapper.enable(SerializationFeature.INDENT_OUTPUT);

		//Define map which will be converted to JSON
		personList = new ArrayList<Person>();
		personList.add(new Person("Mike", "harvey", 34));
		personList.add(new Person("Nick", "young", 75));
		personList.add(new Person("Jack", "slater", 21));
		personList.add(new Person("gary", "hudson", 55));
	}

	@Test
	public void listCreationTest() throws Exception {

		TestObject to = mapper.readValue("{\"strList\":[\"blubber\"]}", TestObject.class);
		System.out.println(to.getStrList());
	}

	@Test
	public void listCreationTest2() throws Exception {

		TestObject to = new TestObject(new ArrayList<String>());
		System.out.println(mapper.writeValueAsString(to));

	}

	@Test
	public void serializeToJSONTest() throws Exception {

		//1. Convert List of Person objects to JSON
		String arrayToJson = mapper.writeValueAsString(personList);
		System.out.println("1. Convert List of person objects to JSON :");
		System.out.println(arrayToJson);


	}

	@Test
	public void deserializeFromJSONTest() throws Exception {

		//2. Convert JSON to List of Person objects
		//Define Custom Type reference for List<Person> type
		TypeReference<List<Person>> mapType = new TypeReference<List<Person>>() {
		};

		String arrayToJson = mapper.writeValueAsString(personList);

		List<Person> jsonToPersonList = mapper.readValue(arrayToJson, mapType);
		System.out.println("\n2. Convert JSON to List of person objects :");

		//Print list of person objects output using Java 8
		System.out.println(jsonToPersonList);
	}

	public static class TestObject {
		private List<String> strList;

		@JsonCreator
		public TestObject(@JsonProperty("strList") List<String> list) {
			strList = list;

		}

		public List<String> getStrList() {
			return strList;
		}


	}

	public static class Person {
		public String firstName;
		public String lastName;
		public int age;

		public Person() {
		}

		public Person(String firstName, String lastName,
		              int age) {
			this.firstName = firstName;
			this.lastName = lastName;
			this.age = age;
		}

		public String toString() {
			return "[" + firstName + " " + lastName +
					" " + age + "]";
		}
	}


}