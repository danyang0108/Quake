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
		vis[start.x][start.y] = true;
		dis[start.x][start.y] = 0;
		while (!q.isEmpty()){
			Point2f cur = q.dequeue();
			if (cur.x == end.x && cur.y == end.y){
				//Backtrack to the original point
				Point2f backtrack = new Point2f(end.x, end.y);
				while (prev[backtrack.x][backtrack.y].x != start.x || prev[backtrack.x][backtrack.y].y != start.y){
					backtrack = prev[backtrack.x][backtrack.y];
				}
				return backtrack;
			}
		    for (int i = 0; i < 4; i++){
		      int nx = cur.x + d[i][0];
		      int ny = cur.y + d[i][1];
		      if (nx >= 0 && nx < HEIGHT && ny >= 0 && ny < WIDTH){
		      	if (vis[nx][ny]) continue; //Already visited
		      	if (!wall[nx][ny]) continue; //Wall
		    	q.enqueue(new Point2f(nx, ny));
		        vis[nx][ny] = true;
		        dis[nx][ny] = dis[cur.x][cur.y] + 1;
		        prev[nx][ny] = cur;
		      }
		    }
		}
		return new Point2f(-1, -1);
	}
}
