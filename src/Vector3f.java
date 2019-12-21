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
		direction = new Point3f(0, 0, 0);
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
}
