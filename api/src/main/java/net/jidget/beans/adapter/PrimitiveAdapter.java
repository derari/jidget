package net.jidget.beans.adapter;

import net.jidget.beans.*;
import org.xml.sax.Attributes;

/**
 * Parses primitive values, String and enums.
 * 
 * @author Arian Treffer
 */
public class PrimitiveAdapter implements BeanAdapter {

    private final Object defaultValue;
    private final ValueParser parser;

    public PrimitiveAdapter(Class<?> clazz, Object defaultValue) {
        this.parser = ValueParser.get(clazz);
        this.defaultValue = defaultValue;
    }
    
    @Override
    public Object create(String className, Attributes attributes) throws BeanException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object create(String ns, String tag, Attributes attributes) throws BeanException {
        return updateOrCreate(null, ns, tag, attributes);
    }

    @Override
    public Object updateOrCreate(Object bean, String className, Attributes attributes) throws BeanException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object updateOrCreate(Object bean, String ns, String tag, Attributes attributes) throws BeanException {
        String value = attributes.getValue(null, "value");
        if (value == null) return bean != null ? bean : defaultValue;
        return parser.parse(value);
    }

    @Override
    public PropertyAdapter getProperty(Object bean, String ns, String tag) throws BeanException {
        throw new BeanException("Primitive has no properties");
    }

    @Override
    public PropertyAdapter getDefaultProperty(Object bean, String ns, String tag) throws BeanException {
        throw new BeanException("Primitive has no properties");
    }

    @Override
    public Object setText(Object bean, int index, String text) throws BeanException {
        return parser.parse(text);
    }

    @Override
    public Object complete(Object bean) throws BeanException {
        return bean;
    }

    @Override
    public void setUtils(Object bean, BeanUtils beanUtils) {
    }
    
}
