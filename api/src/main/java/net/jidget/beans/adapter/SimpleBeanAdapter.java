package net.jidget.beans.adapter;

import java.util.Map;
import net.jidget.beans.*;
import net.jidget.beans.type.factory.BeanFactory;
import net.jidget.utils.AttributesImpl;
import org.xml.sax.Attributes;

/**
 *
 * @author Arian Treffer
 */
public class SimpleBeanAdapter implements BeanAdapter {

    protected final String name;
    protected final BeanManager beanManager;
    protected final BeanFactory beanFactory;
    protected final Map<String, PropertyAdapter> properties;
    protected final boolean defaultIsGeneric;
    protected final PropertyAdapter defaultProperty;

    public SimpleBeanAdapter(String name, BeanManager beanManager, BeanFactory beanFactory, Map<String, PropertyAdapter> properties, PropertyAdapter defaultProperty, boolean isGeneric) {
        this.name = name;
        this.beanManager = beanManager;
        this.beanFactory = beanFactory;
        this.properties = properties;
        this.defaultProperty = defaultProperty;
        this.defaultIsGeneric = isGeneric;
    }

    @Override
    public Object create(String className, Attributes attributes) throws BeanException {
        return beanFactory.create(className, attributes);
    }

    @Override
    public Object create(String ns, String tag, Attributes attributes) throws BeanException {
        return beanFactory.create(ns, tag, attributes);
    }

    @Override
    public Object updateOrCreate(Object bean, String className, Attributes attributes) throws BeanException {
        return beanFactory.updateOrCreate(bean, className, attributes);
    }

    @Override
    public Object updateOrCreate(Object bean, String ns, String tag, Attributes attributes) throws BeanException {
        return beanFactory.updateOrCreate(bean, ns, tag, attributes);
    }

    @Override
    public PropertyAdapter getProperty(Object bean, String ns, String tag) throws BeanException {
        if (ns != null && !ns.isEmpty()) return null;
        PropertyAdapter pa = properties.get(tag);
        if (pa == null && defaultIsGeneric) return defaultProperty;
        return pa;
    }

    @Override
    public PropertyAdapter getDefaultProperty(Object bean, String ns, String tag) throws BeanException {
        return defaultIsGeneric ? null : defaultProperty;
    }

    @Override
    public Object setText(Object bean, int index, String text) throws BeanException {
        if (text == null || text.isEmpty()) return bean;
        if (defaultProperty != null) {
            Object value = defaultProperty.getItem(bean, null, null, index);
            BeanAdapter va = defaultProperty.getItemAdapter(bean, null, null, index);
            if (va == null) {
                value = text;
            } else {
                value = va.updateOrCreate(value, null, null, AttributesImpl.EMPTY);
                value = va.setText(value, 0, text);
                value = va.complete(value);
            }
            bean = defaultProperty.setItem(bean, null, null, index, value);
            return bean;
            //BeanAdapter ba = defaultProperty.getItemAdapter(bean, null, "value", index);
            // TODO set default prop
            //throw new UnsupportedOperationException("set default prop");
        }
        throw new UnsupportedOperationException("cannot set text");
    }

    @Override
    public Object complete(Object bean) throws BeanException {
        return bean;
    }

    @Override
    public void setUtils(Object bean, BeanUtils beanUtils) {
        if (bean instanceof BeanWithUtils) {
            ((BeanWithUtils) bean).setUtils(beanUtils);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " " +
                name + "(" + 
                beanFactory +
                "; " + properties.size() + " properties" +
                (defaultProperty == null ? "" : 
                ((defaultIsGeneric ? ", generic=" : ", default=") + defaultProperty)) +
                ")";
    }
    
}
