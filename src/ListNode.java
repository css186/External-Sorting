/**
 * List node class for doubly linked list
 * (Revised from my own work in project 1)
 *
 * 
 * @param <T>
 *        data type of the data inside the ListNode  
 * @author Guann-Luen Chen
 * @version 2024.10.30
 */
public class ListNode<T> {
    // ~ Fields ....................................................
    //
    // ----------------------------------------------------------
    private T data;
    private ListNode<T> prev;
    private ListNode<T> next;
    
    // ~ Constructors ..............................................
    //
    // ----------------------------------------------------------
    /**
     * Initialization
     * @param data
     *        data to be passed in
     */
    public ListNode(T data) {
        this.data = data;
        this.prev = null;
        this.next = null;
    }
    
    // ~ Public Method ....................................................
    //
    // ----------------------------------------------------------
    /**
     * Getter to get the reference of previous list node
     * @return
     *         Reference of previous list node
     */
    public ListNode<T> getPrev() {
        return this.prev;
    }
    
    // ----------------------------------------------------------
    /**
     * Setter to set the reference to the previous list node
     * @param prev
     *        List node to be set to the previous position
     */
    public void setPrev(ListNode<T> prev) {
        this.prev = prev;
    }
    
    // ----------------------------------------------------------
    /**
     * Getter to get the reference of next list node
     * @return
     *         Reference of next list node
     */
    public ListNode<T> getNext() {
        return this.next;
    }
    
    // ----------------------------------------------------------
    /**
     * Setter to set the reference to the next list node
     * @param next
     *        List node to be set to the next position
     */
    public void setNext(ListNode<T> next) {
        this.next = next;
    }
    
    // ----------------------------------------------------------
    /**
     * Getter to get the data
     * @return 
     *         the data
     */
    public T getData() {
        return data;
    }
    
    // ----------------------------------------------------------
    /**
     * Setter to set data
     * @param data 
     *        the data to set
     */
    public void setData(T data) {
        this.data = data;
    }
    
}