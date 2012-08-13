package net.jidget.beans.type.factory;

import org.xml.sax.Attributes;

/**
 * A {@link Signature} derived from XML tag attributes.
 * @author Arian Treffer
 */
public class AttributesSignature extends Signature {
    
    public static Signature get(Attributes attributes) {
        if (attributes == null || attributes.getLength() == 0) {
            return Signature.EMPTY;
        } else {
            return new AttributesSignature(attributes);
        }
    }
    
    protected static String[] collectNamespaces(Attributes attributes) {
        final int len = attributes.getLength();
        final String[] result = new String[len];
        for (int i = 0; i < len; i++) {
            String ns = attributes.getURI(i);
            if (ns != null && !ns.isEmpty()) {
                result[i] = ns;
            }
        }
        return result;
    }
    
    protected static String[] collectNames(Attributes attributes) {
        final int len = attributes.getLength();
        final String[] result = new String[len];
        for (int i = 0; i < len; i++) {
            result[i] = attributes.getLocalName(i);
        }
        return result;
    }

    protected AttributesSignature(Attributes attributes) {
        super(collectNamespaces(attributes), collectNames(attributes));
    }

    protected AttributesSignature(String[] namespaces, String[] names) {
        super(namespaces, names);
    }
    
}
