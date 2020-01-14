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
	private static final int WINDOW_WIDTH = 1366;
	private static final int WINDOW_HEIGHT = 768;
	private MeshObject MAP;
	private Boolean[][] vis = new Boolean[24][21];
	private ArrayList<MeshObject> enemy = new ArrayList<>();
	private float TX = 0, TZ = 0; //For actual translations
	private boolean[] movement = new boolean[4]; //For keyboard controls (W, S, A, D)
	private ArrayList<Integer> enemyIndex = new ArrayList<>();
	private ArrayList<Point3f> enemyMove = new ArrayList<>();
	private ArrayList<Point4f> enemyRotate = new ArrayList<>();
	int walkSize = 100;

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
			}else if (action == GLFW_RELEASE){
				//A key is released
				if (key == GLFW_KEY_W) movement[0] = false;
				if (key == GLFW_KEY_S) movement[1] = false;
				if (key == GLFW_KEY_A) movement[2] = false;
				if (key == GLFW_KEY_D) movement[3] = false;
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
	private static void setPerspective(){
		float near = 0.01f, far = 100f;
		float v = -near * (float)Math.tan(Math.toRadians(30));
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
		String path2 = "Resource/Models/Move_000";
		for (int i = 1; i <= walkSize; i++){
			String threeDigit;
			if (i < 10) threeDigit = "00" + i;
			else if (i < 100) threeDigit = "0" + i;
			else threeDigit = Integer.toString(i);
			MeshObject animation = new MeshObject(path2 + threeDigit + ".obj");
			animation.scale(new Point3f(0.35f, 0.5f, 0.5f));
			enemy.add(animation);
		}
		enemyIndex.add(1);
		enemyMove.add(new Point3f(0, -1, -2));
		enemyRotate.add(new Point4f(90, 0, 1, 0));
		enemyIndex.add(70);
		enemyMove.add(new Point3f(0, -1, 0));
		enemyRotate.add(new Point4f(0, 0, 0, 0));

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

		Point3f move = new Control().movement(window, movement);
		int zed = Math.round(-TZ - move.z) + 12;
		int ex = Math.round(-TX - move.x) + 10;
		if (vis[zed][ex]){
			TX += move.x;
			TZ += move.z;
		}else{
			zed = Math.round(-TZ) + 12;
			if (vis[zed][ex]) TX += move.x;
			else{
				ex = Math.round(-TX) + 10;
				if (vis[zed][ex]) TZ += move.z;
			}
		}
		glTranslatef(TX, 0, TZ);

		//Draw the objects
		MAP.draw();
		for (int i = 0; i < enemyIndex.size(); i++){
			MeshObject temp = enemy.get(enemyIndex.get(i));
			temp.translate(enemyMove.get(i));
			temp.rotate(enemyRotate.get(i));
			enemy.get(enemyIndex.get(i)).draw();
		}

        //Update the character animation
		for (int i = 0; i < enemyIndex.size(); i++){
			int index = enemyIndex.get(i);
			enemyIndex.set(i, index == (walkSize - 1) ? 1 : (index + 1));
		}
	}
}