package net.jonmiranda.pantry.storage;

import android.support.annotation.Nullable;

import java.util.Date;
import java.util.List;

public interface Storage {
  List<PantryItem> getItems();

  boolean itemWithNameExists(String name);

  PantryItem addItem(String itemName);

  @Nullable PantryItem getItemWithName(String name);

  void setItemPurchased(String itemName, Date purchased);

  void updateItem(PantryItem item, String itemName, boolean inStock, Date purchased);

  void deleteItem(PantryItem item);

  void delete();
}
