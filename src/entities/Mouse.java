package entities;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import events.ExplosionV1;
import events.KillEntity;
import events.Repulsion;
import events.SummonEntity;
import graphicsAndInput.MainGraphics;

@SuppressWarnings("unused")
public class Mouse extends GameEntity implements MouseListener, MouseMotionListener{
	
	private boolean inScreen;
	private MainGraphics myScreen;
	
	boolean explosion;
	boolean summonMass;
	boolean summonMissle;
	
	
	// coordinates on the screen, not in environment
	int mouseX;
	int mouseY;
	
	public Mouse() {
		super("Jerry", -100, -100);
		setHeight(0);
		setWidth(0);
		
		mouseX = 0;
		mouseY = 0;
		
		drawTail = false;
	}
	
	public void setExplosion(boolean explosion) {
		this.explosion = explosion;
	}

	public void setSummonMass(boolean summonMass) {
		this.summonMass = summonMass;
	}

	public void setSummonMissle(boolean summonMissle) {
		this.summonMissle = summonMissle;
	}
	
	public boolean isInScreen(){return inScreen;}
	public void setScreen(MainGraphics g){myScreen = g;} // invoke this when adding listener

	@Override
	public void mouseClicked(MouseEvent arg0) {
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		inScreen = true;
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		inScreen = false;
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		//myEnviron.addEvent(new SummonEntity(myEnviron, e.getX(), e.getY()));	
//		myEnviron.addEvent(new Repulsion(myEnviron, xCoord, yCoord, 100000, -1));
		//myEnviron.addEvent(new Shoot(myEnviron, e.getX(), e.getY(), myEnviron.getLevel().getInputEntities().get(0), new DensityMass(-100,-100, 5), 1000000));
//		myEnviron.addEvent(new KillEntity(myEnviron, xCoord, yCoord, 100));
//		myEnviron.addEvent(new ExplosionV1(myEnviron, myEnviron.getEntityList().get(0), 100));
//		myEnviron.addEvent(new SummonEntity(myEnviron, new Missle(xCoord, yCoord, myEnviron.getLevel().getTrackee())));
//		myEnviron.addEvent(new ExplosionV1(myEnviron, myEnviron.getEntityList().get(0), 100));
		if(summonMass)
			myEnviron.addEvent(new SummonEntity(myEnviron, xCoord, yCoord));

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		if(explosion){
			GameEntity death =  myEnviron.getEntityList().get(0);
			myEnviron.addEvent(new ExplosionV1(death, 100));
			myEnviron.addEvent(new KillEntity(death));
		}

		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		mouseMoved(e);
		mousePressed(e);
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		double xOffset = myScreen.getXOffset();
		double yOffset = myScreen.getYOffset();
		double zoom = myScreen.getZoom();
		
		mouseX = e.getX();
		mouseY = e.getY();
	}
	
	@Override
	public void removeSelf(){
		// do nothing when mouse goes out of bounds
	}
	
	@Override
	public void update(){
		
		double xOffset = myScreen.getXOffset();
		double yOffset = myScreen.getYOffset();
		double zoom = myScreen.getZoom();
		
		double x = mouseX/zoom + xOffset;
		double y = mouseY/zoom + yOffset;
		
		if(myEnviron != null){
			// always keeps mouse position in bounds of environment
			if(x >= 0 && x <= myEnviron.getDimensions().getWidth())
				setXCoord(x);
			
			if(y >= 0 && y <= myEnviron.getDimensions().getHeight())
				setYCoord(y);
		}
//		
//		myEnviron.addEvent(new Repulsion(myEnviron, xCoord, yCoord, 1000, -1));


	}
	
	

}
