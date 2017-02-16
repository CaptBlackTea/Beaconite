package com.example.deas.beaconite.graphStuff.BeaconiteAppGraph;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.example.deas.beaconite.R;
import com.example.deas.beaconite.activities.PlaceStoryElementsActivity;
import com.example.deas.beaconite.graphStuff.BeaconiteVertex;

/**
 * Created by deas on 16/02/17.
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
	 */
	@Override
	public boolean vertexSelected(@Nullable final BeaconiteVertex vertex) {
		// if there is no such vertex: Alert and skip the rest!
		if (vertex == null) {
			Toast.makeText(activity, "No such vertex.", Toast.LENGTH_LONG).show();
			return true;
		}
		PlaceStoryElementsActivity storyActivity = null;

		if (activity instanceof PlaceStoryElementsActivity) {
			storyActivity = (PlaceStoryElementsActivity) activity;
		}

		if (storyActivity != null) {
			// TODO: split getAvailableElements to getDangers, getProtections, etc!
			// TODO: in this Handler lookup Attribute and only show possible List:
			// e.g.: DANGER: List of danger story elements;
			final String[] optionsForVertex = new String[storyActivity.getAvailableElements().size()];

			int i = 0;
			for (String attribute : storyActivity.getAvailableElements()) {
				optionsForVertex[i] = attribute;
				i++;
			}

			DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int selectedIndex) {
					vertex.setStoryElement(optionsForVertex[selectedIndex]);
				}
			};
			buildDialog(optionsForVertex, R.string.chooseStoryElementVertex, onClickListener);
		}
		return true;
	}
}
