package net.jidget.beans.eval;

import java.util.Set;
import javafx.beans.property.Property;

/**
 *
 * @author Arian Treffer
 */
public abstract class EBinary<A, B, T> extends Eval<T> {

    protected final Eval<A> a;
    protected final Eval<B> b;

    public EBinary(Eval<A> a, Eval<B> b) {
        this.a = a;
        this.b = b;
    }

    @Override
    protected void collectArgs(Set<Property<?>> args) {
        a.collectArgs(args);
        b.collectArgs(args);
    }
    
    public static abstract class EDouble<A, B> extends EBinary<A, B, Double> {

        public EDouble(Eval<A> a, Eval<B> b) {
            super(a, b);
        }

        @Override
        public Class<Double> getType() {
            return Double.class;
        }
        
    }
    
    
}
