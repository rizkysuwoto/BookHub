package twotom.bookhub;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ProfileActivity extends AppCompatActivity {
    private static final int RESULT_LOAD_IMAGE = 1;
    private boolean isChangingPassword = false;
    private ImageView mUserPic;
    private EditText oldPasswordView;
    private EditText newPasswordView;
    private EditText newPasswordConfirmView;
    private Button passwordButton;
    private User mCurrentUser;
    private String oldPassword = "";
    private String newPassword = "";
    private String newPasswordConfirm = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        TextView usernameView = (TextView)
            findViewById(R.id.textView_profile_username);
        TextView emailView = (TextView)
            findViewById(R.id.textView_profile_email);
        mUserPic = (ImageView) findViewById(R.id.profile_userPic);
        oldPasswordView = (EditText)
            findViewById(R.id.editText_profile_oldPassword);
        newPasswordView = (EditText)
            findViewById(R.id.editText_profile_newPassword);
        newPasswordConfirmView = (EditText)
            findViewById(R.id.editText_profile_newPasswordConfirm);
        passwordButton = (Button)
            findViewById(R.id.button_profile_password);

        mCurrentUser = new UserManager().getCurrentUser();
        if (!mCurrentUser.getPicture().isEmpty()) {
            mUserPic.setImageBitmap(StringToBitMap(mCurrentUser.getPicture()));
        }
        oldPasswordView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence,
              int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence,
              int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                oldPassword = oldPasswordView.getText().toString();
            }
        });
        newPasswordView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence,
              int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence,
              int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                newPassword = newPasswordView.getText().toString();
            }
        });

        newPasswordConfirmView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence,
                                          int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence,
                                      int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                newPasswordConfirm = newPasswordConfirmView.getText().toString();
            }
        });
    }

    public void onChangePassword(View view) {
        isChangingPassword = !isChangingPassword;
        if (isChangingPassword) {
            passwordButton.setText("Cancel");
            oldPasswordView.setVisibility(View.VISIBLE);
            newPasswordView.setVisibility(View.VISIBLE);
            newPasswordView.setVisibility(View.VISIBLE);
        }
        else {
            passwordButton.setText("Change Password");
            oldPasswordView.setVisibility(View.GONE);
            newPasswordView.setVisibility(View.GONE);
            newPasswordConfirmView.setVisibility(View.GONE);
            oldPassword = "";
            newPassword = "";
            newPasswordConfirm = "";
        }
    }

    public void onChangePicture(View view) {
        Intent i = new Intent(
                Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            Bitmap bitmap = null;
            try {
                //TODO: handle error
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
            } catch (FileNotFoundException e) {
                //TODO: handle error
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String picture = BitMapToString(bitmap);
            Log.d("Picture: ", picture);
            mCurrentUser.setPicture(picture);
            mUserPic.setImageBitmap(bitmap);
        }
    }

    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    public Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte=Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }

    public void onSubmit(View view) {
        // TODO: Perform network requests here
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.putOpt("email", mCurrentUser.getEmail());
            requestBody.putOpt("username", mCurrentUser.getUsername());
            requestBody.putOpt("password", newPassword);
            requestBody.putOpt("picture", mCurrentUser.getPicture());
        } catch (JSONException e) {
            //TODO: handle error
        }
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = NetworkConfiguration.getURL() + "user";
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url, requestBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject resp) {
                String username;
                try {
                    username = resp.getString("username");
                    Toast.makeText(getBaseContext(), username + "'s password changed to " + newPassword,
                            Toast.LENGTH_LONG).show();
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
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
}
