package net.jonmiranda.pantry.storage;

import android.content.Context;

import java.util.Calendar;
import java.util.List;

import io.realm.Realm;

public class Storage {

  private Realm realm;

  public Storage(Context context) {
    realm = Realm.getInstance(context);
  }

  public List<PantryItem> getItems() {
    return realm.allObjects(PantryItem.class);
  }

  public boolean itemWithNameExists(String name) {
    return getItemWithName(name) != null;
  }

  public PantryItem addItem(String itemName) {
    PantryItem item = new PantryItem();
    item.setName(itemName);

    realm.beginTransaction();
    item = realm.copyToRealm(item);
    realm.commitTransaction();
    return item;
  }

  public PantryItem getItemWithName(String name) {
    return realm.allObjects(PantryItem.class).where().contains("name", name).findFirst();
  }

  public void addInstance(String itemName) {
    PantryItem item = getItemWithName(itemName);
    if (item == null) {
      item = addItem(itemName);
    }
    PantryItemInstance instance = new PantryItemInstance();
    instance.setCreated(Calendar.getInstance().getTime());

    realm.beginTransaction();
    item.getInstances().add(instance);
    realm.commitTransaction();
  }

  public void delete() {
    realm.close();
    Realm.deleteRealm(realm.getConfiguration());
  }
}
