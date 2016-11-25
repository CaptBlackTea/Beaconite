package com.example.deas.beaconite.graphStuff;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.ClassBasedEdgeFactory;
import org.jgrapht.graph.SimpleGraph;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.example.deas.beaconite.graphStuff.EdgeLabel.MUSTNOT;
import static com.example.deas.beaconite.graphStuff.EdgeLabel.REQUIRED;

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


//		UndirectedGraph<Vertex, DefaultEdge> testGraph = new SimpleGraph<>(DefaultEdge.class);

		UndirectedGraph<Vertex, LabeledEdge> testGraph = new SimpleGraph<>(new
				ClassBasedEdgeFactory<Vertex, LabeledEdge>(LabeledEdge.class));



		// make some nodes/vertices
		Vertex v1 = new Vertex("v1");
		Vertex v2 = new Vertex("v2");
		Vertex v3 = new Vertex("v3");
		Vertex v4 = new Vertex("v4");

		// put vertices in the graph
		testGraph.addVertex(v1);
		testGraph.addVertex(v2);
		testGraph.addVertex(v3);
		testGraph.addVertex(v4);


		// make edges between vertices
		testGraph.addEdge(v1, v2, new LabeledEdge(v1, v2));
		testGraph.addEdge(v1, v4, new LabeledEdge(v1, v4, MUSTNOT));
		testGraph.addEdge(v2, v4, new LabeledEdge(v2, v4, REQUIRED));
		testGraph.addEdge(v2, v3, new LabeledEdge(v2, v3));

		System.out.println(testGraph);
		plotTestGraph(testGraph);

	}


	public static void plotTestGraph(UndirectedGraph<Vertex, LabeledEdge> graph) {
		StringBuilder sb = new StringBuilder();
		sb.append("graph {");

		for (LabeledEdge de : graph.edgeSet()) {
			sb.append(graph.getEdgeSource(de).getName());
			sb.append(" -- ");
			sb.append(graph.getEdgeTarget(de).getName());

			// for labeling the edges
			sb.append(" [label=" + de.getLabel());
			sb.append(" color=" + chooseEdgeColor(de.getLabel()));
			sb.append("];");

			sb.append("\n");
		}

		sb.append("}");

		try (FileOutputStream fOut = new FileOutputStream(new File("graph.dot"))) {

			fOut.write(sb.toString().getBytes());


		} catch (IOException e) {

		}
	}

	private static String chooseEdgeColor(Enum label) {

		String colorString;

		if (label.equals(EdgeLabel.REQUIRED)) {
			colorString = "green";

		} else if (label.equals(EdgeLabel.MUSTNOT)) {
			colorString = "red";

		} else {
			colorString = "black";
		}

		return colorString;
	}

}
