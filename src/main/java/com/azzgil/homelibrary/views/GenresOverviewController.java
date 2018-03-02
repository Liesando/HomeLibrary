package com.azzgil.homelibrary.views;

import com.azzgil.homelibrary.ICUDController;
import com.azzgil.homelibrary.model.Genre;
import com.azzgil.homelibrary.utils.AlertUtil;
import com.azzgil.homelibrary.utils.DataUtils;
import com.azzgil.homelibrary.utils.FXMLUtils;
import com.azzgil.homelibrary.utils.HibernateUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import java.io.IOException;
import java.util.*;


/**
 * GenresOverviewController
 *
 * Контроллер для секции обзора жанров. Обрабатывает события
 * интерфейса
 *
 * @author Sergey Medelyan
 * @version 1.1 2 March 2018
 */
public class GenresOverviewController implements ICUDController {
    private static final String EMPTY_SELECTION_ERROR =
            "Пожалуйста, сначала выберите жанр из списка";

    @FXML
    private TreeView<Genre> treeView;
    private Genre[] genres;

    @FXML
    private void initialize() {
        refreshGenres();
    }

    /** Обновляет список жанров */
    public void refreshGenres() {
        genres = DataUtils.fetchAllGenres();
        generateTree(genres);
        treeView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    /**
     * Генерирует дерево жанров по переданному массиву жанров.
     *
     * TODO: genres sort
     * @param genres Массив жанров
     */
    private void generateTree(Genre[] genres) {
        TreeItem<Genre>[] items = new TreeItem[genres.length + 1];

        // пустой жанр-корень
        items[genres.length] = new TreeItem<Genre>(new Genre());
        for (int i = 0; i < genres.length; ++i) {
            items[i] = new TreeItem<Genre>(genres[i]);
        }

        int indexOfParent;

        // next one is just to obtain access to indexOf() method :\
        List lgenres = Arrays.asList(genres);

        // здесь мы хотим создать между узлами дерева такую же
        // иерархию, в какой состоят жанры и их поджанры
        for(int i = 0; i < genres.length; ++i) {
            items[i].setExpanded(true);
            if(genres[i].getParentGenre() != null) {
                indexOfParent = lgenres.indexOf(genres[i].getParentGenre());
                items[indexOfParent].getChildren().add(items[i]);
            } else {
                items[genres.length].getChildren().add(items[i]);
            }
        }

//        на данный момент бесполезная строка, но использовалась при попытке
//        прикрутить сортировку
//        TreeItem<Genre> root = items[genres.length];

//        the next one doesn't make any effect (obviously)
//        Arrays.sort(items, Comparator.comparing(i -> i.getValue().getFullName()));
        treeView.setRoot(items[genres.length]);
        treeView.setShowRoot(false);
        items[genres.length].setExpanded(true);
    }

    @Override
    public void create() {
        showGenreEditWindow(false);
    }

    @Override
    public boolean validateUpdate() {
        return treeView.getSelectionModel().getSelectedItem() != null &&
            treeView.getSelectionModel().getSelectedItem().getValue() != null;
    }

    @Override
    public void update() {
        showGenreEditWindow(true);
    }

    /**
     * Создаёт окно редактирования жанра в одном из двух режимов:
     * создание жанра (editMode = false) и редактирование жанра
     * (editMode = false).
     *
     * @param editMode включить режим редактирования?
     */
    private void showGenreEditWindow(boolean editMode) {
        try {
            FXMLLoader loader = FXMLUtils.configureLoaderFor("views/GenreEditWindow.fxml");

            Stage stage = new Stage(StageStyle.UTILITY);
            stage.setScene(new Scene(loader.load()));
            GenreEditWindowController controller = loader.getController();
            controller.setPrimaryStage(stage);

            if(editMode) {
                if (!validateUpdate()) {
                    AlertUtil.showEmptySelectionErrorAndWait(null,
                            EMPTY_SELECTION_ERROR);
                    return;
                }
                controller.switchToEditMode(treeView.getSelectionModel()
                        .getSelectedItem().getValue());
            }
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

    @Override
    public boolean validateDelete() {
        return validateUpdate();
    }

    @Override
    public void delete() {
        if(!validateDelete()) {
            AlertUtil.showEmptySelectionErrorAndWait(null, EMPTY_SELECTION_ERROR);
            return;
        }

        Session session = HibernateUtil.openSession();
        try {
            session.beginTransaction();
            for (TreeItem<Genre> t:
                    treeView.getSelectionModel().getSelectedItems()) {
                session.delete(t.getValue());
            }
            session.getTransaction().commit();
            refreshGenres();
        } catch (HibernateException e) {
            // TODO: Display message that something's happened
            session.getTransaction().rollback();
        }
        finally {
            session.close();
        }
    }

    @FXML
    private void onAddGenreBtn() {
        create();
    }
}
