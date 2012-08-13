package net.jidget.beans.type.factory;

import net.jidget.beans.BeanException;
import org.xml.sax.Attributes;

/**
 * Can be invoked to create an object.
 * 
 * @author Arian Treffer
 */
public interface FactoryMethod {
    
    public Signature getSignature();
    
    public Object invoke(Attributes attributes) throws BeanException;
    
}
