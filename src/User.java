public class User implements Entity {
	
	private Point3f curPos;
	private float health;
	private int weapon;	//index of the weapon in inventory bar
	private int ammo;
	
	//private ArrayList<Weapon> weapon = new ArrayList<Weapon>();
	//private ArrayList<Integer> ammoCount = new ArrayList<Integer>();
	//private ArrayList<Integer> damage = new ArrayList<Integer>();	//weapon damage
	
	@Override
	public void Move() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void Attack() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showHealth() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public float reduceHealth(float h) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Point3f getCurPos() {
		return curPos;
	}
	
	//detects collision between player and enemy
	public void collision() {
		
	}
	
	//returns the current ammo count of the gun
	public int getAmmoCount() {
		
		return 0;
	}
	
	public void setAmmoCount(int ammo) {
		
	}
	
	public void drawInventory() {
		
	}
	
	public void aimingDetection() {
		
	}
}
