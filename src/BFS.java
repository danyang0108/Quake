import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class BFS{

	private boolean[][] vis = new boolean[21][24];
	private int[][] dis = new int[21][24];
	private int[][] d = {{1, 0}, {-1, 0}, {0, -1}, {0, 1}};
	private MyArrayQueue<MyNode<Point2f>> q = new MyArrayQueue<>();
	private boolean[][] adj_matrix = new boolean[21][24];
	
	public void bfs(Point2f start, Point2f end){
		q.enqueue(new MyNode(start));
		vis[start.x][start.y] = true;
		dis[start.x][start.y] = 0;
		while (!q.isEmpty()){
			Node<MyNode<Point2f>> cur = q.dequeue();
			Point2f p = cur.getValue().getValue();

		    for (int i = 0; i < 4; i++){
		      int nx = p.x + d[i][0];
		      int ny = p.y + d[i][1];
		      if (nx >= 0 && nx < 21 && ny >= 0 && ny < 24 && !vis[nx][ny] && adj_matrix[nx][ny]){
		        Point2f nextp = new Point2f(nx, ny);
		    	q.enqueue(new MyNode(nextp));
		        vis[nx][ny] = true;
		        dis[nx][ny] = dis[p.x][p.y] + 1;
		      }
		    }
			if (p == end) {
				
			}
		}
		System.out.println(dis[end.x][end.y]);
	}
	
	public void readFile(String fileName) throws Exception{
		Scanner scan = new Scanner(new File(fileName));

        //read the x and y coordinates on each line of the file and store them in arraylists
        int j = 0;
        while (scan.hasNextLine()){
            String[] line = scan.nextLine().split(" ");
            for (int i = 0; i < line.length; i++){
                adj_matrix[i][j] = Integer.parseInt(line[i]) == 1;
            }
            j++;
       }
       scan.close();
	}
	
	public static void main(String[] args) throws Exception{
	    BFS lol = new BFS();
		lol.readFile("Resource/Models/Map.txt");
		Point2f a = new Point2f(10, 3);
		Point2f b = new Point2f(12, 17);
		lol.bfs(a,b);
	}
}
