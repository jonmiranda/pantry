package net.jonmiranda.pantry;

import android.content.Context;
import android.content.Intent;
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

import net.jonmiranda.pantry.storage.PantryItem;
import net.jonmiranda.pantry.storage.PantryItemInstance;
import net.jonmiranda.pantry.storage.Storage;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

  @Bind(R.id.pantry_list_view) RecyclerView pantryListView;
  @Bind(R.id.add_item_view) View addItemView;
  @Bind(R.id.add_item_input) EditText addItemInput;

  private InputMethodManager inputMethodManager;

  private Storage storage;
  private List<PantryItem> pantryItems;
  private PantryListAdapter pantryAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);

    inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    pantryListView.setLayoutManager(new LinearLayoutManager(this));

    storage = new Storage(this);
    pantryItems = storage.getItems();
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
    String itemName = sanitizeItemName(addItemInput.getText().toString());
    if (itemName.isEmpty()) {
      addItemInput.setError(getString(R.string.input_is_empty));
    } else if (storage.itemWithNameExists(itemName)) {
      addItemInput.setError(getString(R.string.item_already_exists));
    } else {
      storage.addInstance(itemName);
      pantryAdapter.notifyDataSetChanged();
      addItemView.setVisibility(View.GONE);
      addItemInput.setText("");
      hideKeyboard();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main, menu);
    if (BuildConfig.DEBUG) {
      getMenuInflater().inflate(R.menu.menu_debug, menu);
    }
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    switch (id) {
      case R.id.action_settings:
        return true;
      case R.id.delete_realm:
        storage.delete();
        startActivity(new Intent(this, MainActivity.class));
        finish();
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
    private final List<PantryItem> items;

    public PantryListAdapter(List<PantryItem> items) {
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
      PantryItem item = items.get(position);
      // TODO: Assumes that every item will always have at least one instance.
      // Which is true for how we have it set up now.
      holder.name.setText(item.getName());

      PantryItemInstance instance = item.getInstances().first();
      Calendar endTime = Calendar.getInstance();
      Date startTime =
          (Date) Utils.firstNonNull(instance.getBought(), instance.getCreated(), endTime);
      holder.bought.setText(Utils.getDisplayableTime(
          endTime.get(Calendar.SECOND) - startTime.getCreated().getSeconds()));
    }

    @Override
    public int getItemCount() {
      return items.size();
    }
  }

  static class PantryItemViewHolder extends RecyclerView.ViewHolder {
    protected TextView name;
    protected TextView bought;

    public PantryItemViewHolder(View view) {
      super(view);
      name = (TextView) view.findViewById(R.id.item_name);
      bought = (TextView) view.findViewById(R.id.item_bought);
    }
  }
}
