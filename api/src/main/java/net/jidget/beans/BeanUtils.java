package net.jidget.beans;

import java.net.URI;

/**
 *
 * @author Arian Treffer
 */
public interface BeanUtils {
    
    public TaskRegistry getTaskRegistry();
    
    public URI resolve(String uriOrFile);
    
}
