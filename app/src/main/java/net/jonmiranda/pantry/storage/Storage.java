package net.jonmiranda.pantry.storage;

import android.content.Context;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;

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

  private PantryItem addItem(String itemName) {
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
    instance.setInStock(true);
    Date now = Calendar.getInstance().getTime();
    instance.setCreated(now);
    instance.setBought(now);

    realm.beginTransaction();
    item.getInstances().add(instance);
    realm.commitTransaction();
  }

  public void setItemAsOutOfSock(PantryItem item) {
    RealmList<PantryItemInstance> instances = item.getInstances();
    if (instances != null) {
      realm.beginTransaction();
      for (int i = 0; i < instances.size(); ++i) {
        instances.get(i).setInStock(false);
      }
      realm.commitTransaction();
    }
  }

  public void delete() {
    realm.close();
    Realm.deleteRealm(realm.getConfiguration());
  }
}
