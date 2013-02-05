package net.jidget.frame;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import javafx.scene.Scene;
import net.jidget.frame.impl.IFrameFactory;
import org.cthul.log.CLogger;
import org.cthul.log.CLoggerFactory;

/**
 * Created frames.
 * The implementation is provided by 
 * {@code net.jidget.widget.impl.FrameFactoryImpl}.
 * 
 * @author Arian Treffer
 */
public abstract class FrameFactory {
    
    static final CLogger log = CLoggerFactory.getClassLogger();
    
    public static Frame create(Scene scene) {
        return getInstance().create(scene);
    }
    
    private static IFrameFactory instance = null;
    
    public static synchronized IFrameFactory getInstance() {
        if (instance == null) {
            instance = getInstance("net.jidget.frame.impl.FrameFactoryImpl");
            log.info("Frame Factory: %s", instance);
        }
        return instance;
    }
    
    static IFrameFactory getInstance(String className) {
        Throwable t;
        try {
            final Class<IFrameFactory> clazz = (Class<IFrameFactory>) Class.forName(className);
            final Constructor<IFrameFactory> constructor = clazz.getConstructor();
            return constructor.newInstance();
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            t = e;
        }
        log.error(t, "Initializing FrameFactory failed");
        throw new RuntimeException("Initializing FrameFactory failed", t);
    }
    
}
