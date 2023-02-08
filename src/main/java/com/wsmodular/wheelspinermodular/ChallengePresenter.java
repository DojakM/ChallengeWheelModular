package com.wsmodular.wheelspinermodular;

import com.wsmodular.model.ChallengeData;
import com.wsmodular.model.Result;
import javafx.animation.RotateTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.*;
import java.io.*;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Random;
import java.util.Scanner;

public class ChallengePresenter {
    ChallengeController controller;
    ChallengeData challengeData;
    ObservableList<String> spunAlready = FXCollections.observableArrayList();
    Color[] colors = {
            Color.rgb(0, 165, 227),
            Color.rgb(141, 215, 191),
            Color.rgb(255, 150, 197),
            Color.rgb(255, 87, 104),
            Color.rgb(255, 191, 101),
            Color.rgb(108, 136, 196),
            Color.rgb(231, 117, 119),
            Color.rgb(242,212,204),
            Color.rgb(255, 216, 114),
            Color.rgb(253, 98, 56),
            Color.rgb(192, 87, 122),
            Color.rgb(0, 205, 169)

    };
    ChallengePresenter(ChallengeController controller, String file_path) throws IOException {
        challengeData = new ChallengeData(file_path);
        this.controller = controller;
        setup();
        // Just MenuItem Functiality
        controller.getResetMenuItem().setOnAction(event -> {
            try {
                setup();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        controller.getSaveMenuItem().setOnAction(event -> {
            try {
                challengeData.writeFile();
                challengeData.writeResults();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        controller.getAddOption().setOnAction(e -> addOption());
        controller.getRemOption().setOnAction(e -> remOption());
        controller.getAboutMenuItem().setOnAction(e -> {
            try {
                aboutMenu();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        controller.getPlusButton().setOnAction(e->{
            if (!controller.getTreeView().getSelectionModel().isEmpty()){
                String opt = controller.getTreeView().getSelectionModel().getSelectedItem().getValue();
                SimpleStringProperty sio = challengeData.getOpt_val_map().get(opt);
                Integer val_sio = Integer.parseInt(sio.get()) + 1;
                sio.setValue(String.valueOf(val_sio));
            }
        });
        controller.getMinusButton().setOnAction(e->{
            if (!controller.getTreeView().getSelectionModel().isEmpty()){
                String opt = controller.getTreeView().getSelectionModel().getSelectedItem().getValue();
                SimpleStringProperty sio = challengeData.getOpt_val_map().get(opt);
                Integer val_sio = Integer.parseInt(sio.get()) - 1;
                if (val_sio >= 0){sio.setValue(String.valueOf(val_sio));}
            }
        });
        controller.getDistButton().setOnAction(event -> {
            Stage stage = new Stage();
            final CategoryAxis categoryAxis = new CategoryAxis();
            final NumberAxis numberAxis = new NumberAxis();
            BarChart<String, Number> barChart = new BarChart<>(categoryAxis, numberAxis);
            barChart.setTitle("Distribution of done and undone tasks");
            numberAxis.setLabel("Number of occurences");
            XYChart.Series<String, Number> deries = new XYChart.Series<>();
            deries.setName("Done");
            XYChart.Series<String, Number> ueries = new XYChart.Series<>();
            ueries.setName("Undone");
            for (String option:
                challengeData.getOptlist()){
                int done = 0;
                int undone = 0;
                for (Result res:
                     challengeData.getResList()) {
                    if (res.getOption().equals(option)){
                        if (res.isDone()){
                            done++;
                        } else {
                            undone++;
                        }
                    }
                }
                deries.getData().add(new XYChart.Data<>(option, done));
                ueries.getData().add(new XYChart.Data<>(option, undone));
            }
            barChart.getData().addAll(ueries, deries);
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            Scene scene = new Scene(barChart, screenSize.getWidth(), screenSize.getHeight());
            stage.setScene(scene);
            stage.show();
        });

        // Add Listener to Wheel Tab, so it refreshes itself
        controller.getWheelTab().selectedProperty().addListener((o,v,e) ->{
            if (e){
                setUpWheelPage();
            }
        });
    }
    private void setup() throws IOException {
        //Loading data in
        challengeData.loadFile();
        challengeData.loadResult();
        //Setup Result Page
        setUpResults();
        setUpWheelPage();
        setUpOption();
    }
    public void setUpOption(){
        TreeItem<String> root = new TreeItem<>();
        for (int i = 0; i < challengeData.getCategoriesArrayList().size(); i++) {
            TreeItem<String> cat = new TreeItem<>(challengeData.getCategoriesArrayList().get(i));
            ObservableList<String> options =
                    challengeData.getCat_opt_map().get(challengeData.getCategoriesArrayList().get(i));
            for (String option : options) {
                TreeItem<String> opt = new TreeItem<>(option);
                cat.getChildren().add(opt);
            }
            root.getChildren().add(cat);
        }
        controller.getTreeView().getSelectionModel().selectedItemProperty().addListener((o, v, e) -> {
            if (e != null){
                if (e.isLeaf()){
                    controller.getValLabel().textProperty().unbind();
                    StringProperty value = challengeData.getOpt_val_map().get(e.getValue());
                    controller.getValLabel().textProperty().bind(value);
                }
                else {
                    ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
                    for (String ob:
                            challengeData.getCat_opt_map().get(e.getValue())) {
                        int abundance = 0;
                        for (Result res:
                             challengeData.getResList()) {
                            if (Objects.equals(res.getOption(), ob)){
                                abundance+=1;
                            }
                        }
                        pieData.add(new PieChart.Data(ob, abundance));
                    }
                    controller.getPieChart().setData(pieData);
                    controller.getPieChart().setTitle("Abundance of Selections");
                }
            }
        });
        controller.getTreeView().setRoot(root);
        controller.getTreeView().setShowRoot(false);

    }
    public void addOption(){
        Stage window = new Stage();
        window.setTitle("Add new Option");
        GridPane gridPane = new GridPane();
        Label category = new Label("Enter Category:\t");
        TextField cat = new TextField();

        Label option = new Label("Enter Option:\t");
        TextField opt = new TextField();
        Label value = new Label("Enter Value:\t");
        TextField val = new TextField();
        Button go = new Button("GO!");
        cat.textProperty().addListener(event-> {
            if (!cat.textProperty().get().equals("")&&!opt.textProperty().get().equals("")&&!val.textProperty().get().equals("")){
                go.setDisable(false);
            }
        });
        opt.textProperty().addListener((o, v, n)-> {
            if (!challengeData.getOptlist().contains(n)&&!n.equals("Already used")){
                if (!cat.textProperty().get().equals("")&&!opt.textProperty().get().equals("")&&!val.textProperty().get().equals("")){
                    go.setDisable(false);
                }
            } else {
                opt.setText("Already used");
            }

        });
        val.textProperty().addListener(event-> {
            if (!cat.textProperty().get().equals("")&&!opt.textProperty().get().equals("")&&!val.textProperty().get().equals("")){
                go.setDisable(false);
            }
        });
        go.setDisable(true);
        val.setEditable(false);
        val.setText("1");
        val.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                val.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        go.setOnAction(event -> {
            boolean found = false;
            for (TreeItem<String> treeCell:
                    controller.getTreeView().getRoot().getChildren()) {
                if (treeCell.getValue().equals(cat.textProperty().getValue())){
                    treeCell.getChildren().add(new TreeItem<>(opt.textProperty().get()));
                    challengeData.addCat(cat.textProperty().get(), opt.textProperty().get(),
                            new SimpleStringProperty(val.textProperty().getValue()));
                    found = true;
                    break;
                }
            }
            if (!found){
                TreeItem<String> stringTreeItem = new TreeItem<>(cat.textProperty().get());
                stringTreeItem.getChildren().add(new TreeItem<>(opt.textProperty().get()));
                challengeData.addCat(cat.textProperty().get(), opt.textProperty().get(),
                        new SimpleStringProperty(val.textProperty().getValue()));
                controller.getTreeView().getRoot().getChildren().add(stringTreeItem);
            }
            window.close();
        });
        gridPane.add(category, 0, 0);
        gridPane.add(cat, 1, 0);
        gridPane.add(option, 0, 1);
        gridPane.add(opt, 1, 1);
        gridPane.add(value, 0, 2);
        gridPane.add(val, 1, 2);
        gridPane.add(go, 2, 1);
        Scene scene = new Scene(gridPane, 300, 90);
        window.setScene(scene);
        window.show();
    }
    public void remOption(){
        Stage window = new Stage();
        window.setTitle("Are you sure?");
        VBox vBox = new VBox();
        vBox.alignmentProperty().setValue(Pos.CENTER);
        vBox.getChildren().add(new Label("Remove: " +
                controller.getTreeView().getSelectionModel().getSelectedItems().get(0).getValue() +
                "?"));
        Scene scene = new Scene(vBox, 150, 40);
        Button remButton = new Button("REMOVE");
        remButton.setOnAction(event -> {
            boolean isFound = false;
            for (TreeItem<String> categoryItem:
                    controller.getTreeView().getRoot().getChildren()) {
                if (isFound){
                    break;
                }
                if (!categoryItem.getValue().equals(
                        controller.getTreeView().getSelectionModel().getSelectedItems().get(0).getValue())){
                    for (TreeItem<String> option : categoryItem.getChildren()) {
                        if (option.getValue().equals(
                                controller.getTreeView().getSelectionModel().getSelectedItems().get(0).getValue())){
                            categoryItem.getChildren().remove(option);
                            challengeData.removeCat(categoryItem.getValue(), option.getValue());
                            if (categoryItem.getChildren().isEmpty()){
                                controller.getTreeView().getRoot().getChildren().remove(categoryItem);
                            }
                            isFound = true;
                            break;
                        }
                    }
                } else if (categoryItem.getValue().equals(
                        controller.getTreeView().getSelectionModel().getSelectedItems().get(0).getValue())){
                    challengeData.remAll(categoryItem.getValue());
                    controller.getTreeView().getRoot().getChildren().remove(categoryItem);
                    break;
                }
            }
            window.close();
        });
        vBox.getChildren().add(remButton);
        window.setScene(scene);
        window.show();

    }
    public void aboutMenu() throws IOException {
        Stage about = new Stage();
        about.setTitle("Help");
        Pane pane = new Pane();
        StringBuilder stringBuilder = new StringBuilder();
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("about.txt");
        assert is != null;
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        while(br.readLine() != null){
            stringBuilder.append(br.readLine());
            stringBuilder.append("\n");
        }
        Label label = new Label(stringBuilder.toString());
        label.setWrapText(true);
        pane.getChildren().add(label);
        label.setMaxWidth(600);
        Scene scene = new Scene(pane, 600, 250);
        about.setScene(scene);
        about.show();
    }
    public void setUpResults(){
        TableColumn<Result, LocalDate> dateColumn = new TableColumn<>("Date");
        TableColumn<Result, String> optionColumn = new TableColumn<>("Option");
        TableColumn<Result, Boolean> doneColumn = new TableColumn<>("is Done");
        dateColumn.setCellValueFactory(
                new PropertyValueFactory<>("localDateObjectProperty")
        );
        optionColumn.setCellValueFactory(
                new PropertyValueFactory<>("option")
        );
        doneColumn.setCellValueFactory(
                new PropertyValueFactory<>("Done")
        );
        FilteredList<Result> filteredList = new FilteredList<>(challengeData.getResList(), p -> true);
        controller.getStartDate().setOnAction(e-> filteredList.setPredicate(result -> {
            if (controller.getStartDate().getValue() == null){
                return true;
            } else return !result.getLocalDateObjectProperty().isBefore(controller.getStartDate().getValue());
        }));
        controller.getEndDate().setOnAction(e-> filteredList.setPredicate(result -> {
            if (controller.getEndDate().getValue() == null){
                return true;
            } else return !result.getLocalDateObjectProperty().isAfter(controller.getEndDate().getValue());
        }));
        controller.getSwitchField().setOnAction(e->{
            if(controller.getSwitchField().isSelected()){
                controller.getSwitchField().setText("True");
                filteredList.setPredicate(Result::isDone);
            } else {
                controller.getSwitchField().setText("False");
                filteredList.setPredicate(result -> !result.isDone());
            }
        });
        controller.getResTableView().setOnKeyPressed(event -> {
            if (Objects.equals(event.getText(), "d")){
                boolean now = controller.getResTableView().getSelectionModel().getSelectedItem().isDone();
                controller.getResTableView().getSelectionModel().getSelectedItem().setIsDone(!now);
                controller.getResTableView().refresh();
            }
        });
        controller.getPunButton().setOnAction(e->{
            Stage stage = new Stage();
            VBox vBox = new VBox();
            Slider slider = new Slider(0, 100, 50);
            slider.setMajorTickUnit(5);
            slider.setMinorTickCount(1);
            slider.setSnapToTicks(true);
            slider.setShowTickMarks(true);
            slider.setShowTickLabels(true);
            Label rat = new Label();
            Label pun = new Label();
            Label missing = new Label();
            slider.valueProperty().addListener( ev -> {
                rat.setText(String.valueOf(slider.getValue()));
                double all = 0;
                double finished = 0;
                for (Result res:
                     challengeData.getResList()) {
                    all+=1;
                    if (res.isDone()){
                        finished+=1;
                    }
                }
                double ratio = finished/all*100.0;
                if (ratio<slider.getValue()){
                    pun.setText("Punishment!");
                } else {
                    pun.setText("No Punishment");
                }
                double threshhold = Math.floor(all * slider.getValue()/100)+1;
                if (threshhold>finished){
                    missing.setText("You have to finish at least " +
                            (threshhold - finished) +
                            " more challenges!");
                } else {
                    missing.setText("You are in the clear! (" + (finished-threshhold) + ")");
                }
            });
            vBox.getChildren().add(rat);
            vBox.getChildren().add(slider);
            vBox.getChildren().add(pun);
            vBox.getChildren().add(missing);
            vBox.setAlignment(Pos.CENTER);
            Scene scene = new Scene(vBox, 300, 150);
            stage.setScene(scene);
            stage.show();
        });
        SortedList<Result> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(controller.getResTableView().comparatorProperty());
        dateColumn.setMinWidth(200);
        optionColumn.setMinWidth(200);
        doneColumn.setMinWidth(200);
        controller.getResTableView().getColumns().add(dateColumn);
        controller.getResTableView().getColumns().add(optionColumn);
        controller.getResTableView().getColumns().add(doneColumn);
        controller.getResTableView().setItems(sortedList);
    }
    public void setUpWheelPage(){
        if (challengeData.getCategoriesArrayList().isEmpty()){
            controller.getWheelPane().getChildren().add(new Label("No options yet"));
        } else{
            controller.getWheelPane().getChildren().clear();
            controller.getWheelBar().getItems().clear();
            for (String category:
                    challengeData.getCategoriesArrayList()) {
                Button button = new Button(category);
                ObservableList<String> elements = challengeData.getCat_opt_map().get(category);
                button.setOnAction(event -> setUpWheel(category, elements));
                controller.getWheelBar().getItems().add(button);
            }
        }

    }
    public void setUpWheel(String category, ObservableList<String> strings){
        if (!spunAlready.contains(category)){
            controller.getWheelPane().getChildren().clear();
            FXCollections.shuffle(strings);
            Group texts = new Group();
            Group wheelSlices = new Group();
            Group wheelBody = new Group();
            Polygon polygon = createTriangle(
                    new Point2D(controller.getWheelPane().getWidth()/4-25, controller.getWheelPane().getHeight()/2-12.5),
                    new Point2D(controller.getWheelPane().getWidth()/4-25, controller.getWheelPane().getHeight()/2+12.5),
                    new Point2D(controller.getWheelPane().getWidth()/4, controller.getWheelPane().getHeight()/2),
                    Color.FIREBRICK
                    );
            for (int i = 0; strings.size()>i; i++){
                Text text = setTexts(strings.size(), i, strings.get(i));
                texts.getChildren().add(text);
                wheelBody.getChildren().add(drawSliceBody(strings.size(), i));
                wheelSlices.getChildren().add(drawSlice(strings.size(), i));

            }
            wheelSlices.setRotate(90.0+(180.0/strings.size()));
            Group all_comp = new Group(wheelSlices,  wheelBody, texts);
            controller.getWheelPane().getChildren().add(all_comp);
            controller.getWheelPane().getChildren().add(polygon);
            controller.getWheelPane().getChildren().get(0).setOnMouseClicked(event -> {
                Random random = new Random();
                double rot = random.nextDouble(360, 3600);
                RotateTransition rt = new RotateTransition(
                        new Duration(rot),
                        all_comp);
                rt.setByAngle(rot);
                rt.setCycleCount(1);
                rt.setAutoReverse(false);
                rt.play();
                double new_angle = (rot%360)-90;
                int value = (int) Math.round(new_angle/(360.0/strings.size()));
                if (value == -1){
                    value = 0;
                }
                String option = challengeData.getCat_opt_map().get(category).get(value);
                challengeData.addResult(option);
                spunAlready.add(category);
                for (Node but:
                     controller.getWheelBar().getItems()) {
                    if(((Button) but).getText().equals(category)){
                        but.setDisable(true);
                        break;
                    }
                }
                controller.getWheelPane().getChildren().get(0).setDisable(true);
            });
        } else {
            Stage stage = new Stage();
            VBox vbox = new VBox();
            Label label = new Label("Spin again?");
            Button yes = new Button("Yes");
            Button no = new Button("No");
            yes.setOnAction(event -> {
                spunAlready.remove(category);
                setUpWheel(category, strings);
                stage.close();
            });
            no.setOnAction(event -> stage.close());
            vbox.getChildren().add(label);
            vbox.getChildren().add(yes);
            vbox.getChildren().add(no);
            Scene scene = new Scene(vbox);
            stage.setScene(scene);
            stage.show();
        }
    }
    public Text setTexts(int numberSlices, int sliceId, String label){
        double radius = controller.getWheelPane().getHeight()/4;
        double angle = (360.0/numberSlices)*sliceId;
        double x = radius*Math.sin(Math.toRadians(angle));
        double y = radius*Math.cos(Math.toRadians(angle));
        Text text = new Text(label);
        text.setX(x+controller.getWheelPane().getWidth()/2-30);
        text.setY(y+controller.getWheelPane().getHeight()/2);
        text.setBoundsType(TextBoundsType.VISUAL);
        text.setFont(new Font(20));
        if (sliceId == 0){
            text.setRotate(270);
        } else {
            text.setRotate(270-(360.0/numberSlices)*sliceId);
        }
        return text;
    }
    public Polygon createTriangle(Point2D p1, Point2D p2, Point2D p3, Color color){
        Point2D centre = p1.midpoint(p2).midpoint(p3);
        Point2D p1Corrected = p1.subtract(centre);
        Point2D p2Corrected = p2.subtract(centre);
        Point2D p3Corrected = p3.subtract(centre);
        Polygon polygon = new Polygon(
                p1Corrected.getX(), p1Corrected.getY(),
                p2Corrected.getX(), p2Corrected.getY(),
                p3Corrected.getX(), p3Corrected.getY()
        );
        polygon.setLayoutX(centre.getX());
        polygon.setLayoutY(centre.getY());
        polygon.setFill(color);
        return polygon;
    }
    public Group drawSlice(int numSlices, int sliceId){
        double radius = controller.getWheelPane().getHeight()/2;
        double angle = (360.0/(numSlices))*sliceId;
        Group group = new Group();
        Arc arc = new Arc(
                controller.getWheelPane().getWidth()/2,
                controller.getWheelPane().getHeight()/2,
                radius,
                radius,
                angle,
                360.0/numSlices);
        arc.setFill(colors[sliceId]);
        group.getChildren().add(arc);
        return group;
    }
    public Group drawSliceBody(int numSlices, int sliceId){
        Group group = new Group();
        double radius = controller.getWheelPane().getHeight()/2;
        double angle_mid = (360.0/(numSlices))*sliceId;
        double angle = angle_mid - 180.0/numSlices;
        double sec_angle = angle_mid + 180.0/numSlices;
        double x = radius*Math.sin(Math.toRadians(angle));
        double y = radius*Math.cos(Math.toRadians(angle));
        double sec_x = radius*Math.sin(Math.toRadians(sec_angle));
        double sec_y = radius*Math.cos(Math.toRadians(sec_angle));
        Polygon slice = createTriangle(
                new Point2D(x+controller.getWheelPane().getWidth()/2, y+controller.getWheelPane().getHeight()/2),
                new Point2D(sec_x+controller.getWheelPane().getWidth()/2, sec_y+controller.getWheelPane().getHeight()/2),
                new Point2D(controller.getWheelPane().getWidth()/2, controller.getWheelPane().getHeight()/2),
                colors[sliceId]
        );
        group.getChildren().add(slice);
        return group;
    }

}

