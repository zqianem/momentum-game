package environment;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Point2D;

import blocks.GameBlock;
import entities.DensityMass;
import entities.GameEntity;
import entities.Mouse;
import entities.StMBasic;
import entities.StMRand;
import entities.StMV2;
import math.Vector;

@SuppressWarnings("unused")
public class TwoPlayerVS extends GameLevel{

	public TwoPlayerVS(){
		
		displayScore = false;

		GameEntity e = new StMV2("heyo", 400, 500);
		addEntity(e, 0);
		
		GameEntity e2 = new StMV2("heyo2", 1200, 500, 2);
		addEntity(e2, 0);
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
