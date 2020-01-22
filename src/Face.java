///Author: Ethan Zhang
//Class: ICS4U
//Date: Jan 5th, 2020
//Instructor: Mr Radulovic
//Assignment name: ICS4U Culminating
/*Description: This class is for organizing the faces and vertices
 * of objects.
 */
public class Face{

    private int[] vertexIndices; //Stores the vertices
    private int[] textureCoordIndices; //Stores the texture coordinates
    private int[] normalIndices; //Stores the normals

    public Face(int size){
        //Given the size of the face, initialize the arrays.
        vertexIndices = new int[size];
        textureCoordIndices = new int[size];
        normalIndices = new int[size];
    }

    //Returns the size of the face. Since all three Arrays have identical lengths,
    //returning any of the lengths works.
    public int getSize(){
        return vertexIndices.length;
    }

    //Define the vertex at the given index.
    public void setVertex(int i, int[] attrib){
        vertexIndices[i] = attrib[0];
        textureCoordIndices[i] = attrib[1];
        normalIndices[i] = attrib[2];
    }

    //Returns the index of the vertex.
    public int getVertexIndex(int i){
        return vertexIndices[i];
    }

    //Returns the index of the texture.
    public int getTextureIndex(int i){
        return textureCoordIndices[i];
    }

    //Returns the index of the normal.
    public int getNormalIndex(int i){
        return normalIndices[i];
    }
}
