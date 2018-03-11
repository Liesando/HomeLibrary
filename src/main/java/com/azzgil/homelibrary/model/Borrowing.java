package com.azzgil.homelibrary.model;

import javax.persistence.*;
import java.util.Date;

/**
 * Borrowing
 *
 * Представляет займ книги ({@link Book}) другом ({@link Friend}).
 *
 * @version 1.1 11 March 2018
 * @author Sergey Medelyan
 */
@Entity
@Table(name = "Borrowing")
public class Borrowing {
    private BorrowingId id;

    /**
     * Из-за внутреннего представления булевых значений в базе
     * используется тип char: 'f' или 'F' - false,
     * 't' или 'T' - true.
     */
    private char isLost;
    private char isDamaged;
    private Date returnDate;
    private String commentary;
    private Book bookBorrowed;
    private Friend friendBorrowed;


    public Borrowing(){}


    @EmbeddedId
    public BorrowingId getId() {
        return id;
    }

    @Column(name = "commentary")
    public String getCommentary() {
        return commentary;
    }

    @Column(name = "is_damaged")
    public char getIsDamaged() {
        return isDamaged;
    }

    @Column(name = "is_lost")
    public char getIsLost() {
        return isLost;
    }

    @Column(name = "return_date")
    public Date getReturnDate() {
        return returnDate;
    }

    @ManyToOne
    @JoinColumn(name = "id_book", insertable =  false, updatable = false)
    public Book getBookBorrowed() {
        return bookBorrowed;
    }

    @ManyToOne
    @JoinColumn(name = "id_friend", insertable = false, updatable = false)
    public Friend getFriendBorrowed() {
        return friendBorrowed;
    }

    public void setIsLost(char isLost) {
        this.isLost = isLost;
    }

    public void setIsDamaged(char isDamaged) {
        this.isDamaged = isDamaged;
    }

    public void setFriendBorrowed(Friend friendBorrowed) {
        this.friendBorrowed = friendBorrowed;
    }

    public void setBookBorrowed(Book bookBorrowed) {
        this.bookBorrowed = bookBorrowed;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    public void setId(BorrowingId id) {
        this.id = id;
    }

    public void setCommentary(String commentary) {
        this.commentary = commentary;
    }

    @Override
    public String toString() {
        return bookBorrowed.toString() + " borrowed by " + friendBorrowed.toString();
    }
}
