package net.jidget.beans.eval;

import java.util.Set;
import javafx.beans.property.Property;

/**
 *
 * @author Arian Treffer
 */
public class EConstant<T> extends Eval<T> {

    private final T value;

    public EConstant(T value) {
        this.value = value;
    }

    @Override
    public T value() {
        return value;
    }

    @Override
    public Class<T> getType() {
        return value == null ? null : (Class) value.getClass();
    }

    @Override
    protected void collectArgs(Set<Property<?>> args) {
    }
    
}
