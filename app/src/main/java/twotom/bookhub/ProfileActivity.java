package twotom.bookhub;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ProfileActivity extends AppCompatActivity {
    private boolean isChangingPassword = false;
    private EditText oldPasswordView;
    private EditText newPasswordView;
    private EditText newPasswordConfirmView;
    private Button passwordButton;
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
            findViewById(R.id.editText_profile_email);
        oldPasswordView = (EditText)
            findViewById(R.id.editText_profile_oldPassword);
        newPasswordView = (EditText)
            findViewById(R.id.editText_profile_newPassword);
        newPasswordConfirmView = (EditText)
            findViewById(R.id.editText_profile_newPasswordConfirm);
        passwordButton = (Button)
            findViewById(R.id.button_profile_password);

        UserManager userManager = new UserManager();
        User currentUser = userManager.getCurrentUser();

        usernameView.setText(currentUser.getUsername());
        emailView.setText(currentUser.getEmail());

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

    public void onSubmit(View view) {
        // TODO: Perform network requests here
    }
}
