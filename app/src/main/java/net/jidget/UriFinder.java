package net.jidget;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import org.cthul.resolve.*;

/**
 * Is this a good idea? I don't think so.
 * 
 * @author Arian Treffer
 */
public class UriFinder extends UriMappingResolver {

    @Override
    protected RResult get(RRequest request, String source) {
        try {
            final URI uri = new URI(source);
            return new RResult(request, uri.toString()){
                @Override
                public InputStream createInputStream() throws IOException {
                    return uri.toURL().openStream();
                }
            };
        } catch (URISyntaxException e) {
            return null;
        }
    }
    
}
