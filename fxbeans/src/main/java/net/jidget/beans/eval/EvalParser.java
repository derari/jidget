package net.jidget.beans.eval;

import net.jidget.beans.EvalBean;
import org.cthul.parser.*;
import org.cthul.parser.annotation.AnnotationScanner;

/**
 *
 * @author Arian Treffer
 */
public class EvalParser extends AbstractParser<Eval<?>> {
    
    public static EvalParser create(EvalBean bean) {
        AnnotationScanner a = new AnnotationScanner();
        a.addAll(new EvalLexer(bean), new EvalGrammar(bean));
        return new EvalParser(a.createLexer(), a.createGrammar());
    }

    public EvalParser(Lexer lexer, Grammar grammer) {
        super(lexer, grammer);
    }
    
    public Eval<?> parse(String string, String type) {
        if (type == null) type = "any";
        return super.parse(string, "t_" + type);
    }

}
