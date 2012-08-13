package net.jidget.beans;

import java.util.AbstractList;

/**
 *
 * @author Arian Treffer
 */
public class RingList<T> extends AbstractList<T> {

    private final int capacity;
    private final Object[] data;
    private int base = 0;
    private int size = 0;
    
    public RingList(int capacity) {
        this.capacity = capacity;
        data = new Object[capacity];
    }
    
    @Override
    public T get(int index) {
        return (T) data[(base + index) % capacity];
    }

    @Override
    public int size() {
        return size;
    }
    
    @Override
    public T remove(int index) {
        if (index != 0) {
            return super.remove(index);
        }
        T e = get(0);
        base++;
        size--;
        return e;
    }

    @Override
    public boolean add(T e) {
        if (size == capacity) {
            throw new IllegalStateException("Capacity reached");
        }
        data[(base+size)%capacity] = e;
        size++;
        return true;
    }

    @Override
    public void add(int index, T element) {
        if (index == size) {
            add(element);
        } else {
            super.add(index, element);
        }
    }
    
}
