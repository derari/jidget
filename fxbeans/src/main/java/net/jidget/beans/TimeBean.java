package net.jidget.beans;

import java.util.Calendar;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.*;
import net.jidget.utils.UpdateSignal;

/**
 *
 * @author Arian Treffer
 */
public class TimeBean implements BeanWithUtils {

    private final UpdateSignal update = new UpdateSignal(this);
    private final Property<Calendar> value;
    private final Property<Number> second;
    private final Property<Number> minute;
    private final Property<Number> hour;
    //private final StringProperty valueString;
    private TaskRegistry tasks;

    private boolean enabled = true;
    
    public TimeBean() {
        value = new SimpleObjectProperty<>(Calendar.getInstance());
        second = new SimpleIntegerProperty();
        minute = new SimpleIntegerProperty();
        hour = new SimpleIntegerProperty();
        second.bind(new CalendarFieldBinding(value, Calendar.SECOND));
        minute.bind(new CalendarFieldBinding(value, Calendar.MINUTE));
        hour.bind(new CalendarFieldBinding(value, Calendar.HOUR));

        value.bind(new ObjectBinding<Calendar>() {
            {bind(update);}
            @Override
            protected Calendar computeValue() {
                return Calendar.getInstance();
            }
        });
    }

    public void setEnabled(boolean b) {
        enabled = b;
        if (tasks != null) {
            if (b) {
                tasks.addTask(update, 1000);
            } else {
                tasks.stopTask(update);
            }
        }
    }

    public Property<Calendar> valueProperty() {
        return value;
    }
    
    public Property<Number> secondProperty() {
        return second;
    }

    public Property<Number> minuteProperty() {
        return minute;
    }

    public Property<Number> hourProperty() {
        return hour;
    }

    @Override
    public void setUtils(BeanUtils beanUtils) {
        this.tasks = beanUtils.getTaskRegistry();
        setEnabled(enabled);
    }
    
    private class CalendarFieldBinding extends IntegerBinding {

        private final Property<Calendar> date;
        private final int field;

        public CalendarFieldBinding(Property<Calendar> date, int field) {
            this.date = date;
            this.field = field;
            bind(date);
        }

        @Override
        protected int computeValue() {
            return date.getValue().get(field);
        }
        
    }
}
