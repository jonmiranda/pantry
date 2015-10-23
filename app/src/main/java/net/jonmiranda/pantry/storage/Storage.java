package net.jonmiranda.pantry.storage;

import android.content.Context;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class Storage {

  private Realm realm;

  public Storage(Context context) {
    realm = Realm.getInstance(context);
  }

  public List<PantryItem> getItems() {
    RealmResults<PantryItem> items = realm.allObjects(PantryItem.class);
    items.sort(new String[] {"inStock", "lastBought"}, new boolean[] {false, false});
    return items;
  }

  public boolean itemWithNameExists(String name) {
    return getItemWithName(name) != null;
  }

  private PantryItem addItem(String itemName) {
    PantryItem item = new PantryItem();
    item.setInStock(true);
    item.setLastBought(Calendar.getInstance().getTime());
    item.setName(itemName);

    realm.beginTransaction();
    item = realm.copyToRealm(item);
    realm.commitTransaction();
    return item;
  }

  public PantryItem getItemWithName(String name) {
    return realm.allObjects(PantryItem.class).where().equalTo("name", name).findFirst();
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
    item.setInStock(true);
    item.setLastBought(now);
    item.getInstances().add(instance);
    realm.commitTransaction();
  }

  public void setItemAsOutOfStock(PantryItem item) {
    RealmList<PantryItemInstance> instances = item.getInstances();
    realm.beginTransaction();
    item.setInStock(false);
    if (instances != null) {
      for (int i = 0; i < instances.size(); ++i) {
        instances.get(i).setInStock(false);
      }
    }
    realm.commitTransaction();
  }

  public void delete() {
    realm.close();
    Realm.deleteRealm(realm.getConfiguration());
  }
}
