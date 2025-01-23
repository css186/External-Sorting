/**
 * A implementation of doubly linked list
 * (Revised from my own work in project 1)
 * 
 * @param <T>
 *        data type of this linked list  
 * @author Guann-Luen Chen
 * @version 2024.10.30    
 *        
 */
public class LinkedList<T> {

    // ~ Fields ..........................................................
    //
    // ----------------------------------------------------------
    private ListNode<T> head;
    private ListNode<T> tail;
    private int size;
    
    // ~ Constructors ....................................................
    //
    // ----------------------------------------------------------
    /**
     * Initialize a doubly linked list
     * set head and tail as null
     * set size as 0
     * 
     */
    public LinkedList() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }
    
    // ----------------------------------------------------------
    /**
     * Another constructor
     * 
     * @param data
     *        node to be insert at instantiation
     */
    public LinkedList(T data) {
        ListNode<T> newNode = new ListNode<>(data);
        this.head = newNode;
        this.tail = newNode;
        this.size++;
    }
    
    // ~ Public Method ....................................................
    //
    // ----------------------------------------------------------
    /**
     * Method to know the list is empty or not
     * @return
     *         True if the length of the linked list is empty
     */
    public boolean isEmpty() {
        return this.size == 0;
    }

    // ----------------------------------------------------------
    /**
     * Method to insert data at the tail of the list
     * @param data
     *        data to insert
     * @return
     *        return true if the insertion is successful
     */
    public boolean insertTail(T data) {
        
        ListNode<T> newNode = new ListNode<>(data);
        
        // Empty case
        if (this.isEmpty()) {
            this.head = newNode;
            this.tail = newNode;
        }
        
        else {
            this.tail.setNext(newNode);
            newNode.setPrev(this.tail);
            this.tail = newNode;
        }
        
        this.size++;
        return true;
        
    }
    
    // ----------------------------------------------------------
    /**
     * Method to insert data at the head of the list
     * @param data
     *        data to insert
     * @return
     *        return true if the insertion is successful
     */
    public boolean insertHead(T data) {
        
        ListNode<T> newNode = new ListNode<>(data);
        
        // Empty case
        if (this.isEmpty()) {
            this.head = newNode;
            this.tail = newNode; 
        }
        
        else {
            newNode.setNext(this.head);
            this.head.setPrev(newNode);
            this.head = newNode;
        }
        
        this.size++;
        return true;
        
    }
    
    // ----------------------------------------------------------
    /**
     * Method to remove the last node in list
     * @return
     *         ListNode removed
     */
    public ListNode<T> removeTail() {
        // Empty case
        if (this.isEmpty()) {
            return null;
        }
        
        ListNode<T> oldTail = this.tail;
        
        // One node case
        if (this.size == 1) {
            this.head = null;
            this.tail = null;
        }
        
        else {
            this.tail = this.tail.getPrev();
            this.tail.setNext(null);
        }
        
        this.size--;
        return oldTail;
        
    }
    
    // ----------------------------------------------------------
    /**
     * Method to remove the head node in the list
     * @return
     *        ListNode removed
     */
    public ListNode<T> removeHead() {
        // Empty case
        if (this.isEmpty()) {
            return null;
        }
        
        ListNode<T> oldHead = this.head;
        
        // One node case
        if (this.size == 1) {
            this.head = null;
            this.tail = null;
        }
        
        else {
            this.head = this.head.getNext();
            this.head.setPrev(null);
        }
        
        this.size--;
        return oldHead;
  
    }
    
    // ----------------------------------------------------------
    /**
     * Check if the list contains a specific data
     * @param data
     *        data to search
     * @return
     *        true if the list contains that data
     */
    public boolean containListNode(T data) {
        ListNode<T> current = this.head;
        
        while (current != null) {
            if (data.equals(current.getData())) {
                return true;
            }
            current = current.getNext();
        }
        return false;
    }
    
    // ----------------------------------------------------------
    /**
     * Method to remove a specific data in the list
     * @param data
     *        data to remove
     * @return
     *        ListNode containing the data
     */
    public ListNode<T> removeListNode(T data) {
        // Check empty or null
        if (this.isEmpty() || data == null) {
            return null;
        }
        
        ListNode<T> current = this.head;
        while (current != null) {
            if (data.equals(current.getData())) {
                // Head case
                if (current == this.head) {
                    return this.removeHead();
                }
                // Tail case
                else if (current == this.tail) {
                    return this.removeTail();
                }
                else {
                    current.getPrev().setNext(current.getNext());
                    current.getNext().setPrev(current.getPrev());
                    this.size--;
                    return current;
                }
            }
            current = current.getNext();
        }
        return null;
    }
    
    
    // ----------------------------------------------------------
    /**
     * Method to find the ListNode at index "index"
     * @param index
     *        location to look up in the list
     * @return
     *         LintNode
     */
    public ListNode<T> findNodeAt(int index) {
        // Check if the index is out of bounds
        if (index < 0 || index >= this.size) {
            return null;
        }
      
        ListNode<T> current = this.head;
        
        // Move reference for index times
        for (int i = 0; i < index; i++) {
            current = current.getNext();
        }
        
        return current;
    }
    
    // ----------------------------------------------------------
    /**
     * Print out the list sequentially in console
     * @return
     *         String representation of the list
     */
    public String displayList() {
        ListNode<T> current = this.head;
        StringBuilder output = new StringBuilder();
        while (current != null) {
            output.append(current.getData()).append(" ");
            current = current.getNext();
        }
        return output.toString().trim();
    }
    
    // ----------------------------------------------------------
    /**
     * Method to get the first node;
     * @return
     *         The first list node of the linked list
     */
    public ListNode<T> getHead() {
        return this.head;
    }
    
    // ----------------------------------------------------------
    /**
     * Method to set the first node
     * @param head
     *        ListNode to be set to head
     */
    public void setHead(ListNode<T> head) {
        this.head = head;
    }
    
    // ----------------------------------------------------------
    /**
     * Method to get the last node
     * @return
     *         The last list node of the linked list
     */   
    public ListNode<T> getTail() {
        return this.tail;
    }
    
    // ----------------------------------------------------------
    /**
     * Method to set the last node
     * @param tail
     *        ListNode to be set to tail
     */
    public void setTail(ListNode<T> tail) {
        this.tail = tail;
    }
    
    // ----------------------------------------------------------
    /**
     * Method to get the numbers of list nodes in list
     * @return
     *         Number of list nodes in the list
     */
    public int getSize() {
        return this.size;
    }
    
    // ----------------------------------------------------------
    /**
     * Method to set new size for the list
     * @param size
     *        Number of list nodes to be set
     */
    public void setSize(int size) {
        this.size = size;
    }
    
    // ----------------------------------------------------------
    /**
     * Method to get the next node
     * @param node
     *        Current list node
     * @return
     *         The next list node
     */
    public ListNode<T> getNextNode(ListNode<T> node) {
        return node.getNext();
    }
    
    // ----------------------------------------------------------
    /**
     * Method to get the previous node
     * @param node
     *        Current list node
     * @return
     *         The previous list node
     */
    public ListNode<T> getPrevNode(ListNode<T> node) {
        return node.getPrev();
    }
    
    // ----------------------------------------------------------
    /**
     * Clear the list
     */
    public void clear() {
        head = null;
        tail = null;
        size = 0;
    }

}
