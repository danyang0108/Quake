public interface AL<T>{
    void add(T n);
    void removeFront();
    void replaceNode(T n, int i);
    T get(int i);
    int size();
}
