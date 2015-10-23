package net.jonmiranda.pantry;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

  public static final String TAG = DatePickerFragment.class.getCanonicalName();
  public static final String DATE_IN_MS_KEY = "DATE_IN_MS_KEY";

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    Bundle arguments = getArguments();
    final Calendar date = Calendar.getInstance();
    long dateInMilliseconds = arguments.getLong(DATE_IN_MS_KEY, date.getTimeInMillis());
    date.setTimeInMillis(dateInMilliseconds);

    int year = date.get(Calendar.YEAR);
    int month = date.get(Calendar.MONTH);
    int day = date.get(Calendar.DAY_OF_MONTH);

    return new DatePickerDialog(getActivity(), (MainActivity) getActivity(), year, month, day);
  }

  public void onDateSet(DatePicker view, int year, int month, int day) {
    final Calendar date = Calendar.getInstance();
    date.set(year, month, day);
  }

  public static DatePickerFragment newInstance(long dateInMilliseconds) {
    Bundle arguments = new Bundle();
    arguments.putLong(DATE_IN_MS_KEY, dateInMilliseconds);
    DatePickerFragment fragment = new DatePickerFragment();
    fragment.setArguments(arguments);
    return fragment;
  }
}
