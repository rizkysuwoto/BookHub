package twotom.bookhub;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class BookSearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_search);

        (findViewById(R.id.button_bookSearch_search)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                String searchISBN = ((EditText) findViewById(R.id.textfield_bookSearch_isbn)).getText().toString();

                // We're just using ISBN for the demo, we can filter by author/publisher later...

                Intent myIntent = new Intent(BookSearchActivity.this, BookSearchResultActivity.class);
                myIntent.putExtra("ISBN", searchISBN);
                startActivity(myIntent);
            }
        });
    }
}
