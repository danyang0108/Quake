public class Entity {
	
	private int health; 
	
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
		
	
	
}
