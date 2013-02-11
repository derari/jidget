package net.jidget;


import javafx.application.Application;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.stage.Stage;

/**
 *
 * @author Arian Treffer
 */
public class JfxApp extends Application {

    private static boolean started = false;
    
    public static synchronized void start() {
        if (started) return;
        started = true;
        Application.launch(JfxApp.class, new String[0]);
    }

    @Override
    public void start(Stage stage) throws Exception {
        LineChart<Double, Double> lc = new LineChart(new NumberAxis(), new NumberAxis());
        
        ObservableList<Data<Double, Double>> dataList = FXCollections.observableArrayList();
        Series<Double, Double> series = new Series<>();
        //series.dataProperty().bind(new SimpleListProperty<>(dataList));
        series.setData(dataList);
        
        lc.getData().add(series);
        
        Group g = new Group(lc);
        Scene sc = new Scene(g);
        stage.setScene(sc);
        
        dataList.add(new Data<>(1.0, 2.0));
        
    }
    
}
