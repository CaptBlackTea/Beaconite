package com.example.deas.beaconite.graphStuff;

/**
 * Created by deas on 12/01/17.
 */

public class DOTGraphStyling {


	public static String DOTcolorEgde(Enum label) {

		if (label.equals(EdgeAttribute.REQUIRED))

		{
			return "green";

		} else if (label.equals(EdgeAttribute.MUSTNOT))

		{
			return "red";

		} else

		{
			return "black";
		}
	}

	public static String DOTshapeVertex(Enum attribute) {

		if (attribute.equals(VertexAttribute.TREASURE)) {
			return "star";

		} else if (attribute.equals(VertexAttribute.DANGER))

		{
			return "triangle";

		} else if (attribute.equals(VertexAttribute.PROTECTION))

		{
			return "doublecircle";

		} else

		{
			return "circle";
		}

	}
}
