package net.jidget.frame.awt;

import javafx.scene.Scene;
import javax.swing.SwingUtilities;
import net.jidget.frame.Frame;

/**
 *
 * @author Arian Treffer
 */
public class AwtFrame implements Frame {

    private boolean active = true;
    private JidgetJFrame frame = null;
    
    private double tmpX = -1, tmpY = -1;

    public AwtFrame(final Scene scene) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                initFrame(scene);
            }
        });
    }
    
    private synchronized void initFrame(Scene scene) {
        if (!active) return;
        frame = new JidgetJFrame(scene);
        frame.setVisible(true);
        frame.toBack();
        if (tmpX > -1 && tmpY > -1) {
            setLocation(tmpX, tmpY);
        }
    }

    @Override
    public synchronized double getX() {
        if (frame == null) return tmpX;
        return frame.getX();
    }

    @Override
    public double getY() {
        if (frame == null) return tmpY;
        return frame.getY();
    }

    @Override
    public synchronized void setLocation(double x, double y) {
        if (frame == null) {
            tmpX = x;
            tmpY = y;
        } else {
            frame.setLocation((int) x, (int) y);
        }
    }

    @Override
    public synchronized void close() {
        active = false;
        if (frame != null) {
            frame.dispose();
        }
    }
}
