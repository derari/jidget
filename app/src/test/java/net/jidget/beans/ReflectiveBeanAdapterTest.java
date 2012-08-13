package net.jidget.beans;

import net.jidget.beans.adapter.SimpleBeanAdapter;
import net.jidget.beans.type.BeanType;
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
public class ReflectiveBeanAdapterTest {
    
    BeanManager beanManager;
    
    public ReflectiveBeanAdapterTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() throws Exception {
        beanManager = new BeanManager();
        new BeanType(TestBean1.class).registerTo(beanManager);
        new BeanType(TestBean1.SubBean.class).registerTo(beanManager);
    }
    
    @After
    public void tearDown() {
    }
    
    @Test
    public void test_create() throws BeanException {
        BeanAdapter adapter = beanManager.getAdapter(TestBean1.class);
        assertThat("adapter is available", adapter, is(notNullValue()));
        
        Object bean = adapter.create(TestBean1.class.getName(), null);
        assertThat(bean, is(instanceOf(TestBean1.class)));
    }

    /**
     * Test of getAdapter method, of class SimpleBeanAdapter.
     */
    @Test
    public void test_public_field() throws BeanException {
        BeanAdapter adapter = beanManager.getAdapter(TestBean1.class);
        TestBean1 bean = new TestBean1();
        
        bean = new WithItem<TestBean1>(adapter, bean, "publicString") {
            @Override
            protected Object Do(BeanAdapter iAdapter, Object item) throws BeanException {
                item = iAdapter.setText(item, 0, "the public string");
                return iAdapter.complete(item);
            }
        }.Do();
        
        assertThat(bean.publicString, is("the public string"));
        assertThat(bean.getStringA(), is("A"));
    }
    
    @Test
    public void test_method_access() throws BeanException {
        BeanAdapter adapter = beanManager.getAdapter(TestBean1.class);
        TestBean1 bean = new TestBean1();
        
        bean = new WithItem<TestBean1>(adapter, bean, "stringA") {
            @Override
            protected Object Do(BeanAdapter iAdapter, Object item) throws BeanException {
                item = iAdapter.setText(item, 0, "the protected string");
                return iAdapter.complete(item);
            }
        }.Do();
        
        assertThat(bean.getStringA(), is("the protected string"));
    }
    
    @Test
    public void test_generic_method_access() throws BeanException {
        BeanAdapter adapter = beanManager.getAdapter(TestBean1.class);
        TestBean1 bean = new TestBean1();
        
        bean = new WithItem<TestBean1>(adapter, bean, "genericString") {
            @Override
            protected Object Do(BeanAdapter iAdapter, Object item) throws BeanException {
                iAdapter = beanManager.getAdapter(String.class);
                item = iAdapter.setText(item, 0, "a generic string");
                return iAdapter.complete(item);
            }
        }.Do();
        
        assertThat(bean.get("genericString"), is((Object) "a generic string"));
    }
    
    @Test
    public void test_nested_value() throws BeanException {
        BeanAdapter adapter = beanManager.getAdapter(TestBean1.class);
        System.out.println(adapter.toString());
        TestBean1 bean = new TestBean1();
        
        bean = new WithItem<TestBean1>(adapter, bean, "subBean") {
            @Override
            protected Object Do(BeanAdapter iAdapter, Object item) throws BeanException {
                item = iAdapter.updateOrCreate(item, "", "", null);
                item = new WithItem<Object>(iAdapter, item, "value"){
                    @Override
                    protected Object Do(BeanAdapter iAdapter, Object item) throws BeanException {
                        return "the nested string";
                    }
                    
                }.Do();
                return iAdapter.complete(item);
            }
        }.Do();
        
        assertThat(bean.subBean.value, is("the nested string"));
    }

}
