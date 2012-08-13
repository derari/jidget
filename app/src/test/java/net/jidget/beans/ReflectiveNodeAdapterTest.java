package net.jidget.beans;

import javafx.scene.Group;
import javafx.scene.text.Text;
import net.jidget.builder.JidgetSchemaResolver;
import org.hamcrest.Matchers;
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
public class ReflectiveNodeAdapterTest {
    
    private static final String NS = JidgetSchemaResolver.NS_JIDGET_0_1;
    
    BeanManager beanManager;
    
    public ReflectiveNodeAdapterTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() throws BeanException {
        beanManager = new BeanManager();
        //new JfxPlugin().initialize(beanManager);
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void test_create() throws BeanException {
//        BeanAdapter adapter = beanManager.getAdapter(NS, "text");
//        Object bean = adapter.create(NS, "text", null);
//        assertThat(bean, is(instanceOf(Text.class)));
    }
//    
//    @Test
//    public void test_method_access() throws BeanException {
//        BeanAdapter adapter = new ReflectiveNodeAdapter(beanManager, Text.class);
//        Text text = new Text();
//        new WithItem<Text>(adapter, text, "text") {
//            @Override
//            protected Object Do(BeanAdapter iAdapter, Object item) throws BeanException {
//                return "the content";
//            }
//        }.Do();
//        assertThat(text.getText(), is("the content"));
//    }
//    
//    @Test
//    public void test_children_access() throws BeanException {
//        BeanAdapter adapter = new ReflectiveNodeAdapter(beanManager, Group.class);
//        Group group = new Group();
//        new WithItem<Group>(adapter, group, NS, "text") {
//            @Override
//            protected Object Do(BeanAdapter iAdapter, Object item) throws BeanException {
//                item = iAdapter.updateOrCreate(item, NS, "text", null);
//                new WithItem<Object>(iAdapter, item, "text") {
//                    @Override
//                    protected Object Do(BeanAdapter iAdapter, Object item) throws BeanException {
//                        return "the content";
//                    }
//                }.Do();
//                return iAdapter.complete(item);
//            }
//        }.Do();
//        assertThat(group.getChildren(), hasSize(1));
//        Object text = group.getChildren().get(0);
//        assertThat(text, is(instanceOf(Text.class)));
//        assertThat(text, hasProperty("text", equalTo("the content")));
//        //assertThat(text.getContent(), is("the content"));
//    }
//
}
