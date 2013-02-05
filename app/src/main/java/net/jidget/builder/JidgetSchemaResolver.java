package net.jidget.builder;

import org.cthul.xml.OrgW3Resolver;
import java.util.HashMap;
import java.util.Map;
import org.cthul.resolve.*;
import org.cthul.xml.CLSResourceResolver;

/**
 *
 * @author Arian Treffer
 */
public class JidgetSchemaResolver extends CLSResourceResolver {

    /**
     * Provides the W3 schemas.
     */
    public static final ResourceResolver JIDGET_SCHEMA_FINDER;

    public static final String NS_JIDGET_0_1 =
            "http://jidget.net/0.1/jidget";

    static {
        Map<String, String> netJidget = new HashMap<>();
        netJidget.put(NS_JIDGET_0_1,    "/net/jidget/0.1/jidget.xsd");

        JIDGET_SCHEMA_FINDER = new ClassResourceResolver(JidgetSchemaResolver.class, netJidget).immutable();
    }
    
    public JidgetSchemaResolver(ResourceResolver finder) {
        super(JIDGET_SCHEMA_FINDER, OrgW3Resolver.INSTANCE, finder);
    }
    
}
