package net.jidget.beans.type.factory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import net.jidget.beans.BeanException;
import org.xml.sax.Attributes;

/**
 *
 * @author Arian Treffer
 */
public class NoArgsFactory implements BeanFactory {
    
    private final Class<?> clazz;
    private final Constructor<?> constructor;

    public NoArgsFactory(Class<?> clazz) throws BeanException {
        this(clazz, true);
    }
    public NoArgsFactory(Class<?> clazz, boolean force) throws BeanException {
        this.clazz = clazz;
        Constructor<?> c = null;
        try {
            c = clazz.getConstructor();
        } catch (NoSuchMethodException e) {
            if (force) throw new BeanException(e);
        }
        constructor = c;
    }

    @Override
    public Object create(String className, Attributes attributes) throws BeanException {
        if (className != null && !className.equals(clazz.getName())) {
            throw new BeanException("Unexpected class name " + className);
        }
        try {
            return constructor.newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            throw new BeanException(e);
        } catch (InvocationTargetException e) {
            throw new BeanException(e.getCause());
        }
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
        if (bean == null || attributes.getLength() > 0) {
            return create(ns, tag, attributes);
        } else {
            return bean;
        }
    }
    
}
