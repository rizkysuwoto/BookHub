package twotom.bookhub;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class BookItemsListActivity extends AppCompatActivity {
    ArrayList<BookItem> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookitems_list);

        Intent intent = getIntent();
        items = intent.getParcelableArrayListExtra("bookItems");
        BookItemsListAdapter adapter = new
            BookItemsListAdapter(this, items);

        ListView listView = (ListView)
            findViewById(R.id.listView_bookItemsList);
        listView.setAdapter(adapter);
        listView.setClickable(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view,
              int i, long l) {
                Intent intent = new Intent(
                    BookItemsListActivity.this, ViewProfileActivity.class
                );
                intent.putExtra("seller", items.get(i).getSeller());
                startActivity(intent);
            }
        });
    }

    private class BookItemsListAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<BookItem> data;
        private LayoutInflater inflater;

        BookItemsListAdapter(Context context, ArrayList<BookItem> data) {
            this.context = context;
            this.data = data;
            inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = inflater.inflate(R.layout.bookitems_list_item, null);
            }
            TextView usernameView = (TextView)
                view.findViewById(R.id.textView_bookItemsListItem_username);
            TextView priceView = (TextView)
                view.findViewById(R.id.textView_bookItemsListItem_price);
            TextView conditionView = (TextView)
                view.findViewById(R.id.textView_bookItemsListItem_condition);
            Button requestTransactionButton = (Button)
                view.findViewById(
                    R.id.button_bookItemsListItem_requestTransaction);

            BookItem item = data.get(position);

            if (item.getCanRequestTransaction()) {
                requestTransactionButton.setTag(position);
                requestTransactionButton.setOnClickListener(
                  new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        BookItemsListActivity.this.requestTransaction(
                            (Integer) view.getTag(), (Button) view
                        );
                    }
                });
                requestTransactionButton.setFocusable(false);
                requestTransactionButton.setFocusableInTouchMode(false);
            }
            else {
                requestTransactionButton.setVisibility(View.GONE);
            }

            usernameView.setText(item.getSeller());
            priceView.setText(Double.toString(item.getPrice()));
            conditionView.setText(item.getCondition());
            return view;
        }
    }

    private void requestTransaction(int position, final Button button) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = NetworkConfiguration.getURL() + "requestTransaction";

        final BookItem item = items.get(position);
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.putOpt("seller", item.getSeller());
            requestBody.putOpt("isbn10", getIntent().getStringExtra("isbn10"));
        }
        catch (JSONException e) {
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
                    item.setCanRequestTransaction(false);
                    button.setVisibility(View.GONE);
                }
                catch (JSONException e) {
                    Log.e("Error", e.getMessage());
                }
            }
        }, Utilities.getErrorListener(this));
        queue.add(request);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra("bookItems", items);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }
}
