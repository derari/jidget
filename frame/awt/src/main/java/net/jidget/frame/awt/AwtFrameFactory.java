package net.jidget.frame.awt;

import javafx.scene.Scene;
import net.jidget.frame.Frame;
import net.jidget.frame.impl.IFrameFactory;

/**
 *
 * @author Arian Treffer
 */
public class AwtFrameFactory implements IFrameFactory {

    @Override
    public Frame create(Scene scene) {
        return new AwtFrame(scene);
    }

    @Override
    public String toString() {
        return "AWT Widget Factory";
    }
    
}
