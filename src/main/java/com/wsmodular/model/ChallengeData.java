package com.wsmodular.model;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


import java.io.*;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class ChallengeData {
    File result;
    File data;
    public ChallengeData(String URL){
        data = FileSystems.getDefault().getPath(URL + "/data.csv").toFile();
        result = FileSystems.getDefault().getPath(URL + "/result.csv").toFile();
    }
    public int option_amount = 0;

    HashMap<String[], SimpleBooleanProperty> doneMap = new HashMap<>();
    ObservableList<String> categoriesArrayList = FXCollections.observableArrayList();
    ArrayList<String> optlist = new ArrayList<>();
    HashMap<String, ObservableList<String>> cat_opt_map = new HashMap<>();
    HashMap<String, SimpleStringProperty> opt_val_map = new HashMap<>();
    ObservableList<Result> resList = FXCollections.observableArrayList();

    public ArrayList<String> getOptlist() {
        return optlist;
    }
    public ObservableList<String> getCategoriesArrayList() {
        return categoriesArrayList;
    }
    public HashMap<String, ObservableList<String>> getCat_opt_map() {
        return cat_opt_map;
    }
    public HashMap<String, SimpleStringProperty> getOpt_val_map() {
        return opt_val_map;
    }
    public ObservableList<Result> getResList() {
        return resList;
    }
    public void writeFile() throws IOException {
        FileWriter fileWriter = new FileWriter(data, false);
        for (String category:
             categoriesArrayList) {
            for (Object option:
                 cat_opt_map.get(category)) {
                String text = category + ",";
                text += option.toString() + ",";
                text += opt_val_map.get(option).get() + "\n";
                fileWriter.write(text);
            }
        }
        fileWriter.close();
    }
    public void loadFile() throws FileNotFoundException {
        cat_opt_map.clear();
        categoriesArrayList.clear();
        optlist.clear();
        opt_val_map.clear();
        option_amount = 0;
        Scanner sc = new Scanner(data);
        while (sc.hasNextLine()){
            String current_line = sc.nextLine();
            String[] lines = current_line.split(",");
            this.option_amount++;
            String category = lines[0];
            String option = lines[1];
            SimpleStringProperty value = new SimpleStringProperty(lines[2]);
            if (!categoriesArrayList.contains(category)){
                categoriesArrayList.add(category);
                cat_opt_map.put(category, FXCollections.observableArrayList());
                cat_opt_map.get(category).add(option);
                optlist.add(option);
                opt_val_map.put(option, value);
            } else {
                cat_opt_map.get(category).add(option);
                opt_val_map.put(option, value);
                optlist.add(option);
            }

        }
        sc.close();
    }
    public void loadResult() throws IOException {
        resList.clear();
        Scanner sc = new Scanner(result);
        while (sc.hasNextLine()){
            String[] inputString = sc.nextLine().split(",");
            resList.add(new Result(inputString[0], inputString[1], inputString[2]));
        }
        sc.close();
    }
    public void addCat(String cat, String opt, SimpleStringProperty sio){
        this.option_amount++;
        if (!categoriesArrayList.contains(cat)){
            categoriesArrayList.add(cat);
            cat_opt_map.put(cat, FXCollections.observableArrayList());
        }
        cat_opt_map.get(cat).add(opt);
        opt_val_map.put(opt, sio);
        optlist.add(opt);
    }
    public void removeCat(String cat, String opt){
        this.option_amount--;
        optlist.remove(opt);
        cat_opt_map.get(cat).remove(opt);
        if (cat_opt_map.get(cat).isEmpty()){
            categoriesArrayList.remove(cat);
            cat_opt_map.remove(cat);
        }
        opt_val_map.remove(opt);
    }
    public void remAll(String cat){
        cat_opt_map.remove(cat);
        categoriesArrayList.remove(cat);
    }
    public void writeResults() throws IOException {

        FileWriter fileWriter = new FileWriter(result, false);
        StringBuilder text = new StringBuilder();
        for (Result result:
             resList) {
            text.append(result.getLocalDateObjectProperty()).append(",")
                    .append(result.getOption()).append(",")
                    .append(result.doneProperty().get()).append("\n");
        }
        fileWriter.write(text.toString());
        fileWriter.close();
    }

    public void addResult(String object){
        Result newResult = new Result(java.time.LocalDate.now().toString(),object,"false");
        resList.add(newResult);
    }
}

