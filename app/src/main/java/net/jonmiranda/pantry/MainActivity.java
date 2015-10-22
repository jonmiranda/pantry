package net.jonmiranda.pantry;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final String[] ITEMS = new String[] {"Apples", "Bananas", "Carrots"};

    @Bind(R.id.pantry_list_view) RecyclerView pantryListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        pantryListView.setLayoutManager(new LinearLayoutManager(this));
        pantryListView.setAdapter(new PantryListAdapter(ITEMS));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    static class PantryListAdapter extends RecyclerView.Adapter<PantryItemViewHolder> {
        private final String[] items;

        public PantryListAdapter(String[] items) {
            this.items = items;
        }

        @Override
        public PantryItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
              .inflate(R.layout.pantry_list_view_item, parent, false);
            return new PantryItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(PantryItemViewHolder holder, int position) {
            holder.itemName.setText(items[position]);
        }

        @Override
        public int getItemCount() {
            return items.length;
        }
    }

    static class PantryItemViewHolder extends RecyclerView.ViewHolder {
        protected TextView itemName;

        public PantryItemViewHolder(View view) {
            super(view);
            itemName = (TextView) view.findViewById(R.id.item_name);
        }
    }
}
