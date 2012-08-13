package net.jidget.frame.jfx;

import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.jidget.frame.Frame;

/**
 *
 * @author Arian Treffer
 */
public class JfxFrame implements Frame {

    final Stage stage;
    
    public JfxFrame(Scene scene) {
        stage = new Stage(StageStyle.TRANSPARENT);
        stage.setScene(scene);
        scene.setFill(null);
        stage.setWidth(600);
        stage.setHeight(600);
        stage.show();
    }
    
}
