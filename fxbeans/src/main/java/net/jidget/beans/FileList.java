package net.jidget.beans;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import net.jidget.beans.type.Parameters;
import net.jidget.utils.Interval;

/**
 *
 * @author Arian Treffer
 */
public class FileList implements BeanWithUtils {
    
    private final ListProperty<String> files = new SimpleListProperty<>(this, "files", FXCollections.<String>observableArrayList());
    private final int interval;
    
    private List<FileSelection> input = new ArrayList<>();

    @Parameters("update")
    public FileList(Interval interval) {
        this.interval = interval.getMilliseconds();
    }

    public FileList() {
        this(Interval.NONE);
    }
    
    public void include(FileSelection s) {
        input.add(s);
    }
    
    public void addFolder(Folder folder) {
        include(folder);
    }
    
    public ListProperty<String> allProperty() {
        return files;
    }

    public ListProperty<String> filesProperty() {
        return files;
    }

    public ListProperty<String> listProperty() {
        return files;
    }

    @Override
    public void setUtils(BeanUtils beanUtils) {
        update(beanUtils);
        if (interval >= 0) {
            beanUtils.getTaskRegistry().addTask(new Update(beanUtils), interval);
        }
    }

    protected synchronized void update(BeanUtils beanUtils) {
        files.clear();
        for (FileSelection s: input) {
            try {
                s.addAll(beanUtils, files);
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
    }
    
    private class Update implements Runnable {
        private final BeanUtils beanUtils;

        public Update(BeanUtils beanUtils) {
            this.beanUtils = beanUtils;
        }

        @Override
        public void run() {
            update(beanUtils);
        }
    }
    
    public static interface FileSelection {
        public void addAll(BeanUtils beanUtils, List<String> target);
    }
    
    public static class Folder implements FileSelection {
        
        private String path;
        private Pattern pattern;

        @Parameters({"path", "pattern"})
        public Folder(String path, String pattern) {
            this.path = path;
            this.pattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        }

        @Override
        public void addAll(BeanUtils beanUtils, List<String> target) {
            File root = new File(beanUtils.resolve(path));
            if (!root.isDirectory()) return;
            addAll(root, "", target);
        }

        private void addAll(File root, String path, List<String> target) {
            for (File f: root.listFiles()) {
                if (f.isDirectory()){
                    addAll(f, path + f.getName() + "/", target);
                } else {
                    String n = path + f.getName();
                    if (pattern.matcher(n).matches()) {
                        target.add(f.toURI().toString());
                    }
                }
            }
        }
        
    }
    
}
