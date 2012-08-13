package net.jidget.beans.type;

import net.jidget.beans.BeanManager;
import net.jidget.beans.PropertyAdapter;

/**
 *
 * @author Arian Treffer
 */
public interface PropertyDefinition {

    public String getName();
    
    public PropertyAdapter createAdapter(BeanManager beanManager);
    
}
