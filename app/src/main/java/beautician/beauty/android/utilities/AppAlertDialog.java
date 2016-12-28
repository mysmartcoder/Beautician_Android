package beautician.beauty.android.utilities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import java.util.regex.Pattern;

import beautician.beauty.android.R;

/**
 * Created by npatel on 12/24/2015.
 */
public class AppAlertDialog {



    Activity mActivity;


    String TAG = "ALERT METHOD";

    public AppAlertDialog(Activity activity) {
        mActivity = activity;
    }


    /**
     * Function will hide softkeyboard
     *
     * @param mEditText
     */
    public void HideKeyboard(final View mEditText) {
        final InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
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
        final InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
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
     * Method will display alert dialog
     * @param title
     * @param message
     * @param isFinish
     */
    public void showDialog(String title, String message, final boolean isFinish) {
//        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mActivity);
//        alertDialog.setTitle(title);
//        alertDialog.setCancelable(false);
//        alertDialog.setMessage(message);
//        alertDialog.setPositiveButton(mActivity.getResources().getString(R.string.lbl_ok), new DialogInterface.OnClickListener() {
//
//            @Override
//            public void onClick(DialogInterface arg0, int arg1) {
//                if (isFinish) {
//                    mActivity.onBackPressed();
//                }
//            }
//        });
//
//        alertDialog.show();

        final Dialog mDialogCommon = new Dialog(mActivity);
        mDialogCommon.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialogCommon.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialogCommon.setContentView(R.layout.dialog_common);
        Window window = mDialogCommon.getWindow();
        window.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        mDialogCommon.show();
        final TextView mTextViewTitle = (TextView) mDialogCommon.findViewById(R.id.dialog_common_textview_title);
        mTextViewTitle.setText(title);
        if (title.length() > 0)
            mTextViewTitle.setVisibility(View.VISIBLE);
        else
            mTextViewTitle.setVisibility(View.GONE);
        final TextView mTextViewMessage = (TextView) mDialogCommon.findViewById(R.id.dialog_common_textview_message);
        mTextViewMessage.setText(message);
        final TextView mTextViewButton1 = (TextView) mDialogCommon.findViewById(R.id.dialog_common_textview_button1);
        final TextView mTextViewButton2 = (TextView) mDialogCommon.findViewById(R.id.dialog_common_textview_button2);
        mTextViewButton2.setVisibility(View.GONE);
        mTextViewButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFinish) {
                    mActivity.onBackPressed();
                }
                mDialogCommon.dismiss();
            }
        });
    }

    /**
     * Method call will display Alert Dialog
     * @param title
     * @param msg
     * @param button1Text
     * @param button2Text
     * @param listener
     */
    public void showDeleteAlert(String title, String msg, String button1Text, String button2Text, final InterfaceDialogClickListener listener) {
//        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
//        alertDialogBuilder.setTitle(title);
//        alertDialogBuilder.setMessage(msg);
//        alertDialogBuilder.setPositiveButton(mActivity.getString(R.string.lbl_yes), listener);
//        alertDialogBuilder.setNegativeButton(mActivity.getString(R.string.lbl_cancel), new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int id) {
//
//            }
//        });
//        AlertDialog alertDialog = alertDialogBuilder.create();
//        alertDialog.show();

        final Dialog mDialogCommon = new Dialog(mActivity);
        mDialogCommon.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialogCommon.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialogCommon.setContentView(R.layout.dialog_common);
        mDialogCommon.setCancelable(false);
        Window window = mDialogCommon.getWindow();
        window.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        mDialogCommon.show();
        final TextView mTextViewTitle = (TextView) mDialogCommon.findViewById(R.id.dialog_common_textview_title);
        mTextViewTitle.setText(title);
        if (title.length() > 0)
            mTextViewTitle.setVisibility(View.VISIBLE);
        else
            mTextViewTitle.setVisibility(View.GONE);
        final TextView mTextViewMessage = (TextView) mDialogCommon.findViewById(R.id.dialog_common_textview_message);
        mTextViewMessage.setText(msg);
        final TextView mTextViewButton1 = (TextView) mDialogCommon.findViewById(R.id.dialog_common_textview_button1);
        mTextViewButton1.setText(button1Text);
        final TextView mTextViewButton2 = (TextView) mDialogCommon.findViewById(R.id.dialog_common_textview_button2);
        mTextViewButton2.setText(button2Text);
        mTextViewButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialogCommon.dismiss();
            }
        });
        mTextViewButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialogCommon.dismiss();
                listener.onClick();
            }
        });
    }

    /**
     * Method call will display Alert Dialog
     * @param title
     * @param msg
     * @param listener
     */
    public void showAlertWithSingleButton(String title, String msg, String label, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mActivity);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(msg);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton(label, listener);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    /**
     * Method call when check blank field validation for edittex
     *
     * @param et
     * @param context
     * @param msg
     * @return
     */
    public boolean validateBlankField(final EditText et, final Context context, final String msg) {

        if (et.getText().toString().trim().length() == 0) {
            // CommanMethod.showToast(context, msg);
//            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
//            alertDialogBuilder.setTitle(R.string.app_name);
//            alertDialogBuilder.setMessage(msg).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int id) {
//                    et.requestFocus();
//                    dialog.dismiss();
//                }
//            });
//            AlertDialog alertDialog = alertDialogBuilder.create();
//            alertDialog.show();
            et.requestFocus();
            showDialog("", msg, false);
            return false;
        }
        return true;
    }


    /**
     * Function will check text match email patter or not
     *
     * @param email
     * @return true or false
     */
    public boolean checkValidEmail(String email, final Context context, final EditText mEditText) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        if (pattern.matcher(email).matches()) {
            return pattern.matcher(email).matches();
        } else {
//            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
//            alertDialogBuilder.setTitle(R.string.app_name);
//            alertDialogBuilder.setMessage(context.getString(R.string.validation_email)).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int id) {
//                    mEditText.requestFocus();
//                    dialog.dismiss();
//                }
//            });
//            AlertDialog alertDialog = alertDialogBuilder.create();
//            alertDialog.show();

            mEditText.requestFocus();
            showDialog("", context.getString(R.string.validation_email), false);

            return pattern.matcher(email).matches();
        }
    }


    /**
     * Method call will select field validation check.
     * @param context
     * @param mStringCompare
     * @param msg
     * @return
     */
    public  boolean validateSelectkField(String mString, final Context context,final String mStringCompare, final String msg) {

        if (mString.equalsIgnoreCase(mStringCompare)) {
//            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
//            alertDialogBuilder.setTitle(R.string.app_name);
//            alertDialogBuilder.setMessage(msg).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int id) {
//                    dialog.dismiss();
//                }
//            });
//            AlertDialog alertDialog = alertDialogBuilder.create();
//            alertDialog.show();

            showDialog("", msg, false);
            return false;
        }else {
            return true;
        }
    }

    /**
     * Function to show settings alert dialog On pressing Settings button will
     * lauch Settings Options
     */
    public void showSettingsAlert() {
//        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mActivity);
//
//        alertDialog.setTitle("Location Settings");
//        alertDialog.setMessage("Location is not enabled. Do you want to go to settings menu?");
//        alertDialog.setPositiveButton("Settings",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        Intent intent = new Intent(
//                                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                        mActivity.startActivity(intent);
//                    }
//                });
//
//        alertDialog.setNegativeButton("Cancel",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                    }
//                });
//        alertDialog.show();

        showDeleteAlert("Location Settings", "Location is not enabled. Do you want to go to settings menu?", "Cancel", "Settings", new InterfaceDialogClickListener() {
            @Override
            public void onClick() {
                Intent intent = new Intent(
                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mActivity.startActivity(intent);
            }
        });
    }

}
