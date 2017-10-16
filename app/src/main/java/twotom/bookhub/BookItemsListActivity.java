package twotom.bookhub;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class BookItemsListActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookitems_list);

        Intent intent = getIntent();
        final ArrayList<BookItem> items =
            intent.getParcelableArrayListExtra("bookItems");
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
            BookItem item = data.get(position);
            usernameView.setText(item.getSeller());
            priceView.setText(Double.toString(item.getPrice()));
            conditionView.setText(item.getCondition());
            return view;
        }
    }
}
