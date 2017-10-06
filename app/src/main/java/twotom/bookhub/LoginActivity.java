package twotom.bookhub;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

import java.net.CookieHandler;
import java.net.CookieManager;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void onLogin(View view) {
        //TODO: Check Database for proper username/password pair
        CookieHandler.setDefault(new CookieManager());

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.putOpt("username", ((EditText) findViewById(R.id.text_login_username)).getText().toString());
            requestBody.putOpt("password", ((EditText) findViewById(R.id.text_login_password)).getText().toString());
        } catch (JSONException e) {
            //TODO: handle error
        }
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = NetworkConfiguration.getURL() + "login";
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url, requestBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject resp) {
                String username;
                String email;
                String picture;
                try {
                    username = resp.getString("username");
                    email = resp.getString("email");
                    picture = resp.getString("picture");
                    UserManager um = new UserManager();
                    User myUser = new User();
                    myUser.setUsername(username);
                    myUser.setEmail(email);
                    myUser.setPicture(picture);
                    um.setCurrentUser(myUser);
                    Intent myIntent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(myIntent);
                } catch (JSONException e) {
                    Log.e("Error", e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error: ", error.getMessage());
            }
        });
        queue.add(jsObjRequest);



    }

    public void onNewUser(View view) {
        //TODO: Switch to Registration Screen

        Intent myIntent = new Intent(LoginActivity.this, RegistrationActivity.class);
        startActivity(myIntent);
    }
}
