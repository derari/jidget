package net.jidget.beans.type.factory;

import net.jidget.beans.BeanException;
import net.jidget.beans.BeanManager;

/**
 *
 * @author Arian Treffer
 */
public interface FactoryMethodDefinition {

    public FactoryMethod create(BeanManager beanManager) throws BeanException;
    
}
