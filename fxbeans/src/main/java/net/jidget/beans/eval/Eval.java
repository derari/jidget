package net.jidget.beans.eval;

import java.util.HashSet;
import java.util.Set;
import javafx.beans.property.Property;

/**
 *
 * @author Arian Treffer
 */
public abstract class Eval<T> {
    
    public abstract T value();
    
    public abstract Class<T> getType();

    protected abstract void collectArgs(Set<Property<?>> args);
    
    public Property[] collectArgs() {
        Set<Property<?>> args = new HashSet<>();
        collectArgs(args);
        return args.toArray(new Property[args.size()]);
    }

    public static abstract class EString extends Eval<String> {

        @Override
        public Class<String> getType() {
            return String.class;
        }
        
    }
    
    public static abstract class ENumber extends Eval<Double> {

        @Override
        public Class<Double> getType() {
            return Double.class;
        }
        
    }
    
}
