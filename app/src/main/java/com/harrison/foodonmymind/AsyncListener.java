package com.harrison.foodonmymind;

/**
 * Created by harrison on 6/14/17.
 * interface for classes that have methods that must update after an asyncTask is completed
 */

public interface AsyncListener {

    public void onTaskCompletion();
}
