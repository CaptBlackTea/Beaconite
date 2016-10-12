package com.example.deas.beaconite;

import android.util.Log;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class FileSupervisor {

	// Mapper for JSON (de-)serialization
	private final ObjectMapper jsonMapper = new ObjectMapper();
	private File file;

	// FIXME: Exceptions
	public FileSupervisor(File fileToAccess) throws IllegalArgumentException {
		if (fileToAccess == null) {
			throw new NullPointerException();
		} else if (!fileToAccess.canWrite()) {
			throw new IllegalArgumentException("File is not writeable!");
		} else {
			this.file = fileToAccess;
		}
		// make JSON pretty ^^ then write it in a file
		jsonMapper.enable(SerializationFeature.INDENT_OUTPUT);
	}

	public void writeAllCachesInFile(List<Cache> allMyCaches) throws JsonGenerationException,
			JsonMappingException,
			IOException {

		// Map the data with Jackson, write file not with Jackson
		// writing file with Android native tools
		try (FileOutputStream fOut = new FileOutputStream(file)) {
			String jsonAsString = jsonMapper.writeValueAsString(allMyCaches);

			file.createNewFile();

			jsonMapper.writeValue(fOut, allMyCaches);


		} catch (IOException e) {
			Log.e("Exception", "File write failed: " + e.toString());
		}
	}

	public List<Cache> loadCachesFromFile() throws IOException, FileNotFoundException {
		// TODO: implemnt this some day...

		// read Cache Data from a File with inputstream and mapper
		try (FileInputStream fIn = new FileInputStream(file)) {
			TypeReference<List<Cache>> mapType = new TypeReference<List<Cache>>() {
			};

			List<Cache> jsonToCacheList = jsonMapper.readValue(fIn, mapType);
			System.out.println("\n2. Convert JSON to List of person objects :");

			//Print list of person objects output using Java 8
			System.out.println(jsonToCacheList);

			return jsonToCacheList;
		}


	}
}