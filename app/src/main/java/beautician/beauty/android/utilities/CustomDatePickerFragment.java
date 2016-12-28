package beautician.beauty.android.utilities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import beautician.beauty.android.R;


@SuppressLint({"ValidFragment", "SimpleDateFormat", "InlinedApi"})
public class CustomDatePickerFragment extends DialogFragment implements OnDateSetListener {

    TextView mTextView;
    boolean isClick = false;
    Activity mActivity;
    InterfaceDatePicker mInterfaceDatePicker;
    boolean isBday = false;

    private Dialog mDialog;
    private DatePicker mDatePicker;

    public CustomDatePickerFragment(TextView mTextView, Activity activity) {
        this.mTextView = mTextView;
        isClick = false;
        mActivity = activity;
        showDatePickerDialog();
//        showDialog();
    }

    public CustomDatePickerFragment(InterfaceDatePicker interfaceDatePicker, Activity activity) {
        this.mInterfaceDatePicker = interfaceDatePicker;
        isClick = false;
        mActivity = activity;
        showDatePickerDialog();
//        showDialog();
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);


        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), DatePickerDialog.THEME_HOLO_LIGHT, this, year, month, day);
    }


    @Override
    public void onDateSet(DatePicker arg0, int year, int month, int day) {
        final Calendar c = Calendar.getInstance();
        c.set(Calendar.MONTH, month);
        c.set(Calendar.YEAR, year);
        c.set(Calendar.DAY_OF_MONTH, day);
        String date = getDateInFormate(c);
        mTextView.setText(date);
    }

    public String getDateInFormate(Calendar calendar) {
        String formatedDate = "";
        Date mDate = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        formatedDate = sdf.format(mDate);
        return formatedDate;
    }

    @SuppressLint("NewApi")
    public void showDatePickerDialog() {
        final DatePicker mDatePicker = new DatePicker(mActivity);
        mDatePicker.setCalendarViewShown(false);
        Calendar c = Calendar.getInstance();
        if (!isBday) {
            c.set(Calendar.HOUR_OF_DAY, 00);
            mDatePicker.setMinDate(c.getTimeInMillis());

//            if(mTextView!=null) {
//                String mCurrentDate = mTextView.getText().toString().trim();
//                if (!mCurrentDate.equalsIgnoreCase(mActivity.getString(R.string.lbl_date)))
//                {
//                    String[] mStringDate = mCurrentDate.split("/");
//                    int day = Integer.parseInt(mStringDate[0]);
//                    int month = Integer.parseInt(mStringDate[1])-1;
//                    int year = Integer.parseInt(mStringDate[2]);
//                    mDatePicker.init(year, month, day, null);
//                }
//            }
        }


        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(mActivity);
        alertDialog.setTitle(mActivity.getResources().getString(R.string.lbl_select_Date));
        alertDialog.setView(mDatePicker);
        alertDialog.setPositiveButton(mActivity.getResources().getString(R.string.lbl_ok), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                final Calendar c = Calendar.getInstance();
                c.set(Calendar.MONTH, mDatePicker.getMonth());
                c.set(Calendar.YEAR, mDatePicker.getYear());
                c.set(Calendar.DAY_OF_MONTH, mDatePicker.getDayOfMonth());
                String date = getDateInFormate(c);

                if (mTextView != null) {
                    mTextView.setText(date);
                }
                if (mInterfaceDatePicker != null)
                    mInterfaceDatePicker.onDateSelected(mDatePicker.getYear(), mDatePicker.getMonth(), mDatePicker.getDayOfMonth());
            }
        });
        alertDialog.setNegativeButton(mActivity.getResources().getString(R.string.lbl_cancel), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertDialog.show();
    }

    public void showDialog()
    {
        mDialog = new Dialog(mActivity);
        View view = mActivity.getLayoutInflater().inflate(R.layout.dialog_datepicker, null);
        mDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.setContentView(view);
        setCancelable(true);
        mDialog.show();

        mDatePicker = (DatePicker)mDialog.findViewById(R.id.dialog_datepicker_datepicker);
        TextView mTextViewOk = (TextView)mDialog.findViewById(R.id.dialog_datepicker_textview_button1);

        mTextViewOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                c.set(Calendar.MONTH, mDatePicker.getMonth());
                c.set(Calendar.YEAR, mDatePicker.getYear());
                c.set(Calendar.DAY_OF_MONTH, mDatePicker.getDayOfMonth());
                String date = getDateInFormate(c);

                if (mTextView != null) {
                    mTextView.setText(date);
                }
                if (mInterfaceDatePicker != null)
                    mInterfaceDatePicker.onDateSelected(mDatePicker.getYear(), mDatePicker.getMonth(), mDatePicker.getDayOfMonth());
                mDialog.cancel();
            }
        });
    }

}
