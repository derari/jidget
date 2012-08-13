package net.jidget.beans.type.factory;

import net.jidget.beans.BeanException;
import org.xml.sax.Attributes;

/**
 * Used by reflective bean adapter to create instances.
 * 
 * @author Arian Treffer
 */
public interface BeanFactory {
    
    /**
     * Creates an object of the given class and applies the attributes.
     * @param className
     * @param attributes
     * @return 
     */
    public Object create(String className, Attributes attributes) throws BeanException;

    /**
     * Creates an object of the given tag and applies the attributes.
     * @param ns
     * @param tag
     * @param attributes
     * @return 
     */
    public Object create(String ns, String tag, Attributes attributes) throws BeanException;

    /**
     * Like {@link #create(java.lang.String, org.xml.sax.Attributes)},
     * but might reuse an existing instance.
     * @param bean
     * @param className
     * @param attributes
     * @return 
     */
    public Object updateOrCreate(Object bean, String className, Attributes attributes) throws BeanException;
    
    /**
     * Like {@link #create(java.lang.String, java.lang.String, org.xml.sax.Attributes)},
     * but might reuse an existing instance.
     * @param bean
     * @param ns
     * @param tag
     * @param attributes
     * @return 
     */
    public Object updateOrCreate(Object bean, String ns, String tag, Attributes attributes) throws BeanException;

}
