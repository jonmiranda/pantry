package net.jonmiranda.pantry.storage;

import android.content.Context;

import java.util.Calendar;
import java.util.List;

import io.realm.Realm;

public class Storage {

  Realm realm;

  public Storage(Context context) {
    realm = Realm.getInstance(context);
  }

  public List<PantryItem> getItems() {
    return realm.allObjects(PantryItem.class);
  }

  public void add(String itemName) {
    PantryItem item = new PantryItem();
    item.setName(itemName);
    item.setCreated(Calendar.getInstance().getTime());
    realm.beginTransaction();
    realm.copyToRealm(item);
    realm.commitTransaction();
  }
}
