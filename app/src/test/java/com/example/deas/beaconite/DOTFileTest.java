package com.example.deas.beaconite;

import android.util.Log;

import com.example.deas.beaconite.graphStuff.BeaconiteEdge;
import com.example.deas.beaconite.graphStuff.BeaconiteVertex;
import com.example.deas.beaconite.graphStuff.GraphTestingStuff;

import org.jgrapht.Graph;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.ext.ComponentAttributeProvider;
import org.jgrapht.ext.DOTExporter;
import org.jgrapht.ext.StringEdgeNameProvider;
import org.jgrapht.ext.VertexNameProvider;
import org.jgrapht.graph.ClassBasedEdgeFactory;
import org.jgrapht.graph.SimpleGraph;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.example.deas.beaconite.graphStuff.EdgeAttribute.MUSTNOT;
import static com.example.deas.beaconite.graphStuff.EdgeAttribute.REQUIRED;

/**
 * Created by deas on 12/10/16.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({Log.class})
public class DOTFileTest {

	private File dotFile = new File("src/test/res" +
			"/DOTGraph.dot");

	private File correctDOTFile = new File("src/test/res" +
			"/graph.dot");

	private File emptyFile = new File("src/test/res" +
			"/emptyFile.json");

	private FileSupervisor fileSupervisor = new FileSupervisor(emptyFile, dotFile);
	private Graph<BeaconiteVertex, BeaconiteEdge> graph;

	private UndirectedGraph<BeaconiteVertex, BeaconiteEdge> makeTestGraph() {
		UndirectedGraph<BeaconiteVertex, BeaconiteEdge> testGraph = new SimpleGraph<>(new
				ClassBasedEdgeFactory<BeaconiteVertex, BeaconiteEdge>(BeaconiteEdge.class));


		// make some nodes/vertices
		BeaconiteVertex v1 = new BeaconiteVertex("v1");
		BeaconiteVertex v2 = new BeaconiteVertex("v2");
		BeaconiteVertex v3 = new BeaconiteVertex("v3");
		BeaconiteVertex v4 = new BeaconiteVertex("v4");

		// put vertices in the graph
		testGraph.addVertex(v1);
		testGraph.addVertex(v2);
		testGraph.addVertex(v3);
		testGraph.addVertex(v4);


		// make edges between vertices
		testGraph.addEdge(v1, v2, new BeaconiteEdge(v1, v2));
		testGraph.addEdge(v1, v4, new BeaconiteEdge(v1, v4, MUSTNOT));
		testGraph.addEdge(v2, v4, new BeaconiteEdge(v2, v4, REQUIRED));
		testGraph.addEdge(v2, v3, new BeaconiteEdge(v2, v3));

		return testGraph;
	}

	@Before
	public void setUp() throws Exception {
		PowerMockito.mockStatic(Log.class);

//		dotFile = new File("src/test/res" +
//				"/emptyFile.json");
	}

	@After
	public void tearDown() throws Exception {

		//TODO: use again! but now the output is needed.... m)
//		dotFile.delete();
	}

	@Test
	public void graphToDOTFile() {
		graph = makeTestGraph();

		try {
			fileSupervisor.writeGraphToFile((UndirectedGraph<BeaconiteVertex, BeaconiteEdge>) graph);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void DOTFileWithAttributes() {
		graph = makeTestGraph();

		try {
			fileSupervisor.writeGraphToFile((UndirectedGraph<BeaconiteVertex, BeaconiteEdge>) graph);
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
				return vertex.getName();
			}
		};

		// default implementation uses the toString of the Vertex and sets it as [label]
//		StringNameProvider<BeaconiteVertex> vertexIDProvider = new
//				StringNameProvider<BeaconiteVertex>();

		// name of the vertex in the graph picture (the thing in the circle)
		// -> [label] in the dot file
		VertexNameProvider<BeaconiteVertex> vertexLabelProvider = new
				VertexNameProvider<BeaconiteVertex>() {
					@Override
					public String getVertexName(BeaconiteVertex vertex) {
						return vertex.getName() + "\n" + vertex.getAttribute();
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
						map.put("attribute", vertex.getAttribute().toString());
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
			fileWriter = new FileWriter(dotFile);

			exporter.export(fileWriter, graph);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String chooseColor(BeaconiteEdge edge) {
		return GraphTestingStuff.chooseEdgeColor(edge.getAttribute());
	}


}