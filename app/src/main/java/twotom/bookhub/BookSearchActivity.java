package twotom.bookhub;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
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
        final String isbn =
            ((EditText) findViewById(R.id.textfield_bookSearch_isbn))
            .getText().toString();

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = NetworkConfiguration.getURL() + "search/" + isbn;
        JsonObjectRequest request = new JsonObjectRequest(
          Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String thumbnail = response.getString("thumbnail");
                    Intent intent = new Intent(
                        BookSearchActivity.this, BookSearchResultActivity.class
                    );
                    Book book = Utilities.jsonObjectToBook(response);
                    intent.putExtra("book", book);
                    intent.putExtra("thumbnail", thumbnail);
                    startActivity(intent);
                    finish();
                }
                catch (JSONException e) {
                    Log.e("Error", e.getMessage());
                }
            }
        }, Utilities.getErrorListener(this));
        request.setRetryPolicy(new DefaultRetryPolicy(
            5000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        queue.add(request);
    }
}
