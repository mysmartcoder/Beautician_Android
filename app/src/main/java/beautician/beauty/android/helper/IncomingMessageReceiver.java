package beautician.beauty.android.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

public class IncomingMessageReceiver extends BroadcastReceiver {

	// Get the object of SmsManager
	final SmsManager sms = SmsManager.getDefault();
	private String senderNum;
	private String message;

	@SuppressWarnings("unused")
	public void onReceive(Context context, Intent intent) {
		// this.context = context.getApplicationContext();
		// Retrieves a map of extended data from the intent.
		Bundle bundle = intent.getExtras();

		try {

			if (bundle != null) {

				final Object[] pdusObj = (Object[]) bundle.get("pdus");

				for (int i = 0; i < pdusObj.length; i++) {

					SmsMessage currentMessage = SmsMessage
							.createFromPdu((byte[]) pdusObj[i]);
					String phoneNumber = currentMessage
							.getDisplayOriginatingAddress();

					senderNum = phoneNumber;
					message = currentMessage.getDisplayMessageBody();

					Log.i("SmsReceiver", "senderNum: " + senderNum
							+ "; message: " + message.toString() + "---"
							+ currentMessage.getOriginatingAddress());

					break;

				} // end for loop
				if (message != null && message.trim().contains("beautician")) {
					Log.i("SmsReceiver", " message: " + message.toString()+ "---");
					Intent intent2 = new Intent();
					intent2.setAction("RECEIVE_MESSAGE_ACTION_NEW");
					intent2.putExtra("senderNum", senderNum);
					intent2.putExtra("verificationCode",
							ReturnValidationcode(message));
					context.sendBroadcast(intent2);
				}
			} // bundle is null

		} catch (Exception e) {
			e.printStackTrace();
			Log.e("SmsReceiver", "Exception smsReceiver" + e);

		}
	}

	public String ReturnValidationcode(String Message) {
		String strt = " verification code is ";
		int startIndex = Message.indexOf(strt);
		int endIndex = Message.indexOf(". Happy");
		String val = Message.substring(startIndex + strt.length(), endIndex);
		System.out.println("code--" + val);
		// String ReplaceString = Message.replace("4U", "");
//		Log.i("ReplaceSting", "ReplaceString=" + val);
		// numberOnly = Message.replaceAll("[^0-9]", "");
		// numberOnly = numberOnly.substring(1, numberOnly.length() - 1);
		//System.out.println(val);
//		Log.i("ReplaceSting", "numberOnly=" + val);

		return val.trim();
	}
}