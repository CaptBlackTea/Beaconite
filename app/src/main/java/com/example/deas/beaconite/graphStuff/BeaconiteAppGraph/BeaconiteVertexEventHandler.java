package com.example.deas.beaconite.graphStuff.BeaconiteAppGraph;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.example.deas.beaconite.R;
import com.example.deas.beaconite.graphStuff.BeaconiteEdge;
import com.example.deas.beaconite.graphStuff.BeaconiteVertex;
import com.example.deas.beaconite.graphStuff.EdgeAttribute;
import com.example.deas.beaconite.graphStuff.VertexAttribute;

import org.agp8x.android.lib.andrograph.model.VertexEvent;
import org.jgrapht.graph.SimpleDirectedGraph;

/**
 * Created by deas on 02/02/17.
 */

public class BeaconiteVertexEventHandler<V> implements VertexEvent<BeaconiteVertex> {


	private final Activity activity;
	private SimpleDirectedGraph<BeaconiteVertex, BeaconiteEdge> graph;

	private boolean autoInsertMissingEdges = false;

	public BeaconiteVertexEventHandler(Activity activity) {
		this.activity = activity;
	}

	public BeaconiteVertexEventHandler(Activity activity, SimpleDirectedGraph<BeaconiteVertex,
			BeaconiteEdge> graph) {
		this(activity);
		this.graph = graph;
	}

	/**
	 * Handles what happens if a vertex is selected.
	 *
	 * @param vertex the selected vertex
	 * @return true: the event that this vertex was selected was consumed.
	 */
	@Override
	public boolean vertexSelected(@Nullable final BeaconiteVertex vertex) {

		if (autoInsertMissingEdges) {
			drawMustNotEdges(vertex);
			return true; // FIXME: make pretty "else" if this works
		}

		// if there is no such vertex: Alert and skip the rest!
		if (vertex == null) {
			Toast.makeText(activity, "No such vertex.", Toast.LENGTH_LONG).show();
			return true;
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(this.activity);

		String[] optionsForVertex = new String[VertexAttribute.values().length];

		for (Enum attribute : VertexAttribute.values()) {
			optionsForVertex[attribute.ordinal()] = attribute.toString();
			System.out.println("content of options: " + optionsForVertex[attribute.ordinal()]);
		}

		System.out.println("Size Enum/options: " + VertexAttribute.values().length + "/" +
				optionsForVertex.length);

		builder.setTitle(R.string.chooseAnnotationVertex)
				.setItems(optionsForVertex, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// The 'which' argument contains the index position
						// of the selected item
						switch (which) {
							case 0:
								vertex.setAttribute(VertexAttribute.values()[0]);
								break;
							case 1:
								vertex.setAttribute(VertexAttribute.values()[1]);
								break;
							case 2:
								vertex.setAttribute(VertexAttribute.values()[2]);
								break;
							case 3:
								vertex.setAttribute(VertexAttribute.values()[3]);
								break;
						}
					}
				});

		builder.show();
		return true;
	}

	/**
	 * Adds incoming edges to the given vertex from all vertices of the graph to which it has no
	 * edges so far. All added edges will be annotated with "MUSTNOT" Precondition: Directed graph,
	 * no loops (vertex has no edge to itself) will be made.
	 *
	 * @param vertex the vertex to add incoming edges to
	 */
	private void drawMustNotEdges(BeaconiteVertex vertex) {
		if (graph != null) {
			// -1 because the given vertex is also part of the set
			int maxAmountOfInEdges = graph.vertexSet().size() - 1;
			System.out.println("maxAmountOfInEdges/vertexSet: " +
					maxAmountOfInEdges + " / " + graph.vertexSet().size());

			//only add edges if the given vertex is not connected to all vertices of the graph
			if (graph.inDegreeOf(vertex) < maxAmountOfInEdges) {
				for (BeaconiteVertex v : graph.vertexSet()) {
					if (!graph.containsEdge(v, vertex) && !v.equals(vertex)) {
						graph.addEdge(v, vertex).setAttribute(EdgeAttribute.MUSTNOT);
					}
				}
			}
		}
	}

	public boolean isAutoInsertMissingEdges() {
		return autoInsertMissingEdges;
	}

	public void setAutoInsertMissingEdges(boolean autoInsertMissingEdges) {
		this.autoInsertMissingEdges = autoInsertMissingEdges;
	}
}
