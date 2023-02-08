module com.wsmodular.wheelspinermodular {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.desktop;

    opens com.wsmodular.wheelspinermodular to javafx.fxml;
    opens com.wsmodular.model to javafx.fxml;

    exports com.wsmodular.wheelspinermodular;
    exports com.wsmodular.model;
}