import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class BFS {
	
	static boolean[][] vis = new boolean[21][24];
	static int[][] dis = new int[21][24];
	static int d[][] = {{1,0},{-1,0},{0,-1},{0,1}};
	static MyArrayQueue<Point2f> q = new MyArrayQueue<Point2f>();
	static boolean[][] adj_matrix = new boolean[21][24];
	
	public static void bfs(Point2f start, Point2f end) {
		q.enqueue(new MyNode(start));
		vis[start.getX()][start.getY()] = true;
		dis[start.getX()][start.getY()] = 0;
		while (!q.isEmpty()) {
			Node<Point2f> cur = q.dequeue();
			Point2f p = cur.getValue();
			
		    for (int i=0;i<4;i++){
		      int nx=p.getX()+d[i][0];int ny=p.getY()+d[i][1];
		      if (nx>=0 && nx<=21 && ny>=0 && ny<=24 && !vis[nx][ny] && adj_matrix[nx][ny]){
		        Point2f nextp = new Point2f(nx, ny);
		    	q.enqueue(new MyNode(nextp));
		        vis[nx][ny]=true;
		        dis[nx][ny]=dis[p.getX()][p.getY()]+1;
		      }
		    }
			if (p == end) {
				
			}
		}
		System.out.println(dis[end.getX()][end.getY()]);
		
		
		
	}
	
	public static void readFile(String fileName) {
		File input = new File(fileName);
		int j = 0;
        Scanner scan;
		try {
			scan = new Scanner(input);
			
			//read the x and y coordinates on each line of the file and store them in arraylists
			while (scan.hasNextLine()) {
				String line[] = scan.nextLine().split(" ");
				
		        for (int i = 0; i < line.length; i++) {
		        	int val = Integer.parseInt(line[i]);
		        	if (val == 0) {
		        		adj_matrix[i][j] = false;
		        	}
		        	else {
		        		adj_matrix[i][j] = true;
		        	}
		        }
		        j++;
		   }
		   scan.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("File Not Found");
			e.printStackTrace();
		}

	}
	
	public static void main(String args[]) {
		readFile("Resource/Models/Map.txt");
		Point2f a = new Point2f(13,1);
		Point2f b = new Point2f(10,3);
		bfs(a,b);
	}
	
	
}
