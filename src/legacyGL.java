import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;
import java.nio.*;
import java.util.ArrayList;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class legacyGL{
	private long window;
	private static final int WINDOW_WIDTH = 1366;
	private static final int WINDOW_HEIGHT = 768;
	private ArrayList<MeshObject> objects = new ArrayList<>();
	float tx = 0, ty = 0, tz = 0; //For translations
	double dx = 0, dy = 0; //For rotations
	boolean movement[] = new boolean[4]; //For keyboard controls (W, S, A, D)

	public static void main(String[] args) throws Exception{
		new legacyGL().run();
	}

	public void run() throws Exception{
		System.out.println("Hello LWJGL " + Version.getVersion() + "!");
		init();
		GL.createCapabilities();
		System.out.println("OpenGL version " + glGetString(GL_VERSION));
		loop();
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}

	private void init(){
		for (int i = 0; i < 4; i++) movement[i] = false;
		GLFWErrorCallback.createPrint(System.err).set();
		if (!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");
		//Create the window
		window = glfwCreateWindow(WINDOW_WIDTH, WINDOW_HEIGHT, "Quake", glfwGetPrimaryMonitor(), NULL);
		if (window == NULL) throw new RuntimeException("Failed to create the GLFW window");

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
		glfwMakeContextCurrent(window);
		glfwSwapInterval(1);
		// your code to initialize the scene goes here...
		glfwShowWindow(window);
	}

	// sets a perspective projection
	public static void setPerspective(float fovy, float aspect, float near, float far) {
		float bottom = -near * (float) Math.tan(fovy / 2);
		float top = -bottom;
		float left = aspect * bottom;
		float right = -left;
		glFrustum(left, right, bottom, top, near, far);
	}

	private void loop() throws Exception{
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		setPerspective((float)(Math.toRadians(40)), WINDOW_WIDTH/WINDOW_HEIGHT, 0.01f, 100f);
		glEnable(GL_TEXTURE_2D);
		glEnable(GL_SMOOTH);
		glEnable(GL_DEPTH_TEST);
		// set up lighting
		FloatBuffer ambient = BufferUtils.createFloatBuffer(4);
		ambient.put(new float[] { 0.6f, 0.65f, 0.65f, 1f, });
		ambient.flip();
		FloatBuffer specular = BufferUtils.createFloatBuffer(4);
		specular.put(new float[] { 0.8f, 0.8f, 0.8f, 1f, });
		specular.flip();
		FloatBuffer position = BufferUtils.createFloatBuffer(4);
		position.put(new float[] { 0f, 5f, -5f, 1f, });
		position.flip();
		FloatBuffer spot_dir = BufferUtils.createFloatBuffer(4);
		spot_dir.put(new float[] { 0f, -5f, -5f, 0f, });
		spot_dir.flip();

		/*
		//Default Lighting; NOT USED
		glEnable(GL_LIGHTING);
		glEnable(GL_LIGHT0);
		glLightModelfv(GL_LIGHT_MODEL_AMBIENT, ambient);
		glLightModeli(GL_LIGHT_MODEL_LOCAL_VIEWER, GL_FALSE);
		glLightfv(GL_LIGHT0, GL_POSITION, position);
		glLightfv(GL_LIGHT0, GL_DIFFUSE, specular);
		glLightfv(GL_LIGHT0, GL_SPOT_DIRECTION, spot_dir);
		glLightf(GL_LIGHT0, GL_SPOT_CUTOFF, 45.0f);
		glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		 */

		objects.add(new MeshObject("Resource/Models/NMap.obj"));

		while (!glfwWindowShouldClose(window)){
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			update(); //For keyboard + mouse updates
			render();
			glfwSwapBuffers(window);
			glfwPollEvents();
		}
	}

	private void update(){
		dx = getCursorX() - WINDOW_WIDTH/2;
		dy = getCursorY() - WINDOW_HEIGHT/2;
		//First: change in x on the screen
	}

	private void render(){
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
		glRotatef((float)dx/(float)2.0, 0.0f, 1.0f, 0.0f);
		glRotatef((float)dy/(float)2.0, 1.0f, 0.0f, 0.0f);
		if (movement[0]) tz += 0.1;
		else if (movement[1]) tz -= 0.1;
		if (movement[2]) tx += 0.1;
		else if (movement[3]) tx -= 0.1;
		System.out.println(tx + " " + ty + " " + tz);
		//Note: tx is for left/right, tz is for forward/back
		glTranslatef(tx, ty, tz); //NOTE: Only the x value changes; the height never changes
		for (MeshObject object: objects) object.draw(); //Draw the objects
	}

	//Finds the x-value of the current position of the cursor on the screen.
	public double getCursorX(){
		DoubleBuffer posX = BufferUtils.createDoubleBuffer(1);
		glfwGetCursorPos(window, posX, null);
		return posX.get(0);
	}

	//Finds the y-value of the current position of the cursor on the screen.
	public double getCursorY(){
		DoubleBuffer posY = BufferUtils.createDoubleBuffer(1);
		glfwGetCursorPos(window, null, posY);
		return posY.get(0);
	}
}