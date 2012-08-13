package net.jidget.beans.adapter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javafx.beans.Observable;
import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WritableValue;
import net.jidget.beans.*;

/**
 *
 * @author Arian Treffer
 */
public class GetSetPropertyAdapter extends PropMethodPropertyAdapter {
    
    private final boolean hasBinding;
    private final boolean generic;
    private final boolean isProperty;
    private final Method setter;
    private final Method getter;
    private final TypeDescriptor type;

    public GetSetPropertyAdapter(BeanManager beanManager, String name, Method setter, Method getter, Method binding, TypeDescriptor type, boolean generic) {
        super(beanManager, name, binding, type, generic);
        this.setter = setter;
        this.getter = getter;
        this.type = type;
        this.generic = generic;
        this.hasBinding = binding != null;
        final Class fType;
        if (setter != null) {
            fType = setter.getParameterTypes()[0];
        } else {
            fType = getter.getReturnType();
        }
        isProperty = Observable.class.isAssignableFrom(fType);
    }
    
    @Override
    public Object getItem(Object bean, String ns, String tag, int index) throws BeanException {
        if (getter == null) return null;
        Object v = invokeGet(bean, tag);
        if (isProperty) {
            if (type.getType().isInstance(v)) {
                return v;
            } else if (v instanceof ObservableValue) {
                v = ((ObservableValue<?>) v).getValue();
            } else {
                v = null;
            }
        }
        return v;
    }

    protected Object invokeGet(Object bean, String tag) throws BeanException {
        try {
            if (generic) {
                return getter.invoke(bean, tag);
            } else {
                return getter.invoke(bean);
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
            if (isProperty && getter != null) {
                Observable o = (Observable) invokeGet(bean, tag);
                if (o instanceof WritableValue) {
                    WritableValue<Object> v = (WritableValue) o;
                    v.setValue(item);
                }
            } else {
                if (setter == null) {
                    throw new BeanException("Cannot set " + tag);
                }
                invokeSet(bean, tag, item);
            }
            return bean;
    }

    protected void invokeSet(Object bean, String tag, Object item) throws BeanException {
        try {
            if (generic) {
                setter.invoke(bean, tag, item);
            } else {
                setter.invoke(bean, item);
            }
        } catch (IllegalAccessException e) {
            throw new BeanException(e);
        } catch (InvocationTargetException e) {
            throw new BeanException(e.getCause());
        }
    }

    @Override
    public ObservableValue<?> observe(Object bean, String ns, String tag, int index) throws BeanException {
        if (!isProperty) {
            if (hasBinding) {
                return super.observe(bean, ns, tag, index);
            } else {
                return null;
            }
        }
        return (ObservableValue<?>) invokeGet(bean, tag);
    }

    @Override
    public void bind(Object bean, String ns, String tag, int index, ObservableValue<?> binding) throws BeanException {
        if (!isProperty) {
            if (hasBinding) {
                super.bind(bean, ns, tag, index, binding);
            } else {
                throw new BeanException("Cannot bind to " + tag);
            }
        } else {
            final Property<?> p = (Property<?>) invokeGet(bean, tag);
            p.bind((ObservableValue) binding);
        }
    }

    @Override
    public String toString() {
        return super.toString() +
                " (" +
                (getter != null ? getter.getName() + " " : "") +
                "->" +
                (setter != null ? " " + setter.getName() : "") +
                ")" +
                (isProperty ? " [property]" : "") +
                "";
    }
    
}
