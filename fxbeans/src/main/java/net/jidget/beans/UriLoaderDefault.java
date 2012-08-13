package net.jidget.beans;

import java.io.*;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.*;
import javafx.scene.image.Image;

/**
 *
 * @author Arian Treffer
 */
public class UriLoaderDefault {
    
    public static void initialize() {
        UriLoader.registerProperty("string", new StringFactory());
        UriLoader.registerProperty("image", new ImageFactory());
        
    }
    
    public abstract static class InputToObjectBinding<T> extends ObjectBinding<T> {
        private static final InputStream NO_INPUT = new ByteArrayInputStream(new byte[0]);
        
        protected final Property<InputStream> inputStream;
        private InputStream lastIs = NO_INPUT;
        private T lastValue = null;
        
        public InputToObjectBinding(Property<InputStream> inputStream) {
            this.inputStream = inputStream;
            bind(inputStream);
        }
        
        @Override
        protected final synchronized T computeValue() {
            final InputStream is = inputStream.getValue();
            if (is == lastIs) return lastValue;
            lastIs = is;
            if (is == null) {
                lastValue = defaultValue();
            } else {
                try {
                    lastValue = computeValue(is);
                } catch (IOException | RuntimeException e) {
                    e.printStackTrace();
                    lastValue = defaultValue();
                }
            }
            return lastValue;
        }
        
        protected abstract T computeValue(InputStream is) throws IOException;
        
        protected abstract T defaultValue();
    }
    
    public static class StringFactory implements UriLoader.PropertyFactory {

        @Override
        public Property<?> createProperty(Object owner, String key, final Property<InputStream> isProp) {
            StringProperty result = new SimpleStringProperty(owner, key, "");
            result.bind(new InputToObjectBinding<String>(isProp) {
                @Override
                protected String computeValue(InputStream is) throws IOException {
                    String s = new java.util.Scanner(is).useDelimiter("\\A").next();
                    is.close();
                    return s;
                }
                @Override
                protected String defaultValue() {
                    return "";
                }
            });
            return result;
        }
        
    }
    
    public static class ImageFactory implements UriLoader.PropertyFactory {

        @Override
        public Property<?> createProperty(Object owner, String key, final Property<InputStream> isProp) {
            ObjectProperty<Image> result = new SimpleObjectProperty<>(owner, key, null);
            result.bind(new InputToObjectBinding<Image>(isProp) {
                @Override
                protected Image computeValue(InputStream is) throws IOException {
                    Image i = new Image(is);
                    is.close();
                    return i;
                }
                @Override
                protected Image defaultValue() {
                    return null;
                }
            });
            return result;
        }
        
    }
    
}
