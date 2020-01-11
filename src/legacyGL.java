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
	private float TX = 0, TY = 0, TZ = 0; //For actual translations
	private boolean[] movement = new boolean[4]; //For keyboard controls (W, S, A, D)

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
		glfwShowWindow(window);
		GL.createCapabilities();
		loop();
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}

	// sets a perspective projection
	private static void setPerspective(){
		float near = 0.01f, far = 2000f;
		float v = -near * (float)Math.tan(Math.toRadians(20));
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
		String path2 = "Resource/Models/first.obj";
		objects.add(new MeshObject(path2, new Point3f(0, -2, -2), new Point4f(90, 0, 1, 0), new Point3f(0.75f, 0.75f, 0.75f)));

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

		for (MeshObject object: objects) object.draw(); //Draw the objects
	}
}