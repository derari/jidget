package net.jidget.beans.eval;

import net.jidget.beans.EvalBean;
import org.cthul.parser.*;
import org.cthul.parser.annotation.scan.AnnotationScanner;
import org.cthul.parser.api.Context;
import org.cthul.parser.api.StringInput;

/**
 *
 * @author Arian Treffer
 */
public class EvalParser {
    
    private static final SimpleParser<Eval<?>> parser;
    
    static {
        SimpleParser.Builder spb = SimpleParser.buildEarleyWithLexer();
        AnnotationScanner a = new AnnotationScanner(spb);
        a.scan(EvalLexer.class, EvalGrammar.class);
        parser = spb.createParser();
    }

    private EvalBean bean;

    public EvalParser(EvalBean bean) {
        this.bean = bean;
    }
    
    public Eval<?> parse(String string, String type) {
        Context<StringInput> ctx = Context.forString(string);
        ctx.put(EvalLexer.class, new EvalLexer(bean));
        ctx.put(EvalGrammar.class, new EvalGrammar(bean));
        if (type == null) type = "any";
        return parser.parse(ctx, "t_" + type);
    }

}
