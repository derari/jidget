package net.jidget.beans.type;

import java.util.ArrayList;
import net.jidget.beans.TypeDescriptor;

/**
 *
 * @author Arian Treffer
 */
public class ListBeanType extends SimpleBeanType {
    
    private final TypeDescriptor item;

    public ListBeanType(Class<?> item) {
        this(TypeDescriptor.simpleType(item));
    }
    
    public ListBeanType(TypeDescriptor item) {
        super(TypeDescriptor.listType(item));
        this.item = item;
        init();
    }
    
    private void init() {
        item("item");
        implementation(ArrayList.class);
    }

    protected void item(String name) {
        add(property(name).type(item).setter("add", Object.class));
        defaultProperty(name);
        //defaultIsGeneric = true;
    }

}
