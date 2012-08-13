package net.jidget.beans.factory;

import net.jidget.beans.type.factory.Signature;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

/**
 *
 * @author Arian Treffer
 */
public class SignatureTest {
    
    public SignatureTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of equals method, of class Signature.
     */
    @Test
    public void testEquals() {
        Signature s1 = new Signature(
                new String[]{"foo", "foo"}, 
                new String[]{"bar", "baz"});
        Signature s2 = new Signature(
                new String[]{null, null}, 
                new String[]{"baz", "bar"});
        
        assertThat(s1, is(equalTo(s2)));
    }

}
