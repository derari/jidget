package net.jidget.beans.type.factory;

import java.lang.reflect.*;
import java.util.HashMap;
import java.util.Map;
import javafx.util.Builder;
import net.jidget.beans.BeanException;
import net.jidget.beans.BeanManager;
import net.jidget.beans.adapter.ValueParser;
import org.xml.sax.Attributes;

/**
 *
 * @author Arian Treffer
 */
public class BuilderFactoryMethod implements FactoryMethod {
    
    private Map<String, Property> properties = null;
    private final Class builderClazz;
    private final BeanManager beanManager;
    private final Method builderFactory;
    private final Constructor<?> builderConstructor;
    private final boolean buildResult;

    public BuilderFactoryMethod(Class<? extends Builder> builderClazz, boolean buildResult, BeanManager beanManager) throws BeanException {
        this.buildResult = buildResult;
        this.builderClazz = builderClazz;
        this.beanManager = beanManager;
        Method factory = null;
        try {
            factory = builderClazz.getMethod("create");
            if ((factory.getModifiers() & Modifier.STATIC) == 0) {
                factory = null;
            }
        } catch (NoSuchMethodException | SecurityException ex) { }
        if (factory != null) {
            builderFactory = factory;
            builderConstructor = null;
        } else {
            builderFactory = null;
            try {
                builderConstructor = builderClazz.getConstructor();
            } catch (NoSuchMethodException | SecurityException ex) {
                throw new BeanException(ex);
            }
        }
    }

    private Map<String, Property> initProperties() throws SecurityException {
        Map<String, Property> result = new HashMap<>();
        for (Method m: builderClazz.getMethods()) {
            Class[] types = m.getParameterTypes();
            if (types.length != 1) continue;
            ValueParser p = beanManager.findParser(types[0]);
            if (p == null) continue;
            result.put(m.getName(), new Property(m, p));
        }
        for (Field f: builderClazz.getFields()) {
            ValueParser p = beanManager.findParser(f.getType());
            if (p == null) continue;
            result.put(f.getName(), new Property(f, p));            
        }
        return result;
    }
    
    @Override
    public Signature getSignature() {
        return Signature.EMPTY;
    }

    @Override
    public Object invoke(Attributes attributes) throws BeanException {
        try {
            Builder<?> builder;
            if (builderFactory != null) {
                builder = (Builder) builderFactory.invoke(null);
            } else {
                builder = (Builder) builderConstructor.newInstance();
            }

            final int len = attributes.getLength();
            for (int i = 0; i < len; i++) {
                String ns = attributes.getURI(i);
                if (ns == null || ns.isEmpty()) {
                    Property prop = getProperty(attributes.getLocalName(i));
                    if (prop == null) {
                        throw new BeanException("No property " + attributes.getLocalName(i));
                    }
                    Object arg = prop.p.parse(attributes.getValue(i));
                    prop.set(builder, arg);
                }
            }
            return buildResult ? builder.build() : builder;
        } catch (IllegalAccessException | InstantiationException e) {
            throw new BeanException(e);
        } catch (InvocationTargetException e) {
            throw new BeanException(e.getCause());
        }
    }

    private Property getProperty(String localName) {
        if (properties == null) {
            properties = initProperties();
        }
        return properties.get(localName);
    }
    
    private static class Property {
        public final Field f;
        public final Method m;
        public final ValueParser p;

        public Property(Method m, ValueParser p) {
            this.f = null;
            this.m = m;
            this.p = p;
        }

        public Property(Field f, ValueParser p) {
            this.f = f;
            this.m = null;
            this.p = p;
        }
        
        public void set(Object builder, Object arg) throws IllegalAccessException, InvocationTargetException {
            if (m != null) {
                m.invoke(builder, arg);
            } else {
                f.set(builder, arg);
            }
        }
        
    }
    
    public static class Definition implements FactoryMethodDefinition {

        private Class<? extends Builder> clazz;
        private boolean buildResult = true;

        public Definition() {
        }

        public Definition(Class<? extends Builder> clazz) {
            this.clazz = clazz;
        }
        
        @Override
        public FactoryMethod create(BeanManager beanManager) throws BeanException {
            return new BuilderFactoryMethod(clazz, buildResult, beanManager);
        }

        public Class<? extends Builder> getClazz() {
            return clazz;
        }

        public void setClazz(Class<? extends Builder> clazz) {
            this.clazz = clazz;
        }
        
        public boolean isBuildResult() {
            return buildResult;
        }

        public void setBuildResult(boolean buildResult) {
            this.buildResult = buildResult;
        }

    }
    
}
