package events;

import entities.*;
import environment.GameEnvironment;

public class SummonEntity extends GameEvent{
	
	GameEntity myEntity;
	
	public SummonEntity(GameEnvironment g, double x, double y) {
		super(g, x, y);
	}
	
	
	public SummonEntity(GameEnvironment g, double x, double y, int delay) {
		this(g, x, y);
		setDuration(delay);
	}
	
	public SummonEntity(GameEnvironment g, GameEntity e){
		this(g, e.getXCoord(), e.getYCoord());
		myEntity = e;
	}
	
	public SummonEntity(GameEnvironment g, GameEntity e, int num){
		this(g, e.getXCoord(), e.getYCoord(), num);
		myEntity = e;
	}

	public SummonEntity(GameEnvironment g, double x, double y, GameEntity e){
		this(g, x, y);
		myEntity = e;
	}
	
	@Override
	public void execute(int x) {
		
		if(x == duration){
			if(myEntity == null){
				GameEntity e = new DensityMass(xCoord, yCoord, 1000);
				e.setXVel(0);
				e.setYVel(0);
				e.addSelf(getEnvironment());
			}
			
			// Resurrection
			else{
				myEntity.resetLifetime();
				myEntity.setHealth(myEntity.getMaxHealth());
				myEntity.setVel(0, 0);
				myEntity.addSelf(myEnviron);
				myEntity.setInvulnerable(true); //prevents partial tick of vulnerability
				myEnviron.addEvent(new Invulnerability(myEntity, 300));
			}
		}
	}

}
