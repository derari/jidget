package net.jidget.beans.eval;

import java.util.List;
import javafx.beans.property.*;
import net.jidget.beans.EvalBean;
import org.cthul.parser.annotation.*;
import org.cthul.parser.lexer.DoubleToken;
import org.cthul.parser.lexer.StringToken;

/**
 *
 * @author Arian Treffer
 */
public class EvalGrammar {
    
    private static final int P_ATOMIC = 0x1000;
    private static final int P_UNARY =  0x0800;
    private static final int P_MULT =   0x0600;
    private static final int P_ADD =    0x0400;
    
    public final EvalBean bean;

    public EvalGrammar(EvalBean bean) {
        this.bean = bean;
    }
    
    @Production({"t_double", "t_string"})
    public Eval<?> t_any(Eval<?> e) {
        return e;
    }
    
    @Production({"t_double ::= tDouble",
                 "t_string ::= tString"})
    public Eval<?> term(Eval<?> e) {
        return e;
    }
    
    @Priority(P_ATOMIC)
    @Production({"tDouble ::= ( ?tDouble )",
                 "tString ::= ( ?tString )"})
    public Eval<?> paren(Void _open, Eval<?> e) {
        return e;
    }
    
    @Priority(P_ATOMIC)
    @Production("tDouble ::= NUMBER")
    public Eval<Double> double_literal(DoubleToken token) {
        return new EConstant<>(token.getValue());
    }
    
    @Priority(P_ATOMIC)
    @Production("tString ::= STRING")
    public Eval<String> string_literal(StringToken token) {
        return new EConstant<>(token.getValue());
    }
    
    @Priority(P_ATOMIC)
    @Production("tDouble ::= DOUBLE_CONSTANT")
    public Eval<Double> double_constant(String token) {
        switch (token) {
            case "PI":
                return new EConstant<>(Math.PI);
            case "E":
                return new EConstant<>(Math.E);
            case "NAN":
                return new EConstant<>(Double.NaN);
            case "INF":
                return new EConstant<>(Double.POSITIVE_INFINITY);
            default:
                throw new IllegalArgumentException(token);
        }
    }
    
    @Priority(P_ATOMIC)
    @Production("tDouble ::= P_DOUBLE")
    public Eval<Number> double_property(StringToken token) {
        Property<?> p = bean.property(token.getValue());
        return new EProperty<>((DoubleProperty) p);
    }
    
    @Priority(P_ATOMIC)
    @Production("tString ::= P_STRING")
    public Eval<String> string_property(StringToken token) {
        Property<?> p = bean.property(token.getValue());
        return new EProperty<>((StringProperty) p);
    }
    
    @Priority(P_UNARY)
    @Production("tDouble ::= - tDouble")
    public Eval<Double> neg(Eval<Double> a) {
        return new EMath.NegateDouble(a);
    }
    
    @Priority(P_ADD)
    @Production("tDouble ::= tDouble + !tDouble")
    public Eval<Double> add(Eval<Double> a, Void _op, Object b) {
        return new EMath.AddDouble(a, (Eval<Double>) b);
    }
    
    @Priority(P_ADD)
    @Production("tDouble ::= tDouble - !tDouble")
    public Eval<Double> sub(Eval<Double> a, Void _op, Eval<Double> b) {
        return new EMath.SubtractDouble(a, b);
    }
    
    @Priority(P_MULT)
    @Production("tDouble ::= tDouble * !tDouble")
    public Eval<Double> mult(Eval<Double> a, Void _op, Eval<Double> b) {
        return new EMath.MultiplyDouble(a, b);
    }
    
    @Priority(P_MULT)
    @Production("tDouble ::= tDouble / !tDouble")
    public Eval<Double> div(Eval<Double> a, Void _op, Eval<Double> b) {
        return new EMath.DivideDouble(a, b);
    }
    
    @Priority(P_UNARY)
    @Production("tDouble ::= IDENTIFIER ( func_args )")
    public Eval<Double> math_func(String ident, Void _open, List<Eval<?>> args) {
        return new EMath.MathFunc(ident, args);
    }
    
    @Sequence(item="t_any", separator=",")
    public List<Eval<?>> func_args(List<Eval<?>> args) {
        return args;
    }
    
}
