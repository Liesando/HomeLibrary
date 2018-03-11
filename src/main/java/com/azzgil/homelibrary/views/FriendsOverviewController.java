package com.azzgil.homelibrary.views;

import com.azzgil.homelibrary.HomeLibrary;
import com.azzgil.homelibrary.ICUDController;
import com.azzgil.homelibrary.model.Friend;
import com.azzgil.homelibrary.utils.AlertUtil;
import com.azzgil.homelibrary.utils.DataUtils;
import com.azzgil.homelibrary.utils.GUIUtils;
import com.azzgil.homelibrary.utils.HibernateUtil;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import org.hibernate.Session;

import java.util.Arrays;
import java.util.Comparator;

/**
 * FriendsOverviewController
 *
 * Контроллер секции друзей.
 *
 * @author Sergey Medelyan
 * @version 1.0 11 March 2018
 */
public class FriendsOverviewController implements ICUDController {

    // названия столбцов
    private static final String FIO_COL_NAME = "ФИО";
    private static final String PHONE_COL_NAME = "Телефон";
    private static final String EMAIL_COL_NAME = "Email";

    // тексты всплывающих подсказок
    private static final String ADD_BTN_TOOLTIP = "Добавить нового друга";
    private static final String DELETE_BTN_TOOLTIP = "Удалить выбранных друзей";
    private static final String UPDATE_BTN_TOOLTIP = "Редактировать выбранного друга";
    private static final String DETAILS_BTN_TOOLTIP = "Показать подробности о выбранном друге";

    @FXML private TableView<Friend> tableView;
    @FXML private Button addFriendBtn;
    @FXML private Button deleteBtn;
    @FXML private Button updateBtn;
    @FXML private Button detailsBtn;

    private Friend[] friends;

    @FXML
    private void initialize() {
        GUIUtils.loadButtonIcon(addFriendBtn, GUIUtils.ADD_ICON);
        GUIUtils.loadButtonIcon(deleteBtn, GUIUtils.DELETE_ICON);
        GUIUtils.loadButtonIcon(updateBtn, GUIUtils.EDIT_ICON);
        GUIUtils.loadButtonIcon(detailsBtn, GUIUtils.DETAILS_ICON);

        GUIUtils.addTooltipToButton(addFriendBtn, ADD_BTN_TOOLTIP);
        GUIUtils.addTooltipToButton(deleteBtn, DELETE_BTN_TOOLTIP);
        GUIUtils.addTooltipToButton(updateBtn, UPDATE_BTN_TOOLTIP);
        GUIUtils.addTooltipToButton(detailsBtn, DETAILS_BTN_TOOLTIP);

        setupTableView();
        refreshFriends();
    }

    /**
     * Настраивает внешний вид таблицы и формат столбцов
     */
    private void setupTableView() {
        tableView.setRowFactory(param -> new TableRow<>() {
            @Override
            protected void updateItem(Friend item, boolean empty) {
                super.updateItem(item, empty);
                if(item == null) {
                    setTooltip(null);
                } else {
                    String tooltipText = String.format("[vk.com/%1$d]\n%2$s",
                            item.getSocialNumber(), item.getCommentary());
                    setTooltip(GUIUtils.createTooltip(tooltipText));
                }
            }
        });

        TableColumn<Friend, String> fioColumn = new TableColumn<>(FIO_COL_NAME);
        fioColumn.setCellValueFactory(new PropertyValueFactory<>("fio"));
        fioColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        fioColumn.setOnEditCommit(event -> {
            Friend f = event.getRowValue();
            f.setFio(event.getNewValue());
            DataUtils.saveOrUpdate(f);
        });

        TableColumn<Friend, String> phoneColumn = new TableColumn<>(PHONE_COL_NAME);
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        phoneColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        phoneColumn.setOnEditCommit(event -> {
            Friend f = event.getRowValue();
            f.setPhoneNumber(event.getNewValue());
            DataUtils.saveOrUpdate(f);
        });
        phoneColumn.setEditable(true);

        TableColumn<Friend, String> emailColumn = new TableColumn<>(EMAIL_COL_NAME);
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        emailColumn.setOnEditCommit(event -> {
            Friend f = event.getRowValue();
            f.setEmail(event.getNewValue());
            DataUtils.saveOrUpdate(f);
        });

        tableView.getColumns().addAll(fioColumn, phoneColumn, emailColumn);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    /**
     * Обновляет список друзей и таблицу
     */
    public void refreshFriends() {
        friends = Arrays.stream(HomeLibrary.getAllFriends())
                .sorted(Comparator.comparing(Friend::getFio))
                .toArray(Friend[]::new);
        tableView.getItems().setAll(friends);
    }

    @Override
    public void create() {
        HomeLibrary.showFriendEditWindow(false, null);
    }

    @Override
    public void update() {
        if(validateDelete()) {
            HomeLibrary.showFriendEditWindow(true,
                    tableView.getSelectionModel().getSelectedItem());
        }
    }

    @Override
    public void delete() {
        if(!validateDelete()) {
            AlertUtil.showEmptySelectionErrorAndWait("Выберите друга из списка");
            return;
        }

        Session session = HibernateUtil.openSession();
        session.beginTransaction();
        tableView.getSelectionModel().getSelectedItems()
                .forEach(f -> session.delete(f));
        session.getTransaction().commit();
        session.close();

        HomeLibrary.refreshFriends();
        HomeLibrary.getLibraryOverviewController().refreshOverallInfo();
        refreshFriends();
    }

    @Override
    public boolean validateUpdate() {
        return tableView.getSelectionModel().getSelectedItem() != null;
    }

    @Override
    public boolean validateDelete() {
        return validateUpdate();
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
