package net.jidget.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.*;
import net.jidget.beans.type.Parameters;
import net.jidget.beans.type.PropertyType;

/**
 *
 * @author Arian Treffer
 */
public class RegexMatch {
    
    private static final Matcher NO_MATCH;
    static {
        NO_MATCH = Pattern.compile(".*").matcher("");
        NO_MATCH.find();
    }
    
    private final StringProperty input = new SimpleStringProperty(this, "input", "");
    private final StringProperty patternString = new SimpleStringProperty(this, "patternString", ".*");
    private final ObjectProperty<Pattern> pattern = new SimpleObjectProperty<>(this, "pattern", NO_MATCH.pattern());
    private final ObjectProperty<Matcher> matcher = new SimpleObjectProperty<>(this, "matcher", NO_MATCH);
    private final List<StringProperty> groups = new ArrayList<>();
    private IntegerProperty result = null;
    private StringProperty value = null;
    
    @Parameters("result")
    public RegexMatch(int result) {
        this();
        setResult(result);
    }
    
    public RegexMatch() {
        pattern.bind(new ObjectBinding<Pattern>() {
            {bind(patternString);}
            @Override
            protected Pattern computeValue() {
                return Pattern.compile(patternString.get());
            }
        });
        matcher.bind(new ObjectBinding<Matcher>() {
            {bind(input, pattern);}
            @Override
            protected Matcher computeValue() {
                Matcher m = pattern.get().matcher(input.get());
                return m.find() ? m : NO_MATCH;
            }
        });
    }
    
    @PropertyType(String.class)
    public StringProperty inputProperty() {
        return input;
    }
    
    @PropertyType(String.class)
    public StringProperty patternProperty() {
        return patternString;
    }
    
    @PropertyType(Integer.class)
    public synchronized IntegerProperty resultProperty() {
        if (result == null) createResultAndValueProperty();
        return result;
    }
    
    @PropertyType(String.class)
    public synchronized StringProperty valueProperty() {
        if (value == null) createResultAndValueProperty();
        return value;
    }
    
    public void setInput(String input) {
        this.input.set(input);
    }
    
    public synchronized String getValue() {
        if (value == null) {
            return matcher.get().group();
        }
        return value.get();
    }
    
    public Property property(String name) {
        if (!name.startsWith("group")) return null;
        int id = Integer.parseInt(name.substring(5));        
        return getGroup(id);
    }

    private synchronized Property getGroup(int id) {
        while (groups.size() <= id) groups.add(null);
        StringProperty g = groups.get(id);
        if (g == null) {
            g = createGroup(id);
            groups.set(id, g);
        }
        return g;
    }

    private StringProperty createGroup(final int id) {
        StringProperty p = new SimpleStringProperty(this, "group"+id, "");
        p.bind(new StringBinding() {
            {bind(matcher);}
            @Override
            protected String computeValue() {
                if (matcher.get().groupCount() < id) return "";
                return matcher.get().group(id);
            }
        });
        return p;
    }

    private void setResult(int result) {
        resultProperty().set(result);
    }

    private void createResultAndValueProperty() {
        assert result == null : "result property should be null";
        assert value == null : "value property should be null";
        result = new SimpleIntegerProperty(this, "result", -1);
        value = new SimpleStringProperty(this, "value", "");
        value.bind(new StringBinding() {
            {bind(result, matcher);}
            @Override
            protected String computeValue() {
                int id = result.get();
                if (id == -1) return input.get();
                if (matcher.get().groupCount() < id) return "";
                return matcher.get().group(id);
            }
        });
    }
    
}
