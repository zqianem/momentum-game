package events;

import entities.GameEntity;
import environment.GameEnvironment;

public class KillEntity extends GameEvent{
	
	double radius;
	
	public KillEntity(GameEntity e){
		super(e);
	}

	public KillEntity(GameEnvironment g, double x, double y) {
		super(g, x, y);
		radius = 0;
	}
	
	public KillEntity(GameEnvironment g, double x, double y, double r) {
		super(g, x, y);
		radius = r;
	}

	@Override
	public void execute(int tick) {
		
		if(myEntity != null)
			myEntity.removeSelf();
			
		
		else if(radius == 0)
			for(GameEntity e : myEnviron.getEntityList()){
				if(e.contains((int)xCoord, (int)yCoord)){
					e.removeSelf();
				}
			}
		
		else
			for(GameEntity e : myEnviron.getEntityList()){
				if(e.getDistanceTo(xCoord, yCoord) < radius){
					e.removeSelf();
				}
			}
	}

}
