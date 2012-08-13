package net.jidget.beans.type.factory;

import java.util.*;
import net.jidget.beans.BeanException;
import org.xml.sax.Attributes;

/**
 * Wraps a set of factory methods.
 * 
 * @author Arian Treffer
 */
public class SimpleFactory implements BeanFactory {
    
    private final Class<?> clazz;
    private final Map<Signature, FactoryMethod> methods = new HashMap<>();
    private final FactoryMethod builder;

    public SimpleFactory(Class<?> clazz, List<FactoryMethod> methods, FactoryMethod builder) {
        this.clazz = clazz;
        this.builder = builder;
        for (FactoryMethod m: methods) {
            this.methods.put(m.getSignature(), m);
        }
    }

    @Override
    public Object create(String className, Attributes attributes) throws BeanException {
//        if (className != null && !className.equals(clazz.getName())) {
//            throw new BeanException("Unexpected class name " + className);
//        }
        Signature sig = AttributesSignature.get(attributes);
        FactoryMethod m = methods.get(sig);
        m = m != null ? m : builder;
        if (m == null) {
            throw new BeanException("No constructor with signature " + sig);
        }
        return m.invoke(attributes);
    }

    @Override
    public Object create(String ns, String tag, Attributes attributes) throws BeanException {
        return create(null, attributes);
    }

    @Override
    public Object updateOrCreate(Object bean, String className, Attributes attributes) throws BeanException {
        if (bean == null || attributes.getLength() > 0) {
            return create(className, attributes);
        } else {
            return bean;
        }
    }

    @Override
    public Object updateOrCreate(Object bean, String ns, String tag, Attributes attributes) throws BeanException {
        if (!clazz.isInstance(bean) || attributes.getLength() > 0) {
            return create(ns, tag, attributes);
        } else {
            return bean;
        }
    }

    @Override
    public String toString() {
        return getClass().getName() +
                " " + methods.size() + " constructors" +
                (builder != null ? ", +builder" : "") +
                "";
    }
    
}
