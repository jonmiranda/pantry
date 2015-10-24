package net.jonmiranda.pantry;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import net.jonmiranda.pantry.storage.PantryItem;
import net.jonmiranda.pantry.storage.Storage;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryItemViewHolder> {
  private final PantryItemListener listener;
  private final Storage storage;
  private final List<PantryItem> items;

  public PantryAdapter(PantryItemListener listener, Storage storage) {
    this.listener = listener;
    this.storage = storage;
    this.items = storage.getItems();
  }

  @Override
  public PantryItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(
        parent.getContext()).inflate(R.layout.pantry_list_view_item, parent, false);
    return new PantryItemViewHolder(view);
  }

  @Override
  public void onBindViewHolder(PantryItemViewHolder holder, int position) {
    PantryItem item = items.get(position);
    holder.name.setText(item.getName());

    holder.name.setChecked(item.isInStock());
    holder.setOnClickListener(listener, storage, item, this);

    Calendar endTime = Calendar.getInstance();
    Date startTime =
        (Date) Utils.firstNonNull(item.getPurchased(), endTime);
    holder.purchased.setText(
        Utils.getDisplayableTime(endTime.getTimeInMillis() - startTime.getTime()));
  }

  @Override
  public int getItemCount() {
    return items.size();
  }

  static class PantryItemViewHolder extends RecyclerView.ViewHolder {
    protected CheckBox name;
    protected TextView purchased;

    public PantryItemViewHolder(View view) {
      super(view);
      name = (CheckBox) view.findViewById(R.id.item_name);
      purchased = (TextView) view.findViewById(R.id.item_purchased);
    }

    protected void setOnClickListener(
        final PantryItemListener listener,
        final Storage storage,
        final PantryItem item,
        final PantryAdapter adapter) {
      name.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          storage.setItemInStock(item, name.isChecked());
          adapter.notifyDataSetChanged();
        }
      });
      purchased.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          listener.onItemClicked(item.getName());
        }
      });
    }
  }
}