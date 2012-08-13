package net.jidget.beans;

import java.util.HashMap;
import java.util.Map;
import net.jidget.beans.adapter.*;
import net.jidget.beans.type.ListBeanType;

/**
 *
 * @author Arian Treffer
 */
public final class BeanManager {

    private final Map<TypeDescriptor, BeanAdapter> adapterByClass = new HashMap<>();
    private final Map<QName, BeanAdapter> adapterByName = new HashMap<>();
    private final Map<Class, ValueParser> parsers = new HashMap<>();

    public BeanManager() throws BeanException {
        registerDefaultAdapters();
    }

    private void registerDefaultAdapters() throws BeanException {
        registerAdapter(String.class, new PrimitiveAdapter(String.class, ""));
        registerAdapter(Boolean.class, boolean.class, new PrimitiveAdapter(Boolean.class, false));
        registerAdapter(Integer.class, int.class, new PrimitiveAdapter(Integer.class, 0));
        registerAdapter(Double.class, double.class, new PrimitiveAdapter(Double.class, 0.0));
        registerAdapter(Void.class, new AnyTypeBeanAdapter(this));
        new ListBeanType(Object.class).registerTo(this);
    }

    private void registerAdapter(Class<?> c1, Class<?> c2, BeanAdapter a) {
        registerAdapter(c1, a);
        registerAdapter(c2, a);
    }

    public void registerAdapter(Class<?> clazz, BeanAdapter adapter) {
        TypeDescriptor type = TypeDescriptor.simpleType(clazz);
        registerAdapter(type, adapter);
    }
    
    public void registerAdapter(TypeDescriptor type, BeanAdapter adapter) {
        adapterByClass.put(type, adapter);
    }

    public void registerAdapter(String namespaceURI, String localName, BeanAdapter adapter) {
        QName qName = new QName(
                namespaceURI != null ? namespaceURI : "",
                localName);
        adapterByName.put(qName, adapter);
    }

    public BeanAdapter getAdapter(String ns, String localName) throws BeanException {
        QName qName = new QName(
                ns != null && !ns.isEmpty() ? ns : null,
                localName);
        return adapterByName.get(qName);
//        if (JidgetSchemaResolver.NS_JIDGET_0_1.equals(ns)) {
//            if ("text".equals(localName)) {
//                return getAdapter(Text.class);
//            } else if ("vBox".equals(localName)) {
//                return getAdapter(VBox.class);
//            }
//        }
//        return new ReflectiveBeanAdapter(this, null);
    }

    public BeanAdapter getAdapter(String className) throws BeanException {
        try {
            Class clazz = Class.forName(className);
            return getAdapter(clazz);
        } catch (ClassNotFoundException e) {
            throw new BeanException(e);
        }
//        return new ReflectiveBeanAdapter(this, null);
    }

    public BeanAdapter getAdapter(Class<?> clazz) {
        TypeDescriptor type = TypeDescriptor.simpleType(clazz);
        return getAdapter(type);
    }

    public BeanAdapter getAdapter(TypeDescriptor type) {
        return adapterByClass.get(type);
    }

    public synchronized ValueParser getParser(Class<?> clazz) throws BeanException {
        ValueParser p = findParser(clazz);
        if (p == null) {
            throw new BeanException(
                    "No parser for " + clazz.getName());
        }
        return p;
    }

    public synchronized ValueParser findParser(Class<?> clazz) {
        ValueParser parser = parsers.get(clazz);
        if (parser == null) {
            parser = ValueParser.get(clazz);
            if (parser == null) {
                BeanAdapter a = getAdapter(clazz);
                if (a == null) {
                    return null;
                }
                parser = new ValueParser.BeanParser(clazz.getName(), a);
            }
            parsers.put(clazz, parser);
        }
        return parser;
    }

    private static final class QName {

        private String ns;
        private String tag;

        public QName(String ns, String tag) {
            init(ns, tag);
        }

        public void init(String ns, String tag) {
            this.ns = ns;
            this.tag = tag;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final QName other = (QName) obj;
            if ((this.tag == null) ? (other.tag != null) : !this.tag.equals(other.tag)) {
                return false;
            }
            if (this.ns != null && other.ns != null) {
                return this.ns.equals(other.ns);
            }
            return true;
        }

        @Override
        public int hashCode() {
            return tag != null ? tag.hashCode() : 0;
        }
    }
}
