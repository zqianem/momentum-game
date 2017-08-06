package entities;

import java.awt.Color;
import events.Attraction;

public class DensityMass extends CollisionGameEntity{
	
	double density;
	
	public DensityMass(double x, double y, double m){
		super("Nye", x, y);
		
		if(m < 0)
			m = (int)(Math.random()*10);
		
		setRandomColor();
		setColor(Color.YELLOW);
		
		// color based on mass
//		int c = 255-(int)(m/50);
//		if(c<0)
//			c=0;
//		setColor(new Color(c,c,c));

		density = 0.01;
		setMass(m);
		
//		setXVel(Math.random()*1000 - 500);
//		setYVel(Math.random()*1000 - 500);
		//setoffScreenWrap(true);
		//setWallBounce(true);
	}
	
	public DensityMass(double x, double y, double m, double d){
		this(x, y, m);
		density = d;
		updateRadius();
	}
	
	@Override
	public void update(){
		super.update();
		
		if(mass > 150)
			myEnviron.addEvent(new Attraction(myEnviron, xCoord, yCoord, mass, -1));

	}
	
	@Override
	public void setMass(double m){
		super.setMass(m);
		updateRadius();
	}
	
	public void updateRadius(){
		int d = (int)(Math.cbrt(mass/density));
		setHeight(d);
		setWidth(d);
	}
	

	@Override
	public double getRadiusInDirection(double t) {
		return getHeight()/2;
	}
	
	public void recreate(double x, double y, double m){
		setMass(m);
		int d = (int)(5*Math.cbrt(m));
		setHeight(d);
		setWidth(d);
		setXVel(Math.random()*1000 - 500);
		setYVel(Math.random()*1000 - 500);
		//setoffScreenWrap(true);
		setWallBounce(true);
	}

}
