package beautician.beauty.android.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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

import beautician.beauty.android.R;
import beautician.beauty.android.activities.MyFragmentActivity;


public class CommonMapViewFragment extends Fragment implements OnClickListener {

    private MyFragmentActivity mActivity;
    private View rootView;
    private GoogleMap googleMap;
    private LatLng mLatLng;
    private String mStringLat = "0.0";
    private String mStringLng = "0.0";
    private String mStringAddress = "";


    public CommonMapViewFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null)
                parent.removeView(rootView);
        }

        try {
            rootView = inflater.inflate(R.layout.fragment_mapview, container, false);
        } catch (Exception e1) {
//			e1.printStackTrace();
        }

        mActivity = (MyFragmentActivity) getActivity();
        mActivity.setHeaderTitle(R.string.lbl_location);
        mActivity.setSearchIcon(R.drawable.icon_search);
        mActivity.onBackButton(null);

        mStringLat = getArguments().getString(getString(R.string.bundle_lat));
        mStringLng = getArguments().getString(getString(R.string.bundle_lng));
        mStringAddress = getArguments().getString(getString(R.string.bundle_address));

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


        mLatLng = new LatLng(Double.parseDouble(mStringLat), Double.parseDouble(mStringLng));

        getWidgetRefrence(rootView);
        registerOnClick();

        if (!mActivity.getMyApplication().isLocationOn()) {
            mActivity.getAppAlertDialog().showSettingsAlert();
        }

        return rootView;
    }

    /**
     * Method call will get IDs from xml file.
     *
     * @param v
     */
    private void getWidgetRefrence(View v) {


    }

    /**
     * Method call will Register OnClick() Events for widgets.
     */
    private void registerOnClick() {

    }

    /**
     * @param v
     */
    @Override
    public void onClick(View v) {

    }


    /**
     * function to load map. If map is not created it will create it for you
     */
    private void initilizeMap() {
        if (googleMap == null) {
            googleMap = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragment_mapview_map)).getMap();

            googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {

                    googleMap.addMarker(new MarkerOptions().position(mLatLng).title(mStringAddress));
                    googleMap.setMyLocationEnabled(true);
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(mLatLng).zoom(15).build();
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

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
    public void onDestroy() {
        try {
            SupportMapFragment fragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(
                    R.id.fragment_mapview_map);
            if (fragment != null) {
                getChildFragmentManager().beginTransaction().remove(fragment).commit();
                googleMap = null;
            }

        } catch (IllegalStateException e) {
        }
        super.onDestroy();
    }

}
