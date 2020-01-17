import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class BFS{

	private int WIDTH = 21, HEIGHT = 24;

	private boolean[][] vis = new boolean[HEIGHT][WIDTH];
	private int[][] dis = new int[HEIGHT][WIDTH];
	private int[][] d = {{1, 0}, {-1, 0}, {0, -1}, {0, 1}};
	private Queue<Point2f> q = new Queue<>();
	private boolean[][] wall = new boolean[HEIGHT][WIDTH];
	
	public int bfs(Point2f start, Point2f end){
		q.enqueue(start);
		vis[start.x][start.y] = true;
		dis[start.x][start.y] = 0;
		while (!q.isEmpty()){
			Point2f cur = q.dequeue();
			if (cur.x == end.x && cur.y == end.y) return dis[end.x][end.y];
		    for (int i = 0; i < 4; i++){
		      int nx = cur.x + d[i][0];
		      int ny = cur.y + d[i][1];
		      if (nx >= 0 && nx < WIDTH && ny >= 0 && ny < HEIGHT){
		      	if (vis[nx][ny]) continue; //Already visited
		      	if (!wall[nx][ny]) continue; //Wall
		    	q.enqueue(new Point2f(nx, ny));
		        vis[nx][ny] = true;
		        dis[nx][ny] = dis[cur.x][cur.y] + 1;
		      }
		    }
		}
		return -1;
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
	
	public static void main(String[] args) throws Exception{
	    BFS lol = new BFS();
		lol.readFile("Resource/Models/Map.txt");
		Point2f a = new Point2f(1, 2);
		Point2f b = new Point2f(11, 11);
		System.out.println(lol.bfs(a,b));
	}
}
