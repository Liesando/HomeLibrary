package com.azzgil.homelibrary.utils;

import com.azzgil.homelibrary.model.Book;
import com.azzgil.homelibrary.model.Genre;
import org.hibernate.Session;

public class DataUtils {

    private DataUtils() {}

    public static Book[] fetchAllBooks() {
        Object[] objects = fetchAllObjects("from Book");

        Book[] books = new Book[objects.length];
        for(int i=0; i < objects.length; ++i) {
            books[i] = (Book) objects[i];
        }

        return books;
    }

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

    public static Object[] fetchAllObjects(String fromClause) {
        Session session = HibernateUtil.openSession();
        Object[] objects = session.createQuery(fromClause).list().toArray();
        session.close();

        return objects;
    }
}
