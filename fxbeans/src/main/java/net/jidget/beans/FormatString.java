package net.jidget.beans;

import net.jidget.beans.type.Parameters;

/**
 *
 * @author Arian Treffer
 */
public class FormatString {

    @Parameters({})
    public static FormatString valueBinding() {
        return null;
    }
    
    private String format;
    private String args;

    @Parameters({"format", "args"})
    public FormatString(String format, String args) {
        this.format = format;
        this.args = args;
    }

    public String getFormat() {
        return format;
    }

    public String[] getArgs() {
        return args.split(",");
    }
    
}
