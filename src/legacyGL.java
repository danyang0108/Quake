import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import java.nio.*;
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
	float tx = 0, ty = 0, tz = 0; //For translations per frame
	float TX = 0, TY = 0, TZ = 0; //For actual translations
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
		//This line is to not show the cursor on the screen. It's to allow unlimited movement.
		glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
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
		setPerspective((float)(Math.toRadians(40)), WINDOW_WIDTH/WINDOW_HEIGHT, 0.05f, 100f);
		glEnable(GL_TEXTURE_2D);
		glEnable(GL_SMOOTH);
		glEnable(GL_DEPTH_TEST);

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

		//Calculations
		double degreeX = (360.0 / WINDOW_WIDTH * dx + 360.0) % 360.0; //Up to 180 degrees for x
		System.out.println("SPIN: " + degreeX);
		double degreeY = (180.0 / WINDOW_HEIGHT * dy + 360.0) % 360.0; //Only 90 degrees for y
		//Note: Half the screen for x => 180 degrees
		glRotatef((float)degreeX, 0.0f, 1.0f, 0.0f);
		//glRotatef((float)degreeY, 1.0f, 0.0f, 0.0f); Oh please stop my headache
		tx = ty = tz = 0;
		if (degreeX >= 270 && degreeX <= 360){
			//Front-left
			double RAA = 360.0 - degreeX;
			if (movement[0]){
				tx += (float)0.1 * Math.sin(Math.toRadians(RAA));
				tz += (float)0.1 * Math.cos(Math.toRadians(RAA));
			}
			if (movement[1]){
				tx -= (float)0.1 * Math.sin(Math.toRadians(RAA));
				tz -= (float)0.1 * Math.cos(Math.toRadians(RAA));
			}
			if (movement[2]){
				tx += (float)0.1 * Math.cos(Math.toRadians(RAA));
				tz -= (float)0.1 * Math.sin(Math.toRadians(RAA));
			}
			if (movement[3]){
				tx -= (float)0.1 * Math.cos(Math.toRadians(RAA));
				tz += (float)0.1 * Math.sin(Math.toRadians(RAA));
			}
		}else if (degreeX >= 0 && degreeX <= 90){
			//Front-right
			if (movement[0]){
				tx -= (float)0.1 * Math.sin(Math.toRadians(degreeX));
				tz += (float)0.1 * Math.cos(Math.toRadians(degreeX));
			}
			if (movement[1]){
				tx += (float)0.1 * Math.sin(Math.toRadians(degreeX));
				tz -= (float)0.1 * Math.cos(Math.toRadians(degreeX));
			}
			if (movement[2]){
				tx += (float)0.1 * Math.cos(Math.toRadians(degreeX));
				tz += (float)0.1 * Math.sin(Math.toRadians(degreeX));
			}
			if (movement[3]){
				tx -= (float)0.1 * Math.cos(Math.toRadians(degreeX));
				tz -= (float)0.1 * Math.sin(Math.toRadians(degreeX));
			}
		}else if (degreeX >= 90 && degreeX <= 180){
			//Back-right
			double RAA = 180.0 - degreeX;
			if (movement[0]){
				tx -= (float)0.1 * Math.sin(Math.toRadians(RAA));
				tz -= (float)0.1 * Math.cos(Math.toRadians(RAA));
			}
			if (movement[1]){
				tx += (float)0.1 * Math.sin(Math.toRadians(RAA));
				tz += (float)0.1 * Math.cos(Math.toRadians(RAA));
			}
			if (movement[2]){
				tx -= (float)0.1 * Math.cos(Math.toRadians(RAA));
				tz += (float)0.1 * Math.sin(Math.toRadians(RAA));
			}
			if (movement[3]){
				tx += (float)0.1 * Math.cos(Math.toRadians(RAA));
				tz -= (float)0.1 * Math.sin(Math.toRadians(RAA));
			}
		}else{
			//Back-left
			double RAA = degreeX - 180.0;
			if (movement[0]){
				tx += (float)0.1 * Math.sin(Math.toRadians(RAA));
				tz -= (float)0.1 * Math.cos(Math.toRadians(RAA));
			}
			if (movement[1]){
				tx -= (float)0.1 * Math.sin(Math.toRadians(RAA));
				tz += (float)0.1 * Math.cos(Math.toRadians(RAA));
			}
			if (movement[2]){
				tx -= (float)0.1 * Math.cos(Math.toRadians(RAA));
				tz -= (float)0.1 * Math.sin(Math.toRadians(RAA));
			}
			if (movement[3]){
				tx += (float)0.1 * Math.cos(Math.toRadians(RAA));
				tz += (float)0.1 * Math.sin(Math.toRadians(RAA));
			}
		}
		//Note: tx is for left/right, tz is for forward/back
		glTranslatef(TX + tx, TY + ty, TZ + tz);
		TX += tx;
		TY += ty;
		TZ += tz;
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