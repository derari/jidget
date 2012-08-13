package net.jidget.beans;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import net.jidget.beans.type.Parameters;
import net.jidget.utils.Interval;
import net.jidget.utils.UpdateOnce;
import net.jidget.utils.UpdateSignal;

/**
 *
 * @author Arian Treffer
 */
public class RandomValue implements BeanWithUtils {

    private final UpdateSignal update = new UpdateSignal(this);
    private final ListProperty<?> input = new SimpleListProperty<>(this, "input", FXCollections.observableArrayList());
    private final DoubleProperty min = new SimpleDoubleProperty(this, "min", 0);
    private final DoubleProperty max = new SimpleDoubleProperty(this, "max", -1);    
    private final DoubleProperty r = new SimpleDoubleProperty(this, "r", -1);
    private final DoubleProperty value = new SimpleDoubleProperty(this, "value", -1);
    private final ObjectProperty<Object> current = new SimpleObjectProperty<>(this, "current", null);
    private final int interval;

    
    {
        r.bind(new DoubleBinding() {
            {bind(update);}
            @Override
            protected double computeValue() {
                return Math.random();
            }
        });
        
        value.bind(new DoubleBinding() {
            {bind(r);}
            @Override
            protected double computeValue() {
                double _min = min.get();
                double _max = max.get();
                if (_max < _min) _max = input.isEmpty() ? _min+1 : input.getSize();
                double v = _min + (_max - _min) * r.get();
                return v;
            }
        });
        
        current.bind(new ObjectBinding<Object>() {
            {bind(value);}
            @Override
            protected Object computeValue() {
                int i = (int) value.get();
                if (i < 0 || i >= input.getSize()) return null;
                return input.get(i);
            }
        });
    }
    
    public RandomValue() {
        this(Interval.NONE);
    }
    
    @Parameters({"update"})
    public RandomValue(Interval interval) {
        this.interval = interval.getMilliseconds();
        if (this.interval == -1) {
            input.addListener(new UpdateOnce(update));
            min.addListener(new UpdateOnce(update));
            max.addListener(new UpdateOnce(update));
        }
    }
    
    @Override
    public synchronized void setUtils(BeanUtils beanUtils) {
        if (interval < 0) return;
        beanUtils.getTaskRegistry().addTask(update, interval);
    }
    
    public DoubleProperty valueProperty() {
        return value;
    }

    public ObjectProperty<Object> currentProperty() {
        return current;
    }

    public ListProperty<?> inputProperty() {
        return input;
    }

    public DoubleProperty maxProperty() {
        return max;
    }

    public DoubleProperty minProperty() {
        return min;
    }
    
}
