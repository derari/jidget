package net.jidget;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import org.cthul.xml.schema.SchemaFinder;

/**
 * Is this a good idea? I don't think so.
 * 
 * @author Arian Treffer
 */
public class UriFinder implements SchemaFinder {

    @Override
    public InputStream find(String uri) {
        try {
            return new URI(uri).toURL().openStream();
        } catch (URISyntaxException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
    }
    
}
