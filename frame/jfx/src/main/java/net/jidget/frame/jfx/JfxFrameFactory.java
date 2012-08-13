package net.jidget.frame.jfx;

import javafx.scene.Scene;
import net.jidget.frame.impl.IFrameFactory;

/**
 *
 * @author Arian Treffer
 */
public class JfxFrameFactory implements IFrameFactory {
    
    @Override
    public JfxFrame create(Scene scene) {
        return new JfxFrame(scene);
    }

    @Override
    public String toString() {
        return "JFX Frame Factory";
    }
    
}