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

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

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

        Intent intent = getIntent();

        String title     = intent.getStringExtra("title");
        String author    = intent.getStringExtra("author");
        String publisher = intent.getStringExtra("publisher");
        String ISBN10    = intent.getStringExtra("ISBN10");
        String ISBN13    = intent.getStringExtra("ISBN13");
        String imageURL  = intent.getStringExtra("Image URL");

        book.setTitle(title);
        book.setAuthor(author);
        book.setPublisher(publisher);
        book.setISBN10(ISBN10);
        book.setISBN13(ISBN13);

        new DownloadImageTask((ImageView) findViewById(R.id.image_bookSearchResult_cover))
                .execute(imageURL);
        ((TextView) findViewById(R.id.text_bookSearchResult_author)).setText(author);
        ((TextView) findViewById(R.id.text_bookSearchResult_title)).setText(title);
        ((TextView) findViewById(R.id.text_bookSearchResult_publisher)).setText(publisher);

        (findViewById(R.id.button_bookSearchResult_sell)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent myIntent = new Intent(BookSearchResultActivity.this, MyBooksActivity.class);
                myIntent.putExtra("BOOK", book);
                startActivity(myIntent);
            }
        });
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

    public void onAddToWishlist(View view) {
        String username = new UserManager().getCurrentUser().getUsername();

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = NetworkConfiguration.getURL()
                   + "wishList/" + username;

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.putOpt("isbn10", book.getISBN10());
        }
        catch (JSONException e) {
            Log.e("Error", e.getMessage());
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
          url, requestBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String message = response.getString("message");
                    Toast.makeText(getBaseContext(), message,
                        Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(
                        BookSearchResultActivity.this, WishListActivity.class
                    );
                    finish();
                }
                catch (JSONException e) {
                    Log.e("Error", e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                NetworkResponse response = e.networkResponse;
                if (e instanceof ServerError && response != null) {
                    try {
                        String message = new String(
                            response.data,
                            HttpHeaderParser.parseCharset(
                                response.headers, "utf-8"
                            )
                        );
                        Toast.makeText(getBaseContext(), message,
                            Toast.LENGTH_LONG).show();
                    }
                    catch (Exception e1) {
                        Log.e("Error", e1.getMessage());
                    }
                }
            }
        });
        queue.add(request);
    }
}
