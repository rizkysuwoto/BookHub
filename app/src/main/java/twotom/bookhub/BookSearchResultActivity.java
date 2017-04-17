package twotom.bookhub;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;

public class BookSearchResultActivity extends AppCompatActivity {

    private final Book book = new Book();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_search_result);
        RequestQueue queue = Volley.newRequestQueue(this);
        final String ISBN = getIntent().getExtras().getString("ISBN");

        String url = "https://openlibrary.org/api/books?bibkeys=ISBN:"+ISBN+"&format=json&jscmd=data";
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject resp) {
                JSONObject b;
                try {
                    b = resp.getJSONObject("ISBN:"+ISBN);
                } catch (JSONException e) {
                    b = null;
                }
                if (b == null) {
                    Toast.makeText(getApplicationContext(), "Book not found!",
                            Toast.LENGTH_LONG).show();
                } else {
                    String title = b.optString("title");
                    String publisher = b.optJSONArray("publishers").optJSONObject(0).optString("name");
                    String author = b.optJSONArray("authors").optJSONObject(0).optString("name");
                    String ISBN10 = b.optJSONObject("identifiers").optJSONArray("isbn_10").optString(0);
                    String ISBN13 = b.optJSONObject("identifiers").optJSONArray("isbn_13").optString(0);
                    String imgURL = b.optJSONObject("cover").optString("large");

                    book.setAuthor(author);
                    book.setTitle(title);
                    book.setPublisher(publisher);
                    book.setISBN10(ISBN10);
                    book.setISBN13(ISBN13);

                    new DownloadImageTask((ImageView) findViewById(R.id.image_bookSearchResult_cover))
                            .execute(imgURL);
                    ((TextView) findViewById(R.id.text_bookSearchResult_author)).setText(author);
                    ((TextView) findViewById(R.id.text_bookSearchResult_title)).setText(title);
                    ((TextView) findViewById(R.id.text_bookSearchResult_publisher)).setText(publisher);

                    (findViewById(R.id.button_bookSearchResult_wishlist)).setOnClickListener(new View.OnClickListener() {
                        public void onClick(View arg0) {
                            Intent myIntent = new Intent(BookSearchResultActivity.this, WishlistActivity.class);
                            myIntent.putExtra("BOOK", book);
                            startActivity(myIntent);
                        }
                    });

                    (findViewById(R.id.button_bookSearchResult_sell)).setOnClickListener(new View.OnClickListener() {
                        public void onClick(View arg0) {
                            Intent myIntent = new Intent(BookSearchResultActivity.this, MyBooksActivity.class);
                            myIntent.putExtra("BOOK", book);
                            startActivity(myIntent);
                        }
                    });
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        //this actually queues up the async response with Volley
        queue.add(jsObjRequest);

    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}


