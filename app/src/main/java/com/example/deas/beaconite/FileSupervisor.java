package com.example.deas.beaconite;

import android.util.Log;

import com.example.deas.beaconite.dataIO.BeaconMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class FileSupervisor {

	// Mapper for JSON (de-)serialization
	private final ObjectMapper jsonMapper = new BeaconMapper();
	private File file;
	private String jsonAsString;

	// FIXME: Exceptions
	public FileSupervisor(File fileToAccess) throws IllegalArgumentException {
		if (fileToAccess == null) {
			throw new NullPointerException();
		} else {
			this.file = fileToAccess;
		}
		// make JSON pretty ^^ then write it in a file
//		jsonMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
//		jsonMapper.enableDefaultTyping();
	}

	public void writeAllCachesInFile(List<Cache> allMyCaches) throws IOException {
		// FIXME: Exceptions

		// Map the data with Jackson, write file not with Jackson
		// writing file with Android native tools
		try (FileOutputStream fOut = new FileOutputStream(file)) {
			jsonAsString = jsonMapper.writerFor(new TypeReference<List<Cache>>() {
			}).writeValueAsString(allMyCaches);

			file.createNewFile();


			jsonMapper.writerFor(new TypeReference<List<Cache>>() {
			}).writeValue(fOut, allMyCaches);

		} catch (IOException e) {
			Log.e("Exception", "File write failed: " + e.toString());
		}
	}

	public List<Cache> loadCachesFromFile() throws IOException {
		// TODO: implemnt this some day...

		// read Cache Data from a File with inputstream and mapper
		try (FileInputStream fIn = new FileInputStream(file)) {
			TypeReference<List<Cache>> mapType = new TypeReference<List<Cache>>() {
			};

			List<Cache> jsonToCacheList = jsonMapper.readValue(fIn, mapType);
			System.out.println("\n2. Convert JSON to List of cache objects :");

			//Print list of person objects output using Java 8
			System.out.println(jsonToCacheList);

			return jsonToCacheList;
		}


	}

	public File getFile() {
		return file;
	}

	public ObjectMapper getJsonMapper() {
		return jsonMapper;
	}

	public String getJsonString() {
		return jsonAsString;
	}
}