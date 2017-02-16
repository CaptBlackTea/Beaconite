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


	protected final Activity activity;

	public BeaconiteVertexEventHandler(Activity activity) {
		this.activity = activity;
	}

	/**
	 * If a vertex is selected a Dialog with a list of attributes is displayed from which an
	 * element can be chosen. The chosen element is then assigned to this vertex.
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



		String[] optionsForVertex = new String[VertexAttribute.values().length];

		for (Enum attribute : VertexAttribute.values()) {
			optionsForVertex[attribute.ordinal()] = attribute.toString();
			System.out.println("content of options: " + optionsForVertex[attribute.ordinal()]);
		}

		System.out.println("Size Enum/options: " + VertexAttribute.values().length + "/" +
				optionsForVertex.length);

		DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int selectedIndex) {
				vertex.setAttribute(VertexAttribute.values()[selectedIndex]);
			}
		};
		buildDialog(optionsForVertex, R.string.chooseAnnotationVertex, onClickListener);
		return true;
	}

	/**
	 * Makes and shows an AlertDialog with options to choose from
	 *
	 * @param optionsForVertex the options to select from
	 * @param title            use Android R.string resource (this is an integer reference)
	 * @param onClickListener  what should happen when an item is selected
	 */
	protected void buildDialog(String[] optionsForVertex, int title, DialogInterface
			.OnClickListener
			onClickListener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this.activity);
		builder.setTitle(title)
				.setItems(optionsForVertex, onClickListener);

		builder.show();
	}

}
