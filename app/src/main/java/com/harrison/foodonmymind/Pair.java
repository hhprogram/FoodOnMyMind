package com.harrison.foodonmymind;

/**
 * Created by harrison on 6/14/17.
 * Simple Pair class to implement a tuple like object
 */

public class Pair<T,V>{
    private T first;
    private V second;

    public Pair(T first, V second) {
        this.first = first;
        this.second = second;
    }

    public T getFirst() {
        return this.first;
    }

    public V getSecond() {
        return this.second;
    }

}
