package entities;

import java.awt.Color;

public class StMRand extends StMBasic{

	private int frequency; // technically, inverse frequency (how many ticks between shots)
	
	public StMRand(double x, double y, int freq) {
		super("Ayn", x, y);
		frequency = freq;
		setColor(Color.ORANGE);
		
		
		for(Gun g : myGuns)
			g.setChargeRate(0.05);
//		setRecordPath(true);
//		setDrawPath(true);
	}
	
	@Override
	public void update(){
		super.update();
		
		
		if(lifetime%frequency == 0){
			int r = (int)(Math.random()*4);
			myGuns[r].shoot();
			
		}
			
		for(Gun g : myGuns)
			g.startCharge();

		
	}
	
	@Override
	public void removeSelf(){
		myEnviron.getLevel().incrementScore((int)maxHealth);
		super.removeSelf();
	}

}
