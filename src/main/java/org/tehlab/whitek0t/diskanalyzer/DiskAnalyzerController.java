package org.tehlab.whitek0t.diskanalyzer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
public class DiskAnalyzerController {

    private final Analyzer analyzer = new Analyzer();
    public TextField currentPath;
    public Button undo;
    private Map<String, Long> directorySize = new HashMap<>();
    private final ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
    @FXML
    public PieChart pieChart;

    private final Map<String, String> map = new HashMap<>();

    @FXML
    protected void onChooseDirButtonClick(ActionEvent event) {
        Stage stage = (Stage) ((Control) event.getSource()).getScene().getWindow();
        File file = new DirectoryChooser().showDialog(stage);
        if (file == null) return;
        String path = file.getAbsolutePath();
        directorySize = analyzer.calculateDirectorySize(Path.of(path));
        buildChart(path);
    }

    private void buildChart(String path) {
        pieChart.setData(pieChartData);
        fillChart(path, currentPath);
        undo.setOnAction(event -> fillChart(path, currentPath));
        undo.setText(path);
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
        if (size / 1_073_741_824 > 0) return Math.round((0.0 + size) / 1_073_741_824) + "Gb";
        if (size / 10_485_760 > 0) return Math.round((0.0 + size) / 1_048_576) + "Mb";
        if (size / 1_048_576 > 0) return Math.round((0.0 + size) / 1024) + "Kb";
        return String.valueOf(size);
    }
}