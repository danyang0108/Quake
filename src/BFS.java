///Author: Danyang Wang & Ethan Zhang
//Class: ICS4U
//Date: Jan 17th, 2020
//Instructor: Mr Radulovic
//Assignment name: ICS4U Culminating
/*Description: This class is for enemies to find the shortest path towards the user.
 * Using bfs, we find the shortest path from starting position to ending position.
 * Then we trace the path.
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class BFS{

	private int WIDTH = 21, HEIGHT = 24;	//size of map

	//visited array
	private boolean[][] vis = new boolean[HEIGHT][WIDTH];

	//distance from starting node 
	private int[][] dis = new int[HEIGHT][WIDTH];

	//backtracking the previous node that was visited
	private Point2f[][] prev = new Point2f[HEIGHT][WIDTH];

	//directions (left, right, up, down)
	private int[][] d = {{1, 0}, {-1, 0}, {0, -1}, {0, 1}};

	//queue for running bfs
	private Queue<Point2f> q = new Queue<>();

	//position of walls
	private boolean[][] wall = new boolean[HEIGHT][WIDTH];

	public BFS() throws Exception{
		readFile("Resource/Models/Map.txt");
	}

	//reads the map, 0 represents a wall and 1 represents open space
	public void readFile(String fileName) throws Exception{
		Scanner scan = new Scanner(new File(fileName));
		int j = 0; //For tracking number of rows
		while (scan.hasNextLine()){
			String[] line = scan.nextLine().split(" ");
			for (int i = 0; i < line.length; i++){
				//If the value is 1, then it's an open space.
				//The 2D array "wall" also stores 1 if it's an open space.
				wall[j][i] = Integer.parseInt(line[i]) == 1;
			}
			//Increase the row count
			j++;
		}
		scan.close();
	}

	//Finds the shortest path from the start position to the end position
	//then backtrack to trace the path.
	public Point2f bfs(Point2f start, Point2f end){
		q.enqueue(start);
		//Clear the arrays
		for (int i = 0; i < HEIGHT; i++){
			for (int j = 0; j < WIDTH; j++){
				vis[i][j] = false;
				dis[i][j] = 0;
			}
		}
		vis[start.getX()][start.getZ()] = true; //Mark starting position as visited
		dis[start.getX()][start.getZ()] = 0; //Ensure the starting distance is reset
		while (!q.isEmpty()){
			Point2f cur = q.dequeue(); //Current position
			if (cur.getX() == end.getX() && cur.getZ() == end.getZ()){
				//Backtrack to the original point
				Point2f backtrack = new Point2f(end.getX(), end.getZ());
				while (prev[backtrack.getX()][backtrack.getZ()].getX() != start.getX() || 
						prev[backtrack.getX()][backtrack.getZ()].getZ() != start.getZ()){
					backtrack = prev[backtrack.getX()][backtrack.getZ()];
					//Keep on backtracking until at the starting position
				}
				//Since the BFS function must be correct for the program to reach this point,
				//There's no worry that the program would be stuck inside the backtrack progress.
				return backtrack;
			}

			//check the left, right, up, down directions
			for (int i = 0; i < 4; i++){
				int nx = cur.getX() + d[i][0];
				int nz = cur.getZ() + d[i][1];
				//First, ensure the new position is inside the map (i.e. the 2D array)
				if (nx >= 0 && nx < HEIGHT && nz >= 0 && nz < WIDTH){
					if (vis[nx][nz]) continue; //Already visited
					if (!wall[nx][nz]) continue; //Wall
					q.enqueue(new Point2f(nx, nz));
					vis[nx][nz] = true;
					dis[nx][nz] = dis[cur.getX()][cur.getZ()] + 1;
					//Store the previous position, which represents where the path travelled to
					prev[nx][nz] = cur;
				}
			}
		}
		//If this point is ever reached, then there's no path to the user.
		//This should never happen.
		return new Point2f(-1, -1);
	}
}
