//Author: Danyang Wang
//Class: ICS4U
//Date: Dec 20th, 2019
//Instructor: Mr Radulovic
//Assignment name: ICS4U Culminating
/*Description: This program implements a 3d point object of the form (x,y,z) as well as 
 * operations such as subtracting two points. 
*/
public class Point3f{
	public float x;
	public float y;
	public float z;
	
	//Constructors for Point3f class
	public Point3f(){
		x = 0;
		y = 0;
		z = 0;
	}
	public Point3f(float x, float y, float z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	// Point-point subtraction 
	public Vector3f subtract(Point3f p){
		Point3f newPoint = new Point3f();
		newPoint.x = p.x - x;
		newPoint.y = p.y - y;
		newPoint.z = p.z - z;
		return new Vector3f(newPoint);
	}
	
	// Point-vector addition
	public Point3f add(Vector3f v){
		Point3f velocity = v.getDirection();
		Point3f newPoint = new Point3f();
		newPoint.x = velocity.x + x;
		newPoint.y = velocity.y + y;
		newPoint.z = velocity.z + z;
		return newPoint;
	}
	
	// Prints the coordinates of the Point3f object
	public String toString(){
		String result = "x: "+ x + " y: " + y + " z: " + z;
		return result;
	}
}
