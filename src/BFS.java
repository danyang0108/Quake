import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class BFS{

	private int WIDTH = 21, HEIGHT = 24;

	private boolean[][] vis = new boolean[HEIGHT][WIDTH];
	private int[][] dis = new int[HEIGHT][WIDTH];
	private Point2f[][] prev = new Point2f[HEIGHT][WIDTH];
	private int[][] d = {{1, 0}, {-1, 0}, {0, -1}, {0, 1}};
	private Queue<Point2f> q = new Queue<>();
	private boolean[][] wall = new boolean[HEIGHT][WIDTH];

	public BFS() throws Exception{
		readFile("Resource/Models/Map.txt");
	}

	public void readFile(String fileName) throws Exception{
		Scanner scan = new Scanner(new File(fileName));
		//read the x and y coordinates on each line of the file and store them in ArrayLists
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
	
	public Point2f bfs(Point2f start, Point2f end){
		q.enqueue(start);
		vis[start.x][start.z] = true;
		dis[start.x][start.z] = 0;
		while (!q.isEmpty()){
			Point2f cur = q.dequeue();
			if (cur.x == end.x && cur.z == end.z){
				//Backtrack to the original point
				Point2f backtrack = new Point2f(end.x, end.z);
				while (prev[backtrack.x][backtrack.z].x != start.x || prev[backtrack.x][backtrack.z].z != start.z){
					backtrack = prev[backtrack.x][backtrack.z];
				}
				return backtrack;
			}
		    for (int i = 0; i < 4; i++){
		      int nx = cur.x + d[i][0];
		      int nz = cur.z + d[i][1];
		      if (nx >= 0 && nx < HEIGHT && nz >= 0 && nz < WIDTH){
		      	if (vis[nx][nz]) continue; //Already visited
		      	if (!wall[nx][nz]) continue; //Wall
		    	q.enqueue(new Point2f(nx, nz));
		        vis[nx][nz] = true;
		        dis[nx][nz] = dis[cur.x][cur.z] + 1;
		        prev[nx][nz] = cur;
		      }
		    }
		}
		return new Point2f(-1, -1);
	}
}
