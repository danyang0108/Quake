///Author: Ethan Zhang
//Class: ICS4U
//Date: Jan 12th, 2020
//Instructor: Mr Radulovic
//Assignment name: ICS4U Culminating
/*Description: This class applies textures, transformations, and draws
 * 3D models.
*/
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import static org.lwjgl.opengl.GL11.*;

public class MeshObject{
    private ArrayList<Point3f> vertex;
    private ArrayList<Float[]> texCoords;
    private ArrayList<Vector3f> normals;
    private ArrayList<Face> faces;
    private String filename;
    private Point3f trans;
    private Point4f rotate;
    private Point3f scale;
    private Texture texture;

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
            if (lineArray.length < 2) continue;
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
            }
        }
        scan.close();
    }

    public void translate(Point3f trans){
        this.trans.setX(trans.getX());
        this.trans.setY(trans.getY());
        this.trans.setZ(trans.getZ());
    }

    public void rotate(Point4f rotate){
        this.rotate.rot = rotate.rot;
        this.rotate.setX(rotate.getX());
        this.rotate.setY(rotate.getY());
        this.rotate.setZ(rotate.getZ());
    }

    public void scale(Point3f scale){
        this.scale.setX(scale.getX());
        this.scale.setY(scale.getY());
        this.scale.setZ(scale.getZ());
    }

    public void draw(){
        glPushMatrix();
        glTranslatef(trans.getX(), trans.getY(), trans.getZ());
        glRotatef(rotate.rot, rotate.getX(), rotate.getY(), rotate.getZ());
        glScalef(scale.getX(), scale.getY(), scale.getZ());
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
                glVertex3f(vertex.getX(), vertex.getY(), vertex.getZ());
                Vector3f normal = this.normals.get(ni);
                glNormal3f(normal.getX(), normal.getY(), normal.getZ());
            }
            glEnd();
        }
        glPopMatrix();
    }

    public void setTexture(Texture tex){
        texture = tex;
    }
}