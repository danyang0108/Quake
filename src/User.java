public class User extends Entity{
	
	private int health;
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
	
	public void setTotalAmmo(int ammo) {
		totalAmmo = ammo;
	}
	
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
