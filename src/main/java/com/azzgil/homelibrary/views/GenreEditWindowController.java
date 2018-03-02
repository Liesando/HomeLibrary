package com.azzgil.homelibrary.views;

import com.azzgil.homelibrary.model.Genre;
import com.azzgil.homelibrary.utils.AlertUtil;
import com.azzgil.homelibrary.utils.DataUtils;
import com.azzgil.homelibrary.utils.HibernateUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
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
 * @version 1.0 2 March 2018
 */
public class GenreEditWindowController {
//    private static final String BTN_ADD_TEXT = "Добавить";
    private static final String BTN_EDIT_TEXT = "Сохранить";

    @FXML private TextField genreNameTF;
    @FXML private ComboBox<Genre> parentCategories;

    /*
     * Кнопка подтверждения в зависимости от режима выполняет
     * либо функцию добавления, либо функцию обновления
     * введёнными данными.
     */
    @FXML private Button confirmBtn;

    /* успешность завершения редактирования/создания */
    private boolean isSuccessful = false;

    /* режим редактирования? */
    private boolean editMode = false;

    /* редактируемый жанр (если в режиме создания, то создастся новый) */
    private Genre editableOrNew;
    private Stage primaryStage;

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

    /**
     * Успешна ли операция создания/редактирования?
     * @return true, если операция успешна, false - иначе
     */
    public boolean isSuccessful() {
        return isSuccessful;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    private void onConfirmBtn() {
        isSuccessful = !genreNameTF.getText().trim().isEmpty();
        if(!isSuccessful) {
            AlertUtil.showWarningAndWait(null, "Warning", "Пустое имя жанра",
                    "Пожалуйста, введите корректное (непустое) имя жанра");
            return;
        }

        Session session = HibernateUtil.openSession();

        if(!editMode) {
            editableOrNew = new Genre();
        }
        editableOrNew.setName(genreNameTF.getText().trim());
        Genre parent = parentCategories.getSelectionModel().getSelectedItem();
        if(parent == null) {
            editableOrNew.setParentId(null);
        } else {
            editableOrNew.setParentId(parent.getId());
        }

        session.beginTransaction();
        session.saveOrUpdate(editableOrNew);
        session.getTransaction().commit();
        session.close();
        primaryStage.close();
    }

    @FXML
    private void onCancelBtn() {
        // TODO: show dialog warning window first
        primaryStage.close();
    }

    /**
     * Переключает контроллер в режим редактирования
     *
     * @param genre Жанр, который надо редактировать
     */
    public void switchToEditMode(Genre genre) {
        editMode = true;
        editableOrNew = genre;
        genreNameTF.setText(genre.getName());
        prepareCategories(genre);
        parentCategories.getSelectionModel().select(genre.getParentGenre());
        confirmBtn.setText(BTN_EDIT_TEXT);
    }
}
