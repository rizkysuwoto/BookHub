package twotom.bookhub;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class TransactionActivity extends AppCompatActivity {
    private String transactionID;
    private ImageView thumbnailView;
    private RatingBar ratingBar;
    private TextView ratingLabelView;
    private LinearLayout ratingLayout;
    private Button approveButton;
    private TextView approvalMessageView;
    private TextView requestMessageView;
    private TextView bookInfoView;
    private RequestQueue requestQueue;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        requestQueue = Volley.newRequestQueue(this);

        Intent intent = getIntent();
        transactionID = intent.getStringExtra("transactionID");
        final String requestMessage = intent.getStringExtra("requestMessage");
        final String approvalMessage = intent.getStringExtra("approvalMessage");

        thumbnailView = (ImageView)
            findViewById(R.id.imageView_transaction_thumbnail);
        bookInfoView = (TextView)
            findViewById(R.id.textView_transaction_bookInfo);
        requestMessageView = (TextView)
            findViewById(R.id.textView_transaction_requestMessage);
        approvalMessageView = (TextView)
            findViewById(R.id.textView_transaction_approvalMessage);
        approveButton = (Button)
            findViewById(R.id.button_transaction_approve);
        ratingLayout = (LinearLayout)
            findViewById(R.id.linearLayout_transaction_rateUser);
        ratingLabelView = (TextView)
            findViewById(R.id.textView_transaction_rateUser);
        setUpRatingBar();

        String url = NetworkConfiguration.getURL() + "transaction/"
                   + transactionID;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
          url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject book = response.getJSONObject("book");
                    String thumbnail = book.getString("thumbnail");
                    String author = book.getString("author");
                    String publisher = book.getString("publisher");
                    String dateRequested = response.getString("dateRequested");
                    String dateApproved = response.getString("dateApproved");
                    String tradedWith = response.getString("tradedWith");
                    int tradedWithRating = response.getInt("tradedWithRating");
                    boolean approved = response.getBoolean("approved");
                    boolean canApprove = response.getBoolean("canApprove");

                    Intent intent = getIntent();
                    thumbnailView.setImageBitmap(
                        Utilities.stringToBitmap(thumbnail)
                    );
                    bookInfoView.setText(intent.getStringExtra("bookTitle")
                        + "\n" + author + "\n" + publisher);
                    requestMessageView.setText(requestMessage + dateRequested);
                    approvalMessageView.setText(approvalMessage + dateApproved);
                    ratingLabelView.setText("Rate " + tradedWith);
                    ratingBar.setRating(tradedWithRating);

                    if (approved) {
                        ratingLayout.setVisibility(View.VISIBLE);
                    }
                    else if (canApprove) {
                        approveButton.setVisibility(View.VISIBLE);
                    }
                }
                catch (JSONException e) {
                    Log.e("Error", e.getMessage());
                }
            }
        }, Utilities.getErrorListener(this));
        requestQueue.add(request);
    }

    public void onApprove(View view) {
        String url = NetworkConfiguration.getURL() + "approveTransaction";

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.putOpt("id", transactionID);
        }
        catch (Exception e) {
            Log.e("Error", e.getMessage());
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
          url, requestBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String message = response.getString("message");
                    Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG)
                         .show();
                    approveButton.setVisibility(View.GONE);
                    ratingLayout.setVisibility(View.VISIBLE);
                }
                catch (JSONException e) {
                    Log.e("Error", e.getMessage());
                }
            }
        }, Utilities.getErrorListener(this));
        requestQueue.add(request);
    }

    private void setUpRatingBar() {
        ratingBar = (RatingBar)
            findViewById(R.id.ratingBar_transaction_rateUser);
        ratingBar.setOnRatingBarChangeListener(new
          RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating,
              boolean fromUser) {
                if (!fromUser) {
                    return;
                }
                JSONObject requestBody = new JSONObject();
                try {
                    requestBody.putOpt("id", transactionID);
                    requestBody.putOpt("rating", rating);
                }
                catch (Exception e) {
                    Log.e("Error", e.getMessage());
                }
                String url = NetworkConfiguration.getURL() + "rateUser";
                JsonObjectRequest request = new JsonObjectRequest(
                  Request.Method.POST, url, requestBody,
                  new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String message = response.getString("message");
                            Toast.makeText(getBaseContext(),
                                message, Toast.LENGTH_LONG).show();
                        }
                        catch (JSONException e) {
                            Log.e("Error", e.getMessage());
                        }
                    }
                }, Utilities.getErrorListener(TransactionActivity.this));
                requestQueue.add(request);
            }
        });
    }
}
