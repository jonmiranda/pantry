package net.jonmiranda.pantry;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import net.jonmiranda.pantry.storage.PantryItem;
import net.jonmiranda.pantry.storage.Storage;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, manifest = "app/src/main/AndroidManifest.xml", sdk = 19)
public class MainActivityRobolectricTest {
  private MainActivity activity;
  private Storage storage;

  @Before
  public void setUp() {
    activity = Robolectric.setupActivity(MainActivity.class);
    storage = activity.storage;
  }

  @Test
  public void testLayout() {
    View fab = activity.findViewById(R.id.fab);
    View listView = activity.findViewById(R.id.pantry_list_view);
    View addItemView = activity.findViewById(R.id.add_item_view);

    assertEquals(fab.getVisibility(), View.VISIBLE);
    assertEquals(listView.getVisibility(), View.VISIBLE);
    assertEquals(addItemView.getVisibility(), View.GONE);
  }

  @Test
  public void testClickingFabTogglesAddItemViewVisibility() {
    View fab = activity.findViewById(R.id.fab);
    View addItemView = activity.findViewById(R.id.add_item_view);

    fab.performClick();

    assertEquals(addItemView.getVisibility(), View.VISIBLE);

    fab.performClick();

    assertEquals(addItemView.getVisibility(), View.GONE);
  }

  @Test
  public void testErrorWhenAddingItemThatAlreadyExists() {
    TextView itemInput = (TextView) activity.findViewById(R.id.add_item_input);
    View submitItemButton = activity.findViewById(R.id.add_item_submit);

    addItem("Apples", itemInput, submitItemButton);
    int itemsSizeBefore = storage.getItems().size();
    addItem("Apples", itemInput, submitItemButton);

    assertEquals(itemInput.getError(), "Item already exists in Pantry.");
    assertEquals(itemsSizeBefore, storage.getItems().size());
  }

  @Test
  public void testErrorWhenAddingItemWithNoText() {
    View submitItemButton = activity.findViewById(R.id.add_item_submit);
    TextView itemInput = (TextView) activity.findViewById(R.id.add_item_input);
    int itemsSizeBefore = storage.getItems().size();

    submitItemButton.performClick();

    assertEquals(itemInput.getError(), "No text was entered.");
    assertEquals(itemsSizeBefore, storage.getItems().size());
  }

  @Test
  public void testAddingItemUpdatesStorage() {
    TextView itemInput = (TextView) activity.findViewById(R.id.add_item_input);
    View submitItemButton = activity.findViewById(R.id.add_item_submit);

    List<PantryItem> items = storage.getItems();

    assertTrue(items.isEmpty());

    addItem("Apples", itemInput, submitItemButton);

    assertTrue(itemInput.getText().toString().isEmpty());
    assertTrue(items.size() == 1);
  }

  @Test
  public void testAddingItemUpdatesUI() {
    RecyclerView itemList = (RecyclerView) activity.findViewById(R.id.pantry_list_view);

    addItem(
        "Apples",
        (TextView) activity.findViewById(R.id.add_item_input),
        activity.findViewById(R.id.add_item_submit));

    View itemView = getItemFromList(itemList, 0);
    CheckBox item = (CheckBox) itemView.findViewById(R.id.item_name);
    TextView purchased = (TextView) itemView.findViewById(R.id.item_purchased);

    assertTrue(item.getText().equals("Apples"));
    assertTrue(item.isChecked());
    assertTrue(purchased.getText().equals("Today"));
  }

  @Test
  public void testUncheckingItemMarksItAsOutOfStock() {
    RecyclerView itemList = (RecyclerView) activity.findViewById(R.id.pantry_list_view);

    addItem(
        "Apples",
        (TextView) activity.findViewById(R.id.add_item_input),
        activity.findViewById(R.id.add_item_submit));

    View itemView = getItemFromList(itemList, 0);
    CheckBox item = (CheckBox) itemView.findViewById(R.id.item_name);
    item.performClick();

    assertFalse(item.isChecked());
    assertFalse(storage.getItems().get(0).isInStock());
  }

  @Test
  public void testClickingPurchasedOpensDialog() {
    // TODO: Blocked by https://github.com/robolectric/robolectric/issues/783
  }

  private static View getItemFromList(RecyclerView list, int position) {
    list.measure(0, 0);
    list.layout(0, 0, 100, 1000);
    return list.getChildAt(position);
  }

  private static void addItem(String name, TextView input, View submit) {
    input.setText(name);
    submit.performClick();
  }
}
