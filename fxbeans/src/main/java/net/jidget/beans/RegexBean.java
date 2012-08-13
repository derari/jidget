package net.jidget.beans;

import java.util.ArrayList;
import java.util.List;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import net.jidget.beans.type.Parameters;
import net.jidget.beans.type.PropertyType;

/**
 *
 * @author Arian Treffer
 */
public class RegexBean {
    
    private List<Step> steps = new ArrayList<>();
    private final StringProperty string = new SimpleStringProperty(this, "string", "");
    private StringProperty result = null;
    
    private StringProperty lastIntermediateResult = string;

    public RegexBean() {
    }
    
    // necessary if @PropertyType is removed below
//    public void setString(String string) { 
//        this.string.set(string);
//    }
    
    @PropertyType(String.class)
    public StringProperty stringProperty() {
        return string;
    }
    
    public synchronized void addReplace(ReplaceStep step) {
        if (result != null) {
            throw new IllegalStateException("Cannot add steps once result is created");
        }
        steps.add(step);
    }
    
    public void addReplaceFirst(ReplaceFirst r) {
        addReplace(r);
    }
    
    public void addReplaceAll(ReplaceAll r) {
        addReplace(r);
    }
    
    public synchronized void addMatch(RegexMatch match) {
        if (result != null) {
            throw new IllegalStateException("Cannot add match once result is created");
        }
        match.inputProperty().bind(applySteps());
        lastIntermediateResult = match.valueProperty();
    }
    
    public synchronized StringProperty resultProperty() {
        if (result == null) createResult();
        return result;
    }
    
    private ObservableValue<String> applySteps() {
        if (steps.isEmpty()) return lastIntermediateResult;
        final List<Step> currentSteps = steps;
        steps = new ArrayList<>();
        
//        StringProperty p = new SimpleStringProperty(this, "intermediate", "");
//        p.bind(new StringBinding() {
//            {bind(lastIntermediateResult);}
//            @Override
//            protected String computeValue() {
//                String string = lastIntermediateResult.getValue();
//                for (Step step: currentSteps) {
//                    string = step.apply(string);
//                }
//                return string;
//            }
//        });
//        return p;
        return new StringBinding() {
            {bind(lastIntermediateResult);}
            @Override
            protected String computeValue() {
                String string = lastIntermediateResult.get();
                for (Step step: currentSteps) {
                    string = step.apply(string);
                }
                return string;
            }
        };
    }

    private void createResult() {
        StringProperty p = new SimpleStringProperty(this, "result", "");
        p.bind(applySteps());
        result = p;
    }
    
    public static interface Step {
        
        public String apply(String input);
        
    }
    
//    public static class MatchStep implements Step {
//        
//        private final RegexMatch match;
//
//        public MatchStep(RegexMatch match) {
//            this.match = match;
//        }
//
//        @Override
//        public String apply(String input) {
//            match.setInput(input);
//            return match.getValue();
//        }
//        
//    }
    
    public static abstract class ReplaceStep implements Step {
        
        public String pattern;
        public String replacement;

        @Parameters({"first", "with"})
        public static ReplaceStep First(String pattern, String replacement) {
            return new ReplaceFirst(pattern, replacement);
        }
        
        @Parameters({"all", "with"})
        public static ReplaceStep All(String pattern, String replacement) {
            return new ReplaceAll(pattern, replacement);
        }
        
    }
    
    public static class ReplaceFirst extends ReplaceStep {
        
        @Parameters({"pattern", "replacement"})
        public ReplaceFirst(String pattern, String replacement) {
            this.pattern = pattern;
            this.replacement = replacement;
        }

        public ReplaceFirst() {
        }
        
        @Override
        public String apply(String input) {
            return input.replaceFirst(pattern, replacement);
        }
        
    }
    
    public static class ReplaceAll extends ReplaceStep {
        
        @Parameters({"pattern", "replacement"})
        public ReplaceAll(String pattern, String replacement) {
            this.pattern = pattern;
            this.replacement = replacement;
        }

        public ReplaceAll() {
        }
        
        @Override
        public String apply(String input) {
            return input.replaceFirst(pattern, replacement);
        }
        
    }
    
}
