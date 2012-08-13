package net.jidget.sigar;

import net.jidget.Plugin;
import net.jidget.beans.BeanException;
import net.jidget.beans.type.BeanType;

/**
 *
 * @author Arian Treffer
 */
public class SigarPlugin extends Plugin {
    
    private static final String NS_JIDGET_0_1 = "http://jidget.net/0.1/jidget";

    @Override
    protected void setUp() throws BeanException {
        beans(
            new BeanType(CpuMeasure.class){{
                tag(NS_JIDGET_0_1, "cpu");
            }},
            
            null
            );
    }
    
}
