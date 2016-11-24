package com.example.deas.beaconite.graphStuff;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

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


		UndirectedGraph<Vertex, DefaultEdge> testGraph = new SimpleGraph<>(DefaultEdge.class);

		// make some nodes/vertices
		Vertex v1 = new Vertex("v1");
		Vertex v2 = new Vertex("v2");
		Vertex v3 = new Vertex("v3");
		Vertex v4 = new Vertex("v4");

		// put vetices in the graph
		testGraph.addVertex(v1);
		testGraph.addVertex(v2);
		testGraph.addVertex(v3);
		testGraph.addVertex(v4);


		// make edges between vertices
		testGraph.addEdge(v1, v2);
		testGraph.addEdge(v1, v4);
		testGraph.addEdge(v2, v4);
		testGraph.addEdge(v2, v3);

		System.out.println(testGraph);
		plotTestGraph(testGraph);

	}


	public static void plotTestGraph(UndirectedGraph<Vertex, DefaultEdge> graph) {
		StringBuilder sb = new StringBuilder();
		sb.append("graph {");

		for (DefaultEdge de : graph.edgeSet()) {
			sb.append(graph.getEdgeSource(de).getName());
			sb.append(" -- ");
			sb.append(graph.getEdgeTarget(de).getName());

			sb.append("\n");
		}

		sb.append("}");

		try (FileOutputStream fOut = new FileOutputStream(new File("graph.dot"))) {

			fOut.write(sb.toString().getBytes());


		} catch (IOException e) {

		}
	}

}
