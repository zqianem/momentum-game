package environment;

import java.awt.Dimension;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.*;

import blocks.GameBlock;
import entities.GameEntity;
import events.GameEvent;

public class GameLevel implements Cloneable{
	
	// from the top-left corner, initial settings for MainPanel
	double zoom; 
	double xOffset;
	double yOffset; 
	
	Dimension environmentSize;
	
	Map<Long, List<GameEntity>> myEntityTimeMap;	// time and entities that appear at that tick
	Map<Long, List<GameEvent>> myEventTimeMap;	// time and events that appear at that tick
	Map<Long, List<GameBlock>> myBlockTimeMap;
	Stack<GameEntity> myInputEntities;
	GameEntity trackee;
	
	int score;	// keep track of anything, really, displayed in bottom right corner
	boolean displayScore;
	

	public GameLevel(){
		zoom = 1;
		xOffset = 0;
		yOffset = 0;
		environmentSize = new Dimension(1600, 1000);
		
		myEntityTimeMap = new HashMap<Long, List<GameEntity>>();
		myEventTimeMap = new HashMap<Long, List<GameEvent>>();
		myBlockTimeMap = new HashMap<Long, List<GameBlock>>();
		myInputEntities = new Stack<GameEntity>();
		score = 0;
		displayScore = false;
		
		// create and add all entites for a level here in extended classes
	}
	
	public boolean gameStart(){return true;} // condition for the level to start
	public boolean gameOver(){return false;} // condition for the level to end

	
	public Stack<GameEntity> getInputEntities() {
		return myInputEntities;
	}
	
	public List<GameEntity> getEntitiesAtTick(long t){
		return myEntityTimeMap.get(new Long(t));
	}
	
	public List<GameEvent> getEventsAtTick(long t){
		return myEventTimeMap.get(new Long(t));
	}
	
	public List<GameBlock> getBlocksAtTick(long t){
		return myBlockTimeMap.get(new Long(t));
	}
	
	public GameEntity getTrackee(){return trackee;}
	public void setTrackee(GameEntity e){trackee = e;}
	public double getZoom(){return zoom;}
	public double getXOffset(){return xOffset;}
	public double getYOffset(){return yOffset;}

	
	public void addEntities(List<GameEntity> list, long time){
		myEntityTimeMap.put(time, list);
		
		for(GameEntity gE : list){
			if(gE instanceof KeyListener ||
			   gE instanceof MouseListener ||
			   gE instanceof MouseMotionListener)
				myInputEntities.add(gE);
		}
	}
	
	public void addEvents(List<GameEvent> list, long time){
		myEventTimeMap.put(time, list);
	}
	
	public void addBlocks(List<GameBlock> list, long time){
		myBlockTimeMap.put(time, list);
	}
	
	public void addEntity(GameEntity gE, long time){
		if(myEntityTimeMap.containsKey(time))
			myEntityTimeMap.get(time).add(gE);
		
		else{
			List<GameEntity> newList = new Stack<GameEntity>();
			newList.add(gE);
			myEntityTimeMap.put(time, newList);
		}
		
		if(gE instanceof KeyListener ||
	       gE instanceof MouseListener ||
	       gE instanceof MouseMotionListener)
			myInputEntities.add(gE);
	}
	
	public void addEvent(GameEvent gE, long time){
		if(myEventTimeMap.containsKey(time))
			myEventTimeMap.get(time).add(gE);
		
		else{
			List<GameEvent> newList = new Stack<GameEvent>();
			newList.add(gE);
			myEventTimeMap.put(time, newList);
		}
	}
	
	public void addBlock(GameBlock gB, long time){
		if(myBlockTimeMap.containsKey(time))
			myBlockTimeMap.get(time).add(gB);
		
		else{
			List<GameBlock> newList = new Stack<GameBlock>();
			newList.add(gB);
			myBlockTimeMap.put(time, newList);
		}
	}
	
	public int getTotalEntities(){
		int sum = 0;
		for(List<GameEntity> l : myEntityTimeMap.values())
			sum += l.size();
		return sum;
	}
	
	public int getTotalEvents(){
		int sum = 0;
		for(List<GameEvent> l : myEventTimeMap.values())
			sum += l.size();
		return sum;
	}
	
	public int getTotalBlocks(){
		int sum = 0;
		for(List<GameBlock> l : myBlockTimeMap.values())
			sum += l.size();
		return sum;
	}
	
	public void incrementScore(){score += 100;}
	public void incrementScore(int i){score += i;}
	public int getScore(){return score;}
	
	public boolean getDisplayScore(){return displayScore;}
	public void setDisplayScore(boolean b){displayScore = b;}

	public Dimension getEnvironmentSize() {
		return environmentSize;
	}
		
	public GameLevel clone(){
		GameLevel c = new GameLevel();
		c.zoom = zoom;
		c.xOffset = xOffset;
		c.yOffset = yOffset;
		
		c.score = score;
		c.displayScore = displayScore;
		
		c.environmentSize = new Dimension(environmentSize.width, environmentSize.height);
		
		if(trackee != null)
			c.trackee = trackee.clone();
		
		return c;
	}
	
	public double randX(){
		return Math.random()*environmentSize.getWidth();
	}
	
	public double randY(){
		return Math.random()*environmentSize.getHeight();
	}
	
	public double centerX(){
		return environmentSize.getWidth()/2;
	}
	
	public double centerY(){
		return environmentSize.getHeight()/2;
	}
	
}
