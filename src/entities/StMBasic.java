package entities;

import java.awt.Color;

import events.*;

// StM = Shoot to Move
abstract public class StMBasic extends CollisionGameEntity{ 
	

	//double energy;
	//double turningSpeed; // rads/update
	int shotDecayTime;

	Gun[] myGuns;

	public StMBasic(String n, double x, double y) {
		super(n, x, y);
		
		myGuns = new Gun[4];
		
		for(int i=0; i<4; i++)
			myGuns[i] = new Gun(this, i*Math.PI/2);
		
		setHealth(500);
		
		setCollisionType(3);
		setHeight(50);
		setWidth(50);
		setMass(200);
		setWallBounce(true);
		setCOR(1);
		
		setColor(new Color(255, 26, 117));
		
		shotDecayTime = 300;
		
	}
	
	public void setShotDecayTime(int time){
		shotDecayTime = time;
	}

	@Override
	public double getRadiusInDirection(double d) {
		return getHeight()/2;
	}
	
	@Override
	public void update(){
		super.update();
		
		for(Gun g : myGuns)
			g.update();
	}
	
	public class Gun{
		
		double angle;
		double charge; // in terms of mass
		boolean charging;
		double chargeRate;
		
		double energyPerMass;
		double minCharge;
		double maxCharge;
		
		
		GameEntity myEntity;
		
		public Gun(GameEntity e, double ang){
			myEntity = e;
			angle = ang;
			
			chargeRate = 1;
			
			energyPerMass = 2000000;
			minCharge = 0.1;
			maxCharge = myEntity.getMass();
			
			charge = minCharge;
			charging = false;
		}
		
		public void startCharge(){charging = true;}
		public void stopCharge(){charging = false;}
		
		public void update(){
			maxCharge = myEntity.getMass();
			
			if(charging && charge < maxCharge)
				charge += chargeRate;
		}
		
		public void shoot(){
			stopCharge();
			GameEntity shot = new DecayMass(-100,-100, charge, shotDecayTime);
			myEnviron.addEvent(new Shoot(myEntity, shot , angle, energyPerMass*charge));
			charge = minCharge;
		}
		
		public void setAngle(double a){angle = a;}
		public void setChargeRate(double r){chargeRate = r;}
		
	}
	
	@Override
	public void removeSelf(){
		myEnviron.addEvent(new ExplosionV1(this, 100));
		super.removeSelf();
	}
	


}