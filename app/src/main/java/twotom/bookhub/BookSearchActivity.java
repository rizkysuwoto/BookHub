package twotom.bookhub;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class BookSearchActivity extends AppCompatActivity {
    public static final int BOOK_SEACH_RESULT_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_search);
    }

    public void onSearch(View view) {
        final String ISBN =
            ((EditText) findViewById(R.id.textfield_bookSearch_isbn))
            .getText().toString();

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://openlibrary.org/api/books?bibkeys=ISBN:"
                   + ISBN + "&format=json&jscmd=data";
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
          Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONObject b;
                try {
                    b = response.getJSONObject("ISBN:" + ISBN);
                }
                catch (JSONException e) {
                    b = null;
                }

                if (b == null) {
                    Toast.makeText(getApplicationContext(), "Book not found!",
                        Toast.LENGTH_LONG).show();
                    return;
                }

                Intent intent = new Intent(
                    BookSearchActivity.this, BookSearchResultActivity.class
                );

                String title = b.optString("title");
                intent.putExtra("title", title);

                String publisher;
                try {
                    publisher = b.optJSONArray("publishers")
                                 .optJSONObject(0).optString("name");
                }
                catch (Exception e) {
                    publisher = "";
                }
                intent.putExtra("publisher", publisher);

                String author;
                try {
                    author = b.optJSONArray("authors")
                              .optJSONObject(0).optString("name");
                }
                catch (Exception e) {
                    author = "";
                }
                intent.putExtra("author", author);

                String ISBN10;
                try {
                    ISBN10 = b.optJSONObject("identifiers")
                              .optJSONArray("isbn_10").optString(0);
                }
                catch (Exception e) {
                    ISBN10 = "";
                }
                intent.putExtra("ISBN10", ISBN10);

                String ISBN13;
                try {
                    ISBN13 = b.optJSONObject("identifiers")
                              .optJSONArray("isbn_13").optString(0);
                }
                catch (Exception e) {
                    ISBN13 = "";
                }
                intent.putExtra("ISBN13", ISBN13);

                String imageURL;
                try {
                    imageURL = b.optJSONObject("cover").optString("large");
                }
                catch (Exception e) {
                    imageURL = "";
                }
                intent.putExtra("Image URL", imageURL);

                startActivity(intent);
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(jsObjRequest);
    }
}
