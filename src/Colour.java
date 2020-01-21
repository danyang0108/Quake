///Author: Danyang Wang
//Class: ICS4U
//Date: Jan 5th, 2020
//Instructor: Mr Radulovic
//Assignment name: ICS4U Culminating
/*Description: This class stores the Colour based on rgb value. Note
 * that rgb values are floats since I am using the colour3f method in
 * lwjgl 3.
*/
public class Colour {
	
	private float r;
	private float g;
	private float b;
	
	//Constructor (changes rgb value into float)
	public Colour(int r, int g, int b){
		this.r = (float) (r/256.0);
		this.g = (float) (g/256.0);
		this.b = (float) (b/256.0);
	}
	
	//getter and setter
	public float getR() {
		return r;
	}

	public void setR(float r) {
		this.r = r;
	}

	public float getG() {
		return g;
	}

	public void setG(float g) {
		this.g = g;
	}

	public float getB() {
		return b;
	}

	public void setB(float b) {
		this.b = b;
	}
}
