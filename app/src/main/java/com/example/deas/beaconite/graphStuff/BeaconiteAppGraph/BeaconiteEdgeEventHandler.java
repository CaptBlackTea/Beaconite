package com.example.deas.beaconite.graphStuff.BeaconiteAppGraph;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.example.deas.beaconite.R;
import com.example.deas.beaconite.graphStuff.BeaconiteEdge;
import com.example.deas.beaconite.graphStuff.EdgeAttribute;

import org.agp8x.android.lib.andrograph.model.EdgeEvent;

/**
 * Created by deas on 31/01/17.
 */

public class BeaconiteEdgeEventHandler<V, E> implements EdgeEvent<V, BeaconiteEdge> {
	private final Activity activity;

	public BeaconiteEdgeEventHandler(Activity activity) {
		this.activity = activity;
	}

	/**
	 * Handles what happens if an edge gets selected. Opens dialog to annotate and edge with an
	 * attribute.
	 *
	 * @param sourceV the selected source vertex of this edge
	 * @param targetV the selected target vertex of this edge
	 * @param edge    the selected edge
	 * @return true: so the edge won't be deleted! -> default behaviour from interface;
	 */
	@Override
	public boolean edgeSelected(V sourceV, V targetV, final BeaconiteEdge edge) {
		// do stuff when edge selected:

		// if there is no such edge: Alert and skip the rest!
		if (edge == null) {

			Toast.makeText(activity, "No edge in this direction! Please choose an existing egde or " +
					"create one with this source and target vertex.", Toast.LENGTH_LONG).show();
			return true;
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(this.activity);

		String[] optionsForEdge = new String[EdgeAttribute.values().length];

		for (Enum attribute : EdgeAttribute.values()) {
			optionsForEdge[attribute.ordinal()] = attribute.toString();
			System.out.println("content of options: " + optionsForEdge[attribute.ordinal()]);
		}

		System.out.println("Size Enum/options: " + EdgeAttribute.values().length + "/" +
				optionsForEdge.length);

		builder.setTitle(R.string.chooseAnnotation)
				.setItems(optionsForEdge, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// The 'which' argument contains the index position
						// of the selected item
						switch (which) {
							case 0:
								edge.setAttribute(EdgeAttribute.values()[0]);
								break;
							case 1:
								edge.setAttribute(EdgeAttribute.values()[1]);
								break;
							case 2:
								edge.setAttribute(EdgeAttribute.values()[2]);
								break;
						}
					}
				});

		builder.show();

		// return true so the edge won't be deleted! -> default behaviour from interface;
		return true;
	}
}
