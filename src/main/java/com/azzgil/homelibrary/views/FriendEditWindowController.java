package com.azzgil.homelibrary.views;

import com.azzgil.homelibrary.model.Friend;
import com.azzgil.homelibrary.utils.AlertUtil;
import com.azzgil.homelibrary.utils.DataUtils;
import com.azzgil.homelibrary.utils.GUIUtils;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 * FriendEditWindowController
 *
 * Контроллер окна создания/редактирования друга.
 *
 * @author Sergey Medelyan
 * @version 1.0 11 March 2018
 */
public class FriendEditWindowController extends EditWindowBaseController<Friend> {

    @FXML private TextField fioTF;
    @FXML private TextField phoneNumberTF;
    @FXML private TextField emailTF;
    @FXML private TextField vkIdTF;
    @FXML private TextArea commentaryTF;

    @Override
    protected boolean validateData() {
        StringBuilder errorMessage = new StringBuilder();

        if(fioTF.getText().trim().isEmpty()) {
            errorMessage.append("Не указано ФИО. Пожалуйста, укажите непустое ФИО.");
        }

        if(phoneNumberTF.getText().trim().isEmpty()) {
            errorMessage.append("Не указан номер телефона. Пожалуйста, укажите непустой номер.");
        }

        if(emailTF.getText().trim().isEmpty()) {
            errorMessage.append("");
        }

        if(vkIdTF.getText().trim().isEmpty()) {
            errorMessage.append("Не указан ID ВКонтакте. Пожалуйста, укажите непустой ID.");
        } else {
            try {
                Integer.parseInt(vkIdTF.getText().trim());
            } catch (NumberFormatException e) {
                errorMessage.append("");
            }
        }

        commentaryTF.setText(commentaryTF.getText().trim().isEmpty() ?
                GUIUtils.NO_COMMENT_REPLACER : commentaryTF.getText().trim());

        if(errorMessage.length() > 0) {
            AlertUtil.showWarningAndWait(primaryStage, "Ошибки ввода",
                    "Пожалуйста, исправьте следующие ошибки",
                    errorMessage.toString());
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

        if(!editMode) {
            editable = new Friend();
        }

        editable.setFio(fioTF.getText().trim());
        editable.setPhoneNumber(phoneNumberTF.getText().trim());
        editable.setEmail(emailTF.getText().trim());
        editable.setSocialNumber(Integer.parseInt(vkIdTF.getText().trim()));
        editable.setCommentary(commentaryTF.getText().trim());

        DataUtils.saveOrUpdate(editable);

        primaryStage.close();
    }

    @Override
    public void switchToEditMode(Friend editable) {
        super.switchToEditMode(editable);
        fioTF.setText(editable.getFio());
        phoneNumberTF.setText(editable.getPhoneNumber());
        emailTF.setText(editable.getEmail());
        vkIdTF.setText(Integer.toString(editable.getSocialNumber()));
        commentaryTF.setText(editable.getCommentary());
    }
}
