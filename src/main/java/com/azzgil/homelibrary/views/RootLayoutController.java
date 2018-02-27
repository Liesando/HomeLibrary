package com.azzgil.homelibrary.views;

import com.azzgil.homelibrary.HomeLibrary;
import com.azzgil.homelibrary.Section;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;

/**
 * class RootLayoutController
 *
 * Контроллер для корневого представления (окна) приложения. Обрабатывает
 * определённые события.
 *
 * @version 1.1 27 Feb 2018
 * @author Sergey Medelyan
 */
public class RootLayoutController
{
    private HomeLibrary homeLibrary;

    public void setHomeLibrary(HomeLibrary homeLibrary)
    {
        this.homeLibrary = homeLibrary;
    }

    @FXML
    private void initialize()
    {
        RadioButton rb = new RadioButton();
    }

    @FXML
    private void onExit()
    {
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
}
