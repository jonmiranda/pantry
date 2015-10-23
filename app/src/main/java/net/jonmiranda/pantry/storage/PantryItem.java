package net.jonmiranda.pantry.storage;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.RealmClass;

@RealmClass
public class PantryItem extends RealmObject {

  private String name;

  private RealmList<PantryItemInstance> instances;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public RealmList<PantryItemInstance> getInstances() {
    return instances;
  }

  public void setInstances(RealmList<PantryItemInstance> instances) {
    this.instances = instances;
  }

  public static boolean isInStock(PantryItem item) {
    RealmList<PantryItemInstance> instances = item.getInstances();
    if (instances == null) {
      return false;
    }
    boolean inStock = false;
    for (int i = 0; i < instances.size(); ++i) {
      inStock = inStock || instances.get(i).isInStock();
    }
    return inStock;
  }
}
