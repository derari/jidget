package net.jidget.beans.eval;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import javafx.beans.property.Property;

/**
 *
 * @author Arian Treffer
 */
public class EMath {
    
    public static abstract class EBinDouble extends EBinary.EDouble<Number,Number> {
        public EBinDouble(Eval<? extends Number> a, Eval<? extends Number> b) {
            super((Eval) a, (Eval) b);
        }

        @Override
        public Double value() {
            return value(a.value().doubleValue(), b.value().doubleValue());
        }
        
        protected abstract double value(double a, double b);
    }
    
    public static class NegateDouble extends Eval.ENumber {

        private final Eval<? extends Number> e;

        public NegateDouble(Eval<? extends Number> e) {
            this.e = e;
        }

        @Override
        protected void collectArgs(Set<Property<?>> args) {
            e.collectArgs(args);
        }

        @Override
        public Double value() {
            return - e.value().doubleValue();
        }
    }
    
    public static class AddDouble extends EBinDouble {

        public AddDouble(Eval<? extends Number> a, Eval<? extends Number> b) {
            super(a, b);
        }

        @Override
        protected double value(double a, double b) {
            return a + b;
        }
        
    }
    
    public static class SubtractDouble extends EBinDouble {

        public SubtractDouble(Eval<? extends Number> a, Eval<? extends Number> b) {
            super(a, b);
        }

        @Override
        protected double value(double a, double b) {
            return a - b;
        }
        
    }
    
    public static class MultiplyDouble extends EBinDouble {

        public MultiplyDouble(Eval<? extends Number> a, Eval<? extends Number> b) {
            super(a, b);
        }

        @Override
        protected double value(double a, double b) {
            return a * b;
        }
        
    }
    
    public static class DivideDouble extends EBinDouble {

        public DivideDouble(Eval<? extends Number> a, Eval<? extends Number> b) {
            super(a, b);
        }

        @Override
        protected double value(double a, double b) {
            return a / b;
        }
        
    }
    
    public static class MathFunc extends Eval.ENumber {
        
        private final Eval[] eArgs;
        private final Method m;

        public MathFunc(String ident, List<Eval<?>> args) {
            this.eArgs = args.toArray(new Eval[args.size()]);
            
            Class[] argTypes = new Class[args.size()];
            for (int i = 0; i < argTypes.length; i++) {
                argTypes[i] = double.class;
            }
            try {
                m = Math.class.getMethod(ident, argTypes);
            } catch (NoSuchMethodException | SecurityException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public Double value() {
            final Object[] args = new Object[eArgs.length];
            for (int i = 0; i < args.length; i++) {
                args[i] = eArgs[i].value();
            }
            try {
                return (Double) m.invoke(null, args);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        protected void collectArgs(Set<Property<?>> args) {
            for (Eval<?> e: this.eArgs)
                e.collectArgs(args);
        }
        
        
        
    }
    
}
