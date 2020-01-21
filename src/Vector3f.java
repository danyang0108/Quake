//Author: Danyang Wang
//Class: ICS4U
//Date: Dec 20th, 2019
//Instructor: Mr Radulovic
//Assignment name: ICS4U Culminating
/*Description: This program implements a 3d vector object which contains a magnitude and a 
 * direction. Additionally, it contains the operations between vectors.
 * (Not used because we decided to use legacyGL)
*/
public class Vector3f {

	private float magnitude;
	private Point3f direction;
	private int id; // 0 or 1 depending if it's a point or vector

	// Constructors for Vector3f class
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

	// determines the magnitude of the vector
	public float calcMagnitude(Point3f direction) {
		float m = (float) Math
				.sqrt(Math.pow(direction.getX(), 2) + Math.pow(direction.getY(), 2) + Math.pow(direction.getZ(), 2));
		return m;
	}

	// Vector operations

	// adds two vectors together
	public Vector3f add(Vector3f v){
		Point3f velocity = v.getDirection();
		Point3f newDirection = new Point3f(
		velocity.getX() + direction.getX(),
		velocity.getY() + direction.getY(),
		velocity.getZ() + direction.getZ());
		
		Vector3f newVector = new Vector3f(newDirection);
		return newVector;

	}

	// subtracts two vectors
	public Vector3f subtract(Vector3f v) {
		Point3f newDirection = v.getDirection();
		newDirection.setX(-newDirection.getX());
		newDirection.setY(-newDirection.getY());
		newDirection.setZ(-newDirection.getZ());
		return add(v);

	}

	// scale the vector by a positive constant
	public void scale(float a) {
		if (a > 0) {
			direction.setX(direction.getX()* a);
			direction.setY(direction.getY()* a);
			direction.setZ(direction.getZ()* a);
			set(direction);
		}
	}

	// finds the dot product between two vectors
	public float dotProduct(Vector3f v) {
		float sum = 0;

		sum += v.direction.getX() * direction.getX();
		sum += v.direction.getY() * direction.getY();
		sum += v.direction.getZ() * direction.getZ();

		return sum;

	}

	// finds the cross product of two vectors
	public Vector3f crossProduct(Vector3f v) {
		Point3f newDirection = new Point3f();

		newDirection.setX(direction.getY() * v.direction.getZ() - 
				direction.getZ() * v.direction.getY());
		newDirection.setY(direction.getZ() * v.direction.getX() - 
				direction.getX() * v.direction.getZ());
		newDirection.setZ(direction.getX() * v.direction.getY() - 
				direction.getY() * v.direction.getX());

		Vector3f newVector = new Vector3f(newDirection);
		return newVector;
	}

	// calculates the normal of the current vector
	public Vector3f calcNormal() {
		Point3f newDirection = new Point3f();

		newDirection.setX(direction.getX() / magnitude);
		newDirection.setY(direction.getY() / magnitude);
		newDirection.setZ(direction.getZ() / magnitude);

		Vector3f newVector = new Vector3f(newDirection);
		return newVector;
	}

	// getter and setters for the state variables
	public float getX() {
		return this.direction.getX();
	}

	public float getY() {
		return this.direction.getY();
	}

	public float getZ() {
		return this.direction.getZ();
	}

	public float getId() {
		return id;
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
}
