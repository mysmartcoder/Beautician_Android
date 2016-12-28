package beautician.beauty.android.fragments;

import android.annotation.SuppressLint;

import beautician.beauty.android.parsers.BookAppointmentParser;
import beautician.beauty.android.utilities.InterfaceDialogClickListener;
import beautician.beauty.android.views.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.Iterator;

import beautician.beauty.android.R;
import beautician.beauty.android.activities.MyFragmentActivity;
import beautician.beauty.android.parsers.ProviderParser;
import beautician.beauty.android.views.CircleImageView;
import beautician.beauty.android.views.PagerSlidingTabStrip;
import beautician.beauty.android.views.SharingDialogFragment;

@SuppressLint("InflateParams")
public class ProviderDetailsCategoryPagerFragment extends Fragment implements OnClickListener {

    private MyFragmentActivity mActivity;
    private View rootView;
    private ImageLoader mImageLoader;
    private DisplayImageOptions mDisplayImageOptions;

    private BackProcessGetProviderDetails mBackProcessGetProviderDetails;
    private ProgressDialog mProgressDialog;
    private ProviderParser mGetProviderListParser;
    private String mMethodGetProviderDetails = "GetProviderDetaisl";
    private String mStringProviderID = "";
    private String mStringCategoryID = "";
    private String mStringServiceID = "";

    private CircleImageView mImageViewUserDp;
    private ImageView mImageViewPickLocation;
    private ImageView mImageViewShare;
    private TextView mTextViewName;
    private TextView mTextViewLocation;
    private TextView mTextViewLocationPolicy;
    private TextView mTextViewConfirmAppointment;
    private TextView mTextViewMinAppointmentFees;
    private TextView mTextViewBookAppointment;
    private RatingBar mRatingBarSatisfy;
    private RatingBar mRatingBarCommitted;

    private ViewPager mViewPager;
    PagerSlidingTabStrip tabsStrip;
    private View mViewDivider;
    private MyPagerAdapter adapter;
    private int mIntPagePosition = 0;

    public ProviderDetailsCategoryPagerFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_provider_details, container, false);

        mActivity = (MyFragmentActivity) getActivity();
        mActivity.setHeaderTitle(R.string.lbl_provider_detail);
        mActivity.setSearchIcon(R.drawable.icon_search);
        mActivity.onBackButton(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.replaceFragment(new SearchFragment(), true);
            }
        });
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


        if (mStringProviderID.length() == 0) {
            mStringProviderID = getArguments().getString(getString(R.string.bundle_provider_id));
            mStringCategoryID = getArguments().getString(getString(R.string.bundle_category_id));
            if (mStringCategoryID == null)
                mStringCategoryID = "0";

            mStringServiceID = getArguments().getString(getString(R.string.bundle_service_id));
            if (mStringServiceID == null)
                mStringServiceID = "0";
            mGetProviderListParser = new ProviderParser();
            mBackProcessGetProviderDetails = new BackProcessGetProviderDetails();
            mBackProcessGetProviderDetails.execute(mMethodGetProviderDetails);
            mActivity.getMyApplication().clearnBookAppointment("");
        } else {
            setData();
        }

        return rootView;
    }


    /**
     * Method call will get IDs from xml file.
     *
     * @param v
     */
    private void getWidgetRefrence(View v) {

        mImageViewUserDp = (CircleImageView) v.findViewById(R.id.fragment_provider_details_imageview_user_dp);
        mImageViewPickLocation = (ImageView) v.findViewById(R.id.fragment_provider_details_imageview_location);
        mImageViewShare = (ImageView) v.findViewById(R.id.fragment_provider_details_imageview_share);
        mTextViewName = (TextView) v.findViewById(R.id.fragment_provider_details_textview_name);
        mTextViewLocation = (TextView) v.findViewById(R.id.fragment_provider_details_textview_location);
        mTextViewLocationPolicy = (TextView) v.findViewById(R.id.fragment_provider_details_textview_location_policy);
        mTextViewMinAppointmentFees = (TextView) v.findViewById(R.id.fragment_provider_details_textview_min_apt_fees);

        mTextViewConfirmAppointment = (TextView) v.findViewById(R.id.fragment_provider_details_textview_confirmed);
        mRatingBarSatisfy = (RatingBar) v.findViewById(R.id.fragment_provider_details_rating_satification);
        mRatingBarCommitted = (RatingBar) v.findViewById(R.id.fragment_provider_details_rating_commitment);
        mTextViewBookAppointment = (TextView) v.findViewById(R.id.fragment_provider_details_textview_book_appointment);

        mViewPager = (ViewPager) v.findViewById(R.id.pager);
        tabsStrip = (PagerSlidingTabStrip) v.findViewById(R.id.tabs);
//        tabsStrip.setIndicatorColor(mActivity.getMyApplication().setThemeColor());
        tabsStrip.setTextColor(ContextCompat.getColor(mActivity, R.color.white));
//        tabsStrip.setDividerColor(mActivity.getMyApplication().setThemeColor());

    }

    /**
     * Method call will Register OnClick() Events for widgets.
     */
    private void registerOnClick() {
        mImageViewPickLocation.setOnClickListener(this);
        mTextViewBookAppointment.setOnClickListener(this);
        mImageViewShare.setOnClickListener(this);
    }

    /**
     * Method call OnClick Event fire.
     */
    @Override
    public void onClick(View v) {
        if (v == mImageViewPickLocation) {
            CommonMapViewFragment mCommonMapViewFragment = new CommonMapViewFragment();
            Bundle mBundle = new Bundle();
            mBundle.putString(getString(R.string.bundle_lat), mGetProviderListParser.getData().getUser_lat());
            mBundle.putString(getString(R.string.bundle_lng), mGetProviderListParser.getData().getUser_lng());
            mBundle.putString(getString(R.string.bundle_address), mGetProviderListParser.getData().getUsername());
            mCommonMapViewFragment.setArguments(mBundle);
            mActivity.replaceFragment(mCommonMapViewFragment, true);


        }
        else if (v == mImageViewShare) {

            SharingDialogFragment mSharingDialogFragment = new SharingDialogFragment();
            Bundle mBundle = new Bundle();
            mBundle.putString(getString(R.string.bundle_service_id), "");
            mBundle.putString(getString(R.string.bundle_provider_id),mGetProviderListParser.getData().getUser_id());
            mBundle.putString(getString(R.string.bundle_provider_name),mGetProviderListParser.getData().getUsername());
            mBundle.putString(getString(R.string.bundle_location), mTextViewLocation.getText().toString());
            mBundle.putString(getString(R.string.bundle_provider_image),mGetProviderListParser.getData().getUser_image());
            mSharingDialogFragment.setArguments(mBundle);
            mSharingDialogFragment.show(getChildFragmentManager(), "");

        }

        else if (v == mTextViewBookAppointment) {
//            Iterator myVeryOwnIterator = mActivity.getMyApplication().getBookedAppoinmentData().keySet().iterator();
//            while(myVeryOwnIterator.hasNext()) {
//                String key=(String)myVeryOwnIterator.next();
//                BookAppointmentParser value = (BookAppointmentParser)mActivity.getMyApplication().getBookedAppoinmentData().get(key);
//                Toast.makeText(mActivity, value.getService_name()+ " : ("+ value.getCategory_name()+ ") Price : "+value.getTotal_price(), Toast.LENGTH_LONG).show();
//            }

            if (mActivity.getMyApplication().getBookedAppoinmentData().keySet().size() > 0) {

                if(isMinimumPriceReach()) {
                    processBookAnAppointment();
                }else
                {
                    mActivity.getAppAlertDialog().showDeleteAlert("", getString(R.string.validation_min_apt_fees_not_reach),
                            getString(R.string.lbl_no), getString(R.string.lbl_yes), new InterfaceDialogClickListener() {
                                @Override
                                public void onClick() {
                                    processBookAnAppointment();
                                }
                            });
                }
            } else {
                mActivity.getAppAlertDialog().showDialog(getString(R.string.app_name), getString(R.string.validation_book_appointment), false);
            }
        }

    }

    public void processBookAnAppointment()
    {
        BookAppointmentFragment mBookAppointmentFragment = new BookAppointmentFragment();
        Bundle mBundle = new Bundle();
        mBundle.putString(getString(R.string.bundle_provider_name), mGetProviderListParser.getData().getUsername());
        mBundle.putString(getString(R.string.bundle_provider_image), mGetProviderListParser.getData().getUser_image());
        mBundle.putString(getString(R.string.bundle_provider_id), mGetProviderListParser.getData().getUser_id());
        mBundle.putString(getString(R.string.bundle_location), mGetProviderListParser.getData().getUser_location());
        mBundle.putString(getString(R.string.bundle_location), mGetProviderListParser.getData().getUser_location());
        mBundle.putString(getString(R.string.bundle_lat), mGetProviderListParser.getData().getUser_lat());
        mBundle.putString(getString(R.string.bundle_lng), mGetProviderListParser.getData().getUser_lng());
        mBundle.putString(getString(R.string.bundle_min_appt_price), mGetProviderListParser.getData().getUser_minapptfee());
        mBundle.putString(getString(R.string.bundle_trans_fees), mGetProviderListParser.getData().getUser_transportfee());
        mBundle.putString(getString(R.string.bundle_location_policy), mGetProviderListParser.getData().getLocation_policy());

        mBookAppointmentFragment.setArguments(mBundle);
        mActivity.replaceFragment(mBookAppointmentFragment, true);
    }

    /**
     * Method call will set data..
     */
    private void setData() {
        mImageLoader.getInstance().displayImage(mGetProviderListParser.getData().getUser_image(), mImageViewUserDp, mDisplayImageOptions);
        mTextViewName.setText(mGetProviderListParser.getData().getUsername());
        if(mActivity.getMyApplication().getCurrentLang().equalsIgnoreCase("en")) {
            mTextViewLocation.setText(mGetProviderListParser.getData().getCity_name() + ", " + mGetProviderListParser.getData().getCountry_name());
        }else {
            mTextViewLocation.setText(mGetProviderListParser.getData().getCountry_namearebic() + " ," + mGetProviderListParser.getData().getCity_namearabic());
        }

        if(mGetProviderListParser.getData().getLocation_policy().equalsIgnoreCase("seeker")) {
            mTextViewLocationPolicy.setText(getString(R.string.lbl_location_policy_details, getString(R.string.lbl_seeker_place)));
        }else if(mGetProviderListParser.getData().getLocation_policy().equalsIgnoreCase("provider")) {
            mTextViewLocationPolicy.setText(getString(R.string.lbl_location_policy_details, getString(R.string.lbl_provider_place)));
        }else
        {
            mTextViewLocationPolicy.setText(getString(R.string.lbl_location_policy_details, getString(R.string.lbl_both)));
        }

        mTextViewMinAppointmentFees.setText(getString(R.string.lbl_min_app_fees_with_value, mGetProviderListParser.getData().getUser_minapptfee()));

        //mTextViewConfirmAppointment.setText(mGetProviderListParser.getData().getUser_phone());
        mRatingBarSatisfy.setRating(Float.parseFloat(mGetProviderListParser.getData().getTotalsatisfy()));
        mRatingBarCommitted.setRating(Float.parseFloat(mGetProviderListParser.getData().getTotalcommited()));

        if(mGetProviderListParser.getData().getConfirmedappointment().length() >0) {
            mTextViewConfirmAppointment.setText(getString(R.string.lbl_confirmed_appointment, mGetProviderListParser.getData().getConfirmedappointment()));
            mTextViewConfirmAppointment.setVisibility(View.VISIBLE);
        }else
        {
            mTextViewConfirmAppointment.setVisibility(View.GONE);
        }

        if (mGetProviderListParser.getData().getCategories() != null) {
            adapter = new MyPagerAdapter(getChildFragmentManager());
            mViewPager.setAdapter(adapter);
            final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
            mViewPager.setPageMargin(pageMargin);
            tabsStrip.setViewPager(mViewPager);
            mViewPager.setCurrentItem(mIntPagePosition);
        }
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {
        int pos = -1;


        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            pos = position;
//            return mGetProviderListParser.getData().getCategories().get(position).getCategory_name();
            String size = String.valueOf(mGetProviderListParser.getData().getCategories().get(position).getServices().size());
            String title = "";
            if(mActivity.getMyApplication().getCurrentLang().equalsIgnoreCase("en")) {
                if (Integer.parseInt(size) > 1)
                    title = mGetProviderListParser.getData().getCategories().get(position).getCategory_name() + " (" + size + ")";
                else
                    title = mGetProviderListParser.getData().getCategories().get(position).getCategory_name();
            }else
            {
                if (Integer.parseInt(size) > 1)
                    title = mGetProviderListParser.getData().getCategories().get(position).getCategory_namearebic() + " (" + size + ")";
                else
                    title = mGetProviderListParser.getData().getCategories().get(position).getCategory_namearebic();
            }

            return title;
        }

        @Override
        public int getCount() {
            return mGetProviderListParser.getData().getCategories().size();
        }


        @Override
        public Fragment getItem(int arg0) {

//            if(arg0 == 0)
//            {
//                ProviderDetailsViewPagerFragment mProviderDetailsViewPagerFragment = new ProviderDetailsViewPagerFragment();
//                Bundle mBundle = new Bundle();
//                mBundle.putParcelable(getString(R.string.bundle_category_data), mGetProviderListParser.getData().getCategories().get(arg0));
//                mProviderDetailsViewPagerFragment.setArguments(mBundle);
//
//                return mProviderDetailsViewPagerFragment;
//            }else {
//                ProviderDetailsDataFragment mProviderDetailsDataFragment = new ProviderDetailsDataFragment();
//                Bundle mBundle = new Bundle();
//                mBundle.putParcelable(getString(R.string.bundle_service_data), mGetProviderListParser.getData().getCategories().get(arg0));
//                mProviderDetailsDataFragment.setArguments(mBundle);
//                return mProviderDetailsDataFragment;
//
//            }

            ProviderDetailsServicePagerFragment mProviderDetailsViewPagerFragment = new ProviderDetailsServicePagerFragment();
            Bundle mBundle = new Bundle();
            mBundle.putParcelable(getString(R.string.bundle_category_data), mGetProviderListParser.getData().getCategories().get(arg0));
            mBundle.putString(getString(R.string.bundle_provider_name), mGetProviderListParser.getData().getUsername());
            mBundle.putString(getString(R.string.bundle_provider_image), mGetProviderListParser.getData().getUser_image());
            mBundle.putString(getString(R.string.bundle_provider_id), mGetProviderListParser.getData().getUser_id());
            mBundle.putString(getString(R.string.bundle_service_id), mStringServiceID);

            mProviderDetailsViewPagerFragment.setArguments(mBundle);

            return mProviderDetailsViewPagerFragment;

        }

    }

    public boolean isMinimumPriceReach()
    {
        Iterator myVeryOwnIterator = mActivity.getMyApplication().getBookedAppoinmentData().keySet().iterator();
        StringBuilder mStringBuilder = new StringBuilder();
        float mIntTotalPrice = 0;

        while (myVeryOwnIterator.hasNext()) {
            String key = (String) myVeryOwnIterator.next();
            BookAppointmentParser value = (BookAppointmentParser) mActivity.getMyApplication().getBookedAppoinmentData().get(key);
            mIntTotalPrice = Float.parseFloat(value.getTotal_price()) + mIntTotalPrice;
        }

        if(mIntTotalPrice >=  Float.parseFloat(mGetProviderListParser.getData().getUser_minapptfee()))
            return true;
        else
            return false;
    }

    /**
     * AsyncTask for calling webservice in background.
     * @author ebaraiya
     */
    public class BackProcessGetProviderDetails extends AsyncTask<String, Void, String> {
        String mCurrentMethod = "";

        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(mActivity, "", getString(R.string.dialog_loading), true);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            mCurrentMethod = params[0];
            if (mCurrentMethod.equalsIgnoreCase(mMethodGetProviderDetails)) {
                mGetProviderListParser = (ProviderParser) mActivity.getWebMethod().callGetProviderDetails(mActivity.getMyApplication().getUserFiled(getString(R.string.sp_user_id)), mActivity.getMyApplication().getUserFiled(getString(R.string.sp_user_hash)), mStringProviderID, mGetProviderListParser);
                if (mGetProviderListParser.getData()!=null && mGetProviderListParser.getData().getCategories() != null) {
                    for (int i = 0; i < mGetProviderListParser.getData().getCategories().size(); i++) {
                        if (mGetProviderListParser.getData().getCategories().get(i).getCategory_id().equalsIgnoreCase(mStringCategoryID)) {
                            mIntPagePosition = i;
                        }
                    }
                }
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
                    if (mCurrentMethod.equalsIgnoreCase(mMethodGetProviderDetails)) {
                        if (mGetProviderListParser.getWs_status().equalsIgnoreCase("true")) {
                            setData();
                        } else {
                            if (mGetProviderListParser.getMessage().toString().contains(getString(R.string.alt_msg_exipred))) {
                                mActivity.getAppAlertDialog().showAlertWithSingleButton("", mGetProviderListParser.getMessage().toString(),
                                        mActivity.getString(R.string.lbl_logout),
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                mActivity.logout();
                                            }
                                        });
                            } else {
                                mActivity.getAppAlertDialog().showDialog(getString(R.string.app_name), mGetProviderListParser.getMessage(), false);
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
}
