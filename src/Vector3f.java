//Author: Danyang Wang
//Class: ICS4U
//Date: Dec 20th, 2019
//Instructor: Mr Radulovic
//Assignment name: ICS4U Culminating
/*Description: This program contains implements a 3d vector object which contains a magnitude and a 
 * direction. Additionally, it contains the operations between vectors.
*/
public class Vector3f {
	
	private float magnitude;
	private Point3f direction;
	private int id;

	public Vector3f() {
		magnitude = 0;
		direction = new Point3f();
		id = 0;
	}

	public Vector3f(Point3f direction) {
		this.direction = direction;
		magnitude = calcMagnitude(direction);
		id = 0;
	}

	public float calcMagnitude(Point3f direction) {
		float m = (float) Math.sqrt(Math.pow(direction.getX(), 2) + Math.pow(direction.getY(), 2) +
				Math.pow(direction.getZ(), 2));
		return m;
	}

	public float getMagnitude() {
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

	
	public void scale(float a) {
		if (a > 0) {
			direction.set(a * direction.getX(), a * direction.getY(), a * direction.getZ());
			set(direction);
		}
	}
	
	public float dotProduct(Vector3f v) {
		float sum = 0;
		
		sum += v.getX() * direction.getX();
		sum += v.getY() * direction.getY();
		sum += v.getZ() * direction.getZ();
		
		return sum;
		
	}
	
	public Vector3f crossProduct(Vector3f v) {
		Point3f newDirection = new Point3f();
		
		newDirection.setX(direction.getY() * v.getZ() - direction.getZ() * v.getY());
		newDirection.setY(direction.getZ() * v.getX() - direction.getX() * v.getZ());
		newDirection.setZ(direction.getX() * v.getY() - direction.getY() * v.getX());
		
		Vector3f newVector = new Vector3f(newDirection);
		return newVector;
	}
	
	public Vector3f calcNormal() {
		Point3f newDirection = new Point3f();
		
		newDirection.setX(direction.getX() / magnitude);
		newDirection.setY(direction.getY() / magnitude);
		newDirection.setZ(direction.getZ() / magnitude);
		
		Vector3f newVector = new Vector3f(newDirection);
		return newVector;
	}
	
	//getter and setter
	public float getX(){
		return this.direction.getX();
	}

	public float getY(){
		return this.direction.getY();
	}
	
	public float getZ(){
		return this.direction.getZ();
	}
	
	public float getId() {
		return id;
	}
	
	/*public static void main(String args[]) {
		Vector3f v = new Vector3f(new Point3f(1,1,1));
		Vector3f v2 = new Vector3f(new Point3f(2,1,-1));
		v.scale(2);
		System.out.println(v.subtract(v2).getDirection().toString());
		System.out.println(v.getMagnitude());
	}*/
}
