package events;

import entities.GameEntity;

public class Shoot extends GameEvent{
	
	private GameEntity shot; // thing that is shot
	private double direction; // in radians
	private double energy; // in joules, erm, math is hard

	// note - shot should not be in a grid - what does this mean?
	public Shoot(GameEntity shooter, GameEntity shot, double x, double y, double j){
		super(shooter); // x and y is where the cursor is aimed
		direction = shooter.getDirectionTo(x, y);
		this.shot = shot;
		energy = j;
	}
	
	public Shoot(GameEntity shooter, GameEntity shot, double dir, double j){
		super(shooter);
		direction = dir;
		this.shot = shot;
		energy = j;
	}

	@Override
	public void execute(int tick) {
		double shotSpeed = Math.sqrt(2*energy/shot.getMass());
		double shooterSpeed = shotSpeed*(shot.getMass()/myEntity.getMass());
		
		shot.setColor(myEntity.getOriginalColor());
		shot.addSelf(myEnviron);
		
		
		double shotXOffset = (myEntity.getHeight()/2 + shot.getHeight()/2)*Math.cos(direction);
		double shotYOffset = (myEntity.getWidth()/2 + shot.getWidth()/2)*Math.sin(direction);
//		shotXOffset = 0;
//		shotYOffset = 0;
		
		shot.setXCoord(myEntity.getXCoord() + shotXOffset);
		shot.setYCoord(myEntity.getYCoord() + shotYOffset);
		
		shot.setXVel(shotSpeed*Math.cos(direction) + myEntity.getXVel());
		shot.setYVel(shotSpeed*Math.sin(direction) + myEntity.getYVel());		
		
		myEntity.addXVel(-shooterSpeed*Math.cos(direction));
		myEntity.addYVel(-shooterSpeed*Math.sin(direction));
		
		
	}

}
