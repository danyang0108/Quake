public interface Queue_Interface<T> {
	
	//inserts an element into the front of queue
	public void enqueue(T node);
	
	//removes an element from the front of queue
	public T dequeue();
	
	//check if the queue is empty
	public boolean isEmpty();
}
