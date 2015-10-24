package net.jonmiranda.pantry.storage;

import java.util.Date;
import java.util.List;

public interface Storage {
  List<PantryItem> getItems();

  boolean itemWithNameExists(String name);

  PantryItem addItem(String itemName);

  PantryItem getItemWithName(String name);

  void setItemPurchased(String itemName, Date purchased);

  void setItemInStock(PantryItem item, boolean inStock);

  void delete();
}
