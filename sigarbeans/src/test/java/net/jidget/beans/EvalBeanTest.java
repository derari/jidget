package net.jidget.beans;

import javafx.beans.property.Property;
import javafx.scene.shape.Line;
import javafx.scene.transform.Rotate;
import org.hamcrest.Matcher;
import org.junit.*;
import static org.hamcrest.MatcherAssert.*;

/**
 *
 * @author Arian Treffer
 */
public class EvalBeanTest {
    
    public EvalBeanTest() {
    }
    
    private EvalBean instance;

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
        instance = new EvalBean();
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void test_double_literal() {
        instance.createVar("lit", EvalVar.f("-.5E2"));
    }
    
}
