package net.jidget.app;

import java.beans.*;
import java.io.*;
import java.util.*;
import org.cthul.log.CLogger;
import org.cthul.log.CLoggerFactory;

/**
 *
 * @author Arian Treffer
 */
public class Config implements Serializable {
    
    private static final long serialVersionUID = 8002077539859903415L;
    
    static final CLogger log = CLoggerFactory.getClassLogger();
    
    public static Config getOrCreate(File file) {
        try {
            Object result;
            InputStream is = new BufferedInputStream(new FileInputStream(file));
            try (XMLDecoder d = new XMLDecoder(is)) {
                result = d.readObject();
            }
            return (Config) result;
        } catch (FileNotFoundException e) {
            log.info("Config does not exist: %s", file);
        } catch (RuntimeException e) {
            log.warn(e, "Could not read configuration: %E$s");
        }
        return new Config();
    }

    
    private Set<String> activeJidgets;
    private Map<String, Object> jidgetConfigs;

    public Config() {
        this.activeJidgets = new HashSet<>();
        this.jidgetConfigs = new HashMap<>();
    }

    public Set<String> getActiveJidgets() {
        return activeJidgets;
    }
    
    public void addActive(String[] jId) {
        activeJidgets.addAll(Arrays.asList(jId));
    }
    
    public void setJidgetConfig(String key, Object value)     {
        jidgetConfigs.put(key, value);
    }
    
    public Object getJidgetConfig(String key) {
        return jidgetConfigs.get(key);
    }
    
    public void cleanUp(Set<String> jIDs) {
        Iterator<String> itActive = activeJidgets.iterator();
        while (itActive.hasNext()) {
            if (!jIDs.contains(itActive.next())) {
                itActive.remove();
            }
        }
        
        Iterator<String> itConfigs = jidgetConfigs.keySet().iterator();
        while (itConfigs.hasNext()) {
            String config = itConfigs.next();
            if (!jIDs.contains(config)) {
                itConfigs.remove();
            }
        }
    }
    
    public void save(File file) {
        file.getParentFile().mkdirs();
        try {
            JidgetMemento m = new JidgetMemento();
            m.x = 5;
            OutputStream os = new BufferedOutputStream(new FileOutputStream(file));
            try (XMLEncoder e = new XMLEncoder(os)) {
                e.writeObject(this);
                Statement stmt = new Statement(this, "addActive", new Object[]{
                      activeJidgets.toArray(new String[activeJidgets.size()])});
                e.writeStatement(stmt);
                for (Map.Entry<String, Object> j: jidgetConfigs.entrySet()) {
                    stmt = new Statement(this, "setJidgetConfig", new Object[]{
                        j.getKey(), j.getValue()});
                    e.writeStatement(stmt);
                }
            }
            log.info("saved");
        } catch (IOException e) {
            log.error(e);
        }
    }
    
}
