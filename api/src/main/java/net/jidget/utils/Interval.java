package net.jidget.utils;

/**
 *
 * @author Arian Treffer
 */
public class Interval {
    
    public static final Interval NONE = new Interval(-1);
    public static final Interval DEFAULT = new Interval(0);
    
    private final int ms;

    public Interval(int ms) {
        this.ms = ms;
    }

    public int getMilliseconds() {
        return ms;
    }
    
}
