package net.jonmiranda.pantry.storage;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.RealmClass;

@RealmClass
public class PantryItem extends RealmObject {

  private String name;
  private Date lastBought;
  private boolean inStock;

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

  public Date getLastBought() {
    return lastBought;
  }

  public void setLastBought(Date lastBought) {
    this.lastBought = lastBought;
  }

  public boolean isInStock() {
    return inStock;
  }

  public void setInStock(boolean inStock) {
    this.inStock = inStock;
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
