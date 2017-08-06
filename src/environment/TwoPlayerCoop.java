package environment;

import java.awt.Color;

import entities.DensityMass;
import entities.GameEntity;
import entities.Mouse;
import entities.StMRand;
import entities.StMV2;

@SuppressWarnings("unused")
public class TwoPlayerCoop extends GameLevel{

	public TwoPlayerCoop(){
		
		displayScore = true;

		GameEntity e = new StMV2("heyo", 400, 500);
		addEntity(e, 0);
		
		GameEntity e2 = new StMV2("heyo2", 800, 500, 2);
		addEntity(e2, 0);
		
		for(int i=0; i<100; i++){
			GameEntity e7 = new StMRand(Math.random()*1600,Math.random()*1000, 51-i/2);
			e7.setHealth(100+i);
			if(i>10){
				e7.setoffScreenWrap(true);
				e7.setWallBounce(false);
			}
			addEntity(e7, i*1000-i*7);
		}
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
