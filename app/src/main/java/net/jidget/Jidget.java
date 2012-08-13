package net.jidget;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import net.jidget.app.JidgetMemento;
import net.jidget.app.SaveTrigger;
import net.jidget.beans.impl.BeanUtilsImpl;
import net.jidget.frame.Frame;
import net.jidget.frame.FrameFactory;
import org.cthul.log.CLogger;
import org.cthul.log.CLoggerFactory;

/**
 * A widget on the desktop, with all associated data.
 * 
 * @author Arian Treffer
 */
public class Jidget {
    
    static CLogger log = CLoggerFactory.getClasslogger();
    
    private final String id;
    private final String name;
    private final String author;
    private final String version;
    private final Map<String, Object> beans;
    private final Parent root;
    private final BeanUtilsImpl beanUtils;
    private List<File> styles;
    private Frame frame = null;
    private boolean active = true;
    
    private boolean dragged;
    private double dragX, dragY;
    
    private final SaveTrigger save;
    private final JidgetMemento memento = new JidgetMemento();

    public Jidget(String id, String name, String author, String version, Map<String, Object> beans, Parent root, List<File> styles, BeanUtilsImpl beanUtils, SaveTrigger save) {
        this.id = id;
        this.name = name != null ? name : "";
        this.author = author != null ? author : "";
        this.version = version != null ? version : "";
        this.beans = new HashMap<>(beans);
        this.root = root;
        this.styles = styles;
        this.beanUtils = beanUtils;
        this.save = save;
        root.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent arg0) {
                dragX = frame.getX() - arg0.getScreenX();
                dragY = frame.getY() - arg0.getScreenY();
                arg0.setDragDetect(true);
                dragged = false;
            }
        });
        root.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent arg0) {
                dragged = true;
                frame.setLocation(dragX + arg0.getScreenX() , 
                                  dragY + arg0.getScreenY());
            }
        });
        root.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent arg0) {
                if (dragged) {
                    memento.x = frame.getX();
                    memento.y = frame.getY();
                    Jidget.this.save.scheduleSave();
                }
            }
        });
        initFrame();
    }

    private void initFrame() {
        Utils.runLater(new Runnable() {
            @Override
            public void run() {
                asyncInitFrame();
            }
        });
    }

    private synchronized void asyncInitFrame() {
        if (active) {
            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            for (File f: styles) {
                scene.getStylesheets().add(f.toURI().toString());
            }
            styles = null;
            
            this.frame = FrameFactory.create(scene);
            double x = Double.isNaN(memento.x) ? frame.getX() : memento.x;
            double y = Double.isNaN(memento.y) ? frame.getY() : memento.y;
            frame.setLocation(x, y);
        }
    }
                
    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public String getVersion() {
        return version;
    }
    
    public Object getBean(String id) {
        return beans.get(id);
    }

    @Deprecated // is this really needed?
    public Parent getRoot() {
        return root;
    }

    @Deprecated // is this really needed?
    public Frame getFrame() {
        return frame;
    }

    public synchronized void close() {
        active = false;
        beanUtils.getTaskRegistry().shutDown();
        if (frame != null) {
            frame.close();
        }
    }

    public void scheduleTasks(ScheduledExecutorService scheduler) {
        beanUtils.getTaskRegistry().setScheduler(scheduler);
    }
    
    public Object getMemento() {
        return memento;
    }
    
    public synchronized void setMemento(Object o) {
        if (o instanceof JidgetMemento) {
            JidgetMemento m = (JidgetMemento) o;
            memento.x = m.x;
            memento.y = m.y;
            if (frame != null) {
                frame.setLocation(m.x, m.y);
            }
        }
    }

}
