package optimization;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import blocks.GameBlock;
import entities.CollisionGameEntity;
import entities.GameEntity;
import environment.GameEnvironment;

public class CollisionQuadTreeNode {
	
	final static int MAX_ELEMENTS = 100;
	
	private class GameBlockSide{
		
		private GameBlock myBlock;
		private int sideIndex;
		
		public GameBlockSide(GameBlock b, int index){
			myBlock = b;
			sideIndex = index;
		}
		
		public void processCollision(GameEntity e){
			myBlock.processCollisionWithSide(e, sideIndex);
		}
		
		public Point2D.Double getPoint1(){
			return myBlock.getVertexes()[sideIndex];
			
		}
		
		public Point2D.Double getPoint2(){
			return myBlock.getVertexes()[sideIndex+1];
		}
	}
	
	private class GameBlockVertex{
		
		private GameBlock myBlock;
		private int vertexIndex;
		
		public GameBlockVertex(GameBlock b, int index){
			myBlock = b;
			vertexIndex = index;
		}
		
		public void processCollision(GameEntity e){
			myBlock.processCollisionWithVertex(e, vertexIndex);
		}	
		
		public Point2D.Double getPoint(){return myBlock.getVertexes()[vertexIndex];}
	}
	
	GameEnvironment myEnviron;
	
	List<CollisionGameEntity> myEntities;
	List<GameBlockSide> myBlockSides;
	List<GameBlockVertex> myBlockVertexes;
		
	Rectangle2D.Double area;
	
	
	CollisionQuadTreeNode topLeft;
	CollisionQuadTreeNode topRight;
	CollisionQuadTreeNode bottomRight;
	CollisionQuadTreeNode bottomLeft;
	
	// root constructor
	public CollisionQuadTreeNode(GameEnvironment environ, Rectangle2D.Double area){
		
		myEnviron = environ;
		this.area = area;
		
		myEntities = new LinkedList<CollisionGameEntity>();
		myBlockSides = new LinkedList<GameBlockSide>();
		myBlockVertexes = new LinkedList<GameBlockVertex>();

		
		for(CollisionGameEntity e : myEnviron.getCollisionEntityList())
			myEntities.add(e);
		
		for(GameBlock b : myEnviron.getBlockList())
			for(int i=0; i<b.getSides().length; i++){
				myBlockSides.add(new GameBlockSide(b, i));
				myBlockVertexes.add(new GameBlockVertex(b, i));
			}
		
		if(getElementCount() > MAX_ELEMENTS)
			split();
	}
	
	// default constructor
	private CollisionQuadTreeNode(CollisionQuadTreeNode parent, int quadrant){
		
		myEnviron = parent.myEnviron;
		Rectangle2D.Double pArea = parent.area;
		
		myEntities = new LinkedList<CollisionGameEntity>();
		myBlockSides = new LinkedList<GameBlockSide>();
		myBlockVertexes = new LinkedList<GameBlockVertex>();

		
		double x = pArea.getMinX();
		double y = pArea.getMinY();
		
		// by Cartesian Plane Quadrants
		if(quadrant == 1)
			x = pArea.getCenterX();
			
		else if(quadrant == 2){}

		else if(quadrant == 3)
			y = pArea.getCenterY();
			
		else if(quadrant == 4){
			x = pArea.getCenterX();
			y = pArea.getCenterY();
		}
		
		else{
			System.out.println("h);hafdlkh");
		}
			
		area = new Rectangle2D.Double(x, y, pArea.getWidth()/2, pArea.getHeight()/2);
		
		
		Iterator<CollisionGameEntity> itE = parent.myEntities.iterator();
		Iterator<GameBlockSide> itBS = parent.myBlockSides.iterator();
		Iterator<GameBlockVertex> itBV = parent.myBlockVertexes.iterator();
		
		while(itE.hasNext()){
			CollisionGameEntity e = itE.next();
			if(area.contains(e.getFrame())){
				myEntities.add(e);
				itE.remove();
			}
		}
		
		while(itBS.hasNext()){
			GameBlockSide bS = itBS.next();
			if(area.contains(bS.getPoint1()) && area.contains(bS.getPoint2())){
				myBlockSides.add(bS);
				itBS.remove();
			}	
		}
		
		while(itBV.hasNext()){
			GameBlockVertex bV = itBV.next();
			if(area.contains(bV.getPoint())){
				myBlockVertexes.add(bV);
				itBV.remove();
			}	
		}	
		
		if(getElementCount() > MAX_ELEMENTS)
			split();
	}
	
	private int getElementCount(){
		return myEntities.size()+myBlockSides.size()+myBlockVertexes.size();
	}
	
	private void split(){
		topLeft = new CollisionQuadTreeNode(this, 2);
		topRight = new CollisionQuadTreeNode(this, 1);
		bottomLeft = new CollisionQuadTreeNode(this, 3);
		bottomRight = new CollisionQuadTreeNode(this, 4);
	}
	
	public void processCollisions(){
		List<CollisionGameEntity> empty1 = new LinkedList<CollisionGameEntity>();
		List<GameBlockSide> empty2 = new LinkedList<GameBlockSide>();
		List<GameBlockVertex> empty3 = new LinkedList<GameBlockVertex>();;

		processCollisions(empty1, empty2, empty3);
	}
	
	private void processCollisions(List<CollisionGameEntity> pEntities, 
								   List<GameBlockSide> pBlockSides,
								   List<GameBlockVertex> pBlockVertexes){
		
		for(CollisionGameEntity e : pEntities){	
			
			for(CollisionGameEntity e2 : myEntities)
				CollisionGameEntity.processCollision(e, e2);
			
			for(GameBlockSide s : myBlockSides)
				s.processCollision(e);
			
			for(GameBlockVertex v : myBlockVertexes)
				v.processCollision(e);
		}
		
		for(int i=0; i<myEntities.size()-1; i++)
			for(int j=i+1; j<myEntities.size(); j++)
				CollisionGameEntity.processCollision(myEntities.get(i), myEntities.get(j));
		
		for(CollisionGameEntity e : myEntities){
			
			for(GameBlockSide s : pBlockSides)
				s.processCollision(e);
			
			for(GameBlockVertex v : pBlockVertexes)
				v.processCollision(e);
			
			for(GameBlockSide s : myBlockSides)
				s.processCollision(e);
			
			for(GameBlockVertex v : myBlockVertexes)
				v.processCollision(e);
		}
		

		myEntities.addAll(pEntities);
		myBlockSides.addAll(pBlockSides);
		myBlockVertexes.addAll(pBlockVertexes);
		
		if(topLeft != null)
			topLeft.processCollisions(myEntities, myBlockSides, myBlockVertexes);
		
		if(topRight != null)
			topRight.processCollisions(myEntities, myBlockSides, myBlockVertexes);
		
		if(bottomLeft != null)
			bottomLeft.processCollisions(myEntities, myBlockSides, myBlockVertexes);
		
		if(bottomRight != null)
			bottomRight.processCollisions(myEntities, myBlockSides, myBlockVertexes);
	}
}
