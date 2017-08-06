package blocks;

import java.awt.Color;
import java.awt.geom.Point2D;

import entities.GameEntity;
import math.Vector;

public class GameBlock implements Cloneable{
	
	Point2D.Double[] myVertexes;
	Vector[] mySides;
	
	boolean[] disabledVertexes;
	boolean[] disabledSides;
	
	boolean drawAsLine;
	
	Color originalColor;
	Color color;
	double cOR;
	
	private GameBlock(){
		drawAsLine = false;
		
		originalColor = Color.BLUE;
		color = originalColor;
		cOR = 1;
	}
	
	public GameBlock(double x, double y, double w, double h, double angle){
		this();
		
		myVertexes = new Point2D.Double[5];
		
		Point2D.Double origin = new Point2D.Double(x, y);
		
		
		myVertexes[0] = origin;
		myVertexes[1] = new Point2D.Double(x += w*Math.cos(angle), y += w*Math.sin(angle));
		angle += Math.PI/2;
		myVertexes[2] = new Point2D.Double(x += h*Math.cos(angle), y += h*Math.sin(angle));
		angle += Math.PI/2;
		myVertexes[3] = new Point2D.Double(x += w*Math.cos(angle), y += w*Math.sin(angle));
		myVertexes[4] = origin;
		
		createVectorsFromVertexes();
	}
			
	public GameBlock(Point2D.Double[] points){
		this();
		
		int num = points.length;
		
		if(num < 2)
			return; //TODO throw an error
		
		if(points[0] == points[num-1]){
			myVertexes = new Point2D.Double[num];
			for(int i=0; i<num; i++)
				myVertexes[i] = points[i];
			num--;
		}
		
		else{
			// duplicate starting point
			myVertexes = new Point2D.Double[num+1];
			
			for(int i=0; i<num; i++)
				myVertexes[i] = points[i];
			
			myVertexes[num] = points[0];
		}

		createVectorsFromVertexes();
	}
	
	private void createVectorsFromVertexes(){
		int num = myVertexes.length-1;
		
		mySides = new Vector[num];
		
		// create vectors to connect all points
		for(int i=0; i<num; i++){
			double deltaX = myVertexes[i+1].getX() - myVertexes[i].getX();
			double deltaY = myVertexes[i+1].getY() - myVertexes[i].getY();
			mySides[i] = new Vector(deltaX, deltaY);
		}
		
		disabledSides = new boolean[mySides.length];
		disabledVertexes = new boolean[myVertexes.length];
	}
	
	public Point2D.Double[] getVertexes(){return myVertexes;}
	public Vector[] getSides(){return mySides;}
	public Color getColor(){return color;}
	public Color getOriginalColor(){return originalColor;}
	public double getCOR(){return cOR;}
	public boolean drawAsLine(){return drawAsLine;}
	
	public void setColor(Color c){originalColor = c; color = c;}
	public void setTempColor(Color c){color = c;}
	public void setCOR(double c){cOR = c;}
	public void setDrawAsLine(boolean b){drawAsLine = b;}
	
	// disabled sides or vertexes don't process collisions, and are thus only there graphically
	// use this to make "one-way" blocks or secret doors and what not
	public void disableSide(int i){disabledSides[i] = true;}
	public void enableSide(int i){disabledSides[i] = false;}
	public void disableVertex(int i){disabledVertexes[i] = true;}
	public void enableVertex(int i){disabledVertexes[i] = false;}
	

	//apparently this isn't being used
//	public boolean processCollision(GameEntity e){
//
//		boolean toReturn = false;
//		
//		//check for collisions with sides
//		for(int i=0; i<mySides.length; i++)
//			if(processCollisionWithSide(e, i))
//				toReturn = true;
//		
//		// check for collisions with points
//		for(int i=0; i<myVertexes.length-1; i++)
//			if(processCollisionWithVertex(e, i))
//				toReturn = true;
//		
//		return toReturn;
//	}
	
	public boolean processCollisionWithSide(GameEntity e, int side){
		
		if(disabledSides[side])
			return false;
		
		double eX = e.getXCoord();
		double eY = e.getYCoord();
		Vector originalVel = new Vector(e.getXVel(), e.getYVel());
		
		Vector border = mySides[side];
		Vector perpendicular = Vector.perpendicularCounterClockwise(border);
		
		if(Vector.component(perpendicular, originalVel) < 0)
			return false;
		
		Point2D.Double start = myVertexes[side];
		Vector startToE = new Vector(eX-start.getX(), eY-start.getY());
		
		Point2D.Double end = myVertexes[side+1];
		Vector endToE = new Vector(eX-end.getX(), eY-end.getY());
		
		double comp1 = Vector.component(border, startToE);
		double comp2 = Vector.component(border, endToE);
		
		if(comp1 < 0 || comp2 > 0)
			return false;
		
		double distance = -Vector.component(perpendicular, startToE); //I have no idea why this is negative
		double distanceAlongNormalPerTick = Vector.component(perpendicular, originalVel)/GameEntity.METERS_PER_X_TICKS;
		
		
		if(distance < 0 || distance > e.getWidth()/2 + distanceAlongNormalPerTick)
			return false;
				
		Vector normal = Vector.unit(perpendicular);
		Vector newVel = Vector.bounceOff(originalVel, normal, e.getCOR()*cOR);
		
		e.setXVel(newVel.getX());
		e.setYVel(newVel.getY());
		
		//enforce minimum distance //TODO make this a method
		Vector projN = Vector.unit(Vector.projection(perpendicular, startToE));
		Vector locationDif = Vector.scalarMultiply(projN, e.getWidth()/2 - distance);
		e.setXCoord(e.getXCoord()+locationDif.getX());
		e.setYCoord(e.getYCoord()+locationDif.getY());
		
		e.setContactBlock(this);
		e.setContactBlockSideNum(side);
		
		return true;
	}
	
	public boolean processCollisionWithVertex(GameEntity e, int vertex){
		
		if(disabledVertexes[vertex])
			return false;
		
		double eX = e.getXCoord();
		double eY = e.getYCoord();
		Vector originalVel = new Vector(e.getXVel(), e.getYVel());
		
		Vector pointToE = new Vector(eX-myVertexes[vertex].getX(), eY-myVertexes[vertex].getY());

		//why is this negative again, idk
		double distanceAlongNormalPerTick = -Vector.component(pointToE, originalVel)/GameEntity.METERS_PER_X_TICKS;
		
		
		if(Vector.magnitude(pointToE) > e.getWidth()/2 + distanceAlongNormalPerTick)
			return false;

		Vector normal = Vector.unit(pointToE);
		Vector newVel = Vector.bounceOff(originalVel, normal, e.getCOR()*cOR);
		
		e.setXVel(newVel.getX());
		e.setYVel(newVel.getY());
		
		//enforce minimum distance
		Vector locationDif = Vector.scalarMultiply(normal, e.getWidth()/2 - Vector.magnitude(pointToE));
		e.setXCoord(e.getXCoord()+locationDif.getX());
		e.setYCoord(e.getYCoord()+locationDif.getY());
		e.setContactBlock(this);
		e.setContactBlockVertexNum(vertex);

		
		return true;
	}

	@Override
	public GameBlock clone(){
		GameBlock c = new GameBlock(myVertexes);
		c.drawAsLine = drawAsLine;
		c.originalColor = new Color(originalColor.getRGB());
		c.color = new Color(color.getRGB());
		return c;
	}
}
