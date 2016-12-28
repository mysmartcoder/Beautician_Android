package beautician.beauty.android.activities;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import beautician.beauty.android.views.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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

public class RegisterActivity extends MyFragmentActivity implements View.OnClickListener {

    private MyFragmentActivity mActivity;
    public MyApplication mApplication;
    public AppAlertDialog mAppAlertDialog;
    public WebMethod mWebMethod;
    private ProgressDialog mProgressDialog;

    private String mCurrentMethod = "";
    private String mMethodRegister = "Register";
    private String mMethodVerifyCode = "VerifyCode";

    private LoginParser mLoginParser;
    private LoginParser mLoginParserVerify;
    private BackProcessRegister mBackProcessRegister;

    private ImageView mImageViewBack;
    private EditText mEditTextMobile;
    private EditText mEditTextEmailID;
    private EditText mEditTextUserName;
    private EditText mEditTextPassword;
    private EditText mEditTextConfirmPassword;
    private TextView mTextViewRegister;
    private CheckBox mCheckBoxAccept;

    private EditText mEditTextVerifyCode;
    private TextView mTextViewVerifyCode;
    private TextView mTextViewPrivacy;
    private TextView mTextViewUsePolicy;

    private String mStringMobile;
    private String mStringUserNames;
    private String mStringEmail;
    private String mStringPassword;
    private String mStringConfirmPassword;

    private String mStringUserID;
    private String mStringVerifyCode;

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    IntentFilter messageFilter;
    private IncomingMessageReceiver mSMSreceiver;
    private IntentFilter mIntentFilter;

    private Dialog mDialogPrivacyPolicy;


    @Override
    public void onBackPressed() {
//        super.onBackPressed();

        Intent mIntent = new Intent(this, LoginActivity.class);
        startActivity(mIntent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        enableStrictMode();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mActivity = this;
        mApplication = (MyApplication) getApplication();
        mAppAlertDialog = new AppAlertDialog(this);
        mWebMethod = new WebMethod(this);
        mLoginParser = new LoginParser();
        mLoginParserVerify = new LoginParser();

        LinearLayout mLinearLayoutParent = (LinearLayout)findViewById(R.id.activity_parent);
        setTouchForHideKeyboard(mLinearLayoutParent);

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
                        .permitDiskWrites()
                        .permitDiskReads()
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
        unregisterReceiver(broadcastReceiver);
        unregisterReceiver(mSMSreceiver);
        super.onDestroy();
    }

    public void registerBroadcast()
    {
        messageFilter = new IntentFilter();
        messageFilter.addAction("RECEIVE_MESSAGE_ACTION_NEW");
        registerReceiver(broadcastReceiver, messageFilter);

        mSMSreceiver = new IncomingMessageReceiver();
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(mSMSreceiver, mIntentFilter);
    }

    /**
     * Method call will get ids from xml file.
     */
    private void getWidgetReferecne() {
        mImageViewBack = (ImageView) findViewById(R.id.activity_register_imageview_back);
        mEditTextMobile = (EditText) findViewById(R.id.activity_register_edittext_user_id);
        mEditTextEmailID = (EditText) findViewById(R.id.activity_register_edittext_email);
        mEditTextUserName = (EditText) findViewById(R.id.activity_register_edittext_username);
        mEditTextPassword = (EditText) findViewById(R.id.activity_register_edittext_password);
        mEditTextConfirmPassword = (EditText) findViewById(R.id.activity_register_edittext_confirm_password);
        mTextViewRegister = (TextView) findViewById(R.id.activity_register_textview_register);
        mCheckBoxAccept = (CheckBox) findViewById(R.id.activity_register_checkbox_accept);

        mEditTextVerifyCode = (EditText) findViewById(R.id.activity_register_edittext_veridy_code);
        mEditTextVerifyCode.setVisibility(View.GONE);
        mTextViewVerifyCode = (TextView) findViewById(R.id.activity_register_textview_verify);
        mTextViewVerifyCode.setVisibility(View.GONE);
        mTextViewPrivacy = (TextView)findViewById(R.id.activity_register_textview_privacy);
        mTextViewUsePolicy = (TextView)findViewById(R.id.activity_register_textview_use);
    }

    /**
     * Method call will set onClick event.
     */
    private void registerClickEvents() {
        mImageViewBack.setOnClickListener(this);
        mTextViewRegister.setOnClickListener(this);
        mTextViewVerifyCode.setOnClickListener(this);
        mTextViewPrivacy.setOnClickListener(this);
        mTextViewUsePolicy.setOnClickListener(this);
        mCheckBoxAccept.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    showPrivacyPolicyDialog();
            }
        });
    }

    @Override
    public void onClick(View v) {

        if (v == mTextViewRegister) {

            callRegister();

        } else if (v == mTextViewVerifyCode) {

            callVerify();

        } else if (v == mImageViewBack) {

            Intent mIntent = new Intent(this, LoginActivity.class);
            startActivity(mIntent);
            finish();

        }else if(v==mTextViewPrivacy){

            showPrivacyPolicyDialog();

        }else if(v==mTextViewUsePolicy){

            showPrivacyPolicyDialog();
        }

    }

    /**
     * Method call will show privacy policy dialog
     */
    private void showPrivacyPolicyDialog() {
        mDialogPrivacyPolicy = new Dialog(mActivity);
        mDialogPrivacyPolicy.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialogPrivacyPolicy.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialogPrivacyPolicy.setContentView(R.layout.dialog_privacy_policy);
        Window window = mDialogPrivacyPolicy.getWindow();
        window.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        WebView mWebView = (WebView)mDialogPrivacyPolicy.findViewById(R.id.dialog_privacy_policy_webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setUserAgentString("");
//        mWebView.setWebChromeClient(new MyWebCromeClient());
//        mWebView.setWebViewClient(new MyWebViewClient());
        mWebView.loadUrl(mWebMethod.mStringURLTerms+mApplication.getCurrentLang());
        if (Build.VERSION.SDK_INT >= 11) {
            mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        final ImageView mTextViewDialogCancel = (ImageView) mDialogPrivacyPolicy.findViewById(R.id.dialog_privacy_policy_imageview_cloase);
        mTextViewDialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialogPrivacyPolicy.cancel();
            }
        });
        mDialogPrivacyPolicy.show();
    }

    /**
     * Method call will check validation of black field.
     */
    public void callVerify() {

        mStringVerifyCode = mEditTextVerifyCode.getText().toString().trim();

        if (getAppAlertDialog().validateBlankField(mEditTextVerifyCode, mActivity, getString(R.string.validation_verify_code))) {
            getAppAlertDialog().HideKeyboard(mEditTextUserName);
            mBackProcessRegister = new BackProcessRegister();
            mBackProcessRegister.execute(mMethodVerifyCode);
        } else {
            getAppAlertDialog().showKeyboard(mEditTextUserName);
        }

    }

    /**
     * Method call will check validation of black field.
     */
    public void callRegister() {

        mStringMobile = mEditTextMobile.getText().toString().trim();
        mStringUserNames = mEditTextUserName.getText().toString().trim();
        mStringEmail = mEditTextEmailID.getText().toString().trim();
        mStringPassword = mEditTextPassword.getText().toString().trim();
        mStringConfirmPassword = mEditTextConfirmPassword.getText().toString().trim();


        if (getAppAlertDialog().validateBlankField(mEditTextMobile, mActivity, getString(R.string.validation_mobile_no))
                && getAppAlertDialog().checkValidEmail(mStringEmail, mActivity, mEditTextEmailID)
                && getAppAlertDialog().validateBlankField(mEditTextUserName, mActivity, getString(R.string.validation_usename))
                && getAppAlertDialog().validateBlankField(mEditTextPassword, mActivity, getString(R.string.validation_password))
                && getAppAlertDialog().validateBlankField(mEditTextConfirmPassword, mActivity, getString(R.string.validation_confirm_password))) {
            if (mStringPassword.equalsIgnoreCase(mStringConfirmPassword)) {
                getAppAlertDialog().HideKeyboard(mEditTextUserName);
                if(!mCheckBoxAccept.isChecked())
                {
                    getAppAlertDialog().showDialog(getString(R.string.app_name), getString(R.string.validation_terms_conidtion), false);
                    return ;
                }

                if(!mStringUserNames.toLowerCase().contains("beautician")) {
                    mBackProcessRegister = new BackProcessRegister();
                    mBackProcessRegister.execute(mMethodRegister);
                }else
                {
                    mActivity.getAppAlertDialog().showDialog(getString(R.string.app_name), getString(R.string.validation_usename_beautician), false);
                }


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
            System.out.println("code--$$" + verificationCode);
            mEditTextVerifyCode.setText(verificationCode);

        }
    };

    /**
     * Method will show forgot password dialog.
     */
    public void showForgotPasswordDialog(String message)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mActivity);
        alertDialogBuilder.setTitle(getString(R.string.app_name));
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton(mActivity.getString(R.string.lbl_yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent mIntent = new Intent(RegisterActivity.this, ForgotPasswordActivity.class);
                mIntent.putExtra(getString(R.string.bundle_from), getString(R.string.bundle_from_register));
                startActivity(mIntent);
                finish();
            }
        });
        alertDialogBuilder.setNegativeButton(mActivity.getString(R.string.lbl_no), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


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
            if (mCurrentMethod.equalsIgnoreCase(mMethodRegister)) {
                mLoginParser = (LoginParser) getWebMethod().callRegister(mStringUserNames, mStringMobile,  mStringEmail, mStringPassword, mLoginParser);
            } else if (mCurrentMethod.equalsIgnoreCase(mMethodVerifyCode)) {
                mLoginParserVerify = (LoginParser) getWebMethod().callVerifyCode(mStringUserID, mStringVerifyCode, mLoginParserVerify);
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
                    if (mCurrentMethod.equalsIgnoreCase(mMethodRegister)) {
                        if (mLoginParser.getWs_status().equalsIgnoreCase("true") && mLoginParser.getData() != null) {

                            mStringUserID = mLoginParser.getData().getUser_id();

                            mEditTextVerifyCode.setVisibility(View.VISIBLE);
                            mTextViewVerifyCode.setVisibility(View.VISIBLE);

                            mActivity.getAppAlertDialog().showDialog("", mLoginParser.getMessage(), false);

                        } else {
                            if(mLoginParser.getMessage().contains("forget") || mLoginParser.getMessage().contains("نسيت"))
                            {
                                showForgotPasswordDialog(mLoginParser.getMessage());
                            }else {
                                mActivity.getAppAlertDialog().showDialog("", mLoginParser.getMessage(), false);
                            }
                        }
                    }

                    if (mCurrentMethod.equalsIgnoreCase(mMethodVerifyCode)) {
                        if (mLoginParserVerify.getWs_status().equalsIgnoreCase("true")) {
                            mEditor.putBoolean(getString(R.string.sp_is_login), true);
                            mEditor.putString(getString(R.string.sp_user_name), mLoginParser.getData().getUsername());
                            mEditor.putString(getString(R.string.sp_user_email), mLoginParser.getData().getEmail());
                            mEditor.putString(getString(R.string.sp_user_mobile), mLoginParser.getData().getUser_phone());
                            mEditor.putString(getString(R.string.sp_user_hash), mLoginParser.getData().getUser_hash());
                            mEditor.putString(getString(R.string.sp_user_pic), mLoginParser.getData().getUser_image());
                            mEditor.putString(getString(R.string.sp_user_lat), mLoginParser.getData().getUser_lat());
                            mEditor.putString(getString(R.string.sp_user_lon), mLoginParser.getData().getUser_lng());
                            mEditor.putString(getString(R.string.sp_user_id), mLoginParser.getData().getUser_id());
                            mEditor.putString(getString(R.string.sp_user_active), mLoginParser.getData().getUser_active());
                            mEditor.putString(getString(R.string.sp_user_location), mLoginParser.getData().getUser_location());

                            mEditor.commit();

                            mStringUserID = mLoginParser.getData().getUser_id();

                            startActivity(new Intent(mActivity, MainActivity.class));
                            finish();

                        } else {
                            mActivity.getAppAlertDialog().showDialog("", mLoginParserVerify.getMessage(), false);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
