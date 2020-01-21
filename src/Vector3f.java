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
				.sqrt(Math.pow(direction.x, 2) + Math.pow(direction.y, 2) + Math.pow(direction.z, 2));
		return m;
	}

	// Vector operations

	// adds two vectors together
	public Vector3f add(Vector3f v){
		Point3f velocity = v.getDirection();
		Point3f newDirection = new Point3f();

		newDirection.x = velocity.x + direction.x;
		newDirection.y = velocity.y + direction.y;
		newDirection.z = velocity.z + direction.z;

		Vector3f newVector = new Vector3f(newDirection);
		return newVector;

	}

	// subtracts two vectors
	public Vector3f subtract(Vector3f v) {
		Point3f newDirection = v.getDirection();
		newDirection.x = -newDirection.x;
		newDirection.y = -newDirection.y;
		newDirection.z = -newDirection.z;
		return add(v);

	}

	// scale the vector by a positive constant
	public void scale(float a) {
		if (a > 0) {
			direction.x *= a;
			direction.y *= a;
			direction.z *= a;
			set(direction);
		}
	}

	// finds the dot product between two vectors
	public float dotProduct(Vector3f v) {
		float sum = 0;

		sum += v.direction.x * direction.x;
		sum += v.direction.y * direction.y;
		sum += v.direction.z * direction.z;

		return sum;

	}

	// finds the cross product of two vectors
	public Vector3f crossProduct(Vector3f v) {
		Point3f newDirection = new Point3f();

		newDirection.x = direction.y * v.direction.z - direction.z * v.direction.y;
		newDirection.y = (direction.z * v.direction.x - direction.x * v.direction.z);
		newDirection.z = (direction.x * v.direction.y - direction.y * v.direction.x);

		Vector3f newVector = new Vector3f(newDirection);
		return newVector;
	}

	// calculates the normal of the current vector
	public Vector3f calcNormal() {
		Point3f newDirection = new Point3f();

		newDirection.x = direction.x / magnitude;
		newDirection.y = direction.y / magnitude;
		newDirection.z = direction.z / magnitude;

		Vector3f newVector = new Vector3f(newDirection);
		return newVector;
	}

	// getter and setters for the state variables
	public float getX() {
		return this.direction.x;
	}

	public float getY() {
		return this.direction.y;
	}

	public float getZ() {
		return this.direction.z;
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
