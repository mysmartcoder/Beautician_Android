package beautician.beauty.android.activities;

import beautician.beauty.android.views.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import beautician.beauty.android.MyApplication;
import beautician.beauty.android.R;
import beautician.beauty.android.helper.IncomingMessageReceiver;
import beautician.beauty.android.parsers.LoginParser;
import beautician.beauty.android.utilities.AppAlertDialog;
import beautician.beauty.android.utilities.WebMethod;

public class ForgotPasswordActivity extends MyFragmentActivity implements View.OnClickListener {

    private MyFragmentActivity mActivity;
    public MyApplication mApplication;
    public AppAlertDialog mAppAlertDialog;
    public WebMethod mWebMethod;
    private ProgressDialog mProgressDialog;

    private String mCurrentMethod = "";
    private String mMethodReguestCode = "RequestCode";
    private String mMethodChangePassword = "ChangePassword";

    private LoginParser mLoginParserRequestCode;
    private LoginParser mLoginParserChangePassword;
    private BackProcessRegister mBackProcessRegister;

    private ImageView mImageViewBack;
    private EditText mEditTextUserName;
    private EditText mEditTextPassword;
    private EditText mEditTextConfirmPassword;
    private TextView mTextViewReguestCode;

    private EditText mEditTextVerifyCode;
    private TextView mTextViewSave;

    private String mStringEmailOrMobile;

    private String mStringUserID;
    private String mStringPassword;
    private String mStringConfirmPassword;
    private String mStringVerifyCode;

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    IntentFilter messageFilter;
    private IncomingMessageReceiver mSMSreceiver;
    private IntentFilter mIntentFilter;

    private String mStringFrom = "";


    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        Intent mIntent = null;
        if (mStringFrom.equalsIgnoreCase(getString(R.string.bundle_from_login))) {
            mIntent = new Intent(this, LoginActivity.class);
        } else {
            mIntent = new Intent(this, RegisterActivity.class);
        }
        startActivity(mIntent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        enableStrictMode();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mActivity = this;
        mApplication = (MyApplication) getApplication();
        mAppAlertDialog = new AppAlertDialog(this);
        mWebMethod = new WebMethod(this);
        mLoginParserRequestCode = new LoginParser();
        mLoginParserChangePassword = new LoginParser();

        LinearLayout mLinearLayoutParent = (LinearLayout)findViewById(R.id.activity_parent);
        setTouchForHideKeyboard(mLinearLayoutParent);

        mStringFrom = getIntent().getExtras().getString(getString(R.string.bundle_from));

        mSharedPreferences = getSharedPreferences(getString(R.string.sp_file_name), Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();

        getWidgetReferecne();
        registerClickEvents();

        registerBroadcast();

    }


    public void enableStrictMode() {
        StrictMode.setThreadPolicy(
                new StrictMode.ThreadPolicy.Builder()
                        .detectDiskReads()
                        .detectDiskWrites()
                        .detectNetwork()
                        .penaltyLog()
                        .build());
        StrictMode.setVmPolicy(
                new StrictMode.VmPolicy.Builder()
                        .detectLeakedSqlLiteObjects()
                        .penaltyLog()
                        .build());
    }

    @Override
    public void onResume() {


        super.onResume();
    }


    @Override
    protected void onDestroy() {
        mActivity.unregisterReceiver(broadcastReceiver);
        mActivity.unregisterReceiver(mSMSreceiver);
        super.onDestroy();
    }

    public void registerBroadcast() {
        messageFilter = new IntentFilter();
        messageFilter.addAction("RECEIVE_MESSAGE_ACTION_NEW");
        mActivity.registerReceiver(broadcastReceiver, messageFilter);

        mSMSreceiver = new IncomingMessageReceiver();
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        mActivity.registerReceiver(mSMSreceiver, mIntentFilter);
    }

    /**
     * Method call will get ids from xml file.
     */
    private void getWidgetReferecne() {
        mImageViewBack = (ImageView) findViewById(R.id.activity_forgot_password_imageview_back);
        mEditTextUserName = (EditText) findViewById(R.id.activity_forgot_password_edittext_user_id);
        mEditTextPassword = (EditText) findViewById(R.id.activity_forgot_password_edittext_password);
        mEditTextPassword.setVisibility(View.GONE);
        mEditTextConfirmPassword = (EditText) findViewById(R.id.activity_forgot_password_edittext_confirm_password);
        mEditTextConfirmPassword.setVisibility(View.GONE);
        mTextViewReguestCode = (TextView) findViewById(R.id.activity_forgot_password_textview_register);

        mEditTextVerifyCode = (EditText) findViewById(R.id.activity_forgot_password_edittext_veridy_code);
        mEditTextVerifyCode.setVisibility(View.GONE);
        mTextViewSave = (TextView) findViewById(R.id.activity_forgot_password_textview_save);
        mTextViewSave.setVisibility(View.GONE);
    }

    /**
     * Method call will set onClick event.
     */
    private void registerClickEvents() {
        mImageViewBack.setOnClickListener(this);
        mTextViewReguestCode.setOnClickListener(this);
        mTextViewSave.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (v == mTextViewReguestCode) {

            callRequestVerifyCode();

        } else if (v == mTextViewSave) {

            callChangePassword();

        } else if (v == mImageViewBack) {

            Intent mIntent = null;
            if (mStringFrom.equalsIgnoreCase(getString(R.string.bundle_from_login))) {
                mIntent = new Intent(this, LoginActivity.class);
            } else {
                mIntent = new Intent(this, RegisterActivity.class);
            }
            startActivity(mIntent);
            finish();
        }

    }

    /**
     * Method call will check validation of black field.
     */
    public void callRequestVerifyCode() {

        mStringEmailOrMobile = mEditTextUserName.getText().toString().trim();

        if (getAppAlertDialog().validateBlankField(mEditTextUserName, mActivity, getString(R.string.validation_userid))) {
            getAppAlertDialog().HideKeyboard(mEditTextUserName);
            mBackProcessRegister = new BackProcessRegister();
            mBackProcessRegister.execute(mMethodReguestCode);
        } else {
            getAppAlertDialog().showKeyboard(mEditTextUserName);
        }

    }

    /**
     * Method call will check validation of black field.
     */
    public void callChangePassword() {

        mStringVerifyCode = mEditTextVerifyCode.getText().toString().trim();
        mStringPassword = mEditTextPassword.getText().toString().trim();
        mStringConfirmPassword = mEditTextConfirmPassword.getText().toString().trim();


        if (getAppAlertDialog().validateBlankField(mEditTextVerifyCode, mActivity, getString(R.string.validation_verify_code))
                && getAppAlertDialog().validateBlankField(mEditTextPassword, mActivity, getString(R.string.validation_password))
                && getAppAlertDialog().validateBlankField(mEditTextConfirmPassword, mActivity, getString(R.string.validation_confirm_password))) {
            if (mStringPassword.equalsIgnoreCase(mStringConfirmPassword)) {
                getAppAlertDialog().HideKeyboard(mEditTextUserName);
                mBackProcessRegister = new BackProcessRegister();
                mBackProcessRegister.execute(mMethodChangePassword);
            } else {
                getAppAlertDialog().showDialog(getString(R.string.app_name), getString(R.string.validation_confirm_password_not_match), false);
            }
        } else {
            getAppAlertDialog().showKeyboard(mEditTextUserName);
        }

    }

    public BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String broadcastMessage = intent.getExtras().getString("senderNum");
            String verificationCode = intent.getExtras().getString("verificationCode");

            mEditTextVerifyCode.setText(verificationCode);

        }
    };


    @Override
    public void replaceFragment(Fragment mFragment, boolean addBackStack) {


    }


    @Override
    public void setHeaderTitle(int title) {
    }

    @Override
    public void setAddVisible(int visible) {

    }

    @Override
    public void setLogoutVisible(int visible) {

    }

    @Override
    public void onBackButton(View.OnClickListener mClickListener) {

    }

    @Override
    public MyApplication getMyApplication() {
        return mApplication;
    }



    @Override
    public AppAlertDialog getAppAlertDialog() {
        return mAppAlertDialog;
    }

    @Override
    public WebMethod getWebMethod() {
        return mWebMethod;
    }

    /**
     * AsyncTask for calling webservice in background.
     *
     * @author ebaraiya
     */
    public class BackProcessRegister extends AsyncTask<String, Void, String> {
        String responseData = "";

        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(mActivity, "", getString(R.string.dialog_loading), true);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            mCurrentMethod = params[0];
            if (mCurrentMethod.equalsIgnoreCase(mMethodReguestCode)) {
                mLoginParserRequestCode = (LoginParser) getWebMethod().callRequestVerifyCodeForPassword(mStringEmailOrMobile, mLoginParserRequestCode);
            } else if (mCurrentMethod.equalsIgnoreCase(mMethodChangePassword)) {
                mLoginParserChangePassword = (LoginParser) getWebMethod().callChangePassword(mStringUserID, mStringVerifyCode, mStringPassword, mLoginParserChangePassword);
            }

            return null;

        }

        @Override
        protected void onPostExecute(String result) {

            if (mProgressDialog != null)
                mProgressDialog.dismiss();

            if (mActivity.getWebMethod().isNetError) {
                mActivity.getAppAlertDialog().showDialog("", getString(R.string.validation_no_internet), false);
            } else if (mActivity.getWebMethod().isError) {
                mActivity.getAppAlertDialog().showDialog("", getString(R.string.validation_failed), false);
            } else {
                try {
                    if (mCurrentMethod.equalsIgnoreCase(mMethodReguestCode)) {
                        if (mLoginParserRequestCode.getWs_status().equalsIgnoreCase("true")
                                && mLoginParserRequestCode.getData()!=null) {

                            mStringUserID = mLoginParserRequestCode.getData().getUser_id();

                            mEditTextVerifyCode.setVisibility(View.VISIBLE);
                            mTextViewSave.setVisibility(View.VISIBLE);
                            mEditTextPassword.setVisibility(View.VISIBLE);
                            mEditTextConfirmPassword.setVisibility(View.VISIBLE);

                            mActivity.getAppAlertDialog().showDialog("", mLoginParserRequestCode.getMessage(), false);

                        } else {
                            mActivity.getAppAlertDialog().showDialog("", mLoginParserRequestCode.getMessage(), false);
                        }
                    }

                    if (mCurrentMethod.equalsIgnoreCase(mMethodChangePassword)) {
                        if (mLoginParserChangePassword.getWs_status().equalsIgnoreCase("true")
                                && mLoginParserChangePassword.getData()!=null) {
                            mEditor.putBoolean(getString(R.string.sp_is_login), true);
                            mEditor.putString(getString(R.string.sp_user_name), mLoginParserChangePassword.getData().getUsername());
                            mEditor.putString(getString(R.string.sp_user_email), mLoginParserChangePassword.getData().getEmail());
                            mEditor.putString(getString(R.string.sp_user_hash), mLoginParserChangePassword.getData().getUser_hash());
                            mEditor.putString(getString(R.string.sp_user_pic), mLoginParserChangePassword.getData().getUser_image());
                            mEditor.putString(getString(R.string.sp_user_lat), mLoginParserChangePassword.getData().getUser_lat());
                            mEditor.putString(getString(R.string.sp_user_lon), mLoginParserChangePassword.getData().getUser_lng());
                            mEditor.putString(getString(R.string.sp_user_id), mLoginParserChangePassword.getData().getUser_id());
                            mEditor.putString(getString(R.string.sp_user_active), mLoginParserChangePassword.getData().getUser_active());
                            mEditor.putString(getString(R.string.sp_user_location), mLoginParserChangePassword.getData().getUser_location());

                            mEditor.commit();

                            startActivity(new Intent(mActivity, MainActivity.class));
                            finish();

                        } else {
                            mActivity.getAppAlertDialog().showDialog("", mLoginParserChangePassword.getMessage(), false);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
