package entities;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import blocks.GameBlock;
import environment.*;
import events.GameEvent;

public class GameEntity implements Cloneable{
	
	int ID;
	int typeID;
	
	// if variable name affects the entity
	boolean offScreenRemove; // remove if entity goes off the screen
	boolean offScreenWrap; // if entity wraps around to the other side when it goes off-screen
	boolean wallBounce; // if entity bounces off borders of environment, has precedence over offScreenWrap
	boolean recordPath; // if the path of this entity is to recorded by the environment
	boolean drawPath; // draws a simple black line of the path of the entity
	boolean drawTail; // draws a comet-like tail on the entity
	boolean gravity; // if entity is affected by environment's gravity
	
	// all in SI Units (if ticks per second equals 100)
	double mass; // never negative, please. stuff will break

	public final static int METERS_PER_X_TICKS = 100; // unit for the following numbers
	
	double xVel;
	double yVel;	
	double xAccel; // from internal forces (rocket thrusters, etc.), but not gravity
	double yAccel;
	double angVel; // radians per s
	double angAccel;
	
	double cOR; // coefficient of restitution, 0 is perfectly inelastic, 1 is perfectly elastic

	String name; // not really used for anything, but fun

	double xCoord;
	double yCoord;
	double angle;
	
	double height;
	double width;
	Color color;	// background color, or main color if entity is not drawn
	Color originalColor; // for when entity flashes
	
	double maxHealth;
	double health;
	boolean invulnerable;
	
	GameBlock contactBlock; // null if not in contact
	int contactBlockSideNum; // negative if not in contatct
	int contactBlockVertexNum; // negative if not in contact
	
	long lifetime; // how many ticks the entity has been alive, or how many times it has been updated

	GameEnvironment myEnviron; // set when added to an environment
	List<GameEvent> myEvents; // to be executed every game tick
	
/*--Constructors-------------------------------------------------------------------------------------------------------*/
	
	// default entity values (should override most in child class constructor)
	private GameEntity(){
		ID = -1;			// -1 indicates entity does not have a proper ID assigned yet by environment
		offScreenRemove = true;
		offScreenWrap = false;
		wallBounce = false;
		gravity = false;
		mass = 0;
		xVel = 0;
		yVel = 0;
		xAccel = 0;
		yAccel = 0;
		angVel = 0;
		angAccel = 0;
		height = 10;
		width = 10;
		originalColor = new Color(0,255,0);
		color = originalColor;
		lifetime = 0;
		cOR = 1;
		angle = 0;
		maxHealth = -1;
		health = -1;
		invulnerable = false;
		recordPath = true;
		drawPath = false;
		drawTail = true;
		myEvents = new ArrayList<GameEvent>();
		contactBlock = null;
		contactBlockSideNum = -1;
		contactBlockVertexNum = -1;
	}
	
	public GameEntity(String n, double x, double y){
		this();
		name = n;
		xCoord = x;
		yCoord = y;
	}

/*--Getters-------------------------------------------------------------------------------------------------------*/

	public int getID(){return ID;}
	public GameEnvironment getEnvironment(){return myEnviron;}
	public boolean offScreenRemove(){return offScreenRemove;}
	public boolean offScreenWrap(){return offScreenWrap;}
	public double getXVel() {return xVel;}
	public double getYVel() {return yVel;}
	public double getMass() {return mass;}
	public double getXCoord(){return xCoord;}
	public double getYCoord(){return yCoord;}	
	public double getWidth(){return width;}
	public double getHeight(){return height;}
	public Color getColor(){return color;}
	public Color getOriginalColor(){return originalColor;}
	public long getLifetime() {return lifetime;}
	public boolean isWallBounce() {return wallBounce;}
	public double getCOR() {return cOR;}
	public String getName() {return name;}
	public double getHealth(){return health;}
	public double getMaxHealth(){return maxHealth;}
	public boolean invulnerble(){return invulnerable;}
	public boolean recordPath(){return recordPath;}
	public boolean drawPath(){return drawPath;}
	public boolean drawTail(){return drawTail;}
	public boolean getGravity(){return gravity;}
	public GameBlock getContactBlock(){return contactBlock;}
	public int getContactBlockSideNum(){return contactBlockSideNum;}
	public int getContactBlockVertexNum(){return contactBlockVertexNum;}

	// special getters
	
	// TL = top-left, TR = top-right, etc
	public Point2D.Double getTLPoint(){
		return new Point2D.Double(xCoord - width/2, yCoord - height/2);
	}
	
	public Point2D.Double getTRPoint(){
		return new Point2D.Double(xCoord + width/2, yCoord - height/2);
	}
	
	public Point2D.Double getBLPoint(){
		return new Point2D.Double(xCoord - width/2, yCoord + height/2);
	}
	
	public Point2D.Double getBRPoint(){
		return new Point2D.Double(xCoord + width/2, yCoord + height/2);
	}
	
	public Point2D.Double getHealthBarTLPoint(){
		return new Point2D.Double(xCoord - width/2, yCoord + height/2 + 10);
	}	
	
	public Rectangle2D.Double getFrame(){
		return new Rectangle2D.Double(xCoord - width/2, yCoord - height/2, width, height);
	}
	
	public double getDistanceTo(GameEntity other){
		double deltaX = this.xCoord - other.xCoord;
		double deltaY = this.yCoord - other.yCoord;
		return Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
	}
	
	public double getDistanceTo(double x, double y){
		double deltaX = xCoord - x;
		double deltaY = yCoord - y;
		return Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
	}
	
	public double getDirectionTo(double x, double y){
		double deltaX = xCoord - x;
		double deltaY = yCoord - y;
		return Math.atan2(deltaY, deltaX);
	}
	
	public double getDirectionTo(GameEntity e){
		return getDirectionTo(e.getXCoord(), e.getYCoord());
	}
	
	public double getVel(){
		return Math.sqrt(xVel*xVel + yVel*yVel);
	}
	
	@Override
	public String toString(){
		String n = (name == null || name == "")? "[no name]" : name;
		return n + " is at position (" + xCoord + ", " + yCoord + ")";
	}
	
	// return true if the point is contained within the bounds of the label, used for KillEntity
	public boolean contains(double x, double y){		
		
		return x > xCoord - getWidth()/2 &&
			   x < xCoord + getWidth()/2 &&
			   y > yCoord - getHeight()/2 &&
			   y < yCoord + getHeight()/2;
	}
	
	public double getAngleFromContactBlock(){
		if(contactBlock == null)
			return -1;
		
		double angle;
		
		if(contactBlockSideNum != -1)
			angle = -contactBlock.getSides()[contactBlockSideNum].getAngle();
		
		else{
			double x = contactBlock.getVertexes()[contactBlockVertexNum].getX();
			double y = contactBlock.getVertexes()[contactBlockVertexNum].getY();
			angle = this.getDirectionTo(x, y);
		}
		
		return angle;
	}

/*--Setters-------------------------------------------------------------------------------------------------------*/

	public void setEnviron(GameEnvironment e){myEnviron = e;}
	public void setID(int i){ID = i;}
	public void setoffScreenRemove(boolean b){offScreenRemove = b;}
	public void setoffScreenWrap(boolean b){offScreenWrap = b;}
	public void setMass(double m){mass = m;}
	public void setXCoord(double x){xCoord = x;}
	public void setYCoord(double y){yCoord = y;}
	public void setXVel(double x){xVel = x;}
	public void setYVel(double y){yVel = y;}
	public void setXAccel(double x){xAccel = x;}
	public void setYAccel(double y){yAccel = y;}
	public void setWidth(double w){width = w;}
	public void setHeight(double h){height = h;}
	public void setColor(Color c){originalColor = c; color = c;}
	public void setTempColor(Color c){color = c;}
	public void setWallBounce(boolean w) {wallBounce = w;}
	public void setCOR(double cOR) {this.cOR = cOR;}
	public void setName(String name) {this.name = name;}
	public void setHealth(double h){maxHealth = h; health = h;}
	public void setRecordPath(boolean b){recordPath = b;}
	public void setDrawPath(boolean b){drawPath = b;}
	public void setDrawTail(boolean b){drawTail = b;}
	public void setGravity(boolean b) {gravity = b;}
	public long resetLifetime(){long temp = lifetime; lifetime = 0; return temp;}
	public void setInvulnerable(boolean b){invulnerable = b;}
	public void setContactBlock(GameBlock b){contactBlock = b;}
	public void setContactBlockSideNum(int i){contactBlockSideNum = i;}
	public void setContactBlockVertexNum(int i){contactBlockVertexNum = i;}

	
	public double addHealth(double h){
		health += h;
		if(health > maxHealth)
			health = maxHealth;
		return getHealth();
	}
	public double subHealth(double h){
		if(!invulnerable)
			health -= h;
		
		return getHealth();
	}

	// special setters
	public void addXVel(double x){xVel += x;}
	public void addYVel(double y){yVel += y;}
	
	public void setVel(double v, double rads){	
		setXVel(Math.cos(rads)*v);
		setYVel(Math.sin(rads)*v);
	}
	
	public void addVel(double v, double rads){	
		addXVel(Math.cos(rads)*v);
		addYVel(Math.sin(rads)*v);
	}
	
	public void setAccel(double a, double rads){		
		setXAccel(Math.cos(rads)*a);
		setYAccel(Math.sin(rads)*a);
	}
	
	public void setRandomColor(){
		color = new Color((int)(Math.random()*255), 
				          (int)(Math.random()*255), 
						  (int)(Math.random()*255));
	}
	
/*--Other-------------------------------------------------------------------------------------------------------*/

	public void addSelf(GameEnvironment g){		
		myEnviron = g;
		myEnviron.addEntity(this);
	}
	
	public void removeSelf(){
		myEnviron.getToBeRemoved().add(this);
	}
	
	public void update(){

		// update an entities location based on its velocity and acceleration for tickStep number of ticks
		double timeStep = (double) 1 / METERS_PER_X_TICKS;
		
		double newXVel = xVel + xAccel * timeStep;
		double newYVel = yVel + yAccel * timeStep;
		
//		double deltaX = timeStep * (xVel + newXVel) / 2;
//		double deltaY = timeStep * (yVel + newYVel) / 2;
		
		double deltaX = timeStep * xVel;
		double deltaY = timeStep * yVel;
		
		xCoord += deltaX;
		yCoord += deltaY;
		
		xVel = newXVel;
		yVel = newYVel;
		
		//record new location
		if(recordPath)
			myEnviron.getEntityHistories().get(ID).add(new double[]{xCoord, yCoord, xVel, yVel});
		
		contactBlock = null; // reset to the appropriate block during block collision process, if needed
		contactBlockSideNum = -1;
		contactBlockVertexNum = -1;
		lifetime++;
	}
	
	@Override
	public GameEntity clone(){
		GameEntity c = new GameEntity();
		
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
		
		return c;
	}
}
