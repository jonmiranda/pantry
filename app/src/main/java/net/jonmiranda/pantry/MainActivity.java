package net.jonmiranda.pantry;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.jakewharton.rxbinding.widget.TextViewAfterTextChangeEvent;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import net.jonmiranda.pantry.storage.PantryItem;
import net.jonmiranda.pantry.storage.Storage;

import java.util.Calendar;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observer;

public class MainActivity
    extends RxAppCompatActivity
    implements DatePickerDialog.OnDateSetListener, PantryItemListener {

  @Bind(R.id.pantry_list_view) RecyclerView pantryListView;
  @Bind(R.id.add_item_submit) View addItemSubmit;
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

    RxTextView.afterTextChangeEvents(addItemInput)
        .compose(this.<TextViewAfterTextChangeEvent>bindToLifecycle())
        .subscribe(
            new Observer<TextViewAfterTextChangeEvent>() {
              @Override
              public void onCompleted() {
              }

              @Override
              public void onError(Throwable e) {
              }

              @Override
              public void onNext(TextViewAfterTextChangeEvent event) {
                String itemName = sanitizeItemName(event.editable().toString());
                boolean enableSubmit = !itemName.isEmpty();
                if (storage.itemWithNameExists(itemName)) {
                  addItemInput.setError(getString(R.string.item_already_exists));
                  enableSubmit = false;
                }
                addItemSubmit.setEnabled(enableSubmit);
              }
            });
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
  public void addNewItem() {
    String itemName = sanitizeItemName(addItemInput.getText().toString());
    storage.addItem(itemName);
    pantryAdapter.notifyDataSetChanged();
    addItemInput.setText("");
    addItemInput.setError(null);
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
