public class Node<T>{
    private T data; //Make the data any type, so the node can contain any data

    public Node(T n){
        //Assigns the value of the node
        data = n;
    }

    public T getValue(){
        //Returns the value of the node, in type T
        return data;
    }
}