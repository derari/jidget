package net.jidget.beans;

/**
 *
 * @author Arian Treffer
 */
abstract class WithItem<B> {
    private final B bean;
    private final String ns;
    private final String tag;
    private final PropertyAdapter pAdapter;
    private final BeanAdapter iAdapter;
    private Object item;

    public WithItem(BeanAdapter adapter, B bean, String tag) throws BeanException {
        this(adapter, bean, "", tag);
    }
    
    public WithItem(BeanAdapter adapter, B bean, String ns, String tag) throws BeanException {
        this.bean = bean;
        this.tag = tag;
        this.ns = ns;
        pAdapter = adapter.getProperty(bean, ns, tag);
        this.iAdapter = pAdapter.getItemAdapter(bean, ns, tag, 0);
        this.item = pAdapter.getItem(bean, ns, tag, 0);
    }

    public B Do() throws BeanException {
        item = Do(iAdapter, item);
        return (B) pAdapter.setItem(bean, ns, tag, 0, item);
    }

    protected abstract Object Do(BeanAdapter iAdapter, Object item) throws BeanException;
    
}
