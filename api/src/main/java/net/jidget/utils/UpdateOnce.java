package net.jidget.utils;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;

/**
 *
 * @author Arian Treffer
 */
public class UpdateOnce implements InvalidationListener {

    private final UpdateSignal update;

    public UpdateOnce(UpdateSignal update) {
        this.update = update;
    }
    
    @Override
    public void invalidated(Observable arg0) {
        arg0.removeListener(this);
        update.trigger();
    }
    
}
