package net.jidget.beans.eval;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;
import net.jidget.beans.EvalBean;
import org.cthul.parser.TokenBuilder;
import org.cthul.parser.TokenFactory;
import org.cthul.parser.annotation.Match;
import org.cthul.parser.annotation.*;
import org.cthul.parser.lexer.*;

/**
 *
 * @author Arian Treffer
 */
public class EvalLexer {
    
    private static final int P_LITERAL = 0x1000;
    
    private final IdentifierTokenFactory identifierTF = new IdentifierTokenFactory();
    private final EvalBean bean;

    public EvalLexer(EvalBean bean) {
        this.bean = bean;
    }
    
    @Priority(P_LITERAL)
    @Key
    @Match({"\"(([^\"\\\\]|\\\")*)\"",
            "'(([^'\\\\]|\\')*)'"})
    public void STRING(@Group(1) String s, TokenBuilder tb) {
        tb.setValue(s);
    }
    
    @Priority(P_LITERAL)
    @Key
    @Match("[-+]?([0-9]*\\.)?[0-9]+([eE][-+]?[0-9]+)?")
    @TokenClass(DoubleToken.class)
    public void NUMBER() {}
//    
//    @Priority(2)
//    @Match("0x([0-9A-Fa-f]+)")
//    public void hexInt(@Group(1) String s, TokenBuilder tb) {
//        tb.newToken("INT", IntegerToken.FACTORY, Integer.parseInt(s, 16));
//    }

    @Priority(P_LITERAL)
    @Key
    @Match("E|PI|NAN|INF")
    public void DOUBLE_CONSTANT() {}
    
    @Key
    @Match("[a-zA-Z_$][a-zA-Z_$0-9]*")
    public TokenFactory<?,?> IDENTIFIER() {
        return identifierTF;
    }
    
    @Match("[()*+-/,]")
    public void operator() {}
    
    @Key
    @Match("[\\s]+")
    @Channel(Channel.Whitespace)
    public void WS() {}

    public class IdentifierTokenFactory extends AbstractTokenFactory<String, IdentifierToken> {

        @Override
        protected String parse(String value) {
            return value;
        }

        @Override
        protected IdentifierToken newToken(int id, String key, String value, int start, int end, int channel) {
            return new IdentifierToken(id, key, value, start, end, channel, bean);
        }
        
    }
    
    /**
     * A token that is aware whether it references a property, and its type.
     */
    public static class IdentifierToken extends StringToken {

        private final EvalBean bean;
        
        public IdentifierToken(int id, String key, String value, int start, int end, int channel, EvalBean bean) {
            super(id, key, value, start, end, channel);
            this.bean = bean;
        }

        @Override
        public boolean match(String key) {
            boolean b = super.match(key);
            if (b) return true;
            switch (key) {
                case "P_DOUBLE":
                    return bean.property(getValue()) instanceof DoubleProperty;
                case "P_STRING":
                    return bean.property(getValue()) instanceof StringProperty;
            } 
            return false;
        }
        
    }
    
}
