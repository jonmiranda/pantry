package net.jonmiranda.pantry;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import net.jonmiranda.pantry.storage.PantryItem;
import net.jonmiranda.pantry.storage.Storage;

import java.util.Calendar;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity
    extends RxAppCompatActivity
    implements DatePickerDialog.OnDateSetListener, PantryItemListener {

  @Bind(R.id.pantry_list_view) RecyclerView pantryListView;

  @Inject Storage storage;

  private PantryAdapter pantryAdapter;

  private PantryItem lastSelectedItem;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);
    ((PantryApplication) getApplication()).inject(this);

    setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

    pantryAdapter = new PantryAdapter(pantryListView, this, storage);
    pantryListView.setLayoutManager(new LinearLayoutManager(this));
    pantryListView.setAdapter(pantryAdapter);
  }

  @Override
  public void onResume() {
    super.onResume();
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
  public boolean onOptionsItemSelected(MenuItem menuItem) {
    int id = menuItem.getItemId();
    switch (id) {
      case R.id.delete_realm:
        storage.delete();
        startActivity(new Intent(this, MainActivity.class));
        finish();
        return true;
      case R.id.default_items:
        String[] defaultItems = new String[] {"Apples", "Bananas", "Oranges", "Carrots", "Bread",
            "Chicken Breast", "Salmon", "Turkey Burgers", "Eggs"};
        for (String item : defaultItems) {
          storage.addItem(item);
        }
        pantryAdapter.update();
        return true;
    }
    return super.onOptionsItemSelected(menuItem);
  }

  @Override
  public void onDateSet(DatePicker unused, int year, int monthOfYear, int dayOfMonth) {
    if (lastSelectedItem != null) {
      Calendar newDate = Calendar.getInstance();
      newDate.set(year, monthOfYear, dayOfMonth);
      storage.updateItem(
          lastSelectedItem,
          lastSelectedItem.getName(),
          lastSelectedItem.isInStock(),
          newDate.getTime());
      pantryAdapter.update();
    }
  }

  @Override
  public void onItemPurchaseClicked(PantryItem item) {
    lastSelectedItem = item;
    DatePickerFragment datePickerFragment =
        DatePickerFragment.newInstance(item.getPurchased().getTime());
    datePickerFragment.setCancelable(true);
    datePickerFragment.show(getSupportFragmentManager(), DatePickerFragment.TAG);
  }
}
