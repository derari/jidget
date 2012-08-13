package net.jidget.beans.type;

import net.jidget.beans.TypeDescriptor;

/**
 *
 * @author Arian Treffer
 */
public class BeanType extends SimpleBeanType {

    public BeanType(Class<?> clazz, String ns, String tag) {
        super(clazz, ns, tag);
        init(clazz);
    }

    public BeanType(Class<?> clazz) {
        super(clazz);
        init(clazz);
    }
    
    public BeanType(TypeDescriptor clazz, String ns, String tag) {
        super(clazz, ns, tag);
        init(clazz.getType());
    }

    public BeanType(TypeDescriptor clazz) {
        super(clazz);
        init(clazz.getType());
    }
    
    private void init(Class<?> clazz) {
        scanConstructors(clazz);
        scanFactories(clazz);
        scanProperties(clazz);
    }

}
