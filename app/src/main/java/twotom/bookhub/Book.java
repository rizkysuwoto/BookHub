package twotom.bookhub;

/**
 * Created by Thomas on 2017-04-14.
 */
public class Book
{
    private String title;
    private String author;
    private String edition;
    private String ISBN10;
    private String ISBN13;

    private String condition;
    private double price;

    public Book()
    {
        title = "";
        author = "";
        edition = "";
        ISBN10 = "";
        ISBN13 = "";
        condition = "";
        price = 0.00;
    }

    public Book(String title, String author, String edition)
    {
        this.title = title;
        this.author = author;
        this.edition = edition;
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

    public String getEdition() {
        return edition;
    }

    public void setEdition(String edition) {
        this.edition = edition;
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

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }
}
