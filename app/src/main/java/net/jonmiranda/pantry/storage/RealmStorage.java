package net.jonmiranda.pantry.storage;

import android.content.Context;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.exceptions.RealmMigrationNeededException;

public class RealmStorage implements Storage {

  private Realm realm;

  public RealmStorage(Context context) {
    try {
      realm = Realm.getInstance(context);
    } catch (RealmMigrationNeededException e) {
      delete();
      realm = Realm.getInstance(context);
    }
  }

  public List<PantryItem> getItems() {
    RealmResults<PantryItem> items = realm.allObjects(PantryItem.class);
    items.sort("inStock", false);
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

    realm.beginTransaction();
    item = realm.copyToRealm(item);
    realm.commitTransaction();
    return item;
  }

  public PantryItem getItemWithName(String name) {
    return realm.allObjects(PantryItem.class).where().equalTo("name", name).findFirst();
  }

  public void setItemPurchased(String itemName, Date purchased) {
    PantryItem item = getItemWithName(itemName);
    if (item != null) {
      realm.beginTransaction();
      item.setPurchased(purchased);
      realm.commitTransaction();
    }
  }

  public void setItemInStock(PantryItem item, boolean inStock) {
    realm.beginTransaction();
    item.setInStock(inStock);
    item.setPurchased(Calendar.getInstance().getTime());
    realm.commitTransaction();
  }

  public void delete() {
    realm.close();
    Realm.deleteRealm(realm.getConfiguration());
  }
}
