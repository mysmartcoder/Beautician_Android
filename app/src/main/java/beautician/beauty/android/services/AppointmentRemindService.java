package beautician.beauty.android.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import beautician.beauty.android.R;
import beautician.beauty.android.activities.MainActivity;

/**
 * Created by npatel on 2/4/2016.
 */
public class AppointmentRemindService extends IntentService {

    Context mContext;
    String appointment_id;
    boolean isShowUp = false;
    String mStringMessage = "";
    String mStringTitle = "";
    String mStringName = "";
    String mStringTime = "";
    int notify_id = 0;

    public AppointmentRemindService()
    {
        super("MyAlarmServices");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        mContext = getApplicationContext();

        appointment_id = intent.getStringExtra(getString(R.string.bundle_appointment_id));
        mStringTime = intent.getStringExtra(getString(R.string.bundle_appointment_time));
        mStringName = intent.getStringExtra(getString(R.string.bundle_provider_name));
        isShowUp = intent.getBooleanExtra(getString(R.string.bundle_is_showup_notification), false);

        Intent myIntent = new Intent(mContext, MainActivity.class);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        myIntent.putExtra(mContext.getString(R.string.bundle_from), mContext.getString(R.string.bundle_from_notification));
        myIntent.putExtra(mContext.getString(R.string.bundle_appointment_id), appointment_id);

        if(isShowUp) {
            mStringMessage = "Your Appointment time is started.";
            mStringTitle = "ShowUp Reminder";
            notify_id = Integer.parseInt(appointment_id);
        }
        else {
            mStringMessage = "You have appointment with " + mStringName + " at "+mStringTime;
            mStringTitle = "Appointment Reminder";
            notify_id = Integer.parseInt(appointment_id+1000);
        }

        NotificationManager manager = (NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, Integer.parseInt(appointment_id), myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext.getApplicationContext());
        mBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.icon_notification)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
                .setTicker("beautician Appointment")
                .setContentTitle(mStringTitle)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(mStringMessage))
                .setContentIntent(pendingIntent);


        manager.notify(notify_id, mBuilder.build());
    }
}
