package events;

import environment.GameEnvironment;

public class Attraction extends Repulsion{

	public Attraction(GameEnvironment g, double x, double y, double power, double radius) {
		super(g, x, y, -power, radius);
	}

}
