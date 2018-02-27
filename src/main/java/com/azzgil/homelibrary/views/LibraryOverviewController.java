package com.azzgil.homelibrary.views;

import com.azzgil.homelibrary.utils.HibernateUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.hibernate.Session;


/**
 * LibraryOverviewConrtoller
 *
 * Контроллер секции общей информации о библиотеке.
 * Единожды загружает информацию о всех книгах, жанрах
 * и друзьях и отображает её.
 *
 * TODO: вынести вышеописанный функционал в отдельные методы
 *
 * @author Sergey Medelyan
 * @version 1.0 27 Feb 2018
 */
public class LibraryOverviewController {

    @FXML
    private Label booksCountLbl;
    @FXML
    private Label genresCountLbl;
    @FXML
    private Label friendsCountLbl;

    @FXML
    private void initialize() {
        Session session = HibernateUtil.openSession();

        int booksCount = session.createQuery("from Book").list().size();
        int genresCount = session.createQuery("from Genre").list().size();
        int friendsCount = session.createQuery("from Friend").list().size();

        session.close();

        booksCountLbl.setText(Integer.toString(booksCount));
        genresCountLbl.setText(Integer.toString(genresCount));
        friendsCountLbl.setText(Integer.toString(friendsCount));
    }
}
