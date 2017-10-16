package twotom.bookhub;

/**
 * Created by tq on 4/17/2017.
 */

public class BookForSale extends Book {
    private User seller;

    public BookForSale() {
        super();
        seller = null;
    }

    public BookForSale(Book book, User seller) {
        setTitle(book.getTitle());
        setAuthor(book.getAuthor());
        setPublisher(book.getPublisher());
        setISBN10(book.getISBN10());
        setISBN13(book.getISBN13());
        this.seller = seller;
    }

    public User getSeller() {
        return seller;
    }

    public void setSeller(User seller) {
        this.seller = seller;
    }
}
