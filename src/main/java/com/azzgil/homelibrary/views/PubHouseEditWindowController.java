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
 * @version 1.0 4 March 2018
 */
public class PubHouseEditWindowController
        extends EditWindowBaseController<PublishingHouse> {

    @FXML private TextField pubHouseNameTF;

    @FXML
    private void initialize() {}

    @FXML
    private void onConfirmBtn() {
        isSuccessful = !pubHouseNameTF.getText().trim().isEmpty();
        if(!isSuccessful) {
            AlertUtil.showWarningAndWait("Warning", "Пустое имя издательства",
                    "Пожалуйста, введите корректное (непустое) имя издательства");
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
        if(editMode) {
            HomeLibrary.refreshBooks();
        } else {
            HomeLibrary.refreshOverallInfo();
        }

        primaryStage.close();

    }

    @Override
    public void switchToEditMode(PublishingHouse editable) {
        super.switchToEditMode(editable);
        pubHouseNameTF.setText(editable.getName());
    }
}
