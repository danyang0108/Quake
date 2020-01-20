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
	private int sizeX = 24, sizeZ = 21;
	private MeshObject MAP, GUN, MEDKIT, AMMOPACK;
	private Boolean[][] vis = new Boolean[sizeX][sizeZ];
	private ArrayList<MeshObject> keyframes = new ArrayList<>();
	private float TX = 0, TZ = 0; //For actual translations
	private int curHealth = 100;
	private int curAmmo = 30;
	private int totalAmmo = 90;
	private int maxHealth = 100;
	private int maxRound = 30;
	private boolean[] movement = new boolean[5]; //For keyboard controls (W, S, A, D, SHIFT)
	private boolean mouse = false;
	private ArrayList<Enemy> enemies = new ArrayList<>();
	private ArrayList<Point2f> space = new ArrayList<>(); //Spaces in map
	private ArrayList<Point2f> medPos = new ArrayList<>(); //Track medical kit positions
	private ArrayList<Point2f> ammoPos = new ArrayList<>(); //Track ammo kit positions
	private ArrayList<Boolean> probability = new ArrayList<>();
	private Point4f move;
	private Texture enemyTex, mapTex, gunTex, charTex, medTex, ammoTex;
	private int offset = 32;
	private final Colour yellow = new Colour(255, 255, 0);
	private final Colour blue = new Colour(0, 0, 255);
	private int start = 0, end = 0;
	private long startTime = System.nanoTime();
	private long accumulate = 0;
	private long loadTime;
	private long elapsedTime;
	private boolean gameStart = true;
	
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
				if (button == GLFW_MOUSE_BUTTON_LEFT && curAmmo > 0){
					mouse = true;
					curAmmo--;
				}
				else if (button == GLFW_MOUSE_BUTTON_RIGHT && totalAmmo > 0){
					int required = maxRound - curAmmo;
					if (totalAmmo >= required){
						totalAmmo -= required;
						curAmmo += required;
					}else{
						curAmmo += totalAmmo;
						totalAmmo = 0;
					}
				}
			}else if (action == GLFW_RELEASE && button == GLFW_MOUSE_BUTTON_LEFT) mouse = false;
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

		int numFalse = 399;
		for (int i = 0; i < numFalse; i++) probability.add(false);
		probability.add(true);

		charTex = new Texture("Resource/Images/text.png");
		charTex.setPixel(0);
		mapTex = new Texture("Resource/Images/Wall.jpg");
		enemyTex = new Texture("Resource/Images/Enemy1.png");
		gunTex = new Texture("Resource/Images/Pistol.png");
		medTex = new Texture("Resource/Images/Medkit.png");
		ammoTex = new Texture("Resource/Images/Ammo.jpg");

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

		float gunScale = 0.09f;
		float kitScale = 0.8f;
		MAP = new MeshObject("Resource/Models/Map.obj");
		MAP.texture = mapTex;
		GUN = new MeshObject("Resource/Models/M9A1.obj");
		GUN.texture = gunTex;
		GUN.scale(new Point3f(gunScale, gunScale, gunScale));
		MEDKIT = new MeshObject("Resource/Models/MedKit.obj");
		MEDKIT.scale(new Point3f(kitScale, kitScale, kitScale));
		MEDKIT.texture = medTex;
		AMMOPACK = new MeshObject("Resource/Models/Ammo.obj");
		AMMOPACK.texture = ammoTex;

		String path2 = "Resource/Models/Move_000";
		//There are 360 frames in total for enemy animation.
		int frames = 360, ten = 10, hundred = 100;
		for (int i = 1; i <= frames; i++){
			String threeDigit;
			if (i < ten) threeDigit = "00" + i;
			else if (i < hundred) threeDigit = "0" + i;
			else threeDigit = Integer.toString(i);
			MeshObject animation = new MeshObject(path2 + threeDigit + ".obj");
			animation.texture = enemyTex;
			float enemyScaleX = 0.35f, enemyScaleY = 0.5f, enemyScaleZ = 0.5f;
			animation.scale(new Point3f(enemyScaleX, enemyScaleY, enemyScaleZ));
			keyframes.add(animation);
		}

		if (gameStart) {
			gameStart = false;
			loadTime = System.nanoTime();
		}
		
		while (!glfwWindowShouldClose(window)){
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

			//Draw the text and objects
			text();
			render();

			glfwSwapBuffers(window);
			glfwPollEvents();
		}
	}

	private void text() throws Exception{
		//Draw the text on the screen
		glTranslatef(-TX, 0, -TZ);
		if (move != null) glRotatef(-move.rot, 0, 1, 0);
		charTex.bind();
		elapsedTime = Math.round((System.nanoTime()-loadTime) / 1e9d);
		String health = "Health:" + curHealth + "/" + maxHealth;
		String ammo = "Ammo:" + curAmmo + "/" + totalAmmo;
		String time = "Time:" + elapsedTime; 
		//Take care of magic numbers
		float textX = -0.4f, textY = -0.31f, intervalY = 0.04f;
		float statX = 0.2f, statY = -0.31f;
		int fontSize = 6;
		drawText(health, textX, textY, fontSize);
		drawText(ammo, textX, textY - intervalY, fontSize);
		drawText(time, statX, statY, fontSize);
	}

	private void render() throws Exception{
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();

		moveUser(); //Handles keyboard input
		rotateUser(); //Handles mouse input

		ArrayList<Point2f> newKit = space;
		Collections.shuffle(probability);
		if (probability.get(0)){ //Add new medkit/ammo pack
			Random RD = new Random();
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
		float shiftGun = -0.5f, rotateGun = 270;
		GUN.translate(new Point3f(-TX, shiftGun, -TZ));
		GUN.rotate(new Point4f(rotateGun - move.rot, 0, 1, 0));
		GUN.draw();

		//Add new enemies
		long nowTime = System.nanoTime();
		accumulate += (nowTime - startTime);
		double spawnTime = 15;
		int enemyLimit = 4;
		if (accumulate / 1e9d >= spawnTime){
			accumulate = 0;
			if (enemies.size() < enemyLimit){ //Upper limit of 4
				Collections.shuffle(space);
				boolean finish = true;
				int index = 0;
				while (finish){
					Point2f generate = space.get(index);
					float coordX = generate.z - fixX;
					float coordZ = generate.x - fixZ;
					if (!nearUser(coordX, coordZ)){
						float shiftEnemy = -1f;
						Enemy newEnemy = new Enemy(new Point3f(coordX, shiftEnemy, coordZ));
						enemies.add(newEnemy);
						finish = false;
					}
				}
			}
		}
		startTime = nowTime;

		ArrayList<Point2f> removeMed = new ArrayList<>();
		for (Point2f p: medPos){
			float coordX = p.z - fixX;
			float coordZ = p.x - fixZ;
			if (nearUser(coordX, coordZ) && curHealth < maxHealth){
				//The medkit is used
				curHealth = maxHealth;
				removeMed.add(p);
			}else{
				MeshObject medkit = MEDKIT;
				float shiftKit = -2f;
				medkit.translate(new Point3f(coordX, shiftKit, coordZ));
				medkit.draw();
			}
		}
		for (Point2f p: removeMed) medPos.remove(p);

		ArrayList<Point2f> removeAmmo = new ArrayList<>();
		for (Point2f p: ammoPos){
			float coordX = p.z - fixX;
			float coordZ = p.x - fixZ;
			int maxAmmo = 90;
			if (nearUser(coordX, coordZ) && (curAmmo != maxRound || totalAmmo != maxAmmo)){
				//The medkit is used
				curAmmo = maxRound;
				totalAmmo = maxAmmo;
				removeAmmo.add(p);
			}else{
				MeshObject ammoPack = AMMOPACK;
				float shiftPack = -2f;
				ammoPack.translate(new Point3f(coordX, shiftPack, coordZ));
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
				double enemySpeed = 0.05;
				E.moveX = enemySpeed * Math.round(answer.x - E.shift.x);
				E.moveZ = enemySpeed * Math.round(answer.z - E.shift.z);
				E.turnToUser();
			}else if (nearUser(E.shift.x, E.shift.z) && !E.dead){
				double angle = Math.toDegrees(Math.atan2((E.shift.x + TX), (E.shift.z + TZ)));
				float fixAngle = 180;
				E.rotate = new Point4f(fixAngle + (float)angle, 0, 1, 0);
			}else if (!E.dead){
				E.shift.x += E.moveX;
				E.shift.z += E.moveZ;
				E.turnToUser();
			}
			temp.translate(E.shift);
			temp.rotate(E.rotate);
			temp.draw();
			if (E.updateFrame()) remove.add(i); //The enemy is dead
			if (dropHealth) curHealth -= 1;
		}
		//Sort in decreasing order
		Collections.sort(remove);
		//Remove dead enemies
		for (int i = remove.size()-1; i >= 0; i--) enemies.remove((int)remove.get(i));
	}

	private void moveUser(){
		//Handle keyboard and mouse control
		move = new Control().movement(window, movement);
		//Convert world coordinates into indices
		int Z = Math.round(-TZ - move.z) + fixZ;
		int X = Math.round(-TX - move.x) + fixX;
		//Check if the coordinate is accessible (i.e. not walking into a wall)
		if (vis[Z][X]){
			TX += move.x;
			TZ += move.z;
			//Also disable user from walking through enemies
			for (Enemy E: enemies){
				if (nearEnemy(E.shift.x, E.shift.z)){
					//If user is near an enemy, cancel the movement
					TX -= move.x;
					TZ -= move.z;
					break;
				}
			}
			glTranslatef(TX, 0, TZ);
			return;
		}
		//Check if the user can travel in one direction (check description)
		Z = Math.round(-TZ) + fixZ;
		if (vis[Z][X]){
			TX += move.x;
			for (Enemy E: enemies){
				if (nearEnemy(E.shift.x, E.shift.z)){
					TX -= move.x;
					break;
				}
			}
			glTranslatef(TX, 0, TZ);
			return;
		}
		//If this part is reached, the other direction is checked
		X = Math.round(-TX) + fixX;
		if (vis[Z][X]){
			TZ += move.z;
			for (Enemy E: enemies){
				if (nearEnemy(E.shift.x, E.shift.z)){
					TZ -= move.z;
					break;
				}
			}
			glTranslatef(TX, 0, TZ);
		}
	}

	private void rotateUser(){
		if (mouse){
			double ratio = 0.1;
			double faceX = ratio * Math.sin(Math.toRadians(-move.rot));
			double faceZ = ratio * Math.cos(Math.toRadians(-move.rot));
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
	}

	private void drawText(String text, float x, float y, int fontSize) throws Exception{
		text = text.toUpperCase();
		text = text.replace("", " ");
		float startX = x, startY = y;
		for (int i = 0; i < text.length(); i++){
			int ascii = text.charAt(i) - offset;
			int charCnt = 0;
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
			double adjust = -0.4;
			glVertex3d(startX, startY, adjust);
			glTexCoord2d((double)end / charTex.getBI().getWidth(), 0);
			glVertex3d(endX, startY, adjust);
			glTexCoord2d((double)end / charTex.getBI().getWidth(), 1);
			glVertex3d(endX, endY, adjust);
			glTexCoord2d((double)start / charTex.getBI().getWidth(), 1);
			glVertex3d(startX, endY, adjust);
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
		double half = 0.5;
		double smallX = Math.abs(half - fracX);
		double smallZ = Math.abs(half - fracZ);
		int actualX, actualZ;
		if (fracX < half){
			if (fracZ < half){
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
			if (fracZ < half){
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
