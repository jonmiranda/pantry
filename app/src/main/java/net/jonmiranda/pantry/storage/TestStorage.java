package net.jonmiranda.pantry.storage;

import net.jonmiranda.pantry.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class TestStorage implements Storage {

  private List<PantryItem> items;

  public TestStorage() {
    items = new ArrayList<>();
  }

  public List<PantryItem> getItems() {
    Collections.sort(items, new Comparator<PantryItem>() {
      @Override
      public int compare(PantryItem lhs, PantryItem rhs) {
        return (int) (lhs.getPurchased().getTime() - rhs.getPurchased().getTime());
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
    item.setPurchased(Utils.getTodaysDate());
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


  public void updateItem(PantryItem item, String itemName, boolean inStock, Date purchased) {
    item.setName(itemName);
    item.setInStock(inStock);
    item.setPurchased(purchased);
  }

  public void deleteItem(PantryItem item) {
    for (int i = 0; i < items.size(); ++i) {
      if (items.get(i).getName().equals(item.getName())) {
        items.remove(i);
      }
    }
  }

  public void delete() {
    items.clear();
  }
}
