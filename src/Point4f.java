///Author: Ethan Zhang
//Class: ICS4U
//Date: Jan 2nd, 2020
//Instructor: Mr Radulovic
//Assignment name: ICS4U Culminating
/*Description: This class creates point4f objects that stores the rotation 
 * and (x,y,z) coordinates. 
*/
public class Point4f{
    public float rot;
    public float x, y, z;
    public Point4f(float rot, float x, float y, float z){
        this.rot = rot;
        this.x = x;
        this.y = y;
        this.z = z;
    }
	public float getRot() {
		return rot;
	}
	public void setRot(float rot) {
		this.rot = rot;
	}
	public float getX() {
		return x;
	}
	public void setX(float x) {
		this.x = x;
	}
	public float getY() {
		return y;
	}
	public void setY(float y) {
		this.y = y;
	}
	public float getZ() {
		return z;
	}
	public void setZ(float z) {
		this.z = z;
	}
}
