package beautician.beauty.android;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.util.DisplayMetrics;

import com.facebook.FacebookSdk;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.util.HashMap;
import java.util.Locale;

import beautician.beauty.android.parsers.BookAppointmentParser;
import beautician.beauty.android.parsers.CategoryListParser;
import beautician.beauty.android.parsers.LoginParser;
import beautician.beauty.android.utilities.TypefaceUtil;


public class MyApplication extends Application  {

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private CategoryListParser mCategoryListParser;
    private LoginParser mLoginParserUserProfile;

    public HashMap<String, BookAppointmentParser> mHashMapBookAppointment;


    @SuppressWarnings("unused")
    @Override
    public void onCreate() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
//            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyDialog().build());
//            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyDeath().build());
//        }


        FacebookSdk.sdkInitialize(getApplicationContext());
        initImageLoader(getApplicationContext());

        TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/helevetica_normal.TTF");

        mSharedPreferences = getSharedPreferences(getString(R.string.sp_file_name), Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        mHashMapBookAppointment = new HashMap<String, BookAppointmentParser>();

        String currentLang = mSharedPreferences.getString(getString(R.string.sp_sp_lang), "en");
        if(mSharedPreferences.getBoolean(getString(R.string.sp_has_lang_set), false))
            changeLanguage(currentLang);

        super.onCreate();

        MultiDex.install(this);


    }

    /**
     * Method will change app language
     * @param locale
     */
    public void changeLanguage(String locale) {
        Locale myLocale = new Locale(locale);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);

        mEditor.putBoolean(getString(R.string.sp_has_lang_set), true);
        mEditor.putString(getString(R.string.sp_sp_lang), locale);
        mEditor.commit();

    }


    /**
     * Function will return logged user's email id is verify or not
     */

    public static void initImageLoader(Context context) {

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory().discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO).build();
        ImageLoader.getInstance().init(config);
    }

    public CategoryListParser getCategoryListParser() {
        return mCategoryListParser;
    }

    public void setUserProfile(LoginParser mLoginParserUserProfile) {
        this.mLoginParserUserProfile = mLoginParserUserProfile;

        mEditor.putString(getString(R.string.sp_user_active), mLoginParserUserProfile.getData().getUser_active());
        mEditor.putString(getString(R.string.sp_user_name), mLoginParserUserProfile.getData().getUsername());
        mEditor.putString(getString(R.string.sp_user_email), mLoginParserUserProfile.getData().getEmail());
        mEditor.putString(getString(R.string.sp_user_mobile), mLoginParserUserProfile.getData().getUser_phone());
        mEditor.putString(getString(R.string.sp_user_hash), mLoginParserUserProfile.getData().getUser_hash());
        mEditor.putString(getString(R.string.sp_user_pic), mLoginParserUserProfile.getData().getUser_image());
        mEditor.putString(getString(R.string.sp_user_lat), mLoginParserUserProfile.getData().getUser_lat());
        mEditor.putString(getString(R.string.sp_user_lon), mLoginParserUserProfile.getData().getUser_lng());
        mEditor.putString(getString(R.string.sp_user_id), mLoginParserUserProfile.getData().getUser_id());
        mEditor.putString(getString(R.string.sp_user_location), mLoginParserUserProfile.getData().getUser_location());
        mEditor.putString(getString(R.string.sp_satisfy_rating), mLoginParserUserProfile.getData().getTotalsatisfy());
        mEditor.putString(getString(R.string.sp_committed_rating), mLoginParserUserProfile.getData().getTotalcommited());

        mEditor.commit();

    }

    public LoginParser getUserProfile() {
        return mLoginParserUserProfile;
    }

    public void setCategoryListParser(CategoryListParser mCategoryListParser) {
        this.mCategoryListParser = mCategoryListParser;
    }

    /**
     * Function will return current app's lang
     * @return lang
     */
    public String getCurrentLang() {
        mSharedPreferences = getSharedPreferences(getString(R.string.sp_file_name), Context.MODE_PRIVATE);
        return mSharedPreferences.getString(getString(R.string.sp_sp_lang), "en");
    }

    /**
     * Method will return logged user's email
     * @return user field
     */
    public String getUserFiled(String field) {
        mSharedPreferences = getSharedPreferences(getString(R.string.sp_file_name), Context.MODE_PRIVATE);
        return mSharedPreferences.getString(field, "");
    }

    /**
     * Method will return logged user's email
     * @return is login
     */
    public boolean isLogin() {
        mSharedPreferences = getSharedPreferences(getString(R.string.sp_file_name), Context.MODE_PRIVATE);
        return mSharedPreferences.getBoolean(getString(R.string.sp_is_login), false);
    }


    /**
     * Method will return logged user's email
     * @return is login
     */
    public boolean isAccountActive() {
        mSharedPreferences = getSharedPreferences(getString(R.string.sp_file_name), Context.MODE_PRIVATE);
        String mStatus =  mSharedPreferences.getString(getString(R.string.sp_user_active), "Yes");
        if(mStatus.equalsIgnoreCase(getString(R.string.lbl_disable_account)))
            return false;
        return true;
    }

    public String getCurrentLatitude() {
        mSharedPreferences = getSharedPreferences(getString(R.string.sp_file_name), Context.MODE_PRIVATE);
        return mSharedPreferences.getString(getString(R.string.sp_current_latitude), "0.0");
    }

    public String getCurrentLongitude() {
        mSharedPreferences = getSharedPreferences(getString(R.string.sp_file_name), Context.MODE_PRIVATE);
        return mSharedPreferences.getString(getString(R.string.sp_current_longitude), "0.0");
    }

    public HashMap<String, BookAppointmentParser> getBookedAppoinmentData() {
        return mHashMapBookAppointment;
    }

    public void addServiceForBookAppointment(String service_id, BookAppointmentParser bookAppointmentParser) {
        mHashMapBookAppointment.put(service_id, bookAppointmentParser);
    }

    public void clearnBookAppointment(String category_id) {
        if (category_id.length() > 0)
            mHashMapBookAppointment.remove(category_id);
        else
            mHashMapBookAppointment.clear();

    }


    /**
     * Method will check location is ON or OFF
     *
     * @return is location on or off
     */
    public boolean isLocationOn() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (!isGPSEnabled && !isNetworkEnabled) {
            return false;
        }
        return true;
    }



    @Override
    public void onTerminate() {

        super.onTerminate();
    }

}
