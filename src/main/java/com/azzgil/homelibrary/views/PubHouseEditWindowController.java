package com.azzgil.homelibrary.views;

import com.azzgil.homelibrary.HomeLibrary;
import com.azzgil.homelibrary.model.PublishingHouse;
import com.azzgil.homelibrary.utils.AlertUtil;
import com.azzgil.homelibrary.utils.HibernateUtil;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.hibernate.Session;


/**
 * PubHouseEditWindowController
 *
 * Контроллер окна создания/редактирования издательства
 *
 * @author Sergey Medelyan
 * @version 1.1 8 March 2018 */
public class PubHouseEditWindowController
        extends EditWindowBaseController<PublishingHouse> {

    @FXML private TextField pubHouseNameTF;

    @FXML
    private void initialize() {}

    @Override
    protected boolean validateData() {
        // TODO: посмотреть, что больше нет таких издательств
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

        Session session = HibernateUtil.openSession();

        if(!editMode) {
            editable = new PublishingHouse();
        }
        editable.setName(pubHouseNameTF.getText().trim());

        session.beginTransaction();
        session.saveOrUpdate(editable);
        session.getTransaction().commit();
        session.close();
        primaryStage.close();

    }

    @Override
    public void switchToEditMode(PublishingHouse editable) {
        super.switchToEditMode(editable);
        pubHouseNameTF.setText(editable.getName());
    }
}
