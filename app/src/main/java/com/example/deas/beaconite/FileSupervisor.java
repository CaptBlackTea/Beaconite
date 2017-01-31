package com.example.deas.beaconite;

import android.util.Log;

import com.example.deas.beaconite.dataIO.BeaconMapper;
import com.example.deas.beaconite.dataIO.DOTSettings;
import com.example.deas.beaconite.graphStuff.BeaconiteEdge;
import com.example.deas.beaconite.graphStuff.BeaconiteVertex;
import com.example.deas.beaconite.graphStuff.GraphViewPositionProvider;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.jgrapht.ext.DOTExporter;
import org.jgrapht.ext.DOTImporter;
import org.jgrapht.ext.ImportException;
import org.jgrapht.graph.ClassBasedEdgeFactory;
import org.jgrapht.graph.SimpleDirectedGraph;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class FileSupervisor {
	protected static final String TAG = "FileSupervisor";

	// Mapper for JSON (de-)serialization
	private final ObjectMapper jsonMapper = new BeaconMapper();
	private final File graphPositionFile;
	private File cacheFile;
	private File graphFile;
	private String jsonAsString;

	// FIXME: Exceptions
	public FileSupervisor(File cacheFile, File graphFile, File graphPoistionFile) throws IllegalArgumentException {
		if (cacheFile == null && graphFile == null) {
			throw new NullPointerException();
		} else {
			this.cacheFile = cacheFile;
			this.graphFile = graphFile;
			this.graphPositionFile = graphPoistionFile;
		}
		// make JSON pretty ^^ then write it in a cacheFile
//		jsonMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
//		jsonMapper.enableDefaultTyping();
	}


	public void writeAllDataInFile(List<Cache> allMyCaches, SimpleDirectedGraph<BeaconiteVertex, BeaconiteEdge> graph, GraphViewPositionProvider<BeaconiteVertex> positionProvider) {
		try {
			writeAllCachesInFile(allMyCaches);
			writeGraphToFile(graph);
			writeGraphPositionToFile(positionProvider);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void writeGraphPositionToFile(GraphViewPositionProvider<BeaconiteVertex> positionProvider) {
		// FIXME: Exceptions
		Log.d(TAG, "--- PositionProvider: " + positionProvider.getPositionMap());

		// Map the data with Jackson, write cacheFile not with Jackson
		// writing cacheFile with Android native tools
		try (FileOutputStream fOut = new FileOutputStream(graphPositionFile)) {
//			jsonAsString = jsonMapper.writeValueAsString(positionProvider);

			Log.d(TAG, "--- PositionProvider: " + positionProvider.getPositionMap());
//			graphPositionFile.createNewFile();


			jsonMapper.writeValue(fOut, positionProvider);

		} catch (IOException e) {
			Log.e("Exception", "File write failed: " + e.toString());
		}
	}

	public void writeAllCachesInFile(List<Cache> allMyCaches) throws IOException {
		// FIXME: Exceptions

		// Map the data with Jackson, write cacheFile not with Jackson
		// writing cacheFile with Android native tools
		try (FileOutputStream fOut = new FileOutputStream(cacheFile)) {
			jsonAsString = jsonMapper.writerFor(new TypeReference<List<Cache>>() {
			}).writeValueAsString(allMyCaches); // TODO: remove string maker? needed?

			cacheFile.createNewFile();


			jsonMapper.writerFor(new TypeReference<List<Cache>>() {
			}).writeValue(fOut, allMyCaches);

		} catch (IOException e) {
			Log.e("Exception", "File write failed: " + e.toString());
		}
	}

	//TODO: excpetion handling?
	public void writeGraphToFile(SimpleDirectedGraph<BeaconiteVertex, BeaconiteEdge> graph) throws
			IOException {
		try {
			graphFile.createNewFile();


			DOTExporter<BeaconiteVertex, BeaconiteEdge> dotExporter = DOTSettings.makeDOTExporter();
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

	public GraphViewPositionProvider<BeaconiteVertex> loadPositionProviderFromFile() throws IOException {

		// TODO: optimize this?

		// read Cache Data from a File with inputstream and mapper
		try (FileInputStream fIn = new FileInputStream(graphPositionFile)) {

			GraphViewPositionProvider<BeaconiteVertex> positionProviderFromFile = jsonMapper
					.readValue(fIn, GraphViewPositionProvider.class);
			System.out.println("\n2. Convert JSON to PostitionProvider object :");

			//Print list of person objects output using Java 8
			System.out.println(positionProviderFromFile.getPositionMap());

			return positionProviderFromFile;
		}

	}

	//TODO: excpetion handling?
	public SimpleDirectedGraph<BeaconiteVertex, BeaconiteEdge> loadGraphFromFile() throws ImportException, IOException {

		SimpleDirectedGraph<BeaconiteVertex, BeaconiteEdge> importGraph = new SimpleDirectedGraph<>(new
				ClassBasedEdgeFactory<BeaconiteVertex, BeaconiteEdge>(BeaconiteEdge.class));

		try (FileInputStream fin = new FileInputStream(graphFile)) {

			String dotAsString = convertStreamToString(fin);
			//Make sure you close all streams.

			DOTImporter<BeaconiteVertex, BeaconiteEdge> importer = DOTSettings.makeDOTImporter();

			importer.read(dotAsString, importGraph);

			System.out.println("### Imported Graph: " + importGraph);

		}

		return importGraph;
	}

	private String convertStreamToString(InputStream is) throws IOException {

		StringBuilder sb = new StringBuilder();

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line).append("\n");
			}
		}
		return sb.toString();
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