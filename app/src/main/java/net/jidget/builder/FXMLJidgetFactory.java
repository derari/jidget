package net.jidget.builder;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.util.Builder;
import javafx.util.BuilderFactory;
import javax.xml.stream.XMLStreamReader;
import net.jidget.Jidget;
import net.jidget.app.SaveTrigger;
import net.jidget.beans.BeanManager;
import net.jidget.beans.impl.BeanUtilsImpl;

public class FXMLJidgetFactory {
    
    private final SaveTrigger save;
    private final BeanManager beanManager;
    private final String id;
    private final URI root;
    private final URI path;
    private final File file;

    public FXMLJidgetFactory(URI root, String id, File file, BeanManager beanManager, SaveTrigger save) {
        this.id = id;
        this.beanManager = beanManager;
        this.save = save;
        this.root = root;
        this.file = file;
        this.path = file.getParentFile().toURI();
    }
    
    public Jidget build() throws IOException {
        BeanUtilsImpl beanUtils = new BeanUtilsImpl(root, path, 1000);
        Node n = FXMLLoader.load(file.toURI().toURL());
        Parent p = (n instanceof Parent) ? (Parent) n : new Group(n);
        Map<String, Object> beans = Collections.emptyMap();
        List<File> styles = Collections.emptyList();
        return new Jidget(id, "fxml-jidget", "", "", beans, p, styles, beanUtils, save);
    }
    
}
