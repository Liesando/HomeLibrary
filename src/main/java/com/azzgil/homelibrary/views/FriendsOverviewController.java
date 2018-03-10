package com.azzgil.homelibrary.views;

import com.azzgil.homelibrary.HomeLibrary;
import com.azzgil.homelibrary.ICUDController;
import com.azzgil.homelibrary.model.Friend;
import com.azzgil.homelibrary.utils.GUIUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

import java.util.Arrays;
import java.util.Comparator;

public class FriendsOverviewController implements ICUDController {

    private static final String ADD_BTN_TOOLTIP = "Добавить нового друга";
    private static final String DELETE_BTN_TOOLTIP = "Удалить выбранных друзей";
    private static final String UPDATE_BTN_TOOLTIP = "Редактировать выбранного друга";
    private static final String DETAILS_BTN_TOOLTIP = "Показать подробности о выбранном друге";

    @FXML private ListView<Friend> listView;
    @FXML private Button addFriendBtn;
    @FXML private Button deleteBtn;
    @FXML private Button updateBtn;
    @FXML private Button detailsBtn;

    private ObservableList<Friend> friends;

    @FXML
    private void initialize() {
        GUIUtils.loadButtonIcon(addFriendBtn, GUIUtils.ADD_ICON);
        GUIUtils.loadButtonIcon(deleteBtn, GUIUtils.DELETE_ICON);
        GUIUtils.loadButtonIcon(updateBtn, GUIUtils.EDIT_ICON);
        GUIUtils.loadButtonIcon(detailsBtn, GUIUtils.EYE_FIND_ICON);

        GUIUtils.addTooltipToButton(addFriendBtn, ADD_BTN_TOOLTIP);
        GUIUtils.addTooltipToButton(deleteBtn, DELETE_BTN_TOOLTIP);
        GUIUtils.addTooltipToButton(updateBtn, UPDATE_BTN_TOOLTIP);
        GUIUtils.addTooltipToButton(detailsBtn, DETAILS_BTN_TOOLTIP);
    }

    public void refreshFriends() {
//        friends = HomeLibrary.getAllFriends();
//        friends.sort(Comparator.comparing(f -> f.getFio()));
//        // TODO: sort
//        listView.setItems(friends);
    }

    @Override
    public void create() {

    }

    @Override
    public void update() {

    }

    @Override
    public void delete() {

    }

    @Override
    public boolean validateUpdate() {
        return false;
    }

    @Override
    public boolean validateDelete() {
        return false;
    }

    @Override
    public void setReadFeatureAccess(boolean accessible) {

    }

    @Override
    public void setUpdateFeatureAccess(boolean accessible) {

    }

    @Override
    public void setDeleteFeatureAccess(boolean accessible) {

    }
}
