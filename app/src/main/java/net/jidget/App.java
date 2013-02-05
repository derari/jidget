package net.jidget;

import java.io.File;
import java.util.*;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.stage.Stage;
import net.jidget.app.JidgetManager;
import net.jidget.app.JidgetSelection;
import net.jidget.beans.impl.BeanUtilsImpl;
import org.cthul.log.CLogger;
import org.cthul.log.CLoggerFactory;

public class App extends Application {
    
    //static NativeWidget w;
    
    public static void main(String[] args) throws Exception {
        if (args.length == 0) args = new String[]{"./src/test/example"};
        
        ClassLoader loader = App.class.getClassLoader();
        System.out.println(loader.getResource("org/cthul/log/CLoggerFactory.class"));
        
        final CLogger log = CLoggerFactory.getClassLogger();
        log.info("java.library.path=%s", System.getProperty("java.library.path"));
        Application.launch(App.class, args);
        log.info("End");
    }
    
    private File root, config;
    private Jidget j;

    public App() {
    }

    @Override
    public void init() throws Exception {
        List<String> args = new ArrayList<>(getParameters().getRaw());
        if (args.isEmpty()) args.add("./src/test/example");
        
        root = new File(args.get(0));
        config = args.size() > 1 ? new File(args.get(1)) : new File(root, "config.dat");

    }
    
    @Override
    public void start(Stage stage) throws Exception {
//        this.stage = stage;
        
        // dummy jidget to prevent javafx from closing
        Group g = new Group();
        j = new Jidget("", "", "", "", new HashMap<String, Object>(), g, 
                new ArrayList<File>(), new BeanUtilsImpl(null, null, 1), null);
        
        //stage.setVisible(true);
        //w = (NativeWidget)
        JidgetManager jm = new JidgetManager(root, config);
        JidgetSelection.Show(jm);
//        
//        
//        Circle c = new Circle(50);
//        c.setTranslateX(50);
//        c.setTranslateY(50);
//        
//        Line h = new Line(50, 50, 50, 80);
//        h.setFill(Color.WHITE);
//        
//        g = new Group(c, h);
//        Scene scene = new Scene(g);
//        stage.setScene(scene);
//        stage.show();
    }

    @Override
    public void stop() throws Exception {
        j.close();
        final CLogger log = CLoggerFactory.getClassLogger();
        log.warn("stopped");
    }

}
