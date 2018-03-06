package com.azzgil.homelibrary.views;

import com.azzgil.homelibrary.HomeLibrary;
import com.azzgil.homelibrary.ICUDController;
import com.azzgil.homelibrary.model.Book;
import com.azzgil.homelibrary.model.PublishingHouse;
import com.azzgil.homelibrary.utils.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;

import javax.persistence.PersistenceException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;


/**
 * PublishingHousesOverviewController
 *
 * Контроллер секции издательств.
 *
 * @author Sergey Medelyan
 * @version 1.1 6 March 2018
 */
public class PublishingHousesOverviewController implements ICUDController {
    private static final String EMPTY_SELECTION_ERROR =
            "Пожалуйста, сначала выберите издательство из списка";
    private static final String SHOW_BOOKS_BTN_TOOLTIP = "Показать книги этого издательства";
    private static final String DELETE_BTN_TOOLTIP = "Удалить выбранные издательства";
    private static final String EDIT_BTN_TOOLTIP = "Редактировать издательство";

    @FXML private Button addPubHouseBtn;
    @FXML private Button showBooksBtn;
    @FXML private Button deleteBtn;
    @FXML private Button editBtn;
    @FXML private MenuItem showBooksMI;
    @FXML private MenuItem deleteMI;
    @FXML private MenuItem editMI;
    @FXML private ListView<PublishingHouse> listView;
    private ObservableList<PublishingHouse> pubHouses;

    @FXML
    private void initialize() {
        GUIUtils.loadButtonIcon(addPubHouseBtn, GUIUtils.ADD_ICON);
        GUIUtils.loadButtonIcon(showBooksBtn, GUIUtils.EYE_FIND_ICON);
        GUIUtils.loadButtonIcon(deleteBtn, GUIUtils.DELETE_ICON);
        GUIUtils.loadButtonIcon(editBtn, GUIUtils.EDIT_ICON);

        GUIUtils.addTooltipToButton(showBooksBtn, SHOW_BOOKS_BTN_TOOLTIP);
        GUIUtils.addTooltipToButton(deleteBtn, DELETE_BTN_TOOLTIP);
        GUIUtils.addTooltipToButton(editBtn, EDIT_BTN_TOOLTIP);

        refreshPubHouses();
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    /** Обновляет и перерисовывает список издательств */
    private void refreshPubHouses() {
        pubHouses = FXCollections.observableArrayList(
                Arrays.asList(DataUtils.fetchAllPubHouses()));
        pubHouses.sort(Comparator.comparing(ph -> ph.getName()));

        listView.getItems().clear();
        listView.setItems(pubHouses);
        listView.setOnMouseClicked(me -> {
            if(me.getButton() == MouseButton.PRIMARY
                    && me.getClickCount() == 2) {
                showBooks();
            }
        });
    }

    /**
     * Показывает книги, изданные выбранным издательством.
     * Если выбрано несколько издательств, показывает книги
     * для первого из них.
     * TODO: изменить для нескольких издательств
     */
    @FXML
    private void showBooks() {
        PublishingHouse pubHouse = listView.getSelectionModel().getSelectedItem();
        if(pubHouse == null) {
            AlertUtil.showEmptySelectionErrorAndWait(EMPTY_SELECTION_ERROR);
            return;
        }

        String books = DataUtils.reduceBooks(pubHouse.getBooks());

        AlertUtil.showInformationAndWait("Книги издательства",
                pubHouse.getName(),
                (books != "" ? books : "у этого издательство ещё нет книг"));
    }

    @Override
    public void create() {
        showPubHouseEditWindow(false);
    }

    @Override
    public void update() {
        showPubHouseEditWindow(true);
    }

    /** см. {@link GenresOverviewController#showGenreEditWindow(boolean)}*/
    private void showPubHouseEditWindow(boolean editMode) {
        try {
            FXMLLoader loader = FXMLUtils.configureLoaderFor("views/PublishingHouseEditWindow.fxml");

            Stage stage = new Stage(StageStyle.UTILITY);
            stage.setScene(new Scene(loader.load()));
            PubHouseEditWindowController controller = loader.getController();
            controller.setPrimaryStage(stage);

            if(editMode) {
                if (!validateUpdate()) {
                    AlertUtil.showEmptySelectionErrorAndWait(EMPTY_SELECTION_ERROR);
                    return;
                }
                controller.switchToEditMode(listView.getSelectionModel()
                        .getSelectedItem());
            }
            stage.showAndWait();

            if(controller.isSuccessful()) {
                refreshPubHouses();
                if(editMode) {

                    // могли измениться названия издательств
                    HomeLibrary.refreshBooks();
                } else {

                    // меняется количество издательств
                    HomeLibrary.refreshOverallInfo();
                }
            }

        } catch (IOException e) {
            AlertUtil.showDataCorruptionErrorAndWait("views/GenreEditWindow.fxml");
            e.printStackTrace();
            Platform.exit();
        }
    }

    @Override
    public void delete() {
        if(!validateUpdate()) {
            AlertUtil.showEmptySelectionErrorAndWait(EMPTY_SELECTION_ERROR);
            return;
        }

        Session session = HibernateUtil.openSession();
        try {
            session.beginTransaction();
            for (PublishingHouse ph:
                    listView.getSelectionModel().getSelectedItems()) {
                session.delete(ph);
            }
            session.getTransaction().commit();
            refreshPubHouses();

            HomeLibrary.refreshOverallInfo();
        } catch (PersistenceException e) {
            session.getTransaction().rollback();
            if(e.getCause() instanceof ConstraintViolationException) {

                // отображаем список книг этого издательства
                ArrayList<Book> books = new ArrayList<>();
                for (PublishingHouse ph:
                     listView.getSelectionModel().getSelectedItems()) {
                    books.addAll(ph.getBooks());
                }

                String info = DataUtils.reduceBooks(books);

                AlertUtil.showWarningAndWait("Error",
                        "Невозможно удалить издательство",
                        "Существуют книги этого издательства. " +
                                "Чтобы удаление издательства стало возможным, " +
                                "сначала удалите эти книги либо измените их издательство.\n\n" +
                                info);

            } else {
                // unknown exception, throw through
                throw e;
            }
        } finally {
            session.close();
        }
    }

    @Override
    public boolean validateUpdate() {
        return listView.getSelectionModel().getSelectedItem() != null;
    }

    @Override
    public boolean validateDelete() {
        return listView.getSelectionModel().getSelectedItems().size() > 0;
    }

    @Override
    public void setReadFeatureAccess(boolean accessible) {
        showBooksBtn.setDisable(!accessible);
        showBooksMI.setDisable(!accessible);
    }

    @Override
    public void setUpdateFeatureAccess(boolean accessible) {
        editBtn.setDisable(!accessible);
        editMI.setDisable(!accessible);
    }

    @Override
    public void setDeleteFeatureAccess(boolean accessible) {
        deleteBtn.setDisable(!accessible);
        deleteMI.setDisable(!accessible);
    }
}