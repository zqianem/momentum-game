package blocks;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;

import entities.GameEntity;
import math.Vector;

public class LinkedTeleportBlock extends GameBlock{
	
	private GameBlock partner;
	
	public LinkedTeleportBlock(Double[] points) {
		super(points);
	}
	
	public LinkedTeleportBlock(double x, double y, double w, double h, double angle){
		super(x, y, w, h, angle);
	}
	
	public void setLink(GameBlock b){
		partner = b;
	}
	
	
	@Override
	public boolean processCollisionWithSide(GameEntity e, int side){

		boolean sup = super.processCollisionWithSide(e, side);
		
		if(sup && partner != null){
			
			double eX = e.getXCoord();
			double eY = e.getYCoord();
			int newBorderIndex = side%partner.mySides.length;
			
			Point2D.Double start = myVertexes[side];
			Vector startToE = new Vector(eX-start.getX(), eY-start.getY());
			
			Vector border = mySides[side];
			Vector newBorder = partner.mySides[newBorderIndex];
			double angle = border.getAngle() - newBorder.getAngle();
			
			//bounces-back again with negative velocity vector to get proper portal effect
			Vector perpendicular = Vector.perpendicularCounterClockwise(border);
			Vector normal = Vector.unit(perpendicular);
			Vector negOriginalVel = new Vector(-e.getXVel(), -e.getYVel());
			Vector newVel = Vector.bounceOff(negOriginalVel, normal, e.getCOR()*cOR);
			newVel = Vector.rotateClockwise(newVel, angle); // then rotates to plane of new portal
			
			double comp1 = Vector.component(border, startToE);
//			double relativeSideLocation = 1 - comp1/Vector.magnitude(border);
			double relativeSideLocation = comp1/Vector.magnitude(border);
			double sideLocationDif = Vector.magnitude(newBorder)*relativeSideLocation - comp1;
			Vector dif = Vector.setMagnitude(newBorder, sideLocationDif);
			
			Vector newCoordRelative = Vector.add(Vector.rotateClockwise(startToE, angle), dif);
			Vector newCoord = Vector.add(newCoordRelative, new Vector(partner.myVertexes[newBorderIndex].getX(), partner.myVertexes[newBorderIndex].getY()));
	
			e.setXVel(newVel.getX());
			e.setYVel(newVel.getY());
			e.setXCoord(newCoord.getX());
			e.setYCoord(newCoord.getY());
		}
		
		return sup && partner != null;
	}
	
	@Override
	public boolean processCollisionWithVertex(GameEntity e, int vertex){

		boolean sup = super.processCollisionWithVertex(e, vertex);
		
		if(sup && partner != null){
			
			//TODO
		}
		
		return sup && partner != null;
	}
}
