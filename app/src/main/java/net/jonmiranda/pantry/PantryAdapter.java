package net.jonmiranda.pantry;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.VisibleForTesting;
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
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.BasePantryItemViewHolder> {

  private static final int ITEM_COUNT_OFFSET = 1; // +1 for Add Item View

  private static final int ADD_ITEM_VIEW_TYPE = 0;
  private static final int ITEM_VIEW_TYPE = 1;

  private final View rootView;
  private final PantryItemListener listener;
  private final Storage storage;

  public PantryAdapter(View rootView, PantryItemListener listener, Storage storage) {
    this.rootView = rootView;
    this.listener = listener;
    this.storage = storage;
  }

  @Override
  public BasePantryItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    int layoutResourceId = viewType == ADD_ITEM_VIEW_TYPE
        ? R.layout.pantry_list_add_item
        : R.layout.pantry_list_view_item;
    View view = LayoutInflater.from(parent.getContext()).inflate(layoutResourceId, parent, false);
    if (viewType == ADD_ITEM_VIEW_TYPE) {
      return new PantryAddItemViewHolder(view);
    } else {
      return new PantryItemViewHolder(view);
    }
  }

  @Override
  public int getItemViewType(int position) {
    return position == getItemCount() - 1
        ? ADD_ITEM_VIEW_TYPE
        : ITEM_VIEW_TYPE;
  }

  @Override
  public void onBindViewHolder(final BasePantryItemViewHolder holder, int position) {
    final PantryItem item = position >= getItemCount() - ITEM_COUNT_OFFSET
        ? null
        : storage.getItems().get(position);
    holder.onBind(rootView, this, storage, listener, item);
  }

  @Override
  public int getItemCount() {
    return storage.getItems().size() + ITEM_COUNT_OFFSET;
  }

  static abstract class BasePantryItemViewHolder extends RecyclerView.ViewHolder {
    public BasePantryItemViewHolder(View view) {
      super(view);
    }

    public abstract void onBind(
        View rootView,
        PantryAdapter pantryAdapter,
        Storage storage,
        PantryItemListener listener,
        PantryItem item);
  }

  static class PantryAddItemViewHolder extends BasePantryItemViewHolder {
    private static final int ITEM_INPUT_WAIT_TIME_MS = 200;

    protected Subscription inputSubscription;
    protected EditText input;
    protected View submit;

    public PantryAddItemViewHolder(View view) {
      super(view);
      itemView.setTag(this);
      input = (EditText) view.findViewById(R.id.add_item_input);
      submit = view.findViewById(R.id.add_item_submit);
    }

    @VisibleForTesting
    Observable<String> getAddItemInputObservable(Context context) {
      return RxTextView.afterTextChangeEvents(input)
          .compose(((RxAppCompatActivity) context).<TextViewAfterTextChangeEvent>bindToLifecycle())
          .observeOn(AndroidSchedulers.mainThread())
          .map(new Func1<TextViewAfterTextChangeEvent, String>() {
            @Override
            public String call(TextViewAfterTextChangeEvent event) {
              return sanitizeItemName(event.editable().toString());
            }
          })
          .debounce(ITEM_INPUT_WAIT_TIME_MS, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread());
    }

    @Override
    public void onBind(
        View rootView,
        final PantryAdapter pantryAdapter,
        final Storage storage,
        PantryItemListener unused,
        final PantryItem unused2) {
      if (inputSubscription != null) {
        inputSubscription.unsubscribe();
      }

      final Context context = rootView.getContext();
      inputSubscription = getAddItemInputObservable(context)
          .subscribe(new Action1<String>() {
            @Override
            public void call(String itemName) {
              boolean enableSubmit = !itemName.isEmpty();
              if (storage.itemWithNameExists(itemName)) {
                input.setError(context.getString(R.string.item_already_exists));
                enableSubmit = false;
              }
              submit.setEnabled(enableSubmit);
            }
          });

      RxView.clicks(submit)
          .subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
              String itemName = sanitizeItemName(input.getText().toString());
              storage.addItem(itemName);
              pantryAdapter.notifyDataSetChanged();
              input.setText("");
              input.setError(null);
            }
          });
    }

    private String sanitizeItemName(String itemName) {
      return itemName.trim();
    }
  }

  static class PantryItemViewHolder extends BasePantryItemViewHolder {
    protected Subscription focusSubscription;
    protected Subscription textSubscription;
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

    public void onBind(
        final View rootView,
        final PantryAdapter adapter,
        final Storage storage,
        final PantryItemListener listener,
        final PantryItem item) {
      final Context context = rootView.getContext();
      if (textSubscription != null) {
        textSubscription.unsubscribe();
      }
      if (focusSubscription != null) {
        focusSubscription.unsubscribe();
      }

      name.setText(item.getName());
      textSubscription = RxTextView.afterTextChangeEvents(name)
          .compose(((RxAppCompatActivity) context).<TextViewAfterTextChangeEvent>bindToLifecycle())
          .subscribe(new Action1<TextViewAfterTextChangeEvent>() {
            @Override
            public void call(TextViewAfterTextChangeEvent event) {
              String itemName = event.editable().toString();
              storage.updateItem(item, itemName, item.isInStock(), item.getPurchased());
            }
          });

      focusSubscription = RxView.focusChanges(name)
          .compose(((RxAppCompatActivity) context).<Boolean>bindToLifecycle())
          .subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean isFocused) {
              more.setVisibility(isFocused ? View.VISIBLE : View.GONE);
            }
          });
      checkbox.setChecked(item.isInStock());
      setOnClickListener(rootView, listener, storage, item, adapter);

      Calendar endTime = Calendar.getInstance();
      Date startTime = (Date) Utils.firstNonNull(item.getPurchased(), endTime);
      purchased.setText(
          Utils.getDisplayableTime(endTime.getTimeInMillis() - startTime.getTime()));
    }

    protected void setOnClickListener(
        final View rootView,
        final PantryItemListener listener,
        final Storage storage,
        final PantryItem item,
        final PantryAdapter adapter) {
      final Context context = rootView.getContext();
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
              .make(rootView, messageResource, Snackbar.LENGTH_SHORT)
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