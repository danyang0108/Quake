public class MyArrayList<T> implements ArrayList<T>{

    private T data;
    private int size = 100000002;
    private int length = 0;
    private MyNode[] array = new MyNode[size];

    @Override
    public void addNode(Node n){
        array[length++] = new MyNode(n);
    }

    @Override
    public void insertNode(Node n, int i){
        for (int loop = length; loop > i; loop--) array[loop] = array[loop-1];
        array[i] = new MyNode(n);
        length++;
    }

    @Override
    public void removeNode(Node n){
        int index = -1;
        for (int i = 0; i < length; i++){
            if (array[i].equals(new MyNode(n))) index = i;
        }
        if (index == -1) return;
        for (int loop = index; loop < length - 1; loop++) array[loop] = array[loop + 1];
        length = Math.max(length - 1, 0);
    }

    @Override
    public Node<T> removeNode(int i){
        for (int loop = i; loop < length - 1; loop++) array[loop] = array[loop + 1];
        length = Math.max(length - 1, 0);
        return null; //Successfully removed
    }

    @Override
    public Node getNode(int i){
        return array[i];
    }

    @Override
    public Node getFirstNode(){
        return array[0];
    }

    @Override
    public Node getLastNode(){
        if (length == 0) return null;
        return array[length-1];
    }

    @Override
    public int size(){
        return length;
    }
}