package net.jidget.beans.type.factory;

import java.lang.annotation.Annotation;
import net.jidget.beans.type.Parameters;

/**
 *
 * @author Arian Treffer
 */
@SuppressWarnings("AnnotationAsSuperInterface")
public class ParameterNames implements Parameters {
    
    private final String[] names;

    public ParameterNames(String... names) {
        this.names = names;
    }

    @Override
    public String[] value() {
        return names;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return Parameters.class;
    }
    
}
