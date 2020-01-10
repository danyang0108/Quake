import org.lwjgl.BufferUtils;
import java.nio.DoubleBuffer;
import static org.lwjgl.glfw.GLFW.glfwGetCursorPos;
import static org.lwjgl.opengl.GL11.glRotatef;

public class Control{
    public double getCursorX(long window){
        DoubleBuffer posX = BufferUtils.createDoubleBuffer(1);
        glfwGetCursorPos(window, posX, null);
        return posX.get(0);
    }

    //Finds the y-value of the current position of the cursor on the screen.
    public double getCursorY(long window){
        DoubleBuffer posY = BufferUtils.createDoubleBuffer(1);
        glfwGetCursorPos(window, null, posY);
        return posY.get(0);
    }

    public Point3f movement(long window, boolean[] movement){
        //Finds the x-value of the current position of the cursor on the screen.
        double WINDOW_WIDTH = 1366, WINDOW_HEIGHT = 768;
        double cycle = 360, Q1 = 90, Q2 = 180, Q3 = 270;
        float tx = 0, ty = 0, tz = 0;
        float SEN = 0.05f, SED = SEN/(float)Math.sqrt(2);
        double dx = getCursorX(window) - WINDOW_WIDTH / 2;
        double dy = getCursorY(window) - WINDOW_HEIGHT / 2;
        boolean FB = movement[0] || movement[1];
        boolean LR = movement[2] || movement[3];
        double degreeX = (cycle / 2 / WINDOW_WIDTH * dx + cycle) % cycle; //Up to 180 degrees for x
        double degreeY = (cycle / 2 / WINDOW_HEIGHT * dy + cycle) % cycle; //180 degrees for y as well
        glRotatef((float)degreeX, 0f, 1f, 0f);
        //glRotatef((float)degreeY, 1.0f, 0.0f, 0.0f);
        if (degreeX >= Q3 && degreeX <= cycle){
            //Front-left; 270-360 degree quadrant
            double RAA = cycle - degreeX;
            if (movement[0]){
                tx += (LR ? SED : SEN) * Math.sin(Math.toRadians(RAA));
                tz += (LR ? SED : SEN) * Math.cos(Math.toRadians(RAA));
            }
            if (movement[1]){
                tx -= (LR ? SED : SEN) * Math.sin(Math.toRadians(RAA));
                tz -= (LR ? SED : SEN) * Math.cos(Math.toRadians(RAA));
            }
            if (movement[2]){
                tx += (FB ? SED : SEN) * Math.cos(Math.toRadians(RAA));
                tz -= (FB ? SED : SEN) * Math.sin(Math.toRadians(RAA));
            }
            if (movement[3]){
                tx -= (FB ? SED : SEN) * Math.cos(Math.toRadians(RAA));
                tz += (FB ? SED : SEN) * Math.sin(Math.toRadians(RAA));
            }
        }else if (degreeX >= 0 && degreeX <= Q1){
            //Front-right; 0-90 degree quadrant
            if (movement[0]){
                tx -= (LR ? SED : SEN) * Math.sin(Math.toRadians(degreeX));
                tz += (LR ? SED : SEN) * Math.cos(Math.toRadians(degreeX));
            }
            if (movement[1]){
                tx += (LR ? SED : SEN) * Math.sin(Math.toRadians(degreeX));
                tz -= (LR ? SED : SEN) * Math.cos(Math.toRadians(degreeX));
            }
            if (movement[2]){
                tx += (FB ? SED : SEN) * Math.cos(Math.toRadians(degreeX));
                tz += (FB ? SED : SEN) * Math.sin(Math.toRadians(degreeX));
            }
            if (movement[3]){
                tx -= (FB ? SED : SEN) * Math.cos(Math.toRadians(degreeX));
                tz -= (FB ? SED : SEN) * Math.sin(Math.toRadians(degreeX));
            }
        }else if (degreeX >= Q1 && degreeX <= Q2){
            //Back-right; 90-180 degree quadrant
            double RAA = cycle / 2 - degreeX;
            if (movement[0]){
                tx -= (LR ? SED : SEN) * Math.sin(Math.toRadians(RAA));
                tz -= (LR ? SED : SEN) * Math.cos(Math.toRadians(RAA));
            }
            if (movement[1]){
                tx += (LR ? SED : SEN) * Math.sin(Math.toRadians(RAA));
                tz += (LR ? SED : SEN) * Math.cos(Math.toRadians(RAA));
            }
            if (movement[2]){
                tx -= (FB ? SED : SEN) * Math.cos(Math.toRadians(RAA));
                tz += (FB ? SED : SEN) * Math.sin(Math.toRadians(RAA));
            }
            if (movement[3]){
                tx += (FB ? SED : SEN) * Math.cos(Math.toRadians(RAA));
                tz -= (FB ? SED : SEN) * Math.sin(Math.toRadians(RAA));
            }
        }else{
            //Back-left; 180-270 degree quadrant
            double RAA = degreeX - cycle / 2;
            if (movement[0]){
                tx += (LR ? SED : SEN) * Math.sin(Math.toRadians(RAA));
                tz -= (LR ? SED : SEN) * Math.cos(Math.toRadians(RAA));
            }
            if (movement[1]){
                tx -= (LR ? SED : SEN) * Math.sin(Math.toRadians(RAA));
                tz += (LR ? SED : SEN) * Math.cos(Math.toRadians(RAA));
            }
            if (movement[2]){
                tx -= (FB ? SED : SEN) * Math.cos(Math.toRadians(RAA));
                tz -= (FB ? SED : SEN) * Math.sin(Math.toRadians(RAA));
            }
            if (movement[3]){
                tx += (FB ? SED : SEN) * Math.cos(Math.toRadians(RAA));
                tz += (FB ? SED : SEN) * Math.sin(Math.toRadians(RAA));
            }
        }
        return new Point3f(tx, ty, tz);
    }
}
