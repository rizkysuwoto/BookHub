package twotom.bookhub;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieHandler;
import java.net.CookieManager;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        (findViewById(R.id.button_home_wishlist)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                // Start WishlistActivity.class
                Intent myIntent = new Intent(HomeActivity.this, WishListActivity.class);
                startActivity(myIntent);
            }
        });

        (findViewById(R.id.button_home_my_books)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                // Start WishlistActivity.class
                Intent myIntent = new Intent(HomeActivity.this, MyBooksActivity.class);
                startActivity(myIntent);
            }
        });

        (findViewById(R.id.button_home_profile)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                // Start WishlistActivity.class
                Intent myIntent = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(myIntent);
            }
        });

        /* //Chat not added yet
        (findViewById(R.id.button_home_chats)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                // Start WishlistActivity.class
                Intent myIntent = new Intent(HomeActivity.this, ChatsActivity.class);
                startActivity(myIntent);
            }
        });
        */

        /* //Logout not added yet
        (findViewById(R.id.button_home_logout)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                // Start WishlistActivity.class
                Intent myIntent = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(myIntent);
            }
        });
        */

        (findViewById(R.id.button_home_find_books)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                // Start WishlistActivity.class
                Intent myIntent = new Intent(HomeActivity.this, BookSearchActivity.class);
                startActivity(myIntent);
            }
        });
    }

    public void onLogout(View view) {

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://bookhub-backend.herokuapp.com/logout";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Intent myIntent = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(myIntent);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error: ", error.getMessage());
            }
        });
        queue.add(stringRequest);



    }
}
