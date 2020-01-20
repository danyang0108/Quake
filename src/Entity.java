public class Entity {
	
	private int health; 
	private Point3f curPos;
	
	public Entity() {
		health = 100;
	}
	
	public int getHealth(){
		return health;
	}
	
	public void setHealth(int health) {
		this.health = health;
	}
	
	public int reduceHealth(int dmg) {
		health -= dmg;
		return health;
	}
		
	public Point3f getCurPos() {
		return curPos;
	}
	
	
}
