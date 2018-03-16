package com.azzgil.homelibrary;

import com.azzgil.homelibrary.model.*;
import com.azzgil.homelibrary.utils.AlertUtil;
import com.azzgil.homelibrary.utils.DataUtils;
import com.azzgil.homelibrary.utils.FXMLUtils;
import com.azzgil.homelibrary.utils.HibernateUtils;
import com.azzgil.homelibrary.views.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

/**
 * HomeLibrary
 *
 * Главный класс приложения. Отвечает за запуск и конфигурацию программы,
 * отображение интерфейса и открытие окон создания/редактирования сущностей.
 *
 * @author Sergey Medelyan & Maria Laktionova
 * @version 1.0-DEV 16 March 2018
 */
public class HomeLibrary extends Application {

    /** Заголовок окна приложения */
    private static final String APP_TITLE = "Домашняя библиотека";
    private static HomeLibrary instance;

    /** Объект-контейнер для всего содержимого окна (можно считать его синонимом окна) */
    private Stage primaryStage;

    /**
     * Корневой элемент-контейнер для общего GUI окна.
     * В его соответствующие области (Top, Right, Bottom, Left, Center)
     * далее добавляются динамически подгружаемые GUI конкретных секций,
     * с которыми работает пользователь (например, GUI просмотра
     * всех книг в библиотеке)
     */
    private BorderPane rootLayout;

    /**
     * Текущий контроллер (для передачи управления при вызове
     * функций CUD (см. {@link ICUDController}))
     */
    private ICUDController currentController;

    /**
     * Корневые элементы-контейнеры (...Layout) для соотетствующих
     * GUI-секций и их контроллеры (...Controller)
     */
    private AnchorPane bookOverviewLayout;
    private BookOverviewController bookOverviewController;
    private AnchorPane genresOverviewLayout;
    private GenresOverviewController genresOverviewController;
    private AnchorPane libraryOverviewLayout;
    private LibraryOverviewController libraryOverviewController;
    private AnchorPane pubHousesOverviewLayout;
    private PublishingHousesOverviewController pubHousesOverviewController;
    private AnchorPane friendsOverviewLayout;
    private FriendsOverviewController friendsOverviewController;

    private Book[] allBooks;
    private String[] allAuthors;
    private Genre[] allGenres;
    private PublishingHouse[] allPubHouses;
    private Friend[] allFriends;

    public static Book[] getAllBooks() {
        return instance.allBooks;
    }

    public static String[] getAllAuthors() {
        return instance.allAuthors;
    }

    public static Genre[] getAllGenres() {
        return instance.allGenres;
    }

    public static PublishingHouse[] getAllPubHouses() {
        return instance.allPubHouses;
    }

    public static Friend[] getAllFriends() {
        return instance.allFriends;
    }

    /**
     * Обновляет список книг (использовать, когда пользователь
     * изменил например, имя жанра или издательства)
     */
    public static void refreshBooks() {
        instance.allBooks = DataUtils.fetchAllBooks();
    }

    /**
     * Обновляет список авторов (использовать, когда пользователь
     * создал книгу)
     */
    public static void refreshAuthors() {
        instance.allAuthors = DataUtils.fetchAllAuthors(instance.allBooks);
    }

    /**
     * Обновляет список жанров
     */
    public static void refreshGenres() {
        instance.allGenres = DataUtils.fetchAllGenres();
    }

    /**
     * Обновляет список издательств
     */
    public static void refreshPublishingHouses() {
        instance.allPubHouses = DataUtils.fetchAllPubHouses();
    }

    public static void refreshFriends() {
        instance.allFriends = DataUtils.fetchAllFriends();
    }

    /** Просто главный метод */

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Метод, вызываемый JavaFX при вызове launch(args).
     * Настраивает и запускает приложение.
     *
     * @param primaryStage объект-контейнер
     */
    @Override
    public void start(Stage primaryStage) {
        instance = this;
        registerMappingClasses();
        HibernateUtils.buildSessionFactory();

        this.primaryStage = primaryStage;
        this.primaryStage.setTitle(APP_TITLE);

        refreshBooks();
        refreshAuthors();
        refreshGenres();
        refreshPublishingHouses();
        refreshFriends();

        initRootLayout();
        loadSectionLayouts();
        showSectionLayout(Section.Library);
    }

    /**
     * Вспомогательный метод, регистрирующий классы
     * модели (требуется для корреткного отображения данных базы
     * в объекты средствами Hibernate).
     */
    private void registerMappingClasses() {
        HibernateUtils.registerMappingClass(PublishingHouse.class);
        HibernateUtils.registerMappingClass(Genre.class);
        HibernateUtils.registerMappingClass(Book.class);
        HibernateUtils.registerMappingClass(Friend.class);
        HibernateUtils.registerMappingClass(Borrowing.class);
    }

    /**
     * Загружает и настраивает основной каркас интерфейса приложения
     */
    private void initRootLayout() {
        try {
            FXMLLoader loader = FXMLUtils.configureLoaderFor("views/RootLayout.fxml");
            rootLayout = loader.load();
            RootLayoutController controller = loader.getController();
            controller.setHomeLibrary(this);

            Scene scene = new Scene(rootLayout);
            rootLayout.getStylesheets().add("views/theme.css");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();
        } catch (IOException e) {
            AlertUtil.showDataCorruptionErrorAndWait("views/RootLayout.fxml");
            Platform.exit();
        }
    }

    /**
     * Загружает в память интерфейс секций (см. {@link Section} и их
     * контроллеры
     */
    private void loadSectionLayouts() {
        String missingFile = "views/BookOverview.fxml";
        try {
            FXMLLoader loader = FXMLUtils.configureLoaderFor(missingFile);
            bookOverviewLayout = loader.load();
            bookOverviewController = loader.getController();

            missingFile = "views/LibraryOverview.fxml";
            loader = FXMLUtils.configureLoaderFor(missingFile);
            libraryOverviewLayout = loader.load();
            libraryOverviewController = loader.getController();

            missingFile = "views/GenresOverview.fxml";
            loader = FXMLUtils.configureLoaderFor(missingFile);
            genresOverviewLayout = loader.load();
            genresOverviewController = loader.getController();

            missingFile = "views/PublishingHousesOverview.fxml";
            loader = FXMLUtils.configureLoaderFor(missingFile);
            pubHousesOverviewLayout = loader.load();
            pubHousesOverviewController = loader.getController();

            missingFile = "views/FriendsOverview.fxml";
            loader = FXMLUtils.configureLoaderFor(missingFile);
            friendsOverviewLayout = loader.load();
            friendsOverviewController = loader.getController();

        } catch (IOException e) {
            e.printStackTrace();
            AlertUtil.showDataCorruptionErrorAndWait(missingFile);
            Platform.exit();
        }
    }

    /**
     * Отображает интерфейс указанной секции меню
     *
     * @param section Секция меню
     */
    public void showSectionLayout(Section section) {
        switch (section) {

            case Library:
                rootLayout.setCenter(libraryOverviewLayout);
                currentController = null;
                break;

            case Books:
                rootLayout.setCenter(bookOverviewLayout);
                currentController = bookOverviewController;
                break;

            case Genres:
                rootLayout.setCenter(genresOverviewLayout);
                currentController = genresOverviewController;
                break;

            case PublishingHouses:
                rootLayout.setCenter(pubHousesOverviewLayout);
                currentController = pubHousesOverviewController;
                break;

            case Friends:
                rootLayout.setCenter(friendsOverviewLayout);
                currentController = friendsOverviewController;
                break;

            default:
                rootLayout.setCenter(null);
                AlertUtil.showNotRealizedWarningAndWait();
                break;
        }
    }

    /**
     * Этот метод вызывется JavaFX при завершении приложения через вызов
     * Platform.exit() или при закрытии последнего Stage. Предназначен
     * для корректного завершения работы, освобождения ресурсов, сохранений
     * и т. д.
     */
    public void stop() {
        releaseResources();
    }

    /**
     * Освобождает занятые ресурсы.
     */
    private void releaseResources() {
        HibernateUtils.destroySessionFactory();
    }

    public static BookOverviewController getBookOverviewController() {
        return instance.bookOverviewController;
    }

    public static GenresOverviewController getGenresOverviewController() {
        return instance.genresOverviewController;
    }

    public static LibraryOverviewController getLibraryOverviewController() {
        return instance.libraryOverviewController;
    }

    public static PublishingHousesOverviewController getPubHousesOverviewController() {
        return instance.pubHousesOverviewController;
    }

    public static FriendsOverviewController getFriendsOverviewController() {
        return instance.friendsOverviewController;
    }

    public ICUDController getCurrentController() {
        return currentController;
    }

    /**
     * Создаёт окно редактирования жанра в одном из двух режимов:
     * создание жанра (editMode = false) и редактирование жанра
     * (editMode = false).
     *
     * @param editMode включить режим редактирования?
     */
    public static boolean showGenreEditWindow(boolean editMode, Genre editable) {
        try {
            FXMLLoader loader = FXMLUtils.configureLoaderFor("views/GenreEditWindow.fxml");

            Stage stage = new Stage(StageStyle.UTILITY);
            stage.setTitle("Жанр");
            stage.setScene(new Scene(loader.load()));
            GenreEditWindowController controller = loader.getController();
            controller.setPrimaryStage(stage);

            if(editMode) {
                controller.switchToEditMode(editable);
            }
            stage.showAndWait();

            if(controller.isSuccessful()) {
                HomeLibrary.refreshGenres();
                if(editMode) {

                    // могли измениться названия жанров или их категории,
                    // так что обновляем эти изменения в описаниях книг
                    HomeLibrary.getBookOverviewController().refreshBooks();
                } else {

                    // добавился новый жанр, обновляем общую информацию
                    HomeLibrary.getLibraryOverviewController().refreshOverallInfo();
                }

                instance.genresOverviewController.refreshGenres();
            } else {
                return false;
            }

        } catch (IOException e) {
            AlertUtil.showDataCorruptionErrorAndWait("views/GenreEditWindow.fxml");
            e.printStackTrace();
            Platform.exit();
        }

        return true;
    }

    /**
     * Создаёт окно редактирования издательств в одном из двух режимов:
     * создание издательств (editMode = false) и редактирование издательств
     * (editMode = false).
     *
     * @param editMode включить режим редактирования?
     */
    public static boolean showPubHouseEditWindow(boolean editMode, PublishingHouse pubHouse) {
        try {
            FXMLLoader loader = FXMLUtils.configureLoaderFor("views/PublishingHouseEditWindow.fxml");

            Stage stage = new Stage(StageStyle.UTILITY);
            stage.setTitle("Издательство");
            stage.setScene(new Scene(loader.load()));
            PubHouseEditWindowController controller = loader.getController();
            controller.setPrimaryStage(stage);

            if(editMode) {
                controller.switchToEditMode(pubHouse);
            }
            stage.showAndWait();

            if(controller.isSuccessful()) {
                HomeLibrary.refreshPublishingHouses();
                instance.pubHousesOverviewController.refreshPubHouses();

                if(editMode) {

                    // могли измениться названия издательств
                    HomeLibrary.refreshBooks();
                    HomeLibrary.getBookOverviewController().refreshBooks();
                } else {

                    // меняется количество издательств
                    HomeLibrary.getLibraryOverviewController().refreshOverallInfo();
                }
            } else {
                return false;
            }

        } catch (IOException e) {
            AlertUtil.showDataCorruptionErrorAndWait("views/GenreEditWindow.fxml");
            e.printStackTrace();
            Platform.exit();
        }

        return true;
    }

    /**
     * Создаёт окно редактирования книги в одном из двух режимов:
     * создание жанра (editMode = false) и редактирование жанра
     * (editMode = false).
     *
     * @param editMode включить режим редактирования?
     */
    public static void showBookEditWindow(boolean editMode, Book book) {
        try {
            FXMLLoader loader = FXMLUtils.configureLoaderFor("views/BookEditWindow.fxml");

            Stage stage = new Stage(StageStyle.UTILITY);
            stage.setTitle("Книга");
            stage.setScene(new Scene(loader.load()));
            BookEditWindowController controller = loader.getController();
            controller.setPrimaryStage(stage);

            if(editMode) {
                controller.switchToEditMode(book);
            }
            stage.showAndWait();

            if(controller.isSuccessful()) {
                HomeLibrary.refreshBooks();
                instance.bookOverviewController.refreshBooks();
                if(!editMode) {
                    HomeLibrary.getLibraryOverviewController().refreshOverallInfo();
                }
            }

        } catch (IOException e) {
            AlertUtil.showDataCorruptionErrorAndWait("views/GenreEditWindow.fxml");
            e.printStackTrace();
            Platform.exit();
        }
    }

    /**
     * Создаёт окно редактирования друга в одном из двух режимов:
     * создание друга (editMode = false) и редактирование друга
     * (editMode = false).
     *
     * @param editMode включить режим редактирования?
     */
    public static boolean showFriendEditWindow(boolean editMode, Friend friend) {
        try {
            FXMLLoader loader = FXMLUtils.configureLoaderFor("views/FriendEditWindow.fxml");

            Stage stage = new Stage(StageStyle.UTILITY);
            stage.setTitle("Друг");
            stage.setScene(new Scene(loader.load()));
            FriendEditWindowController controller = loader.getController();
            controller.setPrimaryStage(stage);

            if(editMode) {
                controller.switchToEditMode(friend);
            }
            stage.showAndWait();

            if(controller.isSuccessful()) {
                HomeLibrary.refreshFriends();
                instance.friendsOverviewController.refreshFriends();
                if(!editMode) {
                    instance.libraryOverviewController.refreshOverallInfo();
                }
            } else {
                return false;
            }

        } catch (IOException e) {
            AlertUtil.showDataCorruptionErrorAndWait("views/GenreEditWindow.fxml");
            e.printStackTrace();
            Platform.exit();
        }

        return true;
    }

    /**
     * Создаёт окно редактирования займа в одном из режимов:
     * создание, и редактирование жанра
     * (editMode = false).
     *
     * @param mode Режим контроллера
     */
    public static boolean showBorrowingEditWindow(BorrowingEditWindowController.BorrowMode mode, Borrowing borrowing) {
        try {
            FXMLLoader loader = FXMLUtils.configureLoaderFor("views/BorrowingEditWindow.fxml");

            Stage stage = new Stage(StageStyle.UTILITY);
            stage.setTitle("Займ книги");
            stage.setScene(new Scene(loader.load()));
            BorrowingEditWindowController controller = loader.getController();
            controller.setPrimaryStage(stage);

            controller.changeMode(mode, borrowing);
            stage.showAndWait();

            if(controller.isSuccessful()) {
                HomeLibrary.refreshBooks();
                HomeLibrary.refreshFriends();
                instance.bookOverviewController.refreshBooks();
                instance.friendsOverviewController.refreshFriends();
            } else {
                return false;
            }

        } catch (IOException e) {
            AlertUtil.showDataCorruptionErrorAndWait("views/GenreEditWindow.fxml");
            e.printStackTrace();
            Platform.exit();
        }

        return true;
    }

    public static String showFilterBooksWindow() {
        try {
            FXMLLoader loader = FXMLUtils.configureLoaderFor("views/FilterBooksWindow.fxml");

            Stage stage = new Stage(StageStyle.UTILITY);
            stage.setTitle("Фильтр");
            stage.setScene(new Scene(loader.load()));
            FilterBooksWindowController controller = loader.getController();
            controller.setPrimaryStage(stage);

            stage.showAndWait();
            return controller.getFormedQuery();

        } catch (IOException e) {
            AlertUtil.showDataCorruptionErrorAndWait("views/GenreEditWindow.fxml");
            e.printStackTrace();
            Platform.exit();
        }

        return null;
    }
}
