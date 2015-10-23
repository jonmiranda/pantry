package net.jonmiranda.pantry.storage;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.RealmClass;

@RealmClass
public class PantryItemInstance extends RealmObject {

  private Date created;
  private Date bought;
  private boolean inStock;

  public Date getCreated() {
    return created;
  }

  public void setCreated(Date created) {
    this.created = created;
  }

  public Date getBought() {
    return bought;
  }

  public void setBought(Date bought) {
    this.bought = bought;
  }

  public boolean isInStock() {
    return inStock;
  }

  public void setInStock(boolean inStock) {
    this.inStock = inStock;
  }
}
