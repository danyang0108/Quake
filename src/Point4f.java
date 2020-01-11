public class Point4f{
    public float rot;
    public float x, y, z;
    public Point4f(float rot, float x, float y, float z){
        this.rot = rot;
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public Point4f(double rot, double x, double y, double z){
        this.rot = (float)rot;
        this.x = (float)x;
        this.y = (float)y;
        this.z = (float)z;
    }
}
