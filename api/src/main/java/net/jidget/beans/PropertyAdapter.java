package net.jidget.beans;

import javafx.beans.value.ObservableValue;

/**
 * Provides a meta API for a property of a class
 * 
 * @author Arian Treffer
 */
public interface PropertyAdapter {
    
    /**
     * Returns the value of the property specified by tag and index.
     * @param bean
     * @param ns
     * @param tag
     * @param index can be ignored if owner is no collection type.
     * @return 
     */
    public Object getItem(Object bean, String ns, String tag, int index) throws BeanException;

    /**
     * Returns an adapter for the property specified by tag and index.
     * Can return {@code null} if getItem() returned {@code null}.
     * @param bean
     * @param ns
     * @param tag
     * @param index can be ignored if owner is no collection type.
     * @return 
     */
    public BeanAdapter getItemAdapter(Object bean, String ns, String tag, int index) throws BeanException;

    public Object setItem(Object bean, String ns, String tag, int index, Object item) throws BeanException;

    public ObservableValue<?> observe(Object bean, String ns, String tag, int index) throws BeanException;

    public void bind(Object bean, String ns, String tag, int index, ObservableValue<?> binding) throws BeanException;

}
