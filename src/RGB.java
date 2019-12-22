public class RGB{
    public float r, g, b, a;
    public static final RGB Black = new RGB(0f, 0f, 0f, 1f);
    public static final RGB White = new RGB(1f, 1f, 1f, 1f);
    public static final RGB DarkGray = new RGB(0.2f, 0.2f, 0.2f, 1.0f);
    public static final RGB LightGray = new RGB(0.8f, 0.8f, 0.8f, 1.0f);
    public RGB(){
        this.r = this.g = this.b = this.a = 1.0f;
    }
    public RGB(float r, float g, float b, float a){
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }
    public void set(float r, float g, float b, float a){
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }
    public void set(RGB lol){
        this.r = lol.r;
        this.g = lol.g;
        this.b = lol.b;
        this.a = lol.a;
    }
}
