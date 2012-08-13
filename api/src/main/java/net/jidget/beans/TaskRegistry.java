package net.jidget.beans;

/**
 *
 * @author Arian Treffer
 */
public interface TaskRegistry {

    void addTask(Runnable r, int interval);
    
    void addTask(Runnable r, int delay, int interval);

    void stopTask(Runnable r);
    
}
