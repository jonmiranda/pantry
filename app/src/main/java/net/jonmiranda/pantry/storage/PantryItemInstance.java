package net.jonmiranda.pantry.storage;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.RealmClass;

@RealmClass
public class PantryItemInstance extends RealmObject {

  private int quantity;
  private Date created;
  private Date bought;

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }

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
}
