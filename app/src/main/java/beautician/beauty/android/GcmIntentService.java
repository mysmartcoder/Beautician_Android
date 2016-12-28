package beautician.beauty.android;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Random;

import beautician.beauty.android.activities.MainActivity;
import beautician.beauty.android.utilities.CommonMethod;
import beautician.beauty.android.utilities.StaticData;


public class GcmIntentService extends IntentService {
    int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    String TAG = "GcmIntentService";

    SharedPreferences mSharedPreferences;
    String mStringUserId = "";
    Editor mEditor;
    CommonMethod commonMethod;
    boolean isLogin = false;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.i(TAG, "come in service");
        mSharedPreferences = getSharedPreferences(getString(R.string.sp_file_name), Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        mStringUserId = mSharedPreferences.getString(getString(R.string.sp_user_id), "");
        isLogin = mSharedPreferences.getBoolean(getString(R.string.sp_is_login), false);

        commonMethod = new CommonMethod(this);

        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) { // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that
			 * GCM will be extended in the future with new message types, just
			 * ignore any message types you're not interested in, or that you
			 * don't recognize.
			 */
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
                    .equals(messageType)) {
                // sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
                    .equals(messageType)) {
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
                    .equals(messageType)) {
                // This loop represents the service doing some work.
                String message = extras.getString("message");
                if (message != null && message.length() > 0) {
                    Log.i(TAG, "FULL GCM MESSAGE: " + message);
                    readMessage(message);
                }

                // Log.i(TAG, "Received: " + extras.toString());
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GCMBroadcastReceiver.completeWakefulIntent(intent);
    }

    public void readMessage(String json_message) {
        String notify_type = "";
        String user_id = "";
        String appointment_id = "";
        String from_name = "";
        String appointment_time = "";
        String message_content = "";

        JSONArray mJsonArray;
        try {
            //mJsonArray = new JSONArray(json_message);
            //JSONObject mJsonObject = (JSONObject) mJsonArray.get(0);
            JSONObject jresponse = new JSONObject(json_message);
            user_id = jresponse.getString("user_id");

            if (mStringUserId.equalsIgnoreCase(user_id) && isLogin) {

                notify_type = jresponse.getString("notify_type");
                appointment_id = jresponse.getString("appointment_id");
                appointment_time = jresponse.getString("appointment_time");
                from_name = jresponse.getString("from_user_name");
                message_content = jresponse.getString("message_content");

                Intent myIntent = new Intent(this, MainActivity.class);
                myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                myIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                myIntent.putExtra(getString(R.string.bundle_from), getString(R.string.bundle_from_notification));
                myIntent.putExtra(getString(R.string.bundle_appointment_id), appointment_id);

                if (notify_type.equalsIgnoreCase("new_appointment")) {

                    NOTIFICATION_ID = Integer.parseInt(appointment_id);
                    sendNotification("New Appointment", message_content, myIntent, R.drawable.ic_launcher);
                    commonMethod.setNotificationForAppointment(appointment_id, appointment_time, from_name);
                }
                else if (notify_type.equalsIgnoreCase("cancel_appointment")) {

                    NOTIFICATION_ID = Integer.parseInt(appointment_id);
                    sendNotification("Cancel Appointment", message_content, myIntent, R.drawable.ic_launcher);
                    commonMethod.cancelNotification(appointment_id);
                    Intent intent = new Intent(StaticData.ACTION_CANCEL_APPOINTMENT);
                    intent.putExtra(getString(R.string.bundle_appointment_id), appointment_id);
                    sendBroadcast(intent);

                } else if (notify_type.equalsIgnoreCase("remind_appointment")) {

                    NOTIFICATION_ID = Integer.parseInt(appointment_id + 1000);
                    sendNotification("Appointment Reminder", message_content, myIntent, R.drawable.ic_launcher);
                }
                else if (notify_type.equalsIgnoreCase("showup_appointment")) {

                    NOTIFICATION_ID = Integer.parseInt(appointment_id);
                    sendNotification("ShowUp Reminder", message_content, myIntent, R.drawable.ic_launcher);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String title, String msg, Intent mIntent, int icon) {
        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);


        PendingIntent contentIntent = PendingIntent.getActivity(this, NOTIFICATION_ID, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.icon_notification)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
                .setTicker(title)
                .setContentTitle(title)
                .setStyle(new android.support.v7.app.NotificationCompat.BigTextStyle().bigText(msg))
                .setContentIntent(contentIntent);


        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        mEditor.commit();
    }

    public boolean isRunning(Context ctx) {
        ActivityManager activityManager = (ActivityManager) ctx
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> tasks = activityManager
                .getRunningTasks(Integer.MAX_VALUE);

        if (ctx.getPackageName().equalsIgnoreCase(
                tasks.get(0).baseActivity.getPackageName()))
            return true;
        // }
        return false;
    }

    public int getRandomNumber()
    {
        Random ran = new Random();
        int x = ran.nextInt(100-1 + 1) + 1;
        return  x;
    }
}

