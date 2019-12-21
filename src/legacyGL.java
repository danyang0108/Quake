import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class legacyGL {

	// The window handle
	private long window;
	//Camera cam;
	private static final int WINDOW_WIDTH = 800;
	private static final int WINDOW_HEIGHT = 800;
	double fps = 50;

	public void run() {
		System.out.println("Hello LWJGL " + Version.getVersion() + "!");


		init();
		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		GL.createCapabilities();
		
		 System.out.println("OpenGL version " + glGetString(GL_VERSION));
		
		loop();

		// Free the window callbacks and destroy the window
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);

		// Terminate GLFW and free the error callback
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}

	private void init() {
		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFWErrorCallback.createPrint(System.err).set();

		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if ( !glfwInit() )
			throw new IllegalStateException("Unable to initialize GLFW");

		// Configure GLFW
		glfwDefaultWindowHints(); // optional, the current window hints are already the default
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

		// Create the window
		window = glfwCreateWindow(WINDOW_WIDTH, WINDOW_HEIGHT, "Hello World!", NULL, NULL);
		if ( window == NULL )
			throw new RuntimeException("Failed to create the GLFW window");

		// Setup a key callback. It will be called every time a key is pressed, repeated or released.
		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
				glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
		});

		// Get the thread stack and push a new frame
		try ( MemoryStack stack = stackPush() ) {
			IntBuffer pWidth = stack.mallocInt(1); // int*
			IntBuffer pHeight = stack.mallocInt(1); // int*

			// Get the window size passed to glfwCreateWindow
			glfwGetWindowSize(window, pWidth, pHeight);

			// Get the resolution of the primary monitor
			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

			// Center the window
			glfwSetWindowPos(
				window,
				(vidmode.width() - pWidth.get(0)) / 2,
				(vidmode.height() - pHeight.get(0)) / 2
			);
		} // the stack frame is popped automatically

		// Make the OpenGL context current
		glfwMakeContextCurrent(window);
		// Enable v-sync
		glfwSwapInterval(1);
		
		// your code to initialize the scene goes here...

		// Make the window visible
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

	private void loop() {

		glMatrixMode(GL_PROJECTION);
	    glLoadIdentity();
	    setPerspective((float)(Math.toRadians(40)), WINDOW_WIDTH/WINDOW_HEIGHT, 0.01f, 100f);
		
		
		glEnable(GL_TEXTURE_2D);	// enable texture mapping
		glEnable(GL_SMOOTH);
		glEnable(GL_DEPTH_TEST);
		
		// Load our textures


		// Set the clear color
		glClearColor(1.0f, 1.0f, 1.0f, 0.0f);

		// Run the rendering loop until the user has attempted to close
		// the window or has pressed the ESCAPE key.
		while ( !glfwWindowShouldClose(window) ) {
			
			// check for ESCAPE key pressed to exit program
			if(glfwGetKey(window, GLFW_KEY_ESCAPE) == GL_TRUE)
			{
				glfwSetWindowShouldClose(window, true);
			}
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
			
			glMatrixMode(GL_MODELVIEW);
			glLoadIdentity();

			// draw your scene here...
			// first draw a line with thickness 5
			glLineWidth(5);
			glBegin(GL_LINES);
				glColor3f(1, 0, 0);
				glVertex3f(0, 0, -5);
				glColor3f(0, 1, 0);
				glVertex3f(1, -1, -5);
			glEnd();
			
			// Now draw a triangle with each vertex a different color
			glLineWidth(1);
				glBegin(GL_TRIANGLES);
				glColor3f(0, 1, 0);
				glVertex3f(-1, 0, -5);
				glColor3f(0, 0, 1);
				glVertex3f(0, 1.5f, -5);
				glColor3f(1, 0, 0);
				glVertex3f(1, 1, -5);
				glEnd();


			glfwSwapBuffers(window); // swap the color buffers

			// Poll for window events. The key callback above will only be
			// invoked during this call.
			glfwPollEvents();
		}
	}

	public static void main(String[] args) {
		new legacyGL().run();
	}

}