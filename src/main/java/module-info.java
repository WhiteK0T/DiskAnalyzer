module org.tehlab.whitek0t.diskanalyzer {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.tehlab.whitek0t.diskanalyzer to javafx.fxml;
    exports org.tehlab.whitek0t.diskanalyzer;
}