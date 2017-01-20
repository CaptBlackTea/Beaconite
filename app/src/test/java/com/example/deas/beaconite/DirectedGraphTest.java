package com.example.deas.beaconite;

import android.util.Log;

import com.example.deas.beaconite.graphStuff.BeaconiteEdge;
import com.example.deas.beaconite.graphStuff.BeaconiteVertex;
import com.example.deas.beaconite.graphStuff.DOTGraphStyling;
import com.example.deas.beaconite.graphStuff.VertexAttribute;

import org.jgrapht.ext.ComponentAttributeProvider;
import org.jgrapht.ext.DOTExporter;
import org.jgrapht.ext.DOTImporter;
import org.jgrapht.ext.EdgeProvider;
import org.jgrapht.ext.ImportException;
import org.jgrapht.ext.StringEdgeNameProvider;
import org.jgrapht.ext.VertexNameProvider;
import org.jgrapht.ext.VertexProvider;
import org.jgrapht.graph.ClassBasedEdgeFactory;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.example.deas.beaconite.graphStuff.EdgeAttribute.MUSTNOT;
import static com.example.deas.beaconite.graphStuff.EdgeAttribute.REQUIRED;

/**
 * how does a directed graph work
 * <p>
 * Created by deas on 12/10/16.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({Log.class})
public class DirectedGraphTest {

	private File directedGraphDOT = new File("src/test/res" +
			"/directedGraph.dot");

	private File directedGraphJSON = new File("src/test/res" +
			"/directedGraph.json");

	private FileSupervisor fileSupervisor = new FileSupervisor(directedGraphJSON, directedGraphDOT);
	private SimpleDirectedGraph<BeaconiteVertex, BeaconiteEdge> graph;

	private SimpleDirectedGraph<BeaconiteVertex, BeaconiteEdge> makeTestGraph() {
		SimpleDirectedGraph<BeaconiteVertex, BeaconiteEdge> testGraph = new SimpleDirectedGraph<>(new
				ClassBasedEdgeFactory<BeaconiteVertex, BeaconiteEdge>(BeaconiteEdge.class));

		// make some nodes/vertices
		BeaconiteVertex v1 = new BeaconiteVertex("v1");
		BeaconiteVertex v2 = new BeaconiteVertex("v2");
		BeaconiteVertex v3 = new BeaconiteVertex("v3");
		BeaconiteVertex v4 = new BeaconiteVertex("v4");

		// put set some attributes for the vertices
		v1.setAttribute(VertexAttribute.PROTECTION);
		v2.setAttribute(VertexAttribute.DANGER);
		v3.setAttribute(VertexAttribute.TREASURE);

		// put vertices in the graph
		testGraph.addVertex(v1);
		testGraph.addVertex(v2);
		testGraph.addVertex(v3);
		testGraph.addVertex(v4);


		// make edges between vertices
		testGraph.addEdge(v1, v2, new BeaconiteEdge(v1, v2));
		testGraph.addEdge(v2, v1, new BeaconiteEdge(v2, v1));
		testGraph.addEdge(v1, v4, new BeaconiteEdge(v1, v4, MUSTNOT));
		testGraph.addEdge(v2, v4, new BeaconiteEdge(v2, v4, REQUIRED));
		testGraph.addEdge(v2, v3, new BeaconiteEdge(v2, v3));

		return testGraph;
	}

	@Before
	public void setUp() throws Exception {
		PowerMockito.mockStatic(Log.class);

	}

	@After
	public void tearDown() throws Exception {

	}


	@Test
	public void graphToDOTFile() {
		File graphDOT = new File("src/test/res" +
				"/directedGraph_FilSup.dot");

		File graphJSON = new File("src/test/res" +
				"/directedGraph_FilSup.json");

		FileSupervisor fileSupervisor = new FileSupervisor(graphJSON, graphDOT);

		graph = makeTestGraph();

		try {
			fileSupervisor.writeGraphToFile(graph);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


//	vertexIDProvider - for generating vertex IDs. Must not be null.
//	vertexLabelProvider - for generating vertex labels. If null, vertex labels will not be written to the file (unless an attribute provider is supplied which also supplies labels).
//	edgeLabelProvider - for generating edge labels. If null, edge labels will not be written to the file.
//	vertexAttributeProvider - for generating vertex attributes. If null, vertex attributes will not be written to the file.
//	edgeAttributeProvider - for generating edge attributes. If null, edge attributes will not be written to the file.

//	DOTExporter(VertexNameProvider<V> vertexIDProvider,
//	            VertexNameProvider<V> vertexLabelProvider,
//	            EdgeNameProvider<E> edgeLabelProvider,
//	            ComponentAttributeProvider<V> vertexAttributeProvider,
//	            ComponentAttributeProvider<E> edgeAttributeProvider)


	@Test
	public void DOTExporterSettings() {
		graph = makeTestGraph();


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

		FileWriter fileWriter = null;

		try {
			fileWriter = new FileWriter(directedGraphDOT);

			exporter.export(fileWriter, graph);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String chooseColor(BeaconiteEdge edge) {
		return DOTGraphStyling.DOTcolorEgde(edge.getAttribute());
	}

	private String chooseShape(BeaconiteVertex vertex) {
		return DOTGraphStyling.DOTshapeVertex(vertex.getAttribute());
	}

	@Test
	public void DOTImportSettings() throws FileNotFoundException, ImportException {
		SimpleDirectedGraph<BeaconiteVertex, BeaconiteEdge> importGraph = new SimpleDirectedGraph<>(new
				ClassBasedEdgeFactory<BeaconiteVertex, BeaconiteEdge>(BeaconiteEdge.class));

		VertexProvider<BeaconiteVertex> vertexProvider = new VertexProvider<BeaconiteVertex>() {
			@Override
			public BeaconiteVertex buildVertex(String label, Map<String, String> attributes) {

				BeaconiteVertex vertex;
				System.out.println("Vertex Provider: Label and Id - " + label + " | " +
						attributes.get("id"));

				if (attributes.containsKey("id")) {
					vertex = new BeaconiteVertex(label, attributes.get("id"));
				} else {
					vertex = new BeaconiteVertex(label);
				}
				for (Map.Entry<String, String> entry : attributes.entrySet()) {
					System.out.println("Vertex Provider: Entryset - " + entry);
				}

				vertex.updateVertex(attributes);
				System.out.println(vertex);

				return vertex;
			}
		};

		EdgeProvider<BeaconiteVertex, BeaconiteEdge> edgeProvider = new EdgeProvider<BeaconiteVertex, BeaconiteEdge>() {
			@Override
			public BeaconiteEdge buildEdge(BeaconiteVertex from, BeaconiteVertex to, String label, Map<String, String> attributes) {

				BeaconiteEdge edge = new BeaconiteEdge<BeaconiteVertex>(from, to);

				System.out.println("Edge Provider: Label - " + label + "; From: " + from + "; To:" +
						" " + to);

				for (Map.Entry<String, String> entry : attributes.entrySet()) {
					System.out.println("Edge Provider: Entryset - " + entry);
				}

				edge.updateEdgeAttributes(attributes);

				return edge;
			}
		};


		DOTImporter<BeaconiteVertex, BeaconiteEdge> importer = new DOTImporter<BeaconiteVertex, BeaconiteEdge>
				(vertexProvider, edgeProvider);

		try (FileInputStream fin = new FileInputStream(directedGraphDOT)) {

			String dotAsString = convertStreamToString(fin);
			//Make sure you close all streams.

			importer.read(dotAsString, importGraph);

			System.out.println("### Imported Graph: " + importGraph);

		} catch (IOException e) {
			e.printStackTrace();
		}

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
}