public class MyArrayQueue<T> implements ArrayQueue<T>{

    private int length = 0;
    private MyArrayList array = new MyArrayList();

    @Override
    public void enqueue(Node<T> node){
        array.addNode(node);
        length++;
    }

    @Override
    public Node<T> dequeue(){
        if (array.size() <= 0) return null;
        Node temp = array.getFirstNode();
        array.removeNode(0);
        length--;
        return temp;
    }

    @Override
    public Node<T> peek(){
        return array.getFirstNode();
    }

    @Override
    public int size(){
        return length;
    }

    @Override
    public boolean isEmpty(){
        return length == 0;
    }
}