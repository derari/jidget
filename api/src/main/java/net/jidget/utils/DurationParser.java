package net.jidget.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.util.Duration;

/**
 *
 * @author Arian Treffer
 */
public abstract class DurationParser {
//
//    private final int ms;
//
//    public DurationParser(int ms) {
//        this.ms = ms;
//    }
//
//    public int getMilliseconds() {
//        return ms;
//    }
//    
    private static final Pattern pattern = Pattern.compile("(\\d+)(ms|[smhd])?\\s*", Pattern.CASE_INSENSITIVE);
    
    public static int parse(String string) {
        final Matcher m = pattern.matcher(string.trim());
        int ms = 0;
        int start = 0;
        while (m.find(start)) {
            if (m.start() != start) {
                throw new IllegalArgumentException("Unexpected input: '"
                        + string.substring(start, m.start()) + "'");
            }
            start = m.end();
            int value = Integer.parseInt(m.group(1));
            int mult = getMultiplier(m.group(2));
            ms += value * mult;
        }
        if (start < m.regionEnd()) {
            throw new IllegalArgumentException("Unexpected input: '"
                        + string.substring(start) + "'");
        }
        return ms;
    }
    
    private static int getMultiplier(String group) {
        if (group == null) return 1;
        switch (group.toLowerCase()) {
            case "ms": return      1;
            case "s": return    1000;
            case "m": return   60000;
            case "h": return 3600000;
            case "d": return 3600000 * 24;
            default:
                throw new IllegalArgumentException("Multiplier " + group);
        }
    }

}
