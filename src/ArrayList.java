///Author: Ethan Zhang
//Class: ICS4U
//Date: Jan 10th, 2020
//Instructor: Mr Radulovic
//Assignment name: ICS4U Culminating
/*Description: This class implements the AL interface, and includes the 
 * operations of the ArrayList ADT. 
 */
public class ArrayList<T> implements AL<T>{
    private int size = 20001; //Default size
    private int length = 0; //Tracks the actual length of the ArrayList
    private int fix = 0; //Tracks the number of elements removed from the front
    private Node<T>[] array = new Node[size];

    @Override public void add(T n){
        //We also add "fix" to the index; read document for details
        array[fix + length++] = new Node<>(n); //Convert n into a Node
    }

    @Override public void removeFront(){
        //Removes the first element in the ArrayList
        fix++; //One more element at the front is not used
        length = Math.max(length - 1, 0); //Ensure that the value of length is non-negative
    }

    @Override public void replaceNode(T n, int i){
        //Change the value of the Node
        array[i + fix] = new Node<>(n);
    }

    @Override public T get(int i){
        //Returns the value at the given index
        return array[i + fix].getValue();
    }

    @Override public int size(){
        //Returns the length of the ArrayList
        return length;
    }
}