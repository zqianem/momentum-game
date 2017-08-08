package environment;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Point2D;

import blocks.ColoredBounceBlock;
import blocks.GameBlock;
import entities.DensityMass;
import entities.GameEntity;
import entities.Mouse;
import entities.StMBasic;
import entities.StMRand;
import entities.StMV2;
import math.Vector;

@SuppressWarnings("unused")
public class SpeedrunWIP extends GameLevel{

	public SpeedrunWIP(){
		
		displayScore = false;
		zoom = 0.5;
		environmentSize = new Dimension(3200, 200000);
		
		GameEntity e = new StMV2("heyo", centerX()*0.5, centerY());
		e.setCOR(0.5);
		e.setYAccel(400);
		addEntity(e, 0);
		
		myTrackees.add(e);
		
		for(int i=0; i<100; i++){
			double x0 = Math.random()*environmentSize.getWidth();
			double y0 = Math.random()*environmentSize.getHeight();
			double x1;
			double y1;
			double x2;
			double y2;
			
			double angle1 = Math.PI*2*Math.random();
			double angle2 = angle1 + Math.PI*Math.random();
			
			Vector v1 = new Vector(angle1, Math.random()*300+300, null);
			Vector v2 = new Vector(angle2, Math.random()*300+300, null);

			x1 = x0 + v1.getX();
			y1 = y0 + v1.getY();
			x2 = x1 + v2.getX();
			y2 = y1 + v2.getY();
		
			Point2D.Double[] temp3 = new Point2D.Double[3];
			temp3[0] = new Point2D.Double(x0, y0);
			temp3[1] = new Point2D.Double(x1, y1);
			temp3[2] = new Point2D.Double(x2, y2);
			ColoredBounceBlock b = new ColoredBounceBlock(temp3, Color.WHITE);
			b.setCOR(0.5);
			addBlock(b, 0);
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
