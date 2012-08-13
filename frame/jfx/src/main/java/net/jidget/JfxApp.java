package net.jidget;


import javafx.application.Application;
import javafx.stage.Stage;

/**
 *
 * @author Arian Treffer
 */
public class JfxApp extends Application {

    private static boolean started = false;
    
    public static synchronized void start() {
        if (started) return;
        started = true;
        Application.launch(JfxApp.class, new String[0]);
    }

    @Override
    public void start(Stage stage) throws Exception {
        
    }
    
}
