package net.jidget.beans.type;

import java.util.Map;
import javafx.util.Builder;
import net.jidget.beans.*;
import net.jidget.beans.adapter.BuilderBeanAdapter;
import net.jidget.beans.type.factory.BeanFactory;

/**
 *
 * @author Arian Treffer
 */
public class BuilderBeanType extends SimpleBeanType {
    
    private final Class builder;
    
    public <T> BuilderBeanType(Class<T> clazz, Class<? extends Builder<? extends T>> builder, String ns, String tag) {
        super(clazz, ns, tag);
        this.builder = builder;
        init(builder);
    }

    public <T> BuilderBeanType(Class<T> clazz, Class<? extends Builder<? extends T>> builder) {
        super(clazz);
        this.builder = builder;
        init(builder);
    }
    
    public <T> BuilderBeanType(TypeDescriptor clazz, Class<? extends Builder<? extends T>> builder) {
        super(clazz);
        this.builder = builder;
        init(builder);
    }
    
    private void init(Class<? extends Builder<?>> clazz) {
        scanConstructors(clazz);
        scanFactories(clazz);
        scanProperties(clazz);
        BuilderDataBuilder.forBuilderBeanType(super.builder(builder));
    }
    
//    protected void useDefaultFactory() {
//        //factory("create");
//        BuilderDataBuilder.forBuilderBeanType(super.builder(builder));
//    }

    @Override
    public BuilderDataBuilder builder(Class<? extends Builder> clazz) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Class implClass() {
        return builder;
    }
    
    @Override
    protected BeanAdapter createAdapter(BeanManager bm, BeanFactory bf, Map<String, PropertyAdapter> pAdapters, PropertyAdapter defaultPA) {
        return new BuilderBeanAdapter(adapterName(), bm, bf, pAdapters, defaultPA, defaultIsGeneric);
    }
 
    @Override
    public void registerTo(BeanManager beanManager) throws BeanException {
        super.registerTo(beanManager);
    }
    
    
    
}
