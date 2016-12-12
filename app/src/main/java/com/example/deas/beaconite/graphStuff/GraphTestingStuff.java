package com.example.deas.beaconite.graphStuff;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.ClassBasedEdgeFactory;
import org.jgrapht.graph.SimpleGraph;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.example.deas.beaconite.graphStuff.EdgeAttribute.MUSTNOT;
import static com.example.deas.beaconite.graphStuff.EdgeAttribute.REQUIRED;

/**
 * Data structure representing an undirected graph. Such a graph has vertices (v) and edges (e).
 * <p>
 * Created by deas on 23/11/16.
 */

public class GraphTestingStuff {

	/**
	 * This main is just for the purpose of testing the functionality of this and connected classes
	 * without needing to run Android.
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Ahoi");


//		UndirectedGraph<BeaconiteVertex, DefaultEdge> testGraph = new SimpleGraph<>(DefaultEdge.class);

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

		System.out.println(testGraph);
		graphToDotFile(testGraph);

	}


	public static void graphToDotFile(UndirectedGraph<BeaconiteVertex, BeaconiteEdge> graph) {
		StringBuilder sb = new StringBuilder();
		sb.append("graph {");

		for (BeaconiteEdge de : graph.edgeSet()) {
			sb.append(graph.getEdgeSource(de).getName());
			sb.append(" -- ");
			sb.append(graph.getEdgeTarget(de).getName());

			// for labeling the edges
			sb.append(" [label=" + de.getAttribute());
			sb.append(" color=" + chooseEdgeColor(de.getAttribute()));
			sb.append("];");

			sb.append("\n");
		}

		sb.append("}");

		try (FileOutputStream fOut = new FileOutputStream(new File("graph.dot"))) {

			fOut.write(sb.toString().getBytes());


		} catch (IOException e) {

		}
	}

	public static String chooseEdgeColor(Enum label) {

		String colorString;

		if (label.equals(EdgeAttribute.REQUIRED)) {
			colorString = "green";

		} else if (label.equals(EdgeAttribute.MUSTNOT)) {
			colorString = "red";

		} else {
			colorString = "black";
		}

		return colorString;
	}

}
