package net.jidget;

import javafx.scene.Scene;
import net.jidget.beans.BeanManager;
import net.jidget.frame.Frame;

/**
 *
 * @author Arian Treffer
 */
public class Example {
    
    public static Frame run() {
            Frame w = null;
            Scene scene = null;
            
            try {
                BeanManager beanManager = new BeanManager();
                //new JfxPlugin().initialize(beanManager);
//                BeanAdapter adapter = new SimpleBeanAdapter(beanManager, TimerBean.class);
//                beanManager.registerAdapter(TimerBean.class, adapter);
        
//                File file = new File("src/test/resources/net/jidget/timer_test.xml");
//                SchemaResolver resolver = new JidgetSchemaResolver(new UriFinder());
//                StAXJidgetFactory factory = new StAXJidgetFactory("timer_test.xml", beanManager, file, resolver);
//                Jidget jidget = factory.build();
//                w = jidget.getFrame();
//                Text txt = (Text) jidget.getRoot().getChildrenUnmodifiable().get(0);
//                
//                txt.setFont(Font.font("Arial", 200));
//                txt.setOpacity(0.75);
//                txt.setTextOrigin(VPos.TOP);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return w;
    }
    
    /*
     * 
     */
    
    
}
