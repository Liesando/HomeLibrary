package com.azzgil.homelibrary.views;

import com.azzgil.homelibrary.utils.HibernateUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.hibernate.Session;

/**
 * LibraryOverviewController
 *
 * Контроллер секции общей информации о библиотеке.
 *
 * @author Sergey Medelyan & Maria Laktionova
 * @version 1.2 11 March 2018
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

    /**
     * Обновить общую информацию
     */
    public void refreshOverallInfo() {
        Session session = HibernateUtils.openSession();

        int booksCount = ((Number) session.createQuery("select count(*) from Book")
                .iterate().next()).intValue();
        int genresCount = ((Number) session.createQuery("select count(*) from Genre")
                .iterate().next()).intValue();
        int pubHousesCount = ((Number) session.createQuery("select count(*) from PublishingHouse")
                .iterate().next()).intValue();
        int friendsCount = ((Number) session.createQuery("select count(*) from Friend")
                .iterate().next()).intValue();

        session.close();

        booksCountLbl.setText(Integer.toString(booksCount));
        genresCountLbl.setText(Integer.toString(genresCount));
        pubHousesCountLbl.setText(Integer.toString(pubHousesCount));
        friendsCountLbl.setText(Integer.toString(friendsCount));
    }
}
