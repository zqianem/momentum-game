package entities;

import environment.GameEnvironment;
import events.Collision;
import events.DamageMerge;
import events.MassMerge;
import math.Vector;

abstract public class CollisionGameEntity extends GameEntity{
	
	private int collisionType; // a prime number determines behavior upon collision

	public CollisionGameEntity(String n, double x, double y) {
		super(n, x, y);
		setCollisionType(2);
	}
	
	// input in radians
	abstract public double getRadiusInDirection(double t);
	
	public int getCollisionType(){
		return collisionType;
	}
	
	public void setCollisionType(int c) {
		collisionType = c;
	}
	
	
	public static void processCollision(CollisionGameEntity e1, CollisionGameEntity e2){
		
		GameEnvironment environ = e1.getEnvironment();
		GameEnvironment environ2 = e2.getEnvironment();
		
		if(environ == null || environ2 == null){
			return;
		}
		
		
		double deltaX = e2.xCoord - e1.xCoord;
		double deltaY = e2.yCoord - e1.yCoord;
		
		// preliminary checks
		if(deltaX*2 > e2.width + e1.width || deltaY*2 > e2.height + e1.height)
			return;
		
		Vector v = new Vector(deltaX, deltaY);
		double distance = Vector.magnitude(v);
		double angle = v.getAngle();
		
		if(distance <= e1.getRadiusInDirection(angle) + e2.getRadiusInDirection(angle + Math.PI)){
			
			int cProduct = e1.getCollisionType()*e2.getCollisionType();

						
			switch(cProduct){
			
			case 0:
				break;
			
			case 4:
				environ.addEvent(new MassMerge(e1, e2));
				break;
				
			case 6:
				if(!e1.getOriginalColor().equals(e2.getOriginalColor()))
					if(e1.getCollisionType() == 3)
						environ.addEvent(new DamageMerge(e1, e2));
					else
						environ.addEvent(new DamageMerge(e2, e1));
				else
					environ.addEvent(new Collision(e1, e2));
				break;
				
			case 9:
				environ.addEvent(new Collision(e1, e2));
				break;
				
			}
		}
	}
}
