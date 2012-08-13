package net.jidget.builder;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.Property;
import net.jidget.beans.BeanException;
import net.jidget.beans.BeanManager;
import org.junit.*;

/**
 *
 * @author Arian Treffer
 */
public class StAXJidget_1_0Test {
    
    BeanManager beanManager;
    
    public StAXJidget_1_0Test() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() throws BeanException {
//        beanManager = new BeanManager();
//        new JfxPlugin().initialize(beanManager);
//        BeanAdapter adapter = new SimpleBeanAdapter(beanManager, TestBean1.class);
//        beanManager.registerAdapter(TestBean1.class, adapter);
//        adapter = new SimpleBeanAdapter(beanManager, TimerBean.class);
//        beanManager.registerAdapter(TimerBean.class, adapter);
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of build method, of class StAXJidget_1_0.
     */
    @Test
    public void testBuild() throws Exception {
//        File file = new File("src/test/resources/net/jidget/builder/bean_test.xml");
//        SchemaResolver resolver = new JidgetSchemaResolver(new UriFinder());
//        StAXJidgetFactory factory = new StAXJidgetFactory("", beanManager, file, resolver);
//        Jidget jidget = factory.build();
//        assertThat(jidget.getName(), is("Name"));
//        assertThat(jidget.getAuthor(), is("Author"));
//        assertThat(jidget.getVersion(), is("1.0"));
//        
//        TestBean1 bean = (TestBean1) jidget.getBean("test1");
//        assertThat(bean.publicString, is("the public string"));
//        assertThat(bean.getStringA(), is("A"));
//        assertThat(bean.subBean.value, is("nested value"));
    }
    
    @Test
    public void test_build_with_binding() throws Exception {
//        File file = new File("src/test/resources/net/jidget/timer_test.xml");
//        
//        Text txt = Utils.executeAndWait(new Callback<File, Text>() {
//            @Override
//            public Text call(File f) {
//                try {
//                    SchemaResolver resolver = new JidgetSchemaResolver(new UriFinder());
//                    StAXJidgetFactory factory = new StAXJidgetFactory("", beanManager, f, resolver);
//                    Jidget jidget = factory.build();
//                    Text txt = (Text) jidget.getBean("text");
//
//                    if (txt.getText().isEmpty()) {
//                        waitForChange(txt.textProperty(), 5000);
//                    }
//                    return txt;
//                } catch (Exception e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        }, file);
//
//        assertThat(txt.getText(), matchesPattern("\\d+"));
    }
    
    private void waitForChange(final Property<?> property, final int timeout) {
        final Object lock = new Object();
        synchronized (lock) {
            property.addListener(new InvalidationListener() {
                @Override
                public void invalidated(Observable o) {
                    synchronized (lock) {
                        lock.notifyAll();
                    }
                }
            });
            try {
                lock.wait(timeout);
            } catch (InterruptedException e) {
                // similar to timeout
            }
        }
    }
}
