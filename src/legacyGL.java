import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import java.io.File;
import java.util.Collections;
import java.util.Random;
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
	private MeshObject MAP, GUN, MEDKIT, AMMOPACK;
	private Boolean[][] vis = new Boolean[24][21];
	private ArrayList<MeshObject> keyframes = new ArrayList<>();
	private float TX = 0, TZ = 0; //For actual translations
	private int curHealth = 100;
	private int curAmmo = 30;
	private int totalAmmo = 90;
	private boolean[] movement = new boolean[5]; //For keyboard controls (W, S, A, D, SHIFT)
	private boolean mouse = false;
	private ArrayList<Enemy> enemies = new ArrayList<>();
	private ArrayList<Point2f> space = new ArrayList<>(); //Spaces in map
	private ArrayList<Point2f> medPos = new ArrayList<>(); //Track medical kit positions
	private ArrayList<Point2f> ammoPos = new ArrayList<>(); //Track ammo kit positions
	private ArrayList<Boolean> probability = new ArrayList<>();
	private Point4f move;
	private Texture enemyTex, mapTex, gunTex, charTex, medTex, ammoTex;
	private int charCnt = 0;
	private int offset = 32;
	private final Colour yellow = new Colour(255, 255, 0);
	private final Colour blue = new Colour(0, 0, 255);
	private int start = 0, end = 0;

	public static void main(String[] args) throws Exception{
		new legacyGL().run();
	}

	public void run() throws Exception{
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
			if (action == GLFW_PRESS){
				if (button == GLFW_MOUSE_BUTTON_LEFT && curAmmo > 0) {
					mouse = true;
					curAmmo -= 1;
				}
				else if (button == GLFW_MOUSE_BUTTON_RIGHT && totalAmmo > 0) {
					int required = 30 - curAmmo;
					if (totalAmmo >= required) {
						totalAmmo -= required;
						curAmmo += required;
					}
					else {
						curAmmo += totalAmmo;
						totalAmmo = 0;
					}
				}
			}else if (action == GLFW_RELEASE){
				if (button == GLFW_MOUSE_BUTTON_LEFT) mouse = false;
			}
		});
		//This line is to not show the cursor on the screen. It's to allow unlimited movement.
		glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
		glfwMakeContextCurrent(window);
		glfwSwapInterval(1);
		GL.createCapabilities();

		Scanner maze = new Scanner(new File("Resource/Models/Map.txt"));
		int counter = 0;
		while (maze.hasNextLine()){
			String[] line = maze.nextLine().split(" ");
			for (int i = 0; i < line.length; i++){
				vis[counter][i] = Integer.parseInt(line[i]) == 1;
				if (Integer.parseInt(line[i]) == 1) space.add(new Point2f(counter, i));
			}
			counter++;
		}

		for (int i = 0; i < 499; i++) probability.add(false);
		for (int i = 0; i < 1; i++) probability.add(true);

		charTex = new Texture("Resource/Images/text.png");
		charTex.setPixel(0);
		mapTex = new Texture("Resource/Images/Wall.jpg");
		enemyTex = new Texture("Resource/Images/Enemy1.png");
		gunTex = new Texture("Resource/Images/Pistol.png");
		medTex = new Texture("Resource/Images/Medkit.png");
		ammoTex = new Texture("Resource/Images/Ammo.png");

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

		MAP = new MeshObject("Resource/Models/Map.obj");
		MAP.texture = mapTex;
		GUN = new MeshObject("Resource/Models/M9A1.obj");
		GUN.texture = gunTex;
		GUN.scale(new Point3f(0.09f, 0.09f, 0.09f));
		MEDKIT = new MeshObject("Resource/Models/MedKit.obj");
		MEDKIT.scale(new Point3f(0.8f, 0.8f, 0.8f));
		MEDKIT.texture = medTex;
		AMMOPACK = new MeshObject("Resource/Models/Ammo.obj");
		AMMOPACK.texture = ammoTex;

		long START = System.nanoTime();

		String path2 = "Resource/Models/Move_000";
		int frames = 360; //There are 360 frames in total for enemy animation.
		for (int i = 1; i <= frames; i++){
			String threeDigit;
			if (i < 10) threeDigit = "00" + i;
			else if (i < 100) threeDigit = "0" + i;
			else threeDigit = Integer.toString(i);
			MeshObject animation = new MeshObject(path2 + threeDigit + ".obj");
			animation.texture = enemyTex;
			animation.scale(new Point3f(0.35f, 0.5f, 0.5f));
			keyframes.add(animation);
		}

		long END = System.nanoTime();
		System.out.println("TIME: " + (END - START) / 1e9d);

		Enemy second = new Enemy(new Point3f(1, -1, -1), new Point4f(0, 0, 0, 0));
		enemies.add(second);

		while (!glfwWindowShouldClose(window)){
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			glTranslatef(-TX, 0, -TZ);
			if (move != null) glRotatef(-move.rot, 0, 1, 0);
			charTex.bind();
			String health = "Health:" + curHealth + "/100";
			String ammo = "Ammo:" + curAmmo + "/" + totalAmmo;
			drawText(health, -0.4f, -0.31f, 6);
			drawText(ammo, -0.4f, -0.35f, 6);
			render();
			glfwSwapBuffers(window);
			glfwPollEvents();
		}
	}

	private void render() throws Exception{
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();

		move = new Control().movement(window, movement);
		int Z = Math.round(-TZ - move.z) + fixZ;
		int X = Math.round(-TX - move.x) + fixX;
		if (vis[Z][X]){
			TX += move.x;
			TZ += move.z;
			for (Enemy E: enemies){
				if (nearEnemy(E.shift.x, E.shift.z)){
					TX -= move.x;
					TZ -= move.z;
					break;
				}
			}
		}else{
			Z = Math.round(-TZ) + fixZ;
			if (vis[Z][X]){
				TX += move.x;
				for (Enemy E: enemies){
					if (nearEnemy(E.shift.x, E.shift.z)){
						TX -= move.x;
						break;
					}
				}
			}else{
				X = Math.round(-TX) + fixX;
				if (vis[Z][X]){
					TZ += move.z;
					for (Enemy E: enemies){
						if (nearEnemy(E.shift.x, E.shift.z)){
							TZ -= move.z;
							break;
						}
					}
				}
			}
		}
		glTranslatef(TX, 0, TZ);

		if (mouse){
			double faceX = 0.1 * Math.sin(Math.toRadians(-move.rot));
			double faceZ = 0.1 * Math.cos(Math.toRadians(-move.rot));
			double curX = TX + faceX, curZ = TZ + faceZ;
			boolean cont = true;
			while (inMap(curX, curZ) && cont){
				for (Enemy E : enemies){
					if (E.hit(curX, curZ)){
						E.health--;
						cont = false;
					}
				}
				curX += faceX;
				curZ += faceZ;
			}
		}
		mouse = false;

		Collections.shuffle(probability);
		if (probability.get(0)){ //Add new medkit/ammo pack
			Random RD = new Random();
			ArrayList<Point2f> newKit = space;
			ArrayList<Integer> removed = new ArrayList<>();
			for (int i = 0; i < newKit.size(); i++){
				if (medPos.contains(newKit.get(i))) removed.add(i);
				else if (ammoPos.contains(newKit.get(i))) removed.add(i);
			}
			for (int i: removed) newKit.remove(i); //This is the remaining space
			Point2f userRounded = roundUser(-TZ + fixZ, -TX + fixX);
			newKit.remove(userRounded); //Don't generate at user position
			if (newKit.size() > 0){
				Collections.shuffle(newKit);
				Point2f addPoint = newKit.get(0);
				//Define TRUE as medkit, FALSE as ammo pack
				if (RD.nextBoolean()) medPos.add(addPoint);
				else ammoPos.add(addPoint);
			}//Otherwise, there are no more spots to place packs (Shouldn't happen)
		}

		//Draw the objects
		MAP.draw();
		GUN.translate(new Point3f(-TX, -0.5f, -TZ));
		GUN.rotate(new Point4f(270 - move.rot, 0, 1, 0));
		GUN.draw();

		//System.out.println(medPos.size() + " " + ammoPos.size());
		ArrayList<Point2f> removeMed = new ArrayList<>();
		for (Point2f p: medPos){
			float coordX = p.z - fixX;
			float coordZ = p.x - fixZ;
			if (nearUser(coordX, coordZ)){
				//The medkit is used
				curHealth = 100;
				removeMed.add(p);
			}else{
				MeshObject medkit = MEDKIT;
				medkit.translate(new Point3f(coordX, -2f, coordZ));
				medkit.draw();
			}
		}
		for (Point2f p: removeMed) medPos.remove(p);

		ArrayList<Point2f> removeAmmo = new ArrayList<>();
		for (Point2f p: ammoPos){
			float coordX = p.z - fixX;
			float coordZ = p.x - fixZ;
			if (nearUser(coordX, coordZ)){
				//The medkit is used
				curAmmo = 30;
				totalAmmo = 90;
				removeAmmo.add(p);
			}else{
				MeshObject ammoPack = AMMOPACK;
				ammoPack.translate(new Point3f(coordX, -2f, coordZ));
				ammoPack.draw();
			}
		}
		for (Point2f p: removeAmmo) ammoPos.remove(p);

		ArrayList<Integer> remove = new ArrayList<>();
		for (int i = 0; i < enemies.size(); i++){
			boolean dropHealth = false;
			Enemy E = enemies.get(i);
			if (E.health <= 0 && !E.dead){
				//One time only
				E.setChoice(2);
				E.dead = true;
			}else if (nearUser(E.shift.x, E.shift.z) && !E.punch){
				E.setChoice(1);
				E.punch = true;
				dropHealth = true;
			};
			int choice = E.choice; //Which animation it's in
			MeshObject temp;
			if (choice == 0) temp = keyframes.get(E.WF);
			else if (choice == 1) temp = keyframes.get(E.WS + E.PF);
			else temp = keyframes.get(E.WS + E.PS + E.DF);
			Point2f userRounded = roundUser(-TZ + fixZ, -TX + fixX);
			if (E.walk == 0 && !nearUser(E.shift.x, E.shift.z) && !E.dead){
				Point2f answer = E.findUser(userRounded.x, userRounded.z);
				E.moveX = 0.05d * Math.round(answer.x - E.shift.x);
				E.moveZ = 0.05d * Math.round(answer.z - E.shift.z);
				E.turnToUser();
			}else if (nearUser(E.shift.x, E.shift.z) && !E.dead){
				float angle = (float)Math.toDegrees(Math.atan2((E.shift.x + TX), (E.shift.z + TZ)));
				E.rotate = new Point4f(180 + angle, 0, 1, 0);
			}else if (!E.dead){
				E.shift.x += E.moveX;
				E.shift.z += E.moveZ;
				E.turnToUser();
			}
			temp.translate(E.shift);
			temp.rotate(E.rotate);
			temp.draw();
			if (dropHealth) {
				curHealth -= 1;
			}
			//Display health bar
			if (E.updateFrame()) remove.add(i); //The enemy is dead
		}
		//Sort in decreasing order
		Collections.sort(remove);
		//Remove dead enemies
		for (int i = remove.size()-1; i >= 0; i--) enemies.remove((int)remove.get(i));
	}

	public void drawText(String text, float x, float y, int fontSize) throws Exception{
		text = text.toUpperCase();
		text = text.replace("", " ");
		float startX = x, startY = y;
		for (int i = 0; i < text.length(); i++){
			int ascii = text.charAt(i) - offset;
			charCnt = 0;
			for (int j = 0; j < charTex.getBI().getWidth(); j++){
				Colour c = charTex.getPixel(j, 0);
				if (charCnt > ascii)
					break;
				if (c.getR() == yellow.getR() && c.getG() == yellow.getG() && c.getB() == yellow.getB()){
					charCnt++;
					end = j;
				}
				if (c.getR() == blue.getR() && c.getG() == blue.getG() && c.getB() == blue.getB()){
					start = j + 1;
				}
			}
			int x_length = end - start;
			float endX = (startX * WINDOW_WIDTH + x_length * fontSize) / WINDOW_WIDTH;
			float endY = (startY * WINDOW_HEIGHT - charTex.getBI().getHeight() * fontSize) / WINDOW_HEIGHT;
			glBegin(GL_QUADS);
			glTexCoord2d((double)start / charTex.getBI().getWidth(), 0);
			glVertex3d(startX, startY, -0.4);
			glTexCoord2d((double)end / charTex.getBI().getWidth(), 0);
			glVertex3d(endX, startY, -0.4);
			glTexCoord2d((double)end / charTex.getBI().getWidth(), 1);
			glVertex3d(endX, endY, -0.4);
			glTexCoord2d((double)start / charTex.getBI().getWidth(), 1);
			glVertex3d(startX, endY, -0.4);
			glEnd();
			startX = endX;
		}
	}

	public boolean inMap(double x, double z){
		int FX = -(int)Math.round(z);
		int FZ = -(int)Math.round(x);
		if (FX > -fixX && FX < fixX && FZ > 1-fixZ && FZ < fixZ){
			return vis[FX + fixZ][FZ + fixX];
		}
		return false;
	}

	public boolean nearUser(double x, double z){
		double enemyReach = 1.5;
		return (x + TX) * (x + TX) + (z + TZ) * (z + TZ) <= enemyReach * enemyReach;
	}

	public boolean nearEnemy(double x, double z){
		double userReach = 1.0;
		return (x + TX) * (x + TX) + (z + TZ) * (z + TZ) <= userReach * userReach;
	}

	public Point2f roundUser(double x, double z){
		//NOTE: +LEFT, +UP
		double fracX = x - Math.floor(x); //From point to right
		double fracZ = z - Math.floor(z); //From point to bottom
		double smallX = Math.abs(0.5 - fracX);
		double smallZ = Math.abs(0.5 - fracZ);
		int actualX, actualZ;
		if (fracX < 0.5){
			if (fracZ < 0.5){
				//Bottom-Right section
				//First quadrant check
				actualX = (int)Math.floor(x);
				actualZ = (int)Math.floor(z);
				if (vis[actualX][actualZ]) return new Point2f(actualX, actualZ);
				//Check the rest of the quadrants
				if (smallX <= smallZ){
					//Bottom-Left
					if (vis[actualX + 1][actualZ]) return new Point2f(actualX + 1, actualZ);
					//Top-Right
					if (vis[actualX][actualZ + 1]) return new Point2f(actualX, actualZ + 1);
				}else{
					//Top-Right
					if (vis[actualX][actualZ + 1]) return new Point2f(actualX, actualZ + 1);
					//Bottom-Left
					if (vis[actualX + 1][actualZ]) return new Point2f(actualX + 1, actualZ);
				}
				//Corner
				if (vis[actualX + 1][actualZ + 1]) return new Point2f(actualX + 1, actualZ + 1);
				else return new Point2f(-1, -1); //SHOULD NOT REACH
			}else{
				//Top-Right Section
				actualX = (int)Math.floor(x);
				actualZ = (int)Math.ceil(z);
				if (vis[actualX][actualZ]) return new Point2f(actualX, actualZ);
				//Check the rest of the quadrants
				if (smallX <= smallZ){
					//Top-Left
					if (vis[actualX + 1][actualZ]) return new Point2f(actualX + 1, actualZ);
					//Bottom-Right
					if (vis[actualX][actualZ - 1]) return new Point2f(actualX, actualZ - 1);
				}else{
					//Bottom-Right
					if (vis[actualX][actualZ - 1]) return new Point2f(actualX, actualZ - 1);
					//Top-Left
					if (vis[actualX + 1][actualZ]) return new Point2f(actualX + 1, actualZ);
				}
				//Corner
				if (vis[actualX + 1][actualZ - 1]) return new Point2f(actualX + 1, actualZ - 1);
				else return new Point2f(-1, -1); //SHOULD NOT REACH
			}
		}else{
			if (fracZ < 0.5){
				//Bottom-Left section
				actualX = (int)Math.ceil(x);
				actualZ = (int)Math.floor(z);
				if (vis[actualX][actualZ]) return new Point2f(actualX, actualZ);
				//Check the rest of the quadrants
				if (smallX <= smallZ){
					//Bottom-Right
					if (vis[actualX - 1][actualZ]) return new Point2f(actualX - 1, actualZ);
					//Top-Left
					if (vis[actualX][actualZ + 1]) return new Point2f(actualX, actualZ + 1);
				}else{
					//Top-Left
					if (vis[actualX][actualZ + 1]) return new Point2f(actualX, actualZ + 1);
					//Bottom-Right
					if (vis[actualX - 1][actualZ]) return new Point2f(actualX - 1, actualZ);
				}
				//Corner
				if (vis[actualX - 1][actualZ + 1]) return new Point2f(actualX - 1, actualZ + 1);
				else return new Point2f(-1, -1); //SHOULD NOT REACH
			}else{
				//Top-Left section
				actualX = (int)Math.ceil(x);
				actualZ = (int)Math.ceil(z);
				if (vis[actualX][actualZ]) return new Point2f(actualX, actualZ);
				//Check the rest of the quadrants
				if (smallX <= smallZ){
					//Top-Right
					if (vis[actualX - 1][actualZ]) return new Point2f(actualX - 1, actualZ);
					//Bottom-Left
					if (vis[actualX][actualZ - 1]) return new Point2f(actualX, actualZ - 1);
				}else{
					//Bottom-Left
					if (vis[actualX][actualZ - 1]) return new Point2f(actualX, actualZ - 1);
					//Top-Right
					if (vis[actualX - 1][actualZ]) return new Point2f(actualX - 1, actualZ);
				}
				//Corner
				if (vis[actualX - 1][actualZ - 1]) return new Point2f(actualX - 1, actualZ - 1);
				else return new Point2f(-1, -1); //SHOULD NOT REACH
			}
		}
	}
}
