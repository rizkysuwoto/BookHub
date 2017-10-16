package twotom.bookhub;

import android.os.Parcel;
import android.os.Parcelable;

class BookItem implements Parcelable {
    private String seller;
    private String condition;
    private double price;

    BookItem(String seller, String condition, double price) {
        this.seller = seller;
        this.condition = condition;
        this.price = price;
    }

    private BookItem(Parcel in) {
        seller = in.readString();
        condition = in.readString();
        price = in.readDouble();
    }

    public static final Parcelable.Creator<BookItem> CREATOR = new
      Parcelable.Creator<BookItem>() {
        @Override
        public BookItem createFromParcel(Parcel parcel) {
            return new BookItem(parcel);
        }

        @Override
        public BookItem[] newArray(int size) {
            return new BookItem[0];
        }
    };

    String getSeller() {
        return seller;
    }

    String getCondition() {
        return condition;
    }

    double getPrice() {
        return price;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(seller);
        parcel.writeString(condition);
        parcel.writeDouble(price);
    }
}
