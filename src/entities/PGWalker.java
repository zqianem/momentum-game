package entities;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import blocks.LinkedTeleportBlock;
import events.GameEvent;
import events.Shoot;

public class PGWalker extends CollisionGameEntity implements KeyListener{
	
	int playerID;
	Mouse myMouse;

	final int UP_1 = KeyEvent.VK_W;
	final int LEFT_1 = KeyEvent.VK_A;
	final int RIGHT_1 = KeyEvent.VK_D;
	final int DOWN_1 = KeyEvent.VK_S;
	final int PORTAL_1 = KeyEvent.VK_Q;
	final int PORTAL_2 = KeyEvent.VK_E;
	
	// which keys are being held down currently
	boolean up;
	boolean left;
	boolean right;
	boolean down;
	
	int jumpCount; // how many ticks the jump button has been held down for
	double jumpAngle; // angle at which to jump
	
	final double AF_VEL_MAX = 2000;
	
	LinkedTeleportBlock portal1;
	LinkedTeleportBlock portal2;
	int curPortal;

	public PGWalker(String n, double x, double y, Mouse mouse) {
		super(n, x, y);
		
		playerID = 1;
		myMouse = mouse;
		
		curPortal = 1;
		jumpCount = 0;
		jumpAngle = 0;
		
		setHealth(Integer.MAX_VALUE);
		
		setCollisionType(3);
		setHeight(50);
		setWidth(50);
		setMass(100);
		setWallBounce(false);
		setCOR(1);
		
		setColor(Color.MAGENTA);
	}
	
	public PGWalker(String n, double x, double y, Mouse mouse, int player){
		this(n, x, y, mouse);
		
		playerID = player;
	}
	
	public LinkedTeleportBlock updatePortal(LinkedTeleportBlock newPortal){
		curPortal++;
		
		if(curPortal%2 == 0){	
			myEnviron.removeBlock(portal1);
			portal1 = newPortal;
			portal1.setLink(portal2);
			portal1.setColor(Color.ORANGE);
			if(portal2 != null)
				portal2.setLink(portal1);
			return portal2;
		}
		
		else{
			myEnviron.removeBlock(portal2);
			portal2 = newPortal;
			portal2.setLink(portal1);
			portal2.setColor(Color.CYAN);

			if(portal1 != null)
				portal1.setLink(portal2);
			return portal1;
		}
	}
	
	public LinkedTeleportBlock getPortal1(){return portal1;}
	public LinkedTeleportBlock getPortal2(){return portal2;}

	
	@Override
	public void update(){
		
		final int contactAccel = 10;
		final int airAccel = 5;
		final int jumpHeight = 10;
		
		if(contactBlock != null){
			if(left && xVel >= -AF_VEL_MAX)		
				xVel -= contactAccel;
			
			else if(right && xVel <= AF_VEL_MAX)	
				xVel += contactAccel;
						
			else if(!right && !left){
				if(xVel > 0){
					xVel -= contactAccel;
					if(xVel < 0)
						xVel = 0;
				}
				
				else if(xVel < 0){
					xVel += contactAccel;
					if(xVel > 0)
						xVel = 0;
				}	
			}
		}
		
		else{
			if(left && xVel >= -AF_VEL_MAX)		
				xVel -= airAccel;
			
			else if(right && xVel <= AF_VEL_MAX)	
				xVel += airAccel;
			
			else if(!right && !left){
				if(xVel > 0){
					xVel -= airAccel;
					if(xVel < 0)
						xVel = 0;
				}
				
				else if(xVel < 0){
					xVel += airAccel;
					if(xVel > 0)
						xVel = 0;
				}	
			}
		}
		
		
		if(up && contactBlock != null && !(contactBlock instanceof LinkedTeleportBlock)){
			jumpAngle = getAngleFromContactBlock();
			addVel(100, jumpAngle);
			jumpCount = 1;
		}
		
		// holding the jump key after initial jump leads to higher jump, up to certain point
		else if(up && jumpCount > 0 && jumpCount < jumpHeight){
			addVel(100-jumpCount*5, jumpAngle);
			jumpCount++;
		}
		
		else
			jumpCount = 0;
		
		if(down && contactBlock == null){
			addYVel(15);
		}
		
		super.update();
	}
	
	

	@Override
	public double getRadiusInDirection(double t) {
		return width/2;
	}



	@Override
	public void keyPressed(KeyEvent e) {
		
		int code = e.getKeyCode();
		
		if(code == UP_1)
			up = true;
		
		else if(code == DOWN_1)
			down = true;
		
		else if(code == LEFT_1)
			left = true;
		
		else if(code == RIGHT_1)
			right = true;
		
		else if(code == PORTAL_1){
			PGShot shot = new PGShot(name, this);
			GameEvent shoot = new Shoot(this, shot, this.getDirectionTo(myMouse) + Math.PI, 10000000);
			myEnviron.addEvent(shoot);
		}

	}



	@Override
	public void keyReleased(KeyEvent e) {
		
		int code = e.getKeyCode();
		
		if(code == UP_1)
			up = false;
		
		else if(code == DOWN_1)
			down = false;
		
		else if(code == LEFT_1)
			left = false;
		
		else if(code == RIGHT_1)
			right = false;
		
	}



	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
