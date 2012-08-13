package net.jidget.app;

import java.io.File;
import java.io.IOException;
import java.util.*;
import javax.xml.stream.XMLStreamException;
import net.jidget.Jidget;
import net.jidget.Plugin;
import net.jidget.beans.BeanException;
import net.jidget.beans.BeanManager;
import net.jidget.builder.JidgetSchemaResolver;
import net.jidget.builder.StAXJidgetFactory;
import org.cthul.log.CLogger;
import org.cthul.log.CLoggerFactory;
import org.cthul.xml.schema.FileFinder;
import org.cthul.xml.schema.SchemaResolver;
import org.xml.sax.SAXException;

/**
 *
 * @author Arian Treffer
 */
public class JidgetFactory {
    
    static CLogger log = CLoggerFactory.getClasslogger();

    private final SaveTrigger save;
    private final BeanManager beanManager;
    private final SchemaResolver resolver;

    public JidgetFactory(SaveTrigger save) throws BeanException {
        this.save = save;
        beanManager = new BeanManager();
        loadPlugins();
        resolver = new JidgetSchemaResolver(new FileFinder());
    }

    private void loadPlugins() {
        ServiceLoader<Plugin> pluginLoader = ServiceLoader.load(Plugin.class);
        Iterator<Plugin> pluginIt = pluginLoader.iterator();
        boolean retry = true;
        while (retry) {
            Plugin p = null;
            try {
                while (pluginIt.hasNext()) {
                    p = pluginIt.next();
                    p.initialize(beanManager);
                    log.trace("Registered plugin %s", p);
                    p = null; // is used if hasNext() fails
                }
                retry = false;
            } catch (ServiceConfigurationError | BeanException | RuntimeException e) {
                log.error(e,
                        p == null ? "Fetching plugins" : "Initializing %s",
                        p);
            }
        }
    }
    
    public Jidget create(File rootDir, String id, File file) throws IOException, SAXException, XMLStreamException, BeanException {
        StAXJidgetFactory factory = new StAXJidgetFactory(rootDir.toURI(), id, beanManager, save, file, resolver);
        return factory.build();
    }
}
