package net.jidget.beans.type;

import java.lang.annotation.*;

/**
 *
 * @author Arian Treffer
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface PropertyType {

    public Class value();
    
    public CollectionType collection() default CollectionType.AUTO;
    
}
