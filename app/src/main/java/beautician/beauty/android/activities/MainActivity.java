package beautician.beauty.android.activities;

import android.app.AlertDialog;

import beautician.beauty.android.utilities.InterfaceDialogClickListener;
import beautician.beauty.android.utilities.StaticData;
import beautician.beauty.android.views.ChangeLanguageDialogFragment;
import beautician.beauty.android.views.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.nostra13.universalimageloader.core.ImageLoader;

import beautician.beauty.android.BuildConfig;
import beautician.beauty.android.MyApplication;
import beautician.beauty.android.R;
import beautician.beauty.android.fragments.AppointmentDetailsFragment;
import beautician.beauty.android.fragments.BuyFeatureAdsFragment;
import beautician.beauty.android.fragments.HomeFragment;
import beautician.beauty.android.fragments.MyCalendarFragment;
import beautician.beauty.android.fragments.MyServiceFragment;
import beautician.beauty.android.fragments.ProfileFragment;
import beautician.beauty.android.fragments.ProviderDetailsCategoryPagerFragment;
import beautician.beauty.android.fragments.ProviderSettingsFragment;
import beautician.beauty.android.fragments.SearchFragment;
import beautician.beauty.android.parsers.LoginParser;
import beautician.beauty.android.utilities.AppAlertDialog;
import beautician.beauty.android.utilities.CommonMethod;
import beautician.beauty.android.utilities.WebMethod;
import beautician.beauty.android.views.CircleImageView;
import beautician.beauty.android.views.ReportDialogFragment;

public class MainActivity extends MyFragmentActivity implements View.OnClickListener {

    private MyApplication myApplication;
    private DrawerLayout mDrawerLayout;
    private ImageView mImageViewBack;
    private ImageView mImageViewRightButton;
    private TextView mTextViewToolbarTitle;
    private AppAlertDialog mAppAlertDialog;
    private CommonMethod mCommonMethod;
    private WebMethod mWebMethod;

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    private CircleImageView mImageViewUserPicture;

    private RatingBar mRatingBarSatisfy;
    private RatingBar mRatingBarCommitted;

    private TextView mTextViewUserName;
    private TextView mTextViewHome;
    private TextView mTextViewSearch;
    private TextView mTextViewMyServices;
    private TextView mTextViewProfile;
    private TextView mTextViewMyCalender;
    private TextView mTextViewBuyAds;
    private TextView mTextViewBecomeAProvider;
    private TextView mTextViewLogout;
    private TextView mTextViewLanguage;
    private TextView mTextViewContactUs;

    private View mViewHome;
    private View mViewSearch;
    private View mViewMyServices;
    private View mViewProfile;
    private View mViewMyCalender;
    private View mViewBuyAds;
    private View mViewBecomeAProvider;
    private View mViewLogout;

    private ProgressDialog mProgressDialog;
    private BackProcessGetProfile mBackProcessGetProfile;
    private MyFragmentActivity mActivity;

    private LoginParser mLoginParser;
    private LoginParser mExpireParser;
    private LoginParser mVersionParser;
    private String mMethodGetProfile = "GetProfile";
    private String mMethodCheckExpireDate = "CheckExpireDate";
    private String mMethodCheckVersion = "CheckVersion";


    private String mStringAppointmentID = "";
    boolean isFromNotificationError = false;
    int mCurrentLangPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        enableStrictMode();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        mActivity = this;
        myApplication = (MyApplication) getApplication();
        mAppAlertDialog = new AppAlertDialog(this);
        mWebMethod = new WebMethod(this);
        mCommonMethod = new CommonMethod(this);
        mLoginParser = new LoginParser();
        mExpireParser = new LoginParser();
        mVersionParser = new LoginParser();

        mSharedPreferences = getSharedPreferences(getString(R.string.sp_file_name), Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();

        getWidgetReferecne();
        registerClickEvents();

        replaceFragment(new HomeFragment(), false);

        mBackProcessGetProfile = new BackProcessGetProfile();
        mBackProcessGetProfile.execute(mMethodGetProfile);


        Intent intent = getIntent();
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            Uri uri = intent.getData();
            String mStringProviderID = uri.getQueryParameter("p");
            String mStringServiceID = uri.getQueryParameter("s");


            removePreviousFragment();
            ProviderDetailsCategoryPagerFragment mProviderDetailsFragment = new ProviderDetailsCategoryPagerFragment();
            Bundle mBundle = new Bundle();
            mBundle.putString(getString(R.string.bundle_provider_id), mStringProviderID);
            if(mStringServiceID!=null && mStringServiceID.length()>0) {
                String[] ids = mStringServiceID.split("-");
                mBundle.putString(getString(R.string.bundle_service_id), ids[1]);
                mBundle.putString(getString(R.string.bundle_category_id), ids[0]);
            }
            mProviderDetailsFragment.setArguments(mBundle);

//            if(myApplication.isLogin())
                mActivity.replaceFragment(mProviderDetailsFragment, true);
//            else
//            {
//                mActivity.logout();
//            }
        }

        if (getIntent().hasExtra(getString(R.string.bundle_from))) {
            mStringAppointmentID = getIntent().getStringExtra(getString(R.string.bundle_appointment_id));
            openFromNotification();
        }

        LinearLayout mLinearLayoutParent = (LinearLayout)findViewById(R.id.activity_parent);
        setTouchForHideKeyboard(mLinearLayoutParent);
    }

    public void openFromNotification() {
        removePreviousFragment();
        AppointmentDetailsFragment mAppointmentDetailsFragment = new AppointmentDetailsFragment();
        Bundle mBundle = new Bundle();
        mBundle.putString(getString(R.string.bundle_appointment_id), mStringAppointmentID);
        mBundle.putString(getString(R.string.bundle_from), getString(R.string.bundle_from_notification));
        mAppointmentDetailsFragment.setArguments(mBundle);
        mActivity.replaceFragment(mAppointmentDetailsFragment, true);
    }



    @Override
    protected void onNewIntent(Intent intent) {
        if (intent.hasExtra(getString(R.string.bundle_from))) {
            try {
                mStringAppointmentID = intent.getStringExtra(getString(R.string.bundle_appointment_id));
                openFromNotification();
            } catch (Exception e) {
                isFromNotificationError = true;
                e.printStackTrace();
            }
        }
        super.onNewIntent(intent);
    }

    @Override
    protected void onResume() {
        if (isFromNotificationError) {
            openFromNotification();
            isFromNotificationError = false;
        }
        super.onResume();
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

    /**
    nb * Method call will set data..
     */
    private void setData() {

        ImageLoader.getInstance().displayImage(getMyApplication().getUserFiled(getString(R.string.sp_user_pic)), mImageViewUserPicture);
        mTextViewUserName.setText(myApplication.getUserFiled(getString(R.string.sp_user_name)));

        try {
            mRatingBarSatisfy.setRating(Float.parseFloat(myApplication.getUserFiled(getString(R.string.sp_satisfy_rating))));
            mRatingBarCommitted.setRating(Float.parseFloat(myApplication.getUserFiled(getString(R.string.sp_committed_rating))));
        }catch (Exception e){

        }
        System.out.println("Sat==" + myApplication.getUserFiled(getString(R.string.sp_satisfy_rating)));
        System.out.println("Comm==" + myApplication.getUserFiled(getString(R.string.sp_committed_rating)));

        if (myApplication.getUserProfile() != null && myApplication.getUserProfile().getData().getUser_type().equalsIgnoreCase("provider")) {
            mTextViewBuyAds.setVisibility(View.VISIBLE);
            mTextViewMyServices.setVisibility(View.VISIBLE);
            mTextViewBecomeAProvider.setVisibility(View.GONE);

            mViewBuyAds.setVisibility(View.VISIBLE);
            mViewMyServices.setVisibility(View.VISIBLE);
            mViewBecomeAProvider.setVisibility(View.GONE);

        } else {
            mTextViewBuyAds.setVisibility(View.GONE);
            mTextViewMyServices.setVisibility(View.GONE);
            mTextViewBecomeAProvider.setVisibility(View.VISIBLE);

            mViewBuyAds.setVisibility(View.GONE);
            mViewMyServices.setVisibility(View.GONE);
            mViewBecomeAProvider.setVisibility(View.VISIBLE);
        }


    }

    /**
     * Method call will get ids from xml file.
     */
    private void getWidgetReferecne() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mImageViewBack = (ImageView) findViewById(R.id.toolbar_default_imageview_left);
        mImageViewRightButton = (ImageView) findViewById(R.id.toolbar_default_imageview_right);
        mTextViewToolbarTitle = (TextView) findViewById(R.id.toolbar_default_textview_title);

        mRatingBarSatisfy = (RatingBar)findViewById(R.id.view_rating_rating_satification);
        mRatingBarCommitted = (RatingBar)findViewById(R.id.view_rating_rating_commitment);

        mImageViewUserPicture = (CircleImageView) findViewById(R.id.sliding_menu_imageview_user_dp);
        mTextViewUserName = (TextView) findViewById(R.id.sliding_menu_textview_user_name);
        mTextViewHome = (TextView) findViewById(R.id.sliding_menu_textview_home);
        mTextViewSearch = (TextView) findViewById(R.id.sliding_menu_textview_search);
        mTextViewMyServices = (TextView) findViewById(R.id.sliding_menu_textview_my_services);
        mTextViewProfile = (TextView) findViewById(R.id.sliding_menu_textview_profile);
        mTextViewMyCalender = (TextView) findViewById(R.id.sliding_menu_textview_my_calender);
        mTextViewBuyAds = (TextView) findViewById(R.id.sliding_menu_textview_buy_ads);
        mTextViewBecomeAProvider = (TextView) findViewById(R.id.sliding_menu_textview_become_a_provider);
        mTextViewLogout = (TextView) findViewById(R.id.sliding_menu_textview_logout);
        mTextViewLanguage = (TextView) findViewById(R.id.sliding_menu_textview_lang);
        mTextViewContactUs = (TextView) findViewById(R.id.sliding_menu_textview_contactus);

        mViewHome = (View) findViewById(R.id.sliding_menu_view_home);
        mViewSearch = (View) findViewById(R.id.sliding_menu_view_search);
        mViewMyServices = (View) findViewById(R.id.sliding_menu_view_my_services);
        mViewProfile = (View) findViewById(R.id.sliding_menu_view_profile);
        mViewMyCalender = (View) findViewById(R.id.sliding_menu_view_my_calendar);
        mViewBuyAds = (View) findViewById(R.id.sliding_menu_view_buy_ads);
        mViewBecomeAProvider = (View) findViewById(R.id.sliding_menu_view_become_a_provider);
        mViewLogout = (View) findViewById(R.id.sliding_menu_view_logout);

        setData();
    }

    /**
     * Method call will set onClick event.
     */
    private void registerClickEvents() {
        mImageViewBack.setOnClickListener(this);
        mTextViewHome.setOnClickListener(this);
        mTextViewSearch.setOnClickListener(this);
        mTextViewMyServices.setOnClickListener(this);
        mTextViewProfile.setOnClickListener(this);
        mTextViewMyCalender.setOnClickListener(this);
        mTextViewBuyAds.setOnClickListener(this);
        mTextViewBecomeAProvider.setOnClickListener(this);
        mTextViewLogout.setOnClickListener(this);
        mTextViewLanguage.setOnClickListener(this);
        mTextViewContactUs.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        if (v == mImageViewBack) {

            getAppAlertDialog().HideKeyboard(mImageViewBack);
            mDrawerLayout.openDrawer(GravityCompat.START);

        } else if (v == mTextViewHome) {
            removePreviousFragment();
            mDrawerLayout.closeDrawers();
            replaceFragment(new HomeFragment(), true);

        } else if (v == mTextViewSearch) {
            if(mActivity.getMyApplication().isAccountActive()) {
                removePreviousFragment();
                mDrawerLayout.closeDrawers();
                replaceFragment(new SearchFragment(), true);
            }else
            {
                mActivity.getAppAlertDialog().showDialog("", getString(R.string.alt_msg_disable_account), false);
            }


        } else if (v == mTextViewMyServices) {
            removePreviousFragment();
            mDrawerLayout.closeDrawers();
            replaceFragment(new MyServiceFragment(), true);

        } else if (v == mTextViewProfile) {
            removePreviousFragment();
            mDrawerLayout.closeDrawers();
            replaceFragment(new ProfileFragment(), true);

        } else if (v == mTextViewMyCalender) {
            if(mActivity.getMyApplication().isAccountActive()) {
                removePreviousFragment();
                mDrawerLayout.closeDrawers();
                replaceFragment(new MyCalendarFragment(), true);

            }else
            {
                mActivity.getAppAlertDialog().showDialog("", getString(R.string.alt_msg_disable_account), false);
            }

        } else if (v == mTextViewBuyAds) {

            if(mActivity.getMyApplication().isAccountActive()) {
                removePreviousFragment();
                mDrawerLayout.closeDrawers();
                replaceFragment(new BuyFeatureAdsFragment(), true);
            }else
            {
                mActivity.getAppAlertDialog().showDialog("", getString(R.string.alt_msg_disable_account), false);
            }

        } else if (v == mTextViewBecomeAProvider) {

            if(mActivity.getMyApplication().isAccountActive()) {
                removePreviousFragment();
                mDrawerLayout.closeDrawers();
                replaceFragment(new ProviderSettingsFragment(), true);
            }else
            {
                mActivity.getAppAlertDialog().showDialog("", getString(R.string.alt_msg_disable_account), false);
            }


        } else if (v == mTextViewLogout) {

            mDrawerLayout.closeDrawers();
//            mAppAlertDialog.showDeleteAlert(this, getString(R.string.app_name), getString(R.string.alt_msg_logout), new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    LoginManager.getInstance().logOut();
//                    mActivity.logout();
//                }
//            });

            mActivity.getAppAlertDialog().showDeleteAlert("", getString(R.string.alt_msg_logout),
                    getString(R.string.lbl_no), getString(R.string.lbl_yes), new InterfaceDialogClickListener() {
                        @Override
                        public void onClick() {

                            LoginManager.getInstance().logOut();
                            mActivity.logout();
                        }
                    });


        }else if (v == mTextViewLanguage) {

            mDrawerLayout.closeDrawers();

            ChangeLanguageDialogFragment mChangeLanguageDialogFragment = new ChangeLanguageDialogFragment();
            mChangeLanguageDialogFragment.show(mActivity.getSupportFragmentManager(), "");

//            showLanguageDialog();
        }else if (v == mTextViewContactUs) {
            mDrawerLayout.closeDrawers();
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "customer.support@beauticianapp.com", null));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
            startActivity(Intent.createChooser(emailIntent, getString(R.string.app_name)));
        }
    }


    @Override
    public void replaceFragment(Fragment mFragment, boolean addBackStack) {

        FragmentTransaction mFragmentTransaction = getSupportFragmentManager().beginTransaction();
        mFragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
        mFragmentTransaction.replace(R.id.content_frame, mFragment);
        if (addBackStack)
            mFragmentTransaction.addToBackStack(null);
        mFragmentTransaction.commit();
    }


    /**
     * Method call remove previous fragments.
     */
    @Override
    public void removePreviousFragment() {
        FragmentManager fm = getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
        replaceFragment(new HomeFragment(), false);
    }

    @Override
    public void setHeaderTitle(int title) {
        mTextViewToolbarTitle.setText(title);
    }

    @Override
    public void setHeaderTitle(String title) {
        mTextViewToolbarTitle.setText(title);
    }

    @Override
    public void setAddVisible(int visible) {

    }

    @Override
    public void setLogoutVisible(int visible) {

    }

    @Override
    public void onBackButton(View.OnClickListener mClickListener) {

        mImageViewRightButton.setOnClickListener(mClickListener);

    }

    @Override
    public MyApplication getMyApplication() {
        return myApplication;
    }


    @Override
    public AppAlertDialog getAppAlertDialog() {
        return super.getAppAlertDialog();
    }

    @Override
    public WebMethod getWebMethod() {
        return super.getWebMethod();
    }

    @Override
    public void setSearchIcon(int resourcheID) {

        mImageViewRightButton.setImageResource(resourcheID);
    }

    @Override
    public void onBackPressed() {

        if (mDrawerLayout.isDrawerOpen(GravityCompat.START))
            mDrawerLayout.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();

    }

    @Override
    public void updatedUserData() {
        super.updatedUserData();
        setData();
    }

    /**
     * Method will change app lang
     */
    public void showLanguageDialog() {

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
        alert.setPositiveButton(R.string.lbl_cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alert.show();
    }

    /**
     * AsyncTask for calling webservice in background.
     * @author ebaraiya
     */
    public class BackProcessGetProfile extends AsyncTask<String, Void, String> {
        String mCurrentMethod = "";

        @Override
        protected void onPreExecute() {
//            mProgressDialog = ProgressDialog.show(mActivity, "", getString(R.string.dialog_loading), true);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            mCurrentMethod = params[0];
            if (mCurrentMethod.equalsIgnoreCase(mMethodGetProfile)) {
                mLoginParser = (LoginParser) mActivity.getWebMethod().callGetProfile(mActivity.getMyApplication().getUserFiled(getString(R.string.sp_user_id)), mActivity.getMyApplication().getUserFiled(getString(R.string.sp_user_hash)), mLoginParser);

            } else if (mCurrentMethod.equalsIgnoreCase(mMethodCheckExpireDate)) {

                mExpireParser = (LoginParser) mActivity.getWebMethod().callExpireDate(mExpireParser);
            }
            else if (mCurrentMethod.equalsIgnoreCase(mMethodCheckVersion)) {

                mVersionParser = (LoginParser) mActivity.getWebMethod().callCheckVersion(
                        mActivity.getMyApplication().getUserFiled(getString(R.string.sp_user_id)),
                        mActivity.getMyApplication().getUserFiled(getString(R.string.sp_user_hash)),
                        String.valueOf(BuildConfig.VERSION_CODE),
                        mVersionParser);
            }

            return null;

        }

        @Override
        protected void onPostExecute(String result) {


            if (mActivity.getWebMethod().isNetError) {
                mActivity.getAppAlertDialog().showDialog("", getString(R.string.validation_no_internet), false);
            } else if (mActivity.getWebMethod().isError) {
                mActivity.getAppAlertDialog().showDialog("", getString(R.string.validation_failed), false);
            } else {
                try {
                    if (mCurrentMethod.equalsIgnoreCase(mMethodGetProfile)) {


                        if (mLoginParser.getWs_status().equalsIgnoreCase("true") && mLoginParser.getData() != null) {
                            mActivity.getMyApplication().setUserProfile(mLoginParser);
                            setData();
                        } else {
                            if (mLoginParser.getMessage().toString().contains(getString(R.string.alt_msg_exipred))) {
                                mActivity.getAppAlertDialog().showAlertWithSingleButton("", mLoginParser.getMessage().toString(),
                                        mActivity.getString(R.string.lbl_logout),
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                mActivity.logout();
                                            }
                                        });
                            }
                        }
                        mBackProcessGetProfile = new BackProcessGetProfile();
                        mBackProcessGetProfile.execute(mMethodCheckExpireDate);

                    } else if (mCurrentMethod.equalsIgnoreCase(mMethodCheckExpireDate)) {

                        if (mExpireParser.getWs_status().equalsIgnoreCase("false")) {
                            mActivity.getAppAlertDialog().showDialog(getString(R.string.app_name), "Expired App.", true);
                        }

                    }
                    else if (mCurrentMethod.equalsIgnoreCase(mMethodCheckVersion)) {

                        mBackProcessGetProfile = new BackProcessGetProfile();
                        mBackProcessGetProfile.execute(mMethodCheckExpireDate);

                        if (mVersionParser.getWs_status().equalsIgnoreCase("false")) {

                            mActivity.getAppAlertDialog().showAlertWithSingleButton("", mVersionParser.getMessage().toString(),
                                    mActivity.getString(R.string.lbl_ok),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("market://details?id="+getPackageName()))));
                                            finish();
                                        }
                                    });

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (mBackProcessGetProfile != null)
            mBackProcessGetProfile.cancel(false);

        super.onDestroy();
    }
}



