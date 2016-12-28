package beautician.beauty.android.utilities;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.NotificationCompat;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import beautician.beauty.android.R;
import beautician.beauty.android.activities.MainActivity;
import beautician.beauty.android.services.AppointmentRemindService;

/**
 * Created by npatel on 12/24/2015.
 */
public class CommonMethod {


    String TAG = "COMMON METHOD";
    Context mContext;

    public CommonMethod(Context activity) {
        mContext = activity;

    }

    public String getImageDirectory(String mStringFolderName) {

        String appPath = "";
        try {

            File SDCardRoot = Environment.getExternalStorageDirectory();
            File folder = new File(SDCardRoot, mContext.getString(R.string.app_name) + "/" + mStringFolderName);
            if (!folder.exists())
                folder.mkdirs();

            appPath = folder.getAbsolutePath();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return appPath;
    }


    /**
     * Function will resize bitmap
     *
     * @param mBitmap
     * @return Bitmap
     */
    public Bitmap resizeBitmap(Bitmap mBitmap) {
        try {
            mBitmap = Bitmap.createScaledBitmap(mBitmap, (int) (mBitmap.getWidth() * 0.5), (int) (mBitmap.getHeight() * 0.5), true);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mBitmap;
    }

    /**
     * Method will write file
     * @param mStringImagePath
     * @param mBitmap
     */
    public void copyImageFile(String mStringImagePath, Bitmap mBitmap) {
        OutputStream fOut = null;
        File file = new File(mStringImagePath);
        try {
            fOut = new FileOutputStream(file);
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
            // mBitmap.recycle();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Function will download file from URL
     *
     * @param mStringUrl
     * @return local file path
     */
    public String downloadFile(String mStringUrl, String path) {
        String file_path = "";
        try {

//            URL url = new URL(mStringUrl);
//
//            //create the new connection
//            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//
//            //set up some things on the connection
//            urlConnection.setRequestMethod("GET");
//            urlConnection.setDoOutput(true);
//
//            //and connect!
//            urlConnection.connect();
//
//            //set the path where we want to save the file
//            //in this case, going to save it on the root directory of the
//            //sd card.
//            File SDCardRoot = Environment.getExternalStorageDirectory();
//            //create a new file, specifying the path, and the filename
//            //which we want to save the file as.

            File file = new File(path);
            file_path = file.getAbsolutePath();

            //this will be used to write the downloaded data into the file we created
            FileOutputStream fileOutput = new FileOutputStream(file);

            //this will be used in reading the data from the internet
//            InputStream inputStream = urlConnection.getInputStream();
            InputStream inputStream = new URL(mStringUrl).openStream();

            //this is the total size of the file
//	        int totalSize = urlConnection.getContentLength();
            //variable to store total downloaded bytes
//	        int downloadedSize = 0;

            //create a buffer...
            byte[] buffer = new byte[1024];
            int bufferLength = 0; //used to store a temporary size of the buffer

            //now, read through the input buffer and write the contents to the file
            while ((bufferLength = inputStream.read(buffer)) > 0) {
                //add the data in the buffer to the file in the file output stream (the file on the sd card
                fileOutput.write(buffer, 0, bufferLength);
                //add up the size so we know how much is downloaded
//	                downloadedSize += bufferLength;
                //this is where you would do something to report the prgress, like this maybe
//	                updateProgress(downloadedSize, totalSize);

            }
            //close the output stream when done
            fileOutput.close();

            //catch some possible errors...
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return "";
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        return file_path;
    }

    /**
     * Function will hide softkeyboard
     *
     * @param mEditText
     */
    public void HideKeyboard(final View mEditText) {
        final InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            // imm.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY);
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
                }
            }, 200);
        }
    }

    /**
     * Function will show softkeyboard
     *
     * @param mEditText
     */
    public void showKeyboard(final EditText mEditText) {
        final InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    imm.showSoftInput(mEditText, 0);
                }
            }, 200);

        }
    }


    /**
     * Function will return current Month and Year
     *
     * @param calendar
     * @return Month and Year in String
     */
    public String getFullMonthYear(Calendar calendar) {
        String formatedDate = "";
        Date mDate = calendar.getTime();
        SimpleDateFormat sdf;
        // if(locale.equalsIgnoreCase("en"))
        sdf = new SimpleDateFormat("MMMM yyyy", new Locale("en"));
        // else
        // sdf = new SimpleDateFormat("MMMM yyyy", new Locale("nl_NL"));
        formatedDate = sdf.format(mDate);
        return formatedDate;
    }


    /**
     * Function will convert date format
     * @param mStringDate
     * @return date in new format
     */
    public String getDateInFormate(String mStringDate, String oldFormat, String newFormat) {
        String formatedDate = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat(oldFormat);
        Date myDate = null;
        try {
            myDate = dateFormat.parse(mStringDate);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat timeFormat = new SimpleDateFormat(newFormat);
        formatedDate = timeFormat.format(myDate);

        return formatedDate;
    }

    public String getDateInFormateFromDate(Date mDate, String newFormat) {
        String formatedDate = "";

        SimpleDateFormat timeFormat = new SimpleDateFormat(newFormat);
        formatedDate = timeFormat.format(mDate);

        return formatedDate;
    }

    /**
     * Function will return current Month and Year
     * @param calendar
     * @return Month and Year in String
     */
    public String getFormateFromCalendar(Calendar calendar, String format) {
        String formatedDate = "";
        Date mDate = calendar.getTime();
        SimpleDateFormat sdf;
        sdf = new SimpleDateFormat(format, new Locale("en"));
        formatedDate = sdf.format(mDate);
        return formatedDate;
    }


    public long getTimeFromString(String date, String date_formate)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat(date_formate);
        Date myDate = null;
        try {
            myDate = dateFormat.parse(date);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return myDate.getTime() ;
    }


    /**
     * Method will return distance between two location
     *
     * @param Start
     * @param End
     * @return distance
     */
    public double getDistance(LatLng Start, LatLng End) {
        double Radius = 6371;
        double lat1 = Start.latitude;
        double lat2 = End.latitude;
        double lon1 = Start.longitude;
        double lon2 = End.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double km = Radius * c;
        double meter = km * 1000;
        return meter;
    }

    /**
     * Method call will set two digit total price
     * @param mFloatValue
     * @return
     */
    public String getTwodigitValue(float mFloatValue) {
        NumberFormat formatter = NumberFormat.getNumberInstance();
        formatter.setMinimumFractionDigits(2);
        formatter.setMaximumFractionDigits(2);
        return String.valueOf(formatter.format(mFloatValue));
    }

    public void setNotificationForAppointment(String appointment_id, String appointment_time, String name)
    {
        Long app_time = getTimeFromString(appointment_time, StaticData.DATE_FORMAT_6);
        AlarmManager alarmManager = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);

        Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(app_time);
        mCalendar.add(Calendar.HOUR, -2);
        Intent myIntentRemind = new Intent(mContext, AppointmentRemindService.class);
        myIntentRemind.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        myIntentRemind.putExtra(mContext.getString(R.string.bundle_appointment_id), appointment_id);
        myIntentRemind.putExtra(mContext.getString(R.string.bundle_appointment_time), appointment_time);
        myIntentRemind.putExtra(mContext.getString(R.string.bundle_is_showup_notification), false);
        myIntentRemind.putExtra(mContext.getString(R.string.bundle_provider_name), name);
        PendingIntent pendingIntentRemind = PendingIntent.getService(mContext, Integer.parseInt(appointment_id + 1000), myIntentRemind, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis(), pendingIntentRemind);


        Intent myIntentAlarm = new Intent(mContext, AppointmentRemindService.class);
        myIntentAlarm.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        myIntentAlarm.putExtra(mContext.getString(R.string.bundle_appointment_id), appointment_id);
        myIntentAlarm.putExtra(mContext.getString(R.string.bundle_appointment_time), appointment_time);
        myIntentAlarm.putExtra(mContext.getString(R.string.bundle_is_showup_notification), true);
        myIntentAlarm.putExtra(mContext.getString(R.string.bundle_provider_name), name);
        PendingIntent pendingIntentAlarm = PendingIntent.getService(mContext, Integer.parseInt(appointment_id), myIntentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, app_time, pendingIntentAlarm);
    }

    public void cancelNotification(String appointment_id)
    {
//        NotificationManager manager = (NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);
//        manager.cancel(Integer.parseInt(appointment_id));
//        manager.cancel(Integer.parseInt(appointment_id+1000));

        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        Intent updateServiceIntent = new Intent(mContext, AppointmentRemindService.class);
        PendingIntent pendingIntentRemind = PendingIntent.getService(mContext, Integer.parseInt(appointment_id+1000), updateServiceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingIntentAlarm = PendingIntent.getService(mContext, Integer.parseInt(appointment_id), updateServiceIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Cancel alarms
        try {
            alarmManager.cancel(pendingIntentRemind);
            alarmManager.cancel(pendingIntentAlarm);
        } catch (Exception e) {
            Log.e(TAG, "AlarmManager update was not canceled. " + e.toString());
        }

    }

    public int generateRandomNumber()
    {
        Random rand = new Random();
        return 1 + rand.nextInt((1000 - 1) + 1);
    }


    public SpannableString setSpanText(String real_text, String span_text, int color)
    {
        SpannableString spannable = new SpannableString(real_text);
        int start = real_text.toLowerCase().indexOf(span_text.toLowerCase());
        if (start == -1) {
            return spannable;
        }
        int end = start + span_text.length();
        spannable.setSpan(new ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannable;
    }

    /**
     * Method will set maximum length
     * @param length
     * @param mTextView
     */
    public void setMaximumLength(int length, TextView mTextView)
    {
        mTextView.setFilters(new InputFilter[] {new InputFilter.LengthFilter(length)});
    }
}
