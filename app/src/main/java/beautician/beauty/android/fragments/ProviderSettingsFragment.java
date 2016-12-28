package beautician.beauty.android.fragments;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import beautician.beauty.android.views.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.io.IOException;
import java.util.List;

import beautician.beauty.android.R;
import beautician.beauty.android.activities.MyFragmentActivity;
import beautician.beauty.android.parsers.CategoryDataParser;
import beautician.beauty.android.parsers.CategoryListParser;
import beautician.beauty.android.parsers.LoginParser;
import beautician.beauty.android.utilities.CustomTimePickerFragment;
import beautician.beauty.android.utilities.InterfaceTimePicker;
import beautician.beauty.android.utilities.StaticData;
import beautician.beauty.android.views.RangeSeekBar;

@SuppressLint("InflateParams")
public class ProviderSettingsFragment extends Fragment implements OnClickListener, CompoundButton.OnCheckedChangeListener, InterfaceTimePicker {

    private MyFragmentActivity mActivity;
    private View rootView;

    private BackProcessGetSettings mBackProcessGetSettings;
    private ProgressDialog mProgressDialog;
    private String mMethodSetSettings = "SetSettings";
    private String mMethodGetCountry = "GetCountry";
    private String mMethodGetCity = "GetCity";
    private LoginParser mLoginParserSettings;
    private CategoryListParser mCountryDataParser;
    private CategoryListParser mCityDataParser;

    private TextView mTextViewMyPlace;
    private TextView mTextViewSeekerPlace;
    private TextView mTextViewBoth;
    private TextView mTextViewAcceptableRating;
    private TextView mTextViewAutoAcceptable;
    private TextView mTextViewAddress;

    private EditText mEditTextTransFees;
    private EditText mEditTextMinFees;
    private RelativeLayout mRelativeLayoutTransFees;

    private ImageView mImageViewPicLocation;

    private CheckBox mCheckBoxSunday;
    private CheckBox mCheckBoxMonday;
    private CheckBox mCheckBoxTuesday;
    private CheckBox mCheckBoxWednesday;
    private CheckBox mCheckBoxFriday;
    private CheckBox mCheckBoxSaturday;
    private CheckBox mCheckBoxThurday;

    private Spinner mDialogSpinnerCountry;
    private Spinner mDialogSpinnerCity;

    private TextView mTextViewSunStartTime;
    private TextView mTextViewSunEndTime;
    private TextView mTextViewMonStartTime;
    private TextView mTextViewMonEndTime;
    private TextView mTextViewTuesStartTime;
    private TextView mTextViewTuesEndTime;
    private TextView mTextViewWedStartTime;
    private TextView mTextViewWedEndTime;
    private TextView mTextViewThurStartTime;
    private TextView mTextViewThurEndTime;
    private TextView mTextViewFriStartTime;
    private TextView mTextViewFriEndTime;
    private TextView mTextViewSatStartTime;
    private TextView mTextViewSatEndTime;
    private TextView mTextViewSave;

    private CheckBox mCheckBoxAccept;

    private RangeSeekBar mRrangeSeekBarDay;

    private Dialog mDialogRating;

    int mIntCountrySelection = 0;
    int mIntCitySelection = 0;
    String mStringLocation = "";
    String mStringLocationPolicy = "";
    String mStringCityID = "";
    String mStringCountryID = "";
    String mStringCurrentCountry = "";
    String mStringCity = "";
    String mStringCountry = "";
    String mStringLatitude = "";
    String mStringLongitude = "";
    String mStringSatisfyRating = "3";
    String mStringCommitedRating = "2";
    String mStringMonStart = "00:00";
    String mStringMonEnd = "00:00";
    String mStringTueStart = "00:00";
    String mStringTueEnd = "00:00";
    String mStringWedStart = "00:00";
    String mStringWedEnd = "00:00";
    String mStringThuStart = "00:00";
    String mStringThuEnd = "00:00";
    String mStringFriStart = "00:00";
    String mStringFriEnd = "00:00";
    String mStringSatStart = "00:00";
    String mStringSatEnd = "00:00";
    String mStringSunStart = "00:00";
    String mStringSunEnd = "00:00";

    String mStringStart = "";
    String mStringEnd = "";

    private String mStringMinValue = "3";
    private String mStringMaxValue = "90";

    private String mStringTransFees = "0";
    private String mStringMinFees = "0";

    private Dialog mDialogPrivacyPolicy;

    public ProviderSettingsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_provider_settings, container, false);

        mActivity = (MyFragmentActivity) getActivity();
        mActivity.setHeaderTitle(R.string.lbl_setting);
        mActivity.setSearchIcon(R.drawable.icon_search);
        mActivity.onBackButton(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.replaceFragment(new SearchFragment(), true);
            }
        });

        mLoginParserSettings = new LoginParser();
        mCountryDataParser = new CategoryListParser();
        mCityDataParser = new CategoryListParser();

        mStringStart = getString(R.string.lbl_start_time);
        mStringEnd = getString(R.string.lbl_end_time);

        getWidgetRefrence(rootView);
        registerOnClick();

        setData();

        mBackProcessGetSettings = new BackProcessGetSettings();
        mBackProcessGetSettings.execute(mMethodGetCountry);

        return rootView;
    }

    /**
     * Method call will get IDs from xml file.
     */
    private void getWidgetRefrence(View v) {

        mTextViewMyPlace = (TextView) v.findViewById(R.id.fragment_provider_settigs_textview_my_place);
        mTextViewSeekerPlace = (TextView) v.findViewById(R.id.fragment_provider_settigs_textview_seeker_place);
        mTextViewBoth = (TextView) v.findViewById(R.id.fragment_provider_settigs_textview_both);
        mTextViewAcceptableRating = (TextView) v.findViewById(R.id.fragment_provider_settigs_textview_lowest_acceptable_rating);
        mTextViewAutoAcceptable = (TextView) v.findViewById(R.id.fragment_provider_settigs_textview_auto_accept);
        mTextViewAddress = (TextView) v.findViewById(R.id.fragment_provider_settigs_textview_address);
        mImageViewPicLocation = (ImageView) v.findViewById(R.id.fragment_provider_settigs_imageview_pic_location);

        mEditTextTransFees = (EditText)v.findViewById(R.id.fragment_provider_settigs_edittex_trans_fees);
        mEditTextMinFees = (EditText)v.findViewById(R.id.fragment_provider_settigs_edittex_min_fees);
        mRelativeLayoutTransFees = (RelativeLayout)v.findViewById(R.id.fragment_provider_settigs_relative_trans_fees);

        mCheckBoxSunday = (CheckBox) v.findViewById(R.id.fragment_provider_settigs_checkbox_sunday);
        mCheckBoxMonday = (CheckBox) v.findViewById(R.id.fragment_provider_settigs_checkbox_monday);
        mCheckBoxTuesday = (CheckBox) v.findViewById(R.id.fragment_provider_settigs_checkbox_tuesday);
        mCheckBoxWednesday = (CheckBox) v.findViewById(R.id.fragment_provider_settigs_checkbox_wednesday);
        mCheckBoxFriday = (CheckBox) v.findViewById(R.id.fragment_provider_settigs_checkbox_friday);
        mCheckBoxSaturday = (CheckBox) v.findViewById(R.id.fragment_provider_settigs_checkbox_saturday);
        mCheckBoxThurday = (CheckBox) v.findViewById(R.id.fragment_provider_settigs_checkbox_thursday);

        mDialogSpinnerCountry = (Spinner) v.findViewById(R.id.fragment_provider_settigs_spinner_country);
        mDialogSpinnerCity = (Spinner) v.findViewById(R.id.fragment_provider_settigs_spinner_city);

        mTextViewSunStartTime = (TextView) v.findViewById(R.id.fragment_provider_settigs_textview_sunday_starttime);
        mTextViewSunEndTime = (TextView) v.findViewById(R.id.fragment_provider_settigs_textview_sunday_endtime);
        mTextViewMonStartTime = (TextView) v.findViewById(R.id.fragment_provider_settigs_textview_monday_starttime);
        mTextViewMonEndTime = (TextView) v.findViewById(R.id.fragment_provider_settigs_textview_monday_endtime);
        mTextViewTuesStartTime = (TextView) v.findViewById(R.id.fragment_provider_settigs_textview_tueday_starttime);
        mTextViewTuesEndTime = (TextView) v.findViewById(R.id.fragment_provider_settigs_textview_tuesday_endtime);
        mTextViewWedStartTime = (TextView) v.findViewById(R.id.fragment_provider_settigs_textview_wednesday_starttime);
        mTextViewWedEndTime = (TextView) v.findViewById(R.id.fragment_provider_settigs_textview_wednesday_endtime);
        mTextViewThurStartTime = (TextView) v.findViewById(R.id.fragment_provider_settigs_textview_thurseday_starttime);
        mTextViewThurEndTime = (TextView) v.findViewById(R.id.fragment_provider_settigs_textview_thurseday_endtime);
        mTextViewFriStartTime = (TextView) v.findViewById(R.id.fragment_provider_settigs_textview_friday_starttime);
        mTextViewFriEndTime = (TextView) v.findViewById(R.id.fragment_provider_settigs_textview_friday_endtime);
        mTextViewSatStartTime = (TextView) v.findViewById(R.id.fragment_provider_settigs_textview_saturday_starttime);
        mTextViewSatEndTime = (TextView) v.findViewById(R.id.fragment_provider_settigs_textview_saturday_endtime);
        mTextViewSave = (TextView) v.findViewById(R.id.fragment_provider_settigs_textview_save);

        mRrangeSeekBarDay = (RangeSeekBar) v.findViewById(R.id.fragment_provider_settigs_range_seekbar);

        mCheckBoxAccept = (CheckBox) v.findViewById(R.id.fragment_provider_settigs_checkbox_accept);
    }

    /**
     * Method call will Register OnClick() Events for widgets.
     */
    private void registerOnClick() {


        mTextViewMyPlace.setOnClickListener(this);
        mTextViewSeekerPlace.setOnClickListener(this);
        mTextViewBoth.setOnClickListener(this);
        mTextViewAcceptableRating.setOnClickListener(this);
        mTextViewAutoAcceptable.setOnClickListener(this);
        mImageViewPicLocation.setOnClickListener(this);

        mTextViewSunStartTime.setOnClickListener(this);
        mTextViewSunEndTime.setOnClickListener(this);
        mTextViewMonStartTime.setOnClickListener(this);
        mTextViewMonEndTime.setOnClickListener(this);
        mTextViewTuesStartTime.setOnClickListener(this);
        mTextViewTuesEndTime.setOnClickListener(this);
        mTextViewWedStartTime.setOnClickListener(this);
        mTextViewWedEndTime.setOnClickListener(this);
        mTextViewThurStartTime.setOnClickListener(this);
        mTextViewThurEndTime.setOnClickListener(this);
        mTextViewFriStartTime.setOnClickListener(this);
        mTextViewFriEndTime.setOnClickListener(this);
        mTextViewSatStartTime.setOnClickListener(this);
        mTextViewSatEndTime.setOnClickListener(this);
        mTextViewSave.setOnClickListener(this);

        mCheckBoxSunday.setOnCheckedChangeListener(this);
        mCheckBoxMonday.setOnCheckedChangeListener(this);
        mCheckBoxTuesday.setOnCheckedChangeListener(this);
        mCheckBoxWednesday.setOnCheckedChangeListener(this);
        mCheckBoxThurday.setOnCheckedChangeListener(this);
        mCheckBoxFriday.setOnCheckedChangeListener(this);
        mCheckBoxSaturday.setOnCheckedChangeListener(this);

        mDialogSpinnerCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mIntCountrySelection = position;
                if (position != 0) {
                    mStringCountryID = mCountryDataParser.getData().get(position - 1).getCountry_id();
                    mBackProcessGetSettings = new BackProcessGetSettings();
                    mBackProcessGetSettings.execute(mMethodGetCity);
                } else {
                    mStringCountryID = "";
                    setCityData(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mDialogSpinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mIntCitySelection = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mCheckBoxAccept.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    showPrivacyPolicyDialog();
            }
        });

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
        mWebView.loadUrl(mActivity.getWebMethod().mStringURLTerms + mActivity.getMyApplication().getCurrentLang());
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
     * Method will display set country data.
     */
    public void  setCountryData(boolean isNull) {

        if(isNull)
        {
            String[] mStringsMinPrice;
            mStringsMinPrice = new String[1];
            mStringsMinPrice[0] = getString(R.string.hint_select_country);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(mActivity, R.layout.row_spinner_selected, mStringsMinPrice);
            adapter.setDropDownViewResource(R.layout.row_spinner_text);
            mDialogSpinnerCountry.setAdapter(adapter);

        }else
        {
            String[] mStringsMinPrice;
            mIntCountrySelection = 0;
            int size = mCountryDataParser.getData().size();
            mStringsMinPrice = new String[(size) + 1];
            mStringsMinPrice[0] = getString(R.string.hint_select_country);
            for (int i = 0; i < mCountryDataParser.getData().size(); i++) {
                if(mActivity.getMyApplication().getCurrentLang().equalsIgnoreCase("en"))
                    mStringsMinPrice[i + 1] = mCountryDataParser.getData().get(i).getCountry_name();
                else
                    mStringsMinPrice[i + 1] = mCountryDataParser.getData().get(i).getCountry_namearebic();

                String mStringCountryID = mActivity.getMyApplication().getUserProfile().getData().getUser_country();
                if(mStringCountryID.length()>0 && mStringCountryID.equalsIgnoreCase(mCountryDataParser.getData().get(i).getCountry_id()))
                {
                    mIntCountrySelection = i + 1;
                }else {
                    if (mStringCurrentCountry.equalsIgnoreCase(mCountryDataParser.getData().get(i).getCountry_name()))
                        mIntCountrySelection = i + 1;
                }
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(mActivity, R.layout.row_spinner_selected, mStringsMinPrice);
            adapter.setDropDownViewResource(R.layout.row_spinner_text);
            mDialogSpinnerCountry.setAdapter(adapter);
            mDialogSpinnerCountry.setSelection(mIntCountrySelection);
        }
    }


    /**
     * Method will display set city data
     */
    public void setCityData(boolean isNull) {

        if (isNull) {
            String[] mStringsMinPrice;
            mStringsMinPrice = new String[1];
            mStringsMinPrice[0] = getString(R.string.hint_select_city);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(mActivity, R.layout.row_spinner_selected, mStringsMinPrice);
            adapter.setDropDownViewResource(R.layout.row_spinner_text);
            mDialogSpinnerCity.setAdapter(adapter);
            mDialogSpinnerCity.setSelection(0);
        } else {
            String[] mStringsMinPrice;
            int size = mCityDataParser.getData().size();
            mStringsMinPrice = new String[(size) + 1];
            mStringsMinPrice[0] = getString(R.string.hint_select_city);
            mIntCitySelection =0;
            for (int i = 0; i < mCityDataParser.getData().size(); i++) {

                if(mActivity.getMyApplication().getCurrentLang().equalsIgnoreCase("en"))
                    mStringsMinPrice[i + 1] = mCityDataParser.getData().get(i).getCity_name();
                else
                    mStringsMinPrice[i + 1] = mCityDataParser.getData().get(i).getCity_namearabic();

                if(mStringCityID.length() > 0 && mStringCityID.equalsIgnoreCase(mCityDataParser.getData().get(i).getCity_id()))
                {
                    mIntCitySelection = i + 1;
                }else {
                    if (mCityDataParser.getData().get(i).getCity_selected().equalsIgnoreCase("true"))
                        mIntCitySelection = i + 1;
                }

            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(mActivity, R.layout.row_spinner_selected, mStringsMinPrice);
            adapter.setDropDownViewResource(R.layout.row_spinner_text);
            mDialogSpinnerCity.setAdapter(adapter);
            mDialogSpinnerCity.setSelection(mIntCitySelection);
        }

    }

    @Override
    public void onPause() {
        storeScheduleData();
        super.onResume();
    }

    /**
     * Method call OnClick Event fire.
     */
    @Override
    public void onClick(View v) {

        if (v == mTextViewMyPlace) {

            mStringLocationPolicy = "provider";
            setMyLocationPolicy();
            mTextViewMyPlace.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mActivity, R.drawable.icon_radio_btn_selected), null, null, null);
            mRelativeLayoutTransFees.setVisibility(View.GONE);

        } else if (v == mTextViewSeekerPlace) {

            mStringLocationPolicy = "seeker";
            setMyLocationPolicy();
            mTextViewSeekerPlace.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mActivity, R.drawable.icon_radio_btn_selected), null, null, null);
            mTextViewAddress.setVisibility(View.GONE);
            mImageViewPicLocation.setVisibility(View.GONE);
            mRelativeLayoutTransFees.setVisibility(View.VISIBLE);

        } else if (v == mTextViewBoth) {

            mStringLocationPolicy = "both";
            setMyLocationPolicy();
            mTextViewBoth.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mActivity, R.drawable.icon_radio_btn_selected), null, null, null);
            mRelativeLayoutTransFees.setVisibility(View.VISIBLE);

        } else if (v == mImageViewPicLocation) {

//            mActivity.replaceFragment(new ProviderLocationMapViewFragment(), true);

            Intent mIntent = new Intent(mActivity, ProviderLocationMapViewFragment.class);
            mIntent.putExtra(getString(R.string.bundle_from), getString(R.string.bundle_from_appointment));
            if (mStringLocation.length() > 0) {
                 mIntent.putExtra(getString(R.string.bundle_city), "");
                mIntent.putExtra(getString(R.string.bundle_country), "");
                mIntent.putExtra(getString(R.string.bundle_lat), mStringLatitude);
                mIntent.putExtra(getString(R.string.bundle_lng), mStringLongitude);
            }
            startActivityForResult(mIntent, StaticData.REQUEST_MAP_CODE);

        } else if (v == mTextViewAcceptableRating) {

            showRatingDialog();

        } else if (v == mTextViewAutoAcceptable) {

            showAutoAcceptDialog();

        } else if (v == mTextViewSunStartTime) {

            new CustomTimePickerFragment(mTextViewSunStartTime, mActivity).setTimeListener(this, true);


        } else if (v == mTextViewSunEndTime) {

            new CustomTimePickerFragment(mTextViewSunEndTime, mActivity).setTimeListener(this, false);

        } else if (v == mTextViewMonStartTime) {

            new CustomTimePickerFragment(mTextViewMonStartTime, mActivity).setTimeListener(this, true);

        } else if (v == mTextViewMonEndTime) {

            new CustomTimePickerFragment(mTextViewMonEndTime, mActivity).setTimeListener(this, false);

        } else if (v == mTextViewTuesStartTime) {

            new CustomTimePickerFragment(mTextViewTuesStartTime, mActivity).setTimeListener(this, true);

        } else if (v == mTextViewTuesEndTime) {

            new CustomTimePickerFragment(mTextViewTuesEndTime, mActivity).setTimeListener(this, false);

        } else if (v == mTextViewWedStartTime) {

            new CustomTimePickerFragment(mTextViewWedStartTime, mActivity).setTimeListener(this, true);

        } else if (v == mTextViewWedEndTime) {

            new CustomTimePickerFragment(mTextViewWedEndTime, mActivity).setTimeListener(this, false);

        } else if (v == mTextViewThurStartTime) {

            new CustomTimePickerFragment(mTextViewThurStartTime, mActivity).setTimeListener(this, true);

        } else if (v == mTextViewThurEndTime) {

            new CustomTimePickerFragment(mTextViewThurEndTime, mActivity).setTimeListener(this, false);

        } else if (v == mTextViewFriStartTime) {

            new CustomTimePickerFragment(mTextViewFriStartTime, mActivity).setTimeListener(this, true);

        } else if (v == mTextViewFriEndTime) {

            new CustomTimePickerFragment(mTextViewFriEndTime, mActivity).setTimeListener(this, false);

        } else if (v == mTextViewSatStartTime) {

            new CustomTimePickerFragment(mTextViewSatStartTime, mActivity).setTimeListener(this, true);

        } else if (v == mTextViewSatEndTime) {

            new CustomTimePickerFragment(mTextViewSatEndTime, mActivity).setTimeListener(this, false);

        } else if (v == mTextViewSave) {

            saveData();

        }


    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked)
            buttonView.setTextColor(ContextCompat.getColor(mActivity, R.color.myPrimaryColor));
        else
            buttonView.setTextColor(ContextCompat.getColor(mActivity, R.color.white_secondary));

        if (buttonView == mCheckBoxMonday) {
            if (!isChecked) {
                mStringMonStart = "00:00";
                mStringMonEnd = "00:00";
                mTextViewMonStartTime.setTextColor(ContextCompat.getColor(mActivity, R.color.white_secondary));
                mTextViewMonEndTime.setTextColor(ContextCompat.getColor(mActivity, R.color.white_secondary));
                mTextViewMonStartTime.setText(R.string.lbl_start_time);
                mTextViewMonEndTime.setText(R.string.lbl_end_time);
            }else
            {
//                mStringMonStart = mStringMonStart.equalsIgnoreCase("00:00") ? mStringStart : mStringMonStart;
//                mStringMonEnd = mStringMonEnd.equalsIgnoreCase("00:00") ? mStringEnd : mStringMonEnd;
                mTextViewMonStartTime.setTextColor(ContextCompat.getColor(mActivity, R.color.myPrimaryColor));
                mTextViewMonEndTime.setTextColor(ContextCompat.getColor(mActivity, R.color.myPrimaryColor));
                setTimeValue(mStringStart, mStringMonStart, mTextViewMonStartTime);
                setTimeValue(mStringEnd, mStringMonEnd, mTextViewMonEndTime);
//                mTextViewMonStartTime.setText(mStringMonStart);
//                mTextViewMonEndTime.setText(mStringMonEnd);
            }
            mTextViewMonStartTime.setVisibility(isChecked ? View.VISIBLE : View.INVISIBLE);
            mTextViewMonEndTime.setVisibility(isChecked ? View.VISIBLE : View.INVISIBLE);
        } else if (buttonView == mCheckBoxTuesday) {
            if (!isChecked) {
                mStringTueStart = "00:00";
                mStringTueEnd = "00:00";
                mTextViewTuesStartTime.setTextColor(ContextCompat.getColor(mActivity, R.color.white_secondary));
                mTextViewTuesEndTime.setTextColor(ContextCompat.getColor(mActivity, R.color.white_secondary));
                mTextViewTuesStartTime.setText(R.string.lbl_start_time);
                mTextViewTuesEndTime.setText(R.string.lbl_end_time);
            }else
            {
//                mStringTueStart = mStringStart;
//                mStringTueEnd = mStringEnd;
                mTextViewTuesStartTime.setTextColor(ContextCompat.getColor(mActivity, R.color.myPrimaryColor));
                mTextViewTuesEndTime.setTextColor(ContextCompat.getColor(mActivity, R.color.myPrimaryColor));
                setTimeValue(mStringStart, mStringTueStart, mTextViewTuesStartTime);
                setTimeValue(mStringEnd, mStringTueEnd, mTextViewTuesEndTime);
//                mTextViewTuesStartTime.setText(mStringStart);
//                mTextViewTuesEndTime.setText(mStringEnd);
            }
            mTextViewTuesStartTime.setVisibility(isChecked ? View.VISIBLE : View.INVISIBLE);
            mTextViewTuesEndTime.setVisibility(isChecked ? View.VISIBLE : View.INVISIBLE);
        } else if (buttonView == mCheckBoxWednesday) {
            if (!isChecked) {
                mStringWedStart = "00:00";
                mStringWedEnd = "00:00";
                mTextViewWedStartTime.setTextColor(ContextCompat.getColor(mActivity, R.color.white_secondary));
                mTextViewWedEndTime.setTextColor(ContextCompat.getColor(mActivity, R.color.white_secondary));
                mTextViewWedStartTime.setText(R.string.lbl_start_time);
                mTextViewWedEndTime.setText(R.string.lbl_end_time);
            }else
            {
//                mStringWedStart = mStringStart;
//                mStringWedEnd = mStringEnd;
                mTextViewWedStartTime.setTextColor(ContextCompat.getColor(mActivity, R.color.myPrimaryColor));
                mTextViewWedEndTime.setTextColor(ContextCompat.getColor(mActivity, R.color.myPrimaryColor));
//                mTextViewWedStartTime.setText(mStringStart);
//                mTextViewWedEndTime.setText(mStringEnd);
                setTimeValue(mStringStart, mStringWedStart, mTextViewWedStartTime);
                setTimeValue(mStringEnd, mStringWedEnd, mTextViewWedEndTime);
            }
            mTextViewWedStartTime.setVisibility(isChecked ? View.VISIBLE : View.INVISIBLE);
            mTextViewWedEndTime.setVisibility(isChecked ? View.VISIBLE : View.INVISIBLE);
        } else if (buttonView == mCheckBoxThurday) {
            if (!isChecked) {
                mStringThuStart = "00:00";
                mStringThuEnd = "00:00";
                mTextViewThurStartTime.setTextColor(ContextCompat.getColor(mActivity, R.color.white_secondary));
                mTextViewThurEndTime.setTextColor(ContextCompat.getColor(mActivity, R.color.white_secondary));
                mTextViewThurStartTime.setText(R.string.lbl_start_time);
                mTextViewThurEndTime.setText(R.string.lbl_end_time);
            }else
            {
//                mStringThuStart = mStringStart;
//                mStringThuEnd = mStringEnd;
                mTextViewThurStartTime.setTextColor(ContextCompat.getColor(mActivity, R.color.myPrimaryColor));
                mTextViewThurEndTime.setTextColor(ContextCompat.getColor(mActivity, R.color.myPrimaryColor));
                setTimeValue(mStringStart, mStringThuStart, mTextViewThurStartTime);
                setTimeValue(mStringEnd, mStringThuEnd, mTextViewThurEndTime);
//                mTextViewThurStartTime.setText(mStringStart);
//                mTextViewThurEndTime.setText(mStringEnd);
            }
            mTextViewThurStartTime.setVisibility(isChecked ? View.VISIBLE : View.INVISIBLE);
            mTextViewThurEndTime.setVisibility(isChecked ? View.VISIBLE : View.INVISIBLE);
        } else if (buttonView == mCheckBoxFriday) {
            if (!isChecked) {
                mStringFriStart = "00:00";
                mStringFriEnd = "00:00";
                mTextViewFriStartTime.setTextColor(ContextCompat.getColor(mActivity, R.color.white_secondary));
                mTextViewFriEndTime.setTextColor(ContextCompat.getColor(mActivity, R.color.white_secondary));
                mTextViewFriStartTime.setText(R.string.lbl_start_time);
                mTextViewFriEndTime.setText(R.string.lbl_end_time);
            }else
            {
//                mStringFriStart = mStringStart;
//                mStringFriEnd = mStringEnd;
                mTextViewFriStartTime.setTextColor(ContextCompat.getColor(mActivity, R.color.myPrimaryColor));
                mTextViewFriEndTime.setTextColor(ContextCompat.getColor(mActivity, R.color.myPrimaryColor));
//                mTextViewFriStartTime.setText(mStringStart);
//                mTextViewFriEndTime.setText(mStringEnd);
                setTimeValue(mStringStart, mStringFriStart, mTextViewFriStartTime);
                setTimeValue(mStringEnd, mStringFriEnd, mTextViewFriEndTime);
            }
            mTextViewFriStartTime.setVisibility(isChecked ? View.VISIBLE : View.INVISIBLE);
            mTextViewFriEndTime.setVisibility(isChecked ? View.VISIBLE : View.INVISIBLE);
        } else if (buttonView == mCheckBoxSaturday) {
            if (!isChecked) {
                mStringSatStart = "00:00";
                mStringSatEnd = "00:00";
                mTextViewSatStartTime.setTextColor(ContextCompat.getColor(mActivity, R.color.white_secondary));
                mTextViewSatEndTime.setTextColor(ContextCompat.getColor(mActivity, R.color.white_secondary));
                mTextViewSatStartTime.setText(R.string.lbl_start_time);
                mTextViewSatEndTime.setText(R.string.lbl_end_time);
            }else
            {
//                mStringSatStart = mStringStart;
//                mStringSatEnd = mStringEnd;
                mTextViewSatStartTime.setTextColor(ContextCompat.getColor(mActivity, R.color.myPrimaryColor));
                mTextViewSatEndTime.setTextColor(ContextCompat.getColor(mActivity, R.color.myPrimaryColor));
                setTimeValue(mStringStart, mStringSatStart, mTextViewSatStartTime);
                setTimeValue(mStringEnd, mStringSatEnd, mTextViewSatEndTime);
//                mTextViewSatStartTime.setText(mStringStart);
//                mTextViewSatEndTime.setText(mStringEnd);
            }
            mTextViewSatStartTime.setVisibility(isChecked ? View.VISIBLE : View.INVISIBLE);
            mTextViewSatEndTime.setVisibility(isChecked ? View.VISIBLE : View.INVISIBLE);
        } else if (buttonView == mCheckBoxSunday) {
            if (!isChecked) {
                mStringSunStart = "00:00";
                mStringSunEnd = "00:00";
                mTextViewSunStartTime.setTextColor(ContextCompat.getColor(mActivity, R.color.white_secondary));
                mTextViewSunEndTime.setTextColor(ContextCompat.getColor(mActivity, R.color.white_secondary));
                mTextViewSunStartTime.setText(R.string.lbl_start_time);
                mTextViewSunEndTime.setText(R.string.lbl_end_time);
            }else
            {
//                mStringSunStart = mStringStart;
//                mStringSunEnd = mStringEnd;
                mTextViewSunStartTime.setTextColor(ContextCompat.getColor(mActivity, R.color.myPrimaryColor));
                mTextViewSunEndTime.setTextColor(ContextCompat.getColor(mActivity, R.color.myPrimaryColor));
                setTimeValue(mStringStart, mStringSunStart, mTextViewSunStartTime);
                setTimeValue(mStringEnd, mStringSunEnd, mTextViewSunEndTime);
//                mTextViewSunStartTime.setText(mStringStart);
//                mTextViewSunEndTime.setText(mStringEnd);
            }
            mTextViewSunStartTime.setVisibility(isChecked ? View.VISIBLE : View.INVISIBLE);
            mTextViewSunEndTime.setVisibility(isChecked ? View.VISIBLE : View.INVISIBLE);
        }
    }

    public void setTimeValue(String default_time, String real_time, TextView mTextViewDay)
    {
        if(real_time.equalsIgnoreCase("00:00"))
        {
            real_time = default_time;
        }
        if(real_time.equalsIgnoreCase(getString(R.string.lbl_end_time)) || real_time.equalsIgnoreCase(getString(R.string.lbl_start_time)))
            mTextViewDay.setTextColor(ContextCompat.getColor(mActivity, R.color.white_secondary));
        mTextViewDay.setText(real_time);
    }

    /**
     * Method call will set location selection..
     */
    private void setMyLocationPolicy() {
        mTextViewMyPlace.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mActivity, R.drawable.icon_radio_btn_unselect), null, null, null);
        mTextViewSeekerPlace.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mActivity, R.drawable.icon_radio_btn_unselect), null, null, null);
        mTextViewBoth.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mActivity, R.drawable.icon_radio_btn_unselect), null, null, null);
        mTextViewAddress.setVisibility(View.VISIBLE);
        mImageViewPicLocation.setVisibility(View.VISIBLE);
    }


    public void storeScheduleData() {
        mStringMonStart = mTextViewMonStartTime.getText().toString().equalsIgnoreCase(getString(R.string.lbl_start_time)) ? "00:00" : mTextViewMonStartTime.getText().toString();
        mStringMonEnd = mTextViewMonEndTime.getText().toString().equalsIgnoreCase(getString(R.string.lbl_end_time)) ? "00:00" : mTextViewMonEndTime.getText().toString();
        mStringTueStart = mTextViewTuesStartTime.getText().toString().equalsIgnoreCase(getString(R.string.lbl_start_time)) ? "00:00" : mTextViewTuesStartTime.getText().toString();
        mStringTueEnd = mTextViewTuesEndTime.getText().toString().equalsIgnoreCase(getString(R.string.lbl_end_time)) ? "00:00" : mTextViewTuesEndTime.getText().toString();
        mStringWedStart = mTextViewWedStartTime.getText().toString().equalsIgnoreCase(getString(R.string.lbl_start_time)) ? "00:00" : mTextViewWedStartTime.getText().toString();
        mStringWedEnd = mTextViewWedEndTime.getText().toString().equalsIgnoreCase(getString(R.string.lbl_end_time)) ? "00:00" : mTextViewWedEndTime.getText().toString();
        mStringThuStart = mTextViewThurStartTime.getText().toString().equalsIgnoreCase(getString(R.string.lbl_start_time)) ? "00:00" : mTextViewThurStartTime.getText().toString();
        mStringThuEnd = mTextViewThurEndTime.getText().toString().equalsIgnoreCase(getString(R.string.lbl_end_time)) ? "00:00" : mTextViewThurEndTime.getText().toString();
        mStringFriStart = mTextViewFriStartTime.getText().toString().equalsIgnoreCase(getString(R.string.lbl_start_time)) ? "00:00" : mTextViewFriStartTime.getText().toString();
        mStringFriEnd = mTextViewFriEndTime.getText().toString().equalsIgnoreCase(getString(R.string.lbl_end_time)) ? "00:00" : mTextViewFriEndTime.getText().toString();
        mStringSatStart = mTextViewSatStartTime.getText().toString().equalsIgnoreCase(getString(R.string.lbl_start_time)) ? "00:00" : mTextViewSatStartTime.getText().toString();
        mStringSatEnd = mTextViewSatEndTime.getText().toString().equalsIgnoreCase(getString(R.string.lbl_end_time)) ? "00:00" : mTextViewSatEndTime.getText().toString();
        mStringSunStart = mTextViewSunStartTime.getText().toString().equalsIgnoreCase(getString(R.string.lbl_start_time)) ? "00:00" : mTextViewSunStartTime.getText().toString();
        mStringSunEnd = mTextViewSunEndTime.getText().toString().equalsIgnoreCase(getString(R.string.lbl_end_time)) ? "00:00" : mTextViewSunEndTime.getText().toString();
    }

    public void saveData() {
//        mStringLocation = mActivity.getMyApplication().getLocationAddress();
//        mStringCity = mActivity.getMyApplication().getLocationCity();
//        mStringCountry = mActivity.getMyApplication().getLocationCountry();

//        if(mActivity.getMyApplication().getLatLngAddLocation()!=null) {
//            mStringLatitude = String.valueOf(mActivity.getMyApplication().getLatLngAddLocation().latitude);
//            mStringLongitude = String.valueOf(mActivity.getMyApplication().getLatLngAddLocation().longitude);
        if (mDialogSpinnerCountry.getSelectedItemPosition() == 0) {
            mActivity.getAppAlertDialog().showDialog("", getString(R.string.validation_country), false);
            return;
        }
        if (mDialogSpinnerCity.getSelectedItemPosition() == 0) {
            mActivity.getAppAlertDialog().showDialog("", getString(R.string.validation_city), false);
            return;
        }

        mStringCountryID = mCountryDataParser.getData().get(mIntCountrySelection-1).getCountry_id();
        mStringCityID = mCityDataParser.getData().get(mIntCitySelection-1).getCity_id();


        if (mStringLocationPolicy.equalsIgnoreCase("seeker")) {
            mStringLatitude = mCityDataParser.getData().get(mIntCitySelection - 1).getCity_lat();
            mStringLongitude = mCityDataParser.getData().get(mIntCitySelection - 1).getCity_lng();
            mStringLocation = "";
        } else {
            if (mStringLocation.length()==0 || mStringLocation.equalsIgnoreCase(getString(R.string.lbl_no_address))) {
                mActivity.getAppAlertDialog().showDialog("", getString(R.string.validation_location), false);
                return;
            }
        }

        mStringTransFees = mEditTextTransFees.getText().toString().trim();
        if(mStringTransFees.length() == 0)
        {
            mActivity.getAppAlertDialog().showDialog("", getString(R.string.validation_trans_fees), false);
            return;
        }

        mStringMinFees = mEditTextMinFees.getText().toString().trim();
        if(mStringMinFees.length() == 0)
        {
            mActivity.getAppAlertDialog().showDialog("", getString(R.string.validation_min_apt_fees), false);
            return;
        }

//        mStringMinValue = mRrangeSeekBarDay.getSelectedMinValue().toString();
//        mStringMaxValue = mRrangeSeekBarDay.getSelectedMaxValue().toString();

        storeScheduleData();

        if (!mCheckBoxMonday.isChecked() && !mCheckBoxTuesday.isChecked() && !mCheckBoxWednesday.isChecked()
                && !mCheckBoxThurday.isChecked() && !mCheckBoxFriday.isChecked() && !mCheckBoxSaturday.isChecked()
                && !mCheckBoxSunday.isChecked()) {
            mActivity.getAppAlertDialog().showDialog("", getString(R.string.validation_schedule_day), false);
        } else {
            if (validateSelectkField(mCheckBoxSunday, mTextViewSunStartTime, mTextViewSunEndTime)
                    && validateSelectkField(mCheckBoxMonday, mTextViewMonStartTime, mTextViewMonEndTime)
                    && validateSelectkField(mCheckBoxTuesday, mTextViewTuesStartTime, mTextViewTuesEndTime)
                    && validateSelectkField(mCheckBoxWednesday, mTextViewWedStartTime, mTextViewWedEndTime)
                    && validateSelectkField(mCheckBoxThurday, mTextViewThurStartTime, mTextViewThurEndTime)
                    && validateSelectkField(mCheckBoxFriday, mTextViewFriStartTime, mTextViewFriEndTime)
                    && validateSelectkField(mCheckBoxSaturday, mTextViewSatStartTime, mTextViewSatEndTime)) {
                if (Integer.valueOf(mStringMaxValue) > Integer.valueOf(mStringMinValue)) {

                    if(!mCheckBoxAccept.isChecked())
                    {
                        mActivity.getAppAlertDialog().showDialog(getString(R.string.app_name), getString(R.string.validation_terms_conidtion), false);
                        return ;
                    }

                    mBackProcessGetSettings = new BackProcessGetSettings();
                    mBackProcessGetSettings.execute(mMethodSetSettings);
                } else {
                    mActivity.getAppAlertDialog().showDialog("", getString(R.string.validation_min_max_day), false);
                }
            }

        }


    }

    public boolean validateSelectkField(CheckBox mCheckBox, final TextView startTime, final TextView endTime) {
        boolean status = true;
        String message = "";
        String mStringStartTime = startTime.getText().toString();
        String mStringEndTime = endTime.getText().toString();

        if (mCheckBox.isChecked()) {
            if (mStringStartTime.equalsIgnoreCase(getString(R.string.lbl_start_time))) {
                status = false;
                message = getString(R.string.validation_start_time, mCheckBox.getText());
            } else if (mStringEndTime.equalsIgnoreCase(getString(R.string.lbl_end_time))) {
                status = false;
                message = getString(R.string.validation_end_time, mCheckBox.getText());
            } else {
                Long mLongStartTime = Long.parseLong(mStringStartTime.replaceAll(":", ""));
                Long mLongEndTime = Long.parseLong(mStringEndTime.replaceAll(":", ""));
                if (mLongEndTime < mLongStartTime) {
                    status = false;
                    message = getString(R.string.validation_end_time_grater, mCheckBox.getText());
                }
            }
        }

        if (!status) {
            // CommanMethod.showToast(context, msg);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mActivity);
            alertDialogBuilder.setTitle(R.string.app_name);
            alertDialogBuilder.setMessage(message).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
        return status;
    }


    /**
     * Method will set data
     */
    private void setData() {

        setMyLocationPolicy();
        setCountryData(true);
//        setCityData(true);

        if (mActivity.getMyApplication().getUserProfile().getData() != null && mStringLocationPolicy.length() == 0) {
            mStringLocation = mActivity.getMyApplication().getUserProfile().getData().getUser_location();
            mStringLocationPolicy = mActivity.getMyApplication().getUserProfile().getData().getLocation_policy();
            mStringCityID = mActivity.getMyApplication().getUserProfile().getData().getUser_city();
            mStringCountryID = mActivity.getMyApplication().getUserProfile().getData().getUser_country();
            mStringLatitude = mActivity.getMyApplication().getUserProfile().getData().getUser_lat();
            mStringLongitude = mActivity.getMyApplication().getUserProfile().getData().getUser_lng();
            mStringSatisfyRating = mActivity.getMyApplication().getUserProfile().getData().getUser_lowestsatisfy();
            mStringCommitedRating = mActivity.getMyApplication().getUserProfile().getData().getUser_lowestcommited();

            mStringMinValue = mActivity.getMyApplication().getUserProfile().getData().getUser_mindays();
            mStringMaxValue = mActivity.getMyApplication().getUserProfile().getData().getUser_maxdays();

            try {
                mRrangeSeekBarDay.setSelectedMinValue(Integer.parseInt(mStringMinValue));
                mRrangeSeekBarDay.setSelectedMaxValue(Integer.parseInt(mStringMaxValue));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            //Assign value
            mStringMinFees = mActivity.getMyApplication().getUserProfile().getData().getUser_minapptfee();
            mStringTransFees = mActivity.getMyApplication().getUserProfile().getData().getUser_transportfee();
            mEditTextMinFees.setText(mStringMinFees);
            mEditTextTransFees.setText(mStringTransFees);

//            mActivity.getMyApplication().setLocationAddress(mStringLocation, mStringCity, mStringCountry);

//            if(mStringLatitude.length() > 0 && (mStringLongitude.length() > 0 &&
//                    !mStringLatitude.equalsIgnoreCase("0.0000") && !mStringLongitude.equalsIgnoreCase("0.0000")))
//            {
//                mActivity.getMyApplication().setLatLngAddLocation(new LatLng(Double.parseDouble(mStringLatitude), Double.parseDouble(mStringLongitude)));
//            }

            if (mActivity.getMyApplication().getUserProfile().getData().getSchedule() != null &&
                    mActivity.getMyApplication().getUserProfile().getData().getSchedule().size() > 0) {
                for (int i = 0; i < mActivity.getMyApplication().getUserProfile().getData().getSchedule().size(); i++) {
                    String day = mActivity.getMyApplication().getUserProfile().getData().getSchedule().get(i).getDay();
                    String s_time = mActivity.getMyApplication().getUserProfile().getData().getSchedule().get(i).getStart_time();
                    String e_time = mActivity.getMyApplication().getUserProfile().getData().getSchedule().get(i).getEnd_time();
                    if (day.equalsIgnoreCase("mon")) {
                        mStringMonStart = s_time;
                        mStringMonEnd = e_time;

                    } else if (day.equalsIgnoreCase("tue")) {
                        mStringTueStart = s_time;
                        mStringTueEnd = e_time;
                    } else if (day.equalsIgnoreCase("wed")) {
                        mStringWedStart = s_time;
                        mStringWedEnd = e_time;
                    } else if (day.equalsIgnoreCase("thu")) {
                        mStringThuStart = s_time;
                        mStringThuEnd = e_time;
                    } else if (day.equalsIgnoreCase("fri")) {
                        mStringFriStart = s_time;
                        mStringFriEnd = e_time;
                    } else if (day.equalsIgnoreCase("sat")) {
                        mStringSatStart = s_time;
                        mStringSatEnd = e_time;
                    } else if (day.equalsIgnoreCase("sun")) {
                        mStringSunStart = s_time;
                        mStringSunEnd = e_time;
                    }
                }
            }
        }


        //Set Schedule data
        setScheduleTimeOnUI(mStringMonStart, mTextViewMonStartTime);
        setScheduleTimeOnUI(mStringMonEnd, mTextViewMonEndTime);
        if (!mStringMonStart.equalsIgnoreCase("00:00") || !mStringMonEnd.equalsIgnoreCase("00:00")) {
            mCheckBoxMonday.setChecked(true);
            mCheckBoxMonday.setTextColor(ContextCompat.getColor(mActivity, R.color.myPrimaryColor));
        }
        setScheduleTimeOnUI(mStringTueStart, mTextViewTuesStartTime);
        setScheduleTimeOnUI(mStringTueEnd, mTextViewTuesEndTime);
        if (!mStringTueStart.equalsIgnoreCase("00:00") || !mStringTueEnd.equalsIgnoreCase("00:00")) {
            mCheckBoxTuesday.setChecked(true);
            mCheckBoxTuesday.setTextColor(ContextCompat.getColor(mActivity, R.color.myPrimaryColor));
        }
        setScheduleTimeOnUI(mStringWedStart, mTextViewWedStartTime);
        setScheduleTimeOnUI(mStringWedEnd, mTextViewWedEndTime);
        if (!mStringWedStart.equalsIgnoreCase("00:00") || !mStringWedEnd.equalsIgnoreCase("00:00")) {
            mCheckBoxWednesday.setChecked(true);
            mCheckBoxWednesday.setTextColor(ContextCompat.getColor(mActivity, R.color.myPrimaryColor));
        }
        setScheduleTimeOnUI(mStringThuStart, mTextViewThurStartTime);
        setScheduleTimeOnUI(mStringThuEnd, mTextViewThurEndTime);
        if (!mStringThuStart.equalsIgnoreCase("00:00") || !mStringThuEnd.equalsIgnoreCase("00:00")) {
            mCheckBoxThurday.setChecked(true);
            mCheckBoxThurday.setTextColor(ContextCompat.getColor(mActivity, R.color.myPrimaryColor));
        }
        setScheduleTimeOnUI(mStringFriStart, mTextViewFriStartTime);
        setScheduleTimeOnUI(mStringFriEnd, mTextViewFriEndTime);
        if (!mStringFriStart.equalsIgnoreCase("00:00") || !mStringFriEnd.equalsIgnoreCase("00:00")) {
            mCheckBoxFriday.setChecked(true);
            mCheckBoxFriday.setTextColor(ContextCompat.getColor(mActivity, R.color.myPrimaryColor));
        }
        setScheduleTimeOnUI(mStringSatStart, mTextViewSatStartTime);
        setScheduleTimeOnUI(mStringSatEnd, mTextViewSatEndTime);
        if (!mStringSatStart.equalsIgnoreCase("00:00") || !mStringSatEnd.equalsIgnoreCase("00:00")) {
            mCheckBoxSaturday.setChecked(true);
            mCheckBoxSaturday.setTextColor(ContextCompat.getColor(mActivity, R.color.myPrimaryColor));
        }
        setScheduleTimeOnUI(mStringSunStart, mTextViewSunStartTime);
        setScheduleTimeOnUI(mStringSunEnd, mTextViewSunEndTime);
        if (!mStringSunStart.equalsIgnoreCase("00:00") || !mStringSunEnd.equalsIgnoreCase("00:00")) {
            mCheckBoxSunday.setChecked(true);
            mCheckBoxSunday.setTextColor(ContextCompat.getColor(mActivity, R.color.myPrimaryColor));
        }

        //Set Location Policy
        if (mStringLocationPolicy.equalsIgnoreCase("both")) {
            mTextViewBoth.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mActivity, R.drawable.icon_radio_btn_selected), null, null, null);
            mRelativeLayoutTransFees.setVisibility(View.VISIBLE);
        } else if (mStringLocationPolicy.equalsIgnoreCase("seeker")) {
            mTextViewSeekerPlace.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mActivity, R.drawable.icon_radio_btn_selected), null, null, null);
            mTextViewAddress.setVisibility(View.GONE);
            mImageViewPicLocation.setVisibility(View.GONE);
            mRelativeLayoutTransFees.setVisibility(View.VISIBLE);

        } else if (mStringLocationPolicy.equalsIgnoreCase("provider")) {
            mTextViewMyPlace.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mActivity, R.drawable.icon_radio_btn_selected), null, null, null);
            mRelativeLayoutTransFees.setVisibility(View.GONE);
        }

//        mStringLocation = mActivity.getMyApplication().getLocationAddress();
        if (mStringLocation.length() > 0) {
            mTextViewAddress.setText(mStringLocation);
//            mActivity.getMyApplication().setLocationAddress(mStringLocation, mStringCity, mStringCountry);
        } else
            mTextViewAddress.setText(R.string.lbl_no_address);

    }

    /**
     * Method will set time value on textview
     *
     * @param value
     * @param mTextView
     */
    public void setScheduleTimeOnUI(String value, TextView mTextView) {
        if (!value.equalsIgnoreCase("00:00")) {
            mTextView.setText(value);
            mTextView.setTextColor(ContextCompat.getColor(mActivity, R.color.myPrimaryColor));
        } else
            mTextView.setVisibility(View.INVISIBLE);
    }


    /**
     * Method call will show rating dialog
     */
    private void showRatingDialog() {
        mDialogRating = new Dialog(mActivity);
        mDialogRating.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialogRating.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialogRating.setContentView(R.layout.dialog_rating);
        Window window = mDialogRating.getWindow();
        window.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        RatingBar mRatingBarSatisfy = (RatingBar) mDialogRating.findViewById(R.id.view_rating_rating_satification);
        mRatingBarSatisfy.setRating(Float.valueOf(mStringSatisfyRating));
        mRatingBarSatisfy.setIsIndicator(false);
        mRatingBarSatisfy.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
//                if (fromUser) {
                mStringSatisfyRating = String.valueOf((int) rating);
                Log.d("Settings ", mStringSatisfyRating);
//                }
            }
        });
        RatingBar mRatingBarCommited = (RatingBar) mDialogRating.findViewById(R.id.view_rating_rating_commitment);
        mRatingBarCommited.setRating(Float.valueOf(mStringCommitedRating));
        mRatingBarCommited.setIsIndicator(false);
        mRatingBarCommited.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
//                if (fromUser) {
                mStringCommitedRating = String.valueOf((int) rating);
//                }
            }
        });

        TextView mTextViewDialogSubmit = (TextView) mDialogRating.findViewById(R.id.dialog_rating_submit);
        mTextViewDialogSubmit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialogRating.dismiss();
            }
        });
        mDialogRating.show();

        TextView mTextViewDialogSatisfy = (TextView) mDialogRating.findViewById(R.id.view_rating_textview_satification);
        TextView mTextViewDialogCommited = (TextView) mDialogRating.findViewById(R.id.view_rating_textview_commitment);
        mTextViewDialogSatisfy.setTextColor(ContextCompat.getColor(mActivity, R.color.myPrimaryColor));
        mTextViewDialogCommited.setTextColor(ContextCompat.getColor(mActivity, R.color.myPrimaryColor));
    }

    /**
     * Method will set min/max data into spinner
     * @param mSpinner Spinner object
     * @param isMin boolean value
     */
    public void setMinDaysData(Spinner mSpinner, boolean isMin)
    {
        int size = 0;
        int selection = 0;
        if(isMin)
            size = 30;
        else
            size = 12;
        String[] mStringsMinPrice = new String[size];
        for (int i=0 ; i < size ; i++)
        {
            mStringsMinPrice[i] = String.valueOf(i+1);
            if(isMin)
            {
                if(mStringMinValue.equalsIgnoreCase(String.valueOf(i+1)))
                    selection = i;
            }else
            {
                int max = Integer.parseInt(mStringMaxValue)/30;
                if( max == i+1)
                    selection = i;
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mActivity, R.layout.row_spinner_selected, mStringsMinPrice);
        adapter.setDropDownViewResource(R.layout.row_spinner_text);
        mSpinner.setAdapter(adapter);
        mSpinner.setSelection(selection);
    }

    /**
     * Method call will show rating dialog
     */
    private void showAutoAcceptDialog() {
        mDialogRating = new Dialog(mActivity);
        mDialogRating.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialogRating.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialogRating.setContentView(R.layout.dialog_auto_accept_days);
        Window window = mDialogRating.getWindow();
        window.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);

        final Spinner mSpinnerMin = (Spinner) mDialogRating.findViewById(R.id.dialog_auto_accept_days_spinner_min);
        setMinDaysData(mSpinnerMin, true);
        final Spinner mSpinnerMax = (Spinner) mDialogRating.findViewById(R.id.dialog_auto_accept_days_spinner_max);
        setMinDaysData(mSpinnerMax, false);
        TextView mTextViewDialogSubmit = (TextView) mDialogRating.findViewById(R.id.dialog_auto_accept_days_textview_save);
        mTextViewDialogSubmit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mStringMinValue = String.valueOf(mSpinnerMin.getSelectedItemPosition()+1);
                mStringMaxValue = String.valueOf((mSpinnerMax.getSelectedItemPosition()+1)*30);
                mDialogRating.dismiss();
            }
        });
        mDialogRating.show();

    }

    @Override
    public void onTimeSet(String time, boolean isStart) {
        if(isStart)
            mStringStart = time;
        else
            mStringEnd = time;
    }


    /**
     * AsyncTask for calling webservice in background.
     *
     * @author ebaraiya
     */
    public class BackProcessGetSettings extends AsyncTask<String, Void, String> {
        String mCurrentMethod = "";

        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(mActivity, "", getString(R.string.dialog_loading), true);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            mCurrentMethod = params[0];
            if (mCurrentMethod.equalsIgnoreCase(mMethodSetSettings)) {

                mLoginParserSettings = (LoginParser) mActivity.getWebMethod().callUpdateProviderSettings(
                        mActivity.getMyApplication().getUserFiled(getString(R.string.sp_user_id)), mActivity.getMyApplication().getUserFiled(getString(R.string.sp_user_hash)),
                        mStringLocation, mStringLocationPolicy,
                        mStringTransFees, mStringMinFees,
                        mStringCityID, mStringCountryID,
                        mStringLatitude, mStringLongitude,
                        mStringSatisfyRating, mStringCommitedRating,
                        mStringMonStart, mStringMonEnd,
                        mStringTueStart, mStringTueEnd,
                        mStringWedStart, mStringWedEnd,
                        mStringThuStart, mStringThuEnd,
                        mStringFriStart, mStringFriEnd,
                        mStringSatStart, mStringSatEnd,
                        mStringSunStart, mStringSunEnd,
                        mStringMinValue, mStringMaxValue,
                        mLoginParserSettings);
            } else if (mCurrentMethod.equalsIgnoreCase(mMethodGetCountry)) {
                mCountryDataParser = (CategoryListParser) mActivity.getWebMethod().callGetCountry(mCountryDataParser);

                Geocoder geoCoder = new Geocoder(mActivity);
                try {
                    List<Address> addresses = geoCoder.getFromLocation(Double.parseDouble(mActivity.getMyApplication().getCurrentLatitude()), Double.parseDouble(mActivity.getMyApplication().getCurrentLongitude()), 1);
                    if (addresses.size() > 0) {
                        mStringCurrentCountry = addresses.get(0).getCountryName();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (mCurrentMethod.equalsIgnoreCase(mMethodGetCity)) {
                mCityDataParser = (CategoryListParser) mActivity.getWebMethod().callGetCity(mStringCountryID,
                        mActivity.getMyApplication().getCurrentLatitude(),
                        mActivity.getMyApplication().getCurrentLongitude(),
                        mCityDataParser);
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

                    if (mCurrentMethod.equalsIgnoreCase(mMethodSetSettings)) {
                        if (mLoginParserSettings.getWs_status().equalsIgnoreCase("true") && mLoginParserSettings.getData() != null) {
                            mActivity.getMyApplication().setUserProfile(mLoginParserSettings);
                            mActivity.getAppAlertDialog().showDialog("", mLoginParserSettings.getMessage().toString(), false);
                            mActivity.updatedUserData();
                        } else {
                            if (mLoginParserSettings.getMessage().toString().contains(getString(R.string.alt_msg_exipred))) {
                                mActivity.getAppAlertDialog().showAlertWithSingleButton("", mLoginParserSettings.getMessage().toString(),
                                        mActivity.getString(R.string.lbl_logout),
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                mActivity.logout();
                                            }
                                        });
                            } else {
                                mActivity.getAppAlertDialog().showDialog("", mLoginParserSettings.getMessage().toString(), false);
                            }
                        }

                    } else if (mCurrentMethod.equalsIgnoreCase(mMethodGetCountry)) {
                        if (mCountryDataParser.getWs_status().equalsIgnoreCase("true") && mCountryDataParser.getData() != null) {
                            setCountryData(false);
                        } else {
                            setCountryData(true);
                        }
                    } else if (mCurrentMethod.equalsIgnoreCase(mMethodGetCity)) {
                        if (mCityDataParser.getWs_status().equalsIgnoreCase("true") && mCityDataParser.getData() != null) {
                            setCityData(false);
                        } else {
                            setCityData(true);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            super.onPostExecute(result);
        }
    }

    /**
     * Method call will user get picture from gallery or camera.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @SuppressWarnings("static-access")
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == StaticData.REQUEST_MAP_CODE) {

            if (resultCode == getActivity().RESULT_OK) {

                mStringLocation = data.getExtras().getString(getString(R.string.bundle_address));
                mStringCity = data.getExtras().getString(getString(R.string.bundle_city));
                mStringCountry = data.getExtras().getString(getString(R.string.bundle_country));

                mStringLatitude = data.getExtras().getString(getString(R.string.bundle_lat));
                mStringLongitude = data.getExtras().getString(getString(R.string.bundle_lng));

                mTextViewAddress.setText(mStringLocation);

            } else if (resultCode == getActivity().RESULT_CANCELED) {

            }

        }
    }
}


