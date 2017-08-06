package entities;

import blocks.LinkedTeleportBlock;

public class PGShot extends CollisionGameEntity{

	private PGWalker source;
	
	public PGShot(String n, PGWalker source) {
		super(n, -100, -100);
		this.source = source;
		
		setMass(1);
		setWidth(10);
		setHeight(10);
	}
	
	@Override
	public void update(){
		
		if(contactBlock != null){
			removeSelf();
			
			if(contactBlock != source.getPortal1() && contactBlock != source.getPortal2()){
			
				double angle = getAngleFromContactBlock() + Math.PI/2;
				
				double deltaX = 50*Math.cos(angle) + width/2*Math.sin(angle);
				double deltaY = 50*Math.sin(angle) - height/2*Math.cos(angle);

				
					
				LinkedTeleportBlock block = new LinkedTeleportBlock(xCoord-deltaX, yCoord-deltaY, 100, 10, angle);
				block.disableSide(1);
				block.disableSide(2);
				block.disableSide(3);
				
				block.disableVertex(0);
				block.disableVertex(1);
				block.disableVertex(2);
				block.disableVertex(3);


				myEnviron.addBlock(block);
				source.updatePortal(block);
			}
		}
		
		super.update();

	}
	
	
	
	
	
	@Override
	public double getRadiusInDirection(double t) {
		return width/2;
	}

}
