import org.lwjgl.BufferUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Scanner;
import static org.lwjgl.opengl.GL11.*;

public class MeshObject{
    private ArrayList<Point3f> vertex;
    private ArrayList<Float[]> texCoords;
    private ArrayList<Vector3f> normals;
    private ArrayList<Face> faces;
    private Texture texture;
    private Scanner scanner;
    private String filename;
    private Point3f trans;
    private Point4f rotate;
    private Point3f scale;

    private void init(){
        vertex = new ArrayList<>();
        texCoords = new ArrayList<>();
        normals = new ArrayList<>();
        faces = new ArrayList<>();
    }

    public MeshObject(String filename) throws Exception{
        init();
        this.filename = filename;
        this.trans = new Point3f(0, 0, 0);
        this.rotate = new Point4f(0, 0, 0, 0);
        this.scale = new Point3f(1, 1, 1);
        read();
    }

    private void read() throws Exception{
        Scanner scan = new Scanner(new File(filename));
        while (scan.hasNextLine()){
            String[] lineArray = scan.nextLine().split(" ");
            if (lineArray[0].equals("v")){
                vertex.add(new Point3f(
                        Float.parseFloat(lineArray[1]),
                        Float.parseFloat(lineArray[2]),
                        Float.parseFloat(lineArray[3])));
            }else if (lineArray[0].equals("vt")){
                Float[] f = {Float.parseFloat(lineArray[1]), Float.parseFloat(lineArray[2])};
                texCoords.add(f);
            }else if (lineArray[0].equals("vn")){
                normals.add(new Vector3f(new Point3f(
                        Float.parseFloat(lineArray[1]),
                        Float.parseFloat(lineArray[2]),
                        Float.parseFloat(lineArray[3]))));
            }else if (lineArray[0].equals("f")){
                Face f = new Face(lineArray.length-1);
                for (int i = 1; i < lineArray.length; i++){
                    String[] attribString = lineArray[i].split("/");
                    int[] attrib = new int[3];
                    attrib[0] = Integer.parseInt(attribString[0]);
                    attrib[1] = Integer.parseInt(attribString[1]);
                    attrib[2] = Integer.parseInt(attribString[2]);
                    f.setVertex(i - 1, attrib);
                }
                faces.add(f);
            }else if (lineArray[0].equals("mtllib")){
                scanner = new Scanner(new File("Resource/Models/"+lineArray[1]));
            }else if (lineArray[0].equals("usemtl")){
                String material = lineArray[1];
                while (scanner.hasNextLine()){
                    String[] tempLine = scanner.nextLine().split(",");
                    if (tempLine[0].equals(material)) texture = new Texture(tempLine[1]);
                }
            }
        }
        scan.close();
    }

    public void translate(Point3f trans){
        this.trans.x = trans.x;
        this.trans.y = trans.y;
        this.trans.z = trans.z;
    }

    public void rotate(Point4f rotate){
        this.rotate.rot = rotate.rot;
        this.rotate.x = rotate.x;
        this.rotate.y = rotate.y;
        this.rotate.z = rotate.z;
    }

    public void scale(Point3f scale){
        this.scale.x = scale.x;
        this.scale.y = scale.y;
        this.scale.z = scale.z;
    }

    public void draw(){
        glPushMatrix();
        glTranslatef(trans.x, trans.y, trans.z);
        glRotatef(rotate.rot, rotate.x, rotate.y, rotate.z);
        glScalef(scale.x, scale.y, scale.z);
        if(texture != null){
            glEnable(GL_TEXTURE_2D); //Enable texture mapping
            texture.bind();
        }else glDisable(GL_TEXTURE_2D);	//Disable texture mapping
        for (Face face: faces){
            glBegin(GL_POLYGON);
            for (int j = 0; j < face.getSize(); j++){
                int ti = face.getTextureIndex(j) - 1;
                int vi = face.getVertexIndex(j) - 1;
                int ni = face.getNormalIndex(j) - 1;
                Float[] tex = this.texCoords.get(ti);
                glTexCoord2f(tex[0], tex[1]);
                Point3f vertex = this.vertex.get(vi);
                glVertex3f(vertex.x, vertex.y, vertex.z);
                Vector3f normal = this.normals.get(ni);
                glNormal3f(normal.getX(), normal.getY(), normal.getZ());
            }
            glEnd();
        }
        glPopMatrix();
    }

    public void showImage(){
        BufferedImage test = new BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = test.createGraphics();
        g2d.setColor(new Color(1f, 1f, 1f, 0.5f));
        g2d.fillRect(0, 0, 128, 128);
        g2d.setColor(Color.RED);
        g2d.drawRect(0, 0, 127, 127);
        g2d.fillRect(10, 10, 10, 10);
        g2d.setColor(Color.BLUE);
        g2d.drawString("PLEASE WORK", 10, 64);
        int textureID = loadTexture(test);

    }

    public int loadTexture(BufferedImage image){
        int width = image.getWidth();
        int height = image.getHeight();
        int[] pixels_raw = image.getRGB(0, 0, width, height, null, 0, width);
        ByteBuffer pixels = BufferUtils.createByteBuffer(width*height*4);
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
        int id = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, id);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, pixels);
        return id;
    }
}