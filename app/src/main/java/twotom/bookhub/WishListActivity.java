package twotom.bookhub;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WishListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        String username = new UserManager().getCurrentUser().getUsername();

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = NetworkConfiguration.getURL()
                   + "wishList/" + username;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
          url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    // TODO: Actually add these to the list view
                    JSONArray array = response.getJSONArray("array");
                    for (int i = 0; i < array.length(); ++i) {
                        JSONObject object = array.getJSONObject(i);
                        String title = object.getString("title");
                        String author = object.getString("author");
                        Log.d("Response", "Title: " + title + ", Author: " + author);
                    }
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
