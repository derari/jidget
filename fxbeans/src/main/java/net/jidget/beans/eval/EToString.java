package net.jidget.beans.eval;

import java.util.Set;
import javafx.beans.property.Property;

/**
 *
 * @author Arian Treffer
 */
public class EToString extends Eval.EString {
    
    private final Eval<?> eval;

    public EToString(Eval<?> eval) {
        this.eval = eval;
    }

    @Override
    public String value() {
        return String.valueOf(eval.value());
    }
    
    @Override
    protected void collectArgs(Set<Property<?>> args) {
        eval.collectArgs(args);
    }
    
}
