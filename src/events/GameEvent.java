package events;

import java.util.ArrayList;
import java.util.List;

import entities.*;
import environment.GameEnvironment;

public abstract class GameEvent {
	
	GameEnvironment myEnviron;
	GameEntity myEntity;		// use this if only one entity is affected
	List<GameEntity> myEntities;		// use this for multiple entities
	int duration;
	int currentTick; // relative to when event started
	
	double xCoord;
	double yCoord;
	
	private GameEvent(){
		currentTick = 0;
		duration = 0;
	}
	
	public GameEvent(GameEnvironment g){
		this();
		myEnviron = g;
	}
	
	public GameEvent(GameEntity e){
		this();
		myEntity = e;
		myEnviron = e.getEnvironment();
		xCoord = e.getXCoord();
		yCoord = e.getYCoord();
	}
	
	public GameEvent(GameEntity myEntity, GameEntity other){
		this(myEntity);
		myEntities = new ArrayList<GameEntity>();
		myEntities.add(myEntity);
		myEntities.add(other);
	}
	
	public GameEvent(List<GameEntity> l){
		this();
		myEntities = l;
		myEnviron = l.get(0).getEnvironment();
	}
	
	public GameEvent(GameEntity e, List<GameEntity> l){
		this(e);
		myEntities = l;
	}

	public GameEvent(GameEnvironment g, double x, double y){
		this(g);
		xCoord = x;
		yCoord = y;
	}
	
	public GameEvent(GameEntity e, double x, double y){
		this(e);
		xCoord = x;
		yCoord = y;
	}
	
	public GameEvent(List<GameEntity> l, double x, double y){
		this(l);
		xCoord = x;
		yCoord = y;
	}
	
	public int getDuration(){return duration;}
	public int getCurrentTick(){return currentTick;}
	public GameEnvironment getEnvironment() {return myEnviron;}
	
	public void setDuration(int d){duration = d;}
	public void incrementTick(){currentTick++;}
	public void setLocation(double x, double y){
		xCoord = x;
		yCoord = y;
	}
	
	// what ever the event does with the environment or optional entities at specified time tick 
	// after event has been executed
	abstract public void execute(int tick);
}
