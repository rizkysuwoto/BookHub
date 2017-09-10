package twotom.bookhub;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

import java.util.regex.Pattern;

public class RegistrationActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
    }

    public void onSubmit(View view) {
        UserDatabase userDatabase = new UserDatabase(this);
        String username =
            ((EditText) findViewById(R.id.text_registration_username)).getText().toString();
        if (!isUsernameValid(username, userDatabase)) {
            return;
        }
        String email =
            ((EditText) findViewById(R.id.text_registration_email)).getText().toString();
        if (!isEmailValid(email)) {
            return;
        }
        String password =
            ((EditText) findViewById(R.id.text_registration_password)).getText().toString();
        if (!isPasswordValid(password)) {
            return;
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        userDatabase.insertUser(user);

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.putOpt("email", email);
            requestBody.putOpt("username", username);
            requestBody.putOpt("password", password);
        } catch (JSONException e) {
            //TODO: handle error
        }
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://bookhub-backend.herokuapp.com/user";
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.PUT, url, requestBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject resp) {
                String username;
                try {
                    username = resp.getString("username");

                    Toast.makeText(getBaseContext(), "Account " + username + " created",
                            Toast.LENGTH_LONG).show();
                    finish();
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

    private boolean isUsernameValid(String username, UserDatabase database) {
        if (username.isEmpty()) {
            Toast.makeText(getBaseContext(), "Username cannot be empty",
                Toast.LENGTH_LONG).show();
            return false;
        }
        if (database.userExists(username)) {
            Toast.makeText(
                getBaseContext(),
                "Username " + username + " already exists",
                Toast.LENGTH_LONG
            ).show();
            return false;
        }
        return true;
    }

    private boolean isPasswordValid(String password) {
        if (password.isEmpty()) {
            Toast.makeText(getBaseContext(), "Password must not be empty",
                Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private boolean isEmailValid(String email) {
        final String pattern = "\\w+@\\w+\\.\\w+";
        if (!Pattern.matches(pattern, email)) {
            Toast.makeText(getBaseContext(), "Invalid email",
                Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
}
