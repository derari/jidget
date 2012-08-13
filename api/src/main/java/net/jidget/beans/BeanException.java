package net.jidget.beans;

/**
 *
 * @author Arian Treffer
 */
public class BeanException extends Exception {

    /**
     * Creates a new instance of <code>BeanException</code> without detail message.
     */
    public BeanException() {
    }

    /**
     * Constructs an instance of <code>BeanException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public BeanException(String msg) {
        super(msg);
    }

    public BeanException(Throwable cause) {
        super(cause);
    }

    public BeanException(String message, Throwable cause) {
        super(message, cause);
    }
}
