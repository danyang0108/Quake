import org.lwjgl.BufferUtils;
import java.nio.DoubleBuffer;
import static org.lwjgl.glfw.GLFW.glfwGetCursorPos;
import static org.lwjgl.opengl.GL11.glRotatef;

public class Control{
    private double fast = 1.5;
    private double slow = 0.25;

    public double getCursorX(long window){
        DoubleBuffer posX = BufferUtils.createDoubleBuffer(1);
        glfwGetCursorPos(window, posX, null);
        return posX.get(0);
    }

    public Point4f movement(long window, boolean[] movement){
        //Finds the x-value of the current position of the cursor on the screen.
        double WINDOW_WIDTH = 1366;
        double cycle = 360;
        float tx = 0, tz = 0;
        float SEN = 0.05f, SED = SEN/(float)Math.sqrt(2);
        double dx = getCursorX(window) - WINDOW_WIDTH / 2;
        boolean FB = movement[0] || movement[1];
        boolean LR = movement[2] || movement[3];
        double degreeX = (cycle / 2 / WINDOW_WIDTH * dx + cycle) % cycle; //Up to 180 degrees for x
        glRotatef((float)degreeX, 0f, 1f, 0f);

        double X1 = (LR ? SED : SEN) * Math.sin(Math.toRadians(degreeX));
        double Z1 = (LR ? SED : SEN) * Math.cos(Math.toRadians(degreeX));
        double X2 = (FB ? SED : SEN) * Math.cos(Math.toRadians(degreeX));
        double Z2 = (FB ? SED : SEN) * Math.sin(Math.toRadians(degreeX));
        if (movement[4] && movement[5]){
            //To disable sprint and crouch at the same time
            X1 *= slow;
            Z1 *= slow;
            X2 *= slow;
            Z2 *= slow;
        }else if (movement[4]){
            //Speed up
            X1 *= fast;
            Z1 *= fast;
            X2 *= fast;
            Z2 *= fast;
        }else if (movement[5]){
            //Crouch
            X1 *= slow;
            Z1 *= slow;
            X2 *= slow;
            Z2 *= slow;
        }
        if (movement[0]){
            tx -= X1;
            tz += Z1;
        }
        if (movement[1]){
            tx += X1;
            tz -= Z1;
        }
        if (movement[2]){
            tx += X2;
            tz += Z2;
        }
        if (movement[3]){
            tx -= X2;
            tz -= Z2;
        }
        return new Point4f((float)degreeX, tx, 0, tz);
    }
}
