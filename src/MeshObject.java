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
    private ArrayList<Point3f> vertex; //Store the vertices
    private ArrayList<Float[]> texCoords; //Store texture mapping
    private ArrayList<Vector3f> normals; //Store the normals
    private ArrayList<Face> faces; //Store the faces
    private String filename;
    private Point3f trans;
    private Point4f rotate;
    private Point3f scale;
    private Texture texture;

    private void init(){
        //Initialize the ArrayLists
        vertex = new ArrayList<>();
        texCoords = new ArrayList<>();
        normals = new ArrayList<>();
        faces = new ArrayList<>();
    }

    public MeshObject(String filename) throws Exception{
        //When this method is called, a MeshObject is created using the file name path.
        init();
        this.filename = filename;
        this.trans = new Point3f(0, 0, 0);
        this.rotate = new Point4f(0, 0, 0, 0);
        this.scale = new Point3f(1, 1, 1);
        read(); //Read every line of the OBJ file given.
    }

    private void read() throws Exception{
        //This method reads the given OBJ, and stores it as
        //faces, texture mapping, vertices and normals.
        Scanner scan = new Scanner(new File(filename));
        while (scan.hasNextLine()){
            String[] lineArray = scan.nextLine().split(" ");
            if (lineArray.length < 2) continue;
            //If the length is less than 2, then it's most likely an empty line.
            if (lineArray[0].equals("v")){
                //New vertex
                vertex.add(new Point3f(
                        Float.parseFloat(lineArray[1]),
                        Float.parseFloat(lineArray[2]),
                        Float.parseFloat(lineArray[3])));
            }else if (lineArray[0].equals("vt")){
                //Vertex Texture
                Float[] f = {Float.parseFloat(lineArray[1]), Float.parseFloat(lineArray[2])};
                texCoords.add(f);
            }else if (lineArray[0].equals("vn")){
                //Vertex Normal
                normals.add(new Vector3f(new Point3f(
                        Float.parseFloat(lineArray[1]),
                        Float.parseFloat(lineArray[2]),
                        Float.parseFloat(lineArray[3]))));
            }else if (lineArray[0].equals("f")){
                //Face
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
        //Translates the object in world coordinates.
        this.trans.setX(trans.getX());
        this.trans.setY(trans.getY());
        this.trans.setZ(trans.getZ());
    }

    public void rotate(Point4f rotate){
        //Rotates the object in world coordinates.
        this.rotate.rot = rotate.rot;
        this.rotate.setX(rotate.getX());
        this.rotate.setY(rotate.getY());
        this.rotate.setZ(rotate.getZ());
    }

    public void scale(Point3f scale){
        //Scales the object.
        this.scale.setX(scale.getX());
        this.scale.setY(scale.getY());
        this.scale.setZ(scale.getZ());
    }

    public void draw(){
        //Draw the object on the screen.
        glPushMatrix();
        //Translate, rotate and scale the object.
        glTranslatef(trans.getX(), trans.getY(), trans.getZ());
        glRotatef(rotate.rot, rotate.getX(), rotate.getY(), rotate.getZ());
        glScalef(scale.getX(), scale.getY(), scale.getZ());
        if(texture != null){
            //If there is texture, enable texture mapping.
            glEnable(GL_TEXTURE_2D); //Enable texture mapping
            texture.bind();
        }else glDisable(GL_TEXTURE_2D);	//Disable texture mapping
        for (Face face: faces){
            //Treat each face as a polygon.
            glBegin(GL_POLYGON);
            for (int j = 0; j < face.getSize(); j++){
                int ti = face.getTextureIndex(j) - 1;
                int vi = face.getVertexIndex(j) - 1;
                int ni = face.getNormalIndex(j) - 1;
                Float[] tex = this.texCoords.get(ti);
                glTexCoord2f(tex[0], tex[1]); //Texture coordinates
                Point3f vertex = this.vertex.get(vi);
                glVertex3f(vertex.getX(), vertex.getY(), vertex.getZ()); //Vertex
                Vector3f normal = this.normals.get(ni);
                glNormal3f(normal.getX(), normal.getY(), normal.getZ()); //Normal
            }
            glEnd();
        }
        glPopMatrix();
    }

    public void setTexture(Texture tex){
        //Sets the texture of the object.
        texture = tex;
    }
}