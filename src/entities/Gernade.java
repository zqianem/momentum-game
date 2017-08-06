package entities;

import events.ExplosionV1;

public class Gernade extends DensityMass{

	private int detTime;
	
	public Gernade(double x, double y, double mass, int time){
		super(x, y, mass);
		detTime = time;
	}
	
	@Override
	public void update(){
		super.update();
		
		if(lifetime == detTime){
			myEnviron.addEvent(new ExplosionV1(this, 20));
			removeSelf();
		}
	}
}
