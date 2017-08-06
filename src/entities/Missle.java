package entities;

import java.awt.Color;

import math.Vector;

public class Missle extends DensityMass{
	
	private GameEntity target;
	private double acceleration;
	private double maxSpeed; // not really

	public Missle(double x, double y, GameEntity tar) {
		super(x, y, 99);
		target = tar;
		acceleration = 1000;
		maxSpeed = 700;
		setWallBounce(true);
		setColor(Color.CYAN);
		setVel(350, Math.random()*Math.PI*2);
	}
	
	public Missle(double x, double y, GameEntity g, double maxSpeed) {
		this(x, y, g);
		this.maxSpeed = maxSpeed;
	}
	
	@Override
	public void update(){
		super.update();
		
		if(target != null && target.getEnvironment() != null){	
			Vector thisToTarget = new Vector(target.getXCoord() - xCoord, target.getYCoord() - yCoord);
			Vector accel = Vector.scalarMultiply(Vector.unit(thisToTarget), acceleration);
			
			xAccel = accel.getX();
			yAccel = accel.getY();	
		}
		
		else{
			xAccel = 0;
			yAccel = 0;
		}
		
		Vector vel = new Vector(xVel, yVel);
		
		if(Vector.magnitude(vel) > maxSpeed){
			Vector newVel = Vector.setMagnitude(vel, maxSpeed);
			xVel = newVel.getX();
			yVel = newVel.getY();
		}
		
	}

}
