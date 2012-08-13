package net.jidget.beans;

import net.jidget.Plugin;
import net.jidget.beans.type.BeanType;

/**
 *
 * @author Arian Treffer
 */
public class BeansPlugin extends Plugin {
    
    static {
        UriLoaderDefault.initialize();
    }
    
    private static final String NS_JIDGET_0_1 = "http://jidget.net/0.1/jidget";

    @Override
    protected void setUp() throws BeanException {
        beans(
            new BeanType(TimeBean.class, NS_JIDGET_0_1, "time"),
            new BeanType(DateBean.class, NS_JIDGET_0_1, "date"),
            
            new BeanType(FormatString.class),
            new BeanType(FormatterBean.class){{
                tag(NS_JIDGET_0_1, "format");
                genericProperty().type(FormatString.class).setter("createFormatVar").binding("property");
            }},
            
            new BeanType(EvalVar.class),
            new BeanType(EvalBean.class) {{
                tag(NS_JIDGET_0_1, "eval");
                genericProperty().type(EvalVar.class).setter("createVar").binding("property");
            }},
            
            new BeanType(RandomValue.class){{
                tag(NS_JIDGET_0_1, "random");
            }},
            
            new BeanType(RegexBean.class){{
                tag(NS_JIDGET_0_1, "regex");
            }},
            new BeanType(RegexMatch.class){{}},
            new BeanType(RegexBean.ReplaceStep.class){{}},
            new BeanType(RegexBean.ReplaceFirst.class){{}},
            new BeanType(RegexBean.ReplaceAll.class){{}},
            
            new BeanType(UriLoader.class){{
                tag(NS_JIDGET_0_1, "loader");
            }},
            
            new BeanType(FileList.class){{
                tag(NS_JIDGET_0_1, "files");
            }},
            new BeanType(FileList.Folder.class){{}},
            
            null
            );
    }
    
}
