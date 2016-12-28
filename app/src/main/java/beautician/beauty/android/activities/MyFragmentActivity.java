package beautician.beauty.android.activities;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

import beautician.beauty.android.MyApplication;
import beautician.beauty.android.R;
import beautician.beauty.android.utilities.AppAlertDialog;
import beautician.beauty.android.utilities.RegisterGCM;
import beautician.beauty.android.utilities.WebMethod;


public abstract class MyFragmentActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    abstract public void replaceFragment(Fragment mFragment, boolean addBackStack);

    abstract public void setHeaderTitle(int title);

    abstract public void setAddVisible(int visible);

    abstract public void setLogoutVisible(int visible);

    abstract public void onBackButton(OnClickListener mClickListener);


    abstract public MyApplication getMyApplication();

    public void updatedUserData() {

    }

    public void setHeaderTitle(String headerTitle) {

    }

    public void setSearchIcon(int resourcheID) {

    }

    public void removePreviousFragment() {

    }

    AppAlertDialog mAppAlertDialog;
    WebMethod mWebMethod;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private LinearLayout mLinearLayoutParent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        enableStrictMode();
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.myAccentColor));
        }

        mSharedPreferences = getSharedPreferences(getString(R.string.sp_file_name), Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();

        mAppAlertDialog = new AppAlertDialog(this);
        mWebMethod = new WebMethod(this);

        setPermissionLocation();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mGoogleApiClient.connect();

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
        mLocationRequest.setInterval(60000);

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

    public AppAlertDialog getAppAlertDialog() {
        return mAppAlertDialog;
    }

    public WebMethod getWebMethod() {
        return mWebMethod;
    }

    public void logout() {

        mEditor.remove(RegisterGCM.PROPERTY_REG_ID);
        mEditor.remove(getString(R.string.sp_is_login));
        mEditor.commit();
        Intent mIntent = new Intent(MyFragmentActivity.this, LoginActivity.class);
        startActivity(mIntent);
        finish();
    }

    public void setTouchForHideKeyboard(View view) {

        //Set up touch listener for non-text box views to hide keyboard.
        if(!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(MyFragmentActivity.this);
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setTouchForHideKeyboard(innerView);
            }
        }
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }


    private void setPermissionLocation() {


        ArrayList<String> mStringsPermission = new ArrayList<>();


        if (ContextCompat.checkSelfPermission(MyFragmentActivity.this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            mStringsPermission.add(Manifest.permission.RECEIVE_SMS);
        }
//        if (ContextCompat.checkSelfPermission(MyFragmentActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            mStringsPermission.add(Manifest.permission.ACCESS_FINE_LOCATION);
//        }
        if (ContextCompat.checkSelfPermission(MyFragmentActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            mStringsPermission.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        String[] mStringArray = new String[mStringsPermission.size()];
        for (int i = 0; i < mStringsPermission.size(); i++)
            mStringArray[i] = mStringsPermission.get(i);

        if (mStringArray.length > 0)
            ActivityCompat.requestPermissions(MyFragmentActivity.this, mStringArray, 0);

    }

    @Override
    public void onLocationChanged(Location location) {

        if (location != null) {
            mEditor.putString(getString(R.string.sp_current_latitude), String.valueOf(location.getLatitude()));
            mEditor.putString(getString(R.string.sp_current_longitude), String.valueOf(location.getLongitude()));
            mEditor.commit();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        PackageManager pm = getPackageManager();
        int hasPermFine = pm.checkPermission(
                Manifest.permission.ACCESS_FINE_LOCATION,
                getPackageName());

        if (hasPermFine == PackageManager.PERMISSION_GRANTED) {

            Location mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLocation != null) {
                mEditor.putString(getString(R.string.sp_current_latitude), String.valueOf(mLocation.getLatitude()));
                mEditor.putString(getString(R.string.sp_current_longitude), String.valueOf(mLocation.getLongitude()));
                mEditor.commit();
            }
        }


        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            //Execute location service call if user has explicitly granted ACCESS_FINE_LOCATION..
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }else
        {
            ActivityCompat.requestPermissions(MyFragmentActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 123: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    try {
                        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, MyFragmentActivity.this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                }
                return;
            }

        }
    }

    @Override
    protected void onDestroy() {
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
        super.onDestroy();
    }
}
