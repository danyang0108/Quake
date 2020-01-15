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

public class testing {

	// The window handle
	private long window;
	//Camera cam;
	private static final int WINDOW_WIDTH = 800;
	private static final int WINDOW_HEIGHT = 800;
	double fps = 50;
	Texture tex;
	int charCnt = 0;
	Colour yellow = new Colour(255,255,0);
	Colour blue = new Colour(0,0,255);
	int start = 0;
	int end = 0;
	
	public void run()  {
		System.out.println("Hello LWJGL " + Version.getVersion() + "!");


		init();
		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		GL.createCapabilities();
		
		//TEXTURES
		
		try {
			tex = new Texture("Resource/Images/text.png");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("I want to die");
			e.printStackTrace();
		}
		
		//
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
	    //setPerspective((float)(Math.toRadians(40)), WINDOW_WIDTH/WINDOW_HEIGHT, 0.01f, 100f);
		
		
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
			tex.bind();
			/*Colour c;
			try {
				for (int i = 0; i < 308; i++) {
					c = tex.getPixel("Resource/Images/text.png",i, 0);
					if (c.getR() == yellow.getR() && c.getG() == yellow.getG() && c.getB() == yellow.getB()) {
						charCnt++;
					}
					if (c.getR() == blue.getR() && c.getG() == blue.getG() && c.getB() == blue.getB()) {
						System.out.println("blue");
					}
					//System.out.println(c.getR());
					//System.out.println(c.getG());
					//System.out.println(c.getB());
				}
				System.out.println(charCnt);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			drawText(" h e l l o  b o o m e r ! ",-0.75f,-0.5f);
			/*glBegin(GL_QUADS);
			glTexCoord2f(32f/308f,0);
			glVertex2f(-0.4f, 0.8f);
			glTexCoord2f(35f/308f,0);
			glVertex2f(0.4f, 0.8f);
			glTexCoord2f(35f/308f,1f);
			glVertex2f(0.4f, -0.8f);
			glTexCoord2f(32f/308f,1f);
			glVertex2f(-0.4f, -0.8f);
			
			glEnd();*/
			
			glfwSwapBuffers(window); // swap the color buffers

			// Poll for window events. The key callback above will only be
			// invoked during this call.
			glfwPollEvents();
		}
	}

	public static void main(String[] args) {
		new testing().run();
	}

										//-0.75,-0.5 
	public void drawText(String text, float x, float y) {
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
			float endX = (startX * WINDOW_WIDTH + x_length*20) / WINDOW_WIDTH;
			float endY = (startY * WINDOW_HEIGHT - 6*20) / WINDOW_HEIGHT;
			System.out.println(startX + " " + startY + " " + endX + " " + endY);
			//System.out.println(start + " " + end);
			glBegin(GL_QUADS);
			glTexCoord2f(start/308f,0);
			glVertex2f(startX, startY);
			glTexCoord2f(end/308f,0);
			glVertex2f(endX, startY);
			glTexCoord2f(end/308f,1f);
			glVertex2f(endX, endY);
			glTexCoord2f(start/308f,1f);
			glVertex2f(startX, endY);
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
}
