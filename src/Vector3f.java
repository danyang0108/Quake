//Author: Danyang Wang
//Class: ICS4U
//Date: Dec 20th, 2019
//Instructor: Mr Radulovic
//Assignment name: ICS4U Culminating
/*Description: This program contains implements a 3d vector object which contains a magnitude and a 
 * direction. Additionally, it contains the operations between vectors.
*/
public class Vector3f {
	
	private double magnitude;
	private Point3f direction;
	
	public Vector3f() {
		magnitude = 0;
		direction = new Point3f();
	}
	
	public Vector3f(Point3f direction) {
		this.direction = direction;
		magnitude = calcMagnitude(direction);
	}
	
	public double calcMagnitude(Point3f direction) {
		double m = Math.sqrt(Math.pow(direction.getX(), 2) + Math.pow(direction.getY(), 2) + 
				Math.pow(direction.getZ(), 2));
		return m;
	}
	
	public double getMagnitude() {
		return magnitude;
	}
	
	public Point3f getDirection() {
		return direction;
	}
	
	public void set(Point3f direction) {
		this.direction = direction;
		magnitude = calcMagnitude(direction);
	}
	
	public Vector3f add(Vector3f v) {
		Point3f velocity = v.getDirection();
		Point3f newDirection = new Point3f();
		
		newDirection.setX(velocity.getX() + direction.getX());
		newDirection.setY(velocity.getY() + direction.getY());
		newDirection.setZ(velocity.getZ() + direction.getZ());
		
		Vector3f newVector = new Vector3f(newDirection);
		return newVector;
		
	}
	
	public Vector3f subtract(Vector3f v) {
		Point3f newDirection = v.getDirection();
		
		newDirection.set(newDirection.getX() * -1, newDirection.getY() * -1, newDirection.getZ() * -1);
		return add(v);
		
	}
	
	public void scale(double a) {
		direction.set(a * direction.getX(), a * direction.getY(), a * direction.getZ());
		set(direction);
	}
	
	
	
	
	/*public static void main(String args[]) {
		Vector3f v = new Vector3f(new Point3f(1,1,1));
		Vector3f v2 = new Vector3f(new Point3f(2,1,-1));
		v.scale(2);
		System.out.println(v.subtract(v2).getDirection().toString());
		System.out.println(v.getMagnitude());
	}*/
}
