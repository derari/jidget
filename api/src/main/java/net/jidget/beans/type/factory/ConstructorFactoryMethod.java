package net.jidget.beans.type.factory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import net.jidget.beans.BeanException;
import net.jidget.beans.BeanManager;
import net.jidget.beans.adapter.ValueParser;
import net.jidget.beans.type.Parameters;
import org.xml.sax.Attributes;

/**
 * FactoryMethod which invokes a constructor.
 * 
 * @author Arian Treffer
 */
public class ConstructorFactoryMethod implements FactoryMethod {
    
    private static final String[] NO_PARAMS = new String[0];

    private final BeanManager beanManager;
    private final Constructor<?> constructor;
    private final Signature signature;
    private final String[] parameters;
    private ValueParser[] valueParsers;

    public ConstructorFactoryMethod(Constructor<?> constructor, Parameters params, BeanManager beanManager) throws BeanException {
        this.parameters = params != null ? params.value() : NO_PARAMS;
        this.signature = new Signature(null, parameters);
        this.constructor = constructor;
        this.beanManager = beanManager;
        final Class[] paramTypes = constructor.getParameterTypes();
        if (paramTypes.length != parameters.length) {
            throw new IllegalArgumentException(constructor + " expects " +
                    paramTypes.length + " parameters, " + 
                    parameters.length + " are given.");
        }
    }
    
    @Override
    public Signature getSignature() {
        return signature;
    }
    
    private ValueParser[] parsers() throws BeanException {
        if (valueParsers == null) {
            valueParsers = initParsers();
        }
        return valueParsers;
    }

    private synchronized ValueParser[] initParsers() throws BeanException {
        if (valueParsers != null) return valueParsers;
        ValueParser[] result = new ValueParser[parameters.length];
        final Class[] paramTypes = constructor.getParameterTypes();
        for (int i = 0; i < parameters.length; i++) {
            result[i] = beanManager.getParser(paramTypes[i]);
        }
        return result;
    }
    
    @Override
    public Object invoke(Attributes attributes) throws BeanException {
        final int len = parameters.length;
        final Object[] args = len > 0 ? new Object[len] : null;
        final ValueParser[] parsers = parsers();
        for (int i = 0; i < len; i++) {
            String value = attributes.getValue(null, parameters[i]);
            args[i] = parsers[i].parse(value);
        }
        try {
            return constructor.newInstance(args);
        } catch (IllegalAccessException | InstantiationException e) {
            throw new BeanException(e);
        } catch (InvocationTargetException e) {
            throw new BeanException(e.getCause());
        }
    }
    
    public static class Definition implements FactoryMethodDefinition {

        private Constructor<?> constructor;
        private Parameters parameters;

        public Constructor<?> getConstructor() {
            return constructor;
        }

        public void setConstructor(Constructor<?> constructor) {
            this.constructor = constructor;
        }

        public Parameters getParameters() {
            return parameters;
        }

        public void setParameters(Parameters parameters) {
            this.parameters = parameters;
        }

        public Definition() {
        }

        public Definition(Constructor<?> constructor, Parameters parameters) {
            this.constructor = constructor;
            this.parameters = parameters;
        }
        
        @Override
        public ConstructorFactoryMethod create(BeanManager beanManager) throws BeanException {
            return new ConstructorFactoryMethod(constructor, parameters, beanManager);
        }
        
    }
    
}
