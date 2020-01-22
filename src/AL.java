///Author: Ethan Zhang
//Class: ICS4U
//Date: Jan 10th, 2020
//Instructor: Mr Radulovic
//Assignment name: ICS4U Culminating
/*Description: This is an interface for implementing the ArrayList ADT. 
 */
public interface AL<T>{
    void add(T n); //For adding an element into the ArrayList
    void removeFront(); //For removing the first element in the ArrayList
    void replaceNode(T n, int i); //Change the value of an element
    T get(int i); //Return the value stored at the given index
    int size(); //Returns the size of the ArrayList
}
