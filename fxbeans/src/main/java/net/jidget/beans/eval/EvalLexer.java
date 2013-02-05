package net.jidget.beans.eval;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.Property;
import javafx.beans.property.StringProperty;
import net.jidget.beans.EvalBean;
import org.cthul.parser.annotation.Match;
import org.cthul.parser.annotation.*;
import org.cthul.parser.api.RuleKey;
import org.cthul.parser.lexer.*;
import org.cthul.parser.token.*;

/**
 *
 * @author Arian Treffer
 */
public class EvalLexer {
    
    private static final int P_LITERAL = Priority.Default + 0x1000;
    
    private final EvalBean bean;

    public EvalLexer(EvalBean bean) {
        this.bean = bean;
    }
    
    @Priority(P_LITERAL)
    @Key
    @TokenMatch({"\"(([^\"\\\\]|\\\")*)\"",
                 "'(([^'\\\\]|\\')*)'"})
    public void STRING(@Group(1) String s, TokenBuilder tb) {
        tb.setValue(s);
    }
    
    @Priority(P_LITERAL)
    @Key
    @TokenMatch("[-+]?([0-9]*\\.)?[0-9]+([eE][-+]?[0-9]+)?")
    public static final TokenFactory NUMBER = DoubleToken.FACTORY;
//    
//    @Priority(2)
//    @Match("0x([0-9A-Fa-f]+)")
//    public void hexInt(@Group(1) String s, TokenBuilder tb) {
//        tb.newToken("INT", IntegerToken.FACTORY, Integer.parseInt(s, 16));
//    }

    @Priority(P_LITERAL)
    @Key
    @TokenMatch("E|PI|NAN|INF")
    public static final Void DOUBLE_CONSTANT = null;
    
    @TokenMatch("[a-zA-Z_$][a-zA-Z_$0-9]*")
    public void identifier(String value, TokenBuilder tb) {
        Property<?> p = bean.property(value);
        if (p instanceof DoubleProperty) {
            tb.setKey("P_DOUBLE");
        } else if (p instanceof StringProperty) {
            tb.setKey("P_STRING");
        } else {
            tb.setKey("IDENTIFIER");
        }
    }
    
    @TokenMatch("[()*+-/,]")
    public static final Void operator = null;
    
    @Key
    @TokenMatch("[\\s]+")
    @Channel(Channel.Whitespace)
    public static final Void WS = null;
    
}
