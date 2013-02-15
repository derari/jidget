package net.jidget.app;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;
import javafx.application.Platform;
import javax.xml.stream.XMLStreamException;
import net.jidget.Jidget;
import net.jidget.beans.BeanException;
import org.cthul.log.CLogger;
import org.cthul.log.CLoggerFactory;
import org.xml.sax.SAXException;

/**
 *
 * @author Arian Treffer
 */
public class JidgetManager {

    static CLogger log = CLoggerFactory.getClassLogger();
    
    private final File rootDir;
    private final File configFile;
    private final Config config;
    
    private final Save save = new Save();
    private final JidgetFactory jFactory;    
    private final Map<String, Jidget> jidgets = new HashMap<>();
    
    private final ScheduledThreadPoolExecutor scheduler;

    public JidgetManager(File root, File config) throws BeanException {
        this.rootDir = root.getAbsoluteFile();
        this.configFile = config.getAbsoluteFile();
        this.config = Config.getOrCreate(configFile);
        this.jFactory = new JidgetFactory(save);
        scheduler = new ScheduledThreadPoolExecutor(1);
        initialize();
    }
    
    private void initialize() {
        this.config.cleanUp(collectJidgetXmls(rootDir));
        for (String jID : this.config.getActiveJidgets()) {
            show(jID);
        }
    }
    
    public synchronized boolean show(String jID) {
        if (!jidgets.containsKey(jID)) {
            Jidget j;
            try {
                File f = rootDir.toPath().resolve(jID).toFile();
                j = jFactory.create(rootDir, jID, f);
            } catch (XMLStreamException | SAXException | IOException | BeanException | RuntimeException e) {
                log.error(e, "Could not create Jidget %s", jID);
                return false;
            }
            jidgets.put(jID, j);
            Object memento = config.getJidgetConfig(jID);
            j.setMemento(memento);
            j.scheduleTasks(scheduler);
        }
        return true;
    }
    
    public synchronized void hide(String jID) {
        Jidget j = jidgets.get(jID);
        if (j != null) {
            config.setJidgetConfig(jID, j.getMemento());
            j.close();
            jidgets.remove(jID);
        }
    }
    
    public String[] getAllJidgetIDs() {
        SortedSet<String> set = collectJidgetXmls(rootDir);
        return set.toArray(new String[set.size()]);
    }
    
    public synchronized boolean isActive(String jID) {
        return jidgets.containsKey(jID);
    }

    public synchronized void exit() {
        saveJidgets();
        for (Jidget j: jidgets.values()) {
            j.close();
        }
        jidgets.clear();
        scheduler.shutdownNow();
        try {
            scheduler.awaitTermination(2, TimeUnit.SECONDS);
        } catch (InterruptedException e) { }
        Platform.exit();
    }
    
    public synchronized void saveJidgets() {
        config.getActiveJidgets().clear();
        config.getActiveJidgets().addAll(jidgets.keySet());
        for (Map.Entry<String, Jidget> e: jidgets.entrySet()) {
            config.setJidgetConfig(e.getKey(), e.getValue().getMemento());
        }
        config.save(configFile);
    }

    private static SortedSet<String> collectJidgetXmls(File root) {
        SortedSet<String> result = new TreeSet<>();
        collectJidgetXmls(root.toPath().normalize(), root, result);
        return result;
    }
    
    private static void collectJidgetXmls(Path root, File dir, SortedSet<String> result) {
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File[] files = dir.listFiles();
        if (files == null) {
            throw new IllegalArgumentException(
                    dir + " is not a directory");
        }
        for (File f: files) {
            if (f.isDirectory()) {
                collectJidgetXmls(root, f, result);
            } else if (f.getName().endsWith("xml")) {
                Path p = root.relativize(f.toPath().normalize());
                result.add(p.toString());
            }
        }
    }
    
    private class Save implements SaveTrigger {
        
        final Runnable action = new Runnable() {
            @Override public void run() {
                performSave();
            }};
        int rescheduleCounter = 0;
        boolean scheduled = false;
        ScheduledFuture<?> f = null;

        @Override
        public synchronized void scheduleSave() {
            if (scheduled && rescheduleCounter < 5) {
                rescheduleCounter++;
                f.cancel(false);
            }
            scheduled = true;
            f = scheduler.schedule(action, 2, TimeUnit.SECONDS);
        }
        
        private void performSave() {
            synchronized (this) {
                scheduled = false;
                rescheduleCounter = 0;
            }
            saveJidgets();
        }
        
    }
}
