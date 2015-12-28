package net.jonmiranda.pantry;

import net.jonmiranda.pantry.storage.PantryItem;

public interface PantryItemListener {
  void onItemPurchaseClicked(PantryItem item);
}
