package environment;

import java.awt.Color;

import entities.DensityMass;
import entities.GameEntity;
import entities.Mouse;
import entities.StMRand;
import entities.StMV2;

@SuppressWarnings("unused")
public class ThreePlayerVS extends GameLevel{

	public ThreePlayerVS(){
		
		displayScore = false;
//      
		GameEntity e = new StMV2("heyo", 400, 500);
		addEntity(e, 0);
		
		GameEntity e2 = new StMV2("heyo2", 800, 500, 2);
		addEntity(e2, 0);
		
		GameEntity e3 = new StMV2("Bashkettle", 1200, 500, 3);
		addEntity(e3, 0);
	}

	@Override
	public boolean gameStart() {
		// TODO Auto-generated method stub
		return true;
	}
	
	@Override
	public boolean gameOver() {
		return myInputEntities.size() == 0;
//		return false;
	}


}
