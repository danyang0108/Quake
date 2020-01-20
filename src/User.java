public class User {
	
	private Point3f curPos;
	private float health = 100;
	private int curAmmo = 30;
	private int totalAmmo = 90;
	
	
	public float reduceHealth(float h) {
		health -= h;
		
		return 0;
	}

	
	public Point3f getCurPos() {
		return curPos;
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
