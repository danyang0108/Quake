///Author: Danyang Wang
//Class: ICS4U
//Date: Jan 2nd, 2020
//Instructor: Mr Radulovic
//Assignment name: ICS4U Culminating
/*Description: This program implements a 4x4 matrix object which uses a 2d array 
 * to store the elements. Additionally, it contains the operations and transformations 
 * of matrix.
*/
public interface User_Interface {
	
	//return current number of ammo for the weapon
	public int getCurAmmo();
	
	//set the current number of ammo based on parameter
	public void setCurAmmo(int ammo);
	
	//shoots an ammo to attack
	public void attack();
	
	//return total number of ammo left
	public int getTotalAmmo();
	
	//set the total number of ammo based on parameter
	public void setTotalAmmo(int ammo);
	
	//reloads the weapon by updating current ammo and total ammo
	public void reload();
}
