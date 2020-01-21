///Author: Danyang Wang
//Class: ICS4U
//Date: Jan 12th, 2020
//Instructor: Mr Radulovic
//Assignment name: ICS4U Culminating
/*Description: This interface includes the operations and transformations of 
 * matrices. 
 */
public interface Matrix4f_Interface{
	
	// perform matrix addition 
	public Matrix4f add(Matrix4f m);
	
	// perform matrix subtraction 
	public Matrix4f subtract(Matrix4f m);
	
	// perform matrix multiplication
	public Matrix4f multiply(Matrix4f m);
	
	// creates an identity matrix
	public Matrix4f identity();

	// finds the inverse of the current matrix
	public Matrix4f findInverse();
	
	// represent translation using matrix
	public Matrix4f translate(Point3f p);
	
	// represent rotation using matrix
	public Matrix4f rotate(Point3f p);
	
	// represent scaling using matrix
	public Matrix4f scale(Point3f p);
	
}