package twotom.bookhub;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    @Override
    /* Demo Version
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final DatabaseReference db = FirebaseDatabase.getInstance().getReference();

        (findViewById(R.id.button_login)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                String username = ((EditText) findViewById(R.id.text_login_username)).getText().toString();

                db.child("users").child(username).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        UserManager um = new UserManager();
                        um.setCurrentUser(dataSnapshot.getValue(User.class));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                Intent myIntent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(myIntent);
            }
        });
    }
    */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void onLogin(View view) {
        //TODO: Check Database for proper username/password pair

    }

    public void onNewUser(View view) {
        //TODO: Switch to Registration Screen

        Intent myIntent = new Intent(LoginActivity.this, RegistrationActivity.class);
        startActivity(myIntent);
    }
}
