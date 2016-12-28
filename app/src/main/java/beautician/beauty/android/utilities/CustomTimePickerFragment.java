package beautician.beauty.android.utilities;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.lang.reflect.Field;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.jar.Attributes;

import beautician.beauty.android.R;


@TargetApi(Build.VERSION_CODES.HONEYCOMB)
@SuppressLint({"ValidFragment", "SimpleDateFormat"})
public class CustomTimePickerFragment {

    TextView mTextView;
    boolean isClick = false;
    InterfaceTimePicker mInterfaceTimePicker;
    Activity mActivity;
    int TIME_PICKER_INTERVAL = 15;
    boolean isStart = false;

    public CustomTimePickerFragment(TextView mTextView, Activity activity) {
        this.mTextView = mTextView;
        isClick = false;
        mActivity = activity;
        showTimePickerDialog();
    }

    public CustomTimePickerFragment(InterfaceTimePicker interfaceTimePicker, Activity activity) {
        this.mInterfaceTimePicker = interfaceTimePicker;
        isClick = false;
        mActivity = activity;
        showTimePickerDialog();
    }

    public void setTimeListener(InterfaceTimePicker interfaceTimePicker, boolean isStart)
    {
        this.mInterfaceTimePicker = interfaceTimePicker;
        this.isStart = isStart;
    }

    public String getTimeInFormate(Calendar calendar) {
        String formatedDate = "";
        Date mDate = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        formatedDate = sdf.format(mDate);
        return formatedDate;
    }


    public void showTimePickerDialog() {
        final TimePicker mTimePicker = new TimePicker(mActivity, null, TimePickerDialog.THEME_DEVICE_DEFAULT_LIGHT);
        mTimePicker.setIs24HourView(true);
        mTimePicker.setCurrentHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
        mTimePicker.setCurrentMinute(Calendar.getInstance().get(Calendar.MINUTE));
        setMinuteInterval(mTimePicker);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mActivity);
        alertDialog.setTitle(mActivity.getResources().getString(R.string.lbl_select_time));
        alertDialog.setView(mTimePicker);
        alertDialog.setPositiveButton(mActivity.getResources().getString(R.string.lbl_ok), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                final Calendar c = Calendar.getInstance();
                c.set(Calendar.MINUTE, mTimePicker.getCurrentMinute() * TIME_PICKER_INTERVAL);
                c.set(Calendar.HOUR_OF_DAY, mTimePicker.getCurrentHour());
                String date = getTimeInFormate(c);
                if (mTextView != null)
                    mTextView.setText(date.toUpperCase());
                mTextView.setTextColor(ContextCompat.getColor(mActivity, R.color.myPrimaryColor));
//
                if (mInterfaceTimePicker != null)
                    mInterfaceTimePicker.onTimeSet(date, isStart);
            }
        });
        alertDialog.setNegativeButton(mActivity.getResources().getString(R.string.lbl_cancel), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alertDialog.show();
    }

    public void setMinuteInterval(TimePicker timePicker) {
        try {
            Class<?> classForid = Class.forName("com.android.internal.R$id");
            Field timePickerField = classForid.getField("timePicker");
            // this.timePicker = (TimePicker) findViewById(timePickerField.getInt(null));
            Field field = classForid.getField("minute");
            //String[] mstring={"00","15","30","45","60"};

            NumberPicker mMinuteSpinner = (NumberPicker) timePicker
                    .findViewById(field.getInt(null));
            mMinuteSpinner.setMinValue(0);
            mMinuteSpinner.setMaxValue((60 / TIME_PICKER_INTERVAL) - 1);
            //mMinuteSpinner.setDisplayedValues(mstring);
            List<String> displayedValues = new ArrayList<String>();
            for (int i = 0; i < 60; i += TIME_PICKER_INTERVAL) {
                displayedValues.add(String.format("%02d", i));
            }
            mMinuteSpinner.setDisplayedValues(displayedValues
                    .toArray(new String[0]));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
