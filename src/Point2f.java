///Author: Ethan Zhang
//Class: ICS4U
//Date: Jan 2nd, 2020
//Instructor: Mr Radulovic
//Assignment name: ICS4U Culminating
/*Description: This class creates point2f objects, which is used for BFS.
*/
public class Point2f{
    private int x, z; //The two values being stored.
    //Note: the two variables are named "x" and "z". This is because this class
    //is used mostly for 3D coordinates. Thus, the "y" value, representing the height
    //in the 3D world, is rarely used. In order to avoid confusion, I named the variables
    //accordingly.
    public Point2f(int x, int z){
        this.x = x;
        this.z = z;
    }

    //Getters for the variables
    public int getX() {
    	return x;
    }
    
    public int getZ() {
    	return z;
    }
}
