package net.jonmiranda.pantry;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import net.jonmiranda.pantry.storage.PantryItem;
import net.jonmiranda.pantry.storage.PantryItemInstance;
import net.jonmiranda.pantry.storage.Storage;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryItemViewHolder> {
  private static final int IN_STOCK_VIEW_TYPE = 0;
  private static final int OUT_OF_STOCK_VIEW_TYPE = 1;

  private final Storage storage;
  private final List<PantryItem> items;

  public PantryAdapter(Storage storage) {
    this.storage = storage;
    this.items = storage.getItems();
  }

  @Override
  public PantryItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    int layoutId = viewType == IN_STOCK_VIEW_TYPE
        ? R.layout.pantry_list_view_item_in
        : R.layout.pantry_list_view_item_out;
    View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
    if (viewType == IN_STOCK_VIEW_TYPE) {
      return new PantryItemViewHolderInStock(view);
    } else {
      return new PantryItemViewHolderOutOfStock(view);
    }
  }

  @Override
  public void onBindViewHolder(PantryItemViewHolder holder, int position) {
    PantryItem item = items.get(position);
    holder.bind(storage, item);
    holder.name.setText(item.getName());

    if (getItemViewType(position) == IN_STOCK_VIEW_TYPE) {
      onBindViewHolder((PantryItemViewHolderInStock) holder, item);
    } else {
      onBindViewHolder((PantryItemViewHolderOutOfStock) holder, item);
    }
  }

  private void onBindViewHolder(PantryItemViewHolderInStock holder, final PantryItem item) {
    holder.name.setChecked(item.isInStock());
    holder.setOnClickListener(this);
    PantryItemInstance instance = item.getInstances().first();
    Calendar endTime = Calendar.getInstance();
    Date startTime =
        (Date) Utils.firstNonNull(instance.getBought(), instance.getCreated(), endTime);
    holder.bought.setText(Utils.getDisplayableTime(
        endTime.get(Calendar.SECOND) - startTime.getSeconds()));
  }

  private void onBindViewHolder(PantryItemViewHolderOutOfStock holder, final PantryItem item) {
    holder.setOnClickListener(this);
  }

  @Override
  public int getItemCount() {
    return items.size();
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public int getItemViewType(int position) {
    return items.get(position).isInStock()
        ? IN_STOCK_VIEW_TYPE
        : OUT_OF_STOCK_VIEW_TYPE;
  }

  static class PantryItemViewHolder extends RecyclerView.ViewHolder {
    protected TextView name;
    protected PantryItem item;
    protected Storage storage;

    public PantryItemViewHolder(View view) {
      super(view);
      name = (TextView) view.findViewById(R.id.item_name);
    }

    public void bind(Storage storage, PantryItem item) {
      this.storage = storage;
      this.item = item;
    }
  }

  static class PantryItemViewHolderInStock extends PantryItemViewHolder {
    protected CheckBox name;
    protected TextView bought;

    public PantryItemViewHolderInStock(View view) {
      super(view);
      name = (CheckBox) super.name;
      bought = (TextView) view.findViewById(R.id.item_bought);
    }

    protected void setOnClickListener(final PantryAdapter adapter) {
      name.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          storage.setItemAsOutOfStock(item);
          adapter.notifyDataSetChanged();
        }
      });
    }
  }

  static class PantryItemViewHolderOutOfStock extends PantryItemViewHolder {
    protected View addInstance;

    public PantryItemViewHolderOutOfStock(View view) {
      super(view);
      addInstance = view.findViewById(R.id.item_add_instance);
    }

    public void setOnClickListener(final PantryAdapter adapter) {
      addInstance.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View unused) {
          storage.addInstance(item.getName());
          adapter.notifyDataSetChanged();
        }
      });
    }
  }
}