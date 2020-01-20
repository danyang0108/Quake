public class Enemy extends Entity{
	private final int WS = 100, PS = 80, DS = 180;
	private boolean punch = false;
	private boolean dead = false;
	private int WF, PF, DF; //KeyFrame for walk, punch, despawn
	private int choice; //Walk, Punch, Despawn
	private Point3f shift; //Position in map
	private Point4f rotate; //Rotation in map
	private int health;
	private int walk; //For movement
	private double moveX, moveZ;

	public Enemy(Point3f shift){
		choice = 0;
		WF = PF = DF = 1;
		health = 100;
		walk = 0;
		this.shift = new Point3f(-shift.x, shift.y, -shift.z);
	}

	public Enemy(Point4f rotate){
		choice = 0;
		WF = PF = DF = 1;
		health = 100;
		walk = 0;
		this.shift = new Point3f(0, 0, 0);
		this.rotate = rotate;
	}

	public Enemy(Point3f shift, Point4f rotate){
		choice = 0;
		WF = PF = DF = 1;
		health = 100;
		walk = 0;
		this.shift = new Point3f(-shift.x, shift.y, -shift.z);
		this.rotate = rotate;
	}

	public void setChoice(int choice){
		this.choice = choice;
		WF = PF = DF = 1;
	}

	public boolean updateFrame(){
		walk = (walk == 20) ? 0 : (walk + 1);
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

	public Point2f findUser(int x, int z) throws Exception{
		//Graph Theory Part
		int fixZ = 12, fixX = 10;
		Point2f EPos = new Point2f(Math.round(shift.z) + fixZ, Math.round(shift.x) + fixX);
		Point2f UPos = new Point2f(x, z);
		BFS RUN = new BFS();
		Point2f next = RUN.bfs(EPos, UPos);
		if (next.x == -1 && next.z == -1){
			//Shouldn't happen
			System.out.println("RIP");
			return null;
		}
		return new Point2f(next.z - fixX, next.x - fixZ);
	}

	public boolean hit(double x, double z){
		//(x, y) is the center point
		//Hit range: circle of radius 0.2
		return ((x + shift.x) * (x + shift.x) + (z + shift.z) * (z + shift.z) <= 0.04);
	}

	public void turnToUser(){
		if (moveX == 0 && moveZ == -0.05) rotate = new Point4f(180, 0, 1, 0);
		else if (moveX == 0 && moveZ == 0.05) rotate = new Point4f(0, 0, 1, 0);
		else if (moveX == -0.05 && moveZ == 0) rotate = new Point4f(270, 0, 1, 0);
		else if (moveX == 0.05 && moveZ == 0) rotate = new Point4f(90, 0, 1, 0);
	}

	public double getMoveX(){
		return moveX;
	}

	public double getMoveZ(){
		return moveZ;
	}

	public void setMoveX(double x){
		moveX = x;
	}

	public void setMoveZ(double z){
		moveZ = z;
	}

	public Point4f getRotate(){
		return rotate;
	}

	public void setRotate(Point4f rotate){
		this.rotate = rotate;
	}

	public Point3f getShift(){
		return shift;
	}

	public float getShiftX(){
		return shift.x;
	}

	public float getShiftZ(){
		return shift.z;
	}

	public void setShiftX(float x){
		shift.x = x;
	}

	public void setShiftZ(float z){
		shift.z = z;
	}

	public boolean isDead(){
		return dead;
	}

	public void setDead(boolean value){
		dead = value;
	}

	public boolean getPunch(){
		return punch;
	}

	public void setPunch(boolean value){
		punch = value;
	}

	public int getWS(){
		return WS;
	}

	public int getWF(){
		return WF;
	}

	public int getPS(){
		return PS;
	}

	public int getPF(){
		return PF;
	}

	public int getDS(){
		return DS;
	}

	public int getDF(){
		return DF;
	}

	public int getHealth(){
		return health;
	}

	public void setHealth(int value){
		health = value;
	}

	public int getChoice(){
		return choice;
	}

	public int getWalk(){
		return walk;
	}
}
