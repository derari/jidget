package net.jidget.builder;

import java.io.File;
import java.net.URI;
import java.util.*;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import javafx.scene.Node;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import net.jidget.Jidget;
import net.jidget.app.SaveTrigger;
import net.jidget.beans.*;
import net.jidget.beans.impl.BeanUtilsImpl;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;
import static javax.xml.stream.XMLStreamConstants.*;

/**
 *
 * @author Arian Treffer
 */
class StAXJidget_1_0 extends AbstractStAXBuilder {
    
    /* Implementation notes:
     * - do not rely on a schema conform document
     * - use assert only for redundant checks
     */

    private final BeanManager beanManager;
    private final SaveTrigger save;
    private final BeanUtilsImpl beanUtils;
    private final String jidgetNs;
    private final List<Bind> bindings = new ArrayList<>();
    private final TmpAttributes tmpAttributes;    
    
    private final String jID;
    private String name = null;
    private String author = null;
    private String version = null;
    private final Map<String, Object> beans = new HashMap<>();
    private final Group group = new Group();
    
    private final XmlProperties properties = new XmlProperties();
    private final List<File> styles = new ArrayList<>();
    
    public StAXJidget_1_0(URI root, String id, URI path, BeanManager beanManager, SaveTrigger save, XMLStreamReader reader) {
        super(reader);
        this.jID = id;
        this.beanManager = beanManager;
        this.save = save;
        jidgetNs = reader.getNamespaceURI();
        tmpAttributes = new TmpAttributes(reader, jidgetNs, properties);
        beanUtils = new BeanUtilsImpl(root, path, 1000);
    }

    public Jidget build() throws XMLStreamException, BeanException {
        try {
            require(START_ELEMENT, jidgetNs, "jidget");
            while (nextTag() == START_ELEMENT) {
                if (isTag(null, "head")) {
                    head();
                    assert require(END_ELEMENT, null, "head");
                } else if (isTag(null, "body")) {
                    body();
                    assert require(END_ELEMENT, null, "body");
                } else {
                    throw new XMLStreamException("Unexpected " + reader.getLocalName());
                }
            }
            require(END_ELEMENT, jidgetNs, "jidget");
            
            applyBindings();
            return new Jidget(jID, name, author, version, beans, group, styles, beanUtils, save);
        } catch (AssertionError e) {
            throw new RuntimeException("Internal error", e);
        }
    }
    
    private void head() throws XMLStreamException, BeanException {
        require(START_ELEMENT, null, "head");
        while (nextTag() == START_ELEMENT) {
            if (isTag(null, "name")) {
                name = readCharacters();
                assert require(END_ELEMENT, null, "name");
            } else if (isTag(null, "author")) {
                author = readCharacters();
                assert require(END_ELEMENT, null, "author");
            } else if (isTag(null, "version")) {
                version = readCharacters();
                assert require(END_ELEMENT, null, "version");
            } else if (isTag(null, "properties")) {
                properties();
                assert require(END_ELEMENT, null, "properties");
            } else if (isTag(null, "styles")) {
                styles();
                assert require(END_ELEMENT, null, "styles");
            } else if (isTag(null, "beans")) {
                beans();
                assert require(END_ELEMENT, null, "beans");
            } else {
                throw new XMLStreamException("Unexpected " + reader.getLocalName());
            }
        }
        reader.require(END_ELEMENT, null, "head");
    }
    
    private void properties() throws XMLStreamException, BeanException {
        while (nextTag() == START_ELEMENT) {
            String file = reader.getAttributeValue(null, "ref");
            if (file != null) {
                String prefix = isTag(jidgetNs, "properties") ? "" : (getLocalName()+".");
                file = properties.apply(file);
                File[] files = beanUtils.collectFiles(file);
                for (File f: files) {
                    properties.load(prefix, f);
                }
                nextTag();
                //we don't know exactly what was opened
                //require(END_ELEMENT, jidgetNs, "properties");
            } else {
                String pName = getLocalName();
                StringBuilder value = new StringBuilder();
                while (next() == CHARACTERS) {
                    value.append(getCharacters());
                }
                properties.put(pName, value.toString());
            }
        }
    }
    
    private void styles() throws XMLStreamException, BeanException {
        while (nextTag() == START_ELEMENT) {
            require(START_ELEMENT, null, "style");
            String file = reader.getAttributeValue(null, "ref");
            if (file != null) {
                file = properties.apply(file);
                File[] files = beanUtils.collectFiles(file);
                styles.addAll(Arrays.asList(files));
                nextTag();
            }
            require(END_ELEMENT, null, "style");
        }
    }
    
    private void beans() throws XMLStreamException, BeanException {
        while (nextTag() != END_ELEMENT) {
            component();
        }
        require(END_ELEMENT, null, "beans");
    }
    
    private void body() throws XMLStreamException, BeanException {
        require(START_ELEMENT, null, "body");
        while (nextTag() != END_ELEMENT) {
            Object node = component();
            if (!(node instanceof Node)) {
                throw new BeanException("Component " + node + " is no Node");
            }
            group.getChildren().add((Node) node);
        }
        require(END_ELEMENT, null, "body");
    }
    
    private Object component() throws XMLStreamException, BeanException {
        require(START_ELEMENT, null, null);
        final String ns = getNamespace();
        final String tag = getLocalName();
        
        final String id = getOrGenerateId();
        reserveId(id);
        
        final BeanAdapter adapter;
        Object bean;
        if (isTag(jidgetNs, "bean")) {
            String className = reader.getAttributeValue(jidgetNs, "class");
            adapter = beanManager.getAdapter(className);
            if (adapter == null) {
                throw new BeanException("No adapter for " + className);
            }
            bean = adapter.create(className, attributes());
        } else {
            adapter = beanManager.getAdapter(ns, tag);
            if (adapter == null) {
                throw new BeanException("No adapter for " + ns + ":" + tag);
            }
            bean = adapter.create(ns, tag, attributes());
        }
        
        bean = properties(bean, adapter);
        bean = adapter.complete(bean);
        adapter.setUtils(bean, beanUtils);
        
        require(END_ELEMENT, ns, tag);
        beans.put(id, bean);
        
        return bean;
    }
    
    private Object property(Object owner, BeanAdapter adapter, int index) throws XMLStreamException, BeanException {
        require(START_ELEMENT, null, null);
        final String ns = getNamespace();
        final String tag = getLocalName();

        Object item;
        BeanAdapter itemAdapter;
        PropertyAdapter propAdapter = adapter.getProperty(owner, ns, tag);
        if (propAdapter != null) {
            item = item(propAdapter, owner, ns, tag, index);
        } else {
            propAdapter = adapter.getDefaultProperty(owner, ns, tag);
            if (propAdapter == null) {
                throw new BeanException("Unexpected property " + ns + ":" + tag +
                            " of " + owner);
            }
            item = propAdapter.getItem(owner, ns, tag, index);
            itemAdapter = propAdapter.getItemAdapter(owner, ns, tag, index);
            if (itemAdapter == null) {
                itemAdapter = beanManager.getAdapter(Void.class);
            }
            item = itemAdapter.updateOrCreate(item, null, new AttributesImpl());
            item = property(item, itemAdapter, index);
        }
        
        require(END_ELEMENT, ns, tag);
        return propAdapter.setItem(owner, ns, tag, index, item);
    }

    private Object item(PropertyAdapter propAdapter, Object owner, final String ns, final String tag, int index) throws XMLStreamException, BeanException {
        Object item;
        BeanAdapter itemAdapter;
        String ref = reader.getAttributeValue(jidgetNs, "ref");
        if (ref != null) {
            item = beans.get(ref);
            if (item == null) throw new BeanException("Unknown bean reference " + ref);
            reader.nextTag();
        } else {
            final String id = getOrGenerateId();
            if (id != null) reserveId(id);

            item = propAdapter.getItem(owner, ns, tag, index);
            itemAdapter = propAdapter.getItemAdapter(owner, ns, tag, index);
            if (itemAdapter != null) {
                item = itemAdapter.updateOrCreate(item, ns, tag, attributes());
            } else if (isTag(jidgetNs, "bean")) {
                String className = reader.getAttributeValue(jidgetNs, "class");
                itemAdapter = beanManager.getAdapter(className);
                item = itemAdapter.updateOrCreate(item, className, attributes());
            } else {
                itemAdapter = beanManager.getAdapter(ns, tag);
                if (itemAdapter == null) {
                    itemAdapter = beanManager.getAdapter(Void.class);
                }
//                if (itemAdapter == null) throw new BeanException(
//                        "No item adapter for " + ns + ":" + tag);
                item = itemAdapter.updateOrCreate(item, ns, tag, attributes());
            }

            String bind = reader.getAttributeValue(jidgetNs, "bind");
            if (bind != null && !bind.isEmpty()) {
                bind = properties.apply(bind);
                bindings.add(new Bind(propAdapter, owner, ns, tag, index, bind, false));
            }
            String set = reader.getAttributeValue(jidgetNs, "set");
            if (set != null && !set.isEmpty()) {
                set = properties.apply(set);
                bindings.add(new Bind(propAdapter, owner, ns, tag, index, set, true));
            }
            
            String styleClass = reader.getAttributeValue(jidgetNs, "style-class");
            if (styleClass != null && !styleClass.isEmpty()) {
                styleClass = properties.apply(styleClass);
                item = styleClass(item, itemAdapter, styleClass, false);
            } else {
//                item = styleClass(item, itemAdapter, true, tag, itemAdapter.getBeanTypeName());
            }
            
            item = properties(item, itemAdapter);
            item = itemAdapter.complete(item);
            itemAdapter.setUtils(item, beanUtils);
            if (id != null) beans.put(id, item);
        }
        return item;
    }
    
    private Object styleClass(Object bean, BeanAdapter adapter, String styleClass, boolean isDefaultValue) throws BeanException {
        return styleClass(bean, adapter, isDefaultValue, styleClass.split("[\\s,]+"));
    }
    
    private Object styleClass(Object bean, BeanAdapter adapter, boolean isDefaultValue, final String... styleClasses) throws BeanException {
        final String scNs = "";
        final String scTag = "styleClass";
        if (styleClasses.length == 0) return bean;
        PropertyAdapter styleProperty = adapter.getProperty(bean, scNs, scTag);
        if (styleProperty == null) {
            if (isDefaultValue) return bean;
            throw new BeanException(
                    "style-class not supported for " + adapter);
        }
        for (int i = 0; i < styleClasses.length; i++) {
            Object styleValue = styleProperty.getItem(bean, scNs, scTag, i);
            BeanAdapter styleAdapter = styleProperty.getItemAdapter(bean, scNs, scTag, i);
            String styleName = styleClasses[i].trim().replace('.', '-');
            styleAdapter.setText(styleValue, i, styleName);
            bean = styleProperty.setItem(bean, scNs, scTag, i, styleValue);
        }
        return bean;
    }
    
    /**
     * Applies sub nodes. Returns updated bean.
     * 
     * Precondition: at START_ELEMENT of bean
     * Postcondition: at STOP_ELEMENT of bean
     * @param bean
     * @param adapter
     * @return
     * @throws XMLStreamException
     */
    private Object properties(Object bean, final BeanAdapter adapter) throws XMLStreamException, BeanException {
        StringBuilder text = null;
        int index = 0;
        while (next() != END_ELEMENT) {
            switch (getEventType()) {
                case START_ELEMENT:
                    if (text != null) {
                        bean = adapter.setText(bean, index-1, text.toString());
                        text = null;
                    }
                    bean = property(bean, adapter, index);
                    break;
                case CDATA:
                case CHARACTERS:
                    if (text == null) text = new StringBuilder();
                    text.append(getCharacters());
                    break;
                case SPACE:
                    break;
                default:
                    throw new XMLStreamException("Unexpected event " + getEventType());
            }
            index++;
        }
        if (text != null) {
            String filtered = properties.apply(text.toString());
            bean = adapter.setText(bean, index-1, filtered);
        }
        return bean;
    }
    
    private void applyBindings() throws BeanException {
        for (Bind b: bindings) {
            ObservableValue<?> binding = findProperty(b.binding);
            if (b.setOnlyOnce) {
                b.property.setItem(b.owner, b.ns, b.tag, b.index, binding.getValue());
            } else {
                b.property.bind(b.owner, b.ns, b.tag, b.index, binding);
            }
        }
    }
    
    private ObservableValue<?> findProperty(String string) throws BeanException {
        final String[] parts = string.split("\\.");
        if (parts.length < 2) throw new BeanException("Invalid binding: " + string);
        Object bean = beans.get(parts[0]);
        if (bean == null) throw new BeanException("No such bean: " + parts[0]);
        
        BeanAdapter beanAdapter = beanManager.getAdapter(bean.getClass());
        PropertyAdapter propertyAdapter = null;
        for (int i = 1; i < parts.length; i++) {
            propertyAdapter = beanAdapter.getProperty(bean, null, parts[i]);
            if (propertyAdapter == null) {
                throw new BeanException("No such property: " + parts[i] + " of " + bean);
            }
            if (i+1 < parts.length) {
                beanAdapter = propertyAdapter.getItemAdapter(bean, null, parts[i], 0);
                bean = propertyAdapter.getItem(bean, null, parts[i], 0);
            }
        }
        
        int l = parts.length-1;
        
        return propertyAdapter.observe(bean, null, parts[l], 0);
    }
    
    private String readCharacters() throws XMLStreamException {
        String ns = getNamespace();
        String tag = getLocalName();
        final String text;
        if (next() == CHARACTERS) {
            text = getCharacters();
            nextTag();
        } else {
            text = null;
        }
        require(END_ELEMENT, ns, tag);
        return text;
    }
    
    private String getOrGenerateId() {
        String id = getId();
        if (id != null) {
            return id;
        } else {
            String weakId = reader.getAttributeValue(null, "id");
            if (weakId != null && !beans.containsKey(weakId)) {
                return weakId;
            }
            return generateId();
        }
    }
    
    private String getId() {
        return reader.getAttributeValue(jidgetNs, "id");
    }

    private String generateId() {
        final String rawId;
        if (isTag(jidgetNs, "bean")) {
            rawId = reader.getAttributeValue(jidgetNs, "class");
        } else {
            rawId = getLocalName();
        }
        if (!beans.containsKey(rawId)) {
            return rawId;
        } else {
            for (int i = 1; i < Integer.MAX_VALUE; i++) {
                String id = rawId + i;
                if (!beans.containsKey(id)) {
                    return id;
                }
            }
            throw new AssertionError(beans.size());
        }
    }
    
    private Attributes attributes() {
        tmpAttributes.update();
        return tmpAttributes;
    }

    private static final Object DUMMY = new Object();
    
    private void reserveId(String id) {
        beans.put(id, DUMMY);
    }

    private static class Bind {  
        final PropertyAdapter property;
        final Object owner;
        final String ns;
        final String tag;
        final int index;
        final String binding;
        final boolean setOnlyOnce;

        public Bind(PropertyAdapter property, Object owner, String ns, String tag, int index, String binding, boolean setOnlyOnce) {
            this.property = property;
            this.owner = owner;
            this.ns = ns;
            this.tag = tag;
            this.index = index;
            this.binding = binding;
            this.setOnlyOnce = setOnlyOnce;
        }

    }

}
