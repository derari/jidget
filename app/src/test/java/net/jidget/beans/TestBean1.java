package net.jidget.beans;

/**
 *
 * @author Arian Treffer
 */
public class TestBean1 {
    
    public String publicString;
    
    private String stringA = "A";

    public SubBean subBean;
    
    public String getStringA() {
        return stringA;
    }

    public void setStringA(String stringA) {
        this.stringA = stringA;
    }
    
    private String genericString;
    
    public Object get(String key) {
        if ("genericString".equals(key)) {
            return genericString;
        } else {
            throw new IllegalArgumentException(key);
        }
    }
    
    public void set(String key, Object value) {
        if ("genericString".equals(key)) {
            genericString = (String) value;
        } else {
            throw new IllegalArgumentException(key);
        }
    }

    public static class SubBean {

        public String value;
        
    }
    
}
