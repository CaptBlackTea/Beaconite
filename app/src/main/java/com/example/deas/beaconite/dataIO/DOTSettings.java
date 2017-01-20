package com.example.deas.beaconite.dataIO;

import com.example.deas.beaconite.graphStuff.BeaconiteEdge;
import com.example.deas.beaconite.graphStuff.BeaconiteVertex;
import com.example.deas.beaconite.graphStuff.DOTGraphStyling;

import org.jgrapht.ext.ComponentAttributeProvider;
import org.jgrapht.ext.DOTExporter;
import org.jgrapht.ext.DOTImporter;
import org.jgrapht.ext.EdgeProvider;
import org.jgrapht.ext.StringEdgeNameProvider;
import org.jgrapht.ext.VertexNameProvider;
import org.jgrapht.ext.VertexProvider;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * To make export / import settings for graph to/from DOT file Created by deas on 13/01/17.
 */

public class DOTSettings {

	public static DOTExporter<BeaconiteVertex, BeaconiteEdge> makeDOTExporter() {

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

	public static DOTImporter<BeaconiteVertex, BeaconiteEdge> makeDOTImporter() {
//		SimpleDirectedGraph<BeaconiteVertex, BeaconiteEdge> importGraph = new SimpleDirectedGraph<>(new
//				ClassBasedEdgeFactory<BeaconiteVertex, BeaconiteEdge>(BeaconiteEdge.class));

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

		return importer;

//		try (FileInputStream fin = new FileInputStream(directedGraphDOT)) {
//
//			String dotAsString = convertStreamToString(fin);
//			//Make sure you close all streams.
//
//			importer.read(dotAsString, importGraph);
//
//			System.out.println("### Imported Graph: " + importGraph);
//
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}

	private static String chooseColor(BeaconiteEdge edge) {
		return DOTGraphStyling.DOTcolorEgde(edge.getAttribute());
	}

	private static String chooseShape(BeaconiteVertex vertex) {
		return DOTGraphStyling.DOTshapeVertex(vertex.getAttribute());
	}
}
