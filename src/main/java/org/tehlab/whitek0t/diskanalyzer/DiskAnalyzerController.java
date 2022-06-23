package org.tehlab.whitek0t.diskanalyzer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
public class DiskAnalyzerController {

    private final Analyzer analyzer = new Analyzer();
    private Map<String, Long> directorySize = new HashMap<>();
    private final ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
    private PieChart pieChart;

    private final Map<String, String> map = new HashMap<>();

    @FXML
    protected void onChooseDirButtonClick(ActionEvent event) {
        Stage stage = (Stage) ((Control) event.getSource()).getScene().getWindow();
        File file = new DirectoryChooser().showDialog(stage);
        if (file == null) return;
        String path = file.getAbsolutePath();
        directorySize = analyzer.calculateDirectorySize(Path.of(path));
        buildChart(path, stage);
    }

    private void buildChart(String path, Stage stage) {
        pieChart = new PieChart(pieChartData);
        TextField textField = new TextField(path);
        textField.setMinSize(400, 20);
        fillChart(path, textField);
        Button button = new Button(path);
        button.setOnAction(event -> fillChart(path, textField));
        button.setMinSize(150, 20);

        BorderPane pane = new BorderPane();
        HBox hBox =  new HBox();
        hBox.setAlignment(Pos.CENTER);
        hBox.setSpacing(20);
        hBox.setPadding(new Insets(10, 0, 0, 0));
        hBox.getChildren().addAll(button, textField);
        pane.setTop(hBox);
        pane.setCenter(pieChart);
        stage.setScene(new Scene(pane, 1980, 1300));
        stage.show();
    }

    private void fillChart(String path, TextField textField) {
        pieChartData.clear();
        textField.setText(path);
        pieChartData.addAll(directorySize
                .entrySet()
                .parallelStream()
                .filter(entry -> {
                    Path parent = Path.of(entry.getKey()).getParent();
                    return parent != null && parent.toString().equals(path);
                })
                .map(entry -> {
                    String name = entry.getKey() + "    " + parsingSize(entry.getValue());
                    map.put(name, entry.getKey());
                    return new PieChart.Data(name, entry.getValue());
                })
                .toList());
        pieChart.getData().forEach(data -> data.getNode()
                .addEventHandler(MouseEvent.MOUSE_PRESSED, event -> fillChart(map.get(data.getName()), textField)));
    }

    private String parsingSize(Long size) {
        if (size / 1024_000_000 > 0) return Math.round((0.0 + size) / 1024_000_000) + "Gb";
        if (size / 1024_000_0 > 0) return Math.round((0.0 + size) / 1024_000) + "Mb";
        if (size / 1024_000 > 0) return Math.round((0.0 + size) / 1024) + "Kb";
        return String.valueOf(size);
    }
}