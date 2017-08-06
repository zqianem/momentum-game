package events;


import entities.CollisionGameEntity;
import entities.GameEntity;
import math.Vector;

public class Collision extends GameEvent{

	public Collision(GameEntity e1, GameEntity e2) {
		super(e1, e2);
	}

	@Override
	public void execute(int tick) {

		// only first two entities in list, ignores others based on low chance
		GameEntity e1 = myEntities.get(0);
		GameEntity e2 = myEntities.get(1);

		if(e1.getVel() > e2.getVel()){
			e1 = myEntities.get(1);
			e2 = myEntities.get(0);
		}
		
		// ensure entities are no longer colliding	
		double angle = e1.getDirectionTo(e2);
		double dist = ((CollisionGameEntity) e1).getRadiusInDirection(angle) + ((CollisionGameEntity) e2).getRadiusInDirection(angle+Math.PI);
		e2.setXCoord(e1.getXCoord() - dist*Math.cos(angle));
		e2.setYCoord(e1.getYCoord() - dist*Math.sin(angle));
		
		
		// collision velocities equations
		double m1 = e1.getMass();
		double m2 = e2.getMass();
		
		Vector v1 = new Vector(e1.getXVel(), e1.getYVel());
		Vector v2 = new Vector(e2.getXVel(), e2.getYVel());
		
		Vector x1 = new Vector(e1.getXCoord(), e1.getYCoord());
		Vector x2 = new Vector(e2.getXCoord(), e2.getYCoord());
		
		Vector x1Minusx2 = Vector.subtract(x1, x2);
		Vector x2Minusx1 = Vector.subtract(x2, x1);
		
		Vector v1Prime = Vector.subtract(v1, Vector.scalarMultiply(x1Minusx2, (2*m2/(m1+m2))*Vector.dotProduct(Vector.subtract(v1, v2), x1Minusx2)/Vector.magnitudeSquared(x1Minusx2)));
		Vector v2Prime = Vector.subtract(v2, Vector.scalarMultiply(x2Minusx1, (2*m1/(m1+m2))*Vector.dotProduct(Vector.subtract(v2, v1), x2Minusx1)/Vector.magnitudeSquared(x2Minusx1)));
		
		
		e1.setXVel(v1Prime.getX());
		e2.setXVel(v2Prime.getX());
		e1.setYVel(v1Prime.getY());
		e2.setYVel(v2Prime.getY());
		
	}

}
