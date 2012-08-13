package net.jidget.builder;

import javax.xml.stream.XMLStreamReader;
import net.jidget.builder.XmlProperties;
import org.xml.sax.Attributes;

/**
 *
 * @author Arian Treffer
 */
public class TmpAttributes implements Attributes {
    
    public static final Attributes EMPTY = new TmpAttributes(null, null, null);
    
    private final XMLStreamReader reader;
    private final String jidgetNs;
    private final XmlProperties properties;
    
    private int[] indices = new int[16];
    private int length = 0;

    public TmpAttributes(XMLStreamReader reader, String jidgetNs, XmlProperties properties) {
        this.reader = reader;
        this.jidgetNs = jidgetNs;
        this.properties = properties;
    }
    
    public void update() {
        final int c = reader.getAttributeCount();
        ensureIndicesLength(c);
        int index = 0;
        for (int i = 0; i < c; i++) {
            String ns = reader.getAttributeNamespace(i);
            if (ns == null || !ns.equals(jidgetNs)) {
                indices[index] = i;
                index++;
            }
        }
        length = index;
    }

    private void ensureIndicesLength(int c) {
        int len = indices.length;
        if (len < c) {
            while (len < c) len *= 2;
            indices = new int[len];
        }
    }
    
    @Override
    public int getLength() {
        return length;
    }
    
    private int privateIndex(int pubIndex) {
        return indices[pubIndex];
    }

    @Override
    public String getURI(int index) {
        return reader.getAttributeNamespace(privateIndex(index));
    }

    @Override
    public String getLocalName(int index) {
        return reader.getAttributeLocalName(privateIndex(index));
    }

    @Override
    public String getQName(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getType(int index) {
        return reader.getAttributeType(privateIndex(index));
    }

    @Override
    public String getValue(int index) {
        return properties.apply(
                reader.getAttributeValue(privateIndex(index)));
    }

    @Override
    public int getIndex(String uri, String localName) {
        final int len = length;
        for (int i = 0; i < len; i++) {
            if (localName.equals(getLocalName(i)) &&
                    uri.equals(getURI(i))) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int getIndex(String qName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getType(String uri, String localName) {
        return getType(getIndex(uri, localName));
    }

    @Override
    public String getType(String qName) {
        return getType(getIndex(qName));
    }

    @Override
    public String getValue(String uri, String localName) {
        return properties.apply(
                reader.getAttributeValue(uri, localName));
    }

    @Override
    public String getValue(String qName) {
        throw new UnsupportedOperationException();
    }
    
}
