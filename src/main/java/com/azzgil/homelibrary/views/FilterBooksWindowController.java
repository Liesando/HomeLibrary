package com.azzgil.homelibrary.views;

import com.azzgil.homelibrary.HomeLibrary;
import com.azzgil.homelibrary.model.Genre;
import com.azzgil.homelibrary.model.PublishingHouse;
import com.azzgil.homelibrary.utils.GUIUtils;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.controlsfx.control.CheckListView;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * FilterBooksWindowController
 *
 * Контроллер окна фильтрации. Основная функция - сформировать строку
 * запроса с учётом выбранных пользователем критериев.
 *
 * @author Sergey Medelyan & Maria Laktionova
 * @version 1.0 16 March 2018
 */
public class FilterBooksWindowController {

    public enum YearComparisonMethod { LESS_OR_EQUALS, EQUALS, MORE_OR_EQUALS }

    private String formedQuery;
    private Stage primaryStage;

    @FXML private CheckBox byTitleCb;
    @FXML private CheckBox byAuthorCb;
    @FXML private CheckBox byGenresCb;
    @FXML private CheckBox byYearCb;
    @FXML private CheckBox byPublisherCb;
    @FXML private CheckBox byTranslatorCb;
    @FXML private CheckBox byIllustratorCb;
    @FXML private CheckBox byCommentCb;

    @FXML private TextField bookTitleTF;
    @FXML private ComboBox<String> authorCB;
    @FXML private CheckListView<Genre> genresCLV;
    @FXML private ChoiceBox<YearComparisonMethod> comparisonMethodCB;
    @FXML private TextField yearPublishedTF;
    @FXML private ComboBox<PublishingHouse> pubHouseCB;
    @FXML private TextField translatorTF;
    @FXML private TextField illustratorTF;
    @FXML private TextField commentaryTF;
    @FXML private Button filterBtn;

    @FXML
    private void initialize() {
        genresCLV.setCellFactory(listView -> new CheckBoxListCell<>(genresCLV::getItemBooleanProperty) {
            @Override
            public void updateItem(Genre item, boolean empty) {
                super.updateItem(item, empty);
                setText(item == null ? GUIUtils.STD_NOT_SPECIFIED_REPLACER : item.getFullName());
            }
        });

        authorCB.setItems(FXCollections.observableList(
                Arrays.asList(HomeLibrary.getAllAuthors())));

        setupComparisonMethods();
        refreshGenres();
        refreshPubHouses();
    }

    private void setupComparisonMethods() {
        comparisonMethodCB.setItems(FXCollections.observableArrayList());
        comparisonMethodCB.getItems().add(YearComparisonMethod.LESS_OR_EQUALS);
        comparisonMethodCB.getItems().add(YearComparisonMethod.EQUALS);
        comparisonMethodCB.getItems().add(YearComparisonMethod.MORE_OR_EQUALS);
        comparisonMethodCB.setConverter(new StringConverter<>() {
            @Override
            public String toString(YearComparisonMethod object) {
                switch (object) {
                    case LESS_OR_EQUALS:
                        return "до (включительно)";

                    case EQUALS:
                        return "точно";

                    case MORE_OR_EQUALS:
                        return "с";

                    default:
                        throw new RuntimeException("WRONG COMPARISON METHOD");
                }
            }

            @Override
            public YearComparisonMethod fromString(String string) {
                switch (string) {
                    case "до (включительно)":
                        return YearComparisonMethod.LESS_OR_EQUALS;

                    case "точно":
                        return YearComparisonMethod.EQUALS;

                    case "с":
                        return YearComparisonMethod.MORE_OR_EQUALS;

                    default:
                        throw new RuntimeException("WRONG STRING INPUT");
                }
            }
        });
    }

    private void refreshGenres() {
        genresCLV.setItems(FXCollections.observableList(
                Arrays.asList(HomeLibrary.getAllGenres())));
    }

    private void refreshPubHouses() {
        pubHouseCB.setItems(FXCollections.observableList(
                Arrays.asList(HomeLibrary.getAllPubHouses())));
    }

    @FXML
    private void reactivateGUI() {
        bookTitleTF.setDisable(!byTitleCb.isSelected());
        authorCB.setDisable(!byAuthorCb.isSelected());
        genresCLV.setDisable(!byGenresCb.isSelected());
        comparisonMethodCB.setDisable(!byYearCb.isSelected());
        yearPublishedTF.setDisable(!byYearCb.isSelected());
        pubHouseCB.setDisable(!byPublisherCb.isSelected());
        translatorTF.setDisable(!byTranslatorCb.isSelected());
        illustratorTF.setDisable(!byIllustratorCb.isSelected());
        commentaryTF.setDisable(!byCommentCb.isSelected());

        filterBtn.setDisable(!(byTitleCb.isSelected() || byAuthorCb.isSelected()
                || byGenresCb.isSelected() || byYearCb.isSelected()
                || byPublisherCb.isSelected() || byTranslatorCb.isSelected()
                || byIllustratorCb.isSelected() || byCommentCb.isSelected()));
    }

    @FXML
    private void onCancelBtn() {
        formedQuery = null;
        primaryStage.close();
    }

    @FXML
    private void onFilterBtn() {
        StringBuilder query = new StringBuilder("select b from Book b ");
        boolean appendAnd = false;
        validateData();

        if(byGenresCb.isSelected()
                && genresCLV.getCheckModel().getCheckedItems().size() > 0) {

            // выглядит страшно и уродливо; если двумя словами -
            // вытаскивает из выбранных жанров их айдишники
            // и вставляет в нужно место запроса
            String ids = String.join(",", genresCLV.getCheckModel().getCheckedItems()
                    .stream().map(genre -> Integer.toString(genre.getId())).collect(Collectors.toList()));
            query.append(String.format("join b.genres as genre with genre.id in (%s) ", ids));
        }

        if(byTitleCb.isSelected()
                || byAuthorCb.isSelected()
                || byYearCb.isSelected()
                || byPublisherCb.isSelected()) {
            query.append("where ");

            if (byTitleCb.isSelected()) {
                appendAnd = true;
                query.append(String.format("lower(b.name) like '%%%s%%'",
                        bookTitleTF.getText().trim().toLowerCase()));
            }

            if (byAuthorCb.isSelected()) {
                if (appendAnd) {
                    query.append(" and ");
                }
                appendAnd = true;
                query.append(String.format("lower(b.author) like '%%%s%%'",
                        authorCB.getValue().trim().toLowerCase()));
            }

            if (byYearCb.isSelected()) {
                if (appendAnd) {
                    query.append(" and ");
                }
                appendAnd = true;
                query.append("b.year");
                switch (comparisonMethodCB.getValue()) {

                    case LESS_OR_EQUALS:
                        query.append("<=");
                        break;

                    case EQUALS:
                        query.append("=");
                        break;

                    case MORE_OR_EQUALS:
                        query.append(">=");
                        break;

                    default:
                        throw new RuntimeException("COMPARISON METHOD IS WRONG");
                }
                // ну фиг его знает, что там может криво лежать, лучше перестрахуюсь=)
                query.append(Integer.toString(Integer.parseInt(yearPublishedTF.getText().trim())));
            }

            if (byPublisherCb.isSelected()) {
                if (appendAnd) {
                    query.append(" and ");
                }
                appendAnd = true;
                query.append(String.format("b.publishingHouse.id=%d", pubHouseCB.getValue().getId()));
            }

            if(byTranslatorCb.isSelected()) {
                if(appendAnd) {
                    query.append(" and ");
                }
                appendAnd = true;
                query.append(String.format("lower(b.translator) like '%%%s%%'",
                        translatorTF.getText().trim().toLowerCase()));
            }

            if(byIllustratorCb.isSelected()) {
                if(appendAnd) {
                    query.append(" and ");
                }
                appendAnd = true;
                query.append(String.format("lower(b.picAuthor) like '%%%s%%'",
                        illustratorTF.getText().trim().toLowerCase()));
            }

            if(byCommentCb.isSelected()) {
                if(appendAnd) {
                    query.append(" and ");
                }
                query.append(String.format("lower(b.commentary) like '%%%s%%'",
                        commentaryTF.getText().trim().toLowerCase()));
            }
        }

        formedQuery = query.toString();
        primaryStage.close();
    }

    private void validateData() {
        if(byTitleCb.isSelected()
                && bookTitleTF.getText().trim().length() == 0) {
            byTitleCb.setSelected(false);
        }

        if(byAuthorCb.isSelected()
                && (authorCB.getValue() == null
                || authorCB.getValue().trim().length() == 0)) {
            byAuthorCb.setSelected(false);
        }

        if(byYearCb.isSelected()) {
            if (comparisonMethodCB.getValue() == null
                    || yearPublishedTF.getText().trim().length() == 0) {
                byYearCb.setSelected(false);
            } else {
                try {
                    Integer.parseInt(yearPublishedTF.getText().trim());
                } catch (NumberFormatException e) {
                    byYearCb.setSelected(false);
                }
            }
        }

        if(byPublisherCb.isSelected()
                && pubHouseCB.getValue() == null) {
            byPublisherCb.setSelected(false);
        }

        if(byTranslatorCb.isSelected()
                && translatorTF.getText().trim().length() == 0) {
            byTranslatorCb.setSelected(false);
        }

        if(byIllustratorCb.isSelected()
                && illustratorTF.getText().trim().length() == 0) {
            byIllustratorCb.setSelected(false);
        }

        if(byCommentCb.isSelected()
                && commentaryTF.getText().trim().length() == 0) {
            byCommentCb.setSelected(false);
        }
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public String getFormedQuery() {
        return formedQuery;
    }
}
