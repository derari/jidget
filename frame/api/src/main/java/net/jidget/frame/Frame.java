package net.jidget.frame;

/**
 * The visual representation of a Jidget.
 * 
 * @author Arian Treffer
 */
public interface Frame {
    
    double getX();
    
    double getY();
    
    void setLocation(double x, double y);

    void close();
    
}
