package net.jidget.beans;

import javafx.beans.property.Property;
import javafx.scene.shape.Line;
import javafx.scene.transform.Rotate;
import org.hamcrest.Matcher;
import org.junit.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

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
        assertThat(propertyValue("lit"), isDouble(-50));        
    }
    
    @Test
    public void test_string_literal() {
        instance.createVar("lit", EvalVar.f("'hello'"));
        assertThat(propertyValue("lit"), isString("hello"));        
    }
    
    @Test
    public void test_double_constant() {
        instance.createVar("c", EvalVar.f("PI"));
        assertThat(propertyValue("c"), isDouble(Math.PI));        
    }
    
    @Test
    public void test_string_property() {
        setProperty("msg", "string", "hello");
        instance.createVar("f", EvalVar.f("msg"));
        assertThat(propertyValue("f"), isString("hello"));
    }

    @Test
    public void test_math() {
        setProperty("var", "double", 2);
        instance.createVar("f", EvalVar.f("1 + var * 3"));
        assertThat(propertyValue("f"), isDouble(7));
        ((Property<Object>) instance.property("var")).setValue(3);
        assertThat(propertyValue("f"), isDouble(10));
    }

    @Test
    public void test_math_func() {
        instance.createVar("f", EvalVar.f("sin(PI)"));
        assertThat(propertyValue("f"), isDouble(0));
    }

    private void setProperty(String name, String type, Object value) {
        instance.createVar(name, EvalVar.var(type));
        ((Property<Object>) instance.property(name)).setValue(value);
    }
    
    private Object propertyValue(String name) {
        return instance.property(name).getValue();
    }
    
    private static Matcher<Object> isDouble(double d) {
        return (Matcher) closeTo(d, 0.00001);
    }
    
    private static Matcher<Object> isString(String s) {
        return (Matcher) is(s);
    }
    
}
