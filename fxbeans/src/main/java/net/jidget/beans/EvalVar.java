package net.jidget.beans;

import net.jidget.beans.type.Parameters;

/**
 *
 * @author Arian Treffer
 */
public class EvalVar {
    
    public String name;
    public String type;
    public String value;
    
    @Parameters({"name", "type"})
    public static EvalVar var(String name, String type) {
        return new EvalVar(name, type, null);
    }

    @Parameters({"type"})
    public static EvalVar var(String type) {
        return new EvalVar(null, type, null);
    }

    @Parameters({"type", "value"})
    public static EvalVar f(String type, String value) {
        return new EvalVar(null, type, value);
    }

    @Parameters({"value"})
    public static EvalVar f(String value) {
        return new EvalVar(null, null, value);
    }

    public EvalVar(String name, String type, String value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }
    

    public String getValue() {
        return value;
    }
    
}
