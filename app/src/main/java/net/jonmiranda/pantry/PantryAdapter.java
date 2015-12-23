package net.jonmiranda.pantry;

import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.jakewharton.rxbinding.widget.TextViewAfterTextChangeEvent;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import net.jonmiranda.pantry.storage.PantryItem;
import net.jonmiranda.pantry.storage.Storage;

import java.util.Calendar;
import java.util.Date;

import rx.functions.Action1;

public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryItemViewHolder> {

  private final View fabCoordinator;
  private final Context context;
  private final PantryItemListener listener;
  private final Storage storage;

  public PantryAdapter(View fabCoordinator, Context context, PantryItemListener listener, Storage storage) {
    this.fabCoordinator = fabCoordinator;
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

    holder.checkbox.setChecked(item.isInStock());
    holder.setOnClickListener(fabCoordinator, context, listener, storage, item, this);

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
    protected CheckBox checkbox;
    protected EditText name;
    protected TextView purchased;
    protected View more;

    public PantryItemViewHolder(View view) {
      super(view);
      checkbox = (CheckBox) view.findViewById(R.id.item_checkbox);
      name = (EditText) view.findViewById(R.id.item_name);
      purchased = (TextView) view.findViewById(R.id.item_purchased);
      more = view.findViewById(R.id.item_more);
    }

    protected void setOnClickListener(
        final View fabCoordinator,
        final Context context,
        final PantryItemListener listener,
        final Storage storage,
        final PantryItem item,
        final PantryAdapter adapter) {
      RxTextView.afterTextChangeEvents(name)
          .compose(((RxAppCompatActivity) context).<TextViewAfterTextChangeEvent>bindToLifecycle())
          .subscribe(new Action1<TextViewAfterTextChangeEvent>() {
            @Override
            public void call(TextViewAfterTextChangeEvent event) {
              String itemName = event.editable().toString();
              storage.updateItem(item, itemName, item.isInStock(), item.getPurchased());
            }
          });
      RxView.focusChanges(name)
          .compose(((RxAppCompatActivity) context).<Boolean>bindToLifecycle())
          .subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean isFocused) {
              more.setVisibility(isFocused ? View.VISIBLE : View.GONE);
            }
          });
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
      checkbox.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          int messageResource = checkbox.isChecked()
              ? R.string.item_in_stock
              : R.string.item_out_of_stock;
          Snackbar
              .make(fabCoordinator, messageResource, Snackbar.LENGTH_SHORT)
              .setAction(R.string.undo, new View.OnClickListener() {
                @Override
                public void onClick(View unused) {
                  Date undoPurchased = item.getPurchased();
                  storage.updateItem(item, item.getName(), !checkbox.isChecked(), undoPurchased);
                  adapter.notifyDataSetChanged();
                }
              })
              .show();

          Date purchased = item.getPurchased();
          if (checkbox.isChecked()) {
            purchased = Utils.getTodaysDate();
          }
          storage.updateItem(item, item.getName(), checkbox.isChecked(), purchased);
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