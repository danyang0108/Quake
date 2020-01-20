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
	public boolean[][] wall = new boolean[HEIGHT][WIDTH];

	public BFS() throws Exception{
		readFile("Resource/Models/Map.txt");
	}

	//reads the map, 0 represents a wall and 1 represents open space
	public void readFile(String fileName) throws Exception{
		Scanner scan = new Scanner(new File(fileName));
		int j = 0;
		while (scan.hasNextLine()){
			String[] line = scan.nextLine().split(" ");
			for (int i = 0; i < line.length; i++){
				wall[j][i] = Integer.parseInt(line[i]) == 1;
			}
			j++;
		}
		scan.close();
	}
	
	//finds the shortest path from the start position to the end position
	//then backtrack to trace the path  
	public Point2f bfs(Point2f start, Point2f end){
		for (int i = 0; i < HEIGHT; i++){
			for (int j = 0; j < WIDTH; j++){
				vis[i][j] = false;
				dis[i][j] = 0;
			}
		}
		q.enqueue(start);
		vis[start.x][start.z] = true;
		dis[start.x][start.z] = 0;
		prev[start.x][start.z] = start;
		while (!q.isEmpty()){
			Point2f cur = q.dequeue();
			if (cur.x == end.x && cur.z == end.z){
				//Backtrack to the original point
				Point2f backtrack = new Point2f(end.x, end.z);
				while (prev[backtrack.x][backtrack.z] != backtrack){
					backtrack = prev[backtrack.x][backtrack.z];
				}
				return backtrack;
			}
			
			//check the left, right, up, down directions
		    for (int i = 0; i < 4; i++){
		      	int nx = cur.x + d[i][0];
		      	int nz = cur.z + d[i][1];
		      	if (nx >= 0 && nx < HEIGHT && nz >= 0 && nz < WIDTH){
		      		if (vis[nx][nz] || !wall[nx][nz]) continue; //Already visited OR Wall
		    		q.enqueue(new Point2f(nx, nz));
		        	vis[nx][nz] = true;
		        	dis[nx][nz] = dis[cur.x][cur.z] + 1;
		        	prev[nx][nz] = cur;
		      	}
		    }
		}
		System.out.println("PATH NOT FOUND");
		return new Point2f(-1, -1);
	}
}
