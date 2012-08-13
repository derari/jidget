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
public class DateBean implements BeanWithUtils {

    private final Property<Calendar> value;
    private final Property<Number> day;
    private final Property<Number> month;
    private final Property<Number> year;
    //private final StringProperty valueString;

    private final UpdateSignal update = new UpdateSignal(this);
    private TaskRegistry tasks;

    private boolean enabled = true;
    
    public DateBean() {
        value = new SimpleObjectProperty<>(Calendar.getInstance());
        day = new SimpleIntegerProperty();
        month = new SimpleIntegerProperty();
        year = new SimpleIntegerProperty();
        day.bind(new CalendarFieldBinding(value, Calendar.DAY_OF_MONTH));
        month.bind(new CalendarFieldBinding(value, Calendar.MONTH));
        year.bind(new CalendarFieldBinding(value, Calendar.YEAR));

        value.bind(new ObjectBinding<Calendar>() {
            {bind(update);}
            @Override
            protected Calendar computeValue() {
                return update();
            }
        });
    }

    public void setEnabled(boolean b) {
        enabled = b;
        update();
    }
    
    private Calendar update() {
        Calendar now = Calendar.getInstance();
        if (tasks != null) {
            if (enabled) {
                int delta = (24 - now.get(Calendar.HOUR_OF_DAY)) * 60 * 60;
                delta -= now.get(Calendar.MINUTE);
                delta -= now.get(Calendar.SECOND);
                tasks.addTask(update, delta * 1000 + 1000, 1000);
            } else {
                tasks.stopTask(update);
            }
        }
        return now;
    }

    public Property<Calendar> valueProperty() {
        return value;
    }
    
    public Property<Number> secondProperty() {
        return day;
    }

    public Property<Number> monthProperty() {
        return month;
    }

    public Property<Number> yearProperty() {
        return year;
    }

    @Override
    public void setUtils(BeanUtils beanUtils) {
        this.tasks = beanUtils.getTaskRegistry();
        update();
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
            System.out.println("Updated " + field + " to " + date.getValue().get(field));
            return date.getValue().get(field);
        }
        
    }
}
