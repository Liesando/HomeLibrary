package com.azzgil.homelibrary.views;

import com.azzgil.homelibrary.HomeLibrary;
import com.azzgil.homelibrary.model.Genre;
import com.azzgil.homelibrary.utils.AlertUtil;
import com.azzgil.homelibrary.utils.DataUtils;
import com.azzgil.homelibrary.utils.HibernateUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;


/**
 * GenreEditWindowController
 *
 * Контроллер окна создания/редактирования жанра.
 *
 * @author Sergey Medelyan
 * @version 1.1 4 March 2018
 */
public class GenreEditWindowController extends EditWindowBaseController<Genre> {

    @FXML private TextField genreNameTF;
    @FXML private ComboBox<Genre> parentCategories;

    @FXML
    private void initialize() {

        // создаём фабрику, которая в качестве имени ячейки
        // вместо Genre.toString() будет выводить полное имя
        // жанра (Genre.getFullName())
        Callback<ListView<Genre>, ListCell<Genre>> cellFactory =
                new Callback<>() {
            @Override
            public ListCell<Genre> call(ListView<Genre> param) {
                return new ListCell<>() {
                    @Override
                    public void updateItem(Genre item, boolean empty) {
                        super.updateItem(item, empty);
                        if(item != null) {
                            setText(item.getFullName());
                        } else {
                            setText("без категории");
                        }
                    }
                };
            }
        };

        parentCategories.setCellFactory(cellFactory);
        prepareCategories(null);
    }

    /**
     * Перенаполняет список категорий в комбобоксе, исключая из
     * возможных категорий порождённые от <i>exclude</i> и
     * его самого, так как нельзя жанру назначить категорию
     * самого себя (то есть, сделать его поджанром самого себя).
     *
     * @param exclude Исключаемый жанр (может быть null)
     */
    private void prepareCategories(Genre exclude) {
        ArrayList<Genre> list = new ArrayList<>();
        list.add(null);
        list.addAll(Arrays.asList(DataUtils.fetchAllGenres()));

        if(exclude != null) {
            list.removeIf(g -> {
                if(g != null) {
                    Genre current = g;
                    boolean result = false;
                    while(current != null && !result) {
                        result = current.getId() == exclude.getId();
                        current = current.getParentGenre();
                    }
                    return result;
                }
                else return false;
            });
        }

        parentCategories.setItems(FXCollections.
                observableList(list).sorted(Comparator.comparing(
                        e -> e == null ? "": e.getFullName())));
    }


    @FXML
    private void onConfirmBtn() {
        isSuccessful = !genreNameTF.getText().trim().isEmpty();
        if(!isSuccessful) {
            AlertUtil.showWarningAndWait("Warning", "Пустое имя жанра",
                    "Пожалуйста, введите корректное (непустое) имя жанра");
            return;
        }

        Session session = HibernateUtil.openSession();

        if(!editMode) {
            editable = new Genre();
        }
        editable.setName(genreNameTF.getText().trim());
        Genre parent = parentCategories.getSelectionModel().getSelectedItem();
        if(parent == null) {
            editable.setParentId(null);
        } else {
            editable.setParentId(parent.getId());
        }

        session.beginTransaction();
        session.saveOrUpdate(editable);
        session.getTransaction().commit();
        session.close();
        primaryStage.close();

        // произошли изменения в уже существующих жанрах,
        // так что обновим книжечки
        if(editMode) {
            HomeLibrary.refreshBooks();
        }
    }

    /**
     * Переключает контроллер в режим редактирования
     *
     * @param editable Жанр, который надо редактировать
     */
    @Override
    public void switchToEditMode(Genre editable) {
        super.switchToEditMode(editable);
        genreNameTF.setText(editable.getName());
        prepareCategories(editable);
        parentCategories.getSelectionModel().select(editable.getParentGenre());
    }
}
