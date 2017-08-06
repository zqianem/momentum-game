package events;

import java.util.ArrayList;

import entities.CollisionGameEntity;
import entities.DecayMass;
import entities.GameEntity;

public class ExplosionV1 extends GameEvent{
	
	private int particleNumber;

	public ExplosionV1(GameEntity e, int n) {
		super(e);
		particleNumber = n;
		myEntities = new ArrayList<GameEntity>();
		duration = 10;
	}

	@Override
	public void execute(int tick) {
		
		if(tick == 0){
			generateParticles(particleNumber);
			myEnviron.addEvent(new Repulsion(myEntities, xCoord, yCoord, 1000));	
		}			

		
		else if(tick == 10){
			for(GameEntity e : myEntities)
				((CollisionGameEntity) e).setCollisionType(2);
		}
	}
	
	private void generateParticles(int num){
		double w = myEntity.getWidth();
		double h = myEntity.getHeight();
		double m = myEntity.getMass();
		double density = m/(h*w*w);
		double particleMass = m/particleNumber/2; //TODO vary energy based on mass loss
		
		for(int i=0; i<num; i++){
			double x = myEntity.getXCoord() + Math.random()*w-(w/2);
			double y = myEntity.getYCoord() + Math.random()*h-(h/2);
			
			CollisionGameEntity particle = new DecayMass(x, y, particleMass, density, 300);
			particle.setCollisionType(0);
			particle.setColor(myEntity.getColor());
			particle.addSelf(myEnviron);
			myEntities.add(particle);
		}
		
//		myEntity.removeSelf();
		// this caused some null pointer issues, manually remove self
	}
}
