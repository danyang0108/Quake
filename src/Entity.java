public class Entity {
	
	private float health; 
	private Point3f curPos;
	
	public Entity() {
		health = 100;
	}
	public float getHealth(){
		return health;
	}
	
	public float reduceHealth(float h) {
		health -= h;
		return health;
	}
		
	public Point3f getCurPos() {
		return curPos;
	}
	
	
}
