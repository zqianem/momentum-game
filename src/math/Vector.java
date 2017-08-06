package math;

public class Vector{
	
	private double x;
	private double y;
	
	public Vector(double x, double y){
		this.x = x;
		this.y = y;
	}
	
	public Vector(double angle, double length, Object thisDoesNothing){
		x = Math.cos(angle)*length;
		y = Math.sin(angle)*length;
	}
	
	public static Vector add(Vector a, Vector b){
		return new Vector(a.x+b.x, a.y+b.y);
	}
	
	public static Vector subtract(Vector a, Vector b){
		return new Vector(a.x-b.x, a.y-b.y);
	}
	
	public static double magnitude(Vector v){
		return Math.sqrt(Vector.magnitudeSquared(v));
	}
	
	public static double magnitudeSquared(Vector v){
		return v.x*v.x + v.y*v.y;
	}
	
	public static Vector scalarMultiply(Vector v, double s){
		return new Vector(v.x*s, v.y*s);
	}
	
	public static double dotProduct(Vector a, Vector b){
		return a.x*b.x + a.y*b.y;
	}
	
	// component of 'b' in 'a' direction
	public static double component(Vector a, Vector b){
		return Vector.dotProduct(a, b)/Vector.magnitude(a);
	}
	
	// likewise
	public static Vector projection(Vector a, Vector b){
		return Vector.scalarMultiply(a, Vector.dotProduct(a, b)/Vector.magnitudeSquared(a));
	}
	
	public static Vector perpendicularClockwise(Vector v){
		return new Vector(v.y, -v.x);
	}
	
	public static Vector perpendicularCounterClockwise(Vector v){
		return new Vector(-v.y, v.x);
	}
	
	public static Vector negative(Vector v){
		return new Vector(-v.x, -v.y);
	}
	
	public static Vector bounceOff(Vector originalVel, Vector normal){
		return Vector.add(Vector.scalarMultiply(normal, -2*Vector.dotProduct(normal, originalVel)), originalVel);
	}
	
	public static Vector bounceOff(Vector originalVel, Vector normal, double cOR){
		return Vector.add(Vector.scalarMultiply(normal, -(1+cOR)*Vector.dotProduct(normal, originalVel)), originalVel);
	}
	
	public static Vector unit(Vector v){
		return Vector.scalarMultiply(v, 1/Vector.magnitude(v));
	}
	
	public static Vector setMagnitude(Vector v, double mag){
		return Vector.scalarMultiply(v, mag/Vector.magnitude(v));
	}
	
	// in this coordinate system, this works
	public static Vector rotateClockwise(Vector v, double theta){
		double sin = Math.sin(theta);
		double cos = Math.cos(theta);
		return new Vector(v.x*cos - v.y*sin, v.x*sin + v.y*cos);
	}
	
	public double getX(){return x;}
	public double getY(){return y;}
	public double getAngle(){return Math.atan2(x, y);}
}
