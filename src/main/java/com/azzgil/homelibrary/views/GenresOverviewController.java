package com.azzgil.homelibrary.views;

import com.azzgil.homelibrary.model.Genre;
import com.azzgil.homelibrary.utils.AlertUtil;
import com.azzgil.homelibrary.utils.DataUtils;
import com.azzgil.homelibrary.utils.FXMLUtils;
import com.azzgil.homelibrary.utils.HibernateUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Dialog;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.hibernate.Session;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;


/**
 * GenresOverviewController
 *
 * Контроллер для секции обзора жанров. Обрабатывает события
 * интерфейса
 *
 * @author Sergey Medelyan
 * @version 1.0 1 March 2018
 */
public class GenresOverviewController {

    @FXML
    private TreeView<String> treeView;

    @FXML
    private void initialize() {
        refreshGenres();
    }

    /** Обновляет список жанров */
    public void refreshGenres() {
        generateTree(DataUtils.fetchAllGenres());
    }

    /**
     * Генерирует дерево жанров по переданному массиву жанров.
     *
     * @param genres Массив жанров
     */
    private void generateTree(Genre[] genres) {
        TreeItem<String>[] items = new TreeItem[genres.length + 1];
        items[genres.length] = new TreeItem<>("Жанры");
        for (int i = 0; i < genres.length; ++i) {
            items[i] = new TreeItem<>(genres[i].getName());
        }

        int indexOfParent;

        // next one is just to obtain access to indexOf() method :\
        List lgenres = Arrays.asList(genres);
        for(int i = 0; i < genres.length; ++i) {
            if(genres[i].getParentGenre() != null) {
                indexOfParent =lgenres.indexOf(genres[i].getParentGenre());
                items[indexOfParent].getChildren().add(items[i]);
            } else {
                items[genres.length].getChildren().add(items[i]);
            }
        }

        treeView.setRoot(items[genres.length]);
    }

    @FXML
    private void onAddGenreBtnClick() {
        try {
            FXMLLoader loader = FXMLUtils.configureLoaderFor("views/GenreEditWindow.fxml");

            Stage stage = new Stage(StageStyle.UTILITY);
            stage.setScene(new Scene(loader.load()));
            GenreEditWindowController controller = loader.getController();
            controller.setPrimaryStage(stage);
            stage.showAndWait();

            if(controller.isSuccessful()) {
                refreshGenres();
            }

        } catch (IOException e) {
            AlertUtil.showDataCorruptionErrorAndWait(null, "views/GenreEditWindow.fxml");
            e.printStackTrace();
            Platform.exit();
        }
    }
}
