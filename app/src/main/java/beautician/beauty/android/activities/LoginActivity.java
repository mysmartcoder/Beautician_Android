package beautician.beauty.android.activities;

import android.Manifest;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;

import beautician.beauty.android.views.ChangeLanguageDialogFragment;
import beautician.beauty.android.views.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.share.ShareApi;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import beautician.beauty.android.MyApplication;
import beautician.beauty.android.R;
import beautician.beauty.android.helper.FacebookHelper;
import beautician.beauty.android.helper.TwitterHelper;
import beautician.beauty.android.helper.instagram.ApplicationData;
import beautician.beauty.android.helper.instagram.InstagramApp;
import beautician.beauty.android.parsers.LoginParser;
import beautician.beauty.android.utilities.AppAlertDialog;
import beautician.beauty.android.utilities.WebMethod;


public class LoginActivity extends MyFragmentActivity implements OnClickListener, FacebookHelper.OnFbSignInListener, TwitterHelper.OnTwitterSignInListener {

    private MyFragmentActivity mActivity;
    public MyApplication mApplication;
    public AppAlertDialog mAppAlertDialog;
    public WebMethod mWebMethod;
    private Dialog mDialogSocialLogin;

    private SharedPreferences mSharedPreferences;
    private Editor mEditor;

    private String mMethodLogin = "Login";
    private String mMethodSocialLogin = "SocialLogin";
    private String mMethodSignUpBySocial = "SignUpBySocial";
    private LoginParser mLoginParser;

    private String mStringFacebook = "facebook";
    private String mStringTwitter = "twitter";
    private String mStringInstagram = "instagram";
    private String mStringSocialID = "0";
    private String mStringSocialType = "type";

    private BackProcessLogin mBackProcessLogin;
    private ProgressDialog mProgressDialog;

    private EditText mEditTextUserName;
    private EditText mEditTextPassword;
    private TextView mTextViewLogin;
    private TextView mTextViewFacebook;
    private TextView mTextViewTwitter;
    private TextView mTextViewInstagram;
    private TextView mTextViewRegister;
    private TextView mTextViewForgotPassword;

    private FacebookHelper mFacebookHelper;
    private TwitterHelper mTwitterHelper;
    private InstagramApp mInstagramApp;

    private String mStringUserID = "";
    private String mStringPassword = "";
    private String mStringConfirmPassword = "";
    private String mStringUsername = "";
    private String mStringEmail = "";
    private String mStringUserPicPath = "";

    int mCurrentLangPosition = -1;

    private HashMap<String, String> userInfoHashmap = new HashMap<String, String>();
    private Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == InstagramApp.WHAT_FINALIZE) {
                userInfoHashmap = mInstagramApp.getUserInfo();
                //Toast.makeText(LoginActivity.this, "Call handler", Toast.LENGTH_SHORT).show();
            } else if (msg.what == InstagramApp.WHAT_FINALIZE) {
                // Toast.makeText(LoginActivity.this, "Check your network.", Toast.LENGTH_SHORT).show();
            }
            return false;
        }
    });


    public void enableStrictMode() {
        StrictMode.setThreadPolicy(
                new StrictMode.ThreadPolicy.Builder()
                        .detectDiskReads()
                        .detectDiskWrites()
                        .permitDiskReads()
                        .permitDiskWrites()
                        .detectNetwork()
                        .penaltyLog()
                        .build());
        StrictMode.setVmPolicy(
                new StrictMode.VmPolicy.Builder()
                        .detectLeakedSqlLiteObjects()
                        .penaltyLog()
                        .build());
    }

    public void onCreate(Bundle savedInstanceState) {

        enableStrictMode();
        super.onCreate(savedInstanceState);

        mTwitterHelper = new TwitterHelper(this, this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        mSharedPreferences = getSharedPreferences(getString(R.string.sp_file_name), Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();

        mActivity = this;
        mApplication = (MyApplication) getApplication();
        mAppAlertDialog = new AppAlertDialog(this);
        mWebMethod = new WebMethod(this);
        mLoginParser = new LoginParser();

        LinearLayout mLinearLayoutParent = (LinearLayout)findViewById(R.id.activity_parent);
        setTouchForHideKeyboard(mLinearLayoutParent);

        mInstagramApp = new InstagramApp(this, ApplicationData.CLIENT_ID, ApplicationData.CLIENT_SECRET, ApplicationData.CALLBACK_URL);
        mInstagramApp.setListener(new InstagramApp.OAuthAuthenticationListener() {

            @Override
            public void onSuccess() {

                mInstagramApp.fetchUserName(handler);
                userInfoHashmap = mInstagramApp.getUserInfo();
                mStringUsername = userInfoHashmap.get(InstagramApp.TAG_USERNAME);
                mStringUserPicPath = userInfoHashmap.get(InstagramApp.TAG_PROFILE_PICTURE);
                mStringSocialID = userInfoHashmap.get(InstagramApp.TAG_ID);
                mStringSocialType = mStringInstagram;

                mBackProcessLogin = new BackProcessLogin();
                mBackProcessLogin.execute(mMethodSocialLogin);

            }

            @Override
            public void onFail(String error) {
            }
        });

        getWidgetRefrence();
        registerOnClick();

        if (mInstagramApp.hasAccessToken()) {
            mInstagramApp.fetchUserName(handler);
        }
        mFacebookHelper = new FacebookHelper(this, this);


        if(mSharedPreferences.getBoolean(getString(R.string.sp_has_lang_set), false)==false)
        {
//            showLanguageDialog();
            ChangeLanguageDialogFragment mChangeLanguageDialogFragment = new ChangeLanguageDialogFragment();
            mChangeLanguageDialogFragment.show(mActivity.getSupportFragmentManager(), "");
        }

    }

    /**
     * Method will call get widget id from xml.
     */
    public void getWidgetRefrence() {

        mEditTextUserName = (EditText) findViewById(R.id.activity_login_edittext_username);
        mEditTextPassword = (EditText) findViewById(R.id.activity_login_edittext_password);
        mTextViewLogin = (TextView) findViewById(R.id.activity_login_textview_login);
        mTextViewFacebook = (TextView) findViewById(R.id.activity_login_textview_facebook);
        mTextViewTwitter = (TextView) findViewById(R.id.activity_login_textview_twitter);
        mTextViewInstagram = (TextView) findViewById(R.id.activity_login_textview_instagram);
        mTextViewRegister = (TextView) findViewById(R.id.activity_login_textview_register);
        mTextViewForgotPassword = (TextView) findViewById(R.id.activity_login_textview_forgot_password);
    }

    /**
     * Method will call register onClick() Events.
     */
    public void registerOnClick() {

        mTextViewLogin.setOnClickListener(this);
        mTextViewFacebook.setOnClickListener(this);
        mTextViewTwitter.setOnClickListener(this);
        mTextViewInstagram.setOnClickListener(this);
        mTextViewRegister.setOnClickListener(this);
        mTextViewForgotPassword.setOnClickListener(this);

    }

    /**
     * Method will change app lang
     */
    public void showLanguageDialog() {

        mCurrentLangPosition=-1;

        if (getMyApplication().getCurrentLang().equalsIgnoreCase("en"))
            mCurrentLangPosition = 0;
        else
            mCurrentLangPosition = 1;

        final CharSequence[] choice = { "English", "Arabic" };
        AlertDialog.Builder alert = new AlertDialog.Builder(mActivity);
        alert.setTitle(R.string.app_name);
        alert.setSingleChoiceItems(choice, mCurrentLangPosition, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (choice[which] == "English") {
                    mActivity.getMyApplication().changeLanguage("en");
                } else if (choice[which] == "Arabic") {
                    mActivity.getMyApplication().changeLanguage("ar");
                }
                dialog.cancel();
                if (mCurrentLangPosition != which) {
                    Intent intent = mActivity.getIntent();
                    mActivity.finish();
                    startActivity(intent);
                }
            }
        });
//        alert.setPositiveButton(R.string.lbl_cancel, new DialogInterface.OnClickListener() {
//
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//            }
//        });
        alert.show();
    }


    /**
     * Method will call fire onClick() Events.
     */
    @Override
    public void onClick(View v) {


        if (v == mTextViewLogin) {

            callLogin();

        } else if (v == mTextViewFacebook) {

            if(AccessToken.getCurrentAccessToken()!=null) {
                if (AccessToken.getCurrentAccessToken().isExpired()) {
                    mFacebookHelper.connect();
                }
                else {
                    Profile mProfile = Profile.getCurrentProfile();
                    mStringSocialID = mProfile.getId();
                    mStringUserPicPath = "http://graph.facebook.com/" + mStringSocialID + "/picture?type=large";
                    mStringSocialType = mStringFacebook;
                    mStringEmail = "";
                    mStringUsername = mProfile.getName();
                    mBackProcessLogin = new BackProcessLogin();
                    mBackProcessLogin.execute(mMethodSocialLogin);

                }
            }else
            {
                mFacebookHelper.connect();
            }


        } else if (v == mTextViewTwitter) {

            mTwitterHelper.connect();

        } else if (v == mTextViewInstagram) {
            //onTwitterConnection();
            mInstagramApp.authorize();

        } else if (v == mTextViewRegister) {

            int permissionCheck = ContextCompat.checkSelfPermission(mActivity, Manifest.permission.RECEIVE_SMS);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                Intent mIntent = new Intent(this, RegisterActivity.class);
                startActivity(mIntent);
                finish();
            }else
            {
                ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.RECEIVE_SMS}, 123);
            }


        } else if (v == mTextViewForgotPassword) {

            int permissionCheck = ContextCompat.checkSelfPermission(mActivity, Manifest.permission.RECEIVE_SMS);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                Intent mIntent = new Intent(this, ForgotPasswordActivity.class);
                mIntent.putExtra(getString(R.string.bundle_from), getString(R.string.bundle_from_login));
                startActivity(mIntent);
                finish();
            }else
            {
                ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.RECEIVE_SMS}, 123);
            }


        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mFacebookHelper.onActivityResult(requestCode, resultCode, data);
        mTwitterHelper.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * Method call will check validation.
     */
    public void callLogin() {

        if (getAppAlertDialog().validateBlankField(mEditTextUserName, mActivity, getString(R.string.validation_userid))
                && getAppAlertDialog().validateBlankField(mEditTextPassword, mActivity, getString(R.string.validation_password))) {
            getAppAlertDialog().HideKeyboard(mEditTextUserName);

            mStringUserID = mEditTextUserName.getText().toString().trim();
            mStringPassword = mEditTextPassword.getText().toString().trim();

            mBackProcessLogin = new BackProcessLogin();
            mBackProcessLogin.execute(mMethodLogin);
        } else {
            getAppAlertDialog().showKeyboard(mEditTextUserName);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /**
     * Method call will login with Facebook and get user information.
     *
     * @param graphResponse
     * @param error
     */
    @Override
    public void OnFbSignInComplete(GraphResponse graphResponse, String error) {
        if (error == null) {
            try {
                Toast.makeText(mActivity, "FB Login..", Toast.LENGTH_SHORT).show();
                JSONObject jsonObject = graphResponse.getJSONObject();
                mStringSocialID = jsonObject.getString("id");
                mStringUserPicPath = "http://graph.facebook.com/" + mStringSocialID + "/picture?type=large";
                mStringSocialType = mStringFacebook;
                mStringEmail = jsonObject.getString("email");
                mStringUsername = jsonObject.getString("name");
                mBackProcessLogin = new BackProcessLogin();
                mBackProcessLogin.execute(mMethodSocialLogin);
//                LoginManager.getInstance().logOut();

//                sharePhotoToFacebook();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Method call will login with Twitter and get user information
     *
     * @param userDetails
     * @param error
     */
    @Override
    public void OnTwitterSignInComplete(TwitterHelper.UserDetails userDetails, String error) {
        if (userDetails != null) {
            Twitter.getApiClient().getAccountService().verifyCredentials(true, false, new Callback<User>() {
                @Override
                public void success(Result<User> userResult) {
                    User user = userResult.data;
                    if (user.email != null) {
                        mStringEmail = user.email;
                    }
                    mStringUsername = user.name;
                    mStringUserPicPath = user.profileImageUrl.replace("_normal", "");
                    mStringSocialID = String.valueOf(user.getId());
                    mStringSocialType = mStringTwitter;

                    mBackProcessLogin = new BackProcessLogin();
                    mBackProcessLogin.execute(mMethodSocialLogin);
                }

                @Override
                public void failure(TwitterException e) {
                }
            });
        }

    }

    @Override
    public void OnTweetPostComplete(Result<Tweet> result, String error) {

    }

    /**
     * Method call will show signup by social dialog
     */
    private void showLoginWithSocialDialog() {
        mDialogSocialLogin = new Dialog(mActivity);
        mDialogSocialLogin.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialogSocialLogin.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialogSocialLogin.setContentView(R.layout.dialog_social_login);
        Window window = mDialogSocialLogin.getWindow();
        window.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        final EditText mEditTextDialogUserID = (EditText) mDialogSocialLogin.findViewById(R.id.dialog_social_login_edittext_user_id);
        final EditText mEditTextDialogPassword = (EditText) mDialogSocialLogin.findViewById(R.id.dialog_social_login_edittext_password);
        final EditText mEditTextDialogConfirmPassword = (EditText) mDialogSocialLogin.findViewById(R.id.dialog_social_login_edittext_confirm_password);
        final TextView mTextViewDialogSubmit = (TextView) mDialogSocialLogin.findViewById(R.id.dialog_social_login_textview_register);

        mTextViewDialogSubmit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                mStringEmail = mEditTextDialogUserID.getText().toString().trim();
                mStringPassword = mEditTextDialogPassword.getText().toString().trim();
                mStringConfirmPassword = mEditTextDialogConfirmPassword.getText().toString().trim();

                if (getAppAlertDialog().validateBlankField(mEditTextDialogUserID, mActivity, getString(R.string.validation_userid))
                        && getAppAlertDialog().validateBlankField(mEditTextDialogPassword, mActivity, getString(R.string.validation_password))
                        && getAppAlertDialog().validateBlankField(mEditTextDialogConfirmPassword, mActivity, getString(R.string.validation_confirm_password))) {
                    if (mStringPassword.equalsIgnoreCase(mStringConfirmPassword)) {

                        getAppAlertDialog().HideKeyboard(mEditTextUserName);
                        mBackProcessLogin = new BackProcessLogin();
                        mBackProcessLogin.execute(mMethodSignUpBySocial);

                    } else {
                        getAppAlertDialog().showDialog(getString(R.string.app_name), getString(R.string.validation_confirm_password_not_match), false);
                    }
                } else {
                    getAppAlertDialog().showKeyboard(mEditTextUserName);
                }
            }
        });

        mDialogSocialLogin.show();
    }

    /**
     * AsyncTask for calling webservice in background.
     *
     * @author ebaraiya
     */
    public class BackProcessLogin extends AsyncTask<String, Void, String> {
        String responseData = "";
        String mCurrentMethod = "";

        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(mActivity, "", getString(R.string.dialog_loading), true);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            mCurrentMethod = params[0];
            if (mCurrentMethod.equalsIgnoreCase(mMethodLogin)) {
                mLoginParser = (LoginParser) getWebMethod().callLogin(mStringUserID, mStringPassword, mLoginParser);
            } else if (mCurrentMethod.equalsIgnoreCase(mMethodSocialLogin)) {

                mLoginParser = (LoginParser) getWebMethod().callSignInBySocial(mStringSocialID, mStringSocialType, mLoginParser);

            } else if (mCurrentMethod.equalsIgnoreCase(mMethodSignUpBySocial)) {

                mLoginParser = (LoginParser) getWebMethod().callSignUpBySocial(mStringSocialID, mStringSocialType, mStringUsername, mStringEmail, mStringPassword, mStringUserPicPath, mLoginParser);

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
                    if (mCurrentMethod.equalsIgnoreCase(mMethodLogin) || mCurrentMethod.equalsIgnoreCase(mMethodSocialLogin)
                            || mCurrentMethod.equalsIgnoreCase(mMethodSignUpBySocial)) {
                        if (mLoginParser.getWs_status().equalsIgnoreCase("true") && mLoginParser.getData() != null) {
                            mEditor.putBoolean(getString(R.string.sp_is_login), true);
                            mEditor.putString(getString(R.string.sp_user_name), mLoginParser.getData().getUsername());
                            mEditor.putString(getString(R.string.sp_user_email), mLoginParser.getData().getEmail());
                            mEditor.putString(getString(R.string.sp_user_mobile), mLoginParser.getData().getUser_phone());
                            mEditor.putString(getString(R.string.sp_user_hash), mLoginParser.getData().getUser_hash());
                            mEditor.putString(getString(R.string.sp_user_pic), mLoginParser.getData().getUser_image());
                            mEditor.putString(getString(R.string.sp_user_lat), mLoginParser.getData().getUser_lat());
                            mEditor.putString(getString(R.string.sp_user_lon), mLoginParser.getData().getUser_lng());
                            mEditor.putString(getString(R.string.sp_user_id), mLoginParser.getData().getUser_id());
                            mEditor.putString(getString(R.string.sp_user_location), mLoginParser.getData().getUser_location());
                            mEditor.putString(getString(R.string.sp_satisfy_rating), mLoginParser.getData().getTotalsatisfy());
                            mEditor.putString(getString(R.string.sp_committed_rating), mLoginParser.getData().getTotalcommited());
                            mEditor.putString(getString(R.string.sp_user_active), mLoginParser.getData().getUser_active());

                            mEditor.commit();

                            startActivity(new Intent(mActivity, MainActivity.class));
                            finish();
                        } else {
                            if (mCurrentMethod.equalsIgnoreCase(mMethodLogin))
                                mActivity.getAppAlertDialog().showDialog("", mLoginParser.getMessage(), false);
                            else if (mCurrentMethod.equalsIgnoreCase(mMethodSocialLogin)) {
                                if (mStringEmail != null && mStringEmail.length() > 0) {
                                    mBackProcessLogin = new BackProcessLogin();
                                    mBackProcessLogin.execute(mMethodSignUpBySocial);

                                } else {
                                    showLoginWithSocialDialog();
                                }
                            } else if (mCurrentMethod.equalsIgnoreCase(mMethodSignUpBySocial)) {
                                if (mLoginParser.getMessage().contains("forget")) {
                                    showForgotPasswordDialog();
                                } else {
                                    mActivity.getAppAlertDialog().showDialog("", mLoginParser.getMessage(), false);
                                }
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }

    /**
     * Method will show forgot password dialog.
     */
    public void showForgotPasswordDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mActivity);
        alertDialogBuilder.setTitle(getString(R.string.app_name));
        alertDialogBuilder.setMessage(getString(R.string.alt_msg_forgot_password));
        alertDialogBuilder.setPositiveButton(mActivity.getString(R.string.lbl_yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent mIntent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                mIntent.putExtra(getString(R.string.bundle_from), getString(R.string.bundle_from_login));
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
    public void onBackButton(OnClickListener mClickListener) {
        onBackPressed();
    }

    @Override
    public void setTitle(int title) {

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


}
