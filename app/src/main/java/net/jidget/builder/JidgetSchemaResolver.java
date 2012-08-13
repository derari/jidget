package net.jidget.builder;

import java.util.HashMap;
import java.util.Map;
import org.cthul.xml.schema.CompositeFinder;
import org.cthul.xml.schema.ResourceFinder;
import org.cthul.xml.schema.SchemaFinder;
import org.cthul.xml.schema.SchemaResolver;

/**
 *
 * @author Arian Treffer
 */
public class JidgetSchemaResolver extends SchemaResolver {

    /**
     * Provides the W3 schemas.
     */
    public static final SchemaFinder JIDGET_SCHEMA_FINDER;

    public static final String NS_JIDGET_0_1 =
            "http://jidget.net/0.1/jidget";

    static {
        Map<String, String> netJidget = new HashMap<String, String>();
        netJidget.put(NS_JIDGET_0_1,    "/net/jidget/0.1/jidget.xsd");

        JIDGET_SCHEMA_FINDER = new ResourceFinder(JidgetSchemaResolver.class, netJidget).immutable();
    }
    
    public JidgetSchemaResolver(SchemaFinder finder) {
        super(JIDGET_SCHEMA_FINDER, ORG_W3_SCHEMA_FINDER, finder);
    }
    
}
