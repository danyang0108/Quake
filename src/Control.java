///Author: Ethan Zhang
//Class: ICS4U
//Date: Jan 10th, 2020
//Instructor: Mr Radulovic
//Assignment name: ICS4U Culminating
/*Description: This class includes the keyboard and mouse controls for 
 * the 3D first-person shooter game. 
 */
import org.lwjgl.BufferUtils;
import java.nio.DoubleBuffer;
import static org.lwjgl.glfw.GLFW.glfwGetCursorPos;
import static org.lwjgl.opengl.GL11.glRotatef;

public class Control{
    private final double fast = 1.5;
    private final double WINDOW_WIDTH = 1366;
    private final double cycle = 180;

    public double getCursorX(long window){
        //Returns the value of the x-value of the cursor.
        DoubleBuffer posX = BufferUtils.createDoubleBuffer(1);
        glfwGetCursorPos(window, posX, null);
        return posX.get(0);
        //Zero stores x-value, one stores y-value.
    }

    public Point4f movement(long window, boolean[] movement){
        //Finds the x-value of the current position of the cursor on the screen.
        float tx = 0, tz = 0;
        float SEN = 0.05f, SED = SEN / (float)Math.sqrt(2);
        double dx = getCursorX(window) - WINDOW_WIDTH / 2;
        boolean FB = movement[0] || movement[1];
        boolean LR = movement[2] || movement[3];
        //In order to find exactly how much the screen should rotate according to mouse movement,
        //I first establish the fact that if the mouse is limited up to the sides of the screen,
        //the user should only rotate 180 degrees. As a result, I find out how much the cursor
        //moved, multiply by 180 degrees, and finally divide by the window width to find the
        //best mouse movement. Finally, I add another 360 degrees to ensure that the degree is positive.
        double degreeX = (cycle / WINDOW_WIDTH * dx + cycle * 2) % (cycle * 2); //Up to 180 degrees for x
        //Rotate the screen according the mouse movement
        glRotatef((float)degreeX, 0f, 1f, 0f);

        //Handle keyboard input
        double X1 = (LR ? SED : SEN) * Math.sin(Math.toRadians(degreeX));
        double Z1 = (LR ? SED : SEN) * Math.cos(Math.toRadians(degreeX));
        double X2 = (FB ? SED : SEN) * Math.cos(Math.toRadians(degreeX));
        double Z2 = (FB ? SED : SEN) * Math.sin(Math.toRadians(degreeX));
        if (movement[4]){
            //Control key is pressed; the speed increases
            X1 *= fast;
            Z1 *= fast;
            X2 *= fast;
            Z2 *= fast;
        }
        if (movement[0]){
            //"W" key is pressed
            tx -= X1;
            tz += Z1;
        }
        if (movement[1]){
            //"S" key is pressed
            tx += X1;
            tz -= Z1;
        }
        if (movement[2]){
            //"A" key is pressed
            tx += X2;
            tz += Z2;
        }
        if (movement[3]){
            //"D" key is pressed
            tx -= X2;
            tz -= Z2;
        }
        //Returns the rotation, along with the translations
        return new Point4f((float)degreeX, tx, 0, tz);
    }
}
