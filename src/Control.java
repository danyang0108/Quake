import org.lwjgl.BufferUtils;

import java.nio.DoubleBuffer;

import static org.lwjgl.glfw.GLFW.glfwGetCursorPos;
import static org.lwjgl.opengl.GL11.glRotatef;

public class Control{
    //Finds the x-value of the current position of the cursor on the screen.
    private double WINDOW_WIDTH = 1366, WINDOW_HEIGHT = 768;
    private float SEN = 0.03f, SED = 0.03f/(float)Math.sqrt(2);
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
        double dx = getCursorX(window) - WINDOW_WIDTH/2;
        double dy = getCursorY(window) - WINDOW_HEIGHT/2;
        double degreeX = (180.0 / WINDOW_WIDTH * dx + 360.0) % 360.0; //Up to 180 degrees for x
        System.out.println("SPIN: " + degreeX);
        double degreeY = (180.0 / WINDOW_HEIGHT * dy + 360.0) % 360.0; //Only 90 degrees for y
        glRotatef((float)degreeX, 0.0f, 1.0f, 0.0f);
        //glRotatef((float)degreeY, 1.0f, 0.0f, 0.0f); Oh please stop my headache
        float tx = 0, ty = 0, tz = 0;
        boolean FB = movement[0] || movement[1];
        boolean LR = movement[2] || movement[3];
        if (degreeX >= 270 && degreeX <= 360){
            //Front-left
            double RAA = 360.0 - degreeX;
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
        }else if (degreeX >= 0 && degreeX <= 90){
            //Front-right
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
        }else if (degreeX >= 90 && degreeX <= 180){
            //Back-right
            double RAA = 180.0 - degreeX;
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
            //Back-left
            double RAA = degreeX - 180.0;
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
