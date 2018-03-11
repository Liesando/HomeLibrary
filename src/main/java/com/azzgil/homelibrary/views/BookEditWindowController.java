package com.azzgil.homelibrary.views;

import com.azzgil.homelibrary.HomeLibrary;
import com.azzgil.homelibrary.model.Book;
import com.azzgil.homelibrary.model.Genre;
import com.azzgil.homelibrary.model.PublishingHouse;
import com.azzgil.homelibrary.utils.AlertUtil;
import com.azzgil.homelibrary.utils.DataUtils;
import com.azzgil.homelibrary.utils.GUIUtils;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import org.controlsfx.control.CheckListView;

import java.util.Arrays;

/**
 * BookEditWindowController
 *
 * Контроллер окна создания/редактирования книги. Управляет настройкой
 * и событиями интерфейса.
 *
 * @author Sergey Medelyan
 * @version 1.1 11 March 2018
 */
public class BookEditWindowController extends EditWindowBaseController<Book> {
    private static final String ADD_GENRE_BTN_TOOLTIP = "Добавить новый жанр...";
    private static final String ADD_PUB_HOUSE_BTN_TOOLTIP = "Добавить новое издательство...";

    private static final int LEAST_ALLOWED_YEAR_OF_PUBLISHING = 1500;

    @FXML private TextField bookTitleTF;
    @FXML private ComboBox<String> authorCB;
    @FXML private CheckListView<Genre> genresCLV;
    @FXML private TextField yearPublishedTF;
    @FXML private ComboBox<PublishingHouse> pubHouseCB;
    @FXML private TextField translatorTF;
    @FXML private TextField illustratorTF;
    @FXML private TextArea commentaryTA;
    @FXML private Button addGenreBtn;
    @FXML private Button addPubHouseBtn;

    @FXML
    private void initialize() {
        GUIUtils.loadButtonIcon(addGenreBtn, GUIUtils.ADD_ICON);
        GUIUtils.loadButtonIcon(addPubHouseBtn, GUIUtils.ADD_ICON);
        GUIUtils.addTooltipToButton(addGenreBtn, ADD_GENRE_BTN_TOOLTIP);
        GUIUtils.addTooltipToButton(addPubHouseBtn, ADD_PUB_HOUSE_BTN_TOOLTIP);

        // отображаем жанры как полные имена (напр., "Классика / Поэма"
        // вместо "Поэма")
        genresCLV.setCellFactory(listView -> new CheckBoxListCell<>(genresCLV::getItemBooleanProperty) {
            @Override
            public void updateItem(Genre item, boolean empty) {
                super.updateItem(item, empty);
                setText(item == null ? GUIUtils.STD_NOT_SPECIFIED_REPLACER : item.getFullName());
            }
        });

        authorCB.setItems(FXCollections.observableList(
                Arrays.asList(HomeLibrary.getAllAuthors())));
        refreshGenres();
        refreshPubHouses();

        editable = new Book();
    }

    private void refreshGenres() {
        genresCLV.setItems(FXCollections.observableList(
                Arrays.asList(HomeLibrary.getAllGenres())));
    }

    private void refreshPubHouses() {
        pubHouseCB.setItems(FXCollections.observableList(
                Arrays.asList(HomeLibrary.getAllPubHouses())));
    }

    @Override
    protected boolean validateData() {
        StringBuilder errorMessage = new StringBuilder("");

        if(bookTitleTF.getText().trim().length() == 0) {
            errorMessage.append("Не указано название книги. Введите непустое название.\n");
        }
        if(authorCB.getSelectionModel().getSelectedItem() == null
                || authorCB.getSelectionModel().getSelectedItem().trim().length() == 0) {
            errorMessage.append("Не указан автор. Выберите имя автора из списка или введите новое" +
                    " если нет нужного.\n");
        }
        if(genresCLV.getCheckModel().getCheckedItems().size() == 0) {
            errorMessage.append("Не выбран ни один жанр. Выберите жанры из списка или создайте новый" +
                    " если нет нужного\n");
        }

        try {
            int year = Integer.parseInt(yearPublishedTF.getText().trim());
            if(year < LEAST_ALLOWED_YEAR_OF_PUBLISHING) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            errorMessage.append("Неверно указан год издания. Пожалуйста, используйте только цифры. " +
                    "Год должен быть не меньше ");
            errorMessage.append(Integer.toString(LEAST_ALLOWED_YEAR_OF_PUBLISHING));
            errorMessage.append("\n");
        }

        if(pubHouseCB.getSelectionModel().getSelectedItem() == null) {
            errorMessage.append("Не указано издательство. Выберите издательство из списка или " +
                    "создайте новое, если нет нужного.\n");
        }

        translatorTF.setText(translatorTF.getText().trim().length() > 0 ?
                translatorTF.getText() : GUIUtils.STD_NOT_SPECIFIED_REPLACER);

        illustratorTF.setText(illustratorTF.getText().trim().length() > 0 ?
                illustratorTF.getText() : GUIUtils.STD_NOT_SPECIFIED_REPLACER);

        commentaryTA.setText(commentaryTA.getText().length() > 0 ?
                commentaryTA.getText() : GUIUtils.NO_COMMENT_REPLACER);

        if(errorMessage.length() > 0) {
            AlertUtil.showWarningAndWait(primaryStage, "Ошибки ввода", "Пожалуйста, исправьте ошибки ввода",
                    errorMessage.toString());
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

        editable.setName(bookTitleTF.getText().trim());
        editable.setAuthor(authorCB.getSelectionModel().getSelectedItem().trim());
        editable.setGenres(genresCLV.getCheckModel().getCheckedItems());
        editable.setYear(Integer.parseInt(yearPublishedTF.getText().trim()));
        editable.setPublishingHouse(pubHouseCB.getValue());
        editable.setTranslator(translatorTF.getText().trim());
        editable.setPicAuthor(illustratorTF.getText().trim());
        editable.setCommentary(commentaryTA.getText().trim());

        DataUtils.saveOrUpdate(editable);

        primaryStage.close();
    }

    @Override
    public void switchToEditMode(Book editable) {
        super.switchToEditMode(editable);

        bookTitleTF.setText(editable.getName());
        authorCB.getSelectionModel().select(editable.getAuthor());
        genresCLV.getCheckModel().clearChecks();

        // check - отметить, а не проверить :)
        checkGenres();
        yearPublishedTF.setText(Integer.toString(editable.getYear()));
        pubHouseCB.setValue(editable.getPublishingHouse());
        translatorTF.setText(editable.getTranslator());
        illustratorTF.setText(editable.getPicAuthor());
        commentaryTA.setText(editable.getCommentary());
    }

    /** Отмечает галочками жанры, которым принадлежит книга, в списке жанров */
    private void checkGenres() {
        editable.getGenres().forEach(g ->
                genresCLV.getCheckModel().check(g));
    }

    @FXML
    private void onAddGenreBtn() {
        if(HomeLibrary.showGenreEditWindow(false, null)) {
            refreshGenres();
            checkGenres();
        }
    }

    @FXML private void onAddPubHouseBtn() {
        PublishingHouse oldValue = pubHouseCB.getValue();
        if(HomeLibrary.showPubHouseEditWindow(false, null)) {
            refreshPubHouses();

            // TODO: не теряем старое значение
        }
    }
}
