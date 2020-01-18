public class Enemy{
	public final int WS = 100, PS = 80, DS = 180;
	public boolean punch = false;
	public boolean dead = false;
	public int WF, PF, DF; //KeyFrame for walk, punch, despawn
	public int choice; //Walk, Punch, Despawn
	public Point3f shift; //Position in map
	public Point4f rotate; //Rotation in map
	public int health;
	private int fixX = 10, fixZ = 12;

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
		this.shift = new Point3f(-shift.x, shift.y, -shift.z);
		this.rotate = rotate;
	}

	public void setChoice(int choice){
		this.choice = choice;
		WF = PF = DF = 1;
	}

	public boolean updateFrame(){
		//0 is walk, 1 is punch, 2 is despawn
		if (choice == 0) WF = (WF == WS - 1) ? 1 : (WF + 1);
		if (choice == 1){
			if (PF == PS - 1){
				//Punching is done; change to move first
				//The main file would determine if the enemy should continue punching
				setChoice(0);
				punch = false;
			}else PF++;
			PF = (PF == PS - 1) ? 1 : (PF + 1);
		}
		if (choice == 2){
			if (DF == DS - 1) return true; //Finished dying
			DF++;
		}
		return false;
	}

	public Point2f findUser(int x, int y) throws Exception{
		//Graph Theory Part
		Point2f EPos = new Point2f(Math.round(shift.z) + fixZ, Math.round(shift.x) + fixX);
		Point2f UPos = new Point2f(x, y);
		BFS RUN = new BFS();
		Point2f next = RUN.bfs(EPos, UPos);
		if (next.x == -1 && next.y == -1){
			//Shouldn't happen
		}
		return new Point2f(next.x, next.y);
	}

	public boolean hit(double x, double y){
		//(x, y) is the center point
		//Hit range: circle of radius 0.2
		return ((x + shift.x) * (x + shift.x) + (y + shift.z) * (y + shift.z) <= 0.04);
	}
}
