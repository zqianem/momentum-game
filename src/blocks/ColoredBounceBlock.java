package blocks;

import java.awt.Color;
import java.awt.geom.Point2D;

import entities.GameEntity;

public class ColoredBounceBlock extends GameBlock{
	
	Color bounceColor;

	public ColoredBounceBlock(double x, double y, double w, double h, double angle, Color bounceColor) {
		super(x, y, w, h, angle);
		this.bounceColor = bounceColor;
	}
	
	public ColoredBounceBlock(Point2D.Double[] points, Color bounceColor){
		super(points);
		this.bounceColor = bounceColor;
	}
	
	@Override
	public boolean processCollisionWithSide(GameEntity e, int side) {
		boolean toReturn = super.processCollisionWithSide(e, side);
		
		if(toReturn)
			color = bounceColor;
		
		return toReturn;
	}
	
	@Override
	public boolean processCollisionWithVertex(GameEntity e, int vertex) {
		boolean toReturn = super.processCollisionWithVertex(e, vertex);
		
		if(toReturn)
			color = bounceColor;
		
		return toReturn;
	}

}
