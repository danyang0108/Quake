//Author: Danyang Wang
//Class: ICS4U
//Date: Dec 20th, 2019
//Instructor: Mr Radulovic
//Assignment name: ICS4U Culminating
/*Description: This program contains implements a 3d point object of the form (x,y,z) as well as 
 * operations such as subtracting two points. 
*/
public class Point3f {
	
	private float x;
	private float y;
	private float z;
	private int id;
	
	public Point3f(){
		x = 0;
		y = 0;
		z = 0;
		id = 1;
	}
	public Point3f(float x, float y, float z){
		this.x = x;
		this.y = y;
		this.z = z;
		id = 1;
	}
	
	// Point-point subtraction 
	public Vector3f subtract(Point3f p){
		
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
	
	// Prints the coordinates of the Point3f object
	public String toString() {
		String result = "x: "+ x + " y: " + y + " z: " + z;
		return result;
	}
	
	// getter and setter
	public float getX() {
		return x;
	}
	
	public float getY() {
		return y;
	}
	
	public float getZ() {
		return z;
	}
	
	public int getId() {
		return id;
	}
	public void setX(float x) {
		this.x = x;
	}
	
	public void setY(float y) {
		this.y = y;
	}
	
	public void setZ(float z) {
		this.z = z;
	}
	
	public void set(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
}
