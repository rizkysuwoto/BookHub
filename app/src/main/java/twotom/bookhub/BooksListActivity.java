package twotom.bookhub;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

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

// Super class for WishListActivity and MyBooksActivity
public abstract class BooksListActivity extends AppCompatActivity {
    protected ArrayList<Book> items;
    private ArrayList<Integer> indexesToRemove;
    private ArrayList<CheckBox> checkBoxes;
    private boolean isEditing;
    private Button editButton;

    abstract protected String getListName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books_list);

        items = new ArrayList<>();
        indexesToRemove = new ArrayList<>();
        checkBoxes = new ArrayList<>();
        isEditing = false;
        editButton = (Button) findViewById(R.id.button_booksList_edit);
        ListView listView = (ListView) findViewById(R.id.listView_booksList);

        final BooksListAdapter adapter = new BooksListAdapter(this, items);
        listView.setAdapter(adapter);

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = NetworkConfiguration.getURL() + getListName();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
          url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray array = response.getJSONArray("books");
                    for (int i = 0; i < array.length(); ++i) {
                        JSONObject object = array.getJSONObject(i);
                        Book book = Utilities.jsonObjectToBook(object);
                        items.add(book);
                        adapter.notifyDataSetChanged();
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

    public void onEdit(View view) {
        isEditing = !isEditing;

        // Start editing
        if (isEditing) {
            editButton.setText("Cancel");
            for (CheckBox checkBox : checkBoxes) {
                checkBox.setVisibility(View.VISIBLE);
            }
        }
        // No changes to list
        else if (indexesToRemove.isEmpty()) {
            cancelEditing();
        }
        // End editing
        else {
            RequestQueue queue = Volley.newRequestQueue(this);
            String url = NetworkConfiguration.getURL()
                       + getListName() + "/remove";

            JSONArray isbnsToRemove = new JSONArray();
            for (Integer indexToRemove : indexesToRemove) {
                isbnsToRemove.put(items.get(indexToRemove).getISBN10());
            }
            JSONObject requestBody = new JSONObject();
            try {
                requestBody.putOpt("isbnsToRemove", isbnsToRemove);
            }
            catch (JSONException e) {
                Log.e("Error", e.getMessage());
            }

            JsonObjectRequest request = new JsonObjectRequest(
              Request.Method.POST, url, requestBody,
              new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String message = response.getString("message");
                        Toast.makeText(getBaseContext(), message,
                            Toast.LENGTH_LONG).show();
                        recreate();
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

    private void cancelEditing() {
        indexesToRemove.clear();
        editButton.setText("Edit");
        for (CheckBox checkBox : checkBoxes) {
            checkBox.setVisibility(View.INVISIBLE);
        }
    }

    private class BooksListAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<Book> data;
        private LayoutInflater inflater = null;

        BooksListAdapter(Context context, ArrayList<Book> data)
        {
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
                view = inflater.inflate(R.layout.books_list_item, null);
            }
            TextView titleTextView = (TextView)
                view.findViewById(R.id.textView_booksListItem_title);
            TextView authorTextView = (TextView)
                view.findViewById(R.id.textView_booksListItem_author);
            Book book = data.get(position);
            titleTextView.setText(book.getTitle());
            authorTextView.setText(book.getAuthor());

            CheckBox checkBox = (CheckBox)
                view.findViewById(R.id.checkBox_booksListItem);
            checkBoxes.add(checkBox);

            checkBox.setTag(position);
            checkBox.setOnCheckedChangeListener(
              new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton,
                  boolean isChecked) {
                    Integer tag = (Integer) compoundButton.getTag();
                    if (isChecked) {
                        indexesToRemove.add(tag);
                    }
                    else {
                        indexesToRemove.remove(tag);
                    }
                    setEditButtonText();
                }
            });

            return view;
        }
    }

    private void setEditButtonText() {
        if (indexesToRemove.isEmpty()) {
            editButton.setText("Cancel");
        }
        else {
            editButton.setText("Delete selected");
        }
    }
}
