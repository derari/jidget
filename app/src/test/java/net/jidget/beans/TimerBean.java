package net.jidget.beans;

import java.util.Date;
import java.util.IllegalFormatException;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Arian Treffer
 */
public class TimerBean {

    private final StringProperty value;
    private final Updater updater;
    private String pattern = "%tS";
    
    public TimerBean() {
        value = new SimpleStringProperty(this, "value", "");
        updater = new Updater();
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }
    
    public void setEnabled(boolean b) {
        if (b) {
            updater.start();
        } else {
            updater.stop();
        }
    }

    public StringProperty valueProperty() {
        return value;
    }
    
    private class Updater implements Runnable {
        
        private Thread t = null;

        @Override
        public void run() {
            try {
                while (!Thread.interrupted()) {
                    try {
                        value.set(String.format(pattern, new Date()));
                    } catch (IllegalFormatException e) {
                        value.set(e.getMessage());
                    }
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                // stopping now
            }
        }
        
        public synchronized void start() {
            if (t == null) {
                t = new Thread(this);
                t.start();
            }
        }
        
        public synchronized void stop() {
            if (t != null) {
                t.interrupt();
                t = null;
            }
        }
        
    }
}
