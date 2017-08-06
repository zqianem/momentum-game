package entities;

public class DecayMass extends DensityMass{

	private int maxTime; // set negative for infinite decay time
	
	
	public DecayMass(double x, double y, double m, int time) {
		super(x, y, m);
		maxTime = time;
	}
	
	public DecayMass(double x, double y, double m, double d, int time) {
		super(x, y, m, d);
		maxTime = time;
	}
	
	@Override
	public void update(){
		super.update();
		
		if(lifetime == maxTime)
			removeSelf();
	}

}
