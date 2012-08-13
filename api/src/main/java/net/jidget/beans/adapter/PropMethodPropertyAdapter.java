package net.jidget.beans.adapter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WritableValue;
import net.jidget.beans.*;

/**
 *
 * @author Arian Treffer
 */
public class PropMethodPropertyAdapter implements PropertyAdapter {
    
    protected final BeanManager beanManager;

    private final String name;
    private final boolean generic;
    private final Method property;
    private final TypeDescriptor type;

    public PropMethodPropertyAdapter(BeanManager beanManager, String name, Method property, TypeDescriptor type, boolean generic) {
        this.beanManager = beanManager;
        this.name = name;
        this.property = property;
        this.type = type;
        this.generic = generic;
    }
    
    @Override
    public Object getItem(Object bean, String ns, String tag, int index) throws BeanException {        
        ObservableValue<?> v = (ObservableValue<?>) invoke(bean, tag);
        return v != null ? v.getValue() : null;
    }

    protected Object invoke(Object bean, String tag) throws BeanException {
        try {
            if (generic) {
                return property.invoke(bean, tag);
            } else {
                return property.invoke(bean);
            }
        } catch (IllegalAccessException e) {
            throw new BeanException(e);
        } catch (InvocationTargetException e) {
            throw new BeanException(e.getCause());
        }
    }

    @Override
    public BeanAdapter getItemAdapter(Object bean, String ns, String tag, int index) throws BeanException {
        if (type != null) {
            return beanManager.getAdapter(type);
        }
        return null;
    }

    @Override
    public Object setItem(Object bean, String ns, String tag, int index, Object item) throws BeanException {
        WritableValue<Object> v = (WritableValue) invoke(bean, tag);
        if (v == null) {
            throw new BeanException(
                    "Cannot set " + ns + ":" + tag + " of " + bean + 
                    ", property is null.");
        }
        v.setValue(item);
        return bean;
    }

    @Override
    public ObservableValue<?> observe(Object bean, String ns, String tag, int index) throws BeanException {
        ObservableValue<?> v = (ObservableValue) invoke(bean, tag);
        return v;
    }

    @Override
    public void bind(Object bean, String ns, String tag, int index, ObservableValue<?> binding) throws BeanException {
        Property<?> v = (Property) invoke(bean, tag);
        v.bind((ObservableValue) binding);
    }

    @Override
    public String toString() {
        return 
                "(" + getClass().getSimpleName() + ")" +
                (type != null ? " " + type.toString().substring(2): "") +
                (name != null ? " " + name: "") +
                (property != null ? " (" + property.getName() + ")" : "") +
                (generic ? " [generic]" : "") +
                "";
    }  
    
}
