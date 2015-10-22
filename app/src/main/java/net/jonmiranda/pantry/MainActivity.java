package net.jonmiranda.pantry;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

  private static final String[] ITEMS = new String[] {"Apples", "Bananas", "Carrots"};

  @Bind(R.id.pantry_list_view) RecyclerView pantryListView;
  @Bind(R.id.add_item_view) View addItemView;
  @Bind(R.id.add_item_input) EditText addItemInput;

  private InputMethodManager inputMethodManager;

  private List<String> pantryItems;
  private PantryListAdapter pantryAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);

    inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

    pantryListView.setLayoutManager(new LinearLayoutManager(this));
    pantryItems = new ArrayList<>(Arrays.asList(ITEMS));
    pantryAdapter = new PantryListAdapter(pantryItems);
    pantryListView.setAdapter(pantryAdapter);
  }

  @OnClick(R.id.fab)
  public void toggleAddItemViewVisibility() {
    int visibility = addItemView.getVisibility() == View.VISIBLE
        ? View.GONE
        : View.VISIBLE;

    addItemView.setVisibility(visibility);

    if (visibility == View.VISIBLE) {
      addItemInput.requestFocus();
      inputMethodManager.showSoftInput(addItemInput, InputMethodManager.SHOW_IMPLICIT);
    } else {
      hideKeyboard();
    }
  }

  @OnClick(R.id.add_item_submit)
  public void tryAddingNewItem() {
    String item = sanitizeItemName(addItemInput.getText().toString());
    if (item.isEmpty()) {
      addItemInput.setError(getString(R.string.input_is_empty));
    } else if (pantryItems.contains(item)) {
      addItemInput.setError(getString(R.string.item_already_exists));
    } else {
      pantryItems.add(item);
      pantryAdapter.notifyDataSetChanged();
      addItemView.setVisibility(View.GONE);
      addItemInput.setText("");
      hideKeyboard();
    }
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

  private String sanitizeItemName(String itemName) {
    return itemName.trim();
  }

  private void hideKeyboard() {
    View view = getCurrentFocus();
    if (view != null) {
      inputMethodManager.
          hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
  }

  static class PantryListAdapter extends RecyclerView.Adapter<PantryItemViewHolder> {
    private final List<String> items;

    public PantryListAdapter(List<String> items) {
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
      holder.itemName.setText(items.get(position));
    }

    @Override
    public int getItemCount() {
      return items.size();
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
