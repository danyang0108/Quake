public interface Enemy_Interface {
	
	//finds the shortest path toward the user using BFS
	public Point2f findUser(int x, int z);
	
	//checks if the enemy was hit
	public boolean hit(double x, double z);
	
	//rotate the enemy such that it faces the user
	public void turnToUser();
	
	//update the animation frame for walking/punching/dying
	public boolean updateFrame();
	
	//starts the attack animation
	public void attack();
}
