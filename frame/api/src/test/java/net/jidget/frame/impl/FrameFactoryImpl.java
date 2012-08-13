package net.jidget.frame.impl;

import javafx.scene.Scene;
import net.jidget.frame.Frame;
import net.jidget.frame.impl.IFrameFactory;

/**
 *
 * @author Arian Treffer
 */
public class FrameFactoryImpl implements IFrameFactory {

    @Override
    public Frame create(Scene scene) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return "Test Widget Factory";
    }
    
}
