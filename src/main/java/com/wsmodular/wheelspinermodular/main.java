package com.wsmodular.wheelspinermodular;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        var View = new View(); // create view

        var controller = new ChallengeController();
        var presenter = new ChallengePresenter(View.getController(), this.getParameters().getNamed().get("files"));
        // set the stage and show
        stage.setScene(new Scene(View.getRoot()));
        stage.setTitle("Challenge Wheel");
        stage.show();
    }
}