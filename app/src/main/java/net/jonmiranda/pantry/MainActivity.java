package net.jonmiranda.pantry;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;

import net.jonmiranda.pantry.storage.PantryItem;
import net.jonmiranda.pantry.storage.Storage;

import java.util.Calendar;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity
    extends AppCompatActivity
    implements DatePickerDialog.OnDateSetListener, PantryItemListener {

  @Bind(R.id.pantry_list_view) RecyclerView pantryListView;
  @Bind(R.id.add_item_view) View addItemView;
  @Bind(R.id.add_item_input) EditText addItemInput;

  @Inject Storage storage;

  private InputMethodManager inputMethodManager;

  private PantryAdapter pantryAdapter;

  private String lastSelectedItemName;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);
    ((PantryApplication) getApplication()).inject(this);

    setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

    inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    pantryListView.setLayoutManager(new LinearLayoutManager(this));

    pantryAdapter = new PantryAdapter(this, storage);
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
      addItemInput.setError(null);
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
      storage.addItem(itemName);
      pantryAdapter.notifyDataSetChanged();
      addItemView.setVisibility(View.GONE);
      addItemInput.setText("");
      addItemInput.setError(null);
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

  @Override
  public void onDateSet(DatePicker unused, int year, int monthOfYear, int dayOfMonth) {
    if (lastSelectedItemName != null) {
      Calendar newDate = Calendar.getInstance();
      newDate.set(year, monthOfYear, dayOfMonth);
      storage.setItemPurchased(lastSelectedItemName, newDate.getTime());
      pantryAdapter.notifyDataSetChanged();
    }
  }

  @Override
  public void onItemPurchaseClicked(String itemName) {
    lastSelectedItemName = itemName;
    PantryItem item = storage.getItemWithName(itemName);
    DatePickerFragment datePickerFragment =
        DatePickerFragment.newInstance(item.getPurchased().getTime());
    datePickerFragment.setCancelable(true);
    datePickerFragment.show(getSupportFragmentManager(), DatePickerFragment.TAG);
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
}
