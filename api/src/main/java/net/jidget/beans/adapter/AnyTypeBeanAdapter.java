package net.jidget.beans.adapter;

import javafx.beans.value.ObservableValue;
import net.jidget.beans.*;
import org.xml.sax.Attributes;

/**
 *
 * @author Arian Treffer
 */
public class AnyTypeBeanAdapter implements BeanAdapter {
    
    private final Property property;

    public AnyTypeBeanAdapter(BeanManager beanManager) {
        property = new Property(beanManager);
    }

    @Override
    public Object create(String className, Attributes attributes) throws BeanException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object create(String ns, String tag, Attributes attributes) throws BeanException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object updateOrCreate(Object bean, String className, Attributes attributes) throws BeanException {
        return bean;
    }

    @Override
    public Object updateOrCreate(Object bean, String ns, String tag, Attributes attributes) throws BeanException {
        // TODO warn if ns != null || not attributes empty
        return bean;
    }

    @Override
    public PropertyAdapter getProperty(Object bean, String ns, String tag) throws BeanException {
        return property;
    }

    @Override
    public PropertyAdapter getDefaultProperty(Object bean, String ns, String tag) throws BeanException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object setText(Object bean, int index, String text) throws BeanException {
        if (text == null || text.isEmpty()) return bean;
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object complete(Object bean) throws BeanException {
        return bean;
    }

    @Override
    public void setUtils(Object bean, BeanUtils beanUtils) {
    }

    public static class Property implements PropertyAdapter {
        
        private final BeanManager beanManager;

        public Property(BeanManager beanManager) {
            this.beanManager = beanManager;
        }

        @Override
        public Object getItem(Object bean, String ns, String tag, int index) throws BeanException {
            return null;
        }

        @Override
        public BeanAdapter getItemAdapter(Object bean, String ns, String tag, int index) throws BeanException {
            return null;
        }

        @Override
        public Object setItem(Object bean, String ns, String tag, int index, Object item) throws BeanException {
            return item;
        }

        @Override
        public ObservableValue<?> observe(Object bean, String ns, String tag, int index) throws BeanException {
            return null;
        }

        @Override
        public void bind(Object bean, String ns, String tag, int index, ObservableValue<?> binding) throws BeanException {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}
