package twotom.bookhub;

import java.io.Serializable;

/**
 * Created by Thomas on 2017-04-14.
 */
public class Book implements Serializable
{
    private String title;
    private String author;
    private String publisher;
    private String ISBN10;
    private String ISBN13;

    private String condition;
    private double price;

    public Book()
    {
        title = "";
        author = "";
        publisher = "";
        ISBN10 = "";
        ISBN13 = "";
        condition = "";
        price = 0.00;
    }

    public Book(String title, String author, String publisher)
    {
        this.title = title;
        this.author = author;
        this.publisher = publisher;
    }

    //-----------------------------------------------------
    // Getters and Setters
    //-----------------------------------------------------

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getISBN10() {
        return ISBN10;
    }

    public void setISBN10(String ISBN10) {
        this.ISBN10 = ISBN10;
    }

    public String getISBN13() {
        return ISBN13;
    }

    public void setISBN13(String ISBN13) {
        this.ISBN13 = ISBN13;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "title: " + title + "\n" +
                "author: " + author + "\n" +
                "publisher: " + publisher + "\n" +
                "ISBN10: " + ISBN10 + "\n" +
                "ISBN13: " + ISBN13;
    }
}
