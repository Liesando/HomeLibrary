package com.azzgil.homelibrary.model;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.Collection;

/**
 * PublishingHouse
 *
 * Представляет издательство книги и его свойства.
 * Дополнительно содержит информацию о книгах, изданных
 * данным издательством.
 *
 * @version 1.1 4 March 2018
 * @author Sergey Medelyan & Maria Laktionova
 */
@Entity
@Table(name = "Publishing_Houses")
public class PublishingHouse {

    private int id;
    private String name;

    /** Книги, изданные данным издательством */
    private Collection<Book> books;


    public PublishingHouse() {}


    @Id
    @Column(name = "id_pub_house")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pub_house_id_gen")
    @SequenceGenerator(name = "pub_house_id_gen", sequenceName = "pub_house_seq")
    public int getId() {
        return id;
    }

    @Column(name = "pub_house_name")
    public String getName() {
        return name;
    }

    @OneToMany(mappedBy = "publishingHouse")
    @LazyCollection(LazyCollectionOption.FALSE)
    public Collection<Book> getBooks() {
        return books;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBooks(Collection<Book> books) {
        this.books = books;
    }

    @Override
    public String toString() {
        return getName();
    }
}
