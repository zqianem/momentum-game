package events;

import java.util.List;

import entities.*;

// conserves momentum, mass
abstract public class MergeEvent extends GameEvent{
	
	public MergeEvent(List<GameEntity> l) {
		super(l);
		myEntity = l.get(0); // usually override, entity that survives merge
	}
	
	public MergeEvent(GameEntity survivor, List<GameEntity> mergers){
		super(survivor, mergers); // survivor included in mergers list
	}
	
	public MergeEvent(GameEntity e1, GameEntity e2){
		super(e1, e2);
	}

	@Override
	public void execute(int tick) {

		if(tick != 0)
			return;
		
		int num = myEntities.size();
		
		double totalMass = 0;
		double totalXMomentum = 0, totalYMomentum = 0;
		double xVel = 0, yVel = 0;
		double xCoM = 0, yCoM = 0; // center of mass
		
//		Color c;
//		int r = 0, g = 0, b = 0;
		
		for(GameEntity e : myEntities){
			if(e.getEnvironment() == null)			
				return;
			
			double m = e.getMass();
			
			totalMass += m;
			totalXMomentum += m*e.getXVel();
			totalYMomentum += m*e.getYVel();
			xCoM += m*e.getXCoord();
			yCoM += m*e.getYCoord();
			
//			r += Math.pow(e.getColor().getRed(), 2);
//			g += Math.pow(e.getColor().getGreen(), 2);
//			b += Math.pow(e.getColor().getBlue(), 2);
		}
		
//		// averages colors
//		r = (int) Math.sqrt(r/num);
//		g = (int) Math.sqrt(g/num);
//		b = (int) Math.sqrt(b/num);
//		
//		c = new Color(r, g, b);
		
		
		
		// takes averages of things if masses are zero
		if(totalMass == 0){
			for(GameEntity e : myEntities){
				xCoM += e.getXCoord();
				yCoM += e.getYCoord();
				xVel += e.getXVel();
				yVel += e.getYVel();
			}
			
			xCoM /= num;
			yCoM /= num;
			xVel /= num;
			yVel /= num;
		}
		
		else{
			xCoM /= totalMass;
			yCoM /= totalMass;
			xVel = totalXMomentum/totalMass;
			yVel = totalYMomentum/totalMass;
		}
		
		for(GameEntity e : myEntities)
			if(e != myEntity)
				e.removeSelf();;
		
			myEntity.setXCoord(xCoM);
			myEntity.setYCoord(yCoM);
			
			myEntity.setXVel(xVel);
			myEntity.setYVel(yVel);
			myEntity.setMass(totalMass);
//			myEntity.setColor(c);
	}
	
	

}
