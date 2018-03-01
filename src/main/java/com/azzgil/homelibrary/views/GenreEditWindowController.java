package com.azzgil.homelibrary.views;

import com.azzgil.homelibrary.model.Genre;
import com.azzgil.homelibrary.utils.AlertUtil;
import com.azzgil.homelibrary.utils.DataUtils;
import com.azzgil.homelibrary.utils.HibernateUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.hibernate.Hibernate;
import org.hibernate.Session;

import java.util.Arrays;

public class GenreEditWindowController {

    @FXML private TextField genreNameTF;
    @FXML private ComboBox<Genre> parentCategories;

    private boolean isSuccessful = false;
    private Stage primaryStage;

    @FXML
    private void initialize() {
        parentCategories.setItems(FXCollections.
                observableList(Arrays.asList(DataUtils.fetchAllGenres())));
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    private void onConfirmBtnClick() {
        isSuccessful = !genreNameTF.getText().trim().isEmpty();
        if(!isSuccessful) {
            AlertUtil.showWarningAndWait(null, "Warning", "Пустое имя жанра",
                    "Пожалуйста, введите корректное (непустое) имя жанра");
            return;
        }
        Session session = HibernateUtil.openSession();
        Genre g = new Genre();
        g.setName(genreNameTF.getText().trim());
        Genre parent = parentCategories.getSelectionModel().getSelectedItem();
        if(parent == null) {
            g.setParentId(null);
        } else {
            g.setParentId(parent.getId());
        }
        session.beginTransaction();
        session.saveOrUpdate(g);
        session.getTransaction().commit();
        session.close();
        primaryStage.close();
    }

    @FXML
    private void onCancelBtnClick() {
        primaryStage.close();
    }
}
