package com.azzgil.homelibrary.utils;

import com.azzgil.homelibrary.model.Book;
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
        Object[] objects = fetchAllObjects("from Book");

        Book[] books = new Book[objects.length];
        for(int i=0; i < objects.length; ++i) {
            books[i] = (Book) objects[i];
        }

        return books;
    }

    /** Возвращает список всех жанров в базе */
    public static Genre[] fetchAllGenres() {
        Object[] objects = fetchAllObjects("from Genre");

// genres = (Genre[]) objects;   < ------------- this one will fail with
// ClassCastException, so we'll work around it with following straight-forward
// code
        Genre[] genres = new Genre[objects.length];
        for(int i=0; i < objects.length; ++i) {
            genres[i] = (Genre) objects[i];
        }

        return genres;
    }

    /** Возвращает список всех издательств в базе */
    public static PublishingHouse[] fetchAllPubHouses() {
        Object[] objects = fetchAllObjects("from PublishingHouse");

        PublishingHouse[] pubHouses = new PublishingHouse[objects.length];
        for(int i = 0; i < objects.length; ++i) {
            pubHouses[i] = (PublishingHouse) objects[i];
        }

        return pubHouses;
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
    public static Object[] fetchAllObjects(String fromClause) {
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
}
