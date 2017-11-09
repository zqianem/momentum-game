package graphicsAndInput;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.util.*;
import javax.swing.*;

import blocks.GameBlock;
import entities.*;
import environment.*;
import environment.GameLevel.Trackees;
import optimization.GameEnvironmentTripleBuffer;

@SuppressWarnings("serial")
public class MainGraphics extends JLayeredPane implements Runnable{
	
	final static Dimension RES = new Dimension(1600, 1000); 
	
	// updated from environment
	private double zoom;
	private double xOffset;
	private double yOffset;
	
	private double zoomSpeed;
	
	private GameEnvironment myEnviron;
	private boolean running;
	private Thread animator;
	private List<Point> stars;
	
	private int fps;
	private int fpsCount;
	private long timeCount; // since last fps update
	
	private boolean debug;
	
	private GameEnvironmentTripleBuffer myBuffer;
	
	public MainGraphics(GameEnvironment g){
		
		myEnviron = g;	
		
		fps = 0;
		fpsCount = 0;
		timeCount = 0;
		
		debug = true;
		
		running = true;
		
		myBuffer = myEnviron.getBuffer();
		
        setLayout(new GridBagLayout());
        setDoubleBuffered(false);
        setOpaque(true);
        setBackground(Color.BLACK);
        setPreferredSize(RES);
        setFocusable(true);
        requestFocus(true);
        
        
        // add listeners
        addListenersFromLevel();
        setDisplaySettings();
        
        
        //generate star background
        stars = new LinkedList<Point>();
        double w = g.getDimensions().getWidth();
        double h = g.getDimensions().getHeight();
        int num = (int)(w*h/100000);
        for(int i=0; i < num; i++)
        	stars.add(new Point((int)(Math.random()*w),(int)(Math.random()*h)));
        
	}
	
	public double getZoom() {return zoom;}
	public void setZoom(double zoom) {this.zoom = zoom;}
	public double getXOffset() {return xOffset;}
	public void setXOffset(double xOffset) {this.xOffset = xOffset;}
	public double getYOffset() {return yOffset;}
	public void setYOffset(double yOffset) {this.yOffset = yOffset;}
	
	public boolean isRunning(){return running;}
	public void setRunning(boolean b){running = b;}
	public void setEnvironment(GameEnvironment g){myEnviron = g;}
	
	private void setDisplaySettings(){
		zoom = myEnviron.getLevel().getZoom();
		xOffset = myEnviron.getLevel().getXOffset();
		yOffset = myEnviron.getLevel().getYOffset();
		zoomSpeed = 0.01;
	}
	
	private void updateDisplaySettingsFromTrackee(){
		
		Trackees e = myEnviron.getLevel().getTrackees();
		
		if(e == null)
			return;
		
		double width = myEnviron.getDimensions().getWidth();
		double height = myEnviron.getDimensions().getHeight();
		double screenW = RES.getWidth();
		double screenH = RES.getHeight();
		
		double xOffset = e.getAVGXCoord() - screenW/2/zoom;
		if(xOffset < 0)
			xOffset = 0;
		else if(xOffset > width-screenW/zoom)
			xOffset = width-screenW/zoom;
		
		double yOffset = e.getAVGYCoord() - screenH/2/zoom;
		if(yOffset < 0)
			yOffset = 0;
		else if(yOffset > height - screenH/zoom)
			yOffset = height - screenH/zoom;
		
		this.xOffset = xOffset;
		this.yOffset = yOffset;
	}
	
	private void addListenersFromLevel(){
		List<GameEntity> list = myEnviron.getLevel().getInputEntities();
		for(GameEntity gE : list){
          	if(gE instanceof KeyListener)
        		addKeyListener((KeyListener) gE);
        	if(gE instanceof MouseListener){
        		addMouseListener((MouseListener) gE);
        		((Mouse) gE).setScreen(this);
        	}
        	if(gE instanceof MouseMotionListener){
        		addMouseMotionListener((MouseMotionListener) gE);
        		((Mouse) gE).setScreen(this);
        	}
        }
    }
	
	private int processX(double xCoord){
		return (int)((xCoord - xOffset) * zoom + 0.5);
	}
	
	private int processY(double yCoord){
		return (int)((yCoord - yOffset) * zoom + 0.5);
	}
	
	private int processL(double length){
		return (int)(length*zoom + 0.5);
	}
	
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D) g;
		
		myEnviron = myBuffer.getReadyToRender();
		
		updateDisplaySettingsFromTrackee();
		
	    drawStarBackground(g2);
	    		
	    // paints in reverse order so that highest collision-priority blocks are painted on top		
		List<GameBlock> blocks = myEnviron.getBlockList();
		ListIterator<GameBlock> it = blocks.listIterator(blocks.size());
		
		while(it.hasPrevious())
			drawBlock(g2, it.previous());
		
		for(GameEntity e : myEnviron.getEntityList())
			drawEntity(g2, e);

		drawGUI(g2);
	    
		updateZoom();
	}
	
	private void drawEntity(Graphics2D g2, GameEntity e){
		
	    if(e.getHealth() > 0){
	    	 
	    	 // draw Healthbar
	    	 g2.setColor(Color.RED);
	    	 g2.fillRect(processX(e.getHealthBarTLPoint().getX()), processY(e.getHealthBarTLPoint().getY()), processL(e.getWidth()), processL(5));
	    	 g2.setColor(Color.GREEN);
	    	 g2.fillRect(processX(e.getHealthBarTLPoint().getX()), processY(e.getHealthBarTLPoint().getY()), processL(e.getWidth()*e.getHealth()/e.getMaxHealth()), processL(5));
	     }
	     
	     if(e.drawPath()){
	    	 g2.setColor(Color.GREEN);
	    	 
	    	 //generate a path from path history
	    	 
	    	 LinkedList<double[]> entityHistory = myEnviron.getEntityHistories().get(e.getID());
	    	 int pathLength = entityHistory.size();	// need to have to make sure pathLength isn't the wrong value, for some reason
	    	 
	    	 int[] xPath = new int[pathLength];
	    	 int[] yPath = new int[pathLength];
	    	 
		     //updateDisplaySettings();
		     
	    	 for(int i=0; i<pathLength; i++){
	    		 xPath[i] = processX(entityHistory.get(i)[0]);
	    		 yPath[i] = processX(entityHistory.get(i)[1]);
	    	 }
	    	 
	    	 g2.drawPolyline(xPath, yPath, pathLength);
	     }
	     
	     if(e.drawTail()){
	    	 
	    	 try{
	    	 
	    	 int maxPathLength = 5;
	    	 
	    	 int pathLength = (int) (e.getLifetime()-2);
	    	 if(pathLength > maxPathLength)
	    		 pathLength = maxPathLength;
	    	 
	    	 if(pathLength > 2){ 	 
		    	 LinkedList<double[]> eH = myEnviron.getEntityHistories().get(e.getID());
		    	
		    	 
		    	 int[] xPoints = new int[pathLength*2+1];
		    	 int[] yPoints = new int[pathLength*2+1];
		    	 double tailHeadWidth = e.getHeight();
		 
			     //updateDisplaySettings();
		    	 
		    	 int cur = (int) e.getLifetime() - 1;
		    	 
		    	 for(int i=1; i<pathLength+1; i++){
		    		 
		    		 double xOffset;
		    		 double yOffset;
		    		 
		    		 double halfTailWidth = (tailHeadWidth - (tailHeadWidth*i/pathLength))/2;
		    		 
		    		 // difference from point ahead and behind current point
		    		 double deltaX = eH.get(cur-i+1)[0] - eH.get(cur-i-1)[0]; // some weird error happens here very rarely
		    		 double deltaY = eH.get(cur-i+1)[1] - eH.get(cur-i-1)[1];
		    		 
		    		 // find the proper angle and offset to draw the tail
		    		 double offsetAngle = Math.atan2(-deltaX, deltaY);
		    		 xOffset = halfTailWidth*Math.cos(offsetAngle);
		    		 yOffset = halfTailWidth*Math.sin(offsetAngle);
		    		 
		    		 
		    		 
		    		 xPoints[i-1] = processX(eH.get(cur-i)[0] + xOffset);
		    		 yPoints[i-1] = processY(eH.get(cur-i)[1] + yOffset);
		    		 xPoints[pathLength*2-i] = processX(eH.get(cur-i)[0] - xOffset);
		    		 yPoints[pathLength*2-i] = processY(eH.get(cur-i)[1] - yOffset);
		    		 
		    	 }
		    	 
		    	 xPoints[pathLength*2] = processX(eH.get(cur)[0]);
		    	 yPoints[pathLength*2] = processY(eH.get(cur)[1]);
		    	 
		    	 // draw the tail
		    	 Color c = e.getColor();
		    	 g2.setColor(c);
	    		 g2.drawPolygon(xPoints, yPoints, pathLength*2+1);
		    	 g2.setColor(new Color(c.getRed(), c.getGreen() , c.getBlue(), 100));
	    		 g2.fillPolygon(xPoints, yPoints, pathLength*2+1);
	    	 }
	    	 }catch(Exception ex){System.out.println("The weird tail-painting nullpointer error happened again\n" + e);}
	     } 
	     
	     // draw the actual entity
    	 g2.setColor(e.getColor());
	     g2.fillOval(processX(e.getTLPoint().getX()), processY(e.getTLPoint().getY()), processL(e.getWidth()), processL(e.getHeight()));
	     Toolkit.getDefaultToolkit().sync(); // for smoother updates in Linux
	}
	
	private void drawGUI(Graphics2D g2){
		Stroke original = g2.getStroke();

				
		// draw stock circles
		int yPos = (int) (RES.getHeight() - 36);
		int xPos = 36;
		
		g2.setStroke(new BasicStroke(4));
		
		for(StMV2 stm : myEnviron.getStockEntityList()){
			 if(stm.getStock() > 0){
				 g2.setColor(stm.getOriginalColor());
				 for(int i=0; i<stm.getStock(); i++){
					 g2.drawOval(xPos, yPos, 20, 20);
					 xPos += 36;
				 }
			 }
		}
		
		// draw score
		GameLevel level = myEnviron.getLevel();
		if(level.getDisplayScore()){
			
			yPos += 20;
			xPos = (int) (RES.getWidth() - 25);
			
			g2.setColor(Color.ORANGE);
			g2.setFont(new Font(Font.MONOSPACED, Font.BOLD, 60));
			String s = "0000000"+level.getScore();

			if(s.length()>8)
			 s=s.substring(s.length()-8, s.length());
			int len = s.length();
			xPos -= len*35;
			g2.drawString(s, xPos, yPos);
		}
		
		if(debug){
			xPos = RES.width - 115;
			yPos = 36;
			
			g2.setColor(Color.GREEN);
			Font debugFont = new Font(Font.MONOSPACED, Font.ITALIC, 20);
			g2.setFont(debugFont);
			
			String f = fps + "";
			while(f.length() < 3)
				f = " " + f;
			String f2 = "FPS: " + f;
			
			String e = myEnviron.getEntityCount() + "";
			while(e.length() < 3)
				e = " " + e;
			String e2 = "Entities: " + e;
			
			int blockSideSum = 0;
			

			for(GameBlock b : myEnviron.getBlockList())
				blockSideSum += b.getSides().length;
				
			String b = blockSideSum + "";
			while(b.length() < 3)
				b = " " + b;
			String b2 = "Block Sides: " + b;
			
			
//			if(pixelsOffscreen(t) >= 0)
//				g2.drawString("Trackee is off screen", xPos-400, yPos);
//			else
//				g2.drawString("Trackee is on screen", xPos-400, yPos);
			
			g2.drawString(""+maxPixelsOffscreen(), xPos-400, yPos);
			
			g2.drawString(f2, xPos, yPos);
			g2.drawString(e2, xPos-60, yPos + 25);
			g2.drawString(b2, xPos-96, yPos + 50);

			
			
		}
			
		
		
		
		
		
		
	    g2.setStroke(original);

	    // draws a red cross
//		g2.setColor(Color.RED);
//		g2.drawLine(RES.width/2, 0, RES.width/2, RES.height);
//		g2.drawLine(0, RES.height/2, RES.width, RES.height/2);
	    
	    

	}
	
	private void drawBlock(Graphics2D g2, GameBlock b){
				
		Point2D.Double[] temp = b.getVertexes();
		int num = temp.length;
		int[] xPoints = new int[num];
		int[] yPoints = new int[num];
		
		for(int i=0; i<num; i++){
			xPoints[i] = processX(temp[i].getX());
			yPoints[i] = processY(temp[i].getY());
		}
		
		g2.setColor(b.getColor());
		g2.drawPolygon(xPoints, yPoints, num);

		if(!b.drawAsLine()){
			g2.fillPolygon(xPoints, yPoints, num);
		}
		

	}
	
	private void drawStarBackground(Graphics2D g2){
		g2.setColor(Color.GRAY);

		for(Point p : stars){
			g2.fillRect(processX(p.getX()), processY(p.getY()), processL(7), processL(7));
		}
	}
	 
	//closest distance entity is to edge of screen, positive values indicate entity is off-screen
	private double pixelsOffscreen(GameEntity e) {
		
		double x = processX(e.getXCoord());
		double y = processY(e.getYCoord());
		double w = RES.getWidth();
		double h = RES.getHeight();
		
		double distN = -y;
		double distS = y-h;
		double distE = -x;
		double distW = x-w;
		
		double toReturn = distN;
		double[] dists = new double[] {distS, distE, distW};
		
		for(double d : dists)
			if(Math.abs(d) < Math.abs(toReturn))
				toReturn = d;
	
		return toReturn;
	}
	
	// the distance the furthest trackee is offscreen
	private double maxPixelsOffscreen() {
		Set<GameEntity> es = myEnviron.getLevel().getTrackees().getSet();
		double max = pixelsOffscreen((GameEntity) es.toArray()[0]);
		for(GameEntity e : es) {
			double dist = pixelsOffscreen(e);
			if(dist > max)
				max = dist;
		}
		return max;
	}
	
	private void updateZoom() {
		double m = maxPixelsOffscreen();
		
		if(m > 0)
			zoom -= zoomSpeed;
	}
	
	
	// called when MainPanel is added to the JFrame
    @Override
    public void addNotify() {
        super.addNotify();

        animator = new Thread(this);
        myEnviron.getThread().start();
        try {Thread.sleep(100);} catch (InterruptedException e) {}
        animator.start();

        
    }
	
	// runs in animator thread
	public void run(){
		
		long cur = System.nanoTime();
		long dif = 0;
			
        // run graphics in loop
        while(true){
        	
        	cur = System.nanoTime();

			timeCount += dif;
        	
        	if(timeCount >= 1000000000){
        		fps = fpsCount;
        		fpsCount = 0;
        		timeCount = timeCount%1000000000;
        	}
        	
        	fpsCount ++;

        	paintImmediately(0, 0, RES.width, RES.height);
            try{Thread.sleep(2);}catch(Exception e){}


			dif = System.nanoTime() - cur;
        }
	}
}
