
public interface Matrix4f_Interface{
	
	public Matrix4f add(Matrix4f m);
	
	public Matrix4f subtract(Matrix4f m);
	
	public Matrix4f multiply(Matrix4f m);
	
	public Matrix4f identity();

	public Matrix4f findInverse();
	
	public Matrix4f translate(Point3f p);
	
	public Matrix4f rotate(Point3f p);
	
	public Matrix4f scale(Point3f p);
	
	public String toString();
}