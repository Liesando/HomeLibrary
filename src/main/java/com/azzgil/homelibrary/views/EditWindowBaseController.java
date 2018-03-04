package com.azzgil.homelibrary.views;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;


/**
 * EditWindowBaseController
 *
 * Базовый класс контроллера для окон, отвечающих за
 * создание и/или редактирование экземпляров сущностей.
 *
 * @author Sergey Medelyan
 * @version 1.0 4 March 2018
 * @param <T> Тип создаваемой/редактируемой сущности
 */
public class EditWindowBaseController<T> {

    // текст кнопки в режиме редактирования
    private static final String CONFIRM_BTN_EDIT_MODE_TEXT = "Сохранить";

    @FXML
    public Button confirmBtn;

    /** Редактируемый/создаваемый объект */
    protected T editable;
    protected Stage primaryStage;

    /** Успешность завершения редактирования */
    protected boolean isSuccessful = false;

    /** Включён ли режим редактирования? */
    protected boolean editMode = false;

    public boolean isSuccessful() {
        return isSuccessful;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    /**
     * Переключает контроллер в режим редактирования
     *
     * @param editable Редактируемый объект
     */
    public void switchToEditMode(T editable) {
        editMode = true;
        this.editable = editable;
        confirmBtn.setText(CONFIRM_BTN_EDIT_MODE_TEXT);
    }

    @FXML
    protected void onCancelBtn() {
        // TODO: show dialog warning window first
        primaryStage.close();
    }
}
