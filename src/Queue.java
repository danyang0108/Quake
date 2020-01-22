///Author: Ethan Zhang
//Class: ICS4U
//Date: Jan 12th, 2020
//Instructor: Mr Radulovic
//Assignment name: ICS4U Culminating
/*Description: This class implements Queue_Interface. This class contains
 * the operations for the Queue ADT.
*/
public class Queue<T> implements Queue_Interface<T>{
    private ArrayList<T> array = new ArrayList<>(); //Implement queue with ArrayList

    public void enqueue(T node){
        //Add node; for this queue, we add to the end of the ArrayList
        array.add(node);
    }

    public T dequeue(){
        //For this queue, we remove elements from the beginning of the ArrayList
        T temp = array.get(0); //Get the value
        array.removeFront(); //Remove node
        return temp;
    }

    public boolean isEmpty(){
        //Checks if the queue is empty or not
        return array.size() == 0;
    }
}