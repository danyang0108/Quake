
public interface Entity {
	
	public void Move();
	
	public void Attack();
	
	public void showHealth();
	
	public float reduceHealth(float h);
	
	public Point3f getCurPos();
	
	
}
