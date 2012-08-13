package net.jidget.utils;

import javafx.beans.property.SimpleBooleanProperty;

/**
 *
 * @author Arian Treffer
 */
public class UpdateSignal extends SimpleBooleanProperty implements Runnable {

    public UpdateSignal(Object owner) {
        this(owner, "update");
    }
    
    public UpdateSignal(Object owner, String name) {
        super(owner, name, false);
    }

    public UpdateSignal() {
    }
    
    public void trigger() {
        set(!get());
    }
    
    @Override
    public void run() {
        trigger();
    }
    
}
