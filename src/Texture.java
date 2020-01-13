import static org.lwjgl.opengl.GL11.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import javax.imageio.ImageIO;
import org.lwjgl.BufferUtils;

public class Texture{
    private int id;

    public Texture(String filename) throws Exception{
        BufferedImage bi = ImageIO.read(new File(filename));
        int width = bi.getWidth();
        int height = bi.getHeight();
        int[] pixels_raw = bi.getRGB(0, 0, width, height, null, 0, width);
        ByteBuffer pixels = BufferUtils.createByteBuffer(width * height * 4);
        for (int i = 0; i < width; i++){
            for (int j = 0; j < height; j++){
                if (i * height + j < pixels_raw.length){
                    int pixel = pixels_raw[i * height + j];
                    pixels.put((byte)((pixel >> 16) & 0xFF));		// RED
                    pixels.put((byte)((pixel >> 8) & 0xFF));		// GREEN
                    pixels.put((byte)(pixel & 0xFF));				// BLUE
                    pixels.put((byte)((pixel >> 24) & 0xFF));		// ALPHA
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

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, id);
    }

    /*
    YOU REACH I TEACH
    public Colour getPixel(int x, int y) {
        ByteBuffer buffer = BufferUtils.createByteBuffer(bi.getWidth() *
          bi.getHeight() * 4); //4 for RGBA, 3 for RGB

        if (y <= bi.getHeight() && x <= bi.getWidth()){
          int pixel = pixels_raw[y * bi.getWidth() + x];
          int r=(pixel >> 16) & 0xFF;     // Red 
          int g=(pixel >> 8) & 0xFF;      // Green 
          int b=(pixel & 0xFF);            // Blue
          return new Colour(r,g,b);
        }
        else{
          return new Colour(0,0,0);
        }
    }
     */
}