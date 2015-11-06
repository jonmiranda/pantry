package net.jonmiranda.pantry;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
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

public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryItemViewHolder> {

  private final Context context;
  private final PantryItemListener listener;
  private final Storage storage;

  public PantryAdapter(Context context, PantryItemListener listener, Storage storage) {
    this.context = context;
    this.listener = listener;
    this.storage = storage;
  }

  @Override
  public PantryItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(
        parent.getContext()).inflate(R.layout.pantry_list_view_item, parent, false);
    return new PantryItemViewHolder(view);
  }

  @Override
  public void onBindViewHolder(PantryItemViewHolder holder, int position) {
    PantryItem item = storage.getItems().get(position);
    holder.name.setText(item.getName());

    holder.name.setChecked(item.isInStock());
    holder.setOnClickListener(context, listener, storage, item, this);

    Calendar endTime = Calendar.getInstance();
    Date startTime =
        (Date) Utils.firstNonNull(item.getPurchased(), endTime);
    holder.purchased.setText(
        Utils.getDisplayableTime(endTime.getTimeInMillis() - startTime.getTime()));
  }

  @Override
  public int getItemCount() {
    return storage.getItems().size();
  }

  static class PantryItemViewHolder extends RecyclerView.ViewHolder {
    protected CheckBox name;
    protected TextView purchased;
    protected View more;

    public PantryItemViewHolder(View view) {
      super(view);
      name = (CheckBox) view.findViewById(R.id.item_name);
      purchased = (TextView) view.findViewById(R.id.item_purchased);
      more = view.findViewById(R.id.item_more);
    }

    protected void setOnClickListener(
        final Context context,
        final PantryItemListener listener,
        final Storage storage,
        final PantryItem item,
        final PantryAdapter adapter) {
      more.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          new AlertDialog.Builder(context)
              .setMessage(context.getString(R.string.confirm_delete_message, item.getName()))
              .setTitle(context.getString(R.string.confirm_delete))
              .setPositiveButton(context.getString(R.string.delete), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                  storage.deleteItem(item);
                  adapter.notifyDataSetChanged();
                }
              })
              .setNegativeButton(context.getString(R.string.cancel), null)
              .create()
              .show();
        }
      });
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
          listener.onItemPurchaseClicked(item.getName());
        }
      });
    }
  }
}