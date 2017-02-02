package com.example.deas.beaconite.graphStuff.BeaconiteAppGraph;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.example.deas.beaconite.R;
import com.example.deas.beaconite.graphStuff.BeaconiteVertex;
import com.example.deas.beaconite.graphStuff.VertexAttribute;

import org.agp8x.android.lib.andrograph.model.VertexEvent;

/**
 * Created by deas on 02/02/17.
 */

public class BeaconiteVertexEventHandler<V> implements VertexEvent<BeaconiteVertex> {


	private final Activity activity;

	public BeaconiteVertexEventHandler(Activity activity) {
		this.activity = activity;
	}

	/**
	 * Handles what happens if a vertex is selected.
	 *
	 * @param vertex the selected vertex
	 * @return true: the event that this vertex was selected was consumed.
	 */
	@Override
	public boolean vertexSelected(@Nullable final BeaconiteVertex vertex) {

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
}
