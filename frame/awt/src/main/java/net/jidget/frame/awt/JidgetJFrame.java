package net.jidget.frame.awt;

import java.awt.*;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 *
 * @author Arian Treffer
 */
public class JidgetJFrame extends JFrame {

    private static final long serialVersionUID = 5599663217924997904L;

    private static final GraphicsConfiguration gc;
    
    static {
        AwtUtilitiesWrapper.init();
        GraphicsEnvironment env =
                GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] devices = env.getScreenDevices();

        GraphicsConfiguration translucencyCapableGC = null;

        for (int i = 0; i < devices.length && translucencyCapableGC == null; i++) {
            GraphicsConfiguration[] configs = devices[i].getConfigurations();
            for (int j = 0; j < configs.length && translucencyCapableGC == null; j++) {
                if (AwtUtilitiesWrapper.isTranslucencyCapable(configs[j])) {
                    translucencyCapableGC = configs[j];
                }
            }
        }        
        gc = translucencyCapableGC;
    }

    private final Scene scene;
    
    @SuppressWarnings("LeakingThisInConstructor")
    public JidgetJFrame(Scene scene) {
        super(gc);
        this.scene = scene;
        setUndecorated(true);
//        setBackground(new Color(0,255,0,100));
        //setSize(new Dimension(600, 600));
        setLocationRelativeTo(null);
        AwtUtilitiesWrapper.setWindowOpaque(this, false);
        
        JFXPanel jfx = new JFXPanel();
        jfx.setScene(scene);
        
        //JPanel jPanel1 = new javax.swing.JPanel();
        //jPanel1.setBackground(new Color(0, 0, 0, 0));        
        //jPanel1.add(jfx);
        //setContentPane(jPanel1);
        setContentPane(jfx);
        
//        scene.setFill(javafx.scene.paint.Color.color(0, 0, 1, 0.01));
        
        setFocusable(false);
        setFocusableWindowState(false);
        toBack();
        
        SizeListener sizeListener = new SizeListener();
        scene.widthProperty().addListener(sizeListener);
        scene.heightProperty().addListener(sizeListener);
        sizeListener.invalidated(null);
    }

    @Override
    public void toFront() {
    }

    private class SizeListener implements InvalidationListener, Runnable {
        
        private boolean updatePending = false;

        @Override
        public void invalidated(Observable arg0) {
            synchronized (this) {
                if (updatePending) return;
                updatePending = true;
            }
            SwingUtilities.invokeLater(this);
        }

        @Override
        public synchronized void run() {
            updatePending = false;
//            System.out.println("Setting size " + scene.getWidth() + " " + scene.getHeight());
            setSize((int) scene.getWidth(), (int) scene.getHeight());
        }
    
    }
    
}
