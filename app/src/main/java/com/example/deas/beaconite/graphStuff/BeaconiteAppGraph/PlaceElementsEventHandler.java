package com.example.deas.beaconite.graphStuff.BeaconiteAppGraph;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.deas.beaconite.R;
import com.example.deas.beaconite.activities.StoryElements;
import com.example.deas.beaconite.graphStuff.BeaconiteVertex;
import com.example.deas.beaconite.graphStuff.VertexAttribute;

import java.util.ArrayList;
import java.util.List;

/**
 * This EventHandler should only be used with Activities implementing a certain Interface. Created
 * by deas on 16/02/17.
 */
public class PlaceElementsEventHandler extends BeaconiteVertexEventHandler {

	public PlaceElementsEventHandler(Activity activity) {
		super(activity);

	}

	/**
	 * If a vertex is selected a Dialog with a list of available game elements is displayed from
	 * which an element can be chosen. The chosen element is then assigned to this vertex.
	 *
	 * @param vertex the selected vertex
	 * @return true: the event that this vertex was selected was consumed.
	 * @throws IllegalStateException if the calling activity has not the Interface StoryElements
	 */
	@Override
	public boolean vertexSelected(@Nullable final BeaconiteVertex vertex) {
		// if there is no such vertex: Alert and skip the rest!
		if (vertex == null) {
			Toast.makeText(activity, "No such vertex.", Toast.LENGTH_LONG).show();
			return true;
		}

		// if we have an Activity not implementing this Interface we cannot proceed as the
		// methods are needed.
		if (!(activity instanceof StoryElements)) {
//			throw new IllegalStateException("The calling activity has not the required interface!");
			Log.e("PlaceElementsEH", "Wrong invocation. Calling Activity does not " +
					"implement correct interface StoryElements");
			return true;
		}

		final StoryElements storyActivity = (StoryElements) activity;

		// TODO: split getDangerElements to getDangers, getProtections, etc!
		// TODO: in this Handler lookup Attribute and only show possible List:
		// e.g.: DANGER: List of danger story elements;
		List<String> optionsForVertex = new ArrayList<>();

		if (vertex.getAttribute().equals(VertexAttribute.DANGER)) {
			optionsForVertex.addAll(storyActivity.getDangerElements());

		} else if (vertex.getAttribute().equals(VertexAttribute.PROTECTION)) {
			optionsForVertex.addAll(storyActivity.getProtectionElements());

		} else if (vertex.getAttribute().equals(VertexAttribute.TREASURE)) {
			optionsForVertex.addAll(storyActivity.getTreasures());

		} else {
			return true; // break off here as there are no option for vertices without specific
			// attrtibute
			// TODO: adapt so that a story card can be placed no matter what the vertex is -> later
		}

		final String[] optionsForVertexArray = optionsForVertex.toArray(new String[0]);

		DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int selectedIndex) {
				vertex.setStoryElement(optionsForVertexArray[selectedIndex]);
				Log.d("STORY EventHandler", "onClick; vertex: " + vertex.toString());
			}
		};
		buildDialog(optionsForVertexArray, R.string.chooseStoryElementVertex, onClickListener);

		return true;
	}
}
