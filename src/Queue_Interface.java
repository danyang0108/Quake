///Author: Ethan Zhang
//Class: ICS4U
//Date: Jan 2nd, 2020
//Instructor: Mr Radulovic
//Assignment name: ICS4U Culminating
/*Description: This interface includes the operations of the Queue ADT.
*/
public interface Queue_Interface<T> {
	
	//inserts an element into the front of queue
	public void enqueue(T node);
	
	//removes an element from the front of queue
	public T dequeue();
	
	//check if the queue is empty
	public boolean isEmpty();
}
