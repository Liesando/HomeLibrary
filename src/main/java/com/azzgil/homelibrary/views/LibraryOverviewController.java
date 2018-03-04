package com.azzgil.homelibrary.views;

import com.azzgil.homelibrary.utils.HibernateUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.hibernate.Session;


/**
 * LibraryOverviewConrtoller
 *
 * Контроллер секции общей информации о библиотеке.
 *
 * @author Sergey Medelyan
 * @version 1.1 4 March 2018
 */
public class LibraryOverviewController {

    @FXML private Label booksCountLbl;
    @FXML private Label genresCountLbl;
    @FXML private Label pubHousesCountLbl;
    @FXML private Label friendsCountLbl;

    @FXML
    private void initialize() {
        refreshOverallInfo();
    }

    public void refreshOverallInfo() {
        Session session = HibernateUtil.openSession();

        int booksCount = session.createQuery("from Book").list().size();
        int genresCount = session.createQuery("from Genre").list().size();
        int pubHousesCount = session.createQuery("from PublishingHouse").list().size();
        int friendsCount = session.createQuery("from Friend").list().size();

        session.close();

        booksCountLbl.setText(Integer.toString(booksCount));
        genresCountLbl.setText(Integer.toString(genresCount));
        pubHousesCountLbl.setText(Integer.toString(pubHousesCount));
        friendsCountLbl.setText(Integer.toString(friendsCount));
    }
}
