package entities;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import events.SummonEntity;

// Player-controlled version
public class StMV2 extends StMBasic implements KeyListener, Cloneable{ 
	
	private int playerID;
	private int stock;
	
	// controls shooting for P1
	final int UP_1 = KeyEvent.VK_W;
	final int LEFT_1 = KeyEvent.VK_A;
	final int RIGHT_1 = KeyEvent.VK_D;
	final int DOWN_1 = KeyEvent.VK_S;
	
	// controls shooting for P2
	final int UP_2 = KeyEvent.VK_I;
	final int LEFT_2 = KeyEvent.VK_J;
	final int RIGHT_2 = KeyEvent.VK_L;
	final int DOWN_2 = KeyEvent.VK_K;
	
	// controls shooting for P3;
	final int UP_3 = KeyEvent.VK_UP;
	final int LEFT_3 = KeyEvent.VK_LEFT;
	final int RIGHT_3 = KeyEvent.VK_RIGHT;
	final int DOWN_3 = KeyEvent.VK_DOWN;
	
	// controls aim //TODO
	final int TURN_UP_1 = KeyEvent.VK_T;
	final int TURN_LEFT_1 = KeyEvent.VK_F;
	final int TURN_RIGHT_1 = KeyEvent.VK_H;
	final int TURN_DOWN_1 = KeyEvent.VK_G;
	

	public StMV2(String n, double x, double y) {
		super(n, x, y);
		
		playerID = 1;
		stock = 3;
		setRecordPath(true);
		setDrawTail(true);
	}
	
	public StMV2(String n, double x, double y, int pID) {
		this(n, x, y);
		playerID = pID;
		
		switch(playerID){
		case 1:
			setColor(new Color(255, 26, 117));
			break;
		case 2:
			setColor(new Color(0, 204, 102));
			break;
		case 3:
			setColor(new Color(6, 95, 202));
			break;
		}
	}
	
	public StMV2(String n, double x, double y, int pID, int stock){
		this(n, x, y, pID);
		this.stock = stock;
	}


	@Override
	public void keyPressed(KeyEvent e) {
		
		if(myEnviron == null)
			return;

		int code = e.getKeyCode();
		
		switch(playerID){
		case 1:
			if(code == UP_1)
				myGuns[3].startCharge();
			
			else if(code == DOWN_1)
				myGuns[1].startCharge();
			
			else if(code == LEFT_1)
				myGuns[2].startCharge();
				
			else if(code == RIGHT_1){
				myGuns[0].startCharge();
			}
			break;
		case 2:
			if(code == UP_2)
				myGuns[3].startCharge();
			
			else if(code == DOWN_2)
				myGuns[1].startCharge();
			
			else if(code == LEFT_2)
				myGuns[2].startCharge();
				
			else if(code == RIGHT_2){
				myGuns[0].startCharge();
			}
			break;
			
		case 3:
			if(code == UP_3)
				myGuns[3].startCharge();
			
			else if(code == DOWN_3)
				myGuns[1].startCharge();
			
			else if(code == LEFT_3)
				myGuns[2].startCharge();
				
			else if(code == RIGHT_3){
				myGuns[0].startCharge();
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if(myEnviron == null)
			return;
		
		int code = e.getKeyCode();
		
		switch(playerID){
		case 1:
			if(code == UP_1)
				myGuns[3].shoot();
			
			else if(code == DOWN_1)
				myGuns[1].shoot();
			
			else if(code == LEFT_1)
				myGuns[2].shoot();
			
			else if(code == RIGHT_1)
				myGuns[0].shoot();
			
			break;
			
		case 2:
			if(code == UP_2)
				myGuns[3].shoot();
			
			else if(code == DOWN_2)
				myGuns[1].shoot();
			
			else if(code == LEFT_2)
				myGuns[2].shoot();
			
			else if(code == RIGHT_2)
				myGuns[0].shoot();
			
			break;
		case 3:
			if(code == UP_3)
				myGuns[3].shoot();
			
			else if(code == DOWN_3)
				myGuns[1].shoot();
			
			else if(code == LEFT_3)
				myGuns[2].shoot();
			
			else if(code == RIGHT_3)
				myGuns[0].shoot();
		}
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		if(myEnviron == null)
			return;
	}
	
	@Override
	public void removeSelf(){
		
		if(stock > 1){
			myEnviron.addEvent(new SummonEntity(myEnviron, this, 50));
		}
		
		stock--;
		myEnviron.getLevel().incrementScore(-166);
		super.removeSelf();
	}
	
	public void setStock(int s){
		stock = s;
	}
	
	public int getStock(){
		return stock;
	}

//	@Override
//	public void update(int tickStep){
//		System.out.println(inContact);
//
//		super.update(tickStep);	
//	}
	
	@Override
	public StMV2 clone(){
		StMV2 c = new StMV2(name, xCoord, yCoord);
		
		c.ID = ID;
		c.typeID = typeID;

		c.drawPath = drawPath; 
		c.drawTail = drawTail;
		c.name = new String(name); 

		c.xCoord = xCoord;
		c.yCoord = yCoord;
		c.angle = angle;

		c.height = height;
		c.width = width;
		c.color = color;	
		c.originalColor = originalColor; 

		c.maxHealth = maxHealth;
		c.health = health;

		c.lifetime = lifetime; 
		
		c.stock = stock;
		return c;
	}

}
