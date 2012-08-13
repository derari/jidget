package net.jidget.beans.impl;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import net.jidget.beans.BeanUtils;

/**
 *
 * @author Arian Treffer
 */
public class BeanUtilsImpl implements BeanUtils {

    private final TaskRegistryImpl taskRegistry;
    private final URI root;
    private final URI jidgetBase;

    public BeanUtilsImpl(URI root, URI base, int defaultInterval) {
        this.root = root;
        this.jidgetBase = base;
        this.taskRegistry = new TaskRegistryImpl(defaultInterval);
    }

    @Override
    public TaskRegistryImpl getTaskRegistry() {
        return taskRegistry;
    }

    @Override
    public URI resolve(String uriOrFile) {
        URI uri;
        if (uriOrFile.startsWith("/") || uriOrFile.matches("[\\w]\\:.*")) {
            uri = new File(uriOrFile).toURI();
        } else {
            try {
                uri = new URI(uriOrFile);
            } catch (URISyntaxException ex) {
                throw new RuntimeException(ex);
            }
        }
        return resolve(uri);
    }
    
    public File[] collectFiles(String filePattern) {
        if (!filePattern.startsWith("..*/")) {
            File f = new File (resolve(filePattern));
            if (f.exists()) return new File[]{f};
            return new File[]{};
        }
        String fName = filePattern.substring(3);
        String[] path = root.relativize(jidgetBase).toString().split("/");
        File dir = new File(root);
        List<File> files = new ArrayList<>();
        for (String step: path) {
            File f = new File(dir, fName);
            if (f.exists()) files.add(f);
            dir = new File(dir, step);
        }
        File f = new File(dir, fName);
        if (f.exists()) files.add(f);
        return files.toArray(new File[files.size()]);
    }
    
    public URI resolve(URI uri) {
        return jidgetBase.resolve(uri);
    }
    
}
