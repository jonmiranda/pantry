package net.jonmiranda.pantry.storage;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.RealmClass;

@RealmClass
public class PantryItem extends RealmObject {

  private String name;
  private boolean inStock;
  private Date purchased;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isInStock() {
    return inStock;
  }

  public void setInStock(boolean inStock) {
    this.inStock = inStock;
  }

  public Date getPurchased() {
    return purchased;
  }

  public void setPurchased(Date purchased) {
    this.purchased = purchased;
  }
}
