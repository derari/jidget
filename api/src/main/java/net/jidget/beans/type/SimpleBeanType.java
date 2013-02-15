package net.jidget.beans.type;

import java.lang.reflect.*;
import java.util.*;
import javafx.util.Builder;
import net.jidget.beans.*;
import net.jidget.beans.adapter.SimpleBeanAdapter;
import net.jidget.beans.type.factory.*;

/**
 *
 * @author Arian Treffer
 */
public class SimpleBeanType {
    
    protected final TypeDescriptor type;
    protected final List<FactoryMethodDefinition> factoryMethods = new ArrayList<>();
    protected final Map<String, List<String>> tags = new HashMap<>();
    protected final Map<String, PropertyDefinition> properties = new HashMap<>();
    protected String defaultPropertyName; // references an entry in `properties`
    protected PropertyDefinition defaultProperty;
    protected boolean defaultIsGeneric = false;
    private FactoryMethodDefinition builder;

    public SimpleBeanType(TypeDescriptor clazz) {
        this.type = clazz;
    }
    
    @SuppressWarnings("OverridableMethodCallInConstructor")
    public SimpleBeanType(TypeDescriptor clazz, String ns, String tag) {
        this(clazz);
        tag(ns, tag);
    }
    
    public SimpleBeanType(Class clazz) {
        this(TypeDescriptor.simpleType(clazz));
    }
    
    @SuppressWarnings("OverridableMethodCallInConstructor")
    public SimpleBeanType(Class clazz, String ns, String tag) {
        this(TypeDescriptor.simpleType(clazz), ns, tag);
    }
    
    protected Class implClass() {
        return type.getType();
    }
    
    protected TypeDescriptor beanClass() {
        return type;
    }

    protected void scanConstructors(Class<?> clazz) {
        for (Constructor c: clazz.getConstructors()) {
            Class[] paramTypes = c.getParameterTypes();
            Parameters params = (Parameters) c.getAnnotation(Parameters.class);
            if (isPublic(c) && (paramTypes.length == 0 || params != null)) {
                add(new ConstructorFactoryMethod.Definition(c, params));
            }
        }
    }
    
    protected void scanFactories(Class<?> clazz) {
        for (Method m: clazz.getDeclaredMethods()) {
            Parameters params = m.getAnnotation(Parameters.class);
            if (params != null & isPublic(m) & isStatic(m)) {
                add(new InvokingFactoryMethod.Definition(m, null, params));
            }
        }
    }
    
    protected void scanProperties(Class<?> clazz) {
        ReflectivePropertyScanner scanner = new ReflectivePropertyScanner(clazz);
        scanner.scan();
        
        String defName = scanner.getDefaultName();
        if (defName != null) defaultProperty(defName);
        defaultProperty = scanner.getGenericProperty();
        defaultIsGeneric = defaultProperty != null;
        
        for (PropertyDefinition p: scanner.getPropertyList()) {
            add(p);
        }
    }
    
    private static boolean isPublic(Member m) {
        return (m.getModifiers() & Modifier.PUBLIC) != 0;
    }
    
    private static boolean isStatic(Member m) {
        return (m.getModifiers() & Modifier.STATIC) != 0;
    }
    
    public List<String> registeredNamesForNamespace(String namespaceURI) {
        List<String> names = tags.get(namespaceURI);
        if (names == null) {
            names = new ArrayList<>();
            tags.put(namespaceURI, names);
        }
        return names;
    }
    
    public void tag(String name) {
        tag("", name);
    }
    
    public void tag(String namespaceURI, String name) {
        registeredNamesForNamespace(namespaceURI).add(name);
    }
    
    public void setBuilder(FactoryMethodDefinition builder) {
        this.builder = builder;
    }
    
    public void add(FactoryMethodDefinition factoryMethod) {
        factoryMethods.add(factoryMethod);
    }
    
    public void add(ConstructorDataBuilder constructorData) {
        add(constructorData.def);
    }
    
    public void add(MethodDataBuilder methodData) {
        add(methodData.def);
    }
    
    public void add(PropertyDataBuilder prop) {
        add(prop.def);
    }
    
    public void add(PropertyDefinition prop) {
        if (prop.getName() == null) {
            defaultProperty(prop);
        } else {
            properties.put(prop.getName(), prop);
        }
    }
    
    public void defaultProperty(String name) {
        defaultPropertyName = name;
        defaultProperty = null;
        defaultIsGeneric = false;
    }

    public void defaultProperty(PropertyDefinition prop) {
        defaultPropertyName = null;
        defaultProperty = prop;
        defaultIsGeneric = prop.getName() == null;
    }
    
    public static ConstructorDataBuilder Constructor(Class clazz) {
        return new ConstructorDataBuilder(clazz).as();
    }
    
    public static ConstructorDataBuilder Constructor(Class clazz, Class... parameters) {
        return new ConstructorDataBuilder(clazz, parameters);
    }
    
    public ConstructorDataBuilder constructor() {
        ConstructorDataBuilder c = Constructor(implClass());
        add(c);
        return c;
    }
    
    public ConstructorDataBuilder constructor(Class... parameters) {
        ConstructorDataBuilder c = Constructor(implClass(), parameters);
        add(c);
        return c;
    }
    
    public ConstructorDataBuilder implementation(Class impl, Class... parameters) {
        ConstructorDataBuilder c = Constructor(impl, parameters);
        add(c);
        return c;
    }
    
    public static MethodDataBuilder Factory(Class clazz, String name, Class... parameters) {
        return new MethodDataBuilder(clazz, name, parameters);
    }
    
    public static MethodDataBuilder Factory(Object factory, String name, Class... parameters) {
        return new MethodDataBuilder(factory, name, parameters);
    }
    
    public MethodDataBuilder factory(String name, Class... parameters) {
        MethodDataBuilder m = Factory(implClass(), name, parameters);
        add(m);
        return m;
    }
    
    public MethodDataBuilder factory(Class clazz, String name, Class... parameters) {
        MethodDataBuilder m = Factory(clazz, name, parameters);
        add(m);
        return m;
    }
    
    public MethodDataBuilder factory(Object factory, String name, Class... parameters) {
        MethodDataBuilder m = Factory(factory, name, parameters);
        add(m);
        return m;
    }
    
    public static BuilderDataBuilder Builder(Class<? extends Builder> clazz) {
        return new BuilderDataBuilder(clazz);
    }
    
    public BuilderDataBuilder builder(Class<? extends Builder> clazz) {
        BuilderDataBuilder b = Builder(clazz);
        setBuilder(b.def);
        return b;
    }
    
    public static PropertyDataBuilder Property(Class clazz, String name) {
        return new PropertyDataBuilder(clazz, name);
    }
    
    public PropertyDataBuilder property(String name) {
        PropertyDataBuilder p = Property(implClass(), name);
        add(p);
        return p;
    }
    
    public static PropertyDataBuilder GenericProperty(Class clazz) {
        return Property(clazz, null);
    }
    
    public PropertyDataBuilder genericProperty() {
        PropertyDataBuilder p = GenericProperty(implClass());
        defaultProperty(p.def);
        return p;
    }
    
    protected BeanFactory createFactory(BeanManager bm) throws BeanException {
        if (factoryMethods.isEmpty() && builder == null) {
            return new NoArgsFactory(implClass(), false);
        } else {
            List<FactoryMethod> methods = new ArrayList<>(factoryMethods.size());
            for (FactoryMethodDefinition d: factoryMethods) {
                methods.add(d.create(bm));
            }
            FactoryMethod builderMethod = builder != null ? builder.create(bm) : null;
            return new SimpleFactory(implClass(), methods, builderMethod);
        }
    }
    
    protected BeanAdapter createAdapter(BeanManager bm) throws BeanException {
        Map<String, PropertyAdapter> pAdapters = createPropertyAdapters(bm);
        PropertyAdapter defaultPA = getDefaultPropAdapter(pAdapters, bm);
        BeanFactory bf = createFactory(bm);
        return createAdapter(bm, bf, pAdapters, defaultPA);
    }
    
    protected String adapterName() {
        return type.getType().getName();
    }

    protected BeanAdapter createAdapter(BeanManager bm, BeanFactory bf, Map<String, PropertyAdapter> pAdapters, PropertyAdapter defaultPA) {
        return new SimpleBeanAdapter(adapterName(), bm, bf, pAdapters, defaultPA, defaultIsGeneric);
    }
    
    public void registerTo(BeanManager beanManager) throws BeanException {
        final BeanAdapter adapter = createAdapter(beanManager);
        beanManager.registerAdapter(beanClass(), adapter);
        for (Map.Entry<String, List<String>> e: tags.entrySet()) {
            String ns = e.getKey();
            for (String name: e.getValue()) {
                beanManager.registerAdapter(ns, name, adapter);
            }
        }
    }

    protected Map<String, PropertyAdapter> createPropertyAdapters(BeanManager bm) {
        Map<String, PropertyAdapter> map = new HashMap<>();
        for (PropertyDefinition pd: properties.values()) {
            map.put(pd.getName(), pd.createAdapter(bm));
        }
        return map;
    }

    protected PropertyAdapter getDefaultPropAdapter(Map<String, PropertyAdapter> map, BeanManager bm) {
        if (defaultPropertyName != null) {
            return map.get(defaultPropertyName);
        }
        if (defaultProperty != null) {
            return defaultProperty.createAdapter(bm);
        }
        return null;
    }

    public static final class ConstructorDataBuilder {
        
        private final ConstructorFactoryMethod.Definition def = new ConstructorFactoryMethod.Definition();

        public ConstructorDataBuilder(Constructor<?> constructor) {
            def.setConstructor(constructor);
        }

        public ConstructorDataBuilder(Class<?> clazz, Class<?>... types) {
            try {
                Constructor constructor = clazz.getConstructor(types);
                if ((constructor.getModifiers() & Modifier.PUBLIC) == 0) {
                    throw new IllegalArgumentException(constructor + " is not public");
                }
                def.setConstructor(constructor);
            } catch (NoSuchMethodException e) {
                throw new IllegalArgumentException(e);
            }
        }
        
        public ConstructorDataBuilder as(String... names) {
            def.setParameters(new ParameterNames(names));
            return this;
        }
        
    }
    
    public static final class MethodDataBuilder {
        
        private final InvokingFactoryMethod.Definition def = new InvokingFactoryMethod.Definition();

        public MethodDataBuilder(Method method, Object factory) {
            def.setMethod(method);
            def.setFactory(factory);
        }
        
        public MethodDataBuilder(Class<?> clazz, String name, Class<?>... types) {
            this(clazz, null, name, types);
        }
        
        public MethodDataBuilder(Object factory, String name, Class<?>... types) {
            this(factory.getClass(), factory, name, types);
        }

        private MethodDataBuilder(Class<?> clazz, Object factory, String name, Class<?>... types) {
            try {
                Method method = clazz.getMethod(name, types);
                if ((method.getModifiers() & Modifier.PUBLIC) == 0) {
                    throw new IllegalArgumentException(method + " is not public");
                }
                def.setFactory(factory);
                def.setMethod(method);
            } catch (NoSuchMethodException e) {
                throw new IllegalArgumentException(e);
            }
        }
        
        public MethodDataBuilder as(String... names) {
            def.setParams(new ParameterNames(names));
            return this;
        }
        
    }
    
    public static class BuilderDataBuilder {
        
        private BuilderFactoryMethod.Definition def = new BuilderFactoryMethod.Definition();

        public BuilderDataBuilder(Class<? extends Builder> builderClazz) {
            def.setClazz(builderClazz);
        }
        
        public static BuilderDataBuilder forBuilderBeanType(BuilderDataBuilder b) {
            b.def.setBuildResult(false);
            return b;
        }
        
    }
    
    public static final Class[] ANY_ARGS = null;
    public static final Class[] NO_ARGS = new Class[0];
    
    public static final class PropertyDataBuilder {
        
        private final ReflectivePropertyScanner.ReflectivePropertyDefinition def;
        private final Class clazz;

        public PropertyDataBuilder(Class clazz, String name) {
            def = new ReflectivePropertyScanner.ReflectivePropertyDefinition(name);
            this.clazz = clazz;
        }
        
        public PropertyDataBuilder type(TypeDescriptor type) {
            def.setType(type);
            return this;
        }
        
        public PropertyDataBuilder type(Class type) {
            if (type == null) {
                type = Void.class;
            }
            return type(TypeDescriptor.simpleType(type));
        }
        
        public PropertyDataBuilder noType() {
            return type((Class) null);
        }
        
        public PropertyDataBuilder listOf(Class type) {
            return type(TypeDescriptor.listType(type));
        }
        
        private Method getMethod(Class clazz, String name, Class[] args) {
            try {
                if (args != null) {
                    return clazz.getMethod(name, args);
                } else {
                    Method result = null;
                    for (Method m: clazz.getMethods()) {
                        if (m.getName().equals(name)) {
                            if (result != null) {
                                throw new IllegalArgumentException(
                                        "Ambigous method name, " + 
                                        m + " or " + result);
                            }
                            result = m;
                        }
                    }
                    if (result != null) {
                        return result;
                    }
                    throw new IllegalArgumentException("No such method: " + name);
                }
            } catch (NoSuchMethodException e) {
                throw new IllegalArgumentException(e);
            }
        }
        
        public PropertyDataBuilder setter(String name) {
            return setter(clazz, name, ANY_ARGS);
        }
        
        public PropertyDataBuilder setter(String name, Class... args) {
            return setter(clazz, name, args);
        }
        
        public PropertyDataBuilder getter(String name) {
            return getter(clazz, name, ANY_ARGS);
        }
        
        public PropertyDataBuilder property(String name) {
            return property(clazz, name, ANY_ARGS);
        }
        
        public PropertyDataBuilder binding(String name) {
            return binding(clazz, name, ANY_ARGS);
        }
        
        public PropertyDataBuilder setter(Class clazz, String name, Class... args) {
            def.setSetter(getMethod(clazz, name, args));
            return this;
        }
        
        public PropertyDataBuilder getter(Class clazz, String name, Class... args) {
            def.setGetter(getMethod(clazz, name, args));
            return this;
        }
        
        public PropertyDataBuilder property(Class clazz, String name, Class... args) {
            def.setProperty(getMethod(clazz, name, args));
            return this;
        }
        
        public PropertyDataBuilder binding(Class clazz, String name, Class... args) {
            def.setBinding(getMethod(clazz, name, args));
            return this;
        }
        
    }
}
