///Author: Danyang Wang
//Class: ICS4U
//Date: Jan 2nd, 2020
//Instructor: Mr Radulovic
//Assignment name: ICS4U Culminating
/*Description: This class is a subclass of Entity class. It inherits all the methods from
 * Entity class. It also implements all the methods from the User_Interface class.
*/
public class User extends Entity implements User_Interface{
	
	private int health;
	private int curAmmo;	//a weapon can hold 30 ammos before reloading
	private int totalAmmo;	
	
	//constructor for player
	public User() {
		health = 100;
		curAmmo = 30;
		totalAmmo = 90;
	}
	
	//returns the current ammo count of the gun
	public int getCurAmmo() {
		return curAmmo;
	}
	
	//set the total number of ammo based on parameter
	public void setCurAmmo(int ammo) {
		curAmmo = ammo;
	}
	
	//example of dynamic polymorphism, since there's a method with same name
	//and same parameters in Enemy.java
	public void attack() {
		curAmmo--;
	}
	//returns total ammo count 
	public int getTotalAmmo() {
		return totalAmmo;
	}
	//set the total number of ammo based on parameter
	public void setTotalAmmo(int ammo) {
		totalAmmo = ammo;
	}
	
	//update the current ammo and total ammo after
	//the user right-clicks
	public void reload() {
		int maxRound = 30;
		int required = maxRound - curAmmo;
		if (totalAmmo >= required){
			totalAmmo -= required;
			curAmmo += required;
		}else{
			curAmmo += totalAmmo;
			totalAmmo = 0;
		}
	}
	
}
