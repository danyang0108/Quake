import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import java.io.File;
import java.util.Scanner;
import java.util.ArrayList;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

public class legacyGL{
	private long window;
	private final int WINDOW_WIDTH = 1366, WINDOW_HEIGHT = 768;
	private int fixX = 10, fixZ = 12;
	private MeshObject MAP, GUN, KIT;
	private Boolean[][] vis = new Boolean[24][21];
	private ArrayList<MeshObject> keyframes = new ArrayList<>();
	private float TX = 0, TZ = 0; //For actual translations
	private boolean[] movement = new boolean[5]; //For keyboard controls (W, S, A, D, SHIFT)
	private boolean mouse = false;
	private ArrayList<Enemy> enemies = new ArrayList<>();

	TextureV2 tex;
	int charCnt = 0;
	Colour yellow = new Colour(255, 255, 0);
	Colour blue = new Colour(0, 0, 255);
	int start = 0, end = 0;

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
				if (key == GLFW_KEY_LEFT_CONTROL) movement[4] = true;
			}else if (action == GLFW_RELEASE){
				//A key is released
				if (key == GLFW_KEY_W) movement[0] = false;
				if (key == GLFW_KEY_S) movement[1] = false;
				if (key == GLFW_KEY_A) movement[2] = false;
				if (key == GLFW_KEY_D) movement[3] = false;
				if (key == GLFW_KEY_LEFT_CONTROL) movement[4] = false;
			}
		});

		glfwSetMouseButtonCallback(window, (window, button, action, mods) -> {
			if (action == GLFW_PRESS || action == GLFW_REPEAT){
				if (button == GLFW_MOUSE_BUTTON_LEFT) mouse = true;
			}else if (action == GLFW_RELEASE){
				if (button == GLFW_MOUSE_BUTTON_LEFT) mouse = false;
			}
		});
		//This line is to not show the cursor on the screen. It's to allow unlimited movement.
		glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
		glfwMakeContextCurrent(window);
		glfwSwapInterval(1);

		GL.createCapabilities();
		tex = new TextureV2("Resource/Images/text.png");
		tex.setPixel(0);
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
		
		Scanner maze = new Scanner(new File("Resource/Models/Map.txt"));
		int counter = 0;
		while (maze.hasNextLine()){
			String[] line = maze.nextLine().split(" ");
			for (int i = 0; i < line.length; i++) vis[counter][i] = Integer.parseInt(line[i]) == 1;
			counter++;
		}

		MAP = new MeshObject("Resource/Models/Map.obj");
		GUN = new MeshObject("Resource/Models/M9A1.obj");
		KIT = new MeshObject("Resource/Models/Ammo.obj");
		GUN.scale(new Point3f(0.09f, 0.09f, 0.09f));

		long START = System.nanoTime();

		String path2 = "Resource/Models/Move_000";
		int frames = 360; //There are 360 frames in total for enemy animation.
		for (int i = 1; i <= frames; i++){
			String threeDigit;
			if (i < 10) threeDigit = "00" + i;
			else if (i < 100) threeDigit = "0" + i;
			else threeDigit = Integer.toString(i);
			MeshObject animation = new MeshObject(path2 + threeDigit + ".obj");
			animation.scale(new Point3f(0.35f, 0.5f, 0.5f));
			keyframes.add(animation);
		}

		long END = System.nanoTime();
		System.out.println("TIME: " + (END - START) / 1e9d);

		Enemy first = new Enemy();
		enemies.add(first);
		Enemy second = new Enemy(new Point3f(1, -1, 5), new Point4f(0, 0, 0, 0));
		enemies.add(second);
		
		while (!glfwWindowShouldClose(window)){
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			render();
			tex.bind();
			drawText(" h o a n g ", 0, 0, 20);
			glfwSwapBuffers(window);
			glfwPollEvents();
		}
	}

	private void render(){
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
		glTranslatef(TX, 0, TZ);

		if (mouse){
			double faceX = 0.1 * Math.sin(Math.toRadians(-move.rot));
			double faceY = 0.1 * Math.cos(Math.toRadians(-move.rot));
			double curX = TX + faceX, curY = TZ + faceY;
			boolean cont = true;
			while (inMap(curX, curY) && cont){
				for (int i = 0; i < enemies.size(); i++){
					Enemy E = enemies.get(i);
					if (E.hit(curX, curY)){
						E.health--;
						cont = false;
					}
				}
				curX += faceX;
				curY += faceY;
			}
		}

		//Draw the objects
		MAP.draw();
		GUN.translate(new Point3f(-TX, -0.5f, -TZ));
		GUN.rotate(new Point4f(270-move.rot, 0, 1, 0));
		GUN.draw();
		KIT.draw();

		ArrayList<Integer> remove = new ArrayList<>();
		for (int i = 0; i < enemies.size(); i++){
			Enemy E = enemies.get(i);
			if (E.health <= 0 && !E.dead){
				//One time only
				E.setChoice(2);
				E.dead = true;
			}else if (nearUser(E.shift.x, E.shift.z) && !E.punch){
				E.setChoice(1);
				E.punch = true;
			};
			int choice = E.choice; //Which animation it's in
			MeshObject temp;
			if (choice == 0) temp = keyframes.get(E.WF);
			else if (choice == 1) temp = keyframes.get(E.WS + E.PF);
			else temp = keyframes.get(E.WS + E.PS + E.DF);
			temp.translate(E.shift);
			temp.rotate(E.rotate);
			temp.draw();
			//Display health bar
			if (E.updateFrame()) remove.add(i); //The enemy is dead
		}
		for (int i: remove) enemies.remove(i); //Remove dead enemies
	}

	public void drawText(String text, float x, float y, int fontSize) throws Exception{
		text = text.toUpperCase();
		float startX = x, startY = y;
		for (int i = 0; i < text.length(); i++){
			int ascii = text.charAt(i) - 32;
			charCnt = 0;
			for (int j = 0; j < 308; j++){
				Colour c = tex.getPixel(j, 0);
				if (charCnt > ascii) break;
				if (c.getR() == yellow.getR() && c.getG() == yellow.getG() && c.getB() == yellow.getB()){
					charCnt++;
					end = j;
				}
				if (c.getR() == blue.getR() && c.getG() == blue.getG() && c.getB() == blue.getB()){
					start = j+1;
				}
			}
			int x_length = end - start;
			float endX = (startX * WINDOW_WIDTH + x_length * fontSize) / WINDOW_WIDTH;
			float endY = (startY * WINDOW_HEIGHT - 6 * fontSize) / WINDOW_HEIGHT;
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
		}
	}

	public boolean inMap(double x, double y){
		int FX = -(int)Math.round(y);
		int FY = -(int)Math.round(x);
		if (FX > -fixX && FX < fixX && FY > 1-fixZ && FY < fixZ){
			return vis[FX + fixZ][FY + fixX];
		}
		return false;
	}

	public boolean nearUser(double x, double y){
		double enemyReach = 1.75;
		return (x + TX) * (x + TX) + (y + TZ) * (y + TZ) <= enemyReach;
	}
}
