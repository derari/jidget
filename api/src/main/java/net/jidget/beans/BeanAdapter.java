package net.jidget.beans;

import org.xml.sax.Attributes;

/**
 * Provides a meta API for a class.
 * <p>
 * As beans may be immutable, always return the updated instance.
 * 
 * @author Arian Treffer
 */
public interface BeanAdapter {
    
    /**
     * Creates an object of the given class and applies the attributes.
     * @param className
     * @param attributes
     * @return 
     */
    Object create(String className, Attributes attributes) throws BeanException;

    /**
     * Creates an object of the given tag and applies the attributes.
     * @param ns
     * @param tag
     * @param attributes
     * @return 
     */
    Object create(String ns, String tag, Attributes attributes) throws BeanException;

    /**
     * Like {@link #create(java.lang.String, org.xml.sax.Attributes)},
     * but might reuse an existing instance.
     * @param bean
     * @param className
     * @param attributes
     * @return 
     */
    Object updateOrCreate(Object bean, String className, Attributes attributes) throws BeanException;
    
    /**
     * Like {@link #create(java.lang.String, java.lang.String, org.xml.sax.Attributes)},
     * but might reuse an existing instance.
     * @param bean
     * @param ns
     * @param tag
     * @param attributes
     * @return 
     */
    Object updateOrCreate(Object bean, String ns, String tag, Attributes attributes) throws BeanException;

    /**
     * Returns the property identified by the tag.
     * @param bean
     * @param ns
     * @param tag
     * @return
     * @throws BeanException 
     */
    PropertyAdapter getProperty(Object bean, String ns, String tag) throws BeanException;

    /**
     * Returns the property identified by the tag.
     * @param bean
     * @param ns
     * @param tag
     * @return
     * @throws BeanException 
     */
    PropertyAdapter getDefaultProperty(Object bean, String ns, String tag) throws BeanException;    

    /**
     * Adds character data to an object.
     * @param bean
     * @param index
     * @param text
     * @return the updated object.
     */
    Object setText(Object bean, int index, String text) throws BeanException;
    
    Object complete(Object bean) throws BeanException;
    
    void setUtils(Object bean, BeanUtils beanUtils);
    
    String getBeanTypeName();

}
