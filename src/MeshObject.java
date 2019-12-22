import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import static org.lwjgl.opengl.GL11.*;

public class MeshObject{
    private ArrayList<Point3f> vertex;
    private ArrayList<Float[]> texCoords;
    private ArrayList<Vector3f> normals;
    private ArrayList<Face> faces;    	// indices into the vertices array list
    private Texture texture;

    public MeshObject(){
        vertex = new ArrayList();
        texCoords = new ArrayList();
        normals = new ArrayList();
        faces = new ArrayList();
    }

    public void draw(){
        if(texture != null){
            glEnable(GL_TEXTURE_2D);	// enable texture mapping
            texture.bind();
        }
        else glDisable(GL_TEXTURE_2D);	// disable texture mapping
        for (int i = 0; i < faces.size(); i++){
            glBegin(GL_POLYGON);
            for (int j = 0; j < faces.get(i).getSize(); j++){
                Point3f p = vertex.get(faces.get(i).getVertexIndex(j)-1);
                glVertex3f(p.getX(), p.getY(), p.getZ());
                Vector3f v = normals.get(faces.get(i).getNormalIndex(j)-1);
                glNormal3f(v.getX(), v.getY(), v.getZ());
            }
            glEnd();

        }
    }

    public MeshObject(String filename) throws Exception{
        this();
        Scanner scan = new Scanner(new File(filename));
        while (scan.hasNextLine()){
            String[] lineArray = scan.nextLine().split(" ");
            if (lineArray[0].equals("v")){
                Point3f point = new Point3f(
                        Float.parseFloat(lineArray[1]),
                        Float.parseFloat(lineArray[2]),
                        Float.parseFloat(lineArray[3]));
                vertex.add(point);
            }else if (lineArray[0].equals("vt")){
                Float[] f = new Float[2];
                f[0] = Float.parseFloat(lineArray[1]);
                f[1] = Float.parseFloat(lineArray[2]);
                texCoords.add(f);
            }else if (lineArray[0].equals("vn")){
                Vector3f vector = new Vector3f(new Point3f(
                        Float.parseFloat(lineArray[1]),
                        Float.parseFloat(lineArray[2]),
                        Float.parseFloat(lineArray[3])));
                normals.add(vector);
            }else if (lineArray[0].equals("f")){
                Face f = new Face(lineArray.length-1);
                for (int i = 1; i < lineArray.length; i++){
                    String[] attribString = lineArray[i].split("/");
                    int[] attrib = new int[2];
                    attrib[0] = Integer.parseInt(attribString[0]);
                    attrib[1] = Integer.parseInt(attribString[2]);
                    f.setVertex(i-1, attrib);
                }
                faces.add(f);
            }else if (lineArray[0].equals("mtllib")){
                Scanner mtlibScanner = new Scanner(new File("Resource/Models/"+lineArray[1]));
                while (mtlibScanner.hasNextLine()){
                    String[] mtlib_lineArray = mtlibScanner.nextLine().split(" ");
                    if (mtlib_lineArray[0].equals("map_Kd")){
                        texture = new Texture(mtlib_lineArray[1]);
                    }
                }
            }
        }
        scan.close();
    }
}