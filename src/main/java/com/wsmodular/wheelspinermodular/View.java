package com.wsmodular.wheelspinermodular;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class View {
    private final ChallengeController controller;
    private final Parent root;

    public View() throws IOException {
        try (var ins = Objects.requireNonNull(getClass().getResource(
                "main.fxml")
        ).openStream()) {
            var fxmlLoader = new FXMLLoader();
            root = fxmlLoader.load(ins);
            controller = fxmlLoader.getController();
        }
    }

    public ChallengeController getController() {
        return controller;
    }

    public Parent getRoot() {
        return root;
    }
}
