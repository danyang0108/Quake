import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

public class legacyGL{
	private long window;
	private final int WINDOW_WIDTH = 1366;
	private final int WINDOW_HEIGHT = 768;
	private float lower = 0.15f;
	private int fixX = 10, fixZ = 12;
	private MeshObject MAP, GUN;
	private Boolean[][] vis = new Boolean[24][21];
	private ArrayList<MeshObject> keyframes = new ArrayList<>();
	private Scanner maze;
	private float TX = 0, TZ = 0; //For actual translations
	private boolean[] movement = new boolean[6]; //For keyboard controls (W, S, A, D, SHIFT, CTRL)
	private boolean mouse = false;
	private ArrayList<Enemy> enemies = new ArrayList<>();
	int walkSize = 100;

	Texture tex;
	int charCnt = 0;
	Colour yellow = new Colour(255,255,0);
	Colour blue = new Colour(0,0,255);
	int start = 0;
	int end = 0;
	
	
	public static void main(String[] args) throws Exception{
		
		new legacyGL().run();
	}

	private void run() throws Exception{
		GLFWErrorCallback.createPrint(System.err).set();
		glfwInit();

		window = glfwCreateWindow(WINDOW_WIDTH, WINDOW_HEIGHT, "Quake", glfwGetPrimaryMonitor(), NULL);
		//Called when there's keyboard activity
		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
				//Close the window
				glfwSetWindowShouldClose(window, true);
			if (action == GLFW_PRESS || action == GLFW_REPEAT){
				//A key is pressed
				if (key == GLFW_KEY_W) movement[0] = true;
				if (key == GLFW_KEY_S) movement[1] = true;
				if (key == GLFW_KEY_A) movement[2] = true;
				if (key == GLFW_KEY_D) movement[3] = true;
				if (key == GLFW_KEY_LEFT_SHIFT) movement[4] = true;
				if (key == GLFW_KEY_LEFT_CONTROL) movement[5] = true;
			}else if (action == GLFW_RELEASE){
				//A key is released
				if (key == GLFW_KEY_W) movement[0] = false;
				if (key == GLFW_KEY_S) movement[1] = false;
				if (key == GLFW_KEY_A) movement[2] = false;
				if (key == GLFW_KEY_D) movement[3] = false;
				if (key == GLFW_KEY_LEFT_SHIFT) movement[4] = false;
				if (key == GLFW_KEY_LEFT_CONTROL) movement[5] = false;
			}
		});

		glfwSetMouseButtonCallback(window, (window, button, action, mods) -> {
			if (action == GLFW_PRESS || action == GLFW_REPEAT){
				if (button == GLFW_MOUSE_BUTTON_LEFT){
					mouse = true;
				}
			}else if (action == GLFW_RELEASE){
				if (button == GLFW_MOUSE_BUTTON_LEFT){
					mouse = false;
				}
			}
		});
		//This line is to not show the cursor on the screen. It's to allow unlimited movement.
		glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
		glfwMakeContextCurrent(window);
		glfwSwapInterval(1);
		// your code to initialize the scene goes here...

		GL.createCapabilities();

		glfwShowWindow(window);
		loop();
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}

	// sets a perspective projection
	private void setPerspective(){
		float near = 0.01f, far = 100f, perspective = 30;
		float v = -near * (float)Math.tan(Math.toRadians(perspective));
		//Note: Maximum 45 (Quake Pro), Minimum 20 (Zoomed In), Default 30
		v *= (float)WINDOW_WIDTH / (float)WINDOW_HEIGHT;
		glFrustum(v, -v, v, -v, near, far);
	}

	private void loop() throws Exception{
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		setPerspective();
		glEnable(GL_TEXTURE_2D);
		glEnable(GL_SMOOTH);
		glEnable(GL_DEPTH_TEST);

		maze = new Scanner(new File("Resource/Models/Map.txt"));
		int counter = 0;
		while (maze.hasNextLine()){
			String[] line = maze.nextLine().split(" ");
			for (int i = 0; i < line.length; i++) vis[counter][i] = Integer.parseInt(line[i]) == 1;
			counter++;
		}

		MAP = new MeshObject("Resource/Models/Map.obj");
		GUN = new MeshObject("Resource/Models/M9A1.obj");
		GUN.scale(new Point3f(0.09f, 0.09f, 0.09f));
		String path2 = "Resource/Models/Move_000";
		for (int i = 1; i <= walkSize; i++){
			String threeDigit;
			if (i < 10) threeDigit = "00" + i;
			else if (i < 100) threeDigit = "0" + i;
			else threeDigit = Integer.toString(i);
			MeshObject animation = new MeshObject(path2 + threeDigit + ".obj");
			animation.scale(new Point3f(0.35f, 0.5f, 0.5f));
			keyframes.add(animation);
		}
		Enemy first = new Enemy();
		enemies.add(first);

		while (!glfwWindowShouldClose(window)){
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			render();
			glfwSwapBuffers(window);
			glfwPollEvents();
		}
	}

	private void render(){
		long before = System.nanoTime();
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();

		Point4f move = new Control().movement(window, movement);
		int Z = Math.round(-TZ - move.z) + fixZ;
		int X = Math.round(-TX - move.x) + fixX;
		if (vis[Z][X]){
			TX += move.x;
			TZ += move.z;
		}else{
			Z = Math.round(-TZ) + fixZ;
			if (vis[Z][X]) TX += move.x;
			else{
				X = Math.round(-TX) + fixX;
				if (vis[Z][X]) TZ += move.z;
			}
		}
		glTranslatef(TX, movement[5] ? lower : 0, TZ);

		if (mouse){
			double faceX = 0.1 * Math.sin(Math.toRadians(-move.rot));
			double faceY = 0.1 * Math.cos(Math.toRadians(-move.rot));
			double curX = TX + faceX, curY = TZ + faceY;
			boolean cont = true;
			while (inMap(curX, curY) && cont){
				for (Enemy E: enemies){
					if (E.hit(curX, curY)){
						MAP.draw();
						cont = false;
					}
				}
				curX += faceX;
				curY += faceY;
			}
		}

		//Draw the objects
		//MAP.draw();
		GUN.translate(new Point3f(-TX + 0.5f*(float)Math.sin(Math.toRadians(move.rot)), -0.5f, -TZ - 0.5f*(float)Math.cos(Math.toRadians(move.rot))));
		GUN.rotate(new Point4f(270-move.rot, 0, 1, 0));
		GUN.draw();

		for (Enemy E: enemies){
			MeshObject temp = keyframes.get(E.frame);
			temp.translate(E.shift);
			temp.rotate(E.rotate);
			temp.draw();
			E.updateFrame(walkSize);
		}

		long after = System.nanoTime();
		//System.out.println((double)(after-before)/1e9d);
	}
	public void drawText(String text, float x, float y, int fontSize) {
		text = text.toUpperCase();
		float startX = x, startY = y;
		for (int i = 0; i < text.length(); i++) {
			
			
			int ascii = (int)(text.charAt(i));
			ascii -= 32;
			charCnt = 0;
			for (int j = 0; j < 308; j++) {
				Colour c;
				try {
					c = tex.getPixel("Resource/Images/text.png",j, 0);
					if (charCnt > ascii) break;
					if (c.getR() == yellow.getR() && c.getG() == yellow.getG() && c.getB() == yellow.getB()) {
						charCnt++;
						end = j;
					}
					if (c.getR() == blue.getR() && c.getG() == blue.getG() && c.getB() == blue.getB()) {
						start = j+1;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			int x_length = end - start;
			float endX = (startX * WINDOW_WIDTH + x_length * fontSize) / WINDOW_WIDTH;
			float endY = (startY * WINDOW_HEIGHT - 6 * fontSize) / WINDOW_HEIGHT;
			//System.out.println(startX + " " + startY + " " + endX + " " + endY);
			//System.out.println(start + " " + end);
			glBegin(GL_QUADS);
			glTexCoord2f(start/308f,0);
			glVertex3f(startX, startY, -2);
			glTexCoord2f(end/308f,0);
			glVertex3f(endX, startY, -2);
			glTexCoord2f(end/308f,1f);
			glVertex3f(endX, endY, -2);
			glTexCoord2f(start/308f,1f);
			glVertex3f(startX, endY, -2);
			glEnd();
			startX = endX;
			//startY = endY;
			/*glBegin(GL_QUADS);
			glTexCoord2f(start/308f,0);
			glVertex2f(startX, startY);
			glTexCoord2f(end/308f,0);
			glVertex2f(startX+0.04f, startY);
			glTexCoord2f(end/308f,1f);
			glVertex2f(startX+0.04f, 1f);
			glTexCoord2f(start/308f,1f);
			glVertex2f(startX, 1f);
			glEnd();*/
		}
	}

	public boolean inMap(double x, double y){
		//Add: Wall Detection
		return vis[(int)Math.ceil(y) + fixZ][(int)Math.ceil(x) + fixX] && x >= -10 && x <= 10 && y >= -11 && y <= 12;
	}
}
