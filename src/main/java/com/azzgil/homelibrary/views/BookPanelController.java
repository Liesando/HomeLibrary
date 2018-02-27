package com.azzgil.homelibrary.views;

import com.azzgil.homelibrary.model.Book;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;


/**
 * BookPanelController
 *
 * Контроллер панельки конкретной книги. Обрабатывает события интерфейса
 *
 * @author Sergey Medelyan
 * @version 1.0 27 Feb 2018
 */
public class BookPanelController {

    /** Текст для "кнопки" сворота-разворота содержимого */
    private final static String MORE_INFO = "подробнее";
    private final static String LESS_INFO = "свернуть";

    private Book book;
    private boolean isFolded = true;
    private double initialHeigth = 0;

    @FXML private AnchorPane anchorPane;
    @FXML private Label moreInfoLbl;
    @FXML private GridPane additionalInfo;
    @FXML private TextArea bookCommentaryTA;
    @FXML private Label bookTitleLbl;
    @FXML private Label bookAuthorLbl;
    @FXML private Label bookIdLbl;
    @FXML private Label bookPublishingHouseLbl;
    @FXML private Label bookYearLbl;
    @FXML private Label bookIllustratorLbl;
    @FXML private Label bookTranslatorLbl;


    @FXML
    private void initialize() {
        initialHeigth = anchorPane.getPrefHeight();
    }

    @FXML
    private void onMoreInfoClick() {
        isFolded = !isFolded;

        if(isFolded) {
            anchorPane.setPrefHeight(initialHeigth);
            additionalInfo.setVisible(false);
            bookCommentaryTA.setVisible(false);
        } else {
            anchorPane.setPrefHeight(anchorPane.getMaxHeight());
            additionalInfo.setVisible(true);
            bookCommentaryTA.setVisible(true);
        }

        moreInfoLbl.setText( isFolded ? MORE_INFO : LESS_INFO);

    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
        updateBookInfo();
    }

    /**
     * Отображает содержимое текущей заданной книги
     */
    private void updateBookInfo() {
        if(book == null) {
            bookTitleLbl.setText("");
            bookAuthorLbl.setText("");
            bookIdLbl.setText("");
            bookPublishingHouseLbl.setText("");
            bookYearLbl.setText("");
            bookIllustratorLbl.setText("");
            bookTranslatorLbl.setText("");
            bookCommentaryTA.setText("");
        } else {
            bookTitleLbl.setText(book.getName());
            bookAuthorLbl.setText(book.getAuthor());
            bookIdLbl.setText(Integer.toString(book.getId()));
            bookPublishingHouseLbl.setText(book.getPublishingHouse().getName());
            bookYearLbl.setText(Integer.toString(book.getYear()));
            bookIllustratorLbl.setText(book.getPicAuthor());
            bookTranslatorLbl.setText(book.getTranslator());
            bookCommentaryTA.setText(book.getCommentary());
        }
    }
}
