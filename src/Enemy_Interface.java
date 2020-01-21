///Author: Danyang Wang
//Class: ICS4U
//Date: Jan 5th, 2020
//Instructor: Mr Radulovic
//Assignment name: ICS4U Culminating
/*Description: This interface includes the interactions between the enemy and user,
 * as well as the animations.
 */
public interface Enemy_Interface {
	
	//finds the shortest path toward the user using BFS
	public Point2f findUser(int x, int z) throws Exception;
	
	//checks if the enemy was hit
	public boolean hit(double x, double z);
	
	//rotate the enemy such that it faces the user
	public void turnToUser();
	
	//update the animation frame for walking/punching/dying
	public boolean updateFrame();
	
	//starts the attack animation
	public void attack();
}
