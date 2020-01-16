public class Enemy{
	public final int WS = 100, PS = 80, DS = 180;
	public boolean dead = false;
	public int WF, PF, DF; //KeyFrame for walk, punch, despawn
	public int choice; //Walk, Punch, Despawn
	public Point3f shift; //Position in map
	public Point4f rotate; //Rotation in map
	public int health;

	public Enemy(){
		choice = 0; //Start with walking
		WF = PF = DF = 1;
		health = 100;
		shift = new Point3f(0, -1, 0);
		rotate = new Point4f(0, 0, 0, 0);
	}

	public Enemy(Point3f shift, Point4f rotate){
		choice = 0;
		WF = PF = DF = 1;
		health = 100;
		this.shift = shift;
		this.rotate = rotate;
	}

	public void setChoice(int choice){
		this.choice = choice;
		WF = PF = DF = 1;
	}

	public boolean updateFrame(){
		//0 is walk, 1 is punch, 2 is despawn
		if (choice == 0) WF = (WF == WS - 1) ? 1 : (WF + 1);
		if (choice == 1) PF = (PF == PS - 1) ? 1 : (PF + 1);
		if (choice == 2){
			System.out.println("FRAME " + DF);
			if (DF == DS - 1){
				//Finished dying
				return true;
			}
			DF++;
		}
		return false;
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
