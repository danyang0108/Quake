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
