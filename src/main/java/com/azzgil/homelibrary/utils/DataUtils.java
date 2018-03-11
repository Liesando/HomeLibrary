package com.azzgil.homelibrary.utils;

import com.azzgil.homelibrary.model.Book;
import com.azzgil.homelibrary.model.Friend;
import com.azzgil.homelibrary.model.Genre;
import com.azzgil.homelibrary.model.PublishingHouse;
import org.hibernate.Session;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * DataUtils
 *
 * Класс-утилита, предоставляет функции для извлечения данных из базы
 * и вспомогательные функции для работы с этими данными.
 * TODO: обдумать, где должны лежать методы CRUD - здесь или в контроллерах
 *
 * @author Sergey Medelyan
 * @version 1.2 8 March 2018
 */
public class DataUtils {

    private DataUtils() {}

    /** Возвращает список всех книг в базе */
    public static Book[] fetchAllBooks() {
        return Arrays.stream(fetchAllObjects("from Book"))
                .toArray(Book[]::new);
    }

    /** Возвращает список всех жанров в базе */
    public static Genre[] fetchAllGenres() {
        return Arrays.stream(fetchAllObjects("from Genre"))
                .toArray(Genre[]::new);
    }

    /** Возвращает список всех издательств в базе */
    public static PublishingHouse[] fetchAllPubHouses() {
        return Arrays.stream(fetchAllObjects("from PublishingHouse"))
                .toArray(PublishingHouse[]::new);
    }

    public static Friend[] fetchAllFriends() {
        return Arrays.stream(fetchAllObjects("from Friend"))
                .toArray(Friend[]::new);
    }

    /**
     * Возвращает список всех авторов, существующих в базе
     *
     * @param allBooks Массив книг, из которого извлечётся информация об авторах
     * @return Массив имён авторов
     */
    public static String[] fetchAllAuthors(Book[] allBooks) {
        return Arrays.stream(allBooks).filter(
                distinctBy(Book::getAuthor)).map(Book::getAuthor).toArray(String[]::new);
    }

    private static <T> Predicate<T> distinctBy(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    /** Возвращает список всех объектов по указанному FROM-clause. */
    private static Object[] fetchAllObjects(String fromClause) {
        Session session = HibernateUtil.openSession();
        Object[] objects = session.createQuery(fromClause).list().toArray();
        session.close();

        return objects;
    }

    /**
     * Возвращает строку с именами (Book.toString()) книг, разделённых
     * переносом строки
     */
    public static String reduceBooks(Collection<Book> books) {
        if(books.size() == 1) {
            return books.iterator().next().toString();
        }

        Optional<Book> result = books.stream().reduce((book, book2) -> {
            Book b = new Book();
            b.setName(String.format("%1$s\n%2$s", book.toString(), book2.toString()));
            return b;
        });

        return result.isPresent() ? result.get().getName() : "";
    }

    /**
     * Возвращает массив книг, которые принадлежат указанному жанру
     * или его поджанрам.
     *
     * @param g Жанр
     * @return Массив книг данного жанра и его поджанров
     */
    public static ArrayList<Book> collectBooksOfGenre(Genre g) {
        ArrayList<Book> books = new ArrayList<>();
        ArrayList<Genre> genresToCheck = new ArrayList<>();
        genresToCheck.add(g);

        while(!genresToCheck.isEmpty()) {
            books.addAll(genresToCheck.get(0).getBooks());
            genresToCheck.addAll(genresToCheck.get(0).getChildGenres());
            genresToCheck.remove(0);
        }

        return books;
    }

    /**
     * Сохраняет или обновляет переданный объект в базе. Для этого
     * этот объект должен быть замапплен.
     *
     * @param o Сохраняемый объект
     */
    public static void saveOrUpdate(Object o) {
        Session session = HibernateUtil.openSession();
        session.beginTransaction();
        session.saveOrUpdate(o);
        session.getTransaction().commit();
        session.close();
    }
}
