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
	private MeshObject MAP, AMMO;
	private Boolean[][] vis = new Boolean[24][21];
	private ArrayList<MeshObject> enemy = new ArrayList<>();
	private float TX = 0, TZ = 0; //For actual translations
	private boolean[] movement = new boolean[6]; //For keyboard controls (W, S, A, D, SHIFT, CTRL)
	private ArrayList<Integer> enemyIndex = new ArrayList<>();
	private ArrayList<Point3f> enemyMove = new ArrayList<>();
	private ArrayList<Point4f> enemyRotate = new ArrayList<>();
	int walkSize = 2;

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

		Scanner maze = new Scanner(new File("Resource/Models/Map.txt"));
		int counter = 0;
		while (maze.hasNextLine()){
			String[] line = maze.nextLine().split(" ");
			for (int i = 0; i < line.length; i++) vis[counter][i] = Integer.parseInt(line[i]) == 1;
			counter++;
		}

		MAP = new MeshObject("Resource/Models/Map.obj");
		AMMO = new MeshObject("Resource/Models/M9A1.obj");
		AMMO.scale(new Point3f(0.1f, 0.1f, 0.1f));
		String path2 = "Resource/Models/Move_000";
		for (int i = 180; i <= 180 + walkSize; i++){
			String threeDigit;
			if (i < 10) threeDigit = "00" + i;
			else if (i < 100) threeDigit = "0" + i;
			else threeDigit = Integer.toString(i);
			MeshObject animation = new MeshObject(path2 + threeDigit + ".obj");
			animation.scale(new Point3f(0.35f, 0.5f, 0.5f));
			enemy.add(animation);
		}
		enemyIndex.add(0);
		enemyMove.add(new Point3f(0, -1, -2));
		enemyRotate.add(new Point4f(90, 0, 1, 0));
		/*
		enemyIndex.add(0);
		enemyMove.add(new Point3f(0, -1, 0));
		enemyRotate.add(new Point4f(0, 0, 0, 0));
		 */

		while (!glfwWindowShouldClose(window)){
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			render();
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
		glTranslatef(TX, movement[5] ? lower : 0, TZ);

		//Draw the objects
		MAP.draw();
		AMMO.translate(new Point3f(-TX-0.3f, -0.5f, -TZ-0.3f));
		AMMO.rotate(new Point4f(-move.rot, 0, 1, 0));
		AMMO.draw();
		for (int i = 0; i < enemyIndex.size(); i++){
			MeshObject temp = enemy.get(enemyIndex.get(i));
			temp.translate(enemyMove.get(i));
			temp.rotate(enemyRotate.get(i));
			enemy.get(enemyIndex.get(i)).draw();
		}

        //Update the character animation
		for (int i = 0; i < enemyIndex.size(); i++){
			int index = enemyIndex.get(i);
			enemyIndex.set(i, index == (walkSize - 1) ? 0 : (index + 1));
		}
	}
}