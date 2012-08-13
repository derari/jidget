package net.jidget.beans.adapter;

import java.lang.reflect.Field;
import javafx.beans.Observable;
import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WritableValue;
import net.jidget.beans.*;

/**
 *
 * @author Arian Treffer
 */
public class FieldPropertyAdapter implements PropertyAdapter {
    
    protected final BeanManager beanManager;

    private final boolean isProperty;
    private final String name;
    private final Field field;
    private final TypeDescriptor type;

    public FieldPropertyAdapter(BeanManager beanManager, String name, Field field, TypeDescriptor type) {
        this.beanManager = beanManager;
        this.name = name;
        this.field = field;
        this.type = type;
        isProperty = Observable.class.isAssignableFrom(field.getType());
    }
    
    @Override
    public Object getItem(Object bean, String ns, String tag, int index) throws BeanException {
        try {
            Object v = field.get(bean);
            if (isProperty && v != null) {
                v = ((ObservableValue<?>) v).getValue();
            }
            return v;
        } catch (IllegalAccessException e) {
            throw new BeanException(e);
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
        try {
            if (isProperty) {
                WritableValue<Object> v = (WritableValue) field.get(bean);
                v.setValue(item);
            } else {
                field.set(bean, item);
            }
            return bean;
        } catch (IllegalAccessException e) {
            throw new BeanException(e);
        }
    }

    @Override
    public ObservableValue<?> observe(Object bean, String ns, String tag, int index) throws BeanException {
        try {
            if (!isProperty) return null;
            return (ObservableValue<?>) field.get(bean);
        } catch (IllegalAccessException e) {
            throw new BeanException(e);
        }
    }

    @Override
    public void bind(Object bean, String ns, String tag, int index, ObservableValue<?> binding) throws BeanException {
        try {
            if (!isProperty) {
                throw new BeanException("Cannot bind to " + field.getName());
            }
            final Property<?> p = (Property<?>) field.get(bean);
            p.bind((ObservableValue) binding);
        } catch (IllegalAccessException e) {
            throw new BeanException(e);
        }
    }

    @Override
    public String toString() {
        return 
                "(" + getClass().getSimpleName() + ")" +
                (type != null ? " " + type.toString().substring(2) : "") +
                (name != null ? " " + name: "") +
                (field != null ? " (" + field.getName() + ")" : "") +
                (isProperty ? " [property]" : "") +
                "";
    }
    
}
