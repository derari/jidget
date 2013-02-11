package net.jidget.sigar;

import javafx.beans.property.*;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart.Data;
import net.jidget.beans.BeanUtils;
import net.jidget.beans.BeanWithUtils;
import net.jidget.beans.History;
import net.jidget.beans.type.Parameters;
import net.jidget.utils.Interval;
import net.jidget.utils.UpdateSignal;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

/**
 *
 * @author Arian Treffer
 */
public class CpuMeasure implements BeanWithUtils {

    private final DoubleProperty value = new SimpleDoubleProperty(0);
    private History<Number> h;
    private final Sigar sigar = new Sigar();
    private final int cpuId;
    private final int interval;
    
    public CpuMeasure() {
        this(Interval.DEFAULT);
    }
    
    @Parameters({"update"})
    public CpuMeasure(Interval interval) {
        this(-1, interval);
    }
    
    @Parameters({"id"})
    public CpuMeasure(int id) {
        this(id, Interval.DEFAULT);
    }
    
    @Parameters({"id", "update"})
    public CpuMeasure(int id, Interval interval) {
        value.set(0);
        this.cpuId = id;
        this.interval = interval.getMilliseconds();
    }
    
    @Override
    public synchronized void setUtils(BeanUtils beanUtils) {
        if (interval < 0) return;
        beanUtils.getTaskRegistry().addTask(new Runnable() {
            @Override
            public void run() {
                value.set(updatedValue());
            }
        }, interval);
    }
    
    public DoubleProperty valueProperty() {
        return value;
    }
    
    private double updatedValue() {
        try {
            final double val;
            if (cpuId < 0) {
                val = sigar.getCpuPerc().getCombined();
            } else {
                CpuPerc[] cpus = sigar.getCpuPercList();
                if (cpuId >= cpus.length) {
                    val = 0;
                } else {
                    val = cpus[cpuId].getCombined();
                }
            }
            return val;
        } catch (SigarException e) {
            return 0;
        }
    }
    
    public synchronized ListProperty<Data<Double, Number>> dataProperty() {
        if (h == null) {
            h = new History<>(20);
            h.valueProperty().bind(value);
            h.updateOnInvalidation();
        }
        return h.dataProperty();
    }
}
