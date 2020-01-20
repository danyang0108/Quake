public class Enemy extends Entity{
	public final int WS = 100, PS = 80, DS = 180;
	public boolean punch = false;
	public boolean dead = false;
	public int WF, PF, DF; //KeyFrame for walk, punch, despawn
	public int choice; //Walk, Punch, Despawn
	public Point3f shift; //Position in map
	public Point4f rotate; //Rotation in map
	public int health;
	public int walk; //For movement
	public double moveX, moveZ;

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
		//health = 100;
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
		int fixX = 10, fixZ = 12;
		Point2f EPos = roundEnemy(shift.z + fixZ, shift.x + fixX);
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

	public Point2f roundEnemy(double x, double z) throws Exception{
		BFS bfs = new BFS();
		boolean[][] vis = bfs.wall;

		//NOTE: +LEFT, +UP
		double fracX = x - Math.floor(x); //From point to right
		double fracZ = z - Math.floor(z); //From point to bottom
		double half = 0.5;
		double smallX = Math.abs(half - fracX);
		double smallZ = Math.abs(half - fracZ);
		int actualX, actualZ;
		if (fracX < half){
			if (fracZ < half){
				//Bottom-Right section
				//First quadrant check
				actualX = (int)Math.floor(x);
				actualZ = (int)Math.floor(z);
				if (vis[actualX][actualZ]) return new Point2f(actualX, actualZ);
				//Check the rest of the quadrants
				if (smallX <= smallZ){
					//Bottom-Left
					if (vis[actualX + 1][actualZ]) return new Point2f(actualX + 1, actualZ);
					//Top-Right
					if (vis[actualX][actualZ + 1]) return new Point2f(actualX, actualZ + 1);
				}else{
					//Top-Right
					if (vis[actualX][actualZ + 1]) return new Point2f(actualX, actualZ + 1);
					//Bottom-Left
					if (vis[actualX + 1][actualZ]) return new Point2f(actualX + 1, actualZ);
				}
				//Corner
				if (vis[actualX + 1][actualZ + 1]) return new Point2f(actualX + 1, actualZ + 1);
				else{
					System.out.println("OUT OF BOUNDS");
					return new Point2f(-1, -1); //SHOULD NOT REACH
				}
			}else{
				//Top-Right Section
				actualX = (int)Math.floor(x);
				actualZ = (int)Math.ceil(z);
				if (vis[actualX][actualZ]) return new Point2f(actualX, actualZ);
				//Check the rest of the quadrants
				if (smallX <= smallZ){
					//Top-Left
					if (vis[actualX + 1][actualZ]) return new Point2f(actualX + 1, actualZ);
					//Bottom-Right
					if (vis[actualX][actualZ - 1]) return new Point2f(actualX, actualZ - 1);
				}else{
					//Bottom-Right
					if (vis[actualX][actualZ - 1]) return new Point2f(actualX, actualZ - 1);
					//Top-Left
					if (vis[actualX + 1][actualZ]) return new Point2f(actualX + 1, actualZ);
				}
				//Corner
				if (vis[actualX + 1][actualZ - 1]) return new Point2f(actualX + 1, actualZ - 1);
				else{
					System.out.println("OUT OF BOUNDS");
					return new Point2f(-1, -1); //SHOULD NOT REACH
				}
			}
		}else{
			if (fracZ < half){
				//Bottom-Left section
				actualX = (int)Math.ceil(x);
				actualZ = (int)Math.floor(z);
				if (vis[actualX][actualZ]) return new Point2f(actualX, actualZ);
				//Check the rest of the quadrants
				if (smallX <= smallZ){
					//Bottom-Right
					if (vis[actualX - 1][actualZ]) return new Point2f(actualX - 1, actualZ);
					//Top-Left
					if (vis[actualX][actualZ + 1]) return new Point2f(actualX, actualZ + 1);
				}else{
					//Top-Left
					if (vis[actualX][actualZ + 1]) return new Point2f(actualX, actualZ + 1);
					//Bottom-Right
					if (vis[actualX - 1][actualZ]) return new Point2f(actualX - 1, actualZ);
				}
				//Corner
				if (vis[actualX - 1][actualZ + 1]) return new Point2f(actualX - 1, actualZ + 1);
				else{
					System.out.println("OUT OF BOUNDS");
					return new Point2f(-1, -1); //SHOULD NOT REACH
				}
			}else{
				//Top-Left section
				actualX = (int)Math.ceil(x);
				actualZ = (int)Math.ceil(z);
				if (vis[actualX][actualZ]) return new Point2f(actualX, actualZ);
				//Check the rest of the quadrants
				if (smallX <= smallZ){
					//Top-Right
					if (vis[actualX - 1][actualZ]) return new Point2f(actualX - 1, actualZ);
					//Bottom-Left
					if (vis[actualX][actualZ - 1]) return new Point2f(actualX, actualZ - 1);
				}else{
					//Bottom-Left
					if (vis[actualX][actualZ - 1]) return new Point2f(actualX, actualZ - 1);
					//Top-Right
					if (vis[actualX - 1][actualZ]) return new Point2f(actualX - 1, actualZ);
				}
				//Corner
				if (vis[actualX - 1][actualZ - 1]) return new Point2f(actualX - 1, actualZ - 1);
				else{
					System.out.println("OUT OF BOUNDS");
					return new Point2f(-1, -1); //SHOULD NOT REACH
				}
			}
		}
	}
}
