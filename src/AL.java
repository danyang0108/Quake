///Author: Ethan Zhang
//Class: ICS4U
//Date: Jan 10th, 2020
//Instructor: Mr Radulovic
//Assignment name: ICS4U Culminating
/*Description: This is an interface for implementing the ArrayList ADT. 
 */
public interface AL<T>{
    void add(T n);
    void removeFront();
    void replaceNode(T n, int i);
    T get(int i);
    int size();
}
