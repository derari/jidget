package net.jidget.frame;

import net.jidget.frame.impl.FrameFactoryImpl;
import net.jidget.frame.impl.IFrameFactory;
import org.cthul.proc.Proc1;
import org.junit.Test;
import static org.cthul.matchers.CthulMatchers.*;
import static org.cthul.proc.Procs.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

/**
 *
 * @author Arian Treffer
 */
public class FrameFactoryTest {
    
    private Proc1<String> getInstance = invoke(FrameFactory.class, "getInstance", 1).asProc1();
    
    public FrameFactoryTest() {
    }

    /**
     * getInstance() automatically detects the available implementation.
     */
    @Test
    public void test_getInstance() {
        IFrameFactory factory = FrameFactory.getInstance();
        assertThat(factory, is(instanceOf(FrameFactoryImpl.class)));
    }
    
    @Test
    public void test_getInstance_noClass() {
        assertThat(getInstance.call("foo.bar"), 
                    raisesException(
                    causedBy(ClassNotFoundException.class)));
    }
    
    @Test
    public void test_getInstance_noConstructor() {
        assertThat(getInstance.call("net.jidget.frame.impl.FrameFactoryImplX"), 
                    raisesException(
                    causedBy(NoSuchMethodException.class)));
    }

}
