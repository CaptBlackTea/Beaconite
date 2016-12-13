package com.example.deas.beaconite;

import android.util.Log;

import com.example.deas.beaconite.dataIO.BeaconMapper;
import com.example.deas.beaconite.graphStuff.BeaconiteEdge;
import com.example.deas.beaconite.graphStuff.BeaconiteVertex;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.jgrapht.Graph;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.ext.DOTExporter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class FileSupervisor {

	// Mapper for JSON (de-)serialization
	private final ObjectMapper jsonMapper = new BeaconMapper();
	private File cacheFile;
	private File graphFile;
	private String jsonAsString;

	// FIXME: Exceptions
	public FileSupervisor(File cacheFile, File graphFile) throws IllegalArgumentException {
		if (cacheFile == null && graphFile == null) {
			throw new NullPointerException();
		} else {
			this.cacheFile = cacheFile;
			this.graphFile = graphFile;
		}
		// make JSON pretty ^^ then write it in a cacheFile
//		jsonMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
//		jsonMapper.enableDefaultTyping();
	}


	public void writeAllDataInFile(List<Cache> allMyCaches, UndirectedGraph<BeaconiteVertex, BeaconiteEdge> graph) {
		try {
			writeAllCachesInFile(allMyCaches);
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			writeGraphToFile(graph);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writeAllCachesInFile(List<Cache> allMyCaches) throws IOException {
		// FIXME: Exceptions

		// Map the data with Jackson, write cacheFile not with Jackson
		// writing cacheFile with Android native tools
		try (FileOutputStream fOut = new FileOutputStream(cacheFile)) {
			jsonAsString = jsonMapper.writerFor(new TypeReference<List<Cache>>() {
			}).writeValueAsString(allMyCaches);

			cacheFile.createNewFile();


			jsonMapper.writerFor(new TypeReference<List<Cache>>() {
			}).writeValue(fOut, allMyCaches);

		} catch (IOException e) {
			Log.e("Exception", "File write failed: " + e.toString());
		}
	}

	// FIXME: functioning implementation needed!
	public void writeGraphToFile(UndirectedGraph<BeaconiteVertex, BeaconiteEdge> graph) throws
			IOException {
		try {
			graphFile.createNewFile();

			DOTExporter dotExporter = new DOTExporter();
			FileWriter fileWriter = new FileWriter(graphFile);
			dotExporter.export(fileWriter, graph);


		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public List<Cache> loadCachesFromFile() throws IOException {
		// TODO: optimize this?

		// read Cache Data from a File with inputstream and mapper
		try (FileInputStream fIn = new FileInputStream(cacheFile)) {
			TypeReference<List<Cache>> mapType = new TypeReference<List<Cache>>() {
			};

			List<Cache> jsonToCacheList = jsonMapper.readValue(fIn, mapType);
			System.out.println("\n2. Convert JSON to List of cache objects :");

			//Print list of person objects output using Java 8
			System.out.println(jsonToCacheList);

			return jsonToCacheList;
		}


	}

	//TODO: implement
	public Graph<BeaconiteVertex, BeaconiteEdge> loadGraphFromFile() {
		return null;
	}

	public File getCacheFile() {
		return cacheFile;
	}

	public File getGraphFile() {
		return graphFile;
	}

	public ObjectMapper getJsonMapper() {
		return jsonMapper;
	}

	public String getJsonString() {
		return jsonAsString;
	}

}