package com.azzgil.homelibrary.views;

import com.azzgil.homelibrary.model.PublishingHouse;
import com.azzgil.homelibrary.utils.AlertUtil;
import com.azzgil.homelibrary.utils.DataUtils;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

/**
 * PubHouseEditWindowController
 *
 * Контроллер окна создания/редактирования издательства
 *
 * @author Sergey Medelyan
 * @version 1.2 11 March 2018 */
public class PubHouseEditWindowController
        extends EditWindowBaseController<PublishingHouse> {

    @FXML private TextField pubHouseNameTF;

    @FXML
    private void initialize() {}

    @Override
    protected boolean validateData() {

        // TODO: убедиться, что больше нет таких издательств

        if(pubHouseNameTF.getText().trim().isEmpty()) {
            AlertUtil.showWarningAndWait("Warning", "Пустое имя издательства",
                    "Пожалуйста, введите корректное (непустое) имя издательства");
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
            editable = new PublishingHouse();
        }
        editable.setName(pubHouseNameTF.getText().trim());

        DataUtils.saveOrUpdate(editable);

        primaryStage.close();

    }

    @Override
    public void switchToEditMode(PublishingHouse editable) {
        super.switchToEditMode(editable);
        pubHouseNameTF.setText(editable.getName());
    }
}