package com.wsmodular.wheelspinermodular;

import com.wsmodular.model.Result;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;

public class ChallengeController {

    @FXML
    private MenuItem aboutMenuItem;

    @FXML
    private Button addOption;

    @FXML
    private DatePicker endDate;

    @FXML
    private Button minusButton;

    @FXML
    private PieChart pieChart;

    @FXML
    private Button plusButton;

    @FXML
    private Button remOption;

    @FXML
    private TableView<Result> resTableView;

    @FXML
    private MenuItem resetMenuItem;

    @FXML
    private MenuItem saveMenuItem;

    @FXML
    private TextField searchField;

    @FXML
    private DatePicker startDate;

    @FXML
    private ListView<String> statsView;

    @FXML
    private ToggleButton switchField;

    @FXML
    private TreeView<String> treeView;

    @FXML
    private Label valLabel;

    @FXML
    private ToolBar wheelBar;

    @FXML
    private Pane wheelPane;

    @FXML
    private Tab wheelsTab;
    @FXML
    private Button punButton;
    @FXML
    private Button distButton;

    public Tab getWheelsTab() {
        return wheelsTab;
    }

    public Button getPunButton() {
        return punButton;
    }

    public Button getDistButton() {
        return distButton;
    }

    public Tab getWheelTab() {
        return wheelsTab;
    }

    public MenuItem getAboutMenuItem() {
        return aboutMenuItem;
    }

    public Button getAddOption() {
        return addOption;
    }

    public DatePicker getEndDate() {
        return endDate;
    }

    public Button getMinusButton() {
        return minusButton;
    }

    public PieChart getPieChart() {
        return pieChart;
    }

    public Button getPlusButton() {
        return plusButton;
    }

    public Button getRemOption() {
        return remOption;
    }

    public TableView<Result> getResTableView() {
        return resTableView;
    }

    public MenuItem getResetMenuItem() {
        return resetMenuItem;
    }

    public MenuItem getSaveMenuItem() {
        return saveMenuItem;
    }

    public TextField getSearchField() {
        return searchField;
    }

    public DatePicker getStartDate() {
        return startDate;
    }

    public ToggleButton getSwitchField() {
        return switchField;
    }

    public TreeView<String> getTreeView() {
        return treeView;
    }

    public Label getValLabel() {
        return valLabel;
    }

    public ToolBar getWheelBar() {
        return wheelBar;
    }

    public Pane getWheelPane() {
        return wheelPane;
    }
}
