package events;

import java.util.List;

import entities.GameEntity;

public class MassMerge extends MergeEvent{

	public MassMerge(List<GameEntity> l) {
		super(l);

		for(GameEntity e : myEntities){
			if(myEntity == null || e.getMass() > myEntity.getMass()){
				myEntity = e; 
			}
		}
	}
	
	public MassMerge(GameEntity e1, GameEntity e2){
		super(e1, e2);
		myEntity = (e1.getMass() > e2.getMass())? e1 : e2;
	}
}
