package net.jonmiranda.pantry.storage;

import android.content.Context;

import net.jonmiranda.pantry.Utils;

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
    items.sort(new String[] {"inStock", "purchased", "name"}, new boolean[] {false, true, true});
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

    realm.beginTransaction();
    item = realm.copyToRealm(item);
    realm.commitTransaction();
    return item;
  }

  public PantryItem getItemWithName(String name) {
    return realm.allObjects(PantryItem.class).where().equalTo("name", name).findFirst();
  }

  public void updateItem(PantryItem item, String itemName, boolean inStock, Date purchased) {
    realm.beginTransaction();
    item.setName(itemName);
    item.setInStock(inStock);
    item.setPurchased(purchased);
    realm.commitTransaction();
  }

  public void deleteItem(PantryItem item) {
    realm.beginTransaction();
    item.removeFromRealm();
    realm.commitTransaction();
  }

  public void delete() {
    realm.close();
    Realm.deleteRealm(realm.getConfiguration());
  }
}
