package beautician.beauty.android.fragments;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import beautician.beauty.android.views.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import beautician.beauty.android.R;
import beautician.beauty.android.activities.MyFragmentActivity;
import beautician.beauty.android.parsers.CategoryDataParser;
import beautician.beauty.android.parsers.CategoryListParser;
import beautician.beauty.android.parsers.SearchResultParser;
import beautician.beauty.android.parsers.UserDataParser;
import beautician.beauty.android.utilities.CommonMethod;
import beautician.beauty.android.utilities.CustomDatePickerFragment;
import beautician.beauty.android.utilities.CustomTimePickerFragment;
import beautician.beauty.android.views.RangeSeekBar;

@SuppressLint("InflateParams")
public class SearchFragment extends Fragment implements OnClickListener{

    private MyFragmentActivity mActivity;
    private View rootView;
    private Dialog mDialogAdvanceSearch;
    private CommonMethod mCommonMethod;

    private ViewPager mViewPager;
    private GridView mGridView;
    private EditText mEditTextInput;
    private TextView mTextViewSearch;
    private TextView mTextViewAdvanceSearched;

    private NormalSearchResultAdapter mSearchResultAdapter;

    private ImageLoader mImageLoader;
    private DisplayImageOptions mDisplayImageOptions;


    private BackProcessGetSearchResult mBackProcessGetSearchResult;
    private ProgressDialog mProgressDialog;

    private String mMethodGetFeatureProvider = "GetFeatureProvider";
    private String mMethodGetSearchResult = "GetSearchResult";
    private String mMethodGetAdvanceSearchResult = "GetAdvanceSearchResult";
    private String mMethodGetCountry = "GetCountry";
    private String mMethodGetCity = "GetCity";

    private SearchResultParser mSearchResultParser;
    private List<UserDataParser> mListSearchResultFeature;
    private List<UserDataParser> mListSearchResultNormal;
    private CategoryListParser mCountryDataParser;
    private CategoryListParser mCityDataParser;

    private TextView mDialogSpinnerCategory;
    private Spinner mDialogSpinnerMinPrice;
    private Spinner mDialogSpinnerMaxPrice;

    private Spinner mDialogSpinnerCountry;
    private Spinner mDialogSpinnerCity;
    private SeekBar mSeekBarDistanceRange;
    private TextView mTextViewDistanceRange;
    private TextView mTextViewDistanceRangeDetail;
//    private EditText mDialogEditTextCity;
    private EditText mDialogEditTextServiceOrProviderName;
    private EditText mDialogEditTextMinPrice;
    private EditText mDialogEditTextMaxPrice;
    private TextView mDialogTextViewMyPlace;
    private TextView mDialogTextviewBoth;
    private TextView mDialogTextViewProviderPlace;
    private TextView mDialogTextViewDate;
    private TextView mDialogTextViewTime;
    private TextView mDialogTextViewSearch;
    private RatingBar mDialogRatingBarSatisfy;
    private RatingBar mDialogRatingBarCommited;
    RangeSeekBar mDialogRrangeSeekBar;

    private String mStringSearchValue = "";

    int mIntCountrySelection = 0;
    int mIntCitySelection = 0;

    private String mStringCategoryID = "";
    private String mStringCityID = "";
    private String mStringCountryID = "";
    String mStringCurrentCountry = "";
    private String mStringProvider = "";
    private String mStringLocationPolicy = "both";
    private String mStringMinValue = "";
    private String mStringMaxValue = "";
    private String mStringSatisfyRate = "";
    private String mStringCommitedRate = "";
    private String mStringLatitude = "";
    private String mStringLongitude = "";
    private String mStringDate = "";
    private String mStringTime = "";
    private String mStringDateTime = "";
    private int mIntDistanceRange = 100;
    private int mIntMaximumDistance = 100;


    private ViewPagerImageAdapter mSearchResultFeatureAdapter;
    private static long ANIM_VIEWPAGER_DELAY = 10000;
    private Runnable animateViewPager;
    private Handler mHandler = new Handler();
    private boolean isSearchResult = false;

    private boolean[] selected;
    CategoryAdapter adapter;

    public SearchFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_search, container, false);

        mActivity = (MyFragmentActivity) getActivity();
        mActivity.setHeaderTitle(R.string.lbl_search);
        mActivity.setSearchIcon(R.drawable.icon_filter);
        mActivity.onBackButton(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v);
            }
        });

        getWidgetRefrence(rootView);
        registerOnClick();

        mCommonMethod = new CommonMethod(mActivity);
        mDisplayImageOptions = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.bg_user_profile)
                .showImageOnFail(R.drawable.bg_user_profile)
                .cacheInMemory(true)
                .displayer(new RoundedBitmapDisplayer(20))
                .considerExifParams(true)
                .resetViewBeforeLoading(true)
                .cacheOnDisc(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(200))
                .build();

        mCountryDataParser = new CategoryListParser();
        mCityDataParser = new CategoryListParser();


        if (mListSearchResultFeature == null) {
            mListSearchResultFeature = new ArrayList<UserDataParser>();
            mListSearchResultNormal = new ArrayList<UserDataParser>();
            mSearchResultParser = new SearchResultParser();
            mBackProcessGetSearchResult = new BackProcessGetSearchResult();
            mBackProcessGetSearchResult.execute(mMethodGetFeatureProvider);

        } else {
            mSearchResultAdapter = new NormalSearchResultAdapter();
            mGridView.setAdapter(mSearchResultAdapter);
            loadFeatureResult();
        }

        if(!mActivity.getMyApplication().isAccountActive()) {
            mActivity.getAppAlertDialog().showDialog("", getString(R.string.alt_msg_disable_account), true);
        }

        return rootView;
    }


    /**
     * Method call will get IDs from xml file.
     *
     * @param v
     */
    private void getWidgetRefrence(View v) {
        mViewPager = (ViewPager) v.findViewById(R.id.fragment_search_viewpager);
        mGridView = (GridView) v.findViewById(R.id.fragment_search_gridview_result);
        mEditTextInput = (EditText) v.findViewById(R.id.fragment_search_edittext_input);
        mTextViewSearch = (TextView) v.findViewById(R.id.fragment_search_textview_search);
        mTextViewAdvanceSearched = (TextView) v.findViewById(R.id.fragment_search_textview_advanced_searched);


    }

    /**
     * Method call will Register OnClick() Events for widgets.
     */
    private void registerOnClick() {
        mTextViewSearch.setOnClickListener(this);
        mTextViewAdvanceSearched.setOnClickListener(this);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ProviderDetailsCategoryPagerFragment mProviderDetailsFragment = new ProviderDetailsCategoryPagerFragment();
                Bundle mBundle = new Bundle();
                mBundle.putString(getString(R.string.bundle_provider_id), mListSearchResultNormal.get(position).getUser_id());
                mProviderDetailsFragment.setArguments(mBundle);
                mActivity.replaceFragment(mProviderDetailsFragment, true);
            }
        });
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                runnable();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    /**
     * Method call OnClick Event fire.
     */
    @Override
    public void onClick(View v) {

        if (v == mTextViewSearch) {

            if (mEditTextInput.getText().length() > 0) {

                mStringSearchValue = mEditTextInput.getText().toString().trim();

                mBackProcessGetSearchResult = new BackProcessGetSearchResult();
                mBackProcessGetSearchResult.execute(mMethodGetSearchResult);
                mActivity.getAppAlertDialog().HideKeyboard(mEditTextInput);

            }

        } else if (v == mTextViewAdvanceSearched) {

            mActivity.getAppAlertDialog().HideKeyboard(mEditTextInput);
            showAdvanceSearchDiloag();

        }
    }


    public void loadFeatureResult() {
        if(mListSearchResultFeature!=null && mListSearchResultFeature.size() > 0) {
            mSearchResultFeatureAdapter = new ViewPagerImageAdapter(getChildFragmentManager());
            mViewPager.setAdapter(mSearchResultFeatureAdapter);
            runnable();
            mViewPager.setVisibility(View.VISIBLE);
        }else
        {
            mViewPager.setVisibility(View.GONE);
        }
    }

    /**
     * Method call will show update email dialog
     */
    private void showAdvanceSearchDiloag() {


//        mStringProvider = "";
//        mStringCategoryID= "";
//        mStringCity= "";
//        mStringLocationPolicy= "";
//        mStringMinValue= "";
//        mStringMaxValue= "";
//        mStringSatisfyRate= "";
//        mStringCommitedRate= "";
//        mStringDateTime= "";

        if (mActivity.getMyApplication().getCategoryListParser()!= null && mActivity.getMyApplication().getCategoryListParser().getData()!= null) {
            adapter = new CategoryAdapter(getContext(),android.R.layout.simple_spinner_item, mActivity.getMyApplication().getCategoryListParser().getData());
        } else {
            mActivity.getAppAlertDialog().showDialog("", getString(R.string.validation_error), false);
            return;
        }

        mDialogAdvanceSearch = new Dialog(mActivity);
        mDialogAdvanceSearch.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialogAdvanceSearch.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialogAdvanceSearch.setContentView(R.layout.dialog_advance_search);
        Window window = mDialogAdvanceSearch.getWindow();
        window.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        mDialogAdvanceSearch.show();

        mActivity.setTouchForHideKeyboard(mDialogAdvanceSearch.getWindow().getDecorView());

        mDialogSpinnerCountry = (Spinner) mDialogAdvanceSearch.findViewById(R.id.dialog_advance_search_spinner_country);
        mDialogSpinnerCity = (Spinner) mDialogAdvanceSearch.findViewById(R.id.dialog_advance_search_spinner_city);

        mTextViewDistanceRange = (TextView) mDialogAdvanceSearch.findViewById(R.id.dialog_advance_search_textview_range);
        mTextViewDistanceRangeDetail = (TextView) mDialogAdvanceSearch.findViewById(R.id.dialog_advance_search_textview_range_details);
        mSeekBarDistanceRange = (SeekBar) mDialogAdvanceSearch.findViewById(R.id.dialog_advance_search_seekbar_range);
//        mDialogEditTextCity = (EditText) mDialogAdvanceSearch.findViewById(R.id.dialog_advance_search_edittext_city);
        mDialogEditTextServiceOrProviderName = (EditText) mDialogAdvanceSearch.findViewById(R.id.dialog_advance_search_edittext_service_provider_name);
        mDialogEditTextMinPrice = (EditText) mDialogAdvanceSearch.findViewById(R.id.dialog_advance_search_edittext_service_min_price);
        mDialogEditTextMaxPrice = (EditText) mDialogAdvanceSearch.findViewById(R.id.dialog_advance_search_edittext_service_max_price);

        mDialogSpinnerCategory = (TextView) mDialogAdvanceSearch.findViewById(R.id.dialog_advance_search_spinner_category);
        mDialogSpinnerMaxPrice = (Spinner) mDialogAdvanceSearch.findViewById(R.id.dialog_advance_search_spinner_maximum_price);
        mDialogSpinnerMinPrice = (Spinner) mDialogAdvanceSearch.findViewById(R.id.dialog_advance_search_spinner_minimum_price);
        mDialogTextViewDate = (TextView) mDialogAdvanceSearch.findViewById(R.id.dialog_advance_search_textview_date);
        mDialogTextViewTime = (TextView) mDialogAdvanceSearch.findViewById(R.id.dialog_advance_search_textview_time);
        mDialogTextViewMyPlace = (TextView) mDialogAdvanceSearch.findViewById(R.id.dialog_advance_search_textview_my_place);
        mDialogTextviewBoth = (TextView) mDialogAdvanceSearch.findViewById(R.id.dialog_advance_search_textview_provider_both);
        mDialogTextViewProviderPlace = (TextView) mDialogAdvanceSearch.findViewById(R.id.dialog_advance_search_textview_provider_place);
        mDialogTextViewSearch = (TextView) mDialogAdvanceSearch.findViewById(R.id.dialog_advance_search_textview_search);
        mDialogRatingBarSatisfy = (RatingBar) mDialogAdvanceSearch.findViewById(R.id.dialog_advance_search_rating_satification);
        mDialogRatingBarCommited = (RatingBar) mDialogAdvanceSearch.findViewById(R.id.dialog_advance_search_rating_commitment);
        mDialogRrangeSeekBar = (RangeSeekBar)mDialogAdvanceSearch.findViewById(R.id.dialog_advance_search_range_seekbar);

        mDialogRatingBarSatisfy.setIsIndicator(false);
        mDialogRatingBarCommited.setIsIndicator(false);
        mSeekBarDistanceRange.setProgress(mIntDistanceRange);
        mTextViewDistanceRange.setText(getString(R.string.lbl_distance_range, mIntDistanceRange));
        mSeekBarDistanceRange.setMax(mIntMaximumDistance);
        mSeekBarDistanceRange.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mIntDistanceRange = progress;
                mTextViewDistanceRange.setText(getString(R.string.lbl_distance_range, progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mDialogSpinnerCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mIntCountrySelection = position;
                if (position != 0) {
                    mStringCountryID = mCountryDataParser.getData().get(position - 1).getCountry_id();
                    mBackProcessGetSearchResult = new BackProcessGetSearchResult();
                    mBackProcessGetSearchResult.execute(mMethodGetCity);

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
                if (position != 0) {
                    mStringCityID = mCityDataParser.getData().get(position - 1).getCity_id();
                    mIntMaximumDistance = Integer.parseInt(mCityDataParser.getData().get(position - 1).getCity_radius());
                    mSeekBarDistanceRange.setMax(mIntMaximumDistance);
                    mIntDistanceRange = mIntMaximumDistance;
                    mSeekBarDistanceRange.setProgress(mIntDistanceRange);
                    mTextViewDistanceRange.setText(getString(R.string.lbl_distance_range, mIntDistanceRange));
                    if(mCityDataParser.getData().get(position - 1).getCity_selected().equalsIgnoreCase("true"))
                    {
                        mStringLatitude = mActivity.getMyApplication().getCurrentLatitude();
                        mStringLongitude = mActivity.getMyApplication().getCurrentLongitude();
                        mTextViewDistanceRangeDetail.setText(R.string.lbl_distance_range_current);
                    }else
                    {
                        mStringLatitude = mCityDataParser.getData().get(position - 1).getCity_lat();
                        mStringLongitude = mCityDataParser.getData().get(position - 1).getCity_lng();
                        mTextViewDistanceRangeDetail.setText(R.string.lbl_distance_range_other);
                    }

                } else {
                    mStringCityID = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        mDialogSpinnerCategory.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                setCategoryData();
            }
        });

        mDialogTextViewDate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCommonMethod.HideKeyboard(mDialogTextViewDate);
                new CustomDatePickerFragment(mDialogTextViewDate, mActivity);
            }
        });
        mDialogTextViewTime.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCommonMethod.HideKeyboard(mDialogTextViewTime);
                new CustomTimePickerFragment(mDialogTextViewTime, mActivity);
            }
        });
        mDialogTextViewMyPlace.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCommonMethod.HideKeyboard(mDialogTextViewMyPlace);
                mStringLocationPolicy = "seeker";
                mDialogTextViewMyPlace.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mActivity, R.drawable.icon_radio_btn_selected), null, null, null);
                mDialogTextViewProviderPlace.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mActivity, R.drawable.icon_radio_btn_unselect), null, null, null);
                mDialogTextviewBoth.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mActivity, R.drawable.icon_radio_btn_unselect), null, null, null);
            }
        });
        mDialogTextViewProviderPlace.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCommonMethod.HideKeyboard(mDialogTextViewProviderPlace);
                mStringLocationPolicy = "provider";
                mDialogTextViewProviderPlace.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mActivity, R.drawable.icon_radio_btn_selected), null, null, null);
                mDialogTextViewMyPlace.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mActivity, R.drawable.icon_radio_btn_unselect), null, null, null);
                mDialogTextviewBoth.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mActivity, R.drawable.icon_radio_btn_unselect), null, null, null);
            }
        });

        mDialogTextviewBoth.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCommonMethod.HideKeyboard(mDialogTextviewBoth);
                mStringLocationPolicy = "both";
                mDialogTextviewBoth.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mActivity, R.drawable.icon_radio_btn_selected), null, null, null);
                mDialogTextViewMyPlace.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mActivity, R.drawable.icon_radio_btn_unselect), null, null, null);
                mDialogTextViewProviderPlace.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mActivity, R.drawable.icon_radio_btn_unselect), null, null, null);
            }
        });
        mDialogTextViewSearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                mCommonMethod.HideKeyboard(mDialogTextViewSearch);
                callAdvancedSearch();
            }
        });
        mDialogRatingBarSatisfy.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                mStringSatisfyRate = String.valueOf((int) rating);
            }
        });
        mDialogRatingBarCommited.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                mStringCommitedRate = String.valueOf((int) rating);
            }
        });

        mDialogEditTextServiceOrProviderName.setText(mStringProvider);

        mBackProcessGetSearchResult = new BackProcessGetSearchResult();
        mBackProcessGetSearchResult.execute(mMethodGetCountry);

        if(mStringMinValue.length() > 0 ) {
            mDialogRrangeSeekBar.setSelectedMinValue(Integer.parseInt(mStringMinValue));
            mDialogEditTextMinPrice.setText(mStringMinValue);
        }
        if(mStringMaxValue.length() > 0 ) {
            mDialogRrangeSeekBar.setSelectedMaxValue(Integer.parseInt(mStringMaxValue));
            mDialogEditTextMaxPrice.setText(mStringMaxValue);
        }

        if(mStringDate.length() > 0 )
            mDialogTextViewDate.setText(mStringDate);

        if(mStringTime.length() > 0 )
            mDialogTextViewTime.setText(mStringTime);

        if(mStringSatisfyRate.length() > 0 )
            mDialogRatingBarSatisfy.setRating(Float.valueOf(mStringSatisfyRate));

        if(mStringCommitedRate.length() > 0 )
            mDialogRatingBarCommited.setRating(Float.valueOf(mStringCommitedRate));

        if(mStringLocationPolicy.length() > 0)
        {
            if(mStringLocationPolicy.equalsIgnoreCase("seeker"))
            {
                mDialogTextViewMyPlace.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mActivity, R.drawable.icon_radio_btn_selected), null, null, null);
                mDialogTextViewProviderPlace.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mActivity, R.drawable.icon_radio_btn_unselect), null, null, null);
                mDialogTextviewBoth.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mActivity, R.drawable.icon_radio_btn_unselect), null, null, null);
            }else if(mStringLocationPolicy.equalsIgnoreCase("provider"))
            {
                mDialogTextViewProviderPlace.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mActivity, R.drawable.icon_radio_btn_selected), null, null, null);
                mDialogTextViewMyPlace.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mActivity, R.drawable.icon_radio_btn_unselect), null, null, null);
                mDialogTextviewBoth.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mActivity, R.drawable.icon_radio_btn_unselect), null, null, null);
            }else if(mStringLocationPolicy.equalsIgnoreCase("both"))
            {
                mDialogTextviewBoth.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mActivity, R.drawable.icon_radio_btn_selected), null, null, null);
                mDialogTextViewMyPlace.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mActivity, R.drawable.icon_radio_btn_unselect), null, null, null);
                mDialogTextViewProviderPlace.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mActivity, R.drawable.icon_radio_btn_unselect), null, null, null);
            }
        }

        setSelectedCategory();
        setCountryData(true);
        setCityData(true);

    }

    /**
     * Method call will check validation..
     */
    private void callAdvancedSearch() {

        if(mStringCategoryID.length() ==0)
        {
            mActivity.getAppAlertDialog().showDialog("", getString(R.string.validation_service_category), false);
            return;
        }

        if (mDialogSpinnerCountry.getSelectedItemPosition() == 0) {
            mActivity.getAppAlertDialog().showDialog("", getString(R.string.validation_country), false);
            return;
        }
        if (mDialogSpinnerCity.getSelectedItemPosition() == 0) {
            mActivity.getAppAlertDialog().showDialog("", getString(R.string.validation_city), false);
            return;
        }

        if (mActivity.getAppAlertDialog().validateSelectkField(mStringCountryID, mActivity, getString(R.string.hint_select_country), getString(R.string.validation_country))
                && mActivity.getAppAlertDialog().validateSelectkField(mStringCityID, mActivity, getString(R.string.hint_select_city), getString(R.string.validation_city))) {

//            && mActivity.getAppAlertDialog().validateBlankField(mDialogEditTextServiceOrProviderName, mActivity, getString(R.string.validation_service_provider_name))

//            mStringCity = mDialogEditTextCity.getText().toString();
            mStringProvider = mDialogEditTextServiceOrProviderName.getText().toString();

//            mStringMinValue = mDialogRrangeSeekBar.getSelectedMinValue().toString();
//            mStringMaxValue = mDialogRrangeSeekBar.getSelectedMaxValue().toString();

            mStringMinValue = mDialogEditTextMinPrice.getText().toString().trim();
            mStringMaxValue = mDialogEditTextMaxPrice.getText().toString().trim();

            mStringDate = mDialogTextViewDate.getText().toString();
            mStringTime = mDialogTextViewTime.getText().toString();
            boolean isValid = true;
            if(mStringDate.equalsIgnoreCase(getString(R.string.lbl_Date)) && mStringTime.equalsIgnoreCase(getString(R.string.lbl_time)))
            {
                mStringDateTime = "";
                isValid = true;
            }else if((!mStringDate.equalsIgnoreCase(getString(R.string.lbl_Date))) && mStringTime.equalsIgnoreCase(getString(R.string.lbl_time)))
            {
                mActivity.getAppAlertDialog().showDialog(getString(R.string.app_name), getString(R.string.validation_time), false);
                isValid = false;
            }else if(mStringDate.equalsIgnoreCase(getString(R.string.lbl_Date)) && (!mStringTime.equalsIgnoreCase(getString(R.string.lbl_time))))
            {
                mActivity.getAppAlertDialog().showDialog(getString(R.string.app_name), getString(R.string.validation_date), false);
                isValid = false;
            }

            if(isValid) {
                mBackProcessGetSearchResult = new BackProcessGetSearchResult();
                mBackProcessGetSearchResult.execute(mMethodGetAdvanceSearchResult);
                mDialogAdvanceSearch.cancel();
            }
        }

    }

    /**
     * Method will display set category data
     */
    public void setCategoryData() {
//        final String[] mStringsCategory;
//        int size = mActivity.getMyApplication().getCategoryListParser().getData().size();
//        mStringsCategory = new String[(size) + 1];
//        mStringsCategory[0] = getString(R.string.hint_service_category);
//        for (int i = 1; i < mStringsCategory.length; i++) {
//            mStringsCategory[i] = mActivity.getMyApplication().getCategoryListParser().getData().get(i - 1).getCategory_name();
//
//        }
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mActivity, R.layout.row_spinner_selected_blue, mStringsCategory);
//        adapter.setDropDownViewResource(R.layout.row_spinner_text);
//        mDialogSpinnerCategory.setAdapter(adapter);

//        mDialogSpinnerCategory.setItems(mActivity.getMyApplication().getCategoryListParser().getData(), getString(R.string.hint_service_category), this);


        adapter = new CategoryAdapter(getContext(),android.R.layout.simple_spinner_item, mActivity.getMyApplication().getCategoryListParser().getData());
        selected = new boolean[mActivity.getMyApplication().getCategoryListParser().getData().size()];
        for (int i = 0; i < selected.length; i++)
            selected[i] = false;

        /*ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, new String[]{allText});
        setAdapter(adapter);*/


        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(mActivity, android.R.style.Theme_DeviceDefault_Light_Dialog));
//        final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setAdapter(adapter, null);
        builder.setPositiveButton(getString(R.string.lbl_done), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                setSelectedCategory();
                dialog.cancel();
            }
        });
//        builder.setOnCancelListener(mActivity);

        AlertDialog alertDialog = builder.create();

        ListView listView = alertDialog.getListView();
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CategoryDataParser producto = (CategoryDataParser) parent.getItemAtPosition(position);

                if (producto.isChecked())
                    producto.setChecked(false);
                else
                    producto.setChecked(true);


                adapter.notifyDataSetChanged();

            }
        });

        //alertDialog
        alertDialog.show();
    }

    public void setSelectedCategory()
    {
        List<CategoryDataParser> productos = adapter.getProductos();
        StringBuilder mStringBuilder = new StringBuilder();
        mStringCategoryID = "";
        for (CategoryDataParser producto : productos){
            if (producto.isChecked()){
                if(mStringBuilder.length() > 0) {
                    mStringBuilder.append(", ");
                }
                if(mActivity.getMyApplication().getCurrentLang().equalsIgnoreCase("en"))
                    mStringBuilder.append(producto.getCategory_name());
                else
                    mStringBuilder.append(producto.getCategory_namearebic());

                mStringCategoryID = mStringCategoryID.length()>0 ? mStringCategoryID+","+producto.getCategory_id() :producto.getCategory_id();
            }
        }
        if(mStringBuilder.length() > 0) {
            mDialogSpinnerCategory.setText(mStringBuilder);
            mDialogSpinnerCategory.setTextColor(ContextCompat.getColor(mActivity, R.color.myPrimaryColor));
        }
        else {
            mDialogSpinnerCategory.setText(R.string.hint_service_category);
            mDialogSpinnerCategory.setTextColor(ContextCompat.getColor(mActivity, R.color.dialog_bg));
        }
    }

    /**
     * Method will display set country data.
     */
    public void setCountryData(boolean isNull) {

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
                if(mStringCityID.length()>0)
                {

                }

                if (mStringCurrentCountry.equalsIgnoreCase(mCountryDataParser.getData().get(i).getCountry_name()))
                    mIntCountrySelection = i + 1;
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
            mIntCitySelection = 0;
            int size = mCityDataParser.getData().size();
            mStringsMinPrice = new String[(size) + 1];
            mStringsMinPrice[0] = getString(R.string.hint_select_city);
            for (int i = 0; i < mCityDataParser.getData().size(); i++) {
                if(mActivity.getMyApplication().getCurrentLang().equalsIgnoreCase("en"))
                    mStringsMinPrice[i + 1] = mCityDataParser.getData().get(i).getCity_name();
                else
                    mStringsMinPrice[i + 1] = mCityDataParser.getData().get(i).getCity_namearabic();
                if (mCityDataParser.getData().get(i).getCity_selected().equalsIgnoreCase("true"))
                    mIntCitySelection = i + 1;
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(mActivity, R.layout.row_spinner_selected, mStringsMinPrice);
            adapter.setDropDownViewResource(R.layout.row_spinner_text);
            mDialogSpinnerCity.setAdapter(adapter);
            mDialogSpinnerCity.setSelection(mIntCitySelection);
        }

    }

    /**
     * Method will show popup menu
     *
     * @param v
     */
    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(mActivity, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu, popup.getMenu());
        popup.show();
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item1:
                        sortByName();
                        sortFeatureByName();
                        return true;

//                    case R.id.item2:
//                        return true;

                    case R.id.item3:
                        sortByLocation();
                        sortFeatureByLocation();
                        return true;

                    case R.id.item4:
                        sortByRating(true);
                        sortFeatureByRating(true);
                        return true;

                    case R.id.item5:
                        sortByRating(false);
                        sortFeatureByRating(false);
                        return true;


                    default:
                        return false;
                }
            }
        });
    }

    /**
     * Method will sort data by name
     */
    public void sortByName() {
        Collections.sort(mListSearchResultNormal, new Comparator<UserDataParser>() {

            @SuppressLint("NewApi")
            @Override
            public int compare(UserDataParser o1, UserDataParser o2) {

                return o1.getUsername().compareTo(o2.getUsername());
            }
        });
        if (mSearchResultAdapter != null)
            mSearchResultAdapter.notifyDataSetChanged();
    }

    /**
     * Method will sort data by name
     */
    public void sortByLocation() {
        Collections.sort(mListSearchResultNormal, new Comparator<UserDataParser>() {

            @SuppressLint("NewApi")
            @Override
            public int compare(UserDataParser o1, UserDataParser o2) {

                double lat1 = Double.parseDouble(o1.getUser_lat());
                double lng1 = Double.parseDouble(o1.getUser_lng());

                double lat2 = Double.parseDouble(o2.getUser_lat());
                double lng2 = Double.parseDouble(o2.getUser_lng());

                LatLng mLatLng1 = new LatLng(lat1, lng1);
                LatLng mLatLng2 = new LatLng(lat2, lng2);
                LatLng mCurrent = new LatLng(Double.parseDouble(mActivity.getMyApplication().getCurrentLatitude()), Double.parseDouble(mActivity.getMyApplication().getCurrentLongitude()));

                double distance1 = mCommonMethod.getDistance(mLatLng1, mCurrent);
                double distance2 = mCommonMethod.getDistance(mLatLng2, mCurrent);

                return Double.compare(distance1, distance2);
            }
        });
        if (mSearchResultAdapter != null)
            mSearchResultAdapter.notifyDataSetChanged();
    }

    /**
     * Method will sort data by name
     */
    public void sortByRating(final boolean bySatisfaction) {
        Collections.sort(mListSearchResultNormal, new Comparator<UserDataParser>() {

            @SuppressLint("NewApi")
            @Override
            public int compare(UserDataParser o1, UserDataParser o2) {

                int rat1;
                int rat2;
                if (bySatisfaction) {
                    rat1 = Integer.parseInt(o1.getTotalsatisfy());
                    rat2 = Integer.parseInt(o2.getTotalsatisfy());
                } else {
                    rat1 = Integer.parseInt(o1.getTotalcommited());
                    rat2 = Integer.parseInt(o2.getTotalcommited());
                }

                return Integer.compare(rat2, rat1);
            }
        });
        if (mSearchResultAdapter != null)
            mSearchResultAdapter.notifyDataSetChanged();
    }

    /**
     * Method will sort data by name
     */
    public void sortFeatureByName() {
        Collections.sort(mListSearchResultFeature, new Comparator<UserDataParser>() {

            @SuppressLint("NewApi")
            @Override
            public int compare(UserDataParser o1, UserDataParser o2) {

                return o1.getUsername().compareTo(o2.getUsername());
            }
        });
        loadFeatureResult();
    }

    /**
     * Method will sort data by name
     */
    public void sortFeatureByLocation() {
        Collections.sort(mListSearchResultFeature, new Comparator<UserDataParser>() {

            @SuppressLint("NewApi")
            @Override
            public int compare(UserDataParser o1, UserDataParser o2) {

                double lat1 = Double.parseDouble(o1.getUser_lat());
                double lng1 = Double.parseDouble(o1.getUser_lng());

                double lat2 = Double.parseDouble(o2.getUser_lat());
                double lng2 = Double.parseDouble(o2.getUser_lng());

                LatLng mLatLng1 = new LatLng(lat1, lng1);
                LatLng mLatLng2 = new LatLng(lat2, lng2);
                LatLng mCurrent = new LatLng(Double.parseDouble(mActivity.getMyApplication().getCurrentLatitude()), Double.parseDouble(mActivity.getMyApplication().getCurrentLongitude()));

                double distance1 = mCommonMethod.getDistance(mLatLng1, mCurrent);
                double distance2 = mCommonMethod.getDistance(mLatLng2, mCurrent);

                return Double.compare(distance1, distance2);
            }
        });
        loadFeatureResult();
    }

    /**
     * Method will sort data by name
     */
    public void sortFeatureByRating(final boolean bySatisfaction) {
        Collections.sort(mListSearchResultFeature, new Comparator<UserDataParser>() {

            @SuppressLint("NewApi")
            @Override
            public int compare(UserDataParser o1, UserDataParser o2) {

                int rat1;
                int rat2;
                if (bySatisfaction) {
                    rat1 = Integer.parseInt(o1.getTotalsatisfy());
                    rat2 = Integer.parseInt(o2.getTotalsatisfy());
                } else {
                    rat1 = Integer.parseInt(o1.getTotalcommited());
                    rat2 = Integer.parseInt(o2.getTotalcommited());
                }

                return Integer.compare(rat2, rat1);
            }
        });
        loadFeatureResult();
    }

    /**
     * BaseAdapter class for load data into listview
     */
    public class NormalSearchResultAdapter extends BaseAdapter {
        ViewHolder mViewHolder;

        @Override
        public int getCount() {
            return mListSearchResultNormal.size();
        }

        @Override
        public Object getItem(int arg0) {
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertview, ViewGroup arg2) {
            if (convertview == null) {
                convertview = mActivity.getLayoutInflater().inflate(R.layout.row_search, null);
                mViewHolder = new ViewHolder();

                mViewHolder.mTextViewName = (TextView) convertview.findViewById(R.id.row_search_textview_name);
                mViewHolder.mTextViewService1 = (TextView) convertview.findViewById(R.id.row_search_textview_service1);
                mViewHolder.mTextViewService2 = (TextView) convertview.findViewById(R.id.row_search_textview_service2);
                mViewHolder.mTextViewMore = (TextView) convertview.findViewById(R.id.row_search_textview_service_more);
                mViewHolder.mImageViewProviderPic = (ImageView) convertview.findViewById(R.id.row_search_imageview_user_dp);
                mViewHolder.imageViewSugested = (ImageView) convertview.findViewById(R.id.row_search_imageview_suggested);
                mViewHolder.mRatingBarSatisfy = (RatingBar) convertview.findViewById(R.id.row_search_ratingbar_satisfy);
                mViewHolder.mRatingBarCommited = (RatingBar) convertview.findViewById(R.id.row_search_ratingbar_committed);
                mViewHolder.mLinearLayoutCategory = (LinearLayout) convertview.findViewById(R.id.row_search_linear_service);


                convertview.setTag(mViewHolder);

            } else {
                mViewHolder = (ViewHolder) convertview.getTag();
            }

            if(isSearchResult)
                mViewHolder.imageViewSugested.setVisibility(View.GONE);
            else
                mViewHolder.imageViewSugested.setVisibility(View.VISIBLE);
            mViewHolder.mImageViewProviderPic.setImageResource(R.drawable.bg_user_profile);
            // mViewHolder.mTextViewName.setText(mapArrayListResult.get(position).get("name"));
            mViewHolder.mTextViewName.setText(mListSearchResultNormal.get(position).getUsername());

            mViewHolder.mRatingBarSatisfy.setRating(Float.parseFloat(mListSearchResultNormal.get(position).getTotalsatisfy()));
            mViewHolder.mRatingBarCommited.setRating(Float.parseFloat(mListSearchResultNormal.get(position).getTotalcommited()));
            mImageLoader.getInstance().displayImage(mListSearchResultNormal.get(position).getUser_image(), mViewHolder.mImageViewProviderPic, mDisplayImageOptions);


            if (mListSearchResultNormal.get(position).getCategories() != null) {

                mViewHolder.mLinearLayoutCategory.setVisibility(View.VISIBLE);

                if(mActivity.getMyApplication().getCurrentLang().equalsIgnoreCase("en")) {
                    if (mListSearchResultNormal.get(position).getCategories().size() == 1) {
                        mViewHolder.mTextViewService1.setText(mListSearchResultNormal.get(position).getCategories().get(0).getCategory_name());
                        mCommonMethod.setMaximumLength(20, mViewHolder.mTextViewService1);
                        mViewHolder.mTextViewService2.setVisibility(View.GONE);
                        mViewHolder.mTextViewMore.setVisibility(View.GONE);

                    } else if (mListSearchResultNormal.get(position).getCategories().size() == 2) {

                        mViewHolder.mTextViewService1.setText(mListSearchResultNormal.get(position).getCategories().get(0).getCategory_name());
                        mViewHolder.mTextViewService2.setText(mListSearchResultNormal.get(position).getCategories().get(1).getCategory_name());
                        mCommonMethod.setMaximumLength(10, mViewHolder.mTextViewService1);
                        mCommonMethod.setMaximumLength(10, mViewHolder.mTextViewService2);
                        mViewHolder.mTextViewService2.setVisibility(View.VISIBLE);
                        mViewHolder.mTextViewMore.setVisibility(View.GONE);

                    } else if (mListSearchResultNormal.get(position).getCategories().size() > 2) {

                        mViewHolder.mTextViewService1.setText(mListSearchResultNormal.get(position).getCategories().get(0).getCategory_name());
                        mViewHolder.mTextViewService2.setText(mListSearchResultNormal.get(position).getCategories().get(1).getCategory_name());
                        mCommonMethod.setMaximumLength(8, mViewHolder.mTextViewService1);
                        mCommonMethod.setMaximumLength(8, mViewHolder.mTextViewService2);
                        mViewHolder.mTextViewService2.setVisibility(View.VISIBLE);
                        mViewHolder.mTextViewMore.setVisibility(View.VISIBLE);

                    } else {
                        mViewHolder.mTextViewService2.setVisibility(View.GONE);
                        mViewHolder.mTextViewMore.setVisibility(View.GONE);
                    }
                }else
                {
                    if (mListSearchResultNormal.get(position).getCategories().size() == 1) {
                        mViewHolder.mTextViewService1.setText(mListSearchResultNormal.get(position).getCategories().get(0).getCategory_namearebic());
                        mCommonMethod.setMaximumLength(20, mViewHolder.mTextViewService1);
                        mViewHolder.mTextViewService2.setVisibility(View.GONE);
                        mViewHolder.mTextViewMore.setVisibility(View.GONE);

                    } else if (mListSearchResultNormal.get(position).getCategories().size() == 2) {

                        mViewHolder.mTextViewService1.setText(mListSearchResultNormal.get(position).getCategories().get(0).getCategory_namearebic());
                        mViewHolder.mTextViewService2.setText(mListSearchResultNormal.get(position).getCategories().get(1).getCategory_namearebic());
                        mCommonMethod.setMaximumLength(10, mViewHolder.mTextViewService1);
                        mCommonMethod.setMaximumLength(10, mViewHolder.mTextViewService2);
                        mViewHolder.mTextViewService2.setVisibility(View.VISIBLE);
                        mViewHolder.mTextViewMore.setVisibility(View.GONE);

                    } else if (mListSearchResultNormal.get(position).getCategories().size() > 2) {

                        mViewHolder.mTextViewService1.setText(mListSearchResultNormal.get(position).getCategories().get(0).getCategory_namearebic());
                        mViewHolder.mTextViewService2.setText(mListSearchResultNormal.get(position).getCategories().get(1).getCategory_namearebic());
                        mCommonMethod.setMaximumLength(8, mViewHolder.mTextViewService1);
                        mCommonMethod.setMaximumLength(8, mViewHolder.mTextViewService2);
                        mViewHolder.mTextViewService2.setVisibility(View.VISIBLE);
                        mViewHolder.mTextViewMore.setVisibility(View.VISIBLE);
                    } else {
                        mViewHolder.mTextViewService2.setVisibility(View.GONE);
                        mViewHolder.mTextViewMore.setVisibility(View.GONE);
                    }
                }
            } else {
                mViewHolder.mLinearLayoutCategory.setVisibility(View.INVISIBLE);
            }


            return convertview;
        }

    }

    public class ViewHolder {
        TextView mTextViewName;
        TextView mTextViewService1;
        TextView mTextViewService2;
        TextView mTextViewMore;
        ImageView mImageViewProviderPic;
        ImageView imageViewSugested;
        RatingBar mRatingBarSatisfy;
        RatingBar mRatingBarCommited;
        LinearLayout mLinearLayoutCategory;
        RelativeLayout mRelativeLayoutBg;

    }

    class ViewPagerImageAdapter extends FragmentStatePagerAdapter {



        public ViewPagerImageAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public Fragment getItem(int position) {

            int pos1 = (int) position * 2;
            int pos2 = (int) (position * 2) + 1;

            Bundle mBundle = new Bundle();
            SuggestedProviderViewFragment mHelpScreenImageFragment = new SuggestedProviderViewFragment();
            mBundle.putParcelable(getString(R.string.bundle_bundle_feature1), mListSearchResultFeature.get(pos1));
            if (mListSearchResultFeature.size() > pos2)
                mBundle.putParcelable(getString(R.string.bundle_bundle_feature2), mListSearchResultFeature.get(pos2));
            else
                mBundle.putParcelable(getString(R.string.bundle_bundle_feature2), null);
            mHelpScreenImageFragment.setArguments(mBundle);


            return mHelpScreenImageFragment;
        }


        @Override
        public int getCount() {
            int size = mListSearchResultFeature.size();
            int rest = size % 2;
            size = size / 2 + rest;
            return size;
        }

    }


    /**
     * Method call when sliding images.
     */
    public void runnable() {
        if (animateViewPager != null)
            mHandler.removeCallbacks(animateViewPager);
        animateViewPager = new Runnable() {
            public void run() {
                if (mViewPager.getCurrentItem() == mViewPager.getChildCount() - 1) {
                    mViewPager.setCurrentItem(0);
                } else {
                    mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);
                }
                mHandler.postDelayed(animateViewPager, ANIM_VIEWPAGER_DELAY);
            }
        };
        mHandler.postDelayed(animateViewPager, ANIM_VIEWPAGER_DELAY);
    }

    public class CategoryAdapter extends ArrayAdapter<CategoryDataParser>{

        Context context;
        List<CategoryDataParser> productos;
        LayoutInflater inflater;
        CategoryDataParser modelProducto;

        private class ViewHolder{
            TextView txtPresentacion;
            CheckBox checkBox;

        }
        public CategoryAdapter(Context context, int resource, List<CategoryDataParser> objects) {
            super(context, resource, objects);

            this.context = context;
            this.productos = objects;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View row = convertView;
            ViewHolder viewHolder;
            if (row == null){
                row = inflater.inflate(R.layout.row_mutiple_spinner, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.txtPresentacion = (TextView) row.findViewById(R.id.txt_presenta_producto);
                viewHolder.checkBox = (CheckBox) row.findViewById(R.id.checkBoxPro);
                row.setTag(viewHolder);


            }else{
                viewHolder = (ViewHolder) row.getTag();
            }

            modelProducto = productos.get(position);

            if(mActivity.getMyApplication().getCurrentLang().equalsIgnoreCase("en"))
                viewHolder.txtPresentacion.setText(modelProducto.getCategory_name());
            else
                viewHolder.txtPresentacion.setText(modelProducto.getCategory_namearebic());
            viewHolder.checkBox.setChecked(modelProducto.isChecked());



            return row;

        }

        public List<CategoryDataParser> getProductos(){
            return productos;
        }
    }

    @Override
    public void onDestroy() {
        if(animateViewPager!=null)
            mHandler.removeCallbacks(animateViewPager);
        super.onDestroy();
    }

    /**
     * AsyncTask for calling webservice in background.
     *
     * @author ebaraiya
     */
    public class BackProcessGetSearchResult extends AsyncTask<String, Void, String> {
        String mCurrentMethod = "";

        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(mActivity, "", getString(R.string.dialog_loading), true);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            mCurrentMethod = params[0];
            if (mCurrentMethod.equalsIgnoreCase(mMethodGetSearchResult)) {
                isSearchResult = true;
                mSearchResultParser = (SearchResultParser) mActivity.getWebMethod().callSearchByName(mActivity.getMyApplication().getUserFiled(getString(R.string.sp_user_id)),
                        mActivity.getMyApplication().getUserFiled(getString(R.string.sp_user_hash)),
                        mStringSearchValue,
                        mActivity.getMyApplication().getCurrentLatitude(),
                        mActivity.getMyApplication().getCurrentLongitude(),
                        mSearchResultParser);

            } else if (mCurrentMethod.equalsIgnoreCase(mMethodGetAdvanceSearchResult)) {
                isSearchResult = true;
                mSearchResultParser = (SearchResultParser) mActivity.getWebMethod().callAdvanceSearch(mActivity.getMyApplication().getUserFiled(getString(R.string.sp_user_id)),
                        mActivity.getMyApplication().getUserFiled(getString(R.string.sp_user_hash)),
                        mStringProvider,
                        mStringCategoryID,
                        mStringCityID,
                        mStringCountryID,
                        String.valueOf(mIntDistanceRange),
                        mStringLocationPolicy,
                        mStringMinValue,
                        mStringMaxValue,
                        mStringSatisfyRate,
                        mStringCommitedRate,
                        mStringDateTime,
                        mStringLatitude,
                        mStringLongitude,
                        mSearchResultParser);

            } else if (mCurrentMethod.equalsIgnoreCase(mMethodGetFeatureProvider)) {
                isSearchResult = false;
                mSearchResultParser = (SearchResultParser) mActivity.getWebMethod().callGetFeatureSearch(mActivity.getMyApplication().getUserFiled(getString(R.string.sp_user_id)),
                        mActivity.getMyApplication().getUserFiled(getString(R.string.sp_user_hash)),
                        mActivity.getMyApplication().getCurrentLatitude(),
                        mActivity.getMyApplication().getCurrentLongitude(),
                        mSearchResultParser);
            }  else if (mCurrentMethod.equalsIgnoreCase(mMethodGetCountry)) {
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
                    if (mCurrentMethod.equalsIgnoreCase(mMethodGetSearchResult)
                            || mCurrentMethod.equalsIgnoreCase(mMethodGetFeatureProvider)
                                || mCurrentMethod.equalsIgnoreCase(mMethodGetAdvanceSearchResult)) {


                        if (mSearchResultParser.getWs_status().equalsIgnoreCase("true")) {
                            if (mSearchResultParser.getData() != null) {

                                if (mSearchResultParser.getData().getTopfeatured() != null && mSearchResultParser.getData().getTopfeatured().size() > 0) {
                                    mListSearchResultFeature = mSearchResultParser.getData().getTopfeatured();
                                    loadFeatureResult();
                                    mViewPager.setVisibility(View.VISIBLE);
                                }else
                                {
                                    mViewPager.setVisibility(View.GONE);
                                }
                                if (mSearchResultParser.getData().getTopnormal() != null && mSearchResultParser.getData().getTopnormal().size() > 0) {
                                    mListSearchResultNormal = mSearchResultParser.getData().getTopnormal();

                                    mSearchResultAdapter = new NormalSearchResultAdapter();
                                    mGridView.setAdapter(mSearchResultAdapter);
                                    mGridView.setVisibility(View.VISIBLE);
                                }else {
                                    mGridView.setVisibility(View.GONE);
                                }
                            }
                        } else {

                            if (mSearchResultParser.getMessage().toString().contains(getString(R.string.alt_msg_exipred))) {
                                mActivity.getAppAlertDialog().showAlertWithSingleButton("", mSearchResultParser.getMessage().toString(),
                                        mActivity.getString(R.string.lbl_logout),
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                mActivity.logout();
                                            }
                                        });
                            } else {
                                if(!mCurrentMethod.equalsIgnoreCase(mMethodGetFeatureProvider))
                                    mActivity.getAppAlertDialog().showDialog(getString(R.string.app_name), mSearchResultParser.getMessage(), false);
                            }
                        }


                    } else if (mCurrentMethod.equalsIgnoreCase(mMethodGetAdvanceSearchResult)) {

                        mSearchResultAdapter = new NormalSearchResultAdapter();
                        mGridView.setAdapter(mSearchResultAdapter);
                    }
                    else if (mCurrentMethod.equalsIgnoreCase(mMethodGetCountry)) {
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


}
