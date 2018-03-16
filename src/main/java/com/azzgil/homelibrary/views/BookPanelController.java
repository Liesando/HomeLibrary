package com.azzgil.homelibrary.views;

import com.azzgil.homelibrary.HomeLibrary;
import com.azzgil.homelibrary.model.Book;
import com.azzgil.homelibrary.model.Borrowing;
import com.azzgil.homelibrary.model.BorrowingId;
import com.azzgil.homelibrary.utils.AlertUtil;
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
 * @author Sergey Medelyan & Maria Laktionova
 * @version 1.3 14 March 2018
 */
public class BookPanelController {

    // тексты вслывающих подсказок
    private static final String DELETE_BTN_TOOLTIP = "Удалить эту книгу";
    private static final String EDIT_BTN_TOOLTIP = "Редактировать...";
    private static final String HISTORY_BTN_TOOLTIP = "Показать историю займов книги";
    private static final String GIVE_BTN_TOOLTIP = "Одолжить книгу другу...";
    private static final String RETURN_BTN_TOOLTIP = "Зарегистрировать возвращение книги...";

    /** Текст для "кнопки" сворота-разворота содержимого */
    private static final String MORE_INFO = "подробнее";
    private static final String LESS_INFO = "свернуть";

    private static final String BOOK_PERSISTS_LBL_TEXT = "в наличии";
    private static final String BOOK_IS_BORROWED_LBL_TEXT = "книга сейчас у %s";
    private static final String BOOK_IS_LOST_LBL_TEXT = "книга утеряна! (виновник: %s)";

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
    @FXML private Label bookPersistenceLbl;
    @FXML private Button deleteBtn;
    @FXML private Button editBtn;
    @FXML private Button showBorrowingHistoryBtn;
    @FXML private Button giveOrReturnBtn;


    @FXML
    private void initialize() {
        initialHeight = anchorPane.getPrefHeight();

        GUIUtils.loadButtonIcon(deleteBtn, GUIUtils.DELETE_ICON);
        GUIUtils.loadButtonIcon(editBtn, GUIUtils.EDIT_ICON);
        GUIUtils.loadButtonIcon(showBorrowingHistoryBtn, GUIUtils.HISTORY_ICON);
        GUIUtils.addTooltipToButton(deleteBtn, DELETE_BTN_TOOLTIP);
        GUIUtils.addTooltipToButton(editBtn, EDIT_BTN_TOOLTIP);
        GUIUtils.addTooltipToButton(showBorrowingHistoryBtn, HISTORY_BTN_TOOLTIP);
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
        switchGiveOrReturnBtn();
    }

    /**
     * Меняет внешний вид кнопки займа/возврата книги
     * в зависимости от наличия самой книги
     */
    private void switchGiveOrReturnBtn() {
        if(book.isPresent()) {
            GUIUtils.loadButtonIcon(giveOrReturnBtn, GUIUtils.GIVE_ICON);
            GUIUtils.addTooltipToButton(giveOrReturnBtn, GIVE_BTN_TOOLTIP);
            giveOrReturnBtn.setOnAction(actionEvent -> onGiveBtn());
        } else {
            GUIUtils.loadButtonIcon(giveOrReturnBtn, GUIUtils.RETURN_ICON);
            GUIUtils.addTooltipToButton(giveOrReturnBtn, RETURN_BTN_TOOLTIP);
            giveOrReturnBtn.setOnAction(actionEvent -> onReturnBtn());
        }
    }

    /**
     * Обновляет отображаемое содержимое текущей заданной книги
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
            bookPersistenceLbl.setText("");
            bookPersistenceLbl.setTooltip(null);
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
            bookPersistenceLbl.getStyleClass().clear();

            if(book.isPresent()) {
                bookPersistenceLbl.setText(BOOK_PERSISTS_LBL_TEXT);
                bookPersistenceLbl.getStyleClass().add("label-book-persists");
                bookPersistenceLbl.setTooltip(GUIUtils.createTooltip(BOOK_PERSISTS_LBL_TEXT));
            } else {
                bookPersistenceLbl.setTooltip(GUIUtils.createTooltip(book.getLastBorrowing().getCommentary()));
                if (book.getLastBorrowing().isLost()) {
                    bookPersistenceLbl.setText(String.format(BOOK_IS_LOST_LBL_TEXT,
                            book.getLastBorrowing().getFriendBorrowed().toString()));
                    bookPersistenceLbl.getStyleClass().add("label-book-is-lost");
                } else {
                    bookPersistenceLbl.setText(String.format(BOOK_IS_BORROWED_LBL_TEXT,
                            book.getLastBorrowing().getFriendBorrowed().toString()));
                    bookPersistenceLbl.getStyleClass().add("label-book-is-borrowed");
                }
            }
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

    private void onGiveBtn() {
        Borrowing borrowing = new Borrowing();
        borrowing.setId(new BorrowingId());
        borrowing.getId().setIdBook(book.getId());
        borrowing.setBookBorrowed(book);

        if(HomeLibrary.showBorrowingEditWindow(BorrowingEditWindowController.BorrowMode.GIVE,
                borrowing)) {
            switchGiveOrReturnBtn();
        }
    }

    private void onReturnBtn() {
        Borrowing borrowing = book.getLastBorrowing();
        if(borrowing == null) {
            AlertUtil.showErrorAndWait(null, "Ошибка кода",
                    "Неверно назначена функция onReturnBtn",
                    "Обратитесь к разработчику");
            return;
        }

        if(HomeLibrary.showBorrowingEditWindow(BorrowingEditWindowController.BorrowMode.RETURN,
                borrowing)) {
            switchGiveOrReturnBtn();
        }
    }
}
