import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import java.util.ArrayList;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

public class legacyGL{
	private long window;
	private static final int WINDOW_WIDTH = 1366;
	private static final int WINDOW_HEIGHT = 768;
	private ArrayList<MeshObject> objects = new ArrayList<>();
	private ArrayList<Boolean> display = new ArrayList<>();
	private float TX = 0, TY = 0, TZ = 0; //For actual translations
	private boolean[] movement = new boolean[4]; //For keyboard controls (W, S, A, D)
	int index = 1;
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

		String path1 = "Resource/Models/NMap.obj";
		objects.add(new MeshObject(path1));
		display.add(true);
		String path2 = "Resource/Models/Move_000";
		for (int i = 1; i < walkSize; i++){
			String threeDigit;
			if (i < 10) threeDigit = "00" + i;
			else if (i < 100) threeDigit = "0" + i;
			else threeDigit = Integer.toString(i);
			MeshObject curWalk = new MeshObject(path2 + threeDigit + ".obj");
			curWalk.translate(new Point3f(0, -1, -2));
			curWalk.rotate(new Point4f(90, 0, 1, 0));
			curWalk.scale(new Point3f(0.35f, 0.5f, 0.5f));
			objects.add(curWalk);
			display.add(i == 1);
		}

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
		TX += move.x;
		TY += move.y;
		TZ += move.z;
		glTranslatef(TX, TY, TZ);

		//Draw the objects
        for (int i = 0; i < objects.size(); i++) if (display.get(i)) objects.get(i).draw();

        //Update the character animation
		display.set(index, false);
		display.set(index == walkSize - 1 ? 1 : (index + 1), true);
        for (int i = 1; i < walkSize; i++) display.set(i, i == index);
		index = index < walkSize - 1 ? index + 1 : 1;
	}
}