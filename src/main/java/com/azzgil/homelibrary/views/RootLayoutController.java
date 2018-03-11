package com.azzgil.homelibrary.views;

import com.azzgil.homelibrary.HomeLibrary;
import com.azzgil.homelibrary.Section;
import com.azzgil.homelibrary.utils.GUIUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

/**
 * class RootLayoutController
 *
 * Контроллер для корневого представления (окна) приложения. Обрабатывает
 * определённые события.
 *
 * @version 1.3 6 March 2018
 * @author Sergey Medelyan
 */
public class RootLayoutController {

    private static final String REFRESH_BTN_TOOLTIP = "Обновить всё";

    private HomeLibrary homeLibrary;

    @FXML
    private MenuItem editMI;
    @FXML
    private MenuItem deleteMI;
    @FXML
    private Button refreshBtn;

    public void setHomeLibrary(HomeLibrary homeLibrary) {
        this.homeLibrary = homeLibrary;
    }

    @FXML
    private void initialize() {
        deleteMI.setAccelerator(new KeyCodeCombination(KeyCode.DELETE));
        GUIUtils.loadButtonIcon(refreshBtn, GUIUtils.REFRESH_ICON);
        GUIUtils.addTooltipToButton(refreshBtn, REFRESH_BTN_TOOLTIP);
    }

    @FXML
    private void onExit() {
        Platform.exit();
    }

    @FXML
    private void onLibraryRBtn() {
        homeLibrary.showSectionLayout(Section.Library);
    }

    @FXML
    private void onBookRBtn() {
        homeLibrary.showSectionLayout(Section.Books);
    }

    @FXML
    private void onFriendsRBtn() {
        homeLibrary.showSectionLayout(Section.Friends);
    }

    @FXML
    private void onGenresRBtn() {
        homeLibrary.showSectionLayout(Section.Genres);
    }

    @FXML
    private void onPublishingHousesRBtn() {
        homeLibrary.showSectionLayout(Section.PublishingHouses);
    }

//    TODO: add global all-app user capabilities turning on/off methods
//    @FXML
//    private boolean onValidateEdit() {
//        boolean result = homeLibrary.getCurrentController() != null
//                && homeLibrary.getCurrentController().validateUpdate();
//        editMI.setDisable(!result);
//        return result;
//    }

    @FXML
    private void onEdit() {
        if (homeLibrary.getCurrentController() != null) {
            homeLibrary.getCurrentController().update();
        }
    }

    @FXML
    private void onDelete() {
        if (homeLibrary.getCurrentController() != null) {
            homeLibrary.getCurrentController().delete();
        }
    }

    @FXML
    private void onRefresh() {

        // total refresh here :D (code crutch)
        HomeLibrary.refreshBooks();
        HomeLibrary.refreshAuthors();
        HomeLibrary.refreshGenres();
        HomeLibrary.refreshPublishingHouses();
        HomeLibrary.refreshFriends();

        HomeLibrary.getLibraryOverviewController().refreshOverallInfo();
        HomeLibrary.getBookOverviewController().refreshBooks();
        HomeLibrary.getGenresOverviewController().refreshGenres();
        HomeLibrary.getPubHousesOverviewController().refreshPubHouses();
        HomeLibrary.getFriendsOverviewController().refreshFriends();
    }
}
