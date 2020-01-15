public class Enemy{
	public int frame;
	public Point3f shift;
	public Point4f rotate;
	public int health;

	public Enemy(){
		frame = 1;
		health = 100;
		shift = new Point3f(0, -1, 0);
		rotate = new Point4f(0, 0, 0, 0);
	}

	public Enemy(Point3f shift, Point4f rotate){
		frame = 1;
		health = 100;
		this.shift = shift;
		this.rotate = rotate;
	}

	public void updateFrame(int size){
		frame = (frame == size - 1) ? 1 : (frame + 1);
	}

	public void findUser(double x, double y){
		//Graph Theory Part
	}

	public boolean hit(double x, double y){
		//(x, y) is the center point
		//Hit range: circle of radius 0.2
		//Equation: (x-shift.x)^2+(y-shift.z)^2<=0.2^2
		return ((x - shift.x) * (x - shift.x) + (y - shift.z) * (y - shift.z) <= 0.04);
	}
}
