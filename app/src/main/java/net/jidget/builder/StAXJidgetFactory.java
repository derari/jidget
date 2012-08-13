package net.jidget.builder;

import java.io.*;
import java.net.URI;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import net.jidget.Jidget;
import net.jidget.app.SaveTrigger;
import net.jidget.beans.BeanException;
import net.jidget.beans.BeanManager;
import org.cthul.xml.validation.ValidatingXMLInputFactory;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.SAXException;
import static javax.xml.stream.XMLStreamConstants.*;

/**
 *
 * @author Arian Treffer
 */
public class StAXJidgetFactory {
    
    private final SaveTrigger save;
    private final BeanManager beanManager;
    private final XMLStreamReader reader;
    private final String id;
    private final URI root;
    private final URI path;

    public StAXJidgetFactory(URI root, String id, BeanManager beanManager, SaveTrigger save, File file, LSResourceResolver resolver) throws IOException, SAXException, XMLStreamException {
        this(root, id, file.getParentFile().toURI(), beanManager, save, reader(file, resolver));
    }
    
    private static XMLStreamReader reader(File file, LSResourceResolver resolver) throws FileNotFoundException, IOException, SAXException, XMLStreamException {
        ValidatingXMLInputFactory f = new ValidatingXMLInputFactory(resolver);
        InputStream is = new BufferedInputStream(new FileInputStream(file));
        String sysId = file.getCanonicalPath();
        return f.createXMLStreamReader(sysId, is);
    }

    public StAXJidgetFactory(URI root, String id, URI path, BeanManager beanManager, SaveTrigger save, XMLStreamReader reader) {
        this.id = id;
        this.beanManager = beanManager;
        this.reader = reader;
        this.save = save;
        this.root = root;
        this.path = path;
    }
    
    public Jidget build() throws XMLStreamException, BeanException {
        reader.require(START_DOCUMENT, null, null);
        
        reader.nextTag();
        reader.require(START_ELEMENT, null, "jidget");
        
        final Jidget jidget;
        
        String version = reader.getAttributeValue(null, "version");
        if ("1.0".equals(version)) {
            jidget = new StAXJidget_1_0(root, id, path, beanManager, save, reader).build();
        } else {
            throw new XMLStreamException("Invalid version " + version);
        }
        reader.require(END_ELEMENT, null, "jidget");
        reader.next();
        reader.require(END_DOCUMENT, null, null);
        return jidget;
        
    }
    
}
