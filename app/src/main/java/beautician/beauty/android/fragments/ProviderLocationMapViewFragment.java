package beautician.beauty.android.fragments;

import android.app.Activity;
import beautician.beauty.android.views.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

import beautician.beauty.android.MyApplication;
import beautician.beauty.android.R;
import beautician.beauty.android.activities.MyFragmentActivity;


public class ProviderLocationMapViewFragment extends MyFragmentActivity implements OnClickListener {

    private MyApplication myApplication;
    private MyFragmentActivity mActivity;
    private RelativeLayout rootView;
    private GoogleMap googleMap;
    private LatLng mLatLng;

    private EditText mEditTextAddress;
    private EditText mEditTextCity;
    private EditText mEditTextCountry;
    private TextView mTextViewSave;
    private ImageView mImageViewBack;

    private ProgressDialog mProgressDialog;
    private List<Address> addresses;


    private String mStringAddress = "";
    private String mStringCity = "";
    private String mStringCountry = "";
    private double mDoubleLatitude = 0.0;
    private double mDoubleLongitude = 0.0;

    public ProviderLocationMapViewFragment() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_location_mapview);
        rootView = (RelativeLayout)findViewById(R.id.activity_parent);
        mActivity = this;
        myApplication = (MyApplication) getApplication();
        mActivity.setTouchForHideKeyboard(rootView);

        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mActivity);
        if (status == ConnectionResult.SUCCESS) {
            //Success! Do what you want
            try {
                MapsInitializer.initialize(mActivity);
                // Loading map
                initilizeMap();
            } catch (Exception e) {
//	            e.printStackTrace();
            }

        } else {
            Toast.makeText(mActivity, "Not Installed Google Play Services..", Toast.LENGTH_SHORT).show();
        }

        mLatLng = new LatLng(Double.parseDouble(mActivity.getMyApplication().getCurrentLatitude()), Double.parseDouble(mActivity.getMyApplication().getCurrentLongitude()));

        getWidgetRefrence(rootView);
        registerOnClick();

        if (!mActivity.getMyApplication().isLocationOn()) {
            mActivity.getAppAlertDialog().showSettingsAlert();
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        initilizeMap();
    }

    /*@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null)
                parent.removeView(rootView);
        }

        try {
            rootView = inflater.inflate(R.layout.fragment_location_mapview, container, false);
        } catch (Exception e1) {
//			e1.printStackTrace();
        }

        mActivity = this;
        mActivity.setHeaderTitle(R.string.action_settings);
        mActivity.setSearchIcon(R.drawable.icon_search);
        mActivity.onBackButton(null);

        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mActivity);
        if (status == ConnectionResult.SUCCESS) {
            //Success! Do what you want
            try {
                MapsInitializer.initialize(mActivity);
                // Loading map
                initilizeMap();
            } catch (Exception e) {
//	            e.printStackTrace();
            }

        } else {
            Toast.makeText(mActivity, "Not Installed Google Play Services..", Toast.LENGTH_SHORT).show();
        }

        mLatLng = new LatLng(Double.parseDouble(mActivity.getMyApplication().getCurrentLatitude()), Double.parseDouble(mActivity.getMyApplication().getCurrentLongitude()));

        getWidgetRefrence(rootView);
        registerOnClick();

        if (!mActivity.getMyApplication().isLocationOn()) {
            mActivity.getAppAlertDialog().showSettingsAlert();
        }

        return rootView;
    }*/

    /**
     * Method call will get IDs from xml file.
     *
     * @param v
     */
    private void getWidgetRefrence(View v) {
        mTextViewSave = (TextView) findViewById(R.id.fragment_location_textview_save);
        mEditTextAddress = (EditText) findViewById(R.id.fragment_location_edittext_address);
        mEditTextCity = (EditText) findViewById(R.id.fragment_location_edittext_city);
        mEditTextCountry = (EditText) findViewById(R.id.fragment_location_edittext_country);
        mImageViewBack = (ImageView)findViewById(R.id.fragment_location_imageview_back);

//        mEditTextAddress.setText(mActivity.getMyApplication().getLocationAddress());
//        mEditTextCity.setText(mActivity.getMyApplication().getLocationCity());
//        mEditTextCountry.setText(mActivity.getMyApplication().getLocationCountry());

        if(getIntent().getExtras().getString(getString(R.string.bundle_from)).equalsIgnoreCase(getString(R.string.bundle_from_setting)))
        {
            mEditTextCity.setVisibility(View.VISIBLE);
            mEditTextCountry.setVisibility(View.VISIBLE);
        }else {
            mEditTextCity.setVisibility(View.GONE);
            mEditTextCountry.setVisibility(View.GONE);
        }

        if(getIntent().hasExtra(getString(R.string.bundle_address)))
        {
            mStringAddress = getIntent().getExtras().getString(getString(R.string.bundle_address));
            mStringCity = getIntent().getExtras().getString(getString(R.string.bundle_city));
            mStringCountry = getIntent().getExtras().getString(getString(R.string.bundle_country));

            mDoubleLatitude = Double.parseDouble(getIntent().getExtras().getString(getString(R.string.bundle_lat)));
            mDoubleLongitude = Double.parseDouble(getIntent().getExtras().getString(getString(R.string.bundle_lng)));

            mLatLng = new LatLng(mDoubleLatitude, mDoubleLongitude);

            mEditTextAddress.setText(mStringAddress);
            mEditTextCity.setText(mStringCity);
            mEditTextCountry.setText(mStringCountry);
        }

    }

    /**
     * Method call will Register OnClick() Events for widgets.
     */
    private void registerOnClick() {
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mLatLng = latLng;
                googleMap.clear();
                googleMap.addMarker(new MarkerOptions().position(latLng));
                googleMap.setMyLocationEnabled(true);
                new BackProcessSearchAddress().execute("");
            }
        });

        mTextViewSave.setOnClickListener(this);
        mImageViewBack.setOnClickListener(this);
    }

    /**
     * @param v
     */
    @Override
    public void onClick(View v) {
        if (v == mTextViewSave) {
//            mActivity.getMyApplication().setLatLngAddLocation(mLatLng);
//            mActivity.getMyApplication().setLocationAddress(mEditTextAddress.getText().toString(), mEditTextCity.getText().toString(), mEditTextCountry.getText().toString());
//
//            mActivity.onBackPressed();

            Intent intent = new Intent();
            intent.putExtra(getString(R.string.bundle_address), mEditTextAddress.getText().toString());
            intent.putExtra(getString(R.string.bundle_city), mEditTextCity.getText().toString());
            intent.putExtra(getString(R.string.bundle_country), mEditTextCountry.getText().toString());

            intent.putExtra(getString(R.string.bundle_lat), String.valueOf(mLatLng.latitude));
            intent.putExtra(getString(R.string.bundle_lng), String.valueOf(mLatLng.longitude));

            setResult(RESULT_OK, intent);
            finish();
        }else if(v==mImageViewBack){
            finish();
        }

    }


    /**
     * function to load map. If map is not created it will create it for you
     */
    private void initilizeMap() {
        if (googleMap == null) {
            googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_location_map_mapview)).getMap();

            googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {

                    if (mDoubleLatitude != 0.0) {
                        googleMap.clear();
                        googleMap.addMarker(new MarkerOptions().position(mLatLng));
                        googleMap.setMyLocationEnabled(true);
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(mLatLng).zoom(15).build();
                        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    } else {
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(mLatLng).zoom(15).build();
                        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        new BackProcessSearchAddress().execute("");
                    }

                }
            });
            googleMap.setMyLocationEnabled(true);
            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText(mActivity, "Sorry! unable to create maps", Toast.LENGTH_SHORT).show();
            }
        }
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

    }

    @Override
    public MyApplication getMyApplication() {
        return myApplication;
    }


    public class BackProcessSearchAddress extends AsyncTask<String, Void, String> {
        String responseData = "";

        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(mActivity, "", getString(R.string.dialog_loading), true);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            Geocoder geoCoder = new Geocoder(mActivity);
            try {
                addresses = geoCoder.getFromLocation(mLatLng.latitude, mLatLng.longitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (mProgressDialog != null)
                mProgressDialog.dismiss();


            try {
                if (addresses.size() > 0) {
                    StringBuilder address = new StringBuilder();
                    for (int i = 0; i < addresses.get(0).getMaxAddressLineIndex(); i++) {
                        if (i != 0)
                            address.append(", ");
                        address.append(addresses.get(0).getAddressLine(i));
                    }
                    mEditTextAddress.setText(address.toString());

                    mEditTextCity.setText(addresses.get(0).getLocality());
                    mEditTextCountry.setText(addresses.get(0).getCountryName());
                } else {
                    mActivity.getAppAlertDialog().showDialog ("", "Address not found", false);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            super.onPostExecute(result);
        }
    }


    @Override
    public void onDestroy() {
        try {
            SupportMapFragment fragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(
                    R.id.fragment_location_map_mapview);
            if (fragment != null) {
                getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                googleMap = null;
            }

        } catch (IllegalStateException e) {
        }
        super.onDestroy();
    }

}
