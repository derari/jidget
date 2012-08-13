package net.jidget.beans.eval;

import java.util.Set;
import javafx.beans.property.Property;

/**
 *
 * @author Arian Treffer
 */
public class EProperty<T> extends Eval<T> {

    private final Property<T> prop;

    public EProperty(Property<T> prop) {
        this.prop = prop;
    }

    @Override
    public T value() {
        return prop.getValue();
    }

    @Override
    public Class<T> getType() {
        return (Class) value().getClass();
    }

    @Override
    protected void collectArgs(Set<Property<?>> args) {
        args.add(prop);
    }
    
}
