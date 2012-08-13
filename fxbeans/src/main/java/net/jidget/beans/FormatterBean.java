package net.jidget.beans;

import java.util.HashMap;
import java.util.Map;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import org.cthul.strings.Formatter;

/**
 *
 * @author Arian Treffer
 */
public class FormatterBean {
    
    private final Map<String, Property<?>> values;

    public FormatterBean() {
        values = new HashMap<>();
    }
    
    public void createFormatVar(String key, FormatString string) {
        if (string != null) {
            if (values.containsKey(key)) {
                throw new RuntimeException(key + " already bound");
            }
            Property<String> p = new SimpleStringProperty();
            values.put(key, p);
            p.bind(createBinding(string));
        } else {
            // will be bound later
            Property<?> p = new SimpleObjectProperty<>();
            values.put(key, p);
        }
    }
    
    public Property<?> property(String arg) {
        Property<?> p = values.get(arg);
//        if (p == null) {
//            System.out.println("prop"  + arg);
//            p = new SimpleObjectProperty<>();
//            values.put(arg, p);
//        }
        return p;
    }

    private ObservableValue<? extends String> createBinding(FormatString string) {
        String[] args = string.getArgs();
        final String format = string.getFormat();
        final Property[] properties = new Property[args.length];
        for (int i = 0; i < args.length; i++) {
            String arg = args[i].trim();
            properties[i] = values.get(arg);
            if (properties[i] == null) {
                throw new RuntimeException("Cannot bind " + arg);
            }
        }
        return new StringBinding() {
            { bind(properties); }
            
            private final Object[] values = new Object[properties.length];
            
            @Override
            protected String computeValue() {
                try {
                    for (int i = 0; i < values.length; i++) {
                        values[i] = properties[i].getValue();
                    }
                    return Formatter.Format(format, values);
                } catch (RuntimeException e) {
                    return e.getMessage();
                }
            }
        };
    }
    
}
