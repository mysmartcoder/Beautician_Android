package beautician.beauty.android.fragments;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.List;

import beautician.beauty.android.R;
import beautician.beauty.android.activities.MyFragmentActivity;
import beautician.beauty.android.parsers.ProviderParser;
import beautician.beauty.android.parsers.UserDataParser;
import beautician.beauty.android.views.ProgressDialog;

@SuppressLint("InflateParams")
public class ProviderByCategoryFragment extends Fragment implements OnClickListener {

    private MyFragmentActivity mActivity;
    private View rootView;
    private ListView mListView;

    private GetProviderListAdapter mGetProviderListAdapter;
    private ImageLoader mImageLoader;
    private DisplayImageOptions mDisplayImageOptions;


    private BackProcessGetProviderList mBackProcessGetProviderList;
//    private ProgressDialog mProgressDialog;
    private ProgressDialog mProgressDialog;
    private ProviderParser mGetProviderListParser;
    private String mMethodGetProviderList = "GetProviderList";
    private String mStringCategoryID = "";
    private String mStringCategoryName = "";


    private ViewPagerImageAdapter mAdapter;
    private ViewPager mPager;
    private static long ANIM_VIEWPAGER_DELAY = 10000;
    private Runnable animateViewPager;
    private Handler mHandler = new Handler();

    private List<UserDataParser> mListSearchResultFeature;


    public ProviderByCategoryFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_provider_by_category, container, false);

        mActivity = (MyFragmentActivity) getActivity();
        mActivity.setHeaderTitle(R.string.lbl_provider);
        mActivity.setSearchIcon(R.drawable.icon_search);
        mActivity.onBackButton(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.replaceFragment(new SearchFragment(), true);
            }
        });

        mGetProviderListParser = new ProviderParser();
        mStringCategoryID = getArguments().getString(getString(R.string.bundle_category_id));
        mStringCategoryName = getArguments().getString(getString(R.string.bundle_category_name));
        mActivity.setHeaderTitle(mStringCategoryName);

        getWidgetRefrence(rootView);
        registerOnClick();

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


//        mAdapter = new ViewPagerImageAdapter(getChildFragmentManager());
//        mPager.setAdapter(mAdapter);

        try {
            mBackProcessGetProviderList = new BackProcessGetProviderList();
            mBackProcessGetProviderList.execute(mMethodGetProviderList);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return rootView;
    }


    /**
     * Method call when sliding images.
     */
    public void runnable() {
        animateViewPager = new Runnable() {
            public void run() {

                if (mPager.getCurrentItem() == mPager.getChildCount() - 1) {
                    mPager.setCurrentItem(0);
                } else {
                    mPager.setCurrentItem(mPager.getCurrentItem() + 1, true);
                }
                mHandler.postDelayed(animateViewPager, ANIM_VIEWPAGER_DELAY);

            }
        };
        mHandler.postDelayed(animateViewPager, ANIM_VIEWPAGER_DELAY);
    }

    @Override
    public void onDestroy() {
        if (animateViewPager != null)
            mHandler.removeCallbacks(animateViewPager);
        super.onDestroy();
    }


    /**
     * Method call will get IDs from xml file.
     *
     * @param v
     */
    private void getWidgetRefrence(View v) {
        mPager = (ViewPager) v.findViewById(R.id.pager);
        mListView = (ListView) v.findViewById(R.id.fragment_provider_by_category_horizontal_listview);


    }

    /**
     * Method call will Register OnClick() Events for widgets.
     */
    private void registerOnClick() {

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ProviderDetailsCategoryPagerFragment mProviderDetailsFragment = new ProviderDetailsCategoryPagerFragment();
                Bundle mBundle = new Bundle();
                mBundle.putString(getString(R.string.bundle_provider_id), mGetProviderListParser.getServicedata().getTopnormal().get(position).getUser_id());
                mBundle.putString(getString(R.string.bundle_category_id), mStringCategoryID);
                mProviderDetailsFragment.setArguments(mBundle);
                mActivity.replaceFragment(mProviderDetailsFragment, true);
            }
        });
    }

    /**
     * Method call OnClick Event fire.
     */
    @Override
    public void onClick(View v) {

    }

    public void loadFeatureResult() {
        mAdapter = new ViewPagerImageAdapter(getChildFragmentManager());
        mPager.setAdapter(mAdapter);
        runnable();
    }


    class ViewPagerImageAdapter extends FragmentPagerAdapter {


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
     * BaseAdapter class for load data into listview
     */
    public class GetProviderListAdapter extends BaseAdapter {
        ViewHolder mViewHolder;

        @Override
        public int getCount() {
            return mGetProviderListParser.getServicedata().getTopnormal().size();
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
                convertview = mActivity.getLayoutInflater().inflate(R.layout.row_provider, null);
                mViewHolder = new ViewHolder();

                mViewHolder.mTextViewName = (TextView) convertview.findViewById(R.id.row_provider_textview_provider_name);
                mViewHolder.mTextViewService1 = (TextView) convertview.findViewById(R.id.row_provider_textview_service1);
                mViewHolder.mTextViewService2 = (TextView) convertview.findViewById(R.id.row_provider_textview_service2);
                mViewHolder.mTextViewMore = (TextView) convertview.findViewById(R.id.row_provider_textview_service_more);
                mViewHolder.mRatingBarSatisy = (RatingBar) convertview.findViewById(R.id.row_provider_rating_satification);
                mViewHolder.mRatingBarCommited = (RatingBar) convertview.findViewById(R.id.row_provider_rating_commitment);
                mViewHolder.mImageViewProviderPic = (ImageView) convertview.findViewById(R.id.row_provider_imageive_user_pic);

                convertview.setTag(mViewHolder);

            } else {
                mViewHolder = (ViewHolder) convertview.getTag();
            }
            mViewHolder.mTextViewName.setText(mGetProviderListParser.getServicedata().getTopnormal().get(position).getUsername());
            mImageLoader.getInstance().displayImage(mGetProviderListParser.getServicedata().getTopnormal().get(position).getUser_image(), mViewHolder.mImageViewProviderPic, mDisplayImageOptions);
            mViewHolder.mRatingBarSatisy.setRating(Float.parseFloat(mGetProviderListParser.getServicedata().getTopnormal().get(position).getTotalsatisfy()));
            mViewHolder.mRatingBarCommited.setRating(Float.parseFloat(mGetProviderListParser.getServicedata().getTopnormal().get(position).getTotalcommited()));


            if(mActivity.getMyApplication().getCurrentLang().equalsIgnoreCase("en")) {
                if (mGetProviderListParser.getServicedata().getTopnormal().get(position).getCategories().size() == 1) {
                    mViewHolder.mTextViewService1.setText(mGetProviderListParser.getServicedata().getTopnormal().get(position).getCategories().get(0).getCategory_name());
                    mViewHolder.mTextViewService2.setVisibility(View.INVISIBLE);
                    mViewHolder.mTextViewMore.setVisibility(View.INVISIBLE);
                } else if (mGetProviderListParser.getServicedata().getTopnormal().get(position).getCategories().size() == 2) {
                    if (mStringCategoryID.equalsIgnoreCase(mGetProviderListParser.getServicedata().getTopnormal().get(position).getCategories().get(0).getCategory_id())) {
                        mViewHolder.mTextViewService1.setText(mGetProviderListParser.getServicedata().getTopnormal().get(position).getCategories().get(0).getCategory_name());
                        mViewHolder.mTextViewService2.setText(mGetProviderListParser.getServicedata().getTopnormal().get(position).getCategories().get(1).getCategory_name());
                    } else {
                        mViewHolder.mTextViewService1.setText(mGetProviderListParser.getServicedata().getTopnormal().get(position).getCategories().get(1).getCategory_name());
                        mViewHolder.mTextViewService2.setText(mGetProviderListParser.getServicedata().getTopnormal().get(position).getCategories().get(0).getCategory_name());
                    }
                    mViewHolder.mTextViewService2.setVisibility(View.VISIBLE);
                    mViewHolder.mTextViewMore.setVisibility(View.INVISIBLE);
                } else if (mGetProviderListParser.getServicedata().getTopnormal().get(position).getCategories().size() > 2) {

                    for (int i = 0; i < mGetProviderListParser.getServicedata().getTopnormal().get(position).getCategories().size(); i++) {
                        if (mStringCategoryID.equalsIgnoreCase(mGetProviderListParser.getServicedata().getTopnormal().get(position).getCategories().get(i).getCategory_id()))
                            mViewHolder.mTextViewService1.setText(mGetProviderListParser.getServicedata().getTopnormal().get(position).getCategories().get(i).getCategory_name());
                        else
                            mViewHolder.mTextViewService2.setText(mGetProviderListParser.getServicedata().getTopnormal().get(position).getCategories().get(i).getCategory_name());
                    }
                    mViewHolder.mTextViewService2.setVisibility(View.VISIBLE);
                    mViewHolder.mTextViewMore.setVisibility(View.VISIBLE);
                } else {
                    mViewHolder.mTextViewService2.setVisibility(View.INVISIBLE);
                    mViewHolder.mTextViewMore.setVisibility(View.INVISIBLE);
                }
            }else
            {
                if (mGetProviderListParser.getServicedata().getTopnormal().get(position).getCategories().size() == 1) {
                    mViewHolder.mTextViewService1.setText(mGetProviderListParser.getServicedata().getTopnormal().get(position).getCategories().get(0).getCategory_namearebic());
                    mViewHolder.mTextViewService2.setVisibility(View.INVISIBLE);
                    mViewHolder.mTextViewMore.setVisibility(View.INVISIBLE);
                } else if (mGetProviderListParser.getServicedata().getTopnormal().get(position).getCategories().size() == 2) {
                    if (mStringCategoryID.equalsIgnoreCase(mGetProviderListParser.getServicedata().getTopnormal().get(position).getCategories().get(0).getCategory_id())) {
                        mViewHolder.mTextViewService1.setText(mGetProviderListParser.getServicedata().getTopnormal().get(position).getCategories().get(0).getCategory_namearebic());
                        mViewHolder.mTextViewService2.setText(mGetProviderListParser.getServicedata().getTopnormal().get(position).getCategories().get(1).getCategory_namearebic());
                    } else {
                        mViewHolder.mTextViewService1.setText(mGetProviderListParser.getServicedata().getTopnormal().get(position).getCategories().get(1).getCategory_namearebic());
                        mViewHolder.mTextViewService2.setText(mGetProviderListParser.getServicedata().getTopnormal().get(position).getCategories().get(0).getCategory_namearebic());
                    }
                    mViewHolder.mTextViewService2.setVisibility(View.VISIBLE);
                    mViewHolder.mTextViewMore.setVisibility(View.INVISIBLE);
                } else if (mGetProviderListParser.getServicedata().getTopnormal().get(position).getCategories().size() > 2) {

                    for (int i = 0; i < mGetProviderListParser.getServicedata().getTopnormal().get(position).getCategories().size(); i++) {
                        if (mStringCategoryID.equalsIgnoreCase(mGetProviderListParser.getServicedata().getTopnormal().get(position).getCategories().get(i).getCategory_id()))
                            mViewHolder.mTextViewService1.setText(mGetProviderListParser.getServicedata().getTopnormal().get(position).getCategories().get(i).getCategory_namearebic());
                        else
                            mViewHolder.mTextViewService2.setText(mGetProviderListParser.getServicedata().getTopnormal().get(position).getCategories().get(i).getCategory_namearebic());
                    }
                    mViewHolder.mTextViewService2.setVisibility(View.VISIBLE);
                    mViewHolder.mTextViewMore.setVisibility(View.VISIBLE);
                } else {
                    mViewHolder.mTextViewService2.setVisibility(View.INVISIBLE);
                    mViewHolder.mTextViewMore.setVisibility(View.INVISIBLE);
                }
            }
            return convertview;
        }

    }

    public class ViewHolder {
        TextView mTextViewName;
        TextView mTextViewService1;
        TextView mTextViewService2;
        TextView mTextViewMore;
        RatingBar mRatingBarSatisy;
        RatingBar mRatingBarCommited;
        ImageView mImageViewProviderPic;

    }

    /**
     * AsyncTask for calling webservice in background.
     *
     * @author ebaraiya
     */
    public class BackProcessGetProviderList extends AsyncTask<String, Void, String> {
        String mCurrentMethod = "";

        @Override
        protected void onPreExecute() {

            mProgressDialog = ProgressDialog.show(mActivity, "", getString(R.string.dialog_loading), true);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                mCurrentMethod = params[0];
                if (mCurrentMethod.equalsIgnoreCase(mMethodGetProviderList)) {
                    mGetProviderListParser = (ProviderParser) mActivity.getWebMethod().callGetProviderByCategory(mActivity.getMyApplication().getUserFiled(getString(R.string.sp_user_id)), mActivity.getMyApplication().getUserFiled(getString(R.string.sp_user_hash)), mStringCategoryID, mActivity.getMyApplication().getCurrentLatitude(), mActivity.getMyApplication().getCurrentLongitude(), mGetProviderListParser);
                }
            } catch (Exception e) {
                e.printStackTrace();
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
                    if (mCurrentMethod.equalsIgnoreCase(mMethodGetProviderList)) {
                        if (mGetProviderListParser.getWs_status().equalsIgnoreCase("true")) {
                            if (mGetProviderListParser.getServicedata() != null) {

                                if (mGetProviderListParser.getServicedata().getTopfeatured() != null && mGetProviderListParser.getServicedata().getTopfeatured().size() > 0) {
                                    mListSearchResultFeature = mGetProviderListParser.getServicedata().getTopfeatured();
                                    loadFeatureResult();
                                    mPager.setVisibility(View.VISIBLE);
                                }else
                                {
                                    mPager.setVisibility(View.GONE);
                                }
                                if (mGetProviderListParser.getServicedata().getTopnormal() != null && mGetProviderListParser.getServicedata().getTopnormal().size() > 0) {
                                    mGetProviderListAdapter = new GetProviderListAdapter();
                                    mListView.setAdapter(mGetProviderListAdapter);
                                }
                            }
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
                                mActivity.getAppAlertDialog().showDialog(getString(R.string.app_name), mGetProviderListParser.getMessage(), true);
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
