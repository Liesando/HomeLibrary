package com.azzgil.homelibrary.views;

import com.azzgil.homelibrary.HomeLibrary;
import com.azzgil.homelibrary.model.Book;
import com.azzgil.homelibrary.utils.GUIUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
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
 * @version 1.2 8 March 2018
 */
public class BookPanelController {

    private final static String DELETE_BTN_TOOLTIP = "Удалить эту книгу";
    private final static String EDIT_BTN_TOOLTIP = "Редактировать...";

    /** Текст для "кнопки" сворота-разворота содержимого */
    private final static String MORE_INFO = "подробнее";
    private final static String LESS_INFO = "свернуть";

    private BookOverviewController parentController;
    private Book book;
    private boolean isFolded = true;
    private double initialHeight = 0;

    @FXML private AnchorPane anchorPane;
    @FXML private Label moreInfoLbl;
    @FXML private GridPane additionalInfo;
    @FXML private TextArea bookCommentaryTA;
    @FXML private Label bookTitleLbl;
    @FXML private Label bookAuthorLbl;
    @FXML private Label bookGenresLbl;
    @FXML private Label bookIdLbl;
    @FXML private Label bookPublishingHouseLbl;
    @FXML private Label bookYearLbl;
    @FXML private Label bookIllustratorLbl;
    @FXML private Label bookTranslatorLbl;
    @FXML private Button deleteBtn;
    @FXML private Button editBtn;


    @FXML
    private void initialize() {
        initialHeight = anchorPane.getPrefHeight();

        GUIUtils.loadButtonIcon(deleteBtn, GUIUtils.DELETE_ICON);
        GUIUtils.loadButtonIcon(editBtn, GUIUtils.EDIT_ICON);
        GUIUtils.addTooltipToButton(deleteBtn, DELETE_BTN_TOOLTIP);
        GUIUtils.addTooltipToButton(editBtn, EDIT_BTN_TOOLTIP);
    }

    @FXML
    private void onMoreInfoClick() {
        isFolded = !isFolded;

        if(isFolded) {
            anchorPane.setPrefHeight(initialHeight);
            additionalInfo.setVisible(false);
            bookCommentaryTA.setVisible(false);
            parentController.correctVBoxHeight(
                    initialHeight - anchorPane.getMaxHeight());
        } else {
            anchorPane.setPrefHeight(anchorPane.getMaxHeight());
            additionalInfo.setVisible(true);
            bookCommentaryTA.setVisible(true);
            parentController.correctVBoxHeight(
                    anchorPane.getMaxHeight() - initialHeight);
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
            bookGenresLbl.setText("");
            bookIdLbl.setText("");
            bookPublishingHouseLbl.setText("");
            bookYearLbl.setText("");
            bookIllustratorLbl.setText("");
            bookTranslatorLbl.setText("");
            bookCommentaryTA.setText("");
        } else {
            bookTitleLbl.setText(book.getName());
            bookAuthorLbl.setText(book.getAuthor());
            bookGenresLbl.setText(book.getGenresAsString());
            bookIdLbl.setText(Integer.toString(book.getId()));
            bookPublishingHouseLbl.setText(book.getPublishingHouse().getName());
            bookYearLbl.setText(Integer.toString(book.getYear()));
            bookIllustratorLbl.setText(book.getPicAuthor());
            bookTranslatorLbl.setText(book.getTranslator());
            bookCommentaryTA.setText(book.getCommentary());
        }
    }

    public double getCurrentHeight() {
        return anchorPane.getPrefHeight();
    }

    public void setParentController(BookOverviewController parentController) {
        this.parentController = parentController;
    }

    @FXML
    private void onDeleteBtn() {
        parentController.setObservableBook(book);
        parentController.delete();
    }

    @FXML
    private void onEditBtn() {
        HomeLibrary.showBookEditWindow(true, book);
    }
}
