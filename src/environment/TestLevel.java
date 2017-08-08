package environment;

import java.awt.Dimension;
import java.awt.geom.Point2D;

import blocks.GameBlock;
import entities.GameEntity;
import entities.Mouse;
import entities.PGWalker;
import entities.StMV2;
import math.Vector;

public class TestLevel extends GameLevel{

	public TestLevel(){
		
		displayScore = false;
		zoom = 1;
		environmentSize = new Dimension(3200, 2000);

		GameEntity e2 = new StMV2("heyo", 800, 550, 2);
		e2.setWallBounce(true);
		e2.setCOR(1);
		((StMV2) e2).setStock(5);
		addEntity(e2, 0);
		e2.setWallBounce(false);
		myTrackees.add(e2);
		
		Mouse mouse = new Mouse();
		addEntity(mouse, 0);
		mouse.setSummonMass(true);
		
		
		GameEntity e = new PGWalker("heyo", 800, 500, mouse);
		e.setCOR(1); // has to be 1 for portals to work properly, why?	
		e.setGravity(true);
		addEntity(e, 0);
		e.setWallBounce(false);
		myTrackees.add(e);
		
		
		Point2D.Double p1 = new Point2D.Double(10, 10);
		Point2D.Double p2 = new Point2D.Double(10, environmentSize.getHeight()-20);
		Point2D.Double p3 = new Point2D.Double(environmentSize.getWidth()-10, environmentSize.getHeight()-20);
		Point2D.Double p4 = new Point2D.Double(environmentSize.getWidth()-10, 10);

		GameBlock border = new GameBlock(new Point2D.Double[]{p1, p2, p3, p4});
		border.setDrawAsLine(true);
		border.setCOR(0.5);
		addBlock(border, 0);
		
//		for(int i=0; i<1000; i++){
//		GameEntity m = new Missle(randX(), randY(), e, 1200);
//		addEntity(m, i*1000);
//		}
//		
//		for(int i=0; i<100; i++){
//			for(int j=0; j<1+(i/20); j++){
//			GameEntity e7 = new StMRand(randX(),randY(), 51-i/2);
//			e7.setHealth(100+i);
//			if(i>10){
//				e7.setoffScreenWrap(true);
//				e7.setWallBounce(false);
//			}
//			addEntity(e7, i*2000-i*7);
//			}
//		}
////		setTrackee(m);
	
//		for(int i=0; i<100; i++){
//			GameEntity e7 = new StMRand(Math.random()*16000,Math.random()*10000, 51-i/2);
//			e7.setHealth(100+i);
//			if(i>10){
//				e7.setoffScreenWrap(true);
//				e7.setWallBounce(false);
//			}
//			addEntity(e7, i*1000-i*7);
//		}
//		

		
		
//		Point2D.Double[] temp = new Point2D.Double[4];
//		temp[0] = new Point2D.Double(700, 10);
//		temp[1] = new Point2D.Double(800, 533);
//		temp[2] = new Point2D.Double(1200, 700);
//		temp[3] = new Point2D.Double(700, 974);
//
//		addBlock(new GameBlock(temp), 0);
		
//		for(int i=0; i<1000; i++){
//			Point2D.Double[] temp2 = new Point2D.Double[2];
//			
//			double x = Math.random()*environmentSize.getWidth();
//			double y = Math.random()*environmentSize.getHeight();
//			temp2[0] = new Point2D.Double(x, y);
//			temp2[1] = new Point2D.Double(x + (Math.random()*500)-250, y + (Math.random()*500)-250);
//			GameBlock b = new GameBlock(temp2);
//			b.setCOR(Math.random()*1.5);
//			addBlock(b, 0);
//		}

		
//		for(int i=0; i<1000; i++){
//			GameBlock b = new GameBlock(randX(), randY(), 300, 500, Math.random()*Math.PI);
//			addBlock(b, i);
//		}
		
		
		
		for(int i=0; i<10; i++){
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
			GameBlock b = new GameBlock(temp3);
			b.setCOR(0.5);
			addBlock(b, 0);
			
		}
		
//		for(int i =0; i<1; i++){
//			GameEntity blackhole = new DensityMass(centerX(), centerY(), 10000000, 100);
//			blackhole.setWallBounce(true);
//			blackhole.setVel(0, 0);
//			addEntity(blackhole, 0);
//		}
	}
	
	
	@Override
	public boolean gameStart() {
		return true;
	}

	@Override
	public boolean gameOver() {
		return false;
	}

}
