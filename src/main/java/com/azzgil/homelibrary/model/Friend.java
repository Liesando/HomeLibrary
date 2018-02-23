package com.azzgil.homelibrary.model;

import javax.persistence.*;
import java.util.Collection;

/**
 * Friend
 *
 * Друг владельца домашней библиотеки одалживает у него книги.
 * Возможно, возвращает их нет, возможно - нет. Также может книгу
 * потерять или повердить (эта информация хранится в {@link Borrowing}).
 * Дополнительно содержит информацию о совершённых данным другом
 * займах ({@link Borrowing}).
 *
 * @version 1.0 23 Feb 2018
 * @author Sergey Medelyan
 */
@Entity
@Table(name = "Friends")
public class Friend {

    private int id;
    private String fio;
    private String phoneNumber;
    private int socialNumber;
    private String email;
    private String commentary;

    /** История займов */
    private Collection<Borrowing> bookBorrowings;


    public Friend() {}


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_friend")
    public int getId() {
        return id;
    }

    @Column(name = "commentary")
    public String getCommentary() {
        return commentary;
    }

    @Column(name = "social_number")
    public int getSocialNumber() {
        return socialNumber;
    }

    @Column(name = "email")
    public String getEmail() {
        return email;
    }

    @Column(name = "fio")
    public String getFio() {
        return fio;
    }

    @Column(name = "phone_number")
    public String getPhoneNumber() {
        return phoneNumber;
    }

    @OneToMany
    @JoinColumn(name = "id_friend")
    public Collection<Borrowing> getBookBorrowings() {
        return bookBorrowings;
    }

    public void setCommentary(String commentary) {
        this.commentary = commentary;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSocialNumber(int socialNumber) {
        this.socialNumber = socialNumber;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFio(String fio) {
        this.fio = fio;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setBookBorrowings(Collection<Borrowing> bookBorrowings) {
        this.bookBorrowings = bookBorrowings;
    }

    @Override
    @Transient
    public String toString() {
        return getFio();
    }
}
