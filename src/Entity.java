public class Entity {
	
	private int health = 100; 
	private Point3f curPos;
	
	public void showHealth() {
		
	}
	
	public float reduceHealth(float h) {
		health -= h;
		return health;
	}
		
	public Point3f getCurPos() {
		return curPos;
	}
	
	
}
