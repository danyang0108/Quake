//Author: Danyang Wang
//Class: ICS4U
//Date: Dec 20th, 2019
//Instructor: Mr Radulovic
//Assignment name: ICS4U Culminating
/*Description: This program contains implements a 3d point object of the form (x,y,z) as well as 
 * operations such as subtracting two points. 
*/
public class Point3f {
	
	private double x;
	private double y;
	private double z;
	
	public Point3f() {
		x = 0;
		y = 0;
		z = 0;
	}
	public Point3f(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	// Point-point subtraction 
	public Vector3f subract(Point3f p) {
		
		Point3f newPoint = new Point3f();
		
		newPoint.setX(p.getX() - x);
		newPoint.setY(p.getY() - y);
		newPoint.setZ(p.getZ() - z);
		
		Vector3f newVector = new Vector3f(newPoint);
		return newVector;
		
	}
	
	// Point-vector addition
	public Point3f add(Vector3f v) {
		
		Point3f velocity = v.getDirection();
		Point3f newPoint = new Point3f();
		
		newPoint.setX(velocity.getX() + x);
		newPoint.setY(velocity.getY() + y);
		newPoint.setZ(velocity.getZ() + z);
		
		return newPoint;
	}
	
	// getter and setter
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public double getZ() {
		return z;
	}
	
	public void setX(double x) {
		this.x = x;
	}
	
	public void setY(double y) {
		this.y = y;
	}
	
	public void setZ(double z) {
		this.z = z;
	}
	
	public void set(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
}
