package net.jidget.builder;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Arian Treffer
 */
public class XmlProperties {
    
    private final Map<String, String> map = new HashMap<>();

    public void put(String key, String value) {
        map.put(key, value);
    }
    
    public void load(String prefix, File f) {
        try {
            if (f.getName().endsWith(".properties")) {
                loadProperties(prefix, f);
            } else {
                throw new IllegalArgumentException("Unexpected extension: " + f);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void loadProperties(String prefix, File f) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(f));
        String line;
        while ((line = br.readLine()) != null) {
            int iEq = line.indexOf('=');
            if (iEq > 0) {
                String key = line.substring(0, iEq).trim();
                if (!key.startsWith("#")) {
                    String value = line.substring(iEq+1);
                    put(prefix + key, value);
                }
            }
        }
    }
    
    private static Pattern propPattern = Pattern.compile("\\$(\\$|[-\\w\\d.]+|\\{[-\\w\\d.]+\\})");

    public String apply(String text) {
        if (text == null) return null;
        if (!text.contains("$")) return text;
        StringBuffer sb = new StringBuffer();
        Matcher m = propPattern.matcher(text);
        while (m.find()) {
            String key = m.group(1);
            if (key.startsWith(("{"))) {
                key = key.substring(1, key.length()-1);
            }
            final String value;
            if (key.equals(("$"))) {
                value = "$";
            } else {
                String pValue = map.get(key);
                value = pValue == null ? "" : pValue;
            }
            m.appendReplacement(sb, value);
        }
        m.appendTail(sb);
        return sb.toString();
    }
    
}
