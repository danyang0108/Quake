///Author: Danyang Wang & Ethan Zhang
//Class: ICS4U
//Date: Jan 20th, 2020
//Instructor: Mr Radulovic
//Assignment name: ICS4U Culminating
/*Description: This class is for the main game. It checks for 
 * all interactions amongst User, Enemy and walls during the game.
 * 
 */
//Imports
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
	//Variables
	private long window;
	private final int WINDOW_WIDTH = 1366, WINDOW_HEIGHT = 768;
	private final int fixX = 10, fixZ = 12;
	private final int sizeX = 24, sizeZ = 21;
	private final int maxHealth = 100;
	private final int maxRound = 30;
	private final int offset = 32;
	private final int packLimit = 3;
	private final int enemyDamage = 5;
	private final double oneSecond = 1e9d;
	private boolean quitGame = false;
	private MeshObject map, gun, medKit, ammoPack;
	private Boolean[][] vis = new Boolean[sizeX][sizeZ];
	private ArrayList<MeshObject> keyframes = new ArrayList<>();
	private float TX = 0, TZ = 0; //For actual translations
	private boolean[] movement = new boolean[5]; //For keyboard controls (W, S, A, D, SHIFT)
	private boolean mouse = false;
	private ArrayList<Enemy> enemies = new ArrayList<>();
	private ArrayList<Point2f> space = new ArrayList<>(); //Spaces in map
	private ArrayList<Point2f> medPos = new ArrayList<>(); //Track medical kit positions
	private ArrayList<Point2f> ammoPos = new ArrayList<>(); //Track ammo kit positions
	private Point4f move;
	private Texture enemyTex, mapTex, gunTex, charTex, medTex, ammoTex;
	private final Colour yellow = new Colour(255, 255, 0);
	private final Colour blue = new Colour(0, 0, 255);
	private int start = 0, end = 0;
	private long startTime = System.nanoTime();
	private long accumulate = 0, accumulate2 = 0;
	private long timeTrack1, timeTrack2, timeTrack3;
	private boolean gameStart = true;
	private int elimination = 0;
	private User u = new User();
	private Enemy e = new Enemy(new Point3f(0, 0, 0));

	public static void main(String[] args) throws Exception{
		new legacyGL().run();
	}

	public int getElimination(){
		return elimination;
	}

	public int gettime(){
		return (int)(timeTrack1);
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
				if (button == GLFW_MOUSE_BUTTON_LEFT && u.getCurAmmo() > 0){
					mouse = true;
					u.attack();
				}
				else if (button == GLFW_MOUSE_BUTTON_RIGHT && u.getTotalAmmo() > 0){
					u.reload();
				}

			}else if (action == GLFW_RELEASE && button == GLFW_MOUSE_BUTTON_LEFT) mouse = false;
		});
		//This line is to not show the cursor on the screen. It's to allow unlimited movement.
		glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
		glfwMakeContextCurrent(window);
		glfwSwapInterval(1);
		GL.createCapabilities();

		//Load the adjacency matrix of the map (store wall positions)
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

		//Load the image texture
		charTex = new Texture("Resource/Images/text.png");
		charTex.setPixel(0);
		mapTex = new Texture("Resource/Images/Wall.jpg");
		enemyTex = new Texture("Resource/Images/Enemy1.png");
		gunTex = new Texture("Resource/Images/Pistol.png");
		medTex = new Texture("Resource/Images/Medkit.png");
		ammoTex = new Texture("Resource/Images/Ammo.jpg");

		glfwShowWindow(window);
		loop();
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

		//Create the objects and use the textures
		float gunScale = 0.09f;
		float kitScale = 0.8f;
		map = new MeshObject("Resource/Models/Map.obj");
		map.setTexture(mapTex);
		gun = new MeshObject("Resource/Models/M9A1.obj");
		gun.setTexture(gunTex);
		gun.scale(new Point3f(gunScale, gunScale, gunScale));
		medKit = new MeshObject("Resource/Models/MedKit.obj");
		medKit.scale(new Point3f(kitScale, kitScale, kitScale));
		medKit.setTexture(medTex);
		ammoPack = new MeshObject("Resource/Models/Ammo.obj");
		ammoPack.setTexture(ammoTex);
		String path2 = "Resource/Models/Move_000";
		//There are 360 frames in total for enemy animation.
		int frames = 360, ten = 10, hundred = 100;
		for (int i = 1; i <= frames; i++){
			String threeDigit;
			if (i < ten) threeDigit = "00" + i;
			else if (i < hundred) threeDigit = "0" + i;
			else threeDigit = Integer.toString(i);
			MeshObject animation = new MeshObject(path2 + threeDigit + ".obj");
			animation.setTexture(enemyTex);
			float enemyScaleX = 0.35f, enemyScaleY = 0.5f, enemyScaleZ = 0.5f;
			animation.scale(new Point3f(enemyScaleX, enemyScaleY, enemyScaleZ));
			keyframes.add(animation);
		}

		if (gameStart) {
			gameStart = false;
			timeTrack2 = System.nanoTime();
		}

		while (!glfwWindowShouldClose(window)){
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

			//Draw the text and objects
			text();
			render();
			if (quitGame) break;

			glfwSwapBuffers(window);
			glfwPollEvents();
		}
		glfwSetWindowShouldClose(window, true);
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}

	private void text() throws Exception{
		//Draw the text on the screen
		glTranslatef(-TX, 0, -TZ);
		if (move != null) glRotatef(-move.rot, 0, 1, 0);
		charTex.bind();
		timeTrack1 = Math.round((System.nanoTime() - timeTrack2) / oneSecond);
		String health = "Health:" + u.getHealth() + "/" + maxHealth;
		String ammo = "Ammo:" + u.getCurAmmo() + "/" + u.getTotalAmmo();
		String time = "Time:" + timeTrack1;
		String kills = "Kills:" + elimination;
		float textX = -0.3f, textY = -0.27f, intervalY = 0.03f;
		float statX = 0.15f, statY = -0.27f;
		int fontSize = 4;
		drawText(health, textX, textY, fontSize);
		drawText(ammo, textX, textY - intervalY, fontSize);
		drawText(time, statX, statY, fontSize);
		drawText(kills, statX, statY - intervalY, fontSize);
	}

	private void render() throws Exception{
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();

		moveUser(); //Handles keyboard input
		bullet(); //Handles bullets

		//Handles ammo and health packs
		long nowTime = System.nanoTime(); //Get current time
		accumulate2 += (nowTime - timeTrack3);
		double packTime = 5; //Packs are generated every 5 seconds
		if (accumulate2 / oneSecond >= packTime){
			accumulate2 = 0;
			if (medPos.size() < packLimit || ammoPos.size() < packLimit){
				//There are spaces left for packs
				ArrayList<Point2f> newKit = space; //Copy the spaces in the map
				ArrayList<Integer> removed = new ArrayList<>();
				for (int i = 0; i < newKit.size(); i++){
					if (medPos.contains(newKit.get(i))) removed.add(i);
					else if (ammoPos.contains(newKit.get(i))) removed.add(i);
					//If either occurs, then that spot already has a pack
				}
				for (int i: removed) newKit.remove(i); //This is the remaining space
				if (newKit.size() > 0){ //There are more space to place packs
					Collections.shuffle(newKit);
					Point2f addPoint = newKit.get(0);
					//If med packs have reached the limit, spawn ammo packs
					if (medPos.size() == packLimit) ammoPos.add(addPoint);
					//If ammo packs have reached the limit, spawn med packs
					else if (ammoPos.size() == packLimit) medPos.add(addPoint);
					else{
						//Choose which pack to spawn randomly
						Random RD = new Random();
						if (RD.nextBoolean()) medPos.add(addPoint);
						else ammoPos.add(addPoint);
					}
				}//Otherwise, there are no more spots to place packs (Shouldn't happen)
			}
		}
		timeTrack3 = nowTime;

		//Draw the objects
		map.draw();
		float shiftGun = -0.5f, rotateGun = 270;
		gun.translate(new Point3f(-TX, shiftGun, -TZ));
		gun.rotate(new Point4f(rotateGun - move.rot, 0, 1, 0));
		gun.draw();

		//Add new enemies
		double spawnTime = 15; //Enemies spawned every 15 seconds
		int enemyLimit = 4; //A total of 4 enemies allowed at one time
		nowTime = System.nanoTime();
		accumulate += (nowTime - startTime);
		if (accumulate / oneSecond >= spawnTime){
			accumulate = 0;
			if (enemies.size() < enemyLimit){ //Upper limit of 4
				Collections.shuffle(space); //Randomize the spawn point
				boolean finish = true;
				int index = 0;
				while (finish){
					Point2f generate = space.get(index);
					//Convert from indices to world coordinates
					float coordX = generate.getZ() - fixX;
					float coordZ = generate.getX() - fixZ;
					if (!nearUser(coordX, coordZ)){ //Do not generate right beside the user
						float shiftEnemy = -1f;
						Enemy newEnemy = new Enemy(new Point3f(coordX, shiftEnemy, coordZ));
						enemies.add(newEnemy);
						finish = false; //An enemy has been added
					}
				}
			}
		}
		startTime = nowTime;

		//Remove med packs that were consumed by the user
		ArrayList<Point2f> removeMed = new ArrayList<>();
		for (Point2f p: medPos){
			//Convert to local coordinates
			float coordX = p.getZ() - fixX;
			float coordZ = p.getX() - fixZ;
			if (nearUser(coordX, coordZ) && u.getHealth() < maxHealth){
				//If the user walked near it and user health is not full
				//The medkit is used
				u.setHealth(maxHealth);
				removeMed.add(p);
			}else{
				//It has not been used; display it normally
				MeshObject medkit = medKit;
				float shiftKit = -2f;
				medkit.translate(new Point3f(coordX, shiftKit, coordZ));
				medkit.draw();
			}
		}
		for (Point2f p: removeMed) medPos.remove(p); //Remove packs that were used

		//Remove ammo packs that were used by the user
		ArrayList<Point2f> removeAmmo = new ArrayList<>();
		for (Point2f p: ammoPos){
			//Convert to local coordinates
			float coordX = p.getZ() - fixX;
			float coordZ = p.getX() - fixZ;
			int maxAmmo = 90;
			if (nearUser(coordX, coordZ) && (u.getCurAmmo() != maxRound || u.getTotalAmmo() != maxAmmo)){
				//If the user walked near it and user ammo count is not full
				//The ammo pack is used
				u.setCurAmmo(maxRound);
				u.setTotalAmmo(maxAmmo);
				removeAmmo.add(p);
			}else{
				//It has not been used; display it normally
				MeshObject newPack = ammoPack;
				float shiftPack = -2f;
				newPack.translate(new Point3f(coordX, shiftPack, coordZ));
				newPack.draw();
			}
		}	
		for (Point2f p: removeAmmo) ammoPos.remove(p); //Remove packs that were used

		//Update enemies; remove dead ones
		ArrayList<Integer> remove = new ArrayList<>();
		for (int i = 0; i < enemies.size(); i++){
			boolean dropHealth = false;
			Enemy E = enemies.get(i);
			if (E.isDead()){ //The enemy is already dead
				if (E.updateFrame()){
					//The function returns true when the animation is completed
					elimination++; //Add to the kill count displayed on the screen
					remove.add(i); //Remove the dead enemy from the ArrayList
				}
				//Otherwise, continue the death animation.
				MeshObject temp = keyframes.get(E.getWS() + E.getPS() + E.getDF());
				//Shift the enemy to its local position
				temp.translate(E.getShift());
				temp.rotate(E.getRotate());
				temp.draw(); //Draw the enemy
				continue;
			}
			//If this point is reached, then the enemy is still alive
			if (E.getHealth() <= 0){
				//Check if the enemy is supposed to be dead
				E.setChoice(2); //2 represents dead
				E.setDead(true);
			}else if (nearUser(E.getShiftX(), E.getShiftZ()) && !E.getPunch()){
				//The enemy is near the user, and it has not started punching
				E.setChoice(1); //1 represents punch
				E.attack(); //Start the punching animation while dropping the user health
				dropHealth = true;
			};
			int choice = E.getChoice(); //Which animation it's in
			MeshObject temp;
			if (choice == 0) temp = keyframes.get(E.getWF()); //Walking
			else if (choice == 1) temp = keyframes.get(E.getWS() + E.getPF()); //Punching
			else temp = keyframes.get(E.getWS() + E.getPS() + E.getDF()); //Dying
			Point2f userRounded = roundUser(-TZ + fixZ, -TX + fixX);
			if (E.getWalk() == 0 && !nearUser(E.getShiftX(), E.getShiftZ())){
				//If the enemy is walking but not close to the user
				//This call finds the next step the enemy should take to get closer to the user
				Point2f answer = E.findUser(userRounded.getX(), userRounded.getZ()); //BFS
				double enemySpeed = 0.05; //How fast the enemy walks per frame
				//Update enemy position to be closer to the user
				E.setMoveX(enemySpeed * Math.round(answer.getX() - E.getShiftX()));
				E.setMoveZ(enemySpeed * Math.round(answer.getZ() - E.getShiftZ()));
				E.turnToUser(); //Rotate the enemy so it's facing in the direction it's travelling in
			}else if (nearUser(E.getShiftX(), E.getShiftZ())){
				//If the enemy is near the user, start attacking
				double angle = Math.toDegrees(Math.atan2((E.getShiftX() + TX), (E.getShiftZ() + TZ)));
				float fixAngle = 180;
				//Face the user when attacking
				E.setRotate(new Point4f(fixAngle + (float)angle, 0, 1, 0));
			}else{
				//Otherwise, the enemy is on its way to the user; update the position
				E.setShiftX(E.getShiftX() + (float)E.getMoveX());
				E.setShiftZ(E.getShiftZ() + (float)E.getMoveZ());
				E.turnToUser(); //Face the direction it's going
			}
			//Update enemy position and rotation
			temp.translate(E.getShift());
			temp.rotate(E.getRotate());
			temp.draw(); //Draw the enemy
			E.updateFrame(); //Calculate the next frame
			if (dropHealth) u.reduceHealth(enemyDamage); //If the user was attacked, drop the health
			//If the user has no health left, quit the game
			if (u.getHealth() <= 0){
				//Game Over
				quitGame = true;
				return;
			}
		}
		//Sort in decreasing order for removing
		Collections.sort(remove);
		//Remove dead enemies
		for (int i = remove.size()-1; i >= 0; i--) enemies.remove((int)remove.get(i));
	}

	/*
	This method handles the keyboard controls. Whenever the user attempts to move
	by pressing the WASD keys, the program checks if the new spot is available.
	This includes not being a wall and not walking into enemies.
	 */
	private void moveUser(){
		//Handle the keyboard inputs first
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
				if (nearEnemy(E.getShiftX(), E.getShiftZ())){
					//If user is near an enemy, cancel the movement
					TX -= move.x;
					TZ -= move.z;
					break;
				}
			}
			glTranslatef(TX, 0, TZ);
			return;
		}
		//Check if the user can travel in one direction (check analysis document)
		Z = Math.round(-TZ) + fixZ;
		if (vis[Z][X]){
			TX += move.x;
			for (Enemy E: enemies){
				if (nearEnemy(E.getShiftX(), E.getShiftZ())){
					//Again, if near an enemy, do not walk through it
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
				if (nearEnemy(E.getShiftX(), E.getShiftZ())){
					//Similar to the above part
					TZ -= move.z;
					break;
				}
			}
			glTranslatef(TX, 0, TZ);
			return;
		}
		//Otherwise, the user cannot move anywhere. This may occur if
		//the user is directly facing a corner and attempting to walk into it.
		glTranslatef(TX, 0, TZ);
	}

	/*
	This method handles mouse input from the user. If the mouse is clicked,
	then the method tracks the bullet path. The bullets stops when it hits
	a wall or when it hits an enemy.
	 */
	private void bullet(){
		if (mouse){
			//The (left) mouse is clicked
			double ratio = 0.2; //How far the bullet travels
			//Find the x and y distances according to where the bullet was facing
			double faceX = ratio * Math.sin(Math.toRadians(-move.rot));
			double faceZ = ratio * Math.cos(Math.toRadians(-move.rot));
			double curX = TX + faceX, curZ = TZ + faceZ;
			boolean cont = true;
			while (inMap(curX, curZ) && cont){
				//The loop only runs when the bullet is still inside the map,
				//and it has not hit any enemies yet.
				for (Enemy E: enemies){
					if (E.hit(curX, curZ)){
						//The enemy is hit
						int damage = 25;
						E.setHealth(E.getHealth() - damage);
						//Since the bullet has already hit an enemy, it cannot travel further
						cont = false;
					}
				}
				//If the bullet didn't hit an enemies, update the position of the bullet
				curX += faceX;
				curZ += faceZ;
			}
		}
		//This is to disable holding the mouse for continuous shooting
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
			double adjust = -0.35;
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
		double enemyReach = e.getEnemyReach();
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
				else{
					System.out.println("OUT OF BOUNDS");
					return new Point2f(-1, -1); //SHOULD NOT REACH
				}
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
				else{
					System.out.println("OUT OF BOUNDS");
					return new Point2f(-1, -1); //SHOULD NOT REACH
				}
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
				else{
					System.out.println("OUT OF BOUNDS");
					return new Point2f(-1, -1); //SHOULD NOT REACH
				}
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
				else{
					System.out.println("OUT OF BOUNDS");
					return new Point2f(-1, -1); //SHOULD NOT REACH
				}
			}
		}
	}
}
