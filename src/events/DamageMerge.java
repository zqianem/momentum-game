package events;

import java.awt.Color;
import java.util.List;

import entities.*;

public class DamageMerge extends MergeEvent{
	
	private Color originalDamageeColor;
	private double originalDamageeMass;
	boolean dead = false;
	
	public DamageMerge(List<GameEntity> damagers) {
		super(damagers);
		
		for(GameEntity e : myEntities)
			if(((CollisionGameEntity) e).getCollisionType() == 3){
				myEntity = e;
				break;
			}
		
		originalDamageeColor = myEntity.getOriginalColor();
		originalDamageeMass = myEntity.getMass();
		
		duration = 50;
	}
	
	public DamageMerge(GameEntity damagee, GameEntity damager){
		super(damagee, damager);
		
		originalDamageeColor = myEntity.getOriginalColor();
		originalDamageeMass = myEntity.getMass();
		
		duration = 50;
	}
	
	@Override
	public void execute(int tick){
		
		if(myEntity.getEnvironment() == null)
			return;
		
		if(tick == 0){
			super.execute(tick);
			double massDif = myEntity.getMass() - originalDamageeMass;
			myEntity.setMass(originalDamageeMass);
			
			if(myEntity.subHealth(massDif) < 0){
				dead = true;
				((CollisionGameEntity) myEntity).setCollisionType(0);
			}
		
		}
		
		else{
		
			if(tick%20 == 0){
				myEntity.setTempColor(Color.WHITE);
			}
			
			else if(tick%10 == 0)
				myEntity.setTempColor(originalDamageeColor);
		}
		
		if(tick == duration){
			myEntity.setTempColor(originalDamageeColor);
			if(dead){
				((CollisionGameEntity) myEntity).setCollisionType(3);
				myEntity.removeSelf();
			}
		}
	}
	
	

}
