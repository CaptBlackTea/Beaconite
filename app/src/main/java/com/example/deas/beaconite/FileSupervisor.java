package com.example.deas.beaconite;

import android.util.Log;

import com.example.deas.beaconite.dataIO.BeaconMapper;
import com.example.deas.beaconite.graphStuff.BeaconiteEdge;
import com.example.deas.beaconite.graphStuff.BeaconiteVertex;
import com.example.deas.beaconite.graphStuff.GraphStyling;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.jgrapht.ext.ComponentAttributeProvider;
import org.jgrapht.ext.DOTExporter;
import org.jgrapht.ext.StringEdgeNameProvider;
import org.jgrapht.ext.VertexNameProvider;
import org.jgrapht.graph.SimpleDirectedGraph;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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


	public void writeAllDataInFile(List<Cache> allMyCaches, SimpleDirectedGraph<BeaconiteVertex, BeaconiteEdge> graph) {
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
	public void writeGraphToFile(SimpleDirectedGraph<BeaconiteVertex, BeaconiteEdge> graph) throws
			IOException {
		try {
			graphFile.createNewFile();


			DOTExporter<BeaconiteVertex, BeaconiteEdge> dotExporter = makeDOTExporter();
			FileWriter fileWriter = new FileWriter(graphFile);
			dotExporter.export(fileWriter, graph);


		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	// TODO: maybe move to another place?
	private DOTExporter<BeaconiteVertex, BeaconiteEdge> makeDOTExporter() {

		// internal hopefully unique id in the dot file -> not displayed in the graph picture!
		VertexNameProvider<BeaconiteVertex> vertexIDProvider = new VertexNameProvider<BeaconiteVertex>() {
			@Override
			public String getVertexName(BeaconiteVertex vertex) {
				return vertex.getId(); // TODO: good place?
			}
		};

		// default implementation uses the toString of the Vertex and sets it as [label]
//		StringNameProvider<BeaconiteVertex> vertexIDProvider = new
//				StringNameProvider<BeaconiteVertex>();

		// name of the vertex in the graph picture (the thing in the circle)
		// -> [label] in the dot file
		// !!! -> when reading the file [label] is provided as identifier!
		VertexNameProvider<BeaconiteVertex> vertexLabelProvider = new
				VertexNameProvider<BeaconiteVertex>() {
					@Override
					public String getVertexName(BeaconiteVertex vertex) {
						return vertex.getName();
					}
				};

		// default implementation uses the toString of the Edge and sets it as [label]
		StringEdgeNameProvider<BeaconiteEdge> edgeLabelProvider = new
				StringEdgeNameProvider<BeaconiteEdge>();

//		EdgeNameProvider<BeaconiteEdge> edgeLabelProvider = new EdgeNameProvider<BeaconiteEdge>() {
//			@Override
//			public String getEdgeName(BeaconiteEdge edge) {
//				return edge.getAttribute().toString();
//			}
//		};

		// appears only in the dot file if no official dot-attribute
		ComponentAttributeProvider<BeaconiteVertex> vertexAttributeProvider = new
				ComponentAttributeProvider<BeaconiteVertex>() {
					@Override
					public Map<String, String> getComponentAttributes(BeaconiteVertex vertex) {
						Map<String, String> map = new LinkedHashMap<String, String>();
						map.put("id", vertex.getId());
						map.put("attribute", vertex.getAttribute().toString());
						map.put("shape", chooseShape(vertex));
						return map;
					}
				};

		// appears only in the dot file, if it is an official dot-attribute it will be displayed
		// in the picture output
		ComponentAttributeProvider<BeaconiteEdge> edgeAttributeProvider = new ComponentAttributeProvider<BeaconiteEdge>() {
			@Override
			public Map<String, String> getComponentAttributes(BeaconiteEdge edge) {
				Map<String, String> map = new LinkedHashMap<String, String>();
				map.put("attribute", edge.getAttribute().toString());
				map.put("color", chooseColor(edge));
				return map;
			}
		};


		DOTExporter<BeaconiteVertex, BeaconiteEdge> exporter = new DOTExporter<>
				(vertexIDProvider, vertexLabelProvider, edgeLabelProvider,
						vertexAttributeProvider, edgeAttributeProvider);

		return exporter;
	}

	private String chooseColor(BeaconiteEdge edge) {
		return GraphStyling.colorEgde(edge.getAttribute());
	}

	private String chooseShape(BeaconiteVertex vertex) {
		return GraphStyling.shapeVertex(vertex.getAttribute());
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
	public SimpleDirectedGraph<BeaconiteVertex, BeaconiteEdge> loadGraphFromFile() {
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