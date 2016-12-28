package beautician.beauty.android.fragments;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Dialog;
import beautician.beauty.android.views.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.Iterator;

import beautician.beauty.android.R;
import beautician.beauty.android.activities.MyFragmentActivity;
import beautician.beauty.android.parsers.BookAppointmentParser;
import beautician.beauty.android.parsers.LoginParser;
import beautician.beauty.android.utilities.CommonMethod;
import beautician.beauty.android.utilities.CustomDatePickerFragment;
import beautician.beauty.android.utilities.CustomTimePickerFragment;
import beautician.beauty.android.utilities.StaticData;
import beautician.beauty.android.views.HorizontalListView;

@SuppressLint("InflateParams")
public class BookAppointmentFragment extends Fragment implements OnClickListener {

    private MyFragmentActivity mActivity;
    public View rootView;
    private Dialog mDialogCheckAvailability;

    public SuggestedProviderAdapter mProviderAdapter;

    private TextView mTextViewTotal;
    private TextView mTextViewDate;
    private TextView mTextViewTime;
    private TextView mTextViewMyPlace;
    private TextView mTextViewProviderPlace;
    private TextView mTextViewAddress;
    private TextView mTextViewCheckAvailability;
    private ImageView mImageViewPickLocation;
    private RelativeLayout mRelativeLayoutPickLoacation;
    private LinearLayout mLinearLayoutRow;
    private LinearLayout mLinearLayoutLocationPolicy;
    private LinearLayout mLinearLayoutPlace;

    public TextView mDialogTextViewMessageTextView;
    public TextView mDialogTextViewSuggestedProviders;
    public TextView mDialogTextViewBook;
    public HorizontalListView mDialogHListProviders;


    private BackProcessBookAppointment mBackProcessBookAppointment;
    private ProgressDialog mProgressDialog;
    private String mMethodCheckAvailability = "CheckAvailability";
    private String mMethodSetAppointment = "SetAppointment";
    private String mStringProviderName = "";
    private String mStringProviderPic = "";
    private String mStringProviderID = "";
    private String mStringLocation = "";
    private String mStringLat = "";
    private String mStringLong = "";
    public String mStringDate = "";
    public String mStringTime = "";
    private String mStringDateTime = "";
    private String mStringProviderLocationPolicy = "";
    private String mStringAppointmentLocationPolicy = "provider";
    private String mStringAppointmentServices = "";
    public String mStringCheckType = "check";
    public String mStringTransFees = "0";
    public String mStringMinApptFees = "0";

    private float mIntTotalPrice = 0;
    private float mIntMinAppPrice = 0;
    private boolean hasMyPlaceSelected = false;

    private CommonMethod mCommonMethod;

    private LoginParser mLoginParser;
    private LoginParser mLoginParserAppointment;
    private String mStringCheckMessage = "";
    private boolean isProviderAvailable = false;

    private DisplayImageOptions mDisplayImageOptions;

    public BookAppointmentFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_book_appointment, container, false);

        mActivity = (MyFragmentActivity) getActivity();
        mActivity.setHeaderTitle(R.string.lbl_book_an_appointment_);
        mActivity.setSearchIcon(R.drawable.icon_search);
        mActivity.onBackButton(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.replaceFragment(new SearchFragment(), true);
            }
        });

        mCommonMethod = new CommonMethod(mActivity);
        mLoginParserAppointment = new LoginParser();
        mLoginParser = new LoginParser();

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

        getWidgetRefrence(rootView);
        registerOnClick();
        loadServices();

        return rootView;
    }


    /**
     * Method call will get IDs from xml file.
     */
    private void getWidgetRefrence(View v) {
        mTextViewTotal = (TextView) v.findViewById(R.id.fragment_book_appointment_textview_total_price);
        mTextViewDate = (TextView) v.findViewById(R.id.fragment_book_appointment_textview_date);
        mTextViewTime = (TextView) v.findViewById(R.id.fragment_book_appointment_textview_time);
        mTextViewMyPlace = (TextView) v.findViewById(R.id.fragment_book_appointment_textview_my_place);
        mTextViewProviderPlace = (TextView) v.findViewById(R.id.fragment_book_appointment_textview_provider_place);
        mTextViewAddress = (TextView) v.findViewById(R.id.fragment_book_appointment_textview_address);
        mImageViewPickLocation = (ImageView) v.findViewById(R.id.fragment_book_appointment_imageview_pic_location);
        mTextViewCheckAvailability = (TextView) v.findViewById(R.id.fragment_book_appointment_textview_check_availability);
        mRelativeLayoutPickLoacation = (RelativeLayout) v.findViewById(R.id.fragment_book_appointment_relative_pick_location);
        mLinearLayoutRow = (LinearLayout) v.findViewById(R.id.fragment_book_appointment_linear_row);
        mLinearLayoutLocationPolicy = (LinearLayout) v.findViewById(R.id.fragment_book_appointment_linear_loaction_policy);
        mLinearLayoutPlace = (LinearLayout) v.findViewById(R.id.fragment_book_appointment_linear_loaction_place);
    }

    /**
     * Method call will Register OnClick() Events for widgets.
     */
    private void registerOnClick() {

        mTextViewDate.setOnClickListener(this);
        mTextViewTime.setOnClickListener(this);
        mTextViewMyPlace.setOnClickListener(this);
        mTextViewProviderPlace.setOnClickListener(this);
        mRelativeLayoutPickLoacation.setOnClickListener(this);
        mTextViewCheckAvailability.setOnClickListener(this);
    }

    /**
     * Method call OnClick Event fire.
     */
    @Override
    public void onClick(View v) {

        if (v == mTextViewDate) {

            new CustomDatePickerFragment(mTextViewDate, mActivity);

        } else if (v == mTextViewTime) {

            new CustomTimePickerFragment(mTextViewTime, mActivity);

        } else if (v == mTextViewMyPlace) {

            mStringAppointmentLocationPolicy = "seeker";
            hasMyPlaceSelected = true;
            setMyLocationPolicy();
            mTextViewMyPlace.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mActivity, R.drawable.icon_radio_btn_selected), null, null, null);
            mRelativeLayoutPickLoacation.setVisibility(View.VISIBLE);

        } else if (v == mTextViewProviderPlace) {

            mStringAppointmentLocationPolicy = "provider";
            hasMyPlaceSelected = false;
            setMyLocationPolicy();
            mTextViewProviderPlace.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mActivity, R.drawable.icon_radio_btn_selected), null, null, null);
            mRelativeLayoutPickLoacation.setVisibility(View.GONE);


        } else if (v == mRelativeLayoutPickLoacation) {

            Intent mIntent = new Intent(mActivity, ProviderLocationMapViewFragment.class);
            mIntent.putExtra(getString(R.string.bundle_from), getString(R.string.bundle_from_appointment));
            startActivityForResult(mIntent, StaticData.REQUEST_MAP_CODE);

        } else if (v == mTextViewCheckAvailability) {

            saveData();



        }
    }

    public void loadServices() {

        if (getArguments() != null) {

            mStringProviderName = getArguments().getString(getString(R.string.bundle_provider_name));
            mStringProviderPic = getArguments().getString(getString(R.string.bundle_provider_image));
            mStringProviderID = getArguments().getString(getString(R.string.bundle_provider_id));
            mStringLocation = getArguments().getString(getString(R.string.bundle_location));
            mStringLat = getArguments().getString(getString(R.string.bundle_lat));
            mStringLong = getArguments().getString(getString(R.string.bundle_lng));
            mStringProviderLocationPolicy = getArguments().getString(getString(R.string.bundle_location_policy));

            mStringMinApptFees = getArguments().getString(getString(R.string.bundle_min_appt_price));
            mIntMinAppPrice = Float.parseFloat(mStringMinApptFees);
            mStringTransFees = getArguments().getString(getString(R.string.bundle_trans_fees));

            if (mStringProviderLocationPolicy.equalsIgnoreCase(getString(R.string.bundle_provider))) {
                mStringAppointmentLocationPolicy = "provider";
                mLinearLayoutLocationPolicy.setVisibility(View.GONE);
            } else if (mStringProviderLocationPolicy.equalsIgnoreCase(getString(R.string.bundle_seeker))) {
                mStringAppointmentLocationPolicy = "seeker";
                mTextViewProviderPlace.setVisibility(View.GONE);
                mTextViewMyPlace.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mActivity, R.drawable.icon_radio_btn_selected), null, null, null);
                mRelativeLayoutPickLoacation.setVisibility(View.VISIBLE);
                hasMyPlaceSelected = true;
            }
        }

        mLinearLayoutRow.removeAllViews();
        Iterator myVeryOwnIterator = mActivity.getMyApplication().getBookedAppoinmentData().keySet().iterator();
        StringBuilder mStringBuilder = new StringBuilder();

        mIntTotalPrice = 0;

        while (myVeryOwnIterator.hasNext()) {
            String key = (String) myVeryOwnIterator.next();
            BookAppointmentParser value = (BookAppointmentParser) mActivity.getMyApplication().getBookedAppoinmentData().get(key);

            View convertView = mActivity.getLayoutInflater().inflate(R.layout.row_book_appointment_services, null);

            TextView mTextViewName = (TextView) convertView.findViewById(R.id.row_book_appointment_service_name);
            TextView mTextViewPrice = (TextView) convertView.findViewById(R.id.row_book_appointment_service_price);
            TextView mTextViewCateName = (TextView) convertView.findViewById(R.id.row_book_appointment_category_name);

            mTextViewName.setText(value.getService_name());
            mTextViewPrice.setText(getString(R.string.lbl_sr) + " " + mCommonMethod.getTwodigitValue(Float.parseFloat(value.getTotal_price())));
            mTextViewCateName.setText(value.getCategory_name() + " (" + value.getQuantity() + ")");
            mLinearLayoutRow.addView(convertView);

            mIntTotalPrice = Float.parseFloat(value.getTotal_price()) + mIntTotalPrice;


            if (mStringBuilder.toString().length() > 0)
                mStringBuilder.append(",");

            mStringBuilder.append(value.getService_id());
            mStringBuilder.append("||");
            mStringBuilder.append(value.getQuantity());
            mStringBuilder.append("||");
            mStringBuilder.append(value.getCategory_id());
            mStringBuilder.append("||");
            mStringBuilder.append(value.getTotal_price());

        }

        if(mIntMinAppPrice > mIntTotalPrice)
            mIntTotalPrice = mIntMinAppPrice;

        mTextViewTotal.setText(getString(R.string.lbl_sr) + " " + String.valueOf(mCommonMethod.getTwodigitValue(mIntTotalPrice)));
        mStringAppointmentServices = mStringBuilder.toString();


    }

    public void saveData() {
        //Toast.makeText(mActivity, mStringAppointmentServices, Toast.LENGTH_SHORT).show();

        mStringDate = mTextViewDate.getText().toString();
        mStringTime = mTextViewTime.getText().toString();
        if (mActivity.getAppAlertDialog().validateSelectkField(mStringDate, mActivity, getString(R.string.lbl_Date), getString(R.string.validation_date))
                && mActivity.getAppAlertDialog().validateSelectkField(mStringTime, mActivity, getString(R.string.lbl_time), getString(R.string.validation_time))) {
            if (hasMyPlaceSelected && (mTextViewAddress.getText().toString().length() == 0 || mTextViewAddress.getText().toString().equalsIgnoreCase(getString(R.string.lbl_pick_your_location)))) {
                mActivity.getAppAlertDialog().showDialog("", getString(R.string.validation_location), false);

            } else {
                mStringDateTime = mCommonMethod.getDateInFormate(mStringDate + " " + mStringTime, StaticData.DATE_FORMAT_3, StaticData.DATE_FORMAT_6_1);
                mBackProcessBookAppointment = new BackProcessBookAppointment();
                mBackProcessBookAppointment.execute(mMethodCheckAvailability);

//                DepositFragment mDepositFragment = new DepositFragment();
//                Bundle mBundle = new Bundle();
//                mBundle.putString(getString(R.string.bundle_provider_name), mStringProviderName);
//                mBundle.putString(getString(R.string.bundle_provider_image), mStringProviderPic);
//                mBundle.putString(getString(R.string.bundle_provider_id), mStringProviderID);
//                mBundle.putString(getString(R.string.bundle_location), mStringLocation);
//                mBundle.putString(getString(R.string.bundle_location_policy), mStringProviderLocationPolicy);
//                mBundle.putString(getString(R.string.bundle_time), mStringDateTime);
//                mBundle.putString(getString(R.string.bundle_appointment_id), "1");
//                mDepositFragment.setArguments(mBundle);
//                mActivity.replaceFragment(mDepositFragment, true);
            }
        }


    }

    /**
     * Method call will set location selection..
     */
    private void setMyLocationPolicy() {
        mTextViewMyPlace.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mActivity, R.drawable.icon_radio_btn_unselect), null, null, null);
        mTextViewProviderPlace.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mActivity, R.drawable.icon_radio_btn_unselect), null, null, null);
        mTextViewAddress.setVisibility(View.VISIBLE);
        mImageViewPickLocation.setVisibility(View.VISIBLE);
    }

    /**
     * Method call will show update email dialog
     */
    private void showCheckAvailibilityDialog() {
        mDialogCheckAvailability = new Dialog(mActivity);
        mDialogCheckAvailability.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialogCheckAvailability.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialogCheckAvailability.setContentView(R.layout.dialog_check_availability);
        Window window = mDialogCheckAvailability.getWindow();
        window.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        mDialogCheckAvailability.show();
        mDialogTextViewMessageTextView = (TextView) mDialogCheckAvailability.findViewById(R.id.dialog_check_availability_textview_msg);
        mDialogTextViewSuggestedProviders = (TextView) mDialogCheckAvailability.findViewById(R.id.dialog_check_availability_textview_suggested_providers);
        mDialogTextViewBook = (TextView) mDialogCheckAvailability.findViewById(R.id.dialog_check_availability_textview_book);

        mDialogHListProviders = (HorizontalListView) mDialogCheckAvailability.findViewById(R.id.dialog_check_availability_horizontallist_providers);
        mDialogHListProviders.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mActivity.removePreviousFragment();
                ProviderDetailsCategoryPagerFragment mProviderDetailsFragment = new ProviderDetailsCategoryPagerFragment();
                Bundle mBundle = new Bundle();
                mBundle.putString(getString(R.string.bundle_provider_id), mLoginParser.getSuggestedprovider().get(position).getUser_id());
                mProviderDetailsFragment.setArguments(mBundle);
                mActivity.replaceFragment(mProviderDetailsFragment, true);
                mDialogCheckAvailability.cancel();
            }
        });

        mDialogTextViewMessageTextView.setText(mStringCheckMessage);


        if (isProviderAvailable) {
            mDialogHListProviders.setVisibility(View.GONE);
            mDialogTextViewSuggestedProviders.setVisibility(View.GONE);
            mDialogTextViewBook.setText(R.string.lbl_book_an_appointment);
        } else {
            if (mLoginParser.getSuggestedprovider() != null && mLoginParser.getSuggestedprovider().size() > 0) {
                mDialogHListProviders.setVisibility(View.VISIBLE);
                mDialogTextViewSuggestedProviders.setVisibility(View.VISIBLE);
                mProviderAdapter = new SuggestedProviderAdapter();
                mDialogHListProviders.setAdapter(mProviderAdapter);
            }else
            {
                mDialogTextViewSuggestedProviders.setVisibility(View.GONE);
            }
            mDialogTextViewBook.setText(R.string.lbl_cancel);
        }

        mDialogTextViewBook.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {


                if (isProviderAvailable) {
                    mDialogCheckAvailability.cancel();
                    mBackProcessBookAppointment = new BackProcessBookAppointment();
                    mBackProcessBookAppointment.execute(mMethodSetAppointment);
                } else {
                    mDialogCheckAvailability.cancel();
                }


            }
        });

    }

    /**
     * BaseAdapter class for load data into listview
     */
    public class SuggestedProviderAdapter extends BaseAdapter {
        ViewHolder mViewHolder;

        @Override
        public int getCount() {
            return mLoginParser.getSuggestedprovider().size();
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

            mViewHolder.imageViewSugested.setVisibility(View.GONE);

//            mViewHolder.mImageViewProviderPic.setImageResource(R.drawable.bg_user_profile);
            // mViewHolder.mTextViewName.setText(mapArrayListResult.get(position).get("name"));
            mViewHolder.mTextViewName.setText(mLoginParser.getSuggestedprovider().get(position).getUsername());

            mViewHolder.mRatingBarSatisfy.setRating(Float.parseFloat(mLoginParser.getSuggestedprovider().get(position).getTotalsatisfy()));
            mViewHolder.mRatingBarCommited.setRating(Float.parseFloat(mLoginParser.getSuggestedprovider().get(position).getTotalcommited()));
            ImageLoader.getInstance().displayImage(mLoginParser.getSuggestedprovider().get(position).getUser_image(), mViewHolder.mImageViewProviderPic, mDisplayImageOptions);


            /*if (mLoginParser.getSuggestedprovider().get(position).getCategories() != null) {

                mViewHolder.mLinearLayoutCategory.setVisibility(View.VISIBLE);

                if (mLoginParser.getSuggestedprovider().get(position).getCategories().size() == 1) {
                    mViewHolder.mTextViewService1.setText(mLoginParser.getSuggestedprovider().get(position).getCategories().get(0).getCategory_name());
                    mViewHolder.mTextViewService2.setVisibility(View.GONE);
                    mViewHolder.mTextViewMore.setVisibility(View.GONE);

                } else if (mLoginParser.getSuggestedprovider().get(position).getCategories().size() == 2) {

                    mViewHolder.mTextViewService1.setText(mLoginParser.getSuggestedprovider().get(position).getCategories().get(0).getCategory_name());
                    mViewHolder.mTextViewService2.setText(mLoginParser.getSuggestedprovider().get(position).getCategories().get(1).getCategory_name());
                    mViewHolder.mTextViewService2.setVisibility(View.VISIBLE);
                    mViewHolder.mTextViewMore.setVisibility(View.GONE);

                } else if (mLoginParser.getSuggestedprovider().get(position).getCategories().size() > 2) {

                    mViewHolder.mTextViewService1.setText(mLoginParser.getSuggestedprovider().get(position).getCategories().get(0).getCategory_name());
                    mViewHolder.mTextViewService2.setText(mLoginParser.getSuggestedprovider().get(position).getCategories().get(1).getCategory_name());
                    mViewHolder.mTextViewService2.setVisibility(View.VISIBLE);
                    mViewHolder.mTextViewMore.setVisibility(View.VISIBLE);
                } else {
                    mViewHolder.mTextViewService2.setVisibility(View.GONE);
                    mViewHolder.mTextViewMore.setVisibility(View.GONE);
                }
            } else {
                mViewHolder.mLinearLayoutCategory.setVisibility(View.INVISIBLE);
            }*/
            mViewHolder.mLinearLayoutCategory.setVisibility(View.GONE);

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

    }

    /**
     * AsyncTask for calling webservice in background.
     *
     * @author ebaraiya
     */
    public class BackProcessBookAppointment extends AsyncTask<String, Void, String> {
        String mCurrentMethod = "";

        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(mActivity, "", getString(R.string.dialog_loading), true);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            mCurrentMethod = params[0];
            if (mCurrentMethod.equalsIgnoreCase(mMethodCheckAvailability)) {
                mStringCheckType = "check";

                mLoginParser = (LoginParser) mActivity.getWebMethod().callCheckAvailabilityUser(
                        mActivity.getMyApplication().getUserFiled(getString(R.string.sp_user_id)),
                        mActivity.getMyApplication().getUserFiled(getString(R.string.sp_user_hash)),
                        mStringProviderID, mStringDateTime, mStringAppointmentServices,
                        mStringLocation, mStringLat, mStringLong, mStringAppointmentLocationPolicy, mStringCheckType,
                        mLoginParser);


            } else if (mCurrentMethod.equalsIgnoreCase(mMethodSetAppointment)) {
                mStringCheckType = "addappointment";

                mLoginParserAppointment = (LoginParser) mActivity.getWebMethod().callCheckAvailabilityUser(
                        mActivity.getMyApplication().getUserFiled(getString(R.string.sp_user_id)),
                        mActivity.getMyApplication().getUserFiled(getString(R.string.sp_user_hash)),
                        mStringProviderID, mStringDateTime, mStringAppointmentServices,
                        mStringLocation, mStringLat, mStringLong, mStringAppointmentLocationPolicy, mStringCheckType,
                        mLoginParserAppointment);

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
                    if (mCurrentMethod.equalsIgnoreCase(mMethodCheckAvailability)) {
                        mStringCheckMessage = mLoginParser.getMessage().toString();
                        if (mLoginParser.getWs_status().equalsIgnoreCase("true")) {
                            isProviderAvailable = true;
                            showCheckAvailibilityDialog();
                        } else {
                            if (mLoginParser.getMessage().toString().contains(getString(R.string.alt_msg_exipred))) {
                                mActivity.getAppAlertDialog().showAlertWithSingleButton("", mLoginParser.getMessage().toString(),
                                        mActivity.getString(R.string.lbl_logout),
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                mActivity.logout();
                                            }
                                        });
                            } else {
                                isProviderAvailable = false;
                                showCheckAvailibilityDialog();
                            }
                        }

                    } else if (mCurrentMethod.equalsIgnoreCase(mMethodSetAppointment)) {
                        if (mLoginParserAppointment.getWs_status().equalsIgnoreCase("true")) {

                            DepositFragment mDepositFragment = new DepositFragment();
                            Bundle mBundle = new Bundle();
                            mBundle.putString(getString(R.string.bundle_provider_name), mStringProviderName);
                            mBundle.putString(getString(R.string.bundle_provider_image), mStringProviderPic);
                            mBundle.putString(getString(R.string.bundle_provider_id), mStringProviderID);
                            mBundle.putString(getString(R.string.bundle_location), mStringLocation);
                            mBundle.putString(getString(R.string.bundle_location_policy), mStringAppointmentLocationPolicy);
                            mBundle.putString(getString(R.string.bundle_time), mStringDateTime);
                            mBundle.putString(getString(R.string.bundle_appointment_id), mLoginParserAppointment.getAppointment_id());
                            mBundle.putString(getString(R.string.bundle_min_appt_price), mStringMinApptFees);
                            mBundle.putString(getString(R.string.bundle_trans_fees), mStringTransFees);

                            mDepositFragment.setArguments(mBundle);
                            mActivity.replaceFragment(mDepositFragment, true);

                        } else {
                            if (mLoginParserAppointment.getMessage().toString().contains(getString(R.string.alt_msg_exipred))) {
                                mActivity.getAppAlertDialog().showAlertWithSingleButton("", mLoginParserAppointment.getMessage().toString(),
                                        mActivity.getString(R.string.lbl_logout),
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                mActivity.logout();
                                            }
                                        });
                            } else {
                                mActivity.getAppAlertDialog().showDialog("", mLoginParserAppointment.getMessage().toString(), false);
                            }
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
     * Methdo call will user get picture from gallery or camera.
     */
    @SuppressWarnings("static-access")
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == StaticData.REQUEST_MAP_CODE) {
            Log.d("ADD ", "COME IN INSTAGRAM_PICTURE");
            if (resultCode == getActivity().RESULT_OK) {

                mStringLocation = data.getExtras().getString(getString(R.string.bundle_address));

                mStringLat = data.getExtras().getString(getString(R.string.bundle_lat));
                mStringLong = data.getExtras().getString(getString(R.string.bundle_lng));

                mTextViewAddress.setText(mStringLocation);

            } else if (resultCode == getActivity().RESULT_CANCELED) {

            }

        }
    }
}
