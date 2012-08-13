package net.jidget.beans;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import net.jidget.beans.type.CollectionType;

/**
 *
 * @author Arian Treffer
 */
public class TypeDescriptor {

    private static ConcurrentMap<Class, TypeDescriptor> simpleTypes = new ConcurrentHashMap<>();
    private static ConcurrentMap<TypeDescriptor, TypeDescriptor> listTypes = new ConcurrentHashMap<>();
    
    public static TypeDescriptor simpleType(Class clazz) {
        if (clazz == null) clazz = Object.class;
        TypeDescriptor t = simpleTypes.get(clazz);
        if (t == null) {
            t = new TypeDescriptor(clazz);
            simpleTypes.put(clazz, t);
        }
        return t;
    }
    
    public static TypeDescriptor listType(Class itemClass) {
        if (itemClass == null) itemClass = Object.class;
        return listType(simpleType(itemClass));
    }
    
    public static TypeDescriptor listType(TypeDescriptor itemType) {
        if (itemType.getType() == Object.class) {
            return simpleType(List.class);
        }
        TypeDescriptor t = listTypes.get(itemType);
        if (t == null) {
            t = new TypeDescriptor(List.class, itemType);
            listTypes.put(itemType, t);
        }
        return t;
    }
    
    public static TypeDescriptor propertyType(Class pType, CollectionType cType, Class actualClazz) {
        switch (cType) {
            case NONE: return simpleType(pType);
            case LIST: return listType(pType);
            case AUTO:
                if (List.class.isAssignableFrom(actualClazz)) {
                    return listType(pType);
                } else {
                    return simpleType(pType);
                }
            default:
                throw new IllegalArgumentException(String.valueOf(cType));
        }
    }
    
    private final Class type;
    private final TypeDescriptor[] args;

    public TypeDescriptor(Class type) {
        this.type = type;
        this.args = null;
    }
    
    public TypeDescriptor(Class type, TypeDescriptor arg) {
        this.type = type;
        this.args = new TypeDescriptor[]{arg};
    }
    
    public TypeDescriptor(Class type, TypeDescriptor[] args) {
        this.type = type;
        this.args = args == null ? null : Arrays.copyOf(args, args.length);
    }

    public Class getType() {
        return type;
    }
    
    public TypeDescriptor getArg() {
        return args == null || args.length < 1 ? null : args[1];
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TypeDescriptor other = (TypeDescriptor) obj;
        if (!Objects.equals(this.type, other.type)) {
            return false;
        }
        if (!Arrays.deepEquals(this.args, other.args)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 19 * hash + Objects.hashCode(this.type);
        hash = 19 * hash + Arrays.deepHashCode(this.args);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("T ");
        toString(sb);
        return sb.toString();
    }
    
    protected void toString(StringBuilder sb) {
        sb.append(type.getName());
        if (args != null) {
            sb.append('<');
            boolean first = true;
            for (TypeDescriptor a: args) {
                if (!first) sb.append(',');
                first = false;
                a.toString(sb);
            }
            sb.append('>');
        }
    }
}
