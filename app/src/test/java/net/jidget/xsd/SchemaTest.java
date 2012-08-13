package net.jidget.xsd;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import javax.xml.XMLConstants;
import javax.xml.stream.*;
import javax.xml.transform.stax.StAXResult;
import javax.xml.transform.stax.StAXSource;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import net.jidget.builder.JidgetSchemaResolver;
import net.jidget.builder.UriFinder;
import org.cthul.xml.schema.SchemaResolver;
import org.cthul.xml.validation.ValidatingXMLInputFactory;
import org.junit.Test;

/**
 *
 * @author Arian Treffer
 */
public class SchemaTest {
    
    @Test
    public void run() throws Exception {
//        SchemaResolver resolver = new JidgetSchemaResolver(new UriFinder());
//        SchemaFactory schemaFactory = SchemaFactory.newInstance(
//                                    XMLConstants.W3C_XML_SCHEMA_NS_URI);
//        schemaFactory.setResourceResolver(resolver);
//        Validator v = schemaFactory.newSchema().newValidator();
//        v.setResourceResolver(schemaFactory.getResourceResolver());
//
//        File fIn = new File("src/test/resources/jidget1.xml");
//        XMLInputFactory xmlif = XMLInputFactory.newInstance();
//        XMLStreamReader r = xmlif.createXMLStreamReader(fIn.getCanonicalPath(), new FileInputStream(fIn));
//        
//        File fOut = new File("src/test/resources/jidget1_out.xml");
//        XMLOutputFactory xmlof = XMLOutputFactory.newInstance();
//        XMLStreamWriter w = xmlof.createXMLStreamWriter(new FileOutputStream(fOut));
//        
//        v.validate(new StAXSource(r), new StAXResult(w));
//        w.flush();
//        w.close();
//        r.close();
//        
//        ValidatingXMLInputFactory vif = new ValidatingXMLInputFactory(resolver);
//        vif.createXMLStreamReader(fIn.getCanonicalPath(), new FileInputStream(fIn)).next();

    }
    
}
