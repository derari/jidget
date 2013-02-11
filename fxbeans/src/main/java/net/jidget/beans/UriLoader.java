package net.jidget.beans;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.*;
import net.jidget.beans.type.Parameters;
import net.jidget.utils.Interval;
import net.jidget.utils.UpdateSignal;
import org.cthul.log.CLoggerFactory;

/**
 *
 * @author Arian Treffer
 */
public class UriLoader implements BeanWithUtils {
    
    private static final Map<String, PropertyFactory> factories = new HashMap<>();
    
    public static synchronized void registerProperty(String name, PropertyFactory factory) {
        factories.put(name, factory);
    }
    
    protected static synchronized PropertyFactory getFactory(String name) {
        return factories.get(name);
    }

    public static interface PropertyFactory {
        
        public Property<?> createProperty(Object owner, String key, Property<InputStream> is);
        
    }
    
    private final UpdateSignal update = new UpdateSignal(this);
    private final StringProperty uri = new SimpleStringProperty(this, "uri", null);
    private final ObjectProperty<InputStream> is = new SimpleObjectProperty<>(this, "inputstream", null);
    private InputStream lastIs = null;
    private String resultPropName = null;
    private Property<?> resultProperty = null;
    private final int interval;
    private BeanUtils beanUtils = null;

    {
        is.bind(new ObjectBinding<InputStream>() {
            {bind(uri, update);}
            @Override
            protected InputStream computeValue() {
                return openURI();
            }
        });
    }

    @Parameters({"uri", "update"})
    public UriLoader(String uri, Interval interval) {
        this(interval);
        this.uri.set(uri);
    }

    @Parameters({"uri"})
    public UriLoader(String uri) {
        this(uri, Interval.NONE);
    }

    @Parameters({"update"})
    public UriLoader(Interval interval) {
        this.interval = interval.getMilliseconds();
    }

    public UriLoader() {
        this(Interval.NONE);
    }

    public StringProperty uriProperty() {
        return uri;
    }
    
    public Property<?> property(String key) {
        if (resultPropName == null) {
            resultPropName = key;
            resultProperty = createResult(key);
        }
        if (!resultPropName.equals(key)) {
            throw new IllegalStateException("Loading URI as " + resultPropName + ", not " + key);
        }
        return resultProperty;
    }
    
    private Property<?> createResult(String key) {
        PropertyFactory f = getFactory(key);
        if (f == null) throw new IllegalArgumentException("Unsupported result type: " + key);
        return f.createProperty(this, key, is);
    }    
    
    @Override
    public void setUtils(BeanUtils beanUtils) {
        this.beanUtils = beanUtils;
        update.trigger();
        if (interval < 0) return;
        beanUtils.getTaskRegistry().addTask(update, interval);
    }
    
    protected synchronized InputStream openURI() {
        try {
            if (lastIs != null) lastIs.close();
        } catch (IOException | RuntimeException e) {}
        if (beanUtils == null) return null;
        String uriStr = uri.get();
        try {
            if (uriStr == null) return null;
            URI resolvedUri = beanUtils.resolve(uriStr);
            lastIs = resolvedUri.toURL().openStream();
            return lastIs;
        } catch (IOException | RuntimeException e) {
            CLoggerFactory.getClassLogger().warn("Could not load %s: %s", uriStr, e.getMessage());
            return null;
        }
    }

}
