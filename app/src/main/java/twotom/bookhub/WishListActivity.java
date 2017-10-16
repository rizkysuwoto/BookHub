package twotom.bookhub;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class WishListActivity extends BooksListActivity {
    @Override
    protected String getListName() {
        return "wishList";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ListView listView = (ListView) findViewById(R.id.listView_booksList);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view,
              int i, long l) {
                Intent intent = new Intent(
                    WishListActivity.this, BookItemsListActivity.class
                );
                Book book = items.get(i);
                intent.putParcelableArrayListExtra(
                    "bookItems", book.getItems()
                );
                startActivity(intent);
            }
        });
    }
}
