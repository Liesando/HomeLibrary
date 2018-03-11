package com.azzgil.homelibrary.views;

import com.azzgil.homelibrary.HomeLibrary;
import com.azzgil.homelibrary.model.Genre;
import com.azzgil.homelibrary.utils.AlertUtil;
import com.azzgil.homelibrary.utils.DataUtils;
import com.azzgil.homelibrary.utils.GUIUtils;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.hibernate.exception.ConstraintViolationException;

import javax.persistence.PersistenceException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

/**
 * GenreEditWindowController
 *
 * Контроллер окна создания/редактирования жанра.
 *
 * @author Sergey Medelyan
 * @version 1.4 11 March 2018
 */
public class GenreEditWindowController extends EditWindowBaseController<Genre> {

    @FXML private TextField genreNameTF;
    @FXML private ComboBox<Genre> parentCategories;

    @FXML
    private void initialize() {
        parentCategories.setCellFactory(GUIUtils.STD_GENRE_CELL_FACTORY);
        prepareCategories(null);
        editable = new Genre();
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
        list.addAll(Arrays.asList(HomeLibrary.getAllGenres()));

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

    @Override
    protected boolean validateData() {

        // TODO: посмотреть, что больше жанров с таким именем

        if(genreNameTF.getText().trim().isEmpty()) {
            AlertUtil.showWarningAndWait("Warning", "Пустое имя жанра",
                    "Пожалуйста, введите корректное (непустое) имя жанра");
            return false;
        }

        return true;
    }

    @FXML
    private void onConfirmBtn() {
        isSuccessful = validateData();
        if(!isSuccessful) {
            return;
        }

        editable.setName(genreNameTF.getText().trim());
        Genre parent = parentCategories.getSelectionModel().getSelectedItem();
        if(parent == null) {
            editable.setParentId(null);
        } else {
            editable.setParentId(parent.getId());
        }

        try {
            DataUtils.saveOrUpdate(editable);
            primaryStage.close();
        } catch (PersistenceException e) {
            if(e.getCause() instanceof ConstraintViolationException) {
                DataUtils.rollback();

                // если мы доходим сюда, значит, был вызван Session.commit()
                // он задал ненулевое значение editable.id, переведя editable из
                // transient state в persistent state.
                // но, даже откатив все изменения транзакции, мы всё ещё имеем
                // editable.id != 0. То есть в следующий раз при вызове saveOrUpdate
                // хибернейт попытается ОБНОВИТЬ данные, которых нет, т.к.
                // editable - persistent-объект. Таким образом здесь мы, можно сказать,
                // "дооткатываем" изменения нашего объекта.
                if(!editMode) {
                    editable.setId(0);
                }

                AlertUtil.showErrorAndWait(primaryStage,"Ошибка", "Жанр с таким именем уже существует",
                        "Пожалуйста, выберите другое имя для жанра");

            } else {
                throw e;
            }
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
