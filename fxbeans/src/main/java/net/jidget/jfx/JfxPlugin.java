package net.jidget.jfx;

import java.util.ArrayList;
import java.util.List;
import javafx.beans.DefaultProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Point3D;
import javafx.geometry.Point3DBuilder;
import javafx.scene.*;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.control.LabelBuilder;
import javafx.scene.effect.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.RotateBuilder;
import javafx.util.Builder;
import net.jidget.Plugin;
import net.jidget.beans.type.*;

/**
 *
 * @author Arian Treffer
 */
public class JfxPlugin extends Plugin {
    
    private static final String NS_JIDGET_0_1 = "http://jidget.net/0.1/jidget";

    @Override
    public void setUp() {
        beans(
            // scene
            new BeanType(Group.class){{
                tag(NS_JIDGET_0_1, "group");
                builder(GroupBuilder.class);
                constructor();
            }},
            // scene.chart
            new BeanType(NumberAxis.class){{
                tag(NS_JIDGET_0_1, "numberAxis");
                builder(NumberAxisBuilder.class);
            }},
            new BuilderBeanType(LineChart.class, (Class) LineChartBuilder.class){{
                tag(NS_JIDGET_0_1, "lineChart");
                property("data").listOf(XYChart.Series.class).setter("data");
                defaultProperty("data");
            }},
            new ListBeanType(XYChart.Series.class){{
                factory(FXCollections.class, "observableArrayList");
                item("series");
            }},
            new BeanType(XYChart.Series.class){{
                defaultProperty("data");
            }},
            // scene.control
            new BeanType(Label.class){{
                tag(NS_JIDGET_0_1, "label");
                builder(LabelBuilder.class);
                constructor();
                constructor(String.class).as("text");
            }},
            // scene.effect
            new BeanType(DropShadow.class){{
                tag(NS_JIDGET_0_1, "dropShadow");
                builder(DropShadowBuilder.class);
                constructor();
                constructor(double.class, Color.class).as("radius", "color");
                constructor(double.class, double.class, double.class, Color.class).as("radius", "offsetX", "offsetY", "color");
                constructor(BlurType.class, Color.class, double.class, double.class, double.class, double.class).as("blur", "color", "radius", "spread", "offsetX", "offsetY");
                defaultProperty("input");
            }},
            new BeanType(Glow.class){{
                tag(NS_JIDGET_0_1, "glow");
                constructor();
                constructor(double.class).as("level");
                defaultProperty("input");
            }},
            // scene.image
            new BeanType(ImageView.class){{
                tag(NS_JIDGET_0_1, "imageView");
                builder(ImageViewBuilder.class);
                constructor();
                constructor(String.class).as("uri");
            }},
            new BeanType(Image.class){{                
                tag(NS_JIDGET_0_1, "image");
                constructor(String.class).as("value");
                constructor(String.class).as("uri");
                scanFactories(ImageFactory.class);
            }},
            // scene.layout
            new BeanType(VBox.class){{
                tag(NS_JIDGET_0_1, "vBox");
                builder(VBoxBuilder.class);
                constructor();
                constructor(double.class).as("spacing");
            }},
            new BeanType(HBox.class){{
                tag(NS_JIDGET_0_1, "hBox");
                builder(HBoxBuilder.class);
                constructor();
                constructor(double.class).as("spacing");
            }},
            // scene.paint
            new BuilderBeanType(Paint.class, PaintComposer.class){{
                factory(Color.class, "valueOf", String.class).as("value");
                factory(Color.class, "valueOf", String.class).as("color");
                factory(LinearGradient.class, "valueOf", String.class).as("linearGradient");
                factory(RadialGradient.class, "valueOf", String.class).as("radialGradient");
                implementation(Color.class, double.class, double.class, double.class, double.class).as("r", "g", "b", "a");
            }},
            new BuilderBeanType(Color.class, (Class) ColorBuilder.class){{
                tag(NS_JIDGET_0_1, "color");
                factory(Color.class, "valueOf", String.class).as("value");
                implementation(Color.class, double.class, double.class, double.class, double.class).as("r", "g", "b", "a");
            }},
            new BuilderBeanType(LinearGradient.class, XLinearGradientBuilder.class){{
                tag(NS_JIDGET_0_1, "linearGradient");
                property("stops").listOf(Stop.class).setter("stops", List.class);
                defaultProperty("stops");
            }},
            new BuilderBeanType(RadialGradient.class, XRadialGradientBuilder.class){{
                tag(NS_JIDGET_0_1, "radialGradient");
                property("stops").listOf(Stop.class).setter("stops", List.class);
                defaultProperty("stops");
            }},
            new ListBeanType(Stop.class){{ 
                item("stop");
            }},
            new BuilderBeanType(Stop.class, StopBuilder.class){{
            }},
            // scene.shape
            new BeanType(Circle.class){{
                tag(NS_JIDGET_0_1, "circle");
                builder(CircleBuilder.class);
                constructor();
                constructor(double.class).as("radius");
                constructor(double.class, double.class, double.class).as("radius", "x", "y");
            }},
            new BeanType(Line.class){{
                tag(NS_JIDGET_0_1, "line");
                builder(LineBuilder.class);
                constructor();
                constructor(double.class, double.class, double.class, double.class).as("startX", "startY", "endX", "endY");
            }},
            // scene.text
            new BeanType(Font.class){{
                tag(NS_JIDGET_0_1, "font");
                builder(FontBuilder.class);
                factory("font", String.class, double.class).as("family", "size");
                factory("font", String.class, FontPosture.class, double.class).as("family", "posture", "size");
                factory("font", String.class, FontWeight.class, double.class).as("family", "weight", "size");
                factory("font", String.class, FontWeight.class, FontPosture.class, double.class).as("family", "weight", "posture", "size");
            }},
            new BeanType(Text.class){{
                tag(NS_JIDGET_0_1, "text");
                builder(TextBuilder.class);
                constructor();
                constructor(String.class).as("text");
                constructor(double.class, double.class, String.class).as("x", "y", "text");
            }},
            // scene.transform
            new BeanType(Rotate.class){{
                tag(NS_JIDGET_0_1, "rotate");
                builder(RotateBuilder.class);
                constructor();
                constructor(double.class).as("angle");
                constructor(double.class, double.class, double.class).as("angle", "x", "y");                
            }},
            // Other
            new BuilderBeanType(Point3D.class, (Class) Point3DBuilder.class){{
                //constructor(double.class, double.class, double.class).as("x", "y", "z");
            }},
        null);
    }
    
    @DefaultProperty("value")
    public static class PaintComposer implements Builder<Paint> {

        private Paint paint;

        public PaintComposer() {
        }
        
        public PaintComposer value(Object value) {
            if (value instanceof String) {
                this.paint = Color.valueOf((String) value);
            } else {
                this.paint = (Paint) value;
            }
            return this;
        }
        
        public PaintComposer paint(Paint paint) {
            this.paint = paint;
            return this;
        }
        
        public PaintComposer color(Color color) {
            this.paint = color;
            return this;
        }
        
        public PaintComposer linearGradient(LinearGradient gradient) {
            this.paint = gradient;
            return this;
        }
        
        public PaintComposer radialGradient(RadialGradient gradient) {
            this.paint = gradient;
            return this;
        }
        
        @Override
        public Paint build() {
            return paint;
        }
        
    }
    
    public static class ImageFactory {
        
        @Parameters({})
        public static Image create() { return null; }
    }
    
    public static class XLinearGradientBuilder implements Builder<LinearGradient> {

        public double startX, startY, endX, endY;
        public CycleMethod cycleMethod = CycleMethod.NO_CYCLE;
        public boolean proportional;
        private List<Stop> stops = new ArrayList<>();

        /** #stops of LinearGradientBuilder does not seem to work this way */
        public XLinearGradientBuilder stops(List<Stop> stops) {
            this.stops.addAll(stops);
            return this;
        }
        
        @Override
        public LinearGradient build() {
            return LinearGradientBuilder.create().cycleMethod(cycleMethod)
                    .startX(startX).startY(startY).endX(endX).endY(endY)
                    .proportional(proportional).stops(stops).build();
        }

    }

    public static class XRadialGradientBuilder implements Builder<RadialGradient> {

        public double centerX, centerY, radius, focusAngle, focusDistance;
        public CycleMethod cycleMethod = CycleMethod.NO_CYCLE;
        public boolean proportional;
        private List<Stop> stops = new ArrayList<>();

        /** #stops of RadialGradientBuilder does not seem to work this way */
        public XRadialGradientBuilder stops(List<Stop> stops) {
            this.stops.addAll(stops);
            return this;
        }
        
        @Override
        public RadialGradient build() {
            return RadialGradientBuilder.create().centerX(centerX)
                    .centerY(centerY).cycleMethod(cycleMethod)
                    .focusAngle(focusAngle).focusDistance(focusDistance)
                    .proportional(proportional).radius(radius).stops(stops)
                    .build();
        }

    }

}
