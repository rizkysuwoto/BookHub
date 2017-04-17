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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MyBooksActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_books);
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        final UserManager um = new UserManager();

        Book book = (Book) getIntent().getExtras().getSerializable("BOOK");
        if (book != null) {
            // Add this book to myBooks
            Log.d("BOOK", "Adding to myBooks: " + book);

            um.getCurrentUser().addBooktoMyBooks(book);
            db.child("users").child(um.getCurrentUser().getUsername()).setValue(um.getCurrentUser());
            db.child("forSale").push().setValue(new BookForSale(book, um.getCurrentUser()));
        }

        List<String> titles = new ArrayList<>();
        for (Book b : um.getCurrentUser().getMyBooks()) {
            titles.add(b.getTitle());
        }
        ListView list = (ListView) findViewById(R.id.listView_myBooks);
        list.setAdapter(new ArrayAdapter<>(MyBooksActivity.this, android.R.layout.simple_list_item_1, titles));
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent myIntent = new Intent(MyBooksActivity.this, BookSearchResultActivity.class);
                myIntent.putExtra("ISBN", um.getCurrentUser().getMyBooks().get(position).getISBN10());
                startActivity(myIntent);
            }
        });

    }
}
