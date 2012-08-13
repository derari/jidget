package net.jidget.frame.awt;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.shape.Rectangle;
import javafx.util.Callback;
import net.jidget.Utils;
import net.jidget.frame.Frame;
import net.jidget.frame.FrameFactory;
import org.junit.*;

/**
 *
 * @author Arian Treffer
 */
public class AwtFrameFactoryTest {
    
    public AwtFrameFactoryTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        new Thread(){
            @Override
            public void run() {
                JfxApp.start();
            }   
        }.start();
        Thread.sleep(1000);
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
        
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of create method, of class AwtWidgetFactory.
     */
    @Test
    public void testCreate() throws InterruptedException {
        Frame w = Utils.executeAndWait(new Callback<Void, Frame>() {
            @Override
            public Frame call(Void p) {
                Group group = new Group();
                Scene scene = new Scene(group);
                group.getChildren().add(new Rectangle(5, 5, 10, 10));

                return FrameFactory.create(scene);
            }
        }, null);
        
        System.out.println(w);
        Thread.sleep(3000);
        System.out.println("-----");
    }

}
