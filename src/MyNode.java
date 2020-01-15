import java.util.ArrayList;

public class MyNode<T> implements Node<T>{

    private T data;
    private Node<T> prev, next;

    public MyNode(T n){
        data = n;
    }

    public static void main(String[] args){
        MyNode<Integer> test = new MyNode(new Integer(5));
        System.out.println(test);
        MyNode<String> s = new MyNode("Hello world!");
        System.out.println(s);
        MyNode<String> second = new MyNode("Second word!");
        ArrayList<MyNode> list = new ArrayList();
        list.add(test);
        list.add(s);
        for (MyNode i: list) System.out.print(i + " ");
        s.setNext(second); //sets pointer
    }

    @Override
    public T getValue(){
        return data;
    }

    @Override
    public void setValue(T n){
        data = n;
    }

    @Override
    public void setNext(Node<T> n){
        next = n;
    }

    @Override
    public void setPrev(Node<T> n){
        prev = n;
    }

    @Override
    public Node<T> getNext(){
        return next;
    }

    @Override
    public Node<T> getPrev(){
        return prev;
    }

    @Override
    public String toString(){
        return data.toString();
    }
}