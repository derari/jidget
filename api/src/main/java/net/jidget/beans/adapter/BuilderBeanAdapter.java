package net.jidget.beans.adapter;

import java.util.Map;
import javafx.util.Builder;
import net.jidget.beans.BeanException;
import net.jidget.beans.BeanManager;
import net.jidget.beans.PropertyAdapter;
import net.jidget.beans.type.factory.BeanFactory;
import org.xml.sax.Attributes;

/**
 *
 * @author Arian Treffer
 */
public class BuilderBeanAdapter extends SimpleBeanAdapter {

    public BuilderBeanAdapter(String name, BeanManager beanManager, BeanFactory beanFactory, Map<String, PropertyAdapter> properties, PropertyAdapter defaultProperty, boolean isGeneric) {
        super(name, beanManager, beanFactory, properties, defaultProperty, isGeneric);
    }

    @Override
    public Object updateOrCreate(Object bean, String className, Attributes attributes) throws BeanException {
        if (!(bean instanceof Builder)) bean = null;
        return super.updateOrCreate(bean, className, attributes);
    }

    @Override
    public Object updateOrCreate(Object bean, String ns, String tag, Attributes attributes) throws BeanException {
        if (!(bean instanceof Builder)) bean = null;
        return super.updateOrCreate(bean, ns, tag, attributes);
    }

    @Override
    public Object complete(Object bean) throws BeanException {
        if (bean instanceof Builder) {
            return ((Builder) bean).build();
        }
        return bean;
    }

}
