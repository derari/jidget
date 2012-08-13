package net.jidget.beans.adapter;

import java.util.HashMap;
import java.util.Map;
import javafx.util.Duration;
import net.jidget.beans.BeanAdapter;
import net.jidget.beans.BeanException;
import net.jidget.utils.DurationParser;
import net.jidget.utils.Interval;
import org.xml.sax.helpers.AttributesImpl;

/**
 *
 * @author Arian Treffer
 */
public abstract class ValueParser {

    public static final Map<Class, ValueParser> parser = new HashMap<>();
    
    public static synchronized ValueParser get(Class<?> clazz) {
        ValueParser p = parser.get(clazz);
        if (p != null) return p;
        if (clazz.isEnum()) {
            p = new EnumParser(clazz);
            parser.put(clazz, p);
            return p;
        }
        return null;
    }
    
    static {
        parser.put(String.class, new StringParser());
        put(Boolean.class, boolean.class, new BooleanParser());
        put(Double.class, double.class, new DoubleParser());
        put(Integer.class, int.class, new IntegerParser());
        parser.put(Duration.class, new DurationValueParser());
        parser.put(Interval.class, new IntervalParser());
    }
    
    private static void put(Class<?> c1, Class<?> c2, ValueParser p) {
        parser.put(c1, p);
        parser.put(c2, p);
    }
    
    public abstract Object parse(String s) throws BeanException;
    
    private static class StringParser extends ValueParser {
        @Override
        public Object parse(String s) {
            return s;
        }
    }
    
    private static class BooleanParser extends ValueParser {
        @Override
        public Object parse(String text) {
            final String s = text.trim().toLowerCase();
            switch (s) {
                case "1":
                case "true":
                    return true;
                case "0":
                case "false":
                    return false;
            }
            throw new IllegalArgumentException("Invalid boolean '" + text + "'");
        }
    }
    
    private static class DoubleParser extends ValueParser {
        @Override
        public Object parse(String s) {
            return Double.valueOf(s);
        }
    }
    
    private static class IntegerParser extends ValueParser {
        @Override
        public Object parse(String s) {
            return Integer.valueOf(s);
        }
    }
    
    private static class EnumParser extends ValueParser {
        
        private Class<?> clazz;

        public EnumParser(Class<?> clazz) {
            this.clazz = clazz;
        }
        
        @Override
        @SuppressWarnings("unchecked")
        public Object parse(String s) {
            try {
                return Enum.valueOf((Class) clazz, s);
            } catch (IllegalArgumentException e) {
                return Enum.valueOf((Class) clazz, s.toUpperCase());
            }
        }
        
    }
    
    private static class DurationValueParser extends ValueParser {
        @Override
        public Object parse(String s) throws BeanException {
            return Duration.millis(DurationParser.parse(s));
        }
    }
    
    private static class IntervalParser extends ValueParser {
        @Override
        public Object parse(String s) throws BeanException {
            return new Interval(DurationParser.parse(s));
        }
    }
    
    public static class BeanParser extends ValueParser {
        
        private final String className;
        private final BeanAdapter adapter;

        public BeanParser(String className, BeanAdapter adapter) {
            this.className = className;
            this.adapter = adapter;
        }

        @Override
        public Object parse(String s) throws BeanException {
            AttributesImpl a = new AttributesImpl();
            a.addAttribute("", "value", "", "string", s);
            return adapter.create(className, a);
        }
        
    }
    
}
