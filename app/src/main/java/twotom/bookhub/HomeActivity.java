package twotom.bookhub;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        (findViewById(R.id.button_home_wishlist)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                // Start WishlistActivity.class
                Intent myIntent = new Intent(HomeActivity.this, WishlistActivity.class);
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
}
