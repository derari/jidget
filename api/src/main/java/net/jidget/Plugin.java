package net.jidget;

import java.util.*;
import net.jidget.beans.BeanException;
import net.jidget.beans.BeanManager;
import net.jidget.beans.type.SimpleBeanType;

/**
 *
 * @author Arian Treffer
 */
public abstract class Plugin {
    
    private final List<SimpleBeanType> beans = new ArrayList<>();
    
    protected void beans(SimpleBeanType... beans) {
        this.beans.addAll(Arrays.asList(beans));
    }
    
    protected void beans(Class... beans) {
        for (Class c: beans) {
            this.beans.add(new SimpleBeanType(c));
        }
    }
    
    protected abstract void setUp() throws BeanException;
    
    public void initialize(BeanManager beanManager) throws BeanException {
        setUp();
        for (SimpleBeanType b: beans) {
            if (b != null) {
                b.registerTo(beanManager);
            }
        }
    }
    
}
