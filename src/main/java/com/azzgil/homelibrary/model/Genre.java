package com.azzgil.homelibrary.model;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.Collection;

/**
 * Genre
 *
 * Жанр книги в библиотеке. Помимо непосредственной информации о себе
 * содержит информацию о книгах, относящихся к этому жанру (см. {@link Book})
 *
 * @version 1.3 8 March 2018
 * @author Sergey Medelyan
 */
@Entity
@Table(name = "Genres")
public class Genre {
    private int id;
    private String name;
    private Integer parentId;

    /**
     * Книги данного жанра (но это не означает, что они
     * <i>только</i> этого жанра, наоборот, они могут принадлежать
     * и другому жанру).
     */
    private Collection<Book> books;
    private Genre parentGenre;
    private Collection<Genre> childGenres;


    public Genre() {}


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "genre_seq_gen")
    @SequenceGenerator(name = "genre_seq_gen",
            sequenceName = "genre_seq", allocationSize = 20)
    @Column(name = "id_genre")
    public int getId() {
        return id;
    }

    @Column(name = "genre_name")
    public String getName() {
        return name;
    }

    @Column(name = "id_parent_genre")
    public Integer getParentId() {
        return parentId;
    }

    @ManyToMany(mappedBy = "genres")
    @LazyCollection(LazyCollectionOption.FALSE)
    public Collection<Book> getBooks() {
        return books;
    }

    @ManyToOne
    @JoinColumn(name = "id_parent_genre", insertable = false, updatable = false)
    public Genre getParentGenre() {
        return parentGenre;
    }

    @OneToMany(mappedBy = "parentGenre", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    public Collection<Genre> getChildGenres() {
        return childGenres;
    }

    public void setChildGenres(Collection<Genre> childGenres) {
        this.childGenres = childGenres;
    }

    public void setParentGenre(Genre parentGenre) {
        this.parentGenre = parentGenre;
    }

    public void setBooks(Collection<Book> books) {
        this.books = books;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Genre)) {
            return false;
        }

        return id == ((Genre) obj).id;
    }

    /**
     * Возвращает полное имя жанра в формате:
     * < и т. д. > / <имя предка предка> / <имя предка> / <имя жанра>
     * Если name == null, то в качестве имени жанра ( <имя жанра> )
     * берётся пустая строка
     *
     * @return Полное имя жанра
     */
    @Transient
    public String getFullName() {
        String niceName = getName() == null ? "" : getName();
        if(getParentGenre() == null) {
            return niceName;
        }
        return getParentGenre().getFullName().concat(" / " + niceName);
    }
}
