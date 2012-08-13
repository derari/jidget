package net.jidget.frame.impl;

import javafx.scene.Scene;
import net.jidget.frame.Frame;

/**
 *
 * @author Arian Treffer
 */
public interface IFrameFactory {
    
    public Frame create(Scene scene);
    
}
