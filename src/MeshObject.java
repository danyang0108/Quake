import java.io.File;
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

    public MeshObject(String filename) throws Exception{
        vertex = new ArrayList<>();
        texCoords = new ArrayList<>();
        normals = new ArrayList<>();
        faces = new ArrayList<>();
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
                    f.setVertex(i-1, attrib);
                }
                faces.add(f);
            }/*else if (lineArray[0].equals("mtllib")){
                scanner = new Scanner(new File("Resource/Models/"+lineArray[1]));
            }else if (lineArray[0].equals("usemtl")){
                String material = lineArray[1];
                while (scanner.hasNextLine()){
                    String[] tempLine = scanner.nextLine().split(",");
                    if (tempLine[0].equals("newmtl") && tempLine[1].equals(material)){
                        texture = new Texture(tempLine[2]);
                    }
                }
            }
            */
        }
        scan.close();
    }

    public void draw(){
        if(texture != null){
            glEnable(GL_TEXTURE_2D);	// enable texture mapping
            texture.bind();
        }else glDisable(GL_TEXTURE_2D);	// disable texture mapping
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
    }
}