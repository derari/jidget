package net.jidget.beans.type;

import java.lang.reflect.*;
import java.util.*;
import javafx.beans.DefaultProperty;
import javafx.util.Builder;
import net.jidget.beans.*;
import net.jidget.beans.adapter.*;

/**
 *
 * @author Arian Treffer
 */
public class ReflectivePropertyScanner {

    private final Map<String, ReflectivePropertyDefinition> props = new HashMap<>();
    private final Class clazz;
    private final boolean isBuilder;
    
    private List<PropertyDefinition> propList;
    private String defaultName;
    private ReflectivePropertyDefinition generic;

    public ReflectivePropertyScanner(Class clazz) {
        this.clazz = clazz;
        this.isBuilder = Builder.class.isAssignableFrom(clazz);
    }
    
    public ReflectivePropertyScanner(Class clazz, boolean isBuilder) {
        this.clazz = clazz;
        this.isBuilder = isBuilder;
    }

    public List<PropertyDefinition> getPropertyList() {
        if (propList == null) {
            throw new IllegalStateException("Nothing scanned yet");
        }
        return propList;
    }

    public PropertyDefinition getGenericProperty() {
        return generic;
    }

    public String getDefaultName() {
        return defaultName;
    }
    
    public void scan() {
        DefaultProperty defP = (DefaultProperty) clazz.getAnnotation(DefaultProperty.class);
        if (defP != null) defaultName = defP.value();
        
        for (Field f: clazz.getFields()) {
            if (isStatic(f)) continue;
            getProp(f.getName()).setField(f);
        }
        for (Method m: clazz.getMethods()) {
            if (isStatic(m)) continue;
            String mName = m.getName();
            Class[] pTypes = m.getParameterTypes();
            switch (pTypes.length) {
                case 0:
                    if (mName.endsWith("Property")) {
                        String pName = mName.substring(0, mName.length() - 8);
                        getProp(pName).setProperty(m);
                    } else if (mName.startsWith("get") || mName.startsWith("is")) {
                        String pName = firstToLower(mName.substring(3));
                        getProp(pName).setGetter(m);
                    }
                    break;
                case 1:
                    if (mName.startsWith("set") || mName.startsWith("add")) {
                        String pName = firstToLower(mName.substring(3));
                        getProp(pName).setSetter(m);
                    } else if (isBuilder &&
                                Builder.class.isAssignableFrom(m.getReturnType())) {
                        getProp(mName).setSetter(m);
                    } else if (pTypes[0].isAssignableFrom(String.class)) {
                        switch (mName) {
                            case "property":
                                getGenericProp().setProperty(m);
                                break;
                            case "get":
                                getGenericProp().setGetter(m);
                                break;
                        }
                    }
                    break;
                case 2:
                    if (pTypes[0].isAssignableFrom(String.class)) {
                        if (mName.equals("set") || mName.equals("put")) {
                            getGenericProp().setSetter(m);
                        }
                    }
                    break;
            }
        }
        propList = new ArrayList<PropertyDefinition>(props.values());
    }
    
    private ReflectivePropertyDefinition getProp(String key) {
        ReflectivePropertyDefinition p = props.get(key);
        if (p == null) {
            p = new ReflectivePropertyDefinition(key);
            props.put(key, p);
        }
        return p;
    }
    
    private ReflectivePropertyDefinition getGenericProp() {
        if (generic == null) {
            generic = new ReflectivePropertyDefinition(null);
        }
        return generic;
    }
    
    private static boolean isStatic(Member m) {
        return (m.getModifiers() & Modifier.STATIC) != 0;
    }

    private static String firstToLower(String s) {
        return Character.toLowerCase(s.charAt(0)) + s.substring(1);
    }
    
    public static class ReflectivePropertyDefinition implements PropertyDefinition {
    
        private final String name;
        
        private TypeDescriptor type;
        private Field field = null;
        private Method setter = null;
        private Method getter = null;
        private Method property = null;
        
        private boolean propertyBindingOnly = false;

        public ReflectivePropertyDefinition(String name) {
            this.name = name;
        }
        
        private boolean generic() {
            return name == null;
        }

        public void setType(TypeDescriptor type) {
            this.type = type;
        }

        public void setField(Field f) {
            assert field == null;
            field = f;
        }

        public void setProperty(Method m) {
            assert property == null;
            property = m;
        }
        
        public void setBinding(Method m) {
            assert property == null;
            property = m;
            propertyBindingOnly = true;
        }

        public void setGetter(Method m) {
            assert getter == null;
            getter = m;
        }
        
        public void setSetter(Method m) {
            assert setter == null;
            setter = m;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public PropertyAdapter createAdapter(BeanManager beanManager) {
            if (field != null) {
                TypeDescriptor t = type != null ? type : detectType(generic(), field, property, setter, getter);
                if (t != null && t.getType() == Void.class) t = null;
                return new FieldPropertyAdapter(beanManager, name, field, t);
            }
            if (property != null && !propertyBindingOnly) {
                TypeDescriptor t = type != null ? type : detectType(generic(), property, field, setter, getter);
                if (t != null && t.getType() == Void.class) t = null;
                return new PropMethodPropertyAdapter(beanManager, name, property, t, generic());
            }
            TypeDescriptor t = type != null ? type : detectType(generic(), setter, getter, field, property);
            if (t != null && t.getType() == Void.class) t = null;
            return new GetSetPropertyAdapter(beanManager, name, setter, getter, property, t, generic());
        }
        
    }
    
    public static TypeDescriptor detectType(AccessibleObject... members) {
        return detectType(false, members);
    }
    
    public static TypeDescriptor detectType(boolean genericAccess, AccessibleObject... members) {
        for (AccessibleObject a: members) {
            if (a != null) {
                PropertyType pt = a.getAnnotation(PropertyType.class);
                if (pt != null) {
                    Class actual = null;
                    if (pt.collection() == CollectionType.AUTO) actual = getActualType(a, genericAccess);
                    return TypeDescriptor.propertyType(pt.value(), pt.collection(), actual);
                }
            }
        }
        Class observableClass = null;
        for (AccessibleObject a: members) {
            Class t = getActualType(a, genericAccess);
            if (t != null) {
                if (!javafx.beans.Observable.class.isAssignableFrom(t)) {
                    return TypeDescriptor.simpleType(t);
                }
                observableClass = t;
            }
        }
        if (List.class.isAssignableFrom(observableClass)) {
            return TypeDescriptor.listType(Object.class);
        }
        return null;
    }
    
    private static Class getActualType(AccessibleObject a, boolean genericAccess) {
        if (a instanceof Field) {
            return ((Field) a).getType();
        } else if (a instanceof Method) {
            Method m = (Method) a;
            Class[] pTypes = m.getParameterTypes();
            if (pTypes.length == (genericAccess ? 2 : 1)) {
                return pTypes[genericAccess ? 1 : 0];
            } else {
                return m.getReturnType();
            }
        }
        return null;
    }
    
}
