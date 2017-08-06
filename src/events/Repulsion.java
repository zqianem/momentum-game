package events;

import java.util.LinkedList;
import java.util.List;

import entities.GameEntity;
import environment.GameEnvironment;

public class Repulsion extends GameEvent{
	
	private double power;

	public Repulsion(GameEnvironment g, double x, double y, double power, double radius) {
		super(g, x, y);
		
		this.power = power * 100;
		
		if(radius < 0){
			myEntities = new LinkedList<GameEntity>();
			
			// prevent div by zero
			for(GameEntity e : myEnviron.getEntityList())
				if(!(e.getXCoord() == x && e.getYCoord() == y))
					myEntities.add(e);
		}
			
		else
			myEntities = myEnviron.getEntitiesInRadius(x, y, radius);
	}
	
	public Repulsion(List<GameEntity> l, double x, double y, double power) {
		super(l, x, y);
		
		this.power = power * 100;
		
	}


	@Override
	public void execute(int tick) {
		
		for(GameEntity e : myEntities){
		
			double distance; // in pixels
			double angle; // in radians, from horizontal line through this entity to other entity
		
			double deltaX = this.xCoord - e.getXCoord();
			double deltaY = this.yCoord - e.getYCoord();
			distance = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
			
			angle = Math.atan2(deltaY, deltaX);
					
			double deltaVX = power * Math.abs(Math.cos(angle)/Math.pow(distance, 2));
			double deltaVY = power * Math.abs(Math.sin(angle)/Math.pow(distance, 2));
			
			if(deltaX < 0)
				e.addXVel(deltaVX);
			
			else
				e.addXVel(-deltaVX);
			
			if(deltaY < 0)
				e.addYVel(deltaVY);
			
			else
				e.addYVel(-deltaVY);

		}
		
	}

}
