public class User extends Entity{
	
	private Point3f curPos;
	private float health;
	private int curAmmo;
	private int totalAmmo;
	
	public User() {
		health = 100;
		curAmmo = 30;
		totalAmmo = 90;
	}
	
	//returns the current ammo count of the gun
	public int getCurAmmo() {
		return curAmmo;
	}
	
	//returns total ammo count 
	public int getTotalAmmo() {
		return totalAmmo;
	}
}
