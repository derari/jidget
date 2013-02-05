package net.jidget.beans;

import java.util.HashMap;
import java.util.Map;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.*;
import net.jidget.beans.eval.Eval;
import net.jidget.beans.eval.EvalParser;

/**
 *
 * @author Arian Treffer
 */
public class EvalBean {
    
    private final Map<String, Property<?>> values;

    public EvalBean() {
        values = new HashMap<>();
    }
    
    public void createVar(String key, EvalVar var) {
        if (var.name == null) {
            var.name = key;
        } else if (var.type == null) {
            var.name = key;
        }
        if (values.containsKey(var.name)) {
            throw new RuntimeException(var.name + " already bound");
        }
        final Property<?> p;
        if (var.value != null) {
            p = createFunction(var.name, var.type, var.value);
        } else {
            p = createProperty(var.name, var.type);
        }
        values.put(var.name, p);
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

    private Property<?> createFunction(String name, String type, String value) {
        Eval<?> eval = new EvalParser(this).parse(value, type);
        final Property<?>  p;
        if (eval.getType() == String.class) {
            if (type != null && !type.equals("string")) {
                throw new RuntimeException(name + " expected " + type + ", got string");
            }
            p = createStringFunction((Eval<String>) eval);
        } else if (eval.getType() == Double.class) {
            if (type != null && !type.equals("double")) {
                throw new RuntimeException(name + " expected " + type + ", got double");
            }
            p = createNumberFunction((Eval<Double>) eval);            
        } else {
            throw new AssertionError(eval.getType());
        }
        return p;
    }
    
    private Property<String> createStringFunction(final Eval<String> eval) {
        Property<String> p = new SimpleStringProperty();
        p.bind(new StringBinding() {
            { bind(eval.collectArgs()); }
            @Override
            protected String computeValue() {
                return eval.value();
            }
        });
        return p;
    }

    private Property<Number> createNumberFunction(final Eval<Double> eval) {
        Property<Number> p = new SimpleDoubleProperty();
        p.bind(new DoubleBinding() {
            { bind(eval.collectArgs()); }
            @Override
            protected double computeValue() {
                return eval.value();
            }
        });
        return p;
    }

    private Property<?> createProperty(String name, String type) {
        if (type == null) {
            throw new RuntimeException(name + " requires type");
        }
        switch (type) {
            case "double":
                return new SimpleDoubleProperty(0);
            case "string":
                return new SimpleStringProperty();
            default:
                throw new RuntimeException("Unsupported type " + type + " of " + name);
        }
    }
    
}
