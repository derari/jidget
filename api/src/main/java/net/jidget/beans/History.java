package net.jidget.beans;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import net.jidget.beans.BeanUtils;
import net.jidget.beans.BeanWithUtils;
import net.jidget.beans.type.Parameters;
import net.jidget.utils.Interval;

/**
 * 
 * @author Arian Treffer
 */
public class History<T> implements BeanWithUtils {

    private final ObjectProperty<T> value = new SimpleObjectProperty<>(this, "value", null);
    private final ObservableList<XYChart.Data<Double, T>> data;
    private final ListProperty<XYChart.Data<Double, T>> dataProp;
    private final int length;
    private final int interval;

    /**
     * Value is updated automatically.
     * @param length
     * @param interval 
     */
    @Parameters({"length", "update"})
    public History(int length, Interval interval) {
        this.length = length;
        this.interval = interval.getMilliseconds();
        
        List<XYChart.Data<Double, T>> list = new LinkedList<>();// new RingList<>(length);
        data = FXCollections.observableList(list);
        
        dataProp = new SimpleListProperty<>(this, "history", data);
    }
    
    /**
     * Values have to be updated manually via {@link #updateValue()}.
     */
    public History(int length) {
        this(length, Interval.NONE);
    }
    
    public void updateValue() {
        final T v = valueProperty().get();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (data.size() == length) 
                    data.remove(0);
                data.add(new XYChart.Data<>(0.0, v));
                final int size = data.size();
                for (int i = 0; i < size; i++) {
                    data.get(i).setXValue((double) i);
                }
            }
        });
    }

    @Override
    public void setUtils(BeanUtils beanUtils) {
        if (interval < 0) return;
        beanUtils.getTaskRegistry().addTask(new Update(), interval);
    }
    
    public ObjectProperty<T> valueProperty() {
        return value;
    }

//    public ObservableList<Data<Double, T>> dataProperty() {
//        return data;
//    }

    public synchronized ListProperty<XYChart.Data<Double, T>> dataProperty() {
        return dataProp;
    }

    public void updateOnInvalidation() {
        value.addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable arg0) {
                updateValue();
            }
        });
    }

    private class Update implements Runnable {

        @Override
        public void run() {
            updateValue();
        }
        
    }
    
}
