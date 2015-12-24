package net.jonmiranda.pantry;

import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import net.jonmiranda.pantry.dagger.TestModule;
import net.jonmiranda.pantry.storage.PantryItem;
import net.jonmiranda.pantry.storage.Storage;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowAlertDialog;
import org.robolectric.shadows.ShadowView;

import java.util.ArrayList;
import java.util.List;

import dagger.ObjectGraph;
import rx.functions.Action1;

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
    PantryApplication application = (PantryApplication) RuntimeEnvironment.application;
    application.objectGraph = ObjectGraph.create(new TestModule(application));
    activity = Robolectric.setupActivity(MainActivity.class);
    storage = activity.storage;
  }

  @Test
  public void testLayout() {
    RecyclerView listView = (RecyclerView) activity.findViewById(R.id.pantry_list_view);
    View addItemView = getItemFromList(listView, 0);
    View addItemSubmit = activity.findViewById(R.id.add_item_submit);

    assertEquals(listView.getVisibility(), View.VISIBLE);
    assertEquals(addItemView.getVisibility(), View.VISIBLE);
    assertFalse(addItemSubmit.isEnabled());
  }

  @Test
  public void testErrorWhenTypingItemThatAlreadyExists() {
    TextView itemInput = getItemInput();
    View submitItemButton = activity.findViewById(R.id.add_item_submit);

    addItem("Apples");
    setItemInputAndWait("Apples");

    assertEquals(itemInput.getError(), "Item already exists in Pantry.");
    assertFalse(submitItemButton.isEnabled());
  }

  @Test
  public void testSubmitEnabledDisabledCases() throws Exception {
    View submitItemButton = getItemSubmit();

    setItemInputAndWait("Apples");
    assertTrue(submitItemButton.isEnabled());
    submitItemButton.performClick();

    setItemInputAndWait("");
    assertFalse(submitItemButton.isEnabled());

    setItemInputAndWait("Apples");
    assertFalse(submitItemButton.isEnabled());
  }

  @Test
  public void testAddingItemUpdatesStorage() {
    List<PantryItem> items = storage.getItems();
    assertTrue(items.isEmpty());

    addItem("Apples");

    assertTrue(getItemInput().getText().toString().isEmpty());
    assertTrue(items.size() == 1);
  }

  @Test
  public void testAddingItemUpdatesUI() {
    addItem("Apples");

    RecyclerView itemList = (RecyclerView) activity.findViewById(R.id.pantry_list_view);
    View itemView = getItemFromList(itemList, 0);
    CheckBox checkBox = (CheckBox) itemView.findViewById(R.id.item_checkbox);
    EditText item = (EditText) itemView.findViewById(R.id.item_name);
    TextView purchased = (TextView) itemView.findViewById(R.id.item_purchased);

    assertTrue(getItemInput().getText().equals(""));
    assertTrue(getItemInput().getError() == null);
    assertTrue(item.getText().equals("Apples"));
    assertTrue(checkBox.isChecked());
    assertTrue(purchased.getText().equals("Today"));
  }

  @Test
  public void testUncheckingItemMarksItAsOutOfStock() {
    addItem("Apples");

    RecyclerView itemList = (RecyclerView) activity.findViewById(R.id.pantry_list_view);
    View itemView = getItemFromList(itemList, 0);
    CheckBox checkBox = (CheckBox) itemView.findViewById(R.id.item_checkbox);
    checkBox.performClick();

    assertFalse(checkBox.isChecked());
    assertFalse(storage.getItems().get(0).isInStock());
  }

  @Test
  public void testDeletingItem() {
    addItem("Apples");

    RecyclerView itemList = (RecyclerView) activity.findViewById(R.id.pantry_list_view);
    assertEquals(2, itemList.getAdapter().getItemCount());

    View itemView = getItemFromList(itemList, 0);
    View itemName = itemView.findViewById(R.id.item_name);
    View deleteView = itemView.findViewById(R.id.item_delete);
    itemName.requestFocus(); // focus the view so the more button appears
    deleteView.performClick(); // ShadowView.clickOn(more); not working :(

    List<Dialog> dialogs = ShadowAlertDialog.getShownDialogs();
    assertEquals(1, dialogs.size());

    AlertDialog dialog = (AlertDialog) dialogs.get(0);
    ShadowView.clickOn(dialog.getButton(DialogInterface.BUTTON_POSITIVE));

    assertEquals(1, itemList.getAdapter().getItemCount());

    //    TODO: Figure out how to make the below tests pass.
    //    assertEquals("Confirm Delete", confirmationDialog.getTitle());
    //    assertEquals("Are you sure you want to delete 'Apples'?", confirmationDialog.getMessage());
  }

  @Test
  public void testClickingPurchasedOpensDialog() {
    // TODO: Blocked by https://github.com/robolectric/robolectric/issues/783
  }

  @Test
  public void testAddItemInputObservable() throws Exception {
    final List<String> items = new ArrayList<>();

    RecyclerView itemList = (RecyclerView) activity.findViewById(R.id.pantry_list_view);
    View addItemView = getItemFromList(itemList, 0);
    ((PantryAdapter.PantryAddItemViewHolder) addItemView.getTag())
        .getAddItemInputObservable(activity)
        .subscribe(new Action1<String>() {
          @Override
          public void call(String item) {
            items.add(item);
          }
        });

    TextView itemInput = getItemInput();
    itemInput.setText("A");
    itemInput.setText("Ap");
    setItemInputAndWait("App"); // Stimulates the Observable
    itemInput.setText("Appl");
    itemInput.setText("Apple");
    setItemInputAndWait("Apples"); // Stimulates the Observable

    assertEquals(2, items.size());
    assertEquals("App", items.get(0));
    assertEquals("Apples", items.get(1));
  }

  @Test
  public void testEditingItemName() {
    addItem("Apples");

    RecyclerView itemList = (RecyclerView) activity.findViewById(R.id.pantry_list_view);
    View itemView = getItemFromList(itemList, 0);
    TextView itemName = (TextView) itemView.findViewById(R.id.item_name);

    assertEquals(null, storage.getItemWithName("Bananas"));

    itemName.setText("Bananas");

    assertTrue(storage.getItemWithName("Bananas") != null);
  }

  private TextView getItemInput() {
    RecyclerView itemList = (RecyclerView) activity.findViewById(R.id.pantry_list_view);
    View itemView = getItemFromList(itemList, itemList.getAdapter().getItemCount() - 1);
    return (TextView) itemView.findViewById(R.id.add_item_input);
  }

  private View getItemSubmit() {
    RecyclerView itemList = (RecyclerView) activity.findViewById(R.id.pantry_list_view);
    View itemView = getItemFromList(itemList, itemList.getAdapter().getItemCount() - 1);
    return itemView.findViewById(R.id.add_item_submit);
  }

  private void addItem(String name) {
    getItemInput().setText(name);
    getItemSubmit().performClick();
  }

  private void setItemInputAndWait(String input) {
    getItemInput().setText(input);
    Robolectric.flushForegroundThreadScheduler();
  }

  private static View getItemFromList(RecyclerView list, int position) {
    list.measure(0, 0);
    list.layout(0, 0, 100, 1000);
    return list.getChildAt(position);
  }
}
