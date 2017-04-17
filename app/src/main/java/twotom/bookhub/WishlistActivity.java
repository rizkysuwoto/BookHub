package twotom.bookhub;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class WishlistActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        final UserManager um = new UserManager();
        if (getIntent().getExtras() != null) {
            Book book = (Book) getIntent().getExtras().getSerializable("BOOK");
            // Add this book to wishlist
            Log.d("BOOK", "Adding to wishlist: " + book);
            um.getCurrentUser().addBooktoWishlist(book);
            db.child("users").child(um.getCurrentUser().getUsername()).setValue(um.getCurrentUser());
        }

        List<String> titles = new ArrayList<>();
        for (Book b : um.getCurrentUser().getWishlist()) {
            titles.add(b.getTitle());
        }
        ListView list = (ListView) findViewById(R.id.listView_wishlist);
        list.setAdapter(new ArrayAdapter<>(WishlistActivity.this, android.R.layout.simple_list_item_1, titles));
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent myIntent = new Intent(WishlistActivity.this, BooksForSaleActivity.class);
                myIntent.putExtra("ISBN", um.getCurrentUser().getWishlist().get(position).getISBN10());
                startActivity(myIntent);
            }
        });
    }
}
