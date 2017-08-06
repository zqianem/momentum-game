package environment;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

import blocks.GameBlock;
import blocks.LinkedTeleportBlock;
import entities.*;
import events.*;
import optimization.CollisionQuadTreeNode;
import optimization.GameEnvironmentTripleBuffer;

public class GameEnvironment implements Runnable, Cloneable{
	
	int gameLoopType = 0;
	// 0 is based on semaphores
	// 1 is based on Thread.sleep
	// 2 is based on constantly checking System.nanoTime
	
	
	// TODO consolidate your final constants across classes
	
	long currentTick;
	int ticksPerSecond; // how many times the game updates per second
	int currentEntityID;				// used to assign IDs to entities, (why do we have IDs???)
	int entityCount;
	int blockCount;	
	double gravity;
	
	boolean running;
	
	Dimension myBounds;

	
	List<GameEntity> myEntities;
	List<CollisionGameEntity> myCollisionEntities; // entities that can collide with each other, also included in myEntities
	List<StMV2> myStockEntities; // player-controlled entities with more than one life (to be displayed)
	List<GameEntity> toBeRemoved;		// list cleared at start of each cycle, used to track removal of entities
	List<GameBlock> myBlocks;
	Queue<GameEvent> myEventQueue;
	GameLevel myLevel;
	
	ArrayList<LinkedList<double[]>> entityHistories; //TODO make this its own object
	
	Thread engine;
	
	GameEnvironmentTripleBuffer myBuffer;

/*--Constructor-------------------------------------------------------------------------------------------------------*/

	public GameEnvironment(){
		
		currentTick = 0;
		ticksPerSecond = 100;
		currentEntityID = 0;
		entityCount = 0;
		blockCount = 0;
		gravity = 1000;
//		gravity = 0;
		
		running = true;
		
		myEntities = new LinkedList<GameEntity>();
		myCollisionEntities = new LinkedList<CollisionGameEntity>();
		myStockEntities = new LinkedList<StMV2>();
		toBeRemoved = new Stack<GameEntity>();
		myBlocks = new LinkedList<GameBlock>();
		myEventQueue = new LinkedBlockingQueue<GameEvent>();
		entityHistories = new ArrayList<LinkedList<double[]>>();
		engine = new Thread(this);
		myBuffer = new GameEnvironmentTripleBuffer();
		myBuffer.setRunning(this);
	}

/*--Getters and Listers-------------------------------------------------------------------------------------------------------*/
	
	public GameLevel getLevel() {return myLevel;}
	public List<GameEntity> getEntityList(){return myEntities;}
	public List<CollisionGameEntity> getCollisionEntityList(){return myCollisionEntities;}
	public List<GameEntity> getToBeRemoved(){return toBeRemoved;}
	public List<GameBlock> getBlockList(){return myBlocks;}
	public long getCurrentTick() {return currentTick;}
	public int getTickSpeed(){return ticksPerSecond;}
	public Dimension getDimensions(){return myBounds;}
	public ArrayList<LinkedList<double[]>> getEntityHistories(){return entityHistories;}
	public List<StMV2> getStockEntityList(){return myStockEntities;}
	public int getEntityCount(){return entityCount;}
	public int getBlockCount() {return blockCount;}
	public GameEnvironmentTripleBuffer getBuffer(){return myBuffer;}
	
	public void setGravity(double g){gravity = g;}
	
	public void setLevel(GameLevel g){
		myLevel = g;
		myBounds = g.getEnvironmentSize();
	}
	
	public int setTickSpeed(int t){
		int toReturn = ticksPerSecond;
		ticksPerSecond = t;
		return toReturn;
	}
	
	// adds event to queue to be triggered on next run of update
	public void addEvent(GameEvent e){myEventQueue.add(e);}
	

/*--Update Methods-------------------------------------------------------------------------------------------------------*/

	public void updateAll(){
		
		toBeRemoved.clear();
		
		// updates entity locations
		for(GameEntity e : myEntities){	
			e.setYAccel(gravity);
			e.update();
		}
		
		//processes collision using a quadtree
		CollisionQuadTreeNode root = new CollisionQuadTreeNode(this, new Rectangle2D.Double(0, 0, myBounds.getWidth(), myBounds.getHeight()));
		root.processCollisions();
		root = null;
		
		// executes all events
		Iterator<GameEvent> i = myEventQueue.iterator(); // necessary to remove during loop
	
		while(i.hasNext()){
			GameEvent e = i.next();
			e.execute(e.getCurrentTick());
    		e.incrementTick();
			
			if(e.getCurrentTick() > e.getDuration()){
				i.remove();
			}
		}
				
		loadStuffFromLevel(myLevel);
		updateOutOfBounds();
		currentTick ++;
		
		// toBeRemoved determined by outOfBounds, zero health, merges, etc. added to be entities removeSelf method
		for(GameEntity e : toBeRemoved){
			removeEntity(e);
		}
		
		myBuffer.setReadyToRender(this);
		//System.out.println(myEntities.size());
		
	}

	// removes all entities not within the dimensions, flips border-wrapping entities to other side
	// changes velocities of stuff bouncing off walls
	private void updateOutOfBounds(){
		
		double h = myBounds.getHeight();
		double w = myBounds.getWidth();
		
		final int SMUDGE = 5; // how many extra pixels to wait before removing entity off-screen
						 	   // or how far off to put it off sceen on the opposite side
							   // to account for Swing's sketchy repaint abilities for labels
		
		
		for(GameEntity g : myEntities){
			
			if(g.offScreenRemove()){
				double x = g.getXCoord();
				double y = g.getYCoord();
				double eW = g.getWidth() + SMUDGE;
				double eH = g.getHeight() + SMUDGE;
				
				if(g.isWallBounce()){
					double xWest = x - eW/2;
					double xEast = x + eW/2;
					double yNorth = y - eH/2;
					double ySouth = y + eH/2;
					
					if(xWest < 0){
						g.setXVel(-g.getXVel() * g.getCOR());
						g.setXCoord(eW/2);					// required to prevent things getting stuck in walls if going too fast
					}
					
					if(xEast > w){
						g.setXVel(-g.getXVel() * g.getCOR());
						g.setXCoord(w - eW/2);
					}
						
					
					if(yNorth < 0){
						g.setYVel(-g.getYVel() * g.getCOR());
						g.setYCoord(eH/2);
					}
					
					if(ySouth > h){
						g.setYVel(-g.getYVel() * g.getCOR());
						g.setYCoord(h - eH/2);
					}
				}
				
				else{
					if(x < 0 - eW || x > w + eW){
						if(g.offScreenWrap()){
							if(x < 0)
								g.setXCoord((x+w)%w + eW);
							else
								g.setXCoord((x+w)%w - eW);
						}
						else
							g.removeSelf();
					}
					
					else if(y < 0 - eH || y > h + eH){
						if(g.offScreenWrap()){
							if(y < 0)
								g.setYCoord((y+h)%h + eH);
							else
								g.setYCoord((y+h)%h - eH);
						}
						else
							g.removeSelf();
					}	
				}
			}
		}
	}
	
	// adds all entities and events from a level at the current tick
	// returns false if no entities were added
	public boolean loadStuffFromLevel(GameLevel level){
		
		if(level == null){
			System.out.println("ERROR: no level loaded");
			return false;
		}
		
		List<GameEntity> newEntities = level.getEntitiesAtTick(currentTick);
		List<GameEvent> newEvents = level.getEventsAtTick(currentTick);
		List<GameBlock> newBlocks = level.getBlocksAtTick(currentTick);
		
		if(newEntities == null && newEvents == null && newBlocks == null)
			return false;
				
		if(newEntities != null)
			for(GameEntity g : newEntities)
				g.addSelf(this);
		
		if(newEvents != null)
			for(GameEvent e : newEvents)
				addEvent(e);
		
		if(newBlocks != null)
			for(GameBlock b : newBlocks)
				addBlock(b);
		
		return true;	
	}
	
/*--Entity Add/Remove Methods-------------------------------------------------------------------------------------------------------*/

	
	// DO NOT USE THE FOLLOWING METHODS TO ADD OR REMOVE ENTITIES, 
	// USE THE ENTITY'S addSelf AND removeSelf METHODS INSTEAD
	public boolean addEntity(GameEntity e){
		
		if(myEntities.contains(e))
			return false;
		
		e.setID(currentEntityID);
		currentEntityID ++;
		
		entityCount ++;
		myEntities.add(0, e);
		
		if(e instanceof CollisionGameEntity)
			myCollisionEntities.add((CollisionGameEntity) e);
		
		if(e instanceof StMV2 && !myStockEntities.contains(e))
			myStockEntities.add((StMV2) e);
		
		LinkedList<double[]> entityHistory = new LinkedList<double[]>();
		entityHistories.add(e.getID(), entityHistory);
		
		return true;
	}
	
	
	// could optimize search with IDs somewhat
	public boolean removeEntity(GameEntity g){
		
		boolean toReturn = myEntities.remove(g);
		
		g.setEnviron(null);
		
		if(g instanceof CollisionGameEntity)
			myCollisionEntities.remove(g);
		
		g = null;
		
		if(toReturn)
			entityCount --;
		
		return toReturn;
	}
	
/*--Other Entity Methods-------------------------------------------------------------------------------------------------------*/

	
	public boolean contains(GameEntity g){
		
		return myEntities.contains(g);
	}
	
	// excludes entity if it is exactly at center point, to prevent div by zero errors in some events
	public List<GameEntity> getEntitiesInRadius(double x, double y, double r){
		List<GameEntity> toReturn = new LinkedList<GameEntity>();
		
		for(GameEntity e : myEntities){
			double d = e.getDistanceTo(x, y);
			
			if(d <= r && d > 0)
				toReturn.add(e);
		}
		
		return toReturn;
	}
	
/*--Block Methods-------------------------------------------------------------------------------------------------------*/

	public void addBlock(GameBlock g){
		blockCount++;
		if(g instanceof LinkedTeleportBlock)
			myBlocks.add(0, g); // gives LinkedTeleportBlocks priority in collisions
		else
			myBlocks.add(g);
	}
	
	public void removeBlock(GameBlock g){
		blockCount--;
		myBlocks.remove(g);
	}
	
/*--Stuff-------------------------------------------------------------------------------------------------------*/


	@Override
	public void run() {
		
		
		// game loop from http://stackoverflow.com/questions/5274619/investigation-of-optimal-sleep-time-calculation-in-game-loop
		// TODO learn how it actually works
		if(gameLoopType == 0){
			
	        final Semaphore mutexRefresh = new Semaphore(0);
	        final Semaphore mutexRefreshing = new Semaphore(1);
	
	        Timer timRefresh = new Timer();
	        timRefresh.scheduleAtFixedRate(new TimerTask() {
	            @Override
	            public void run() {
	                if(mutexRefreshing.tryAcquire()) {
	                    mutexRefreshing.release();
	                    mutexRefresh.release();
	                }
	            }
	        }, 0, 1000/ticksPerSecond);
	
	        // The timer is started and configured for 100fps?
	        while(true) { // Refreshing loop
	            try {
					mutexRefresh.acquire();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
	            try {
					mutexRefreshing.acquire();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
	
	            updateAll();
	
	            mutexRefreshing.release();
	        }
        }

	
		// original sleep-based game thread
		else if(gameLoopType == 1){
			long updateInterval = 1000000000/ticksPerSecond;
			long cur = System.nanoTime();
	
			while(true){
				long overTime = 0;
				
				if(!running && myLevel.gameStart())
					running = true;
			
				else if(running && myLevel.gameOver())
					running = false;
			
				if(running)
					updateAll();
				
				long dif = System.nanoTime() - cur;
				long sleepTime =  updateInterval - dif - overTime;
				
				if(sleepTime < 0){
	//				overTime += -sleepTime;
	//				if(overTime > updateInterval){
	//					overTime = 0;
	//					System.out.println("dropped one update");
	//				}
					try{Thread.sleep(1);}catch(Exception e){}
	
				}
				
				else
					try{Thread.sleep(sleepTime/1000000);}catch(Exception e){}
				
				cur = System.nanoTime();
	    	}
		}
		
		// another game loop, from here: https://www.youtube.com/watch?v=Zh7YiiEuJFw
		else if(gameLoopType == 2){
			
			long lastTime = System.nanoTime();
			double amountOfTicks = 100.0;
			double ns = 1000000000 / amountOfTicks;
			double delta = 0;
			long timer = System.currentTimeMillis();
			int updates = 0;
			while(running){
				long now = System.nanoTime();
				delta += (now - lastTime) / ns;
				lastTime = now;
				
				// this is actually brilliant
				while(delta >= 1){
					updateAll();
					updates++;
					delta--;
				}
						
				if(System.currentTimeMillis() - timer > 1000){
					timer += 1000;
					if(updates - amountOfTicks != 0)
						System.out.println("TICKS_OFF_BY: " + (updates - amountOfTicks));
					updates = 0;
				}
	        }
		}
    }
	
	public Thread getThread() {
		return engine;
	}
	
	// only clones info needed for MainGraphics display
	public GameEnvironment clone(){
		GameEnvironment c = new GameEnvironment();
		c.currentTick = currentTick;
//		c.ticksPerSecond = ticksPerSecond;
//		c.currentEntityID = currentEntityID;
		c.entityCount = entityCount;
		c.blockCount = blockCount;
		
//		c.running = running;
		
		c.myBounds = new Dimension(myBounds.width, myBounds.height);
		
		for(GameEntity e : myEntities)
			c.myEntities.add(e.clone());
		
		for(int i=0; i< myBlocks.size(); i++)
			c.myBlocks.add(myBlocks.get(i).clone());
		
		for(StMV2 s : myStockEntities)
			c.myStockEntities.add(s.clone());
		
		c.myLevel = myLevel.clone();
		
		c.entityHistories = entityHistories;
		c.engine = null;
		
		return c;
	}


	

}
