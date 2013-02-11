package net.jidget.builder;

import java.io.*;
import java.util.Map.Entry;
import java.util.*;
import org.cthul.log.CLogger;
import org.cthul.log.CLoggerFactory;

/**
 *
 * @author Arian Treffer
 */
public class XmlProperties {
    
    private final CLogger log = CLoggerFactory.getClassLogger();
    private final Map<String, String> map = new HashMap<>();

    public void put(String key, String value) {
        map.put(key, value);
    }
    
    private final Set<String> lock = new HashSet<>();
    
    public synchronized String get(String key) {
        if (!lock.add(key)) {
            log.error("recursion: %s", key);
            return "{recursion:" + key + "}";
        }
        try {
            return apply(map.get(key));
        } finally {
            lock.remove(key);
        }
    }
    
    public void load(String prefix, File f) {
        try {
            if (f.getName().endsWith(".properties")) {
                loadProperties(prefix, f);
            } else {
                log.error("Unexpected extension: %s", f);
            }
        } catch (IOException e) {
            log.error(e);
        }
    }
    
    public void loadProperties(String prefix, File f) throws IOException {
        Properties tmp = new Properties();
        tmp.load(new FileReader(f));
        BufferedReader br = new BufferedReader(new FileReader(f));
        for (Entry<Object, Object> e: tmp.entrySet()) {
            String key = (String) e.getKey();
            String value = (String) e.getValue();
            put(prefix + key, value);
        }
    }
    
    public String apply(String text) {
        if (text == null) return null;
        final int len = text.length();
        int i = 0;
        for (; i < len; i++) {
            char c = text.charAt(i);
            if (c == '$') break;
        }
        if (i == len) return text;
        StringBuilder sb = new StringBuilder();
        int last = 0;
        for (; i < len; i++) {
            char c = text.charAt(i);
            if (c == '$' && i+1 < len) {
                sb.append(text, last, i);
                c = text.charAt(i+1);
                if (c == '$') {
                    sb.append('$');
                    i++;
                } else if (c == '{') {
                    int start = i+2;
                    i = scanComplexKey(text, start);
                    String key = getComplexKey(text, start, i);
                    sb.append(get(key));
                } else {
                    String key = parseSimpleKey(text, i+1);
                    sb.append(get(key));
                    i += key.length();
                }
                last = i+1;
            }
        }
        sb.append(text, last, len);
        return sb.toString();
    }

    private String parseSimpleKey(String text, int i) {
        final int len = text.length();
        final int start = i;
        while (i < len) {
            char c = text.charAt(i);
            boolean valid = c == '.' || ('a' <= c && c <= 'z') ||
                            c == '_' || ('A' <= c && c <= 'Z') ||
                            ('0' <= c && c <= '9');
            if (!valid) {
                break;
            }
            i++;
        }
        return text.substring(start, i);
    }

    private int scanComplexKey(String text, int i) {
        final int len = text.length();
        final int start = i;
        int nested = 0;
        for (; i < len; i++) {
            char c = text.charAt(i);
            if (c == '{') {
                nested++;
            } else if (c == '}') {
                nested--;
                if (nested < 0) {
                    break;
                }
            }
        }
        return i;
    }
    
    private String getComplexKey(String text, int start, int end) {
        return apply(text.substring(start, end));
    }
    
}
