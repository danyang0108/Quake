
public class Colour {
	//rgb colour converter for colour3f method in openGL
	private float r;
	private float g;
	private float b;
	
	public Colour(int r, int g, int b){
		this.r = (float) (r/256.0);
		this.g = (float) (g/256.0);
		this.b = (float) (b/256.0);
	}
	
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
