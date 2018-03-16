package com.azzgil.homelibrary.views;

import com.azzgil.homelibrary.HomeLibrary;
import com.azzgil.homelibrary.model.Book;
import com.azzgil.homelibrary.model.Borrowing;
import com.azzgil.homelibrary.model.Friend;
import com.azzgil.homelibrary.utils.AlertUtil;
import com.azzgil.homelibrary.utils.DataUtils;
import com.azzgil.homelibrary.utils.GUIUtils;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;

/**
 * BorrowingEditWindowController
 *
 * Контроллер окна создания/редактирования займа книги.
 *
 * @author Sergey Medelyan & Maria Laktionova
 * @version 1.0 13 March 2018
 */
public class BorrowingEditWindowController
        extends EditWindowBaseController<Borrowing> {

    private static final String ADD_FRIEND_BTN_TOOLTIP = "Добавить друга...";
    private static final String EDIT_BTN_TEXT = "Сохранить";
    private static final String RETURN_BTN_TEXT = "Вернуть книгу";
    private static final LocalDate DEFAULT_DATE = LocalDate.of(1000, 1, 1);

    /**
     * Режимы работы контроллера: GIVE - выдача книги,
     * EDIT - редактирование данных о всём займе
     * <i><b>(не используется в данной версии программы)</b></i>,
     * RETURN - возвращение книги.
     * От режима работы зависят доступность и значения
     * некоторых полей (см. {@link BorrowingEditWindowController#changeMode(BorrowMode, Borrowing)})
     */
    public enum BorrowMode { GIVE, EDIT, RETURN }

    @FXML private ComboBox<Book> booksCB;
    @FXML private ComboBox<Friend> friendsCB;
    @FXML private Button addFriendBtn;
    @FXML private DatePicker borrowDateDP;
    @FXML private DatePicker returnDateDP;
    @FXML private CheckBox lostCb;
    @FXML private CheckBox damagedCb;
    @FXML private TextArea commentaryTA;

    /** Режим, в котором работает контроллер (см. {@link BorrowMode}) */
    private BorrowMode mode;

    @FXML
    private void initialize() {
        GUIUtils.loadButtonIcon(addFriendBtn, GUIUtils.ADD_ICON);
        GUIUtils.addTooltipToButton(addFriendBtn, ADD_FRIEND_BTN_TOOLTIP);

        refreshBooks();
        refreshFriends();

        editable = new Borrowing();
    }

    private void refreshBooks() {

        // фильтруем и отображаем только те книги, которые
        // можно одолжить - т. е. книги в наличии
        booksCB.setItems(FXCollections.observableList(
                Arrays.asList(HomeLibrary.getAllBooks()))
                .filtered(Book::isPresent)
                .sorted(Comparator.comparing(Book::getName)));
    }

    private void refreshFriends() {
        friendsCB.setItems(FXCollections.observableList(
                Arrays.asList(HomeLibrary.getAllFriends()))
                .sorted(Comparator.comparing(Friend::getFio)));
    }

    /**
     * Изменяет режим контроллера на переданный, изменяя
     * доступность соответствующих элементов GUI и их значения
     *
     * @param mode      Режим
     * @param borrowing Займ (может быть null)
     */
    public void changeMode(BorrowMode mode, Borrowing borrowing) {
        this.mode = mode;
        editable = borrowing != null ? borrowing : editable;
        switch (mode) {
            case GIVE:
                booksCB.getSelectionModel().select(borrowing.getBookBorrowed());
                returnDateDP.setDisable(true);
                returnDateDP.setValue(DEFAULT_DATE);
                lostCb.setDisable(true);
                lostCb.setSelected(false);
                damagedCb.setDisable(true);
                damagedCb.setSelected(false);
                break;

            case EDIT:

                // предполагается, что контроллер используется в одном
                // режиме и после этого уничтожается
                // поэтому здесь не нужно делать доступными
                // элементы GUI - они изначально доступны
                if(borrowing == null) {
                    throw new RuntimeException("NULL BORROWING");
                }
                confirmBtn.setText(EDIT_BTN_TEXT);
                fillValues(borrowing);
                break;

            case RETURN:
                if(borrowing == null) {
                    throw new RuntimeException("NULL BORROWING");
                }
                fillValues(borrowing);
                booksCB.setDisable(true);
                friendsCB.setDisable(true);
                addFriendBtn.setDisable(true);
                borrowDateDP.setDisable(true);
                confirmBtn.setText(RETURN_BTN_TEXT);
                break;

            default:
                AlertUtil.showDataCorruptionErrorAndWait("Выбран неверный режим отображения. " +
                        "Свяжитесь с разработчиком, если получили данную ошибку.");
        }
    }

    private void fillValues(Borrowing borrowing) {
        booksCB.getSelectionModel().select(borrowing.getBookBorrowed());
        friendsCB.getSelectionModel().select(borrowing.getFriendBorrowed());
        borrowDateDP.setValue(borrowing.getId().getBorrowingDate().toLocalDate());
        returnDateDP.setValue(borrowing.getId().getBorrowingDate().toLocalDate());
        lostCb.setSelected(borrowing.isLost());
        damagedCb.setSelected(borrowing.isDamaged());
        commentaryTA.setText(borrowing.getCommentary());
    }

    @Override
    protected boolean validateData() {
        StringBuilder errorMessage = new StringBuilder();

        if(booksCB.getSelectionModel().getSelectedItem() == null) {
            errorMessage.append("Не выбрана книга. Пожалуйста, выберите книгу из списка\n");
        } else if (mode == BorrowMode.GIVE
                && !booksCB.getSelectionModel().getSelectedItem().isPresent()) {

            // несмотря на то, что мы отфильтровали список книг,
            // пользователь мог просто открыть данное окно и оставить
            // его висеть, пока редактировал книги. То есть, ситуация
            // могла поменяться, нужно ещё раз проверить наличие выбранной книги
            errorMessage.append("Выбранной книги нет в наличии");
        }

        if(friendsCB.getSelectionModel().getSelectedItem() == null) {
            errorMessage.append("Не выбран друг. Пожалуйста, выберите друга из списка " +
                    "или создайте нового\n");
        }

        if(borrowDateDP.getValue() == null) {
            errorMessage.append("Не выбрана дата займа. Пожалуйста, укажите, когда вы " +
                    "одолжили эту книгу другу");
        }

        if(returnDateDP.getValue() == null) {
            errorMessage.append("Не указана дата возвращения книги. Пожалуйста, укажите, " +
                    "когда друг вернул вам книгу.\n");
        }

        if(returnDateDP.getValue() != null
                && mode == BorrowMode.RETURN
                && returnDateDP.getValue().isBefore(borrowDateDP.getValue())) {
            errorMessage.append("Дата возвращения должна быть указана не ранее даты займа.\n");
        }

        commentaryTA.setText(commentaryTA.getText().trim().isEmpty() ?
                GUIUtils.NO_COMMENT_REPLACER : commentaryTA.getText().trim());

        if(errorMessage.length() > 0) {
            AlertUtil.showErrorAndWait(primaryStage, "Ошибка ввода",
                    "Пожалуйста, исправьте следующие ошибки", errorMessage.toString());
            return false;
        }

        return true;
    }

    @FXML
    private void onAddFriendBtn() {
        if(HomeLibrary.showFriendEditWindow(false, null)) {
            HomeLibrary.refreshFriends();
            HomeLibrary.getFriendsOverviewController().refreshFriends();
            refreshFriends();

            // TODO: сохранить выбранного друга
        }
    }

    @FXML
    private void onConfirmBtn() {
        isSuccessful = validateData();
        if(!isSuccessful) {
            return;
        }

        editable.getId().setIdBook(booksCB.getSelectionModel().getSelectedItem().getId());
        editable.getId().setIdFriend(friendsCB.getSelectionModel().getSelectedItem().getId());
        editable.getId().setBorrowingDate(Date.valueOf(borrowDateDP.getValue()));
        editable.setReturnDate(Date.valueOf(returnDateDP.getValue()));
        editable.setLost(lostCb.isSelected());
        editable.setDamaged(damagedCb.isSelected());
        editable.setCommentary(commentaryTA.getText());

        DataUtils.saveOrUpdate(editable);

        primaryStage.close();
    }
}
