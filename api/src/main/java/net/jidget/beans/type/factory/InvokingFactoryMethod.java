package net.jidget.beans.type.factory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import net.jidget.beans.BeanException;
import net.jidget.beans.BeanManager;
import net.jidget.beans.adapter.ValueParser;
import net.jidget.beans.type.Parameters;
import org.xml.sax.Attributes;

/**
 * FactoryMethod which invokes a method (of a factory object).
 * @author Arian Treffer
 */
public class InvokingFactoryMethod implements FactoryMethod {

    private final Method method;
    private final Object factory;
    private final Signature signature;
    private final String[] parameters;
    private final ValueParser[] parsers;

    public InvokingFactoryMethod(Method method, Object factory, Parameters params, BeanManager beanManager) throws BeanException {
        this.parameters = params != null ? params.value() : new String[0];
        this.signature = new Signature(new String[parameters.length], parameters);
        this.method = method;
        this.factory = factory;
        parsers = new ValueParser[parameters.length];
        final Class[] paramTypes = method.getParameterTypes();
        for (int i = 0; i < parameters.length; i++) {
            parsers[i] = beanManager.getParser(paramTypes[i]);
        }
    }
    
    @Override
    public Signature getSignature() {
        return signature;
    }

    @Override
    public Object invoke(Attributes attributes) throws BeanException {
        final int len = parameters.length;
        final Object[] args = len > 0 ? new Object[len] : null;
        for (int i = 0; i < len; i++) {
            String value = attributes.getValue("", parameters[i]);
            args[i] = parsers[i].parse(value);
        }
        try {
            return method.invoke(factory, args);
        } catch (IllegalAccessException e) {
            throw new BeanException(e);
        } catch (InvocationTargetException e) {
            throw new BeanException(e.getCause());
        }
    }
    
    public static class Definition implements FactoryMethodDefinition {
        
        private Method method; 
        private Object factory;
        private Parameters params;

        public Definition() {
        }

        public Definition(Method method, Object factory, Parameters params) {
            this.method = method;
            this.factory = factory;
            this.params = params;
        }

        @Override
        public InvokingFactoryMethod create(BeanManager beanManager) throws BeanException {
            return new InvokingFactoryMethod(method, factory, params, beanManager);
        }

        public Object getFactory() {
            return factory;
        }

        public void setFactory(Object factory) {
            this.factory = factory;
        }

        public Method getMethod() {
            return method;
        }

        public void setMethod(Method method) {
            this.method = method;
        }

        public Parameters getParams() {
            return params;
        }

        public void setParams(Parameters params) {
            this.params = params;
        }
        
    }
    
}
