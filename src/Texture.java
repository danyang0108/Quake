///Author: Danyang Wang
//Class: ICS4U
//Date: Jan 2nd, 2020
//Instructor: Mr Radulovic
//Assignment name: ICS4U Culminating
/*Description: This class loads the texture based on file path. In addition, 
 * it can find and change the colour of specific pixels of the texture. 
*/
import static org.lwjgl.opengl.GL11.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import javax.imageio.ImageIO;
import org.lwjgl.BufferUtils;

public class Texture{
    
	private int id;
    private BufferedImage bi;	//stores texture
    private int[] pixels_raw;	//array of pixels
    
    //load texture based on file name
    public Texture(String filename) throws Exception{
        bi = ImageIO.read(new File(filename));
        int width = bi.getWidth();
        int height = bi.getHeight();
        pixels_raw = bi.getRGB(0, 0, width, height, null, 0, width);
        ByteBuffer pixels = BufferUtils.createByteBuffer(width*height*4);
        
        //loop through the image pixels, and load the colour
        for (int i = 0; i < width; i++){
            for (int j = 0; j < height; j++){
                if (i * height + j < pixels_raw.length){
                    Color temp = new Color(pixels_raw[i * height + j]);
                    pixels.put((byte)temp.getRed());
                    pixels.put((byte)temp.getGreen());
                    pixels.put((byte)temp.getBlue());
                    pixels.put((byte)temp.getAlpha());
                }
            }
        }
        pixels.flip();
        id = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, id);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, pixels);
    }

    //binds texture
    public void bind() {
        glBindTexture(GL_TEXTURE_2D, id);
    }

    //returns the colour of the pixel at (x,y)
    public Colour getPixel(int x, int y) throws Exception {
    	
    	//calculate the index of the pixel, and convert the colour
    	//to a rgb value
        if (y <= bi.getHeight() && x <= bi.getWidth()){
          int pixel = pixels_raw[y * bi.getWidth() + x];
          int r=(pixel >> 16) & 0xFF;     // Red 
          int g=(pixel >> 8) & 0xFF;      // Green 
          int b=(pixel & 0xFF);           // Blue
          return new Colour(r,g,b);
        }
        else{
          return new Colour(0,0,0);
        }
    }
    
    //changes the colour of the pixel based on user input
    public void setPixel(int col) throws Exception {
    	int width = bi.getWidth();
    	int height = bi.getHeight();
    	Colour pink = new Colour(255,0,255);
    	//update the pixel colour inside the pixels_raw[] array
    	for (int i = 0; i < width; i++) {
    		for (int j = 0; j < height; j++) {
    			Colour c = getPixel(i,j);
    			if (c.getR() == pink.getR() && c.getG() == pink.getG() && c.getB() == pink.getB()) {
    				pixels_raw[j * bi.getWidth() + i] = col;
    			}
    		}
    	}
    	ByteBuffer pixels = BufferUtils.createByteBuffer(width*height*4);
    	//loop through the image pixels, and load the colour
        for (int i = 0; i < width; i++){
            for (int j = 0; j < height; j++){
                if (i * height + j < pixels_raw.length){
                    Color temp = new Color(pixels_raw[i * height + j]);
                    pixels.put((byte)temp.getRed());
                    pixels.put((byte)temp.getGreen());
                    pixels.put((byte)temp.getBlue());
                    pixels.put((byte)temp.getAlpha());
                }
            }
        }
        pixels.flip();
        id = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, id);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, pixels);
    }
    
    //getter 
    public BufferedImage getBI() {
    	return bi;
    }
}