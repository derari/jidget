package net.jidget.builder;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 *
 * @author Arian Treffer
 */
public class AbstractBuilder {
    
    protected final XMLStreamReader reader;

    public AbstractBuilder(XMLStreamReader reader) {
        this.reader = reader;
    }
    
    protected int next() throws XMLStreamException {
        return reader.next();
    }
    
    protected int nextTag() throws XMLStreamException {
        return reader.nextTag();
    }
    
    protected boolean isTag(String namespace, String name) {
        if (namespace != null && !namespace.equals(reader.getNamespaceURI())) {
            return false;
        }
        if (name != null && !name.equals(reader.getLocalName())) {
            return false;
        }
        return true;
    }
    
    protected boolean require(int eventType, String namespace, String name) throws XMLStreamException {
        reader.require(eventType, namespace, name);
        return true;
    }
    
    protected int getEventType() {
        return reader.getEventType();
    }
    
    protected String getNamespace() {
        return reader.getNamespaceURI();
    }
    
    protected String getLocalName() {
        return reader.getLocalName();
    }
    
    protected String getCharacters() {
        return reader.getText().trim();
    }
    
}
