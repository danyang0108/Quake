///Author: Danyang Wang
//Class: ICS4U
//Date: Jan 5th, 2020
//Instructor: Mr Radulovic
//Assignment name: ICS4U Culminating
/*Description: This class is the parent of User and Enemy class. Health is common
 * in both User and Enemy class, therefore both subclasses can inherit the methods.
 */
public class Entity {
	
	private int health; 
	
	public Entity() {
		health = 100;
	}
	
	//getter and setter
	public int getHealth(){
		return health;
	}
	
	public void setHealth(int health) {
		this.health = health;
	}
	
	//update health after taking damage
	public int reduceHealth(int dmg) {
		health -= dmg;
		return health;
	}
		
	
	
}
