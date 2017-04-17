package twotom.bookhub;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BooksForSaleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books_for_sale);
        final String ISBN = getIntent().getExtras().getString("ISBN");
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        final UserManager um = new UserManager();
        final List<BookForSale> books = new ArrayList<>();

        db.child("forSale").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.getValue(BookForSale.class).getISBN10().equals(ISBN)) {
                        books.add(snapshot.getValue(BookForSale.class));
                    }
                }
                Log.d("BOOK", books.toString());
                List<String> titles = new ArrayList<>();
                for (BookForSale b : books) {
                    titles.add(b.getSeller().getUsername() + ", rated " + b.getSeller().getSellerRating() + "/5");
                }
                ListView list = (ListView) findViewById(R.id.listView_booksForSale);
                list.setAdapter(new ArrayAdapter<>(BooksForSaleActivity.this, android.R.layout.simple_list_item_1, titles));
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent myIntent = new Intent(BooksForSaleActivity.this, BookForSaleActivity.class);
                        myIntent.putExtra("BOOK", books.get(position));
                        startActivity(myIntent);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
}
