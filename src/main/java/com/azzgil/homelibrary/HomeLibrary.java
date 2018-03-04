package com.azzgil.homelibrary;

import com.azzgil.homelibrary.model.*;
import com.azzgil.homelibrary.utils.AlertUtil;
import com.azzgil.homelibrary.utils.FXMLUtils;
import com.azzgil.homelibrary.utils.HibernateUtil;
import com.azzgil.homelibrary.views.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.hibernate.Session;

import java.io.IOException;
import java.util.*;
import java.util.List;


/**
 * HomeLibrary
 *
 * Главный класс приложения. Отвечает за запуск и конфигурацию программы,
 * отображение интерфейса.
 *
 * @version dev-0.6 2 March 2018
 * @author Sergey Medelyan
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
        HibernateUtil.buildSessionFactory();

        this.primaryStage = primaryStage;
        this.primaryStage.setTitle(APP_TITLE);

        initRootLayout();
        loadSectionLayouts();
        showSectionLayout(Section.Library);

        // TODO: delete when tests are finished
        runTests();
    }

    /**
     * Вспомогательный метод, регистрирующий классы
     * модели (требуется для корреткного отображения данных базы
     * в объекты средствами Hibernate).
     */
    private void registerMappingClasses() {
        HibernateUtil.registerMappingClass(PublishingHouse.class);
        HibernateUtil.registerMappingClass(Genre.class);
        HibernateUtil.registerMappingClass(Book.class);
        HibernateUtil.registerMappingClass(Friend.class);
        HibernateUtil.registerMappingClass(Borrowing.class);
    }

    /**
     * Временный метод, используемый для исследования
     * работы с Hibernate
     * TODO: удалить после завершения разработки
     */
    private void runTests()
    {
        Session s = HibernateUtil.openSession();
        List l = s.createQuery("from Genre").list();
        ArrayList<Genre> genres = new ArrayList<>();

        for(Iterator i = l.iterator(); i.hasNext();) {
            genres.add((Genre) i.next());
        }

        String message = Arrays.toString(genres.toArray());
        System.out.println(message);

        l = s.createQuery("from Book").list();
        Iterator i = l.iterator();
        if (i.hasNext()) {
            Book b = (Book) i.next();
            message = "Жанры: ";

            for (Genre g :
                    b.getGenres()) {
                message += g.getFullName() + ", ";
            }
            message += "\n";

            message += b.getPublishingHouse().toString() + "\n";
            message += b.toString() + "\nBorrowed by: ";

            Collection<Borrowing> ll = b.getBookBorrowings();

            for (Borrowing bw :
                    ll) {
                message += bw.toString() + "; ";
            }

            System.out.println(message);
        }

        l = s.createQuery("from Friend").list();
        i = l.iterator();
        if (i.hasNext()) {
            Friend f = (Friend) i.next();
            message = f.toString() + "\n" + f.getPhoneNumber() + "\n" + f.getSocialNumber() +
                    "\n" + f.getEmail() + "\n" + f.getCommentary();

            for (Borrowing bw :
                    f.getBookBorrowings()) {
                message += "\n" + bw.toString();
            }

            System.out.println(message);
        }

        s.close();
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
        HibernateUtil.destroySessionFactory();
    }

    public BookOverviewController getBookOverviewController() {
        return bookOverviewController;
    }

    public ICUDController getCurrentController() {
        return currentController;
    }

    /**
     * Обновляет список книг (использовать, когда пользователь
     * изменил например, имя жанра или издательства)
     */
    public static void refreshBooks() {
        instance.bookOverviewController.refreshBooks();
    }

    /**
     * Обновляет общую информацию, отображаемую в секции
     * "Библиотека" (использовать, когда пользователь
     * что-то удалил или создал).
     */
    public static void refreshOverallInfo() {
        instance.libraryOverviewController.refreshOverallInfo();
    }
}
