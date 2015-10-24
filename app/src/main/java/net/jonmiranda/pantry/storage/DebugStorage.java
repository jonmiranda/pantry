package net.jonmiranda.pantry.storage;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class DebugStorage implements Storage {

  private List<PantryItem> items;

  public DebugStorage() {
    items = new ArrayList<>();
  }

  public List<PantryItem> getItems() {
    Collections.sort(items, new Comparator<PantryItem>() {
      @Override
      public int compare(PantryItem lhs, PantryItem rhs) {
        return (int) (rhs.getPurchased().getTime() - lhs.getPurchased().getTime());
      }
    });
    return items;
  }

  public boolean itemWithNameExists(String name) {
    return getItemWithName(name) != null;
  }

  public PantryItem addItem(String itemName) {
    PantryItem item = new PantryItem();
    item.setInStock(true);
    item.setName(itemName);
    item.setPurchased(Calendar.getInstance().getTime());
    items.add(item);
    return item;
  }

  public PantryItem getItemWithName(String name) {
    for (int i = 0; i < items.size(); ++i) {
      if (items.get(i).getName().equals(name)) {
        return items.get(i);
      }
    }
    return null;
  }

  public void setItemPurchased(String itemName, Date purchased) {
    PantryItem item = getItemWithName(itemName);
    if (item != null) {
      item.setPurchased(purchased);
    }
  }

  public void setItemInStock(PantryItem item, boolean inStock) {
    item.setInStock(inStock);
    item.setPurchased(Calendar.getInstance().getTime());
  }

  public void delete() {
    items.clear();
  }
}
