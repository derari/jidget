package net.jidget.beans.type;

import java.lang.annotation.*;

/**
 *
 * @author Arian Treffer
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD})
public @interface Parameters {

    public String[] value();
    
}
